package edu.berkeley.ims.myt

/**
 * Regex test for CalNet passphrase rules 1, 2, and the ID part of 3.
 *
 * Rules and methodology are below:
 *
 * 1) A minimum length of 9 characters (maximum 255). It may also include spaces
 *    (which is why we call it a passphrase).
 * 2) It must contain characters from at least three of the following four
 *    character groups:
 *    a) English uppercase (A through Z)
 *    b) English lowercase (a through z)
 *    c) numeric digits (0 through 9)
 *    d) non-alphanumeric characters (such as !, $, #, or %)
 * 3) Without regard to case, the passphrase may not contain your first name,
 *    middle name, last name, or your CalNet ID itself if any of these are three
 *    characters or longer.
 * 4) Any time you change your passphrase, the new one may not be the same as
 *    the current or previous passphrase.
 *
 * Again, we are only testing 1 and 2 with this regex.
 *
 * Methodology:
 *  1) Test to make sure the passphrase does not contain the ID
 *  2) Length: .{9,255}*  3) Permutations of a, b, c, and d
 *      i)      a, b, c     (?=.*[a-z])(?=.*[A-Z])(?=.*\d)
 *      ii)     a, b, d     (?=.*[a-z])(?=.*[A-Z])(?=.*\p{Punct})
 *      iii)    b, c, d     (?=.*[A-Z])(?=.*\d)(?=.*\p{Punct})
 *      iv)     a, c, d     (?=.*[a-z])(?=.*\d)(?=.*\p{Punct})
 *
 **/
class PassphraseComplexityValidator {

    private static LOWER_UPPER_DIGIT = /(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{9,255}/
    private static LOWER_UPPER_PUNCT = /(?=.*[a-z])(?=.*[A-Z])(?=.*\p{Punct}).{9,255}/
    private static UPPER_DIGIT_PUNCT = /(?=.*[A-Z])(?=.*\d)(?=.*\p{Punct}).{9,255}/
    private static LOWER_DIGIT_PUNCT = /(?=.*[a-z])(?=.*\d)(?=.*\p{Punct}).{9,255}/

    private PassphraseComplexityValidator() {}

    /**
     * Make sure the calnetId is not contained in the passphrase
     *
     * @param calnetId calnetId
     * @param passphrase the passphrase to validate
     * @return true if calnetId is contained in the passphrase otherwise false
     **/
    static boolean isUsernameInPassphrase(String calnetId, String passphrase) {
        return calnetId?.size() > 2 && passphrase?.toLowerCase()?.contains(calnetId?.toLowerCase())
    }

    /**
     * Make sure that no part of the display name is contained in the passphrase
     *
     * @param displayName the display name
     * @passphrase the passphrase to validate
     * @return true if any part of the display name is contained in the passphrase, otherwise false
     **/
    static boolean isDisplayNameInPassphrase(String displayName, String passphrase) {
        displayName?.split(/[\s\p{Punct}]/)?.any { String name ->
            name?.size() > 2 && passphrase?.toLowerCase()?.contains(name?.toLowerCase())
        }
    }

    /**
     * Validates to check if the passphrase given is complex enough
     * @param the passphrase passphrase
     * @return true, if at least one of the rules above is covered.
     */
    static boolean isComplexPassphrase(String passphrase) {
        // The permutations
        // If the passphrase exists, check each permutation
        return passphrase ==~ LOWER_UPPER_DIGIT || passphrase ==~ LOWER_UPPER_PUNCT ||
                passphrase ==~ UPPER_DIGIT_PUNCT || passphrase ==~ LOWER_DIGIT_PUNCT

    }
}
