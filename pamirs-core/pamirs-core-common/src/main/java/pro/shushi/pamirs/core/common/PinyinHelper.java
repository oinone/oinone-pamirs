package pro.shushi.pamirs.core.common;

import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.function.BiFunction;

/**
 * @author Adamancy Zhang
 * @date 2020-11-02 11:43
 */
public class PinyinHelper {

    private PinyinHelper() {
        //reject create object
    }

    public static final int CHINESE_CHARACTER_MIN = 0x4e00;

    public static final int CHINESE_CHARACTER_MAX = 0x9fa5;

    /**
     * <h>default output format description</h>
     * <p>
     * 1. output lowercase
     *
     * @see HanyuPinyinCaseType#LOWERCASE default
     * @see HanyuPinyinCaseType#UPPERCASE output uppercase
     * <br/>
     * 2. with tone mark
     * @see HanyuPinyinToneType#WITH_TONE_MARK default (VCharType must be {@link HanyuPinyinVCharType#WITH_U_UNICODE})
     * @see HanyuPinyinToneType#WITHOUT_TONE without tone
     * @see HanyuPinyinToneType#WITH_TONE_NUMBER use one to four as tone
     * <br/>
     * 3. use u as ü
     * @see HanyuPinyinVCharType#WITH_U_AND_COLON default
     * @see HanyuPinyinVCharType#WITH_V use v as ü
     * @see HanyuPinyinVCharType#WITH_U_UNICODE use ü
     * </p>
     */
    public static final HanyuPinyinOutputFormat DEFAULT_OUTPUT_FORMAT;

    public static final HanyuPinyinOutputFormat DEFAULT_WITHOUT_TONE_OUTPUT_FORMAT;

    static {
        DEFAULT_WITHOUT_TONE_OUTPUT_FORMAT = new HanyuPinyinOutputFormat();
        DEFAULT_WITHOUT_TONE_OUTPUT_FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        DEFAULT_WITHOUT_TONE_OUTPUT_FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        DEFAULT_WITHOUT_TONE_OUTPUT_FORMAT.setVCharType(HanyuPinyinVCharType.WITH_U_AND_COLON);

        DEFAULT_OUTPUT_FORMAT = new HanyuPinyinOutputFormat();
        DEFAULT_OUTPUT_FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        DEFAULT_OUTPUT_FORMAT.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);
        DEFAULT_OUTPUT_FORMAT.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
    }

    public static String getFullPinyin(String s) {
        return getFullPinyin(s, DEFAULT_WITHOUT_TONE_OUTPUT_FORMAT);
    }

    /**
     * get full pinyin by {@link PinyinHelper#DEFAULT_OUTPUT_FORMAT} and get first default pinyin
     *
     * @param s            chinese string
     * @param outputFormat output format {@link HanyuPinyinOutputFormat}
     * @return after convert output string
     */
    public static String getFullPinyin(String s, HanyuPinyinOutputFormat outputFormat) {
        return getPinyin(s, outputFormat, (c, ss) -> ss[0]);
    }

    public static String[] getFullPinyin(char c) {
        return getFullPinyin(c, DEFAULT_OUTPUT_FORMAT);
    }

    public static String[] getFullPinyin(char c, HanyuPinyinOutputFormat outputFormat) {
        try {
            return net.sourceforge.pinyin4j.PinyinHelper.toHanyuPinyinStringArray(c, outputFormat);
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            return new String[]{StringHelper.valueOf(c)};
        }
    }

    public static String getPinyin(String s, HanyuPinyinOutputFormat outputFormat, BiFunction<Character, String[], Object> function) {
        StringBuilder sb = new StringBuilder();
        char[] cs = s.toCharArray();
        for (char c : cs) {
            if (c >= CHINESE_CHARACTER_MIN && c <= CHINESE_CHARACTER_MAX) {
                sb.append(function.apply(c, getFullPinyin(c, outputFormat)));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
