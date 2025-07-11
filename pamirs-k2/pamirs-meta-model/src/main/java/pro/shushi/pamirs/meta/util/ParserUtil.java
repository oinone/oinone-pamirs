package pro.shushi.pamirs.meta.util;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pro.shushi.pamirs.meta.enmu.MetaExpEnumerate.BASE_STRING_FORMAT_2_ERROR;
import static pro.shushi.pamirs.meta.enmu.MetaExpEnumerate.BASE_STRING_FORMAT_ERROR;

public class ParserUtil {

    private static final Pattern pattern = Pattern.compile("\\$\\{(.*?)}");

    /**
     * 替换字符串占位符, 字符串中使用${key}表示占位符
     *
     * @param sourceString 需要匹配的字符串，示例："名字:${name},年龄:${age},学校:${school}";
     * @param param        参数集,Map类型
     * @return 替换结果
     */
    public static String replaceWithMap(String sourceString, Map<String, Object> param) {
        if (Strings.isNullOrEmpty(sourceString) || CollectionUtils.isEmpty(param)) {
            return sourceString;
        }
        String targetString = sourceString;
        Matcher matcher = pattern.matcher(sourceString);
        while (matcher.find()) {
            try {
                String key = matcher.group();
                String keyclone = key.substring(2, key.length() - 1).trim();//如果占位符是{} 这里就是key.substring(1, key.length() - 1).trim()
                Object value = param.get(keyclone);
                if (value != null) {
                    targetString = targetString.replace(key, value.toString());
                }
            } catch (Exception e) {
                throw PamirsException.construct(BASE_STRING_FORMAT_ERROR, e).errThrow();
            }
        }
        return targetString;
    }

    /**
     * 替换字符串占位符, 字符串中使用${key}表示占位符
     * <p>
     * 利用反射 自动获取对象属性值 (必须有get方法)
     *
     * @param sourceString 需要匹配的字符串
     * @param param        参数集
     * @return 替换结果
     */
    public static String replaceWithObject(String sourceString, Object param) {
        if (Strings.isNullOrEmpty(sourceString) || ObjectUtils.isEmpty(param)) {
            return sourceString;
        }

        String targetString = sourceString;

        PropertyDescriptor pd;
        Method getMethod;

        // 匹配${}中间的内容 包括括号
        Matcher matcher = pattern.matcher(sourceString);
        while (matcher.find()) {
            String key = matcher.group();
            String holderName = key.substring(2, key.length() - 1).trim();
            try {
                pd = new PropertyDescriptor(holderName, param.getClass());
                getMethod = pd.getReadMethod(); // 获得get方法
                Object value = getMethod.invoke(param);
                if (value != null) {
                    targetString = targetString.replace(key, value.toString());
                }
            } catch (Exception e) {
                throw PamirsException.construct(BASE_STRING_FORMAT_2_ERROR, e).errThrow();
            }
        }
        return targetString;
    }

    /**
     * 查找String中的占位符keys；<br/>
     * 示例： "名字:${name},年龄:${age},学校:${school}"， 则返回：Set[name,age,school]
     * <p>
     * pattern示例：
     * <pre> {@code
     *  // 尖括号：<placeHolder> 表示为占位符
     *  Pattern pattern = Pattern.compile("\\$\\<(.*?)\\>");
     *
     *  // 大括号：{placeHolder} 表示为占位符， 上面的示例中就使用{}作为占位符
     *  Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
     * }
     * </pre>
     *
     * @param sourceString 源字符串
     * @param pattern      格式
     * @return 结果
     */
    public static Set<String> findPlaceHolderKeys(String sourceString, Pattern pattern) {
        Set<String> placeHolderSet = Sets.newConcurrentHashSet();

        if (Strings.isNullOrEmpty(sourceString) || ObjectUtils.isEmpty(pattern)) {
            return placeHolderSet;
        }
        Matcher matcher = pattern.matcher(sourceString);
        while (matcher.find()) {
            String key = matcher.group();  //示例: {name}
            String placeHolder = key.substring(2, key.length() - 1).trim();  //示例： name
            placeHolderSet.add(placeHolder);
        }

        return placeHolderSet;
    }

    /**
     * 将除占位符以外的所有地方大写；<br/>
     * 示例： "Name:${name},age:${age},sCHOOL:${school}"， 则返回："NAME:${name},AGE:${age},SCHOOL:${school}"
     * @param str
     * @return
     */
    public static String toUpperCaseExcludePlaceholder(String str) {
        if (str == null) {
            return null;
        }
        Matcher matcher = pattern.matcher(str);
        int lastEnd = 0;
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String before = str.substring(lastEnd, matcher.start());
            result.append(before.toUpperCase());

            result.append(matcher.group());

            lastEnd = matcher.end();
        }

        if (lastEnd < str.length()) {
            result.append(str.substring(lastEnd).toUpperCase());
        }

        return result.toString();
    }

}