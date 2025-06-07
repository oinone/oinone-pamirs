package pro.shushi.pamirs.apps.view.action;


import pro.shushi.pamirs.apps.api.enmu.AppChannelEnum;
import pro.shushi.pamirs.apps.api.enmu.AppsExpEnumerate;
import pro.shushi.pamirs.apps.api.tmodel.AppTrialGuidanceProcess;
import pro.shushi.pamirs.apps.api.tmodel.AppTrialGuidanceProcessNode;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.core.common.MapHelper;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;


/**
 * 应用引导数据mock
 *
 * @author haibo(xf.z @ shushi.pro)
 * @date 2022-09-15 16:27:17
 */
public class AppDataMock {

    public static final String TENANT_PLACEHOLDER = "TENANT_PLACEHOLDER";

    public static final String OMS_TRIAL_PROCESS_TRADE = "1";   //履约流程
    public static final String OMS_TRIAL_PROCESS_REVERSE = "2";   //售后流程
    public static final String OMS_TRIAL_PROCESS_SKU = "3";    //创建新产品
    public static final String HTTPS = "https://";
    public static final String DOMAIN = "oinone.top";
    public static final String GEMINI_TRIAL_PROCESS = "4";   //全员营销试用流程


    protected static final Supplier<Map<String, AppTrialGuidanceProcess>> mockTrialGuidanceProcessMap = () ->
            MapHelper.newInstance(new LinkedHashMap<String, AppTrialGuidanceProcess>())
                    //region 全员营销试用流程
                    .put(GEMINI_TRIAL_PROCESS, new AppTrialGuidanceProcess()
                            .setProcessName("全员营销流程")
                            .setIsDefault(true)
                            .setRoles(Arrays.asList(new AuthRole().setName("平台运营"), new AuthRole().setName("员工")))
                            .setNodeList(new ArrayList<AppTrialGuidanceProcessNode>() {{
                                add(new AppTrialGuidanceProcessNode().setNodeName("上传素材").setNodeDepth(1).setNodeLink(genNodeLink(AppChannelEnum.GEMINI, "gemini.resource.GeminiMaterial", ViewTypeEnum.TABLE, "MenuuiMenu0000000000000003")).setRole(new AuthRole().setName("平台运营")));
                                add(new AppTrialGuidanceProcessNode().setNodeName("创建任务").setNodeDepth(2).setNodeLink(genNodeLink(AppChannelEnum.GEMINI, "gemini.biz.GeminiTaskProxy", ViewTypeEnum.TABLE, "MenuuiMenu0000000000000002")).setRole(new AuthRole().setName("平台运营")));
                                add(new AppTrialGuidanceProcessNode().setNodeName("接任务去小程序发布原创视频").setNodeDepth(3).setRole(new AuthRole().setName("员工")));
                                add(new AppTrialGuidanceProcessNode().setNodeName("内容分析").setNodeDepth(4).setNodeLink(genNodeLink(AppChannelEnum.GEMINI, "gemini.sdk.GeminiDyVideoProxy", ViewTypeEnum.TABLE, "MenuuiMenu0000000000001002")).setRole(new AuthRole().setName("平台运营")));
                                add(new AppTrialGuidanceProcessNode().setNodeName("账号分析").setNodeDepth(5).setNodeLink(genNodeLink(AppChannelEnum.GEMINI, "gemini.major.GeminiUserProxy", ViewTypeEnum.TABLE, "MenuuiMenu0000000000001501")).setRole(new AuthRole().setName("平台运营")));
                            }})
                    )
                    //endregion

                    .build();


    private static final String genNodeLink(AppChannelEnum module, String modelModel, String actionName) {
        return genNodeLink(module, modelModel, ViewTypeEnum.TABLE, actionName);
    }

    private static final String genNodeLink(AppChannelEnum module, String modelModel, ViewTypeEnum viewType, String actionName) {
        String moduleDomainCore;
        switch (module) {
            case B2C:
                moduleDomainCore = "leoMerchantCore";
                break;
            case OMS:
                moduleDomainCore = "libraCore";
                break;
            case DMS:
                moduleDomainCore = "ariesMerchantCore";
                break;
            case GEMINI:
                moduleDomainCore = "geminiCore";
                break;
            default:
                throw PamirsException.construct(AppsExpEnumerate.APPS_DATA_MOCK_NODE_MODULE_ERROR).errThrow();
        }
        return HTTPS + TENANT_PLACEHOLDER + "." + DOMAIN + "/page;module=" + moduleDomainCore + ";model=" + modelModel + ";viewType=" + viewType.value() + ";action=" + actionName;
    }
}
