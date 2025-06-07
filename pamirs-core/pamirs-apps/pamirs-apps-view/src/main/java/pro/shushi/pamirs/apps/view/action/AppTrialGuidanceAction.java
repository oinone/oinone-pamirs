package pro.shushi.pamirs.apps.view.action;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.apps.api.enmu.AppChannelEnum;
import pro.shushi.pamirs.apps.api.tmodel.AppTrialGuidance;
import pro.shushi.pamirs.apps.api.tmodel.AppTrialGuidanceProcess;
import pro.shushi.pamirs.apps.api.tmodel.AppTrialGuidanceProcessNode;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * 移动端试用引导
 *
 * @author haibo(xf.z @ shushi.pro)
 * @date 2022-09-15 16:37:32
 */
@Base
@Component
@Model.model(AppTrialGuidance.MODEL_MODEL)
public class AppTrialGuidanceAction {

    @Function(openLevel = FunctionOpenEnum.API, summary = "应用引导信息")
    @Function.Advanced(type = FunctionTypeEnum.QUERY, displayName = "应用引导信息")
    public List<AppTrialGuidance> guidanceInfo(AppTrialGuidance data) {
        String tenant = PamirsTenantSession.getTenant();
        List<AppTrialGuidance> result = new ArrayList<>();
        List<AppTrialGuidanceProcess> processList = new ArrayList<>(AppDataMock.mockTrialGuidanceProcessMap.get().values());
        for (AppTrialGuidanceProcess guidanceProcess : processList) {
            for (AppTrialGuidanceProcessNode processNode : guidanceProcess.getNodeList()) {
                if (StringUtils.isNoneBlank(processNode.getNodeLink())) processNode.setNodeLink(processNode.getNodeLink().replaceFirst(AppDataMock.TENANT_PLACEHOLDER, tenant));
            }
        }
        result.add(new AppTrialGuidance()
                .setModule(AppChannelEnum.GEMINI.getDisplayName())
                .setModuleName(AppChannelEnum.GEMINI.getValue())
                .setModuleLogo(FileClientFactory.getClient().getStaticUrl() + "/kubernetes/upload/product/welcome/logo/default.png?x-oss-process=image/resize,m_lfit,h_800")
                .setProcessList(processList));
        return result;
    }

}
