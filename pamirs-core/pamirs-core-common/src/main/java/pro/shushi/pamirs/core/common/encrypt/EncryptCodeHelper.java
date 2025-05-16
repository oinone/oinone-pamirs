package pro.shushi.pamirs.core.common.encrypt;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Adamancy Zhang on 2021-02-19 21:20
 */
public class EncryptCodeHelper {

    private static final Base16EncryptCode BASE16_ENCRYPT_CODE_LOWER = new Base16EncryptCode(false);

    private static final Base16EncryptCode BASE16_ENCRYPT_CODE_UPPER = new Base16EncryptCode(true);

    public static byte[] toBytesDirect(String data) {
        char[] src = data.toCharArray();
        byte[] dest = new byte[src.length];
        for (int i = 0; i < dest.length; ++i) {
            char c = src[i];
            if (c > 127) {
                throw new IllegalArgumentException("Invalid character found at position " + i + " for " + data);
            }
            dest[i] = (byte) c;
        }
        return dest;
    }

    public static String toStringDirect(byte[] bytes) {
        char[] dest = new char[bytes.length];
        int i = 0;
        for (byte b : bytes) {
            dest[i++] = (char) b;
        }
        return new String(dest);
    }

    public static byte[] toHexByBase16(boolean isUpper, byte[] bytes) {
        if (bytes != null && bytes.length != 0) {
            if (isUpper) {
                bytes = BASE16_ENCRYPT_CODE_UPPER.encode(bytes);
            } else {
                bytes = BASE16_ENCRYPT_CODE_LOWER.encode(bytes);
            }
        }
        return bytes;
    }

    public static byte[] fromHexByBase16(boolean isUpper, byte[] bytes) {
        if (bytes != null && bytes.length != 0) {
            if (isUpper) {
                bytes = BASE16_ENCRYPT_CODE_UPPER.decode(bytes, bytes.length);
            } else {
                bytes = BASE16_ENCRYPT_CODE_LOWER.decode(bytes, bytes.length);
            }
        }
        return bytes;
    }

    public static byte[] mac(String algorithm, byte[] key, String data) throws NoSuchAlgorithmException, InvalidKeyException {
        return mac(algorithm, key, data.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] mac(String algorithm, byte[] key, byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data);
    }

    public static byte[] md(String algorithm, String data) throws NoSuchAlgorithmException {
        return md(algorithm, data.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] md(String algorithm, byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.update(data);
        return md.digest();
    }
}
