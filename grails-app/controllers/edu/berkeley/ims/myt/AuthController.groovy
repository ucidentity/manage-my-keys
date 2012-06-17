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
        //redirect(action:'login')
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
        else {
            def user = request.getHeader('REMOTE_USER') ?: request.getRemoteUser()
            if (!user) {
                log.error("Did not get user from REMOTE_USER header...")
                redirect(action:'failure')
            }
            else {
                //def person = ldapService.findByEppn(user)
                def person = ldapService.find(
                    grailsApplication.config.ldap.personUsernameAttr, user)
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
        }
    }

    /**
     * Invalidate the session, and send the user to the config.myt.logoutURL.
     */
    def logout() {
        session.invalidate()
        redirect(url:grailsApplication.config.myt.logoutURL)
        return
    }

    def notAuthorized() { }
    
    def failure() { }
    
    def notEligibleWpa() { }
    
    def notEligibleBApps() { }
}
