package edu.berkeley.ims.myt

import grails.test.mixin.TestFor
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
        ["AFFILIATE-TYPE-ADVCON-ALUMNUS"]                                                | false
        ["EMPLOYEE-TYPE-ACADEMIC", "AFFILIATE-TYPE-ADVCON-ALUMNUS"]                      | true
        ["AFFILIATE-TYPE-ADVCON-ALUMNUS", "EMPLOYEE-TYPE-ACADEMIC"]                      | true
        ["EMPLOYEE-TYPE-ACADEMIC", "STUDENT-STATUS-EXPIRED", "AFFILIATE-STATUS-EXPIRED"] | true
        ['GUEST-TYPE-COLLABORATOR']                                                      | true
    }
}
