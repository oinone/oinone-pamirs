package pro.shushi.pamirs.draft.core.hook;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Hook;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.faas.HookBefore;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.Collection;

/**
 * 创建或删除前删除草稿hook
 *
 * @author Gesi at 11:13 on 2025/9/18
 */
@Component
public class BeforeUpdateDropDraftHook implements HookBefore {

    @Hook
    @Override
    public Object run(Function function, Object... args) {
        if (args.length == 0) {
            return args;
        }
        if (
                function.getType() != null &&
                        !StringUtils.equals(function.getName(), "createDraft") && !StringUtils.equals(function.getName(), "updateDraft") &&
                        (function.getType().contains(FunctionTypeEnum.CREATE) || function.getType().contains(FunctionTypeEnum.UPDATE) || function.getType().contains(FunctionTypeEnum.DELETE))
        ) {
            Object data = args[0];
            if (data != null && !(data instanceof Collection)) {
                String model = Models.api().getDataModel(data);
                if (StringUtils.isNotBlank(model)) {
                    ModelTypeEnum modelType = PamirsSession.getContext().getModelConfig(model).getType();
                    if (!ModelTypeEnum.TRANSIENT.equals(modelType)) {
                        Fun.run(model, "deleteDraft", FieldUtils.getFieldValue(data, LambdaUtil.fetchFieldName(BaseModel::getDraftCode)));
                    }
                }
            }
        }
        return args;
    }
}
