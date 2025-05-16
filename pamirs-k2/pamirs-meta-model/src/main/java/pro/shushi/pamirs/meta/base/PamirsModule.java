package pro.shushi.pamirs.meta.base;

import java.util.HashMap;
import java.util.Map;

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
     * @return 包
     */
    default String[] packagePrefix() {
        String defaultApiPackage = this.getClass().getPackage().getName();
        return new String[]{defaultApiPackage};
    }

    /**
     * 裁剪依赖包前缀
     * <p>
     * 直接指定依赖模块的包，可以裁剪缩小扫描范围，提高性能
     * 但是要处理好模型之间的依赖关系，所有被依赖的模型必需在配置的依赖包路径中
     * 若未实现该方法，系统会自动查找module的dependencies配置中的依赖模块
     * 若在dependentPackagePrefix配置了指定模块，将不会在依据module的dependencies配置中配置的该依赖模块包路径扫描
     * 若在dependentPackagePrefix配置了指定模块不在module的dependencies配置中，将会报错
     *
     * @return 依赖包
     */
    default Map<String/*module name*/, String[]> dependentPackagePrefix() {
        return new HashMap<>();
    }

}
