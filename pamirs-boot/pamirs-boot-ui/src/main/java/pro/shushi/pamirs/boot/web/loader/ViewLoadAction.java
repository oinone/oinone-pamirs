package pro.shushi.pamirs.boot.web.loader;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.web.service.ViewService;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.List;

/**
 * 视图管理器
 * <p>
 * 2021/5/26 12:07 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Base
@Service
@Fun(View.MODEL_MODEL)
public class ViewLoadAction {

    @Resource
    private ViewService viewService;

    /**
     * 加载视图
     * <p>
     * 加载布局、编译、权限处理、国际化
     * 通过View中的loadLayout、compiled、authed、translated控制是否加载布局、编译、权限处理、国际化
     *
     * @param viewList 视图列表
     * @return 处理后视图列表
     */
    @Function(summary = "加载", openLevel = FunctionOpenEnum.API)
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public List<View> load(List<View> viewList) {
        return viewService.load(viewList);
    }

}
