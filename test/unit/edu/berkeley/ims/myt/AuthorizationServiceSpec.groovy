package edu.berkeley.ims.myt

import com.unboundid.ldap.sdk.SearchResultEntry
import grails.test.mixin.TestFor
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Affiliations for CalNet can be found here:
 *
 *   https://wikihub.berkeley.edu/display/calnet/People+OU+Affiliations
 *
 */

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(AuthorizationService)
class AuthorizationServiceSpec extends Specification {
    @Shared
    SearchResultEntry PERSON = GroovyMock(SearchResultEntry)
    @Shared
    SearchResultEntry GUEST = GroovyMock(SearchResultEntry)
    @Shared
    SearchResultEntry ADVCON = GroovyMock(SearchResultEntry)

    /**
     * Test the various combinations of affiliations to make sure the
     * isAuthoirzedWpa() logic is correct. Basically, any affiliate except for
     * expired and ADVCON are allowed to use the WPA service.
     */
    @Unroll
    def "Test that user is authorized for various combinations of affiliations"() {
        when:
        def result = service.isAuthorizedWpa(affiliations)

        then:
        result == expectedResult

        where:
        affiliations                                                                     | expectedResult
        ["EMPLOYEE-TYPE-STAFF", "STUDENT-STATUS-EXPIRED"]                                | true
        ["EMPLOYEE-TYPE-STAFF", "AFFILIATE-STATUS-EXPIRED"]                              | true
        ["EMPLOYEE-STATUS-EXPIRED", "AFFILIATE-STATUS-EXPIRED"]                          | false
        ["EMPLOYEE-TYPE-STAFF", "STUDENT-TYPE-NOT REGISTERED"]                           | true
        ["STUDENT-TYPE-NOT REGISTERED", "AFFILIATE-TYPE-TEMPORARY AGENCY STAFF"]         | true
        ["AFFILIATE-TYPE-ADVCON-ALUMNUS"]                                                | true
        ["EMPLOYEE-TYPE-ACADEMIC", "AFFILIATE-TYPE-ADVCON-ALUMNUS"]                      | true
        ["AFFILIATE-TYPE-ADVCON-ALUMNUS", "EMPLOYEE-TYPE-ACADEMIC"]                      | true
        ["EMPLOYEE-TYPE-ACADEMIC", "STUDENT-STATUS-EXPIRED", "AFFILIATE-STATUS-EXPIRED"] | true
        ['GUEST-TYPE-COLLABORATOR']                                                      | true
    }

    @Unroll
    def "Test that authorize user works as expected"() {
        given:
        service.ldapService = GroovyMock(LdapService)
        and:
        service.personUsernameAttr = 'uid'
        service.peopleDnString = 'ou=people,dc=berkeley,dc=edu;ou=guests,dc=berkeley,dc=edu;ou=advcon people,dc=berkeley,dc=edu'

        when:
        def result = service.authorizeUser('12345')

        then:
        peopleCalls * service.ldapService.find('uid', '12345', 'ou=people,dc=berkeley,dc=edu') >> person
        guestCalls * service.ldapService.find('uid', '12345', 'ou=guests,dc=berkeley,dc=edu') >> guest
        advconCalls * service.ldapService.find('uid', '12345', 'ou=advcon people,dc=berkeley,dc=edu') >> advcon


        and:
        result == expectedResult

        where:
        person | peopleCalls | guest | guestCalls | advcon | advconCalls | expectedResult
        PERSON | 1           | GUEST | 0          | ADVCON | 0           | PERSON
        null   | 1           | GUEST | 1          | ADVCON | 0           | GUEST
        null   | 1           | null  | 1          | ADVCON | 1           | ADVCON
        null   | 1           | null  | 1          | null   | 1           | null
    }

}
