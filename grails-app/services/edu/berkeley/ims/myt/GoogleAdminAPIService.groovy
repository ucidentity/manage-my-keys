package edu.berkeley.ims.myt

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.admin.directory.Directory
import com.google.api.services.admin.directory.DirectoryScopes
import com.google.api.services.admin.directory.model.User
import grails.plugin.cache.CacheEvict
import grails.plugin.cache.Cacheable
import org.springframework.beans.factory.InitializingBean
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

import javax.xml.bind.DatatypeConverter
import java.security.KeyStore
import java.security.MessageDigest
import java.security.PrivateKey

class GoogleAdminAPIService implements InitializingBean {

    private static final String MD5 = "MD5"


    def grailsApplication

    private Directory directoryService

    /**
     * Get a Google User from the Admin API
     * @param userId The users account name (user@berkeley.edu)
     * @return The found user
     */
    User getUser(String userId) {
        // This will alter the users domain (eg. for testg.berkeley.edu)
        userId = prefixDomain(userId)
        log.debug("Retrieving user: $userId")
        try {
            def user = directoryService.users().get(userId).execute()
            if (user?.suspended) {
                log.warn("The Retrieved user is suspended: $userId, $user")
                return null
            }
            log.debug("Retrieved user: $userId, $user")
            return user
        } catch (e) {
            log.warn("Failed to retrieve user: $userId: $e.message", e)
            return null
        }
    }

    /**
     * Update a users password token with the Admin API
     * @param userId The users account name (user@berkeley.edu)
     * @param token
     */
    void updatePasswordToken(String userId, String token) {
        userId = prefixDomain(userId)
        def user = getUser(userId)
        if (user) {

            MessageDigest md = MessageDigest.getInstance(MD5);
            byte[] digested = md.digest(token.getBytes("UTF-8"));
            def newToken = DatatypeConverter.printHexBinary(digested);

            user.setHashFunction(MD5).setPassword(newToken)
            try {
                directoryService.users().update(user, user).execute()
                log.debug("Updated user $userId password")
            } catch (e) {
                log.warn("Failed to update $userId password: $e.message", e)
            }
        } else {
            log.warn("Could not update $userId password")
        }
    }
    /**
     * For test-servers, the user account domain should be prefixed with 'testg'
     * @param userId
     * @return
     */
    String prefixDomain(String userId) {
        String domainPrefix = adminAPIConfig.domainPrefix ?: ''
        if (domainPrefix && !userId.contains(domainPrefix)) {
            def (String account, String domain)  = userId.split('@')
            userId = "${account}@${domainPrefix}.${domain}"
        }
        return userId
    }
    /**
     * Initialize the Google Admin API Directory Service client
     * @throws Exception if creating the api fails
     */
    @Override
    void afterPropertiesSet() {
        try {
            String emailAddress = adminAPIConfig.apiEmailAddress
            String serviceAccountUser = adminAPIConfig.serviceAccountUser
            String applicationName = adminAPIConfig.apiApplicationName
            log.info("Initializing Google API with: $emailAddress, $serviceAccountUser, $applicationName")

            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance()
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport()

            GoogleCredential credential = new GoogleCredential.Builder()
                    .setTransport(httpTransport)
                    .setJsonFactory(jsonFactory)
                    .setServiceAccountId(emailAddress)
                    .setServiceAccountPrivateKey(privatePKCS12Key)
                    .setServiceAccountScopes([DirectoryScopes.ADMIN_DIRECTORY_USER])
                    .setServiceAccountUser(serviceAccountUser)
                    .build()

            // Create a new authorized API client
            directoryService = new Directory.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName(applicationName)
                    .build()
            log.info("Initializing Google API complete")
        } catch (e) {
            log.warn("Failed to initialize Google API", e)
            throw e
        }
    }

    /**
     * Get the private key from the keystore (classpath: or file://)
     * @return the privateKey from the keystore
     * @throws Exception  If the keystore is not found or if the key alias or secret is wrong
     */
    private PrivateKey getPrivatePKCS12Key() {
        KeyStore keystore = KeyStore.getInstance("PKCS12")
        def keystorePath = adminAPIConfig.keystore.path as String
        def keystoreAlias = adminAPIConfig.keystore.alias as String
        def keystoreSecret = adminAPIConfig.keystore.secret.toString().toCharArray()
        def resourceResolver = new PathMatchingResourcePatternResolver()
        def keystoreResource = resourceResolver.getResource(keystorePath)
        log.info "Attempting to load Google Admin API Key from '$keystorePath'"
        if (!keystoreResource.exists()) {
            throw new RuntimeException("Could not find keystore at: $keystorePath")
        }
        keystore.load(keystoreResource.inputStream, keystoreSecret)
        return keystore.getKey(keystoreAlias, keystoreSecret) as PrivateKey
    }

    private Map getAdminAPIConfig() {
        grailsApplication.config.mmk.googleAdminAPI
    }
}
