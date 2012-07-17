// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

grails.config.locations = [ "classpath:myt-config.properties",
                            "classpath:${appName}-config.groovy",
                            "file:${userHome}/.grails/${appName}-config.properties",
                            "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }


grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']


// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password', 'token', 'definedToken', 'definedTokenConfirmation']

// enable query caching by default
grails.hibernate.cache.queries = true

// MailService Plugin
grails.mail.disabled=false

/*
 * Passwords, so they are all in one place so we can easily
 * remove them before committing. I would rather changes these
 * often in one place, than remember to check all the envs
 * before committing and building.
 */
/* Mail password, needed for dev only. */
grails.mail.password = ''

/* Krb Service password. */
myt.krbAuthKey = ''

/* Google Apps password. */
myt.gAppsPassword = '' // testg.berkeley.edu

/* LDAP password. */
ldap.bindPassword = '' // dev
//ldap.bindPassword = '' // ucb dev, test, prod

/* CalNet Test ID passphrase. */
myt.calNetTestIdPassphrase = ''

// set per-environment serverURL stem for creating absolute links
environments {
    development {
        grails.logging.jul.usebridge = true
        // Use the properties file which is defined at the top of the file.
        
        grails.serverURL = 'http://nose10-2.local/mmk'
        
        grails.mail.host = 'mail.ucsf.edu'
        grails.mail.from = 'vldappy@ucsf.edu'
        grails.mail.replyTo = 'donotreply@ucsf.edu.edu'
        grails.mail.port = 465
        grails.mail.username = 'ldappys@som.ucsf.edu'
        grails.mail.props = [
            "mail.smtp.auth":"true",
            "mail.smtp.socketFactory.port":"465",
            "mail.smtp.socketFactory.class":"javax.net.ssl.SSLSocketFactory",
            "mail.smtp.socketFactory.fallback":"false"
        ]
        
        /* Where the application should send the user after logout. This will
           probably be the CAS logout URL, or the local Shib SP logout URL.
           http://nose10-2.local/Shibboleth.sso/Logout for dev and test
           https://auth-test.berkeley.edu/cas/logout for UCB dev and test 
           https://auth.berkeley.edu/cas/logout for UCB prod */
        myt.logoutURL = 'http://nose10-2.local/Shibboleth.sso/Logout'
        
        /* Kerberos Service app ID. */
        myt.krbAppId = 'calnettoken'
        
        /* Kerberos Service URL. */
        myt.krbURL = 'https://net-auth.berkeley.edu/cgi-bin/krbservice?'
        
        /* Google Apps API URL for getting and update a user entry */
        myt.gAppsUrl = 'https://apps-apis.google.com/a/feeds/testg.berkeley.edu/user/2.0'
        
        /* Username for the Google Apps API */
        myt.gAppsUsername = 'tokenapp@testg.berkeley.edu'
        
        /* domain_id in the CalMail accounts table. */
        myt.gAppsDomainId = 110

        /* CalNet username attribute 
           eduPersonPrincipalName for dev and test
           berkeleyEduKerberosPrincipalString for production */
        //myt.calNetUsername = 'eduPersonPrincipalName'
        myt.calNetUsername = 'carLicense' // Contains 'lr' for Lucas

        /* Attribute in ldap that will be used for the username in the WPA token
           database.
           eduPersonPrincipalName for def and test
           berkeleyEduKerberosPrincipalString for production */
        myt.wpaTokenLdapUsername = 'eduPersonPrincipalName'

        /* Mail attribute for sending mail once someone sets/deletes their WPA token.
           mail for dev and test
           berkeleyEduOfficialEmail for production */
        myt.tokenLdapEmailAddress = 'mail'
        
        /* LDAP hostname(s). If more than one, separate them with a comma. Do not
           leave a space. */
        ldap.addresses = 'localhost,localhost'

        /* LDAP port(s). If more than one, separate them with a comma. Do not leave a
           space, and they number must match the number of hosts, above. */
        ldap.ports = '3636,3636'

        /* Whether or not the LDAP service should auto reconnect. */
        ldap.autoReconnect = true

        /* Whether or not the LDAP service should use SSL. */
        ldap.useSsl = true

        /* Whether or not the LDAP service should implicitly trust the SSL cert. */
        ldap.alwaysTrustSslCert = true

        /* DN of the privileged LDAP bind. */
        ldap.bindDn = 'cn=Manager With Timeout'

        /* DN of the LDAP server. */
        ldap.baseDn = 'dc=ucsf,dc=edu'
        //ldap.baseDn = 'dc=berkeley,dc=edu'

        /* DN for the branch where people are found in the LDAP server. */
        ldap.peopleDn = 'ou=people,dc=ucsf,dc=edu'
        //ldap.peopleDn = 'ou=people,dc=berkeley,dc=edu'

        /* RDN attribute for people, e.g., uid, cn. */
        ldap.peopleRdnAttr = 'uid'

        /* Person ID attribute. Right now this only works if you have one ID number
           for everyone. Not being used by this app, but the LdapService wants to
           to know about it */
        ldap.personIdAttr = 'ucsfEduIDNumber'

        /* Person username attribute. This should be the name of the attribute which
           will map to the remoteUserHeader from CAS or Shib.
           eduPersonPrincipalName for Shib
           uid for CAS */
        ldap.personUsernameAttr = 'eduPersonPrincipalName'

        /* Person affiliation attribute.
           ucsfEduStatus for dev and test
           berkeleyEduAffiliations for production */
        ldap.personAffiliationAttr = 'ucsfEduStatus'
        
    }
    test {
        grails.logging.jul.usebridge = true
        // Use the properties file which is defined at the top of the file.
        
        grails.serverURL = 'http://nose10-2.local/mmk'
        
        grails.mail.host = 'mail.ucsf.edu'
        grails.mail.from = 'lucas.rockwell@ucsf.edu'
        grails.mail.replyTo = 'donotreply@berkeley.edu'
        grails.mail.port = 465
        grails.mail.username = 'campus\\lrockwell'
        grails.mail.props = [
            "mail.smtp.auth":"true",
            "mail.smtp.socketFactory.port":"465",
            "mail.smtp.socketFactory.class":"javax.net.ssl.SSLSocketFactory",
            "mail.smtp.socketFactory.fallback":"false"
        ]
        
        /* Google Apps API URL for getting and update a user entry */
        myt.gAppsUrl = 'https://apps-apis.google.com/a/feeds/testg.berkeley.edu/user/2.0'
        
        /* Username for the Google Apps API */
        myt.gAppsUsername = 'tokenapp@testg.berkeley.edu'
        
        /* domain_id in the CalMail accounts table. */
        myt.gAppsDomainId = 110
        
        /* Where the application should send the user after logout. This will
           probably be the CAS logout URL, or the local Shib SP logout URL.
           http://nose10-2.local/Shibboleth.sso/Logout for dev and test
           https://auth-test.berkeley.edu/cas/logout for UCB dev and test 
           https://auth.berkeley.edu/cas/logout for UCB prod */
        myt.logoutURL = 'http://nose10-2.local/Shibboleth.sso/Logout'

        /* Kerberos Service app ID. */
        myt.krbAppId = 'calnettoken'
        
        /* Kerberos Service URL. */
        myt.krbURL = 'https://net-auth.berkeley.edu/cgi-bin/krbservice?'

        /* CalNet username attribute 
           eduPersonPrincipalName for dev and test
           berkeleyEduKerberosPrincipalString for production */
        myt.calNetUsername = 'eduPersonPrincipalName'
        //myt.calNetUsername = 'berkeleyEduKerberosPrincipalString'

        /* Attribute in ldap that will be used for the username in the WPA token
           database.
           eduPersonPrincipalName for def and test
           berkeleyEduKerberosPrincipalString for production */
        myt.wpaTokenLdapUsername = 'eduPersonPrincipalName'

        /* Mail attribute for sending mail once someone sets/deletes their WPA token.
           mail for dev and test
           berkeleyEduOfficialEmail for production */
        myt.tokenLdapEmailAddress = 'mail'
        
        /* LDAP hostname(s). If more than one, separate them with a comma. Do not
           leave a space. */
        ldap.addresses = 'localhost,localhost'
        //ldap.addresses = 'ldap-test.berkeley.edu'

        /* LDAP port(s). If more than one, separate them with a comma. Do not leave a
           space, and they number must match the number of hosts, above. */
        ldap.ports = '3636,3636'
        //ldap.ports = '636'

        /* Whether or not the LDAP service should auto reconnect. */
        ldap.autoReconnect = true

        /* Whether or not the LDAP service should use SSL. */
        ldap.useSsl = true

        /* Whether or not the LDAP service should implicitly trust the SSL cert. */
        ldap.alwaysTrustSslCert = true

        /* DN of the privileged LDAP bind. */
        ldap.bindDn = 'cn=Manager With Timeout'
        //ldap.bindDn = 'uid=manage_tokens,ou=applications,dc=berkeley,dc=edu'

        /* DN of the LDAP server. */
        ldap.baseDn = 'dc=ucsf,dc=edu'
        //ldap.baseDn = 'dc=berkeley,dc=edu'

        /* DN for the branch where people are found in the LDAP server. */
        //ldap.peopleDn = 'ou=people,dc=ucsf,dc=edu'
        ldap.peopleDn = 'ou=people,dc=berkeley,dc=edu'

        /* RDN attribute for people, e.g., uid, cn. */
        ldap.peopleRdnAttr = 'uid'

        /* Person ID attribute. Right now this only works if you have one ID number
           for everyone. Not being used by this app, but the LdapService wants to
           to know about it */
        ldap.personIdAttr = 'ucsfEduIDNumber'

        /* Person username attribute. This should be the name of the attribute which
           will map to the remoteUserHeader from CAS or Shib.
           eduPersonPrincipalName for Shib
           uid for CAS */
        ldap.personUsernameAttr = 'eduPersonPrincipalName'

        /* Person affiliation attribute.
           ucsfEduStatus for dev and test
           berkeleyEduAffiliations for production */
        ldap.personAffiliationAttr = 'ucsfEduStatus'
    }
    production {
        grails.logging.jul.usebridge = false
        
        //grails.serverURL = 'https://idc-d1.calnet.berkeley.edu/mmk'
        grails.serverURL = 'https://idc-test.berkeley.edu/mmk'
        //grails.serverURL = 'https://idc.berkeley.edu/mmk'
        
        grails.mail.host = 'localhost'
        grails.mail.from = 'donotreply@berkeley.edu'
        grails.mail.replyTo = 'donotreply@berkeley.edu'
        grails.mail.port = 25
        
        /* Where the application should send the user after logout. */
        myt.logoutURL = 'https://auth-test.berkeley.edu/cas/logout'

        /* Kerberos Service app ID. */
        myt.krbAppId = 'calnettoken'

        /* Kerberos Service URL. */
        myt.krbURL = 'https://net-auth.berkeley.edu/cgi-bin/krbservice?'

        /* Google Apps API URL for getting and update a user entry */
        myt.gAppsUrl = 'https://apps-apis.google.com/a/feeds/testg.berkeley.edu/user/2.0'
        
        /* Username for the Google Apps API */
        myt.gAppsUsername = 'tokenapp@testg.berkeley.edu'
        
        /* domain_id in the CalMail accounts table. */
        myt.gAppsDomainId = 110
        
        /* CalNet username attribute  */
        myt.calNetUsername = 'berkeleyEduKerberosPrincipalString'

        /* Attribute in ldap that will be used for the username in the WPA token
           database. */
        myt.wpaTokenLdapUsername = 'berkeleyEduKerberosPrincipalString'

        /* Mail attribute for sending mail once someone sets/deletes their WPA token. */
        myt.tokenLdapEmailAddress = 'berkeleyEduOfficialEmail'
        
        /* LDAP hostname(s). If more than one, separate them with a comma. Do not
           leave a space. */
        ldap.addresses = 'ldap.berkeley.edu,ldap.berkeley.edu'

        /* LDAP port(s). If more than one, separate them with a comma. Do not leave a
           space, and they number must match the number of hosts, above. */
        ldap.ports = '636,636'

        /* Whether or not the LDAP service should auto reconnect. */
        ldap.autoReconnect = true

        /* Whether or not the LDAP service should use SSL. */
        ldap.useSsl = true

        /* Whether or not the LDAP service should implicitly trust the SSL cert. */
        ldap.alwaysTrustSslCert = true

        /* DN of the privileged LDAP bind. */
        ldap.bindDn = 'uid=manage_tokens,ou=applications,dc=berkeley,dc=edu'

        /* DN of the LDAP server. */
        ldap.baseDn = 'dc=berkeley,dc=edu'

        /* DN for the branch where people are found in the LDAP server. */
        ldap.peopleDn = 'ou=people,dc=berkeley,dc=edu'

        /* RDN attribute for people, e.g., uid, cn. */
        ldap.peopleRdnAttr = 'uid'
        
        /* Person ID attribute. Right now this only works if you have one ID number
           for everyone. Not being used by this app, but the LdapService wants to
           to know about it */
        ldap.personIdAttr = 'ucsfEduIDNumber'

        /* Person username attribute. This should be the name of the attribute which
           will map to the remoteUserHeader from CAS or Shib. */
        ldap.personUsernameAttr = 'uid'

        /* Person affiliation attribute. */
        ldap.personAffiliationAttr = 'berkeleyEduAffiliations'
        
    }
    
}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}
    appenders {
        rollingFile name:'file', maxFileSize:10240,
            file:'/var/log/tomcat6/mmk.log'
        rollingFile name:'stacktrace', maxFileSize:10240,
            file:'/var/log/tomcat6/mmk-stacktrace.log'
    }
    root {
        info 'stdout', 'file'
    }

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
           
    info 'grails.app.edu.berkeley.ims.myt',
         'grails.app.services.edu.berkeley.ims.myt.WpaService',
         'grails.app.services.edu.berkeley.ims.myt.KerberosService'
         'grails.app.services.edu.berkeley.ims.myt.GoogleAppsService'
    
    debug 'grails.app.edu.berkeley.ims.myt',
          'grails.app.services.edu.berkeley.ims.myt.WpaService',
          'grails.app.services.edu.berkeley.ims.myt.KerberosService',
          'grails.app.services.edu.berkeley.ims.myt.GoogleAppsService'
}
