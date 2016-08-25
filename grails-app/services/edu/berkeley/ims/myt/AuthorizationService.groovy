package edu.berkeley.ims.myt

import com.unboundid.ldap.sdk.SearchResultEntry
import org.springframework.beans.factory.annotation.Value

class AuthorizationService {

    /* LdapService */
    def ldapService

    @Value('${ldap.personUsernameAttr}')
    String personUsernameAttr

    List<String> peopleDns

    static final MATCH_PATTERNS = [~/EMPLOYEE-TYPE-.*/, ~/STUDENT-TYPE-.*/, ~/AFFILIATE-TYPE-.*/, ~/GUEST-TYPE-COLLABORATOR/, ~/AFFILIATE-TYPE-ADVCON.*/]
    static final REJECT_PATTERNS = []

    /**
     * Checks to see if a user is authorized to use the WPA service. This has
     * been factored into its own method so that the logic can be easily tested.
     *
     * @param affiliations List of a person's affiliations.
     * @return true if the person is authorized based on their affiliations, or
     * false if not authorized.
     */
    def isAuthorizedWpa(List<String> affiliations) {

        return affiliations?.any { affiliation ->
            // Match one of the MATCH_PATTERNS and none of the REJECT_PATTERNS
            return MATCH_PATTERNS.any { pattern -> affiliation.matches(pattern) } && !(REJECT_PATTERNS.any { pattern -> affiliation.matches(pattern) })
        }
    }

    /**
     * For CalMail, as long as the person has an entry in the CalMail database,
     * and the domainId is 1, then the person is authorized. However, the
     * person may not have a Google Apps account, but that will be determined
     * later, and if not, then appropriate error message will be displayed at
     * that time.
     */
    boolean isAuthorizedCalMail(googleAppsAccounts) {
        googleAppsAccounts
    }

    /**
     * Will search peopleDns for the uid and return the SearchResultEntry if found
     * @param user
     * @return
     */
    SearchResultEntry authorizeUser(String user) {
        for(dn in peopleDns) {
            SearchResultEntry entry = ldapService.find(personUsernameAttr, user, dn)
            if(entry) {
                return entry
            }
        }
        return null
    }

    @Value('${ldap.peopleDnString}')
    void setPeopleDnString(String peopleDns) {
        this.peopleDns = peopleDns.split(';')
    }
}
