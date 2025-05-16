package pro.shushi.pamirs.eip.view.action;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.eip.api.behavior.ISynchronizationModel;
import pro.shushi.pamirs.eip.api.behavior.model.SynchronizationInfoDefinition;
import pro.shushi.pamirs.eip.api.behavior.model.SynchronizationInfoTransient;
import pro.shushi.pamirs.eip.api.behavior.util.SynchronizationBehavior;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

@Component
@Model.model(SynchronizationInfoTransient.MODEL_MODEL)
public class SynchronizationInfoTransientAction extends SynchronizationBehavior {

    @Function(openLevel = FunctionOpenEnum.API)
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    @Function.fun(FunctionConstants.construct)
    public SynchronizationInfoTransient construct(SynchronizationInfoTransient definition) {
        String modelModel = definition.getModelModel();
        if (StringUtils.isNotBlank(modelModel)) {
            definition.setDefinitions(new SynchronizationInfoDefinition()
                    .setModelModel(modelModel).setDataStatus(DataStatusEnum.ENABLED).queryList());
        }
        return definition;
    }

    //    @XAsync(displayName = "异步同步给周边系统") 改成同步
    @Action(displayName = "同步信息")
    public SynchronizationInfoTransient push(SynchronizationInfoTransient data) {
        String interfaceNames = data.getInterfaceNames();
        if (StringUtils.isNotBlank(interfaceNames)) {
            String[] interfaceList = PStringUtils.split(interfaceNames, CharacterConstants.SEPARATOR_COMMA);
            for (String interfaceName : interfaceList) {
                if (StringUtils.isNotBlank(data.getIds())) {
                    String[] ids = PStringUtils.split(data.getIds(), CharacterConstants.SEPARATOR_COMMA);
                    for (String id : ids) {
                        ISynchronizationModel o = FetchUtil.fetchOneById(data.getModelModel(), Long.valueOf(id));
                        execute(o, interfaceName);
//                        Models.data().updateWithField(o); execute方法自己去做更新
                    }
                }
            }
        }
        return data;
    }

}
