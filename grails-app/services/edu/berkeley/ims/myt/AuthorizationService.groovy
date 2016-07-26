package edu.berkeley.ims.myt

class AuthorizationService {
    static final MATCH_PATTERNS = [~/EMPLOYEE-TYPE-.*/, ~/STUDENT-TYPE-.*/, ~/AFFILIATE-TYPE-.*/, ~/GUEST-TYPE-COLLABORATOR/]
    static final REJECT_PATTERNS = [~/AFFILIATE-TYPE-ADVCON.*/]

    /**
     * Checks to see if a user is authorized to use the WPA service. This has
     * been factored into its own method so that the logic can be easily tested.
     *
     * @param affiliations    List of a person's affiliations.
     * @return true if the person is authorized based on their affiliations, or
     * false if not authorized.
     */
    def isAuthorizedWpa(List<String> affiliations) {

        return affiliations?.any { affiliation ->
            // Match one of the MATCH_PATTERNS and none of the REJECT_PATTERNS
            return MATCH_PATTERNS.any { pattern -> affiliation.matches(pattern) } && !(REJECT_PATTERNS.any { pattern -> affiliation.matches(pattern)})
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
}
