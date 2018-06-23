package com.dearzs.commonlib.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

    /**
     * 32位，16位的话将函数的16改为8
     * 
     * @param string
     * @return
     */
    public static String hexdigest(String string) {
        String s = null;
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(string.getBytes());
            byte tmp[] = md.digest();
            char str[] = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            s = new String(str);
        } catch (Exception e) {
        }
        return s;
    }

    /**
     * 32位加密大写
     * 
     * @param string
     * @return
     */
    public static String hexdigestForUpper(String string) {
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(string.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)// 16位改为8
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return result.toUpperCase();
    }
}