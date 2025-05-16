package pro.shushi.pamirs.boot.web.service;

import pro.shushi.pamirs.boot.base.model.SharedPage;
import pro.shushi.pamirs.boot.base.model.SharedPageViewAction;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

/**
 * 分享页面API
 *
 * @author Adamancy Zhang at 22:06 on 2024-04-11
 */
@Fun(SharedPageService.FUN_NAMESPACE)
public interface SharedPageService {

    String FUN_NAMESPACE = "base.SharedPageService";

    /**
     * 初始化分享页面数据
     *
     * @param page 指定动作页面信息
     * @return 初始化结果
     */
    @Function
    SharedPage init(SharedPage page);

    /**
     * 分享指定动作并生成URL链接
     *
     * @param page 指定动作页面信息
     * @return 带URL的分享数据
     */
    @Function
    SharedPage shared(SharedPage page);

    /**
     * 加载分享页面
     *
     * @param page 分享页面
     * @return 分享页面元数据
     */
    @Function
    SharedPageViewAction load(SharedPageViewAction page);
}
