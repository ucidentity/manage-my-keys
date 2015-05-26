package edu.berkeley.calnet.mmk
import java.security.SecureRandom

class TokenUtil {

    public static String generateToken(int length) {
        String charset = '23456789BCDFGHJKLMNPQRSTVWXYZbcdfghjklmnpqrstvwxyz&!@#()^$+%*{},.'
        SecureRandom sr = new SecureRandom()
        def result = (1..length).collect {
            charset[sr.nextInt(charset.size())]
        }.join('')
        return result
    }
    
    public static boolean verifyCurrentToken(currentToken, token) {
        return currentToken == token
    }
}
