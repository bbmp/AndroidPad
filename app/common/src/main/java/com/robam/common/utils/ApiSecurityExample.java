package com.robam.common.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class ApiSecurityExample {
    public static void main(String[] args) {
        System.out.println(hmacSha1("mykey", "helloworld"));
        System.out.println(hmacSha256("mykey", "helloworld"));
    }

    public static String hmacSha1(String KEY, String VALUE) {
        return hmacSha(KEY, VALUE, "HmacSHA1");
    }

    public static String hmacSha256(String KEY, String VALUE) {
        return hmacSha(KEY, VALUE, "HmacSHA256");
    }

    private static String hmacSha(String KEY, String VALUE, String SHA_TYPE) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(KEY.getBytes("UTF-8"), SHA_TYPE);
            Mac mac = Mac.getInstance(SHA_TYPE);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(VALUE.getBytes("UTF-8"));

            byte[] hexArray = {
                    (byte) '0', (byte) '1', (byte) '2', (byte) '3',
                    (byte) '4', (byte) '5', (byte) '6', (byte) '7',
                    (byte) '8', (byte) '9', (byte) 'a', (byte) 'b',
                    (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f'
            };
            byte[] hexChars = new byte[rawHmac.length * 2];
            for (int j = 0; j < rawHmac.length; j++) {
                int v = rawHmac[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }
            return new String(hexChars);
//            return BaseEncoding.base64().encode(hexArray);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
