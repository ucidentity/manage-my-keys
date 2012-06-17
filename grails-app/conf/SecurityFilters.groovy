import java.util.regex.Matcher
import java.util.regex.Pattern

class SecurityFilters {

    /* GrailsApplication -- needed for the config. */
    def grailsApplication
    
    /* AuthorizationService */
    def authorizationService
    
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
                }
            }
        }
        
        /**
         * WPA authorized users check.
         */
        isAuthorizedWpa(controller:'wpa', action:'*') {
            before = {
                def affiliations =
                    session.person.getAttributeValues(grailsApplication.config.ldap.personAffiliationAttr)
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
        isAuthorizedCalMail(controller:'bmail', action:'*') {
            before = {
                if (authorizationService.isAuthorizedCalMail(session.googleAppsAccounts)) {
                    return true
                }
                else {
                    redirect(controller:'auth', action:'notEligibleBApps')
                    return false
                }
            }
        }
        
    }
    
}