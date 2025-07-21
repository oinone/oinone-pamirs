package pro.shushi.pamirs.sso.api.utils;

import pro.shushi.pamirs.core.common.RSAEncryptUtils;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;
import pro.shushi.pamirs.sso.api.constant.SsoConfigurationConstant;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EncryptionHandler {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 256;
    private static final String SALT = "qwdsasfdssdfbvcgfdgf";
    public final static String PUBLIC_KEY = "rsa_publicKey";

    public final static String PRIVATE_KEY = "rsa_privateKey";

    public static String encrypt(String appKey, String data) {
        try {
            SecretKey secretKey = generateSecretKey(appKey);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(new byte[16]));
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.getUrlEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String appKey, String encryptedData) throws BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException {
        SecretKey secretKey = generateSecretKey(appKey);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(new byte[16]));
        byte[] decryptedBytes = cipher.doFinal(Base64.getUrlDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }

    private static SecretKey generateSecretKey(String appKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
        KeySpec spec = new PBEKeySpec(appKey.toCharArray(), SALT.getBytes(), ITERATION_COUNT, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    /**
     * 计算code 是否超时
     *
     * @param fromDate      传入时间戳
     * @param codeExpiresIn 过期时间
     * @return
     */
    public static boolean isCode(String fromDate, Long codeExpiresIn) {
        try {
            Date date = new Date(Long.parseLong(fromDate));
            Date toDate = new Date();
            long from = date.getTime();
            long to = toDate.getTime();
            int second = (int) ((to - from) / 1000);
            return second <= codeExpiresIn;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 生成随机的ClientId
     *
     * @return
     */
    public static String generateClientId() {
        return SsoConfigurationConstant.PAMIRS_SSO_CLIENT_ID_PREFIX + UUIDUtil.getUUIDNumberString();
    }

    /**
     * 生成clientSecret
     *
     * @param clientId 加密字符串
     * @return
     * @throws Exception
     */
    public static Map<String, String> generateClientSecret(String clientId) throws Exception {
        Map<String, String> keyMap = RSAEncryptUtils.genKeyPair();
        HashMap<String, String> result = new HashMap<>();
        String messageEn = RSAEncryptUtils.encrypt(clientId, keyMap.get(PUBLIC_KEY));
        result.put(SsoConfigurationConstant.PAMIRS_SSO_INTERNAL_CLIENT_PRIVATE, keyMap.get(PRIVATE_KEY));
        result.put(SsoConfigurationConstant.PAMIRS_SSO_INTERNAL_CLIENT_PUBLIC, keyMap.get(PUBLIC_KEY));
        result.put(SsoConfigurationConstant.PAMIRS_SSO_CLIENT_PUBLIC, messageEn);
        return result;
    }

    /**
     * 解析ClientSecret
     *
     * @param appKey        私钥
     * @param encryptedData 解析值
     * @return
     * @throws Exception
     */
    public static String decryptSecret(String appKey, String encryptedData) throws Exception {
        return RSAEncryptUtils.decrypt(encryptedData, appKey);
    }

    public static String decryptBase64(String base64EncodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64EncodedString);
        String decodedString = new String(decodedBytes);
        return decodedString;
    }

    public static String encryptBase64(String originalString) {
        String base64EncodedString = Base64.getEncoder().encodeToString(originalString.getBytes());
        return base64EncodedString;
    }
}