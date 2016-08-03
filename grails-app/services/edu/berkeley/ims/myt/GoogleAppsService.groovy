package edu.berkeley.ims.myt
import com.unboundid.ldap.sdk.SearchResultEntry
import edu.berkeley.calnet.mmk.GoogleUser
import edu.berkeley.calnet.mmk.TokenUtil

class GoogleAppsService {

    def grailsApplication
    def googleAdminAPIService

    /**
     * Returns all of the Google Apps accounts for the passed in person.
     *
     * @param person UnboundID LDAP SDK person
     * @return A list of the accounts the person has. Could be none, but it will
     * still be a list.
     */
    GoogleUser getGoogleAppsAccount(SearchResultEntry person) {
        String primaryEmail = getPrimaryMailAccount(person)
        log.debug("Searching for $primaryEmail in the Google API")
        def user = googleAdminAPIService.getUser(primaryEmail)
        if (user) {
            log.debug("Found user $primaryEmail in Google API")
            new GoogleUser(name: user.getName().getFullName(), emailAddress: user.getPrimaryEmail())
        } else {
            log.debug("Did not find user $primaryEmail in google API")
            return null
        }
    }

    /**
     * Takes the passed in {@code person}, and {@code token} and sets the
     * user's Google Apps Account token. The {@code userDefined} is so that
     * the logging mechanism knows whether or not this is a set by the user,
     * or a set by the app.
     *
     * @param person The Google Apps user to be updated.
     * @param token The token to set for the provided user.
     * @param userDefined set by user or system (defaults to system)
     */
    boolean updateTokenForAccount(SearchResultEntry person, String token, Boolean userDefined = false) {
        String primaryEmail = getPrimaryMailAccount(person)
        Long uid = person.getAttributeValueAsInteger('uid') as Long

        log.debug("Setting token for uid: $uid ($primaryEmail")
        googleAdminAPIService.updatePasswordToken(primaryEmail, token)

        def calnetId = getCalnetId(person)
        if (userDefined) {
            log.info("Set Google Apps token for: [uid: $uid], [calnetId: $calnetId], [account: $primaryEmail].")
        } else {
            log.info("Set Random Google Apps token for: [uid: $uid], [calnetId: $calnetId], [account: $primaryEmail].")
        }
        true
    }

    /**
     * Takes the passed in {@code person}, sets the
     * user's Google Apps Account token to a 30 char randomized key.
     *
     * @param person The Google Apps user to be updated.
     */
    boolean deleteTokenForAccount(SearchResultEntry person) {
        String primaryEmail = getPrimaryMailAccount(person)
        Long uid = person.getAttributeValueAsInteger('uid') as Long

        log.debug("Setting token for uid: $uid ($primaryEmail")
        googleAdminAPIService.updatePasswordToken(primaryEmail, TokenUtil.generateToken(30))

        def calnetId = getCalnetId(person)
        log.info("Deleted (randomized with 30 chars) Google Apps token for: [uid: $uid], [calnetId: $calnetId], [account: $primaryEmail].")
        return true
    }

    /**
     * Get the email associated with bConnected from the LDAP person logged in.
     * @param person
     * @return
     */
    private String getPrimaryMailAccount(SearchResultEntry person) {
        person.getAttributeValue(config.berkeleyEduAlternateID as String)
    }

    /**
     * Get the calnetId associated with bConnected from the LDAP person logged in.
     * @param person
     * @return
     */
    private String getCalnetId(SearchResultEntry person) {
        String calnetIdKey = config.calNetUsername
        person.getAttributeValue(calnetIdKey)
    }


    private Map getConfig() {
        grailsApplication.config.myt
    }

}
