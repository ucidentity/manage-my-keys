package edu.berkeley.ims.myt

import com.unboundid.ldap.sdk.SearchResultEntry
import edu.berkeley.calnet.mmk.GoogleUser
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.codehaus.groovy.grails.web.util.WebUtils

class EmailService {

    /* GrailsApplication -- needed for the config. */
    def grailsApplication

    /* Instance of the MailService plugin. */
    def mailService
    def messageSource

    /**
     * Sends a confirmation email to a user when he/she sets an AirBears2 Login
     * Token.
     *
     * @param person           The person to who the email should be sent.
     */
    def sendWpaSetConfirmation(SearchResultEntry person) {
        String email = getEmailAddress(person)
        if (email) {
            def personName = getPersonName(person)
            def subject = getMessage('wpa.email.setConfirmation.subject')
            def message = getMessage('wpa.email.setConfirmation.body', personName, serverUrl)
            sendMail(email, subject, message)
        }
        else {
            def username = person.getAttributeValue(config.myt.wpaTokenLdapUsername)
            log.info("Can't send WPA Set Confirmation; no email address for user ${username}.")
        }
    }

    /**
     * Sends a confirmation email to a user when he/she deletes an AirBears2
     * Login Token.
     *
     * @param person           The person to who the email should be sent.
     */
    def sendWpaDeleteConfirmation(SearchResultEntry person) {
        String email = getEmailAddress(person)
        if (email) {
            def personName = getPersonName(person)
            def subject = getMessage('wpa.email.deleteConfirmation.subject')
            def message = getMessage('wpa.email.deleteConfirmation.body', personName, serverUrl)
            sendMail(email, subject, message)
        }
        else {
            def username = person.getAttributeValue(config.myt.wpaTokenLdapUsername)
            log.info("Can't send WPA Delete Confirmation; no email address for user ${username}.")
        }
    }

    /**
     * Sends a confirmation email to a user when he/she sets a bApps Login
     * Token.
     *
     * @param googleUser   Name and google email address.
     */
    def sendBappsSetConfirmation(GoogleUser googleUser) {
        def subject = getMessage('bapps.email.setConfirmation.subject')
        def message = getMessage('bapps.email.setConfirmation.body',googleUser.name, googleUser.emailAddress, serverUrl)
        sendMail(googleUser.emailAddress, config.myt.bAppsEmailSetSubject, message)
    }

    /**
     * Sends a confirmation email to a user when he/she deletes a bApps
     * Login Token.
     *
     * @param person            The person to who the email should be sent.
     * @param username          The email for which the token was deleted.
     */
    def sendBappsDeleteConfirmation(GoogleUser googleUser) {
        def subject = getMessage('bapps.email.deleteConfirmation.subject')
        def message = getMessage('bapps.email.deleteConfirmation.body', googleUser.name, googleUser.emailAddress, serverUrl)
        sendMail(googleUser.emailAddress, subject, message)
    }
    private String getEmailAddress(SearchResultEntry person) {
        person.getAttributeValue(config.myt.tokenLdapEmailAddress) ?:person.getAttributeValue('mail')
    }

    String getPersonName(SearchResultEntry person) {
        person.getAttributeValue('displayName')
    }

    private Map getConfig() {
        grailsApplication.config
    }

    private String getServerUrl() {
        config.grails.serverURL
    }

    private sendMail(String email, String title, String text) {
        mailService.sendMail {
            to email
            from config.grails.mail.from
            replyTo config.grails.mail.replyTo
            subject title
            body(text)
        }
    }

    private getMessage(String code, Object... args) {
        GrailsWebRequest webUtils = WebUtils.retrieveGrailsWebRequest()
        def request = webUtils.getCurrentRequest()
        def locale = request.getLocale()
        messageSource.getMessage(code, args, locale)
    }
}
