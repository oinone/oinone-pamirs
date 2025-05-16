package pro.shushi.pamirs.user.api.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author xzf 2022/06/10 14:04
 **/
@Slf4j
public class AES256Utils {


    /**
     * 偏移量
     */
    private static String iv                = "1234567890aabbcc";

    /**
     * 加密串
     */
    private static String key               = "1234567890abcdefghijklmnopqrstuv";
    /**
     * 加密算法
     */
    private static String Algorithm         = "AES";
    /**
     * 算法/模式/补码方式
     */
    private static String AlgorithmProvider = "AES/CBC/PKCS5Padding";

    public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    /**
     * 加密
     *
     * @param src 加密内容
     * @return
     */
    public static String encrypt(String src) throws Exception {
        SecretKey       secretKey       = new SecretKeySpec(key.getBytes(), Algorithm);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
        Cipher          cipher          = Cipher.getInstance(AlgorithmProvider);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        byte[] cipherBytes = cipher.doFinal(src.getBytes(StandardCharsets.UTF_8));
        return byteToHexString(cipherBytes);
    }

    /**
     * 解密
     *
     * @param content 加密内容
     * @return
     */
    public static String decrypt(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        try {
            SecretKey       secretKey       = new SecretKeySpec(key.getBytes(), Algorithm);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            Cipher          cipher          = Cipher.getInstance(AlgorithmProvider);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            byte[] hexBytes   = hexStringToBytes(content);
            byte[] plainBytes = cipher.doFinal(hexBytes);
            String s          = new String(plainBytes, StandardCharsets.UTF_8);
            return s;
        } catch (Exception e) {
            log.warn("解密错误,错误信息:" + e.getMessage());
            return content;
        }
    }

    /**
     * 将byte数组转换为16进制字符串
     *
     * @param src
     * @return
     */
    private static String byteToHexString(byte[] src) {
        return Hex.encodeHexString(src);
    }

    /**
     * 将16进制字符串转换为byte数组
     *
     * @param hexString
     * @return
     */
    private static byte[] hexStringToBytes(String hexString) throws DecoderException {
        return Hex.decodeHex(hexString);
    }

}
