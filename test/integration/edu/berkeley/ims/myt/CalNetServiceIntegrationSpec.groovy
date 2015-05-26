package edu.berkeley.ims.myt
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import com.unboundid.ldap.sdk.Entry
import grails.plugin.spock.IntegrationSpec
import spock.lang.Shared
import spock.lang.Unroll

import java.util.concurrent.Executors
/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
class CalNetServiceIntegrationSpec extends IntegrationSpec {

    def calNetService

    Map requestParameters = [:] // Request parameter map

    String serverResponse // What should the build in server respond

    @Shared
    HttpServer httpServer

    def setupSpec() {
    }

    def setup() {
        createKerbserviceMock()
    }

    def cleanup() {
        httpServer.stop(0);
    }

    def "test authentication with good user data"() {
        given:
        def person = GroovyMock(Entry)

        and:
        serverResponse = '0\nAuthentication successful'

        when:
        def result = calNetService.testAuthenticationFor("good", person)

        then:
        1 * person.getAttributeValue('berkeleyEduKerberosPrincipalString') >> "calnetId"

        requestParameters.query == 'authN'
        def params = requestParameters.body.split('&').collectEntries { it.split('=') as List }
        params.appid == 'krbappid' // See Config.groovy
        params.authkey == 'krbpasswd' // See Config.groovy
        params.id == 'calnetId'
        params.passphrase == 'good'


        result == true
    }

    def "test authentication with bad user data"() {
        given:
        def person = GroovyMock(Entry)

        and:
        serverResponse = '31\nCurrent passphrase incorrect'

        when:
        def result = calNetService.testAuthenticationFor("bad", person)

        then:
        1 * person.getAttributeValue('berkeleyEduKerberosPrincipalString') >> "calnetId"

        requestParameters.query == 'authN'
        def params = requestParameters.body.split('&').collectEntries { it.split('=') as List }
        params.appid == 'krbappid' // See Config.groovy
        params.authkey == 'krbpasswd' // See Config.groovy
        params.id == 'calnetId'
        params.passphrase == 'bad'


        result == false
    }

    @Unroll("Validating if '#passphrase' is #goodBad for the STU-NEW TEST with calnetId: test-212383 ")
    void 'test a set of passphrases'() {
        given:
        def person = GroovyMock(Entry)

        when:
        def result = calNetService.validatePassphraseComplexityFor(passphrase, person)

        then:
        1 * person.getAttributeValue('displayName') >> 'STU-NEW TEST'
        1 * person.getAttributeValue('berkeleyEduKerberosPrincipalString') >> "test-212383"

        and:
        result == good

        where:
        passphrase                                          | good
        'Hi there!'                                         | true
        'aaabbbCC2'                                         | true
        'Password1'                                         | true
        'a      B1'                                         | true
        'Hi'                                                | false
        'Hi1!'                                              | false
        'What___?'                                          | false
        'what about this'                                   | false
        'abcefghijklmn1'                                    | false
        'A      B1'                                         | false
        'a     B1'                                          | false
        'Hi there test-21238!'                              | false
        'This is still not good enough one: test-212383!'   | false
        'This is still not good enough one: TEST-212383!'   | false
        "This is looking good! Oh, wait".padRight(256, '.') | false

        goodBad = good ? 'good' : 'bad'
    }

    void createKerbserviceMock() {
        //configuring a Java 6 HttpServer
        InetSocketAddress addr = new InetSocketAddress(9999)
        httpServer = HttpServer.create(addr, 0)
        httpServer.with {
            createContext('/krbservice', new HttpHandler() {
                @Override
                void handle(HttpExchange httpExchange) throws IOException {
                    if (httpExchange.requestMethod != 'POST') {
                        httpExchange.sendResponseHeaders(400, 0)
                        return
                    }
                    httpExchange.sendResponseHeaders(200, 0)
                    requestParameters.query = httpExchange.requestURI.query
                    requestParameters.body = httpExchange.requestBody.text

                    httpExchange.responseBody.withWriter { writer ->
                        writer.write(serverResponse)
                    }
                    httpExchange.responseBody.close()
                }
            })
            setExecutor(Executors.newCachedThreadPool())
            start()
        }

    }
}
