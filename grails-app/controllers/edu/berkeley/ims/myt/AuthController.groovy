package edu.berkeley.ims.myt

import org.springframework.web.servlet.ModelAndView

class AuthController {

    /* GrailsApplication -- needed for the config. */
    def grailsApplication
    
    /* LdapService */
    def ldapService
    
    /**
     * Displays the base login page for the app.
     */
    def index() {
        if (session.person) {
            redirect(controller:'main', action:'index')
        }
    }

    /**
     * Logs a user in by checking the REMOTE_USER header.
     */
    def login() {
        if (session.person) {
            redirect(controller:'main', action:'index')
        }
        else if (grails.util.Environment.current.name == "development" &&
            grailsApplication.config.myt.autoLoginUserAs) {
            def person = ldapService.find(
                grailsApplication.config.ldap.personUsernameAttr,
                grailsApplication.config.myt.autoLoginUserAs)
            setSessionAndRedirect(person)
        }
        else {
            def user = request.getHeader('REMOTE_USER') ?: request.getRemoteUser()
            if (!user) {
                log.error "Did not get user from REMOTE_USER header..."
                redirect(action:'failure')
            }
            else {
                def person = ldapService.find(
                    grailsApplication.config.ldap.personUsernameAttr, user)
                setSessionAndRedirect(person)
            }
        }
    }

    private setSessionAndRedirect(person) {
        if (person) {
            session.person = person
            // Get these from the session. They were set by the
            // isAuthenticated SecurityFilter.
            if (session.intendedController) {
                redirect(
                    controller:session.intendedController,
                    action:session.intendedAction,
                    params: session.intendedParams)
            }
            else {
                redirect(controller:'main', action:'index')
            }
        }
        else {
            redirect(action:'notAuthorized')
        }
    }

    /**
     * Invalidate the session, and send the user to the config.myt.logoutURL, if one
     * is specified.
     */
    def logout() {
        session.invalidate()
        if (grailsApplication.config.myt?.logoutURL) {
            redirect(url:grailsApplication.config.myt.logoutURL)
        }
        else {
            flash.logout = message(code:'auth.logout')
            redirect(action:'index')
        }
    }

    def notAuthorized() { }
    
    def failure() { }
    
    def notEligibleWpa() { }
    
    def notEligibleBApps() { }
}
