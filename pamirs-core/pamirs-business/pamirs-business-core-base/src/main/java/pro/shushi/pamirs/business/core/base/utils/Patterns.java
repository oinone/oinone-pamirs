package pro.shushi.pamirs.business.core.base.utils;

import java.util.regex.Pattern;

/**
 * Patterns
 *
 * @author yakir on 2022/09/15 11:15.
 */
public interface Patterns {

    // 中文 英文 数字
    Pattern CH_EN_NUM = Pattern.compile("^[a-zA-Z0-9\\u4e00-\\u9fa5\\uFF21-\\uFF3A\\uFF41-\\uFF5A]+$");

    Pattern EN_LETTER_NUM = Pattern.compile("^[a-z][a-z0-9]*$");
    Pattern NUM_START     = Pattern.compile("^\\d+?.*$");


}
