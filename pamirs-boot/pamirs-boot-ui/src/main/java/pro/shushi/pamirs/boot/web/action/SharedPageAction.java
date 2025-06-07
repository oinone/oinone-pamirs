package pro.shushi.pamirs.boot.web.action;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.SharedPage;
import pro.shushi.pamirs.boot.base.model.SharedPageViewAction;
import pro.shushi.pamirs.boot.web.service.SharedPageService;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

/**
 * 分享页面动作
 *
 * @author Adamancy Zhang at 18:58 on 2024-04-11
 */
@Base
@Component
@Model.model(SharedPage.MODEL_MODEL)
public class SharedPageAction {

    @Function.Advanced(displayName = "初始化分享数据", type = FunctionTypeEnum.QUERY)
    @Function(openLevel = FunctionOpenEnum.API)
    public SharedPage construct(SharedPage page) {
        if (page == null) {
            page = new SharedPage();
        }
        page = page.construct();
        return Fun.run(SharedPageService::init, page);
    }

    @Action(displayName = "分享")
    public SharedPage shared(SharedPage page) {
        return Fun.run(SharedPageService::shared, page);
    }

    @Function.Advanced(displayName = "加载分享页面", type = FunctionTypeEnum.QUERY)
    @Function(openLevel = FunctionOpenEnum.API)
    public SharedPageViewAction load(SharedPageViewAction page) {
        return Fun.run(SharedPageService::load, page);
    }
}
