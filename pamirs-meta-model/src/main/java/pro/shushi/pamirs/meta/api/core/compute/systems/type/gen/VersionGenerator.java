package pro.shushi.pamirs.meta.api.core.compute.systems.type.gen;

/**
 * 版本生成器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 6:17 下午
 */
public interface VersionGenerator {

    String generate();

    /**
     * 从版本信息中获取平台版本信息
     *
     * @param version 版本
     * @return 平台版本
     */
    String platformVersion(String version);

    /**
     * 从版本信息中获取小版本
     *
     * @param version 版本信息
     * @return 小版本
     */
    String littleVersion(String version);

}
