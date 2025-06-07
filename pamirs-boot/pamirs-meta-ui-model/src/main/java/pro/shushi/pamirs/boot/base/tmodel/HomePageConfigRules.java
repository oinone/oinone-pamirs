package pro.shushi.pamirs.boot.base.tmodel;

import pro.shushi.pamirs.boot.base.enmu.BindingTypeEnum;
import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.base.proxy.AdvancedHomePageMenuProxy;
import pro.shushi.pamirs.boot.base.proxy.AdvancedHomePageModelProxy;
import pro.shushi.pamirs.boot.base.proxy.AdvancedHomeUeModuleProxy;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * @author: Wuxin
 * @createTime: 2024/05/21 18:11
 */
@Model(displayName = "首页配置规则")
@Model.model(HomePageConfigRules.MODEL_MODEL)
public class HomePageConfigRules extends TransientModel {

    public static final String MODEL_MODEL = "base.HomePageConfigRules";

    @Field.String
    @Field(displayName = "规则编码")
    private String code;


    @Field(displayName = "表达式解析", summary = "用于前端交互,表达式的json解析")
    @Field.Text
    private String expressionJson;

    @Field.String
    @Field(displayName = "规则名称")
    private String ruleName;

    @Field.Text
    @Field(displayName = "过滤条件", required = true)
    private String expression;

    @Field(displayName = "绑定类型")
    private BindingTypeEnum bindingType;

    @Field.many2one
    @Field(displayName = "绑定应用", summary = "绑定首页时用于下拉选择模型")
    private AdvancedHomeUeModuleProxy bindHomePageModule;


    @Field.many2one
    @Field(displayName = "绑定首页菜单")
    private AdvancedHomePageMenuProxy bindHomePageMenu;


    @Field.many2one
    @Field(displayName = "绑定首页模型", summary = "绑定首页时用于下拉选择模型")
    private AdvancedHomePageModelProxy bindHomePageModel;

    @Field.many2one
    @Field(displayName = "绑定首页页面")
    private View bindHomePageView;


    @Field(displayName = "是否开启")
    private Boolean enabled;

}
