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
        isAuthenticated(controller: '*', action: '*') {
            before = {
                if (controllerName == 'auth') {
                    return true
                } else {
                    if (!session.person) {
                        session.intendedController = controllerName
                        session.intendedAction = actionName
                        session.intendedParams = params
                        redirect(controller: 'auth', action: 'index')
                        return false
                    }
                    // Get the Wpa and Google Account for session.
                    getWpaForSession(session)
                    getGoogleAccountForSession(session)
                }
                return true

            }
        }

        /**
         * WPA authorized users check.
         */
        isAuthorizedWpa(controller: 'wpa', action: '*') {
            before = {
                getWpaForSession(session)
                if (!session.isWpaAuthorized) {
                    redirect(controller: 'auth', action: 'notEligibleWpa')
                    return false
                }
                return true
            }
        }

        /**
         * CalMail authorized users check.
         */
        isAuthorizedCalMail(controller: 'bapps', action: '*') {
            before = {
                getGoogleAccountForSession(session)
                if (!session.googleAppsAccount) {
                    redirect(controller: 'auth', action: 'notEligibleBApps')
                    return false
                }
                return true
            }
        }

    }

    private void getWpaForSession(HttpSession session) {
        if (!session.isWpaAuthorized) {
            def affiliations = session.person.getAttributeValues(grailsApplication.config.ldap.personAffiliationAttr as String) as List<String>
            session.isWpaAuthorized = authorizationService.isAuthorizedWpa(affiliations)
        }
    }

    private void getGoogleAccountForSession(HttpSession session) {
        if (!session.googleAppsAccount) {
            session.googleAppsAccount = googleAppsService.getGoogleAppsAccount(session.person)
        }
    }

}
