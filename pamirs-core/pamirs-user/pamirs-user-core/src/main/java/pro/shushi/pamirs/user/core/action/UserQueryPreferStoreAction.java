package pro.shushi.pamirs.user.core.action;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.function.FunctionConstant;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.user.core.base.model.UserQueryPreferStore;

import java.util.List;

@Component
@Model.model(UserQueryPreferStore.MODEL_MODEL)
public class UserQueryPreferStoreAction {

    @Function.Advanced(type = FunctionTypeEnum.CREATE)
    @Function.fun(FunctionConstants.create)
    @Function(openLevel = {FunctionOpenEnum.API})
    public UserQueryPreferStore create(UserQueryPreferStore data) {
        if (data != null && data.getPartnerId() == null) {
            data.setPartnerId(PamirsSession.getUserId());
        }
        return data.create();
    }

    @Function.Advanced(type = FunctionTypeEnum.DELETE, managed = true)
    @Function(openLevel = {FunctionOpenEnum.API})
    @Function.fun(FunctionConstant.deleteWithFieldBatch)
    public List<UserQueryPreferStore> delete(List<UserQueryPreferStore> dataList) {
        return Models.origin().deleteWithFieldBatch(dataList);
    }
}

