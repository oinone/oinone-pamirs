package pro.shushi.pamirs.business.view;

import pro.shushi.pamirs.boot.base.constants.ViewActionConstants;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenu;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenus;
import pro.shushi.pamirs.business.api.entity.PamirsCompany;
import pro.shushi.pamirs.business.api.entity.PamirsPerson;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.business.api.model.PamirsPosition;
import pro.shushi.pamirs.core.common.constant.CommonConstants;

/**
 * Business管理后台菜单
 * <p>
 * 2020/11/18 5:08 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SuppressWarnings({"unused", "InnerClassMayBeStatic"})// @formatter:off 忽略格式化，请开启IDE格式化注解开关

@UxMenus(module = CommonConstants.MANAGEMENT_CENTER_MODULE, basePriority = 200) /*可以注解到该模块的任意类上，建议同一个模块中只配置一处*/ class BusinessMenus implements ViewActionConstants {

   @UxMenu("合作伙伴") class PamirsPartnerMenu {
        @UxMenu("公司") @UxRoute(model = PamirsCompany.MODEL_MODEL, filter = "partnerType=='COMPANY'") class PamirsCompanyMenu {}
        @UxMenu("个人") @UxRoute(model = PamirsPerson.MODEL_MODEL, filter = "partnerType=='PERSON'") class PamirsPersonMenu {}
    }
    @UxMenu("组织架构") class OrganizationalStructureMenu {
        @UxMenu("部门") @UxRoute(model = PamirsDepartment.MODEL_MODEL) class PamirsDepartmentMenu {}
        @UxMenu("岗位") @UxRoute(model = PamirsPosition.MODEL_MODEL) class PamirsPositionMenu {}
        @UxMenu("员工") @UxRoute(model = PamirsEmployee.MODEL_MODEL) class PamirsEmployeeMenu {}
    }
}
