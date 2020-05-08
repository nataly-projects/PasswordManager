package com.example.passmanager.util;

import android.util.Base64;

import java.security.MessageDigest;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Util {

    public static final int LAYOUT_TITLE = 0;
    public static final int LAYOUT_ITEM = 1;


    //for the registration password
    public static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +       //start string
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-zA-Z])" +         //any letter (lowercase or uppercase)
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 6 character
                    "$");           //end of string


    private static SecretKeySpec generateKey() throws Exception
    {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = "password".getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secret = new SecretKeySpec(key, "AES");
        return secret;
    }

    public static String encryptMsg(String data) throws Exception{
        SecretKeySpec key = generateKey();
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] enc = c.doFinal(data.getBytes());
        String encryptVal = Base64.encodeToString(enc, Base64.DEFAULT);
        return encryptVal;
    }

    public static String decryptMsg(String data) throws Exception{
        SecretKeySpec key = generateKey();
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decode = Base64.decode(data, Base64.DEFAULT);
        byte[] decodeVal = c.doFinal(decode);
        String decryptVal = new String(decodeVal);
        return decryptVal;
    }
}
