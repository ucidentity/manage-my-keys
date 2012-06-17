package edu.berkeley.ims.myt

import org.springframework.web.servlet.ModelAndView

class BappsController {

    /* GrailsApplication -- needed for the config. */
    def grailsApplication

    /* Make sure the supplied account ID is valid for this user. */    
    def beforeInterceptor = [action:this.&accountFromId, except:['choose']]

    /* TokenService service */
    def tokenService
    
    /* EmailService service */
    def emailService

    /** GoogleAppsService service */
    def googleAppsService
    
    /* Keeps track of the account that is set using params.id */
    def currentAccount
    
    /* Since we use this a lot, we pull it out when setting currentAccount */
    def currentUsername

    /**
     * Shows the index page with the provided account. If no account
     * is provided and the user only has one account, then the account
     * is set to that one account. If the user has more than one account,
     * then the user is sent to the 'choose' action.
     *
     * If the user only has one account, then accountFromId() should set it,
     * so we can assume that if it is not set here, the user needs to choose.
     */
    def index() {
        return new ModelAndView('/bapps/index',
            ['account':currentAccount])
    }
    
    /**
     * Shows a list of IDs for the person to choose. If an ID has been chosen,
     * i.e., params.id will be set, then it redirects to 'index' with that
     * account set as the id.
     */
    def choose() {
        if (session.googleAppsAccounts?.size() == 1) {
            redirect(action:'index')
        }
        
        if (params.id) {
            redirect(action:'index', id:params.id)
        }
        else {
            return new ModelAndView('/bapps/choose',
                ['accounts':session.googleAppsAccounts])
        }
    }
    
    /**
     * Displays the 'set' (form) page for the token.
     */
    def set() {
        // You can not get this from the config because the GoogleAppsCommand
        // also needs to know about the token length, and you can't inject that
        // into the CommandObject.
        //def token = tokenService.token(grailsApplication.config.myt.bAppsTokenLength.toInteger())
        def token = tokenService.token(12)
        session.currentToken = token
        return new ModelAndView('/bapps/set',
            ['token':token, 'account':currentAccount])
    }
    
    /**
     * Saves the token. This makes use of the GoogleAppsCommand object which is
     * and inner class defined at the bottom of this class.
     */
    def save(GoogleAppsCommand cmd) {
        if (request.method == 'POST') {
            def userDefined = params.definedToken ? true : false
            if (cmd.hasErrors()) {
                
                return new ModelAndView('/bapps/set',
                    ['token':params.token, 'account':currentAccount,
                     'googleApps':cmd, 'userDefined':userDefined])
            }
            else {
                def result = googleAppsService.saveTokenForAccount(
                    currentAccount, params.definedToken ?: params.token, false, session.person)
                if (result.size() == 0) {
                    emailService.sendBappsSetConfirmation(session.person, currentUsername)
                    if (params.definedToken) {
                        flash.success = message(code: 'bapps.alert.save.success', 
                            args:[currentUsername])
                        // Redirect to either index or choose...
                        session.googleAppsAccounts.size() == 1 ? 
                            redirect(action:'index') : redirect(action:'choose')
                    }
                    else {
                        flash.token = params.definedToken ?: params.token
                        redirect(action:'view', id:params.id)
                    }
                }
                else {
                    flash.error = result.error
                    return new ModelAndView('/bapps/set',
                        ['token':params.token, 'account':currentAccount,
                         'googleApps':cmd, 'userDefined':userDefined])
                }
            }
        }
        else {
            redirect(action:'index')
        }
    }
    
    /**
     * Displays the token if the user chooses the pre-generated token.
     */
    def view() {
        if (!flash.token) {
            redirect(action:'index')
        }
        else {
            return new ModelAndView('/bapps/view',
                ['account':currentAccount])
        }
    }
    
    /**
     * Deletes the token, sets a flash.success message, and redirects the user
     * to either the choose page or the index page, where the flash message is
     * shown.
     *
     * Actually, it does not delete the token, but it sets the passphrase to a
     * random, 30 character string.
     */
    def delete() {
        if (request.method == 'POST') {
            def result = googleAppsService.saveTokenForAccount(
                currentAccount, tokenService.token(30), true, session.person)
            
            if (result.size() == 0) {
                emailService.sendBappsDeleteConfirmation(session.person, currentUsername)
                flash.success = message(code:'bapps.alert.delete.success',
                    args:[currentUsername])

                // Redirect to either index or choose...
                session.googleAppsAccounts.size() == 1 ? 
                    redirect(action:'index') : redirect(action:'choose')
            }
            else {
                flash.error = result.error
                return new ModelAndView('/bapps/delete',
                    ['account':currentAccount])
            }
        }
        else {
            return new ModelAndView('/bapps/delete',
                ['account':currentAccount])
        }
    }
    
    /**
     * Checks to see if the supplied ID is valid for the logged in user.
     * If not, then it either sends the user to the index or choose page,
     * depending on whether or not the user has one or more Google accounts.
     *
     * It keeps the user in the bApps section because if the user was not
     * authorized to be in the section at all, the user would have been stopped
     * by the SecurityFilters.
     */
    protected accountFromId() {        
        if (!session.googleAppsAccounts) {
            session.googleAppsAccounts = 
                googleAppsService.googleAppsAccounts(session.person)
        }
        
        // Only do this if the user has any Google apps. If not, then the
        // SecurityFilter will pick it up.
        if (session.googleAppsAccounts) {
            if (params.id) {
                session.googleAppsAccounts.each {
                    if (it.getLogin().getUserName() == params.id) {
                        currentAccount = it
                        currentUsername = it.getLogin().getUserName()
                    }
                }
            }
            else if (session.googleAppsAccounts.size() == 1) {
                currentAccount = session.googleAppsAccounts[0]
                currentUsername = currentAccount.getLogin().getUserName()
            }
        
            if (!currentAccount) {
                session.googleAppsAccounts.size() == 1 ? redirect(action:'index') : redirect(action:'choose')
            }
        }
    }
    
    /**
     * Command Object for interacting with the GoogleAppsService.
     */
    class GoogleAppsCommand {

        /* TokenService service */
        def tokenService
        
        String token
        
        String definedToken
        
        String definedTokenConfirmation
        
        static constraints = {
            token(blank:true,nullable:true,size:12..12, validator: {val, obj ->
                if (val && !obj.tokenService.verifyCurrentToken(obj.session.currentToken, val)) {
                    return 'googleAppsCommand.token.donotmatch'
                }
            })
            definedToken(blank:true,nullable:true,size:9..255)
            definedTokenConfirmation(blank:true,nullable:true,size:9..255, validator: {val, obj ->
                if (val && val != obj.definedToken) {
                    return 'googleAppsCommand.definedTokenConfirmation.donotmatch'
                }
            })
        }
        
    }
    
}
