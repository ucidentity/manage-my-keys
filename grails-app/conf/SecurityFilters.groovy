import javax.servlet.http.HttpSession

class SecurityFilters {

    /* GrailsApplication -- needed for the config. */
    def grailsApplication
    
    /* AuthorizationService */
    def authorizationService
    
    /* GoogleAppsService */
    def googleAppsService
    
    def filters = {
        
        /** 
         * Make sure all controllers except for the auth controller
         * require AuthN.
         */
        isAuthenticated(controller:'*', action:'*') {
            before = {
                if (controllerName == 'auth') {
                    return true
                }
                else {
                    if (!session.person) {
                        session.intendedController = controllerName
                        session.intendedAction = actionName
                        session.intendedParams = params
                        redirect(controller:'auth', action:'index')
                        return false
                    }
                    getGoogleAccountForSession(session)
                }
            }
        }
        
        /**
         * WPA authorized users check.
         */
        isAuthorizedWpa(controller:'wpa', action:'*') {
            before = {
                def affiliations =
                    session.person.getAttributeValues(grailsApplication.config.ldap.personAffiliationAttr as String) as List<String>
                def isAuthorized = authorizationService.isAuthorizedWpa(affiliations)
                
                if (isAuthorized) {
                    return true
                }
                else {
                    redirect(controller:'auth', action:'notEligibleWpa')
                    return false
                }
            }
        }
        
        /**
         * CalMail authorized users check.
         */
        isAuthorizedCalMail(controller:'bapps', action:'*') {
            before = {
                getGoogleAccountForSession(session)
                if(!session.googleAppsAccount) {
                    redirect(controller:'auth', action:'notEligibleBApps')
                    return false
                }
                return true
            }
        }
        
    }

    private void getGoogleAccountForSession(HttpSession session) {
        if (!session.googleAppsAccount) {
            session.googleAppsAccount = googleAppsService.getGoogleAppsAccount(session.person)
        }
    }

}