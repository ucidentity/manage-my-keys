package edu.berkeley.ims.myt
import edu.berkeley.calnet.mmk.TokenUtil
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
class TokenServiceTests extends Specification{

    @Unroll
    void testTokenLength() {
        when:
        def t1 = TokenUtil.generateToken(len)
        then:
        len == t1.size()

        where:
        len << [5,10,15,20]
    }
}
