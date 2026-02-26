package pro.shushi.pamirs.core.common.version;

import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.exception.IncomparableException;
import pro.shushi.pamirs.ux.common.utils.NumberHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 版本
 *
 * @author Adamancy Zhang on 2021-06-07 12:43
 */
public class Version implements Comparable<Version> {

    public static final char SPLIT = '.';

    public static final String SPLIT_STRING = ".";

    public static final String SPLIT_REGEX = "\\.";

    /**
     * 版本号
     */
    private final String value;

    /**
     * 前缀
     */
    private final String prefix;

    /**
     * 后缀
     */
    private final String suffix;

    /**
     * 版本序列
     */
    private final int[] versions;

    public Version(String value, String prefix, String suffix, int[] versions) {
        this.value = value;
        this.prefix = prefix;
        this.suffix = suffix;
        this.versions = versions;
    }

    public Version(String value) {
        this.value = value;
        Version version = parse(value);
        this.prefix = version.prefix;
        this.suffix = version.suffix;
        this.versions = version.versions;
    }

    public String getValue() {
        return value;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getVersion() {
        StringBuilder builder = new StringBuilder();
        for (int version : versions) {
            if (builder.length() != 0) {
                builder.append(SPLIT);
            }
            builder.append(version);
        }
        return builder.toString();
    }

    /**
     * <h>版本比较</h>
     * <p>
     * 可比较条件：<br/>
     * 1、版本号数量相同<br/>
     * 2、前缀和后缀一致<br/>
     * </p>
     * <p>
     * 比较结果说明：<br/>
     * 1、当版本相同时，返回0<br/>
     * 2、当版本小于当前版本时，返回-1<br/>
     * 3、当版本大于当前版本时，返回0<br/>
     * 4、当不可比较时，返回{@link Integer#MIN_VALUE}<br/>
     * </p>
     *
     * @param o 目标版本
     * @return 比较结果
     */
    @Override
    public int compareTo(@Nonnull Version o) {
        int tl = this.versions.length,
                ol = o.versions.length;
        if (tl == ol
                && this.prefix.equals(o.prefix)
                && this.suffix.equals(o.suffix)) {
            for (int i = 0; i < ol; i++) {
                int compare = Integer.compare(this.versions[i], o.versions[i]);
                if (compare != 0) {
                    return compare;
                }
            }
            return 0;
        }
        throw new IncomparableException("These values cannot be compared. value1: " + this.value + ", value2: " + o.value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Version)) {
            return false;
        }
        try {
            return compareTo((Version) o) == 0;
        } catch (IncomparableException e) {
            return false;
        }
    }

    public static boolean check(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        int length = value.length();
        List<StringBuilder> versionBuilderList = new ArrayList<>();
        boolean appendPrefix = true,
                isNewNumber = true;
        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (NumberHelper.isNumber(c)) {
                if (appendPrefix) {
                    appendPrefix = false;
                }
                if (isNewNumber) {
                    isNewNumber = false;
                    StringBuilder builder = new StringBuilder();
                    builder.append(c);
                    versionBuilderList.add(builder);
                }
            } else {
                if (c == SPLIT) {
                    isNewNumber = true;
                } else {
                    if (!appendPrefix) {
                        return true;
                    }
                }
            }
        }
        if (versionBuilderList.isEmpty()) {
            return false;
        }
        for (StringBuilder versionBuilder : versionBuilderList) {
            if (!NumberHelper.isNumber(versionBuilder.toString())) {
                return false;
            }
        }
        return true;
    }

    public static Version parse(String value) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("Invalid value.");
        }
        StringBuilder prefixBuilder = new StringBuilder();
        StringBuilder suffixBuilder = new StringBuilder();
        List<StringBuilder> versionBuilderList = new ArrayList<>();
        int length = value.length();
        boolean appendPrefix = true,
                appendSuffix = false,
                isNewNumber = true;
        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (appendSuffix) {
                suffixBuilder.append(c);
                continue;
            }
            if (NumberHelper.isNumber(c)) {
                if (appendPrefix) {
                    appendPrefix = false;
                }
                if (isNewNumber) {
                    isNewNumber = false;
                    StringBuilder builder = new StringBuilder();
                    builder.append(c);
                    versionBuilderList.add(builder);
                } else {
                    versionBuilderList.get(versionBuilderList.size() - 1).append(c);
                }
            } else {
                if (c == SPLIT) {
                    isNewNumber = true;
                } else {
                    if (appendPrefix) {
                        prefixBuilder.append(c);
                    } else {
                        appendSuffix = true;
                        suffixBuilder.append(c);
                    }
                }
            }
        }
        if (versionBuilderList.isEmpty()) {
            throw new IllegalArgumentException("Invalid version string. value=" + value);
        }
        int[] versions = new int[versionBuilderList.size()];
        for (int i = 0; i < versionBuilderList.size(); i++) {
            versions[i] = NumberHelper.intValueOf(versionBuilderList.get(i));
        }
        return new Version(value, prefixBuilder.toString(), suffixBuilder.toString(), versions);
    }
}
