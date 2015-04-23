Manage My Keys
===========

Manage My Keys (MMK) is an application which allows users to manage an application-specific key (which is a password/passphrase) for a variety of services. At the present time, MMK is integrated with the WPA Wireless (AirBears2) system, and Google Apps for the berkeley.edu domain (bConnected).

## Requirements



## Configuration
Currently, the config for the MMK app is located under `/opt/idc/conf/ManageMyTokensConfig.groovy`. This file is readable only by the Tomcat user. To configure the app for the given environment, copy the file:

<pre>
grails-app/conf/ManageMyTokensConfig.groovy.dist
</pre>

to:

<pre>
/opt/idc/conf/ManageMyTokensConfig.groovy
</pre>

## Changes in new Google Admin API version:

### Configuration for Google Api:
<pre>
mmk {
    googleAdminAPI {
        apiEmailAddress = "xxx@developer.gserviceaccount.com" // API email address from Google Developer Console
        serviceAccountUser = "tokenapp@berkeley.edu"          // Service  Account User (ex: tokenapp@berkeley.edu)
        apiApplicationName = "CalNet-Token-App"               // API application name
        keystore {
            path = 'file:/somefile.p12'                       // Path to keystore (or classpath: for file on class path)
            alias = 'privatekey'                              // Alias of private key in keystore (likely 'privatekey')
            secret = '***'                                    // Keystore secret
        }
    }
}

</pre>

### Database configuration changes

Remove datasource for CalMail (not needed any longer)