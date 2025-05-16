package pro.shushi.pamirs.file.api.enmu;

import com.alibaba.excel.support.ExcelTypeEnum;
import org.apache.poi.util.IOUtils;
import pro.shushi.pamirs.file.api.config.FileConstant;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Excel类型/字符集枚举
 *
 * @author Adamancy Zhang at 15:33 on 2024-11-04
 */
public enum ExcelTypeCharsetEnum {

    XLSX(ExcelTypeEnum.XLSX, ExcelTypeEnum.XLSX.getMagic(), null),
    XLS(ExcelTypeEnum.XLS, ExcelTypeEnum.XLS.getMagic(), null),
    CSV(ExcelTypeEnum.CSV, null, null),
    CSV_GB2312(ExcelTypeEnum.CSV, new byte[]{-67, -57, -55, -85, -64, -32, -48, -51}, FileConstant.CSV_IMPORT_CHARSET),
    CSV_UTF8(ExcelTypeEnum.CSV, new byte[]{-17, -69, -65, -24, -89, -110, -24, -119}, StandardCharsets.UTF_8),
    ;

    private static final int MAX_PATTERN_LENGTH = 8;

    private final ExcelTypeEnum type;

    private final byte[] magic;

    private final Charset charset;

    ExcelTypeCharsetEnum(ExcelTypeEnum type, byte[] magic, Object charsetObject) {
        this.type = type;
        this.magic = magic;
        if (charsetObject == null) {
            this.charset = null;
        } else {
            this.charset = parseCharset(charsetObject);
        }
    }

    public ExcelTypeEnum type() {
        return type;
    }

    public Charset charset() {
        return charset;
    }

    private static Charset parseCharset(Object charsetObject) {
        Charset charset = null;
        if (charsetObject instanceof String) {
            try {
                charset = Charset.forName((String) charsetObject);
            } catch (Throwable e) {
                // slf4j不可用
                System.err.println("charset unsupported.");
                e.printStackTrace();
            }
        } else if (charsetObject instanceof Charset) {
            charset = (Charset) charsetObject;
        } else {
            // slf4j不可用
            System.err.println("charset unsupported.");
        }
        return charset;
    }

    /**
     * @see ExcelTypeEnum#recognitionExcelType(InputStream)
     */
    public static ExcelTypeCharsetEnum recognitionExcelType(InputStream inputStream) throws IOException {
        byte[] data = IOUtils.peekFirstNBytes(inputStream, MAX_PATTERN_LENGTH);
        if (findMagic(ExcelTypeCharsetEnum.XLSX.magic, data)) {
            return ExcelTypeCharsetEnum.XLSX;
        } else if (findMagic(ExcelTypeCharsetEnum.XLS.magic, data)) {
            return ExcelTypeCharsetEnum.XLS;
        } else if (findMagic(ExcelTypeCharsetEnum.CSV_UTF8.magic, data)) {
            return ExcelTypeCharsetEnum.CSV_UTF8;
        } else if (findMagic(ExcelTypeCharsetEnum.CSV_GB2312.magic, data)) {
            return ExcelTypeCharsetEnum.CSV_GB2312;
        }
        return ExcelTypeCharsetEnum.CSV;
    }

    private static boolean findMagic(byte[] expected, byte[] actual) {
        int i = 0;
        for (byte expectedByte : expected) {
            if (actual[i++] != expectedByte && expectedByte != '?') {
                return false;
            }
        }
        return true;
    }
}
