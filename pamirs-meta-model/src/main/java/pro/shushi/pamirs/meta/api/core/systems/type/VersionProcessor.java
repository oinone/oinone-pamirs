package pro.shushi.pamirs.meta.api.core.systems.type;

/**
 *
 * 版本处理器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 6:17 下午
 */
public interface VersionProcessor {

    boolean isNewVersion(String originVersion, String destVersion);

    boolean isOldVersion(String originVersion, String destVersion);

    boolean isSameVersion(String originVersion, String destVersion);

    String upSmallVersion(String version);

    String upBigVersion(String version);

}
