package pro.shushi.pamirs.core.common.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.core.common.EncryptHelper;
import pro.shushi.pamirs.core.common.enmu.EncryptTypeEnum;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

import static pro.shushi.pamirs.core.common.EncryptHelper.*;

/**
 * {@link EncryptHelper}测试
 *
 * @author Adamancy Zhang at 14:05 on 2022-03-31
 */
@DisplayName("EncryptHelper测试")
public class EncryptHelperTest {

    @Test
    public void test() throws Exception {
        //        String testData = JSONObject.toJSONString(MapHelper.newInstance()
//                .put("test", "abc")
//                .build());
//        String publicKey = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAl7lnKTkUnw9YeofJUT6XOYi8Wf6x29KWYyxfvXCbpScwdTDjJMENKT7BISThv0vxxH7ekbvmTq6F+q3j1WiPwJuzP+2jkLB/Ylc/kP07oTdKKf/xsCvLTquCSVywFvmzC3JIhtPk+OoF5vteucfqCYi0u9/uKQtJXhoIrxzLjPVkX554ggFHSsXgA8rh3buPB7PBTZZYbpbpOFTCo2GOb7ebIay05QiBjiqbv7TsxnflcqDVlQWg1n8dn/+qggp/3/OCZkoHKtUOuPV78v+2lEagxQ3UlANP7lsu6qtGp88gife3r5TXD9FG6NJ525nURfjvbJiJgq2OKmX2Lh7hDc9QJsdwfJmJKgSH3xh9QX73H4MbyMKl7pzoIcQJhBC5RQV0M+0itG0bSaAU6vQBjDYBk06ud1KXnLZAADRO8AfTBK6iXq9Hk4Wi6bRbE1XnELu7oEHK0ePxu2deen9Qzbr7sFp99tmpxS/YpGQdrMoGZloLjh8MoCmD6oEwRv8HEhvSsDz7T8DvxRauQNOzr5TnJyrbrakCTzLFQ5RjHGEJtsTpt7zM2EnEKlayZx8n3o+GdRbONOG8EDCNsiuuaATcZ74uCaO8Emf3UTs7BcntlFKmplJZzC8d4+yu3L8neuYrXmIajIM1divSrgu+eQORg1ggyyfAORQP21Tu2GMCAwEAAQ==";
//        print("encrypt", EncryptHelper.encryptByKey(EncryptHelper.getPublicKey(EncryptTypeEnum.RSA.getValue(), publicKey), testData));
        encryptTest();
//        dingTalkTest();
//        print("AES", EncryptHelper.getKey(EncryptHelper.getKey(EncryptTypeEnum.AES.getValue(), EncryptTypeEnum.AES.getInitializeSize())));
    }

    private static void encryptTest() throws NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, IOException {
        KeyPair keyPair = EncryptHelper.getRSAKeyPair();
        String publicKey = getKey(keyPair.getPublic());
        String privateKey = getKey(keyPair.getPrivate());
        print("RSA Public Key", publicKey);
        print("RSA Private Key", privateKey);
        String aesKey = getKey(getAESKey());
        print("AES", aesKey);
        //测试数据
        final String testData = UUIDUtil.getUUIDNumberString();
        print("Test Data", testData);
        //私钥加密，公钥解密
        String testEncryptData = encryptByKey(getPrivateKey(EncryptTypeEnum.RSA.getValue(), privateKey), testData);
        print("RSA encryptByPrivateKey", testEncryptData);
        print("RSA decryptByPublicKey", decryptByKey(getPublicKey(EncryptTypeEnum.RSA.getValue(), publicKey), testEncryptData));
        //公钥加密，私钥解密
        testEncryptData = encryptByKey(getPublicKey(EncryptTypeEnum.RSA.getValue(), publicKey), testData);
        print("RSA encryptByPublicKey", testEncryptData);
        print("RSA decryptByPrivateKey", decryptByKey(getPrivateKey(EncryptTypeEnum.RSA.getValue(), privateKey), testEncryptData));
        //AES加/解密
        Key key = getSecretKeySpec(EncryptTypeEnum.AES.getValue(), aesKey);
        testEncryptData = encryptByKey(key, testData);
        print("AES encryptByKey", testEncryptData);
        print("AES decryptByKey", decryptByKey(key, testEncryptData));
    }

    private static void dingTalkTest() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        String keyString = "Ick5wm0a15xRvhr+noHW9Ee1ar7ATPmQgZaSnBelM00" + "=";
        String encrypt = "X5vJNc95k1Bi6nURszl0yub/b68uAzMY9ARHMxjMcffzycTUClCJfFkurpdN06xbyOknUpRMqXpMi3Q+746Z5ffbA9Qg8aTA9TS/qus+IGz50u9plKfxJUu5bHdCP6g5";
        Key key = getSecretKeySpec(EncryptTypeEnum.AES.getValue(), keyString);
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(Arrays.copyOfRange(keyString.getBytes(StandardCharsets.UTF_8), 0, 16)));
        byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(encrypt.getBytes(StandardCharsets.UTF_8)));
        int pad = bytes[bytes.length - 1];
        if (pad < 1 || pad > 32)
            pad = 0;
        bytes = Arrays.copyOfRange(bytes, 0, bytes.length - pad);
        byte[] networkOrder = Arrays.copyOfRange(bytes, 16, 20);
        int plainTextLength = 0;
        for (int i = 0; i < 4; i++) {
            plainTextLength <<= 8;
            plainTextLength |= networkOrder[i] & 255;
        }
        if (!"dingf6145997434de8f535c2f4657eb6378f".equals(new String(Arrays.copyOfRange(bytes, 20 + plainTextLength, bytes.length), StandardCharsets.UTF_8)))
            throw new RuntimeException("已拦截非法的钉钉回调请求");
        print("dingTalk解密测试", new String(Arrays.copyOfRange(bytes, 20, 20 + plainTextLength), StandardCharsets.UTF_8));
    }

    private static void print(String name, String data) {
        System.out.println(name + ": " + data);
        System.out.println(name + ": " + data.length());
    }
}
