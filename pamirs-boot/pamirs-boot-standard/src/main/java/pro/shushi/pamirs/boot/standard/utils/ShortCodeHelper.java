package pro.shushi.pamirs.boot.standard.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 短编码
 *
 * @author Adamancy Zhang at 12:47 on 2024-10-11
 */
public class ShortCodeHelper {

    private static final char[] ALL_CHARS = new char[]{
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    /**
     * 短编码算法
     *
     * @param input 输入内容
     * @return 短编码
     * @author ranjingnian at 19:11 on 2024-01-17
     */
    public static String encode(String input) {
        String hex = md5(input);
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            long lHexLong = 0x3FFFFFFF & Long.parseLong(hex.substring(i * 8, i * 8 + 8), 16);
            StringBuilder outChars = new StringBuilder();
            for (int j = 0; j < 6; j++) {
                long index = 0x0000003D & lHexLong;
                outChars.append(ALL_CHARS[(int) index]);
                lHexLong = lHexLong >> 5;
            }
            res.append(outChars);
        }
        return res.toString();
    }

    /**
     * MD5编码(hex)
     *
     * @param input 输入内容
     * @return MD5编码
     * @author ranjingnian at 19:11 on 2024-01-17
     */
    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }
}
