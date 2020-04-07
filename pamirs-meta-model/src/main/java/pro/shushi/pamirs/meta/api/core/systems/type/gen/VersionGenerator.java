package pro.shushi.pamirs.meta.api.core.systems.type.gen;

/**
 *
 * 版本生成器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 6:17 下午
 */
public interface VersionGenerator {

    String generate();

    /**
     * 从版本信息中获取平台版本
     *
     * @param version
     * @return
     */
    String platformVersion(String version);

    /**
     * 从版本信息中获取小版本
     *
     * @param version
     * @return
     */
    String littleVersion(String version);

}
