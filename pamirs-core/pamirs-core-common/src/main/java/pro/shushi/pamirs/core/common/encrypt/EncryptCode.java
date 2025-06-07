package pro.shushi.pamirs.core.common.encrypt;

/**
 * @author Adamancy Zhang on 2021-02-19 21:15
 */
public interface EncryptCode {

    /**
     * encode data
     *
     * @param data data bytes
     * @return result bytes
     */
    byte[] encode(byte[] data);

    /**
     * decode data
     *
     * @param data   data bytes
     * @param length decode length, it must be a multiple of 2.
     * @return result bytes
     */
    byte[] decode(byte[] data, int length);
}
