package pro.shushi.pamirs.core.common.encrypt;

/**
 * @author Adamancy Zhang on 2021-02-19 21:18
 */
public class Base16EncryptCode implements EncryptCode {

    private static final String UPPER_CASE_DIRECT_STRING = "0123456789ABCDEF";

    private static final String LOWER_CASE_DIRECT_STRING = "0123456789abcdef";

    private static final int DECODE_MULTIPLE_LENGTH = 2;

    private static final int MASK_4BITS = 15;

    private final byte[] alphabets;

    public Base16EncryptCode(boolean isUpperCase) {
        this.alphabets = isUpperCase ? EncryptCodeHelper.toBytesDirect(UPPER_CASE_DIRECT_STRING) : EncryptCodeHelper.toBytesDirect(LOWER_CASE_DIRECT_STRING);
    }

    @Override
    public byte[] encode(byte[] data) {
        byte[] dest = new byte[data.length * DECODE_MULTIPLE_LENGTH];
        int pos = 0;
        byte p;
        for (byte b : data) {
            dest[pos++] = this.alphabets[(p = b) >>> 4 & MASK_4BITS];
            dest[pos++] = this.alphabets[p & MASK_4BITS];
        }
        return dest;
    }

    @Override
    public byte[] decode(byte[] data, int length) {
        if (length % DECODE_MULTIPLE_LENGTH != 0) {
            throw new IllegalArgumentException("Input is expected to be encoded in multiple of 2 bytes but found: " + length);
        } else {
            byte[] dest = new byte[length / DECODE_MULTIPLE_LENGTH];
            int i = 0;
            for (int j = 0; j < dest.length; j++) {
                dest[j] = (byte) (this.pos(data[i++]) << 4 | this.pos(data[i++]));
            }
            return dest;
        }
    }

    protected int pos(byte in) {
        int pos = LazyHolder.DECODED[in];
        if (pos > -1) {
            return pos;
        } else {
            throw new IllegalArgumentException("Invalid base 16 character: '" + (char) in + "'");
        }
    }

    private static class LazyHolder {

        private static final int OFFSET_OF_a = 87;
        private static final int OFFSET_OF_A = 55;
        private static final int MAX_DECODE_TABLE_SIZE = 103;

        private static final byte[] DECODED = decodeTable();

        private LazyHolder() {
            //reject create object
        }

        private static byte[] decodeTable() {
            byte[] dest = new byte[MAX_DECODE_TABLE_SIZE];
            for (int i = 0; i < MAX_DECODE_TABLE_SIZE; i++) {
                if (i >= 48 && i <= 57) {
                    dest[i] = (byte) (i - 48);
                } else if (i >= 65 && i <= 70) {
                    dest[i] = (byte) (i - OFFSET_OF_A);
                } else if (i >= 97) {
                    dest[i] = (byte) (i - OFFSET_OF_a);
                } else {
                    dest[i] = -1;
                }
            }
            return dest;
        }
    }
}
