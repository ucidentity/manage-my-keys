package edu.berkeley.calnet.mmk

import java.security.MessageDigest

class Password {
    public static final PASSWORD_HASH = 'SHA-1'

    public static String hash(String password) {
        MessageDigest messageDigest = MessageDigest.getInstance(PASSWORD_HASH)
        messageDigest.update(password.getBytes())
        return messageDigest.digest().encodeHex().toString()
    }
}
