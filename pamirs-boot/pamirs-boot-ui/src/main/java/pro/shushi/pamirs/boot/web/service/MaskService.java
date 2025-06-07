package pro.shushi.pamirs.boot.web.service;

import pro.shushi.pamirs.boot.base.model.MaskDefinition;

/**
 * 母版处理服务
 * <p>
 * 2022/2/23 9:55 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface MaskService {

    /**
     * 加载母版
     *
     * @param mask 母版
     * @return 处理后母版
     */
    MaskDefinition load(MaskDefinition mask);

    /**
     * 编译母版
     *
     * @param mask 母版
     * @return 编译后母版
     */
    MaskDefinition compile(MaskDefinition mask);

    /**
     * 加载母版布局
     *
     * @param mask 母版
     * @return 加载布局后母版
     */
    MaskDefinition layout(MaskDefinition mask);

    /**
     * 处理母版权限
     *
     * @param mask 视图
     * @return 处理权限后母版
     */
    MaskDefinition auth(MaskDefinition mask);

    /**
     * 国际化母版
     *
     * @param mask 母版
     * @return 国际化后母版
     */
    MaskDefinition internationalization(MaskDefinition mask);

}
