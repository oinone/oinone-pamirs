package pro.shushi.pamirs.apps.view.manager;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.apps.api.tmodel.AppCategoryModule;
import pro.shushi.pamirs.apps.api.tmodel.AppCategoryModuleList;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;

import java.util.ArrayList;
import java.util.List;

import static pro.shushi.pamirs.apps.api.enmu.ModuleCategoryType.*;

/**
 * AppCategoryModuleManager
 *
 * @author yakir on 2022/11/28 19:54.
 */
@Component
public class AppCategoryModuleManager {

    private static final List<AppCategoryModuleList> ueModules = new ArrayList<>();

    public AppCategoryModuleManager() {
        String logBaseUrl = FileClientFactory.getClient().getStaticUrl() + "/welcome/logo/";
        AppCategoryModuleList businessModuleList = new AppCategoryModuleList();
        List<AppCategoryModule> list0 = new ArrayList<>();
        businessModuleList.setName(OPERATION_SUPPORT.getDisplayName());
        businessModuleList.setModuleList(list0);
        //高效的运营支撑
        list0.add(new AppCategoryModule().setIcon(logBaseUrl + "uiDesigner.png").setName("页面设计").setModule("ui_designer").setDescription("所见即所得，在线设计多端运营页面"));
        list0.add(new AppCategoryModule().setIcon(logBaseUrl + "workflow.png").setName("工作流").setModule("workflow").setDescription("可视化的流程设计与管理，让审批变得简单"));
        list0.add(new AppCategoryModule().setIcon(logBaseUrl + "data_report.png").setName("数据可视化").setModule("data_report").setDescription("实时、多维度的数据分析，数据可视化呈现"));
        list0.add(new AppCategoryModule().setIcon(logBaseUrl + "eip.png").setName("企业集成").setModule("eip").setDescription("打破数据孤岛，让企业数据自由流转"));

        AppCategoryModuleList baseModuleList = new AppCategoryModuleList();
        baseModuleList.setName(BASE_SUPPORT.getDisplayName());
        List<AppCategoryModule> list1 = new ArrayList<>();
        baseModuleList.setModuleList(list1);
        //稳定的基础支持
        list1.add(new AppCategoryModule().setIcon(logBaseUrl + "resource.png").setName("资源").setModule("resource").setDescription("基础行业资源包，支持税务资源、地址库 、语言包管理"));
        list1.add(new AppCategoryModule().setIcon(logBaseUrl + "default.png").setName("地图").setModule("third_party_map").setDescription("三方地图数据,高德地图,地图资源"));
        list1.add(new AppCategoryModule().setIcon(logBaseUrl + "translate.png").setName("翻译").setModule("translate").setDescription("人性化翻译能力,支持全球主流语种"));
        list1.add(new AppCategoryModule().setIcon(logBaseUrl + "file.png").setName("文件").setModule("file").setDescription("提供文件管理能力，支持云端存储文件，保障文件安全"));
        list1.add(new AppCategoryModule().setIcon(logBaseUrl + "message.png").setName("消息").setModule("message").setDescription("可自定义消息模版，支持短信/email/站内信发送方式"));
        list1.add(new AppCategoryModule().setIcon(logBaseUrl + "business.png").setName("合作伙伴").setModule("business").setDescription("强大的组织管理能力，支持多种业务合作伙伴入驻流程"));
        list1.add(new AppCategoryModule().setIcon(logBaseUrl + "user.png").setName("用户中心").setModule("user").setDescription("支持自营用户与第三方平台用户管理"));
        list1.add(new AppCategoryModule().setIcon(logBaseUrl + "auth.png").setName("权限").setModule("auth").setDescription(" 多角色的系统管理能力，安全的白名单机制，支持页面/数据/逻辑行为的权限控制"));

        AppCategoryModuleList businessAppModuleList = new AppCategoryModuleList();
        businessAppModuleList.setName(BUSINESS_GROWTH.getDisplayName());
        List<AppCategoryModule> list2 = new ArrayList<>();
        businessAppModuleList.setModuleList(list2);
        //促进业务快速增长
        list2.add(new AppCategoryModule().setIcon(logBaseUrl + "gemini_core.png").setName("全员营销").setModule("gemini_core").setDescription("智能原创内容创作与任务机制，多平台多账号矩阵协同管理，提升营销效果"));
        list2.add(new AppCategoryModule().setIcon(logBaseUrl + "oms.png").setName("全渠道运营").setModule("libra_core").setDescription("拓展销售渠道，全域触达客户，轻松搞定全渠道订单管理与运营"));
        list2.add(new AppCategoryModule().setIcon(logBaseUrl + "fxpt.png").setName("分销协同").setModule("aries_core_merchant").setDescription("多元化分销商管理，进销存工具赋能分销商数字化管理"));
        list2.add(new AppCategoryModule().setIcon(logBaseUrl + "web4.png").setName("在线商城").setModule("web4").setDescription("一键商品转发分享，个性化装修多端全渠道商城"));
        list2.add(new AppCategoryModule().setIcon(logBaseUrl + "web2.png").setName("官网门户").setModule("web").setDescription("组件化模块设计，自定义排版随机切换主题，支持多端多站点"));

        ueModules.add(businessModuleList);
        ueModules.add(businessAppModuleList);
        ueModules.add(baseModuleList);
    }

    public List<AppCategoryModuleList> categoryModules() {
        return ueModules;
    }
}
