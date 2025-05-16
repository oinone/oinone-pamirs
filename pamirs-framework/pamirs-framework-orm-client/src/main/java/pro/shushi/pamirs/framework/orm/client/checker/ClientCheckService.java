package pro.shushi.pamirs.framework.orm.client.checker;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.meta.api.core.orm.template.DataComputeTemplate;
import pro.shushi.pamirs.meta.api.core.orm.template.context.ModelComputeContext;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.constant.FunctionParamConstants;

import javax.annotation.Resource;

/**
 * 模型校验服务
 * <p>
 * 2022/5/11 2:28 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Service
public class ClientCheckService implements ClientCheckServiceApi {

    @Resource
    private DataComputeTemplate dataComputeTemplate;

    @Resource
    private ClientModelChecker clientModelChecker;

    @Resource
    private ClientFieldChecker clientFieldChecker;

    @Override
    public <T> T check(String model, T obj) {
        return check(model, null, obj);
    }

    @Override
    public <T> T check(String model, String argName, T obj) {
        if (StringUtils.isBlank(model) || null == obj || IWrapper.MODEL_MODEL.equals(model)) {
            return obj;
        }
        ModelComputeContext context = new ModelComputeContext();
        context.initPath();
        if (null == argName) {
            argName = FunctionParamConstants.data;
        }
        context.segment(argName);
        try {
            return check(context, model, obj);
        } finally {
            context.dropSegment();
        }
    }

    @Override
    public <T> T check(ModelComputeContext totalContext, String model, T obj) {
        boolean isDoCheck = PamirsSession.directive().isDoCheck();
        try {
            PamirsSession.directive().enableCheck();
            if (StringUtils.isBlank(model) || null == obj || IWrapper.MODEL_MODEL.equals(model)) {
                return obj;
            }
            return dataComputeTemplate.compute(totalContext, model, obj,
                    this::check,
                    (context, oModel, oObj) -> oObj,
                    (context, oModel, oObj) -> {
                        return clientModelChecker.check(context, oModel, oObj);// 模型约束校验
                    },
                    (context, fieldConfig, oObj) -> clientFieldChecker.check(context, fieldConfig, oObj)// 前端字段约束校验
            );
        } finally {
            if (!isDoCheck) {
                PamirsSession.directive().disableCheck();
            }
        }
    }

}
