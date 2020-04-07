package pro.shushi.pamirs.meta.base;

/**
 * pamirs module配置类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 4:09 下午
 */
public interface PamirsModule {

    /**
     * 包前缀
     *
     * @return
     */
    String[] packagePrefix();

    /**
     * 依赖包前缀
     *
     * @return
     */
    default String[] dependentPackagePrefix(){
        return new String[0];
    }

}
