package edu.berkeley.ims.myt

import com.unboundid.ldap.sdk.SearchResultEntry
import edu.berkeley.calnet.mmk.GoogleUser
import edu.berkeley.calnet.mmk.TokenUtil
import grails.converters.JSON

class BappsController {
    static allowedMethods = [save: 'POST']

    /* GrailsApplication -- needed for the config. */
    def grailsApplication

    /* EmailService service */
    def emailService

    /** GoogleAppsService service */
    def googleAppsService

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
        GoogleUser currentAccount = googleAppsService.getGoogleAppsAccount(session.person)
        ['account': currentAccount]
    }

    /**
     * Displays the 'set' (form) page for the token.
     */
    def set() {
        // You can not get this from the config because the GoogleAppsCommand
        // also needs to know about the token length, and you can't inject that
        // into the CommandObject.
        GoogleUser currentAccount = googleAppsService.getGoogleAppsAccount(session.person)
        [account: currentAccount, userDefined: false]
    }

    def generateToken() {
        int tokenLength = grailsApplication.config.myt.bAppsTokenLength
        render([token: TokenUtil.generateToken(tokenLength)] as JSON)
    }

    /**
     * Saves the token. This makes use of the GoogleAppsCommand object which is
     * and inner class defined at the bottom of this class.
     */
    def save(GoogleAppsCommand cmd) {

        GoogleUser currentAccount = googleAppsService.getGoogleAppsAccount(person)
        if (cmd.hasErrors()) {
            def errorsForTitle = cmd.errors.allErrors.collect { "${g.message(error: it)}" }.join(' ')
            flash.title = "${message(code: 'bapps.alert.save.errorForTitle')}$errorsForTitle"
            render(view: 'set', model: ['token': params.token, 'account': currentAccount, 'googleApps': cmd, 'userDefined': cmd.userDefined])
        } else {
            def result = googleAppsService.updateTokenForAccount(person, cmd.token, cmd.userDefined)
            if (result) {
                emailService.sendBappsSetConfirmation(currentAccount)
                if (cmd.userDefined) {
                    flash.success = message(code: 'bapps.alert.save.success', args: [currentAccount.emailAddress])
                    flash.title = message(code: 'bapps.alert.save.successForTitleDefined', args: [currentAccount.emailAddress])
                    // Redirect to either index or choose...
                    redirect(action: 'index')
                } else {
                    flash.token = params.definedToken ?: params.token
                    flash.title = message(code: 'bapps.alert.save.successForTitleGenerated', args: [currentAccount.emailAddress])
                    redirect(action: 'view', id: params.id)
                }
            } else {
                flash.error = message(code: 'bapps.alert.save.failed', args: [currentAccount.emailAddress])
                flash.title = message(code: 'bapps.alert.save.failedTitle', args: [currentAccount.emailAddress])
                render(view: 'set', model: ['token': params.token, 'account': currentAccount, 'googleApps': cmd, 'userDefined': cmd.userDefined])
            }
        }
        // The final else would have already been handled by accountFromId()
    }

    /**
     * Displays the token if the user chooses the pre-generated token.
     */
    def view() {
        GoogleUser currentAccount = googleAppsService.getGoogleAppsAccount(person)
        if (!flash.token && currentAccount) {
            redirect(action: 'index')
        } else {
            ['account': currentAccount]
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
        GoogleUser currentAccount = googleAppsService.getGoogleAppsAccount(person)
        if(request.method == 'GET') {
            return [account: currentAccount]
        }

        def result = googleAppsService.deleteTokenForAccount(person)


        if (result) {
            emailService.sendBappsDeleteConfirmation(currentAccount)
            flash.success = message(code: 'bapps.alert.delete.success', args: [currentAccount.emailAddress])
            flash.title = message(code: 'bapps.alert.delete.successForTitle', args: [currentAccount.emailAddress])

            redirect(action: 'index')
        } else {
            flash.error = message(code: 'bapps.alert.delete.failed', args: [currentAccount.emailAddress])
            flash.title = message(code: 'bapps.alert.delete.failedTitle', args: [currentAccount.emailAddress])
            [account: currentAccount]
        }
    }

    /**
     * Command Object for interacting with the GoogleAppsService.
     */
    class GoogleAppsCommand {

        /* CalNetService service */
        def calNetService

        boolean userDefined
        String token
        String tokenRepeat

        static constraints = {
            token(nullable: false, size: 9..255, validator: { val, obj ->
                if (obj.calNetService.testAuthenticationFor(val, obj.session.person)) {
                    return 'cannotmatchcalnet'
                }
                if (!obj.calNetService.validatePassphraseComplexityFor(val, obj.session.person)) {
                    return 'doesnotmeetrequirements'
                }
            })
            tokenRepeat(validator: { val, obj ->
                if (val != obj.token) {
                    return 'donotmatch'
                }
            })
        }
    }

    private SearchResultEntry getPerson() {
        session.person
    }

}
