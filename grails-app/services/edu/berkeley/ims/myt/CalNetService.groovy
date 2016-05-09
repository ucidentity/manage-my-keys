package edu.berkeley.ims.myt

import com.unboundid.ldap.sdk.Entry
import groovyx.net.http.HTTPBuilder

//import grails.plugins.rest.client.RestBuilder

class CalNetService {

    /* GrailsApplication -- needed for the config. */
    def grailsApplication

    /**
     * Takes the passed in {@code person} and tries to authenticate
     * as that user using the passed in {@code token}.
     *
     * @param passphrase The token/password to use for the authN attempt
     * @param person UnboundID LDAP SDK person
     * @return true if the authN attempt is successful, and false otherwise.
     */
    boolean testAuthenticationFor(String passphrase, Entry person) {
        def calnetId = person.getAttributeValue(grailsApplication.config.myt.calNetUsername as String)
        def url = "${grailsApplication.config.myt.krbURL}authN"

        def params = [
                appid     : grailsApplication.config.myt.krbAppId,
                authkey   : grailsApplication.config.myt.krbAuthKey,
                id        : calnetId,
                passphrase: passphrase
        ]

        def response

        def http = new HTTPBuilder(url)

        http.post(body: params) { resp, reader ->
            response = reader.text.split(/\n/)
        }

        return response[0] == "0"

    }

    boolean validatePassphraseComplexityFor(String passphrase, Entry person) {

        // Get the ID and displayName of the person
        String calnetId = person.getAttributeValue(grailsApplication.config.myt.calNetUsername as String)
        String displayName = person.getAttributeValue('displayName')

        return !PassphraseComplexityValidator.isUsernameInPassphrase(calnetId, passphrase) &&
                !PassphraseComplexityValidator.isDisplayNameInPassphrase(displayName, passphrase) &&
                PassphraseComplexityValidator.isComplexPassphrase(passphrase)
    }
}
