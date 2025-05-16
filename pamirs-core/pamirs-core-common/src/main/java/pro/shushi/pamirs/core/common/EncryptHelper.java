package pro.shushi.pamirs.core.common;

import pro.shushi.pamirs.boot.standard.utils.ShortCodeHelper;
import pro.shushi.pamirs.core.common.enmu.EncryptTypeEnum;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 加解密帮助类
 */
@Slf4j
public class EncryptHelper {

    /**
     * RSA最大加密明文长度
     */
    private static final int MAX_ENCRYPT_BLOCK = 501;

    /**
     * RSA最大解密密文长度
     */
    private static final int MAX_DECRYPT_BLOCK = 512;

    private static final char[] ALL_CHARS = new char[]{
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    private EncryptHelper() {
        //reject create object
    }

    public static KeyPair getRSAKeyPair() throws NoSuchAlgorithmException {
        return getKeyPair(EncryptTypeEnum.RSA.getValue(), EncryptTypeEnum.RSA.getInitializeSize());
    }

    public static KeyPair getRSAKeyPair(int size) throws NoSuchAlgorithmException {
        return getKeyPair(EncryptTypeEnum.RSA.getValue(), size);
    }

    public static Key getAESKey() throws NoSuchAlgorithmException {
        return getKey(EncryptTypeEnum.AES.getValue(), EncryptTypeEnum.AES.getInitializeSize());
    }

    public static Key getAESKey(int size) throws NoSuchAlgorithmException {
        return getKey(EncryptTypeEnum.AES.getValue(), size);
    }

    /**
     * 获取指定类型的密钥对
     *
     * @param algorithm 指定密钥类型
     * @param size      位数
     * @return 返回密钥对
     * @throws NoSuchAlgorithmException 没有找到该指定类型
     */
    public static KeyPair getKeyPair(String algorithm, int size) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
        keyPairGenerator.initialize(size, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 获取指定类型的密钥
     *
     * @return 返回密钥
     * @throws NoSuchAlgorithmException 没有找到该指定类型
     */
    public static Key getKey(String algorithm, int size) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
        keyGenerator.init(size, new SecureRandom());
        return keyGenerator.generateKey();
    }

    /**
     * 获取密钥Base64编码字符串
     *
     * @param key 密钥
     * @return 返回字符串
     */
    public static String getKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * 获取指定类型的密钥
     *
     * @param algorithm 指定密钥类型
     * @param key       密钥Base64编码字符串
     * @return 返回密钥
     */
    public static Key getSecretKeySpec(String algorithm, String key) {
        return new SecretKeySpec(Base64.getDecoder().decode(key.getBytes(StandardCharsets.UTF_8)), algorithm);
    }

    /**
     * 获取指定类型的密钥
     *
     * @param algorithm 指定密钥类型
     * @param key       密钥Base64编码字符串
     * @return 返回密钥
     */
    public static PublicKey getPublicKey(String algorithm, String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance(algorithm).generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(key.getBytes(StandardCharsets.UTF_8))));
    }

    /**
     * 获取指定类型的密钥
     *
     * @param algorithm 指定密钥类型
     * @param key       密钥Base64编码字符串
     * @return 返回密钥
     */
    public static PrivateKey getPrivateKey(String algorithm, String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance(algorithm).generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key.getBytes(StandardCharsets.UTF_8))));
    }

    /**
     * 使用密钥对数据进行加密
     *
     * @param key  密钥
     * @param data 需要加密的字符串
     * @return 返回加密后的字符串
     * @throws NoSuchPaddingException    没有找到缓冲区
     * @throws NoSuchAlgorithmException  没有找到该指定类型
     * @throws InvalidKeyException       无效的密钥
     * @throws BadPaddingException       坏的缓冲区
     * @throws IllegalBlockSizeException 非法的块大小
     */
    public static String encryptByKey(Key key, String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        Cipher cipher = Cipher.getInstance(key.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] bts = data.getBytes(StandardCharsets.UTF_8);
        int dataLength = bts.length;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            int offset = 0;
            do {
                int block = Math.min(dataLength - offset, MAX_ENCRYPT_BLOCK);
                byte[] bytes = cipher.doFinal(bts, offset, block);
                outputStream.write(bytes);
                offset += block;
            } while (offset < dataLength);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        }
    }

    /**
     * 使用密钥对数据进行解密
     *
     * @param key  密钥
     * @param data 需要解密的字符串
     * @return 返回解密后的字符串
     * @throws NoSuchPaddingException    没有找到缓冲区
     * @throws NoSuchAlgorithmException  没有找到该指定类型
     * @throws InvalidKeyException       无效的密钥
     * @throws BadPaddingException       坏的缓冲区
     * @throws IllegalBlockSizeException 非法的块大小
     */
    public static String decryptByKey(Key key, String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        Cipher cipher = Cipher.getInstance(key.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] bts = Base64.getDecoder().decode(data.getBytes(StandardCharsets.UTF_8));
        int dataLength = bts.length;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            int offset = 0;
            do {
                int block = Math.min(dataLength - offset, MAX_DECRYPT_BLOCK);
                byte[] bytes = cipher.doFinal(bts, offset, block);
                outputStream.write(bytes);
                offset += block;
            } while (offset < dataLength);
            return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    public static String shortCode(String input) {
        return ShortCodeHelper.encode(input);
    }

    public static String md5(String input) {
        return ShortCodeHelper.md5(input);
    }

    /**
     * 位数组转十六进制字符串
     *
     * @param bytes 位数组
     * @return 十六进制字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
