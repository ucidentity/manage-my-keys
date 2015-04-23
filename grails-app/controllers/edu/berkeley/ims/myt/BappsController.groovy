package edu.berkeley.ims.myt

class BappsController {

    /* Make sure the supplied account ID is valid for this user. */
    def beforeInterceptor = [action: this.&accountFromId, except: ['choose']]

    /* GrailsApplication -- needed for the config. */
    def grailsApplication

    /* TokenService service */
    def tokenService

    /* EmailService service */
    def emailService

    /** GoogleAppsService service */
    def googleAppsService

    def calMailService

    /* Keeps track of the account that is set using params.id */
    def currentAccount

    /* Since we use this a lot, we pull it out when setting currentAccount */
    def currentUsername

    /* The underlying CalMail account for the selected account */
    def calMailAccount

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
        ['account': currentAccount, calMailAccount: calMailAccount]
    }

    /**
     * Shows a list of IDs for the person to choose. If an ID has been chosen,
     * i.e., params.id will be set, then it redirects to 'index' with that
     * account set as the id.
     */
    def choose() {
        if (session.googleAppsAccounts?.size() == 1) {
            redirect(action: 'index')
        }

        if (params.id) {
            redirect(action: 'index', id: params.id)
        } else {
            ['accounts': session.googleAppsAccounts]
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
        ['token': token, 'account': currentAccount]
    }

    /**
     * Saves the token. This makes use of the GoogleAppsCommand object which is
     * and inner class defined at the bottom of this class.
     */
    def save(GoogleAppsCommand cmd) {
        if (request.method == 'POST') {
            def userDefined = (params.definedToken || params.definedTokenConfirmation) ? true : false
            if (cmd.hasErrors()) {
                def errorsForTitle = cmd.errors.allErrors.collect { "${g.message(error: it)}" }.join(' ')
                flash.title = "${message(code: 'bapps.alert.save.errorForTitle')}$errorsForTitle"
                render(view: 'set', model: ['token': params.token, 'account': currentAccount, 'googleApps': cmd, 'userDefined': userDefined])
                return
            } else {
                def result = googleAppsService.saveTokenForAccount(currentAccount, params.definedToken ?: params.token, false, session.person)
                if (result.size() == 0) {
                    emailService.sendBappsSetConfirmation(session.person, currentUsername)
                    if (params.definedToken) {
                        flash.success = message(code: 'bapps.alert.save.success', args: [currentUsername])
                        flash.title = message(code: 'bapps.alert.save.successForTitleDefined', args: [currentUsername])
                        // Redirect to either index or choose...
                        redirect(action: session.googleAppsAccounts.size() == 1 ? 'index' : 'choose')
                    } else {
                        flash.token = params.definedToken ?: params.token
                        flash.title = message(code: 'bapps.alert.save.successForTitleGenerated', args: [currentUsername])
                        redirect(action: 'view', id: params.id)
                    }
                } else {
                    flash.error = result.error
                    flash.title = result.error
                    render(view: 'set', model: ['token': params.token, 'account': currentAccount, 'googleApps': cmd, 'userDefined': userDefined])
                    return
                }
            }
        } else if (currentAccount) {
            redirect(action: 'index')
        }
        // The final else would have already been handled by accountFromId()
    }

    /**
     * Displays the token if the user chooses the pre-generated token.
     */
    def view() {
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
        if (request.method == 'POST') {
            def result = googleAppsService.saveTokenForAccount(
                    currentAccount, tokenService.token(30), true, session.person)

            if (result.size() == 0) {
                emailService.sendBappsDeleteConfirmation(session.person, currentUsername)
                flash.success = message(code: 'bapps.alert.delete.success', args: [currentUsername])
                flash.title = message(code: 'bapps.alert.delete.successForTitle', args: [currentUsername])

                // Redirect to either index or choose...
                redirect(action: session.googleAppsAccounts.size() == 1 ? 'index' : 'choose')
                return
            } else {
                flash.error = result.error
                flash.title = result.error
                ['account': currentAccount]
            }
        } else {
            ['account': currentAccount]
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
        // Only do this if the user has any Google apps. If not, then the
        // SecurityFilter will pick it up.
        if (session.googleAppsAccounts) {
            if (params.id) {
                session.googleAppsAccounts.each {
                    if (it.getPrimaryEmail() == params.id) {
                        currentAccount = it
                        currentUsername = it.getPrimaryEmail()
                    }
                }
            } else if (session.googleAppsAccounts.size() == 1) {
                currentAccount = session.googleAppsAccounts[0]
                currentUsername = currentAccount.getPrimaryEmail()
            }

            if (!currentAccount) {
                session.googleAppsAccounts.size() == 1 ? redirect(action: 'index') : redirect(action: 'choose')
            }
        }
        // If the currentAccount is set, now pull out the actual account from
        // the CalMail database
        if (currentAccount) {
            calMailAccount = calMailService.account(session.person, currentAccount.getPrimaryEmail())
        }

        // Now, if this is any of the actions below and the login is disabled,
        // then we need to redirect to index.
        if (actionName in ['set', 'save', 'view', 'delete'] && calMailAccount?.loginDisabled == true) {
            redirect(action: 'index')
        }
    }

    /**
     * Command Object for interacting with the GoogleAppsService.
     */
    class GoogleAppsCommand {

        /* TokenService service */
        def tokenService

        /* CalNetService service */
        def calNetService

        String token

        String definedToken

        String definedTokenConfirmation

        static constraints = {
            token(blank: true, nullable: true, size: 12..12, validator: { val, obj ->
                if (val && !obj.tokenService.verifyCurrentToken(obj.session.currentToken, val)) {
                    return 'googleAppsCommand.key.donotmatch'
                }
            })
            definedToken(blank: true, nullable: true, size: 9..255, validator: { val, obj ->
                if (!val && obj.definedTokenConfirmation) {
                    return 'googleAppsCommand.definedToken.cannotbeblank'
                } else {
                    if (val && !obj.definedTokenConfirmation) {
                        return 'googleAppsCommand.definedKeyConfirmation.donotmatch'
                    }
                    if (obj.definedTokenConfirmation &&
                            obj.calNetService.testAuthenticationFor(val, obj.session.person)) {
                        return 'googleAppsCommand.definedKey.cannotmatchcalnet'
                    } else if (obj.definedTokenConfirmation &&
                            !obj.calNetService.validatePassphraseComplexityFor(val, obj.session.person)) {
                        return 'googleAppsCommand.definedKey.doesnotmeetrequirements'
                    }
                }
            })
            definedTokenConfirmation(blank: true, nullable: true, size: 9..255, validator: { val, obj ->
                if (val && val != obj.definedToken) {
                    return 'googleAppsCommand.definedKeyConfirmation.donotmatch'
                }
            })
        }

    }

}
