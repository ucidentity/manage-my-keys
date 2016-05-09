package edu.berkeley.ims.myt

import spock.lang.Specification
import spock.lang.Unroll

class PassphraseComplexityValidatorSpec extends Specification {
    @Unroll
    def "test if the passphrase contains parts of the calnetId"() {
        when:
        def result = PassphraseComplexityValidator.isUsernameInPassphrase(calnetId, passphrase)

        then:
        result == expectedResult

        where:
        calnetId    | passphrase           | expectedResult
        'aa'        | 'aabbccdd'           | false
        'kryf'      | 'plyf'               | false
        'test-1234' | 'abc-test-1234-plyf' | true
        'abcd'      | 'test-abcdef'        | true
        'def'       | 'test-abcdef'        | true
    }

    @Unroll("Test if '#passphrase' contains any of the parts from '#displayName'")
    def "test if the passphrase is in any parts of the displayname"() {
        when:
        def result = PassphraseComplexityValidator.isDisplayNameInPassphrase(displayName, passphrase)

        then:
        result == expectedResult

        where:
        displayName             | passphrase           | expectedResult
        'Some Name'             | 'impossibleToGuess'  | false
        'Some Other Name'       | 'otherShouldNotWork' | true
        'Some (nerd) Name'      | 'impossibleToGuess'  | false
        'Some (nerd fail) Name' | 'impossibleToGuess'  | false         //  Fails with regex
        'Some (nerd fail) Name' | 'failToGuess'        | true
        'Søme Name'             | 'sømeShouldNotWork'  | true
        'Some (nerd fail) Name' | 'nerdToGuess'        | true         //  Fails with regex
    }

    @Unroll("Test complexity of #passphrase and expect it to #pass because it #matchedRule")
    def "test the complexity of the password meets the criteria"() {
        when:
        def result = PassphraseComplexityValidator.isComplexPassphrase(passphrase)

        then:
        result == expectedResult

        where:
        passphrase  | expectedResult | matchedRule
        null        | false          | 'fails because passphrase is null'
        "aB2"       | false          | 'fails length requirement'
        "aB2" * 100 | false          | 'fails length requirement'
        "abcDEF123" | true           | 'matches LOWER_UPPER_DIGIT'
        "abcdef123" | false          | 'fails LOWER_UPPER_DIGIT and LOWER_DIGIT_PUNCT'
        "abcDEFGHI" | false          | 'fails LOWER_UPPER_DIGIT and LOWER_UPPER_PUNCT'
        "abc123!'#" | true           | 'matches LOWER_UPPER_PUNCT'
        "abc!#(%&/" | false          | 'fails LOWER_DIGIT_PUNCT'
        "abc!#(%&/" | false          | 'fails LOWER_DIGIT_PUNCT'
        "ABC123=)(" | true           | 'matches UPPER_DIGIT_PUNCT'
        "ABC!'#%&/" | false          | 'fails UPPER_DIGIT_PUNCT'

        pass = expectedResult ? 'not pass' : 'pass'
    }

}
