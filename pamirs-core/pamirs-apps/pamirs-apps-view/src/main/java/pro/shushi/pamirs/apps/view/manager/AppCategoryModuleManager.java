package pro.shushi.pamirs.apps.view.manager;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.apps.api.tmodel.AppCategoryModule;
import pro.shushi.pamirs.apps.api.tmodel.AppCategoryModuleList;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;
import pro.shushi.pamirs.locale.utils.I18nUtils;

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
        list0.add(new AppCategoryModule().setIcon(logBaseUrl + "uiDesigner.png").setName(I18nUtils.getMessage("apps.view.manager.page_design")).setModule("ui_designer").setDescription(I18nUtils.getMessage("apps.view.manager.page_design.desc")));
        list0.add(new AppCategoryModule().setIcon(logBaseUrl + "workflow.png").setName(I18nUtils.getMessage("apps.view.manager.workflow")).setModule("workflow").setDescription(I18nUtils.getMessage("apps.view.manager.workflow.desc")));
        list0.add(new AppCategoryModule().setIcon(logBaseUrl + "data_report.png").setName(I18nUtils.getMessage("apps.view.manager.data_report")).setModule("data_report").setDescription(I18nUtils.getMessage("apps.view.manager.data_report.desc")));
        list0.add(new AppCategoryModule().setIcon(logBaseUrl + "eip.png").setName(I18nUtils.getMessage("apps.view.manager.eip")).setModule("eip").setDescription(I18nUtils.getMessage("apps.view.manager.eip.desc")));

        AppCategoryModuleList baseModuleList = new AppCategoryModuleList();
        baseModuleList.setName(BASE_SUPPORT.getDisplayName());
        List<AppCategoryModule> list1 = new ArrayList<>();
        baseModuleList.setModuleList(list1);
        //稳定的基础支持
        list1.add(new AppCategoryModule().setIcon(logBaseUrl + "resource.png").setName(I18nUtils.getMessage("apps.view.manager.resource")).setModule("resource").setDescription(I18nUtils.getMessage("apps.view.manager.resource.desc")));
        list1.add(new AppCategoryModule().setIcon(logBaseUrl + "default.png").setName(I18nUtils.getMessage("apps.view.manager.map")).setModule("third_party_map").setDescription(I18nUtils.getMessage("apps.view.manager.map.desc")));
        list1.add(new AppCategoryModule().setIcon(logBaseUrl + "translate.png").setName(I18nUtils.getMessage("apps.view.manager.translate")).setModule("translate").setDescription(I18nUtils.getMessage("apps.view.manager.translate.desc")));
        list1.add(new AppCategoryModule().setIcon(logBaseUrl + "file.png").setName(I18nUtils.getMessage("apps.view.manager.file")).setModule("file").setDescription(I18nUtils.getMessage("apps.view.manager.file.desc")));
        list1.add(new AppCategoryModule().setIcon(logBaseUrl + "message.png").setName(I18nUtils.getMessage("apps.view.manager.message")).setModule("message").setDescription(I18nUtils.getMessage("apps.view.manager.message.desc")));
        list1.add(new AppCategoryModule().setIcon(logBaseUrl + "business.png").setName(I18nUtils.getMessage("apps.view.manager.business")).setModule("business").setDescription(I18nUtils.getMessage("apps.view.manager.business.desc")));
        list1.add(new AppCategoryModule().setIcon(logBaseUrl + "user.png").setName(I18nUtils.getMessage("apps.view.manager.user")).setModule("user").setDescription(I18nUtils.getMessage("apps.view.manager.user.desc")));
        list1.add(new AppCategoryModule().setIcon(logBaseUrl + "auth.png").setName(I18nUtils.getMessage("apps.view.manager.auth")).setModule("auth").setDescription(I18nUtils.getMessage("apps.view.manager.auth.desc")));

        AppCategoryModuleList businessAppModuleList = new AppCategoryModuleList();
        businessAppModuleList.setName(BUSINESS_GROWTH.getDisplayName());
        List<AppCategoryModule> list2 = new ArrayList<>();
        businessAppModuleList.setModuleList(list2);
        //促进业务快速增长
        list2.add(new AppCategoryModule().setIcon(logBaseUrl + "gemini_core.png").setName(I18nUtils.getMessage("apps.view.manager.marketing")).setModule("gemini_core").setDescription(I18nUtils.getMessage("apps.view.manager.marketing.desc")));
        list2.add(new AppCategoryModule().setIcon(logBaseUrl + "oms.png").setName(I18nUtils.getMessage("apps.view.manager.omni_channel")).setModule("libra_core").setDescription(I18nUtils.getMessage("apps.view.manager.omni_channel.desc")));
        list2.add(new AppCategoryModule().setIcon(logBaseUrl + "fxpt.png").setName(I18nUtils.getMessage("apps.view.manager.distribution")).setModule("aries_core_merchant").setDescription(I18nUtils.getMessage("apps.view.manager.distribution.desc")));
        list2.add(new AppCategoryModule().setIcon(logBaseUrl + "web4.png").setName(I18nUtils.getMessage("apps.view.manager.online_mall")).setModule("web4").setDescription(I18nUtils.getMessage("apps.view.manager.online_mall.desc")));
        list2.add(new AppCategoryModule().setIcon(logBaseUrl + "web2.png").setName(I18nUtils.getMessage("apps.view.manager.portal")).setModule("web").setDescription(I18nUtils.getMessage("apps.view.manager.portal.desc")));

        ueModules.add(businessModuleList);
        ueModules.add(businessAppModuleList);
        ueModules.add(baseModuleList);
    }

    public List<AppCategoryModuleList> categoryModules() {
        return ueModules;
    }
}
