package pro.shushi.pamirs.boot.web.loader;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.model.MaskDefinition;
import pro.shushi.pamirs.boot.web.service.MaskService;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import jakarta.annotation.Resource;

/**
 * 母版管理器
 * <p>
 * 2021/5/26 12:07 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Base
@Service
@Fun(MaskDefinition.MODEL_MODEL)
public class MaskLoadAction {

    @Resource
    private MaskService maskService;

    /**
     * 加载母版
     * <p>
     * 加载布局、编译、权限处理、国际化
     * 通过MaskDefinition中的loadLayout、compiled、authed、translated控制是否加载布局、编译、权限处理、国际化
     *
     * @param mask 母版
     * @return 处理后母版
     */
    @Function(summary = "加载", openLevel = FunctionOpenEnum.API)
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public MaskDefinition load(MaskDefinition mask) {
        return maskService.load(mask);
    }

}
