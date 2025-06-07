package pro.shushi.pamirs.core.common.loader.view;

import pro.shushi.pamirs.core.common.directive.Directive;

/**
 * ViewLoader特性
 *
 * @author Adamancy Zhang on 2021-05-18 15:08
 */
public enum ViewLoaderFeature implements Directive {

    /**
     * 使用模块后缀
     */
    USING_MODULE_SUFFIX(2 >> 1),

    /**
     * 仅加载当前模块（在baseResourcePattern后追加模块编码路径）
     */
    ONLY_LOAD_CURRENT_MODULE(2),

    /**
     * 递归文件夹，类似于ls -R
     */
    RECURSION_FOLDER(2 << 1),

    /**
     * 通过文件名加载视图类型
     */
    LOAD_TYPE_BY_FILENAME(2 << 2),
    ;

    private final int intValue;

    ViewLoaderFeature(int intValue) {
        this.intValue = intValue;
    }

    @Override
    public int intValue() {
        return intValue;
    }
}
