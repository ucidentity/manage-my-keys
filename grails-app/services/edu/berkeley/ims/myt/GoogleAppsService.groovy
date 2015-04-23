package edu.berkeley.ims.myt

import com.google.api.services.admin.directory.model.User
import com.unboundid.ldap.sdk.SearchResultEntry
import edu.berkeley.calnet.mmk.Password

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
    List<User> googleAppsAccounts(SearchResultEntry person) {
        List<CalMail> calMailAccounts = getCalMailAccounts(person)

        return calMailAccounts.inject([]) { List<User> list, account ->
            def gAccount = googleAdminAPIService.getUser(account.localpart)
            if (gAccount) {
                list.add(gAccount)
            }
            return list
        }
    }

    /**
     * Takes the passed in {@code user}, and {@code password} and sets the
     * user's Google Apps Account password. The {@code isRandom} param is so that
     * the logging mechanism knows whether or not this is a set by the user,
     * or a set by the app (which is actually a delete).
     *
     * @param user The Google Apps user to be updated.
     * @param password The password to set for the provided user.
     */
    def saveTokenForAccount(User user, String password, Boolean isGenerated, SearchResultEntry person) {
        Long uid = person.getAttributeValueAsInteger('uid') as Long
        log.debug("Setting password for uid: $uid ($user.getPrimaryEmail()")
        user.setHashFunction(Password.PASSWORD_HASH)
        user.setPassword(password)
        googleAdminAPIService.updatePasswordToken(user.getPrimaryEmail(), user)
        if (isGenerated) {
            log.info("Deleted (set random) Google Apps token for: [uid: ${person.getAttributeValue('uid')}], [username: ${person.getAttributeValue(config.calNetUsername)}], [account: ${user.getLogin().getUserName()}].")
        } else {
            log.info("Set Google Apps token for: [uid: ${person.getAttributeValue('uid')}], [username: ${person.getAttributeValue(config.calNetUsername)}], [account: ${user.getLogin().getUserName()}].")
        }
    }

    /**
     * Get a persons Google Mail Accounts based on registrations in CalMail
     * @param person
     * @return
     */
    private List<CalMail> getCalMailAccounts(SearchResultEntry person) {
        int uid = person.getAttributeValueAsInteger('uid') as Integer
        int googleAppsDomainId = config.gAppsDomainId as Integer
        def calMailAccounts = CalMail.findAllByOwnerUidAndDomainId(uid, googleAppsDomainId)
        calMailAccounts
    }


    private Map getConfig() {
        grailsApplication.config.myt
    }

}
