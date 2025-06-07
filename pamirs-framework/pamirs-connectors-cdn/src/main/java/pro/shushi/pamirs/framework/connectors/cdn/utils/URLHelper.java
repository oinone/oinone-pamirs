package pro.shushi.pamirs.framework.connectors.cdn.utils;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * URL帮助类
 *
 * @author Adamancy Zhang at 10:34 on 2024-06-18
 */
public class URLHelper {

    private URLHelper() {
        //reject create object
    }

    /**
     * url encode. 只对文件名(中文部分)进行编码
     *
     * @param url url
     * @return encoded url
     */
    public static String encodeFileName(String url) {
        if (StringUtils.isBlank(url)) {
            return url;
        }
        try {
            String fileName = url.substring(url.lastIndexOf(CharacterConstants.SEPARATOR_SLASH) + 1);
            String res = url.substring(0, url.lastIndexOf(CharacterConstants.SEPARATOR_SLASH) + 1) + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name());
            // 其中空格被编码成  +   ；这样编码后空格编码还是有问题，需在处理
            // 因为 + 符号在java是关键字符需要转义，不能直接用
            // %20 为空格的编码，这里替换掉，URL才不会报错
            return res.replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException ignored) {
        }
        return null;
    }
}
