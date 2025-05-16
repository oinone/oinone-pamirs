package pro.shushi.pamirs.framework.compute.sequence;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.VersionProcessor;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate.BASE_NO_VERSION_ERROR;

/**
 * 版本处理器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:48 上午
 */
@Slf4j
@Component
public class DefaultVersionProcessor implements VersionProcessor {

    @Override
    public boolean isNewVersion(String originVersion, String destVersion) {
        return compareVersion(originVersion, destVersion) < 0;
    }

    @Override
    public boolean isOldVersion(String originVersion, String destVersion) {
        return compareVersion(originVersion, destVersion) > 0;
    }

    @Override
    public boolean isSameVersion(String originVersion, String destVersion) {
        return compareVersion(originVersion, destVersion) == 0;
    }

    @Override
    public String upSmallVersion(String version) {
        return upVersion(version, Boolean.TRUE);
    }

    @Override
    public String upBigVersion(String version) {
        return upVersion(version, Boolean.FALSE);
    }

    private String upVersion(String version, boolean small) {
        if (StringUtils.isBlank(version)) {
            return version;
        }
        String[] subVersions = version.split("\\.");
        if (subVersions.length < 1) {
            return version;
        }
        int index = 0;
        if (small) {
            index = subVersions.length - 1;
        }
        String subVersion = subVersions[index];

        String regEx = "[0-9]\\d*";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(subVersion);
        String replacement = "";
        while (m.find()) {
            replacement = m.replaceFirst(TypeUtils.createLong(m.group(0)) + 1 + "");
            break;
        }
        subVersions[index] = replacement;
        return StringUtils.join(subVersions, ".");
    }

    private static List<String> getSubVersions(String version) {
        String regEx = "[0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(version);
        List<String> nums = new ArrayList<>();
        while (m.find()) {
            nums.add(m.group(0));
        }
        return nums;
    }

    /**
     * 比较版本号的大小,前者大则返回一个正数,后者大返回一个负数,相等则返回0
     *
     * @param version1
     * @param version2
     * @return
     */
    public static int compareVersion(String version1, String version2) {
        if (version1 == null || version2 == null) {
            throw PamirsException.construct(BASE_NO_VERSION_ERROR).errThrow();
        }
        String[] versionArray1 = version1.split("\\.");//注意此处为正则匹配，不能用"."；
        String[] versionArray2 = version2.split("\\.");
        int idx = 0;
        int minLength = Math.min(versionArray1.length, versionArray2.length);//取最小长度值
        int diff = 0;
        while (idx < minLength
                && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0//先比较长度
                && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {//再比较字符
            ++idx;
        }
        //如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
        diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
        return diff;
    }

}
