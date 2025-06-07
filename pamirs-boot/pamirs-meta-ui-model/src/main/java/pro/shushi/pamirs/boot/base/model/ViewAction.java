package pro.shushi.pamirs.boot.base.model;

import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.enmu.QueryModeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.*;
import pro.shushi.pamirs.meta.util.DiffUtils;

import java.util.List;

import static pro.shushi.pamirs.boot.base.model.ViewAction.MODEL_MODEL;

@MetaSimulator(onlyBasicTypeField = false)
@MetaModel(priority = 18)
@Base
@Model.model(MODEL_MODEL)
@Model.MultiTableInherited(type = "VIEW")
@Model.Advanced(name = "viewAction", index = {"module"}, unique = {"model, name"}, priority = 21)
@Model(displayName = "窗口动作", summary = "窗口动作", labelFields = "displayName")
public class ViewAction extends Action {

    private static final long serialVersionUID = 4306605549245495521L;

    public final static String MODEL_MODEL = "base.ViewAction";

    @Base
    @Field(required = true)
    private String model;

    @Base
    @Field.String
    @Field(required = true, displayName = "api名称")
    private String name;

    @Base
    @Field.String
    @Field(displayName = "页面标题", translate = true)
    private String title;

    @Base
    @Field.Enum
    @Field(displayName = "类型", defaultValue = "VIEW")
    private ActionTypeEnum actionType;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "resModel", referenceFields = "model")
    @Field(displayName = "目标模型", required = true)
    private ModelDefinition resModelDefinition;

    @Base
    @Field(required = true, invisible = true)
    private String resModel;

    @Base
    @Field.Related(related = {"resModelDefinition", "name"})
    @Field
    private String resModelName;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "module")
    @Field(displayName = "源模块", required = true)
    private ModuleDefinition moduleDefinition;

    @Base
    @Field(required = true, invisible = true)
    private String module;

    @Base
    @Field.Related(related = {"moduleDefinition", "name"})
    @Field
    private String moduleName;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "resModule", referenceFields = {"module"})
    @Field(displayName = "目标模块")
    private ModuleDefinition resModuleDefinition;

    @Base
    @Field(invisible = true)
    private String resModule;

    @Base
    @Field.Related(related = {"resModuleDefinition", "name"})
    @Field
    private String resModuleName;

    @Base
    @Field.Enum
    @Field(displayName = "视图数据类型", required = true)
    private DataContainerTypeEnum dataType;

    @Base
    @Field.Enum
    @Field(displayName = "视图类型", summary = "打开目标模型后，默认打开的视图类型", required = true)
    private ViewTypeEnum viewType;

    @Base
    @Field.Enum
    @Field(displayName = "打开方式", required = true)
    private ActionTargetEnum target;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"resModel", "resViewName"}, referenceFields = {"model", "name"})
    @Field(displayName = "目标视图", summary = "动作视图跳转目标模型的指定视图")
    private View resView;

    @Base
    @Field(invisible = true)
    private String resViewName;

    @Base
    @Field.Enum
    @Field(displayName = "可选视图类型", summary = "打开目标模型后，可支持切换的视图类型", multi = true)
    private List<ViewTypeEnum> optionViewTypes;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = {"resModel", "optionViewNames"}, referenceFields = {"model", "name"})
    @Field(displayName = "可选视图列表")
    private List<View> optionViewList;

    @Base
    @Field(displayName = "可选视图名称列表")
    private List<String> optionViewNames;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "mask", referenceFields = "name")
    @Field(displayName = "母版")
    private MaskDefinition maskDefinition;

    @Base
    @Field(displayName = "母版名", summary = "母版api名称")
    private String mask;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "theme", referenceFields = "name")
    @Field(displayName = "主题")
    private ThemeDefinition themeDefinition;

    @Base
    @Field(displayName = "主题名", summary = "主题api名称")
    private String theme;

    @Base
    @Validation(check = checkRsqlExpression)
    @Field.Text
    @Field.Advanced(columnDefinition = "varchar(2048)")
    @Field(invisible = true)
    private String domain;

    @Base
    @Field(displayName = "数据加载方式", invisible = true)
    private QueryModeEnum queryMode;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"resModel", "load"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "数据加载函数")
    private FunctionDefinition loadFunction;

    @Base
    @Field.String
    @Field(displayName = "数据加载函数编码", invisible = true)
    private String load;

    @Base
    @Validation(check = checkRsqlExpression)
    @Field.Text
    @Field(invisible = true)
    private String filter;

    @Base
    @Field.Integer
    @Field(displayName = "限制", summary = "限制")
    private Integer limit;

    @Base
    @Field(displayName = "是否编译视图", summary = "编译视图指令", store = NullableBoolEnum.FALSE)
    private Boolean needCompileView;

    @Base
    @Field(displayName = "是否编译母版", summary = "编译母版指令", store = NullableBoolEnum.FALSE)
    private Boolean needCompileMask;

    @Base
    @Field(displayName = "是否返回已安装基础应用", summary = "是否返回已安装基础应用", store = NullableBoolEnum.FALSE)
    private Boolean needBaseModules;

    @Base
    @Field(displayName = "使用默认首页", store = NullableBoolEnum.FALSE)
    private Boolean defaultHomePage;

    @Function(openLevel = FunctionOpenEnum.API)
    @Function.Advanced(displayName = "初始化数据", type = FunctionTypeEnum.QUERY)
    public ViewAction construct(ViewAction data) {
        data.setActionType(ActionTypeEnum.VIEW);
        data = (ViewAction) super.construct(data);
        return data;
    }

    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }

    @Override
    public String stringify() {
        return DiffUtils.stringify(this, "modelDefinition", "resModelDefinition", "bindingView",
                "moduleDefinition", "moduleName", "resModuleDefinition", "resModuleName", "resView", "optionViewList",
                "loadFunction");
    }

}
