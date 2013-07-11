package com.honey.util;

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 13-6-26
 * Time: 上午10:48
 * To change this template use File | Settings | File Templates.
 */
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.impl.auth.UnsupportedDigestAlgorithmException;

import android.util.Log;

/**
 * @version 1.0
 */
public final class MD5 {
    private static final String LOG_TAG = "MD5";
    private static final String ALGORITHM = "MD5";

    private static char sHexDigits[] = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };
    private static MessageDigest sDigest;

    static {
        try {
            sDigest = MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            Log.e(LOG_TAG, "Get MD5 Digest failed.");
            throw new UnsupportedDigestAlgorithmException(ALGORITHM, e);
        }
    }

    private MD5() {
    }


    //TODO
    final public static String encode(String source) {
        byte[] btyes = source.getBytes();
        byte[] encodedBytes = sDigest.digest(btyes);

        return "";
//        return Utility.hexString(encodedBytes);
    }

}