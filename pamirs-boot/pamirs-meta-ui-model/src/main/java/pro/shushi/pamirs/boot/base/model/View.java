package pro.shushi.pamirs.boot.base.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.meta.util.DiffUtils;

import java.util.Map;

@MetaSimulator(onlyBasicTypeField = false)
@MetaModel(22)
@Base
@Model.model(View.MODEL_MODEL)
@Model.Advanced(unique = "model,name", priority = 28)
@Model(displayName = "视图定义", labelFields = {"title"}, summary = "视图定义")
public class View extends AbstractView {

    private static final long serialVersionUID = 7877370032467047473L;

    public static final String MODEL_MODEL = "base.View";

    @Base
    @Field.Related(related = {"modelDefinition", "model"})
    @Field(displayName = "模型编码", required = true, invisible = true, store = NullableBoolEnum.TRUE)
    private String model;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "model")
    @Field(displayName = "模型", required = true)
    private ModelDefinition modelDefinition;

    @Base
    @Field(displayName = "视图名称", required = true)
    private String name;

    @Base
    @Field(displayName = "视图标题", translate = true)
    private String title;

    @Base
    @Field.String
    @Field(displayName = "简介", summary = "描述摘要", translate = true)
    private String summary;

    @Base
    @Field.Text
    @Field(displayName = "描述", summary = "描述详情", translate = true)
    private String description;

    @Base
    @Field(displayName = "分组")
    @Field.many2one
    @Field.Relation(relationFields = "categoryId", referenceFields = "id")
    private ViewCategory category;

    @Base
    @Field(displayName = "分组ID", index = true)
    @Field.Integer
    private Long categoryId;

    @Base
    @Field(displayName = "布局定义")
    @Field.many2one
    @Field.Relation(relationFields = {"baseLayoutName", "#view#"}, referenceFields = {"name", "layoutType"})
    private LayoutDefinition baseLayoutDefinition;

    @Base
    @Field.Relation(relationFields = {"model", "loadFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "数据源")
    private FunctionDefinition load;

    @Base
    @Field(defaultValue = "load")
    private String loadFun;

    @Base
    @Field(store = NullableBoolEnum.FALSE)
    private Map<String, Object> loadData;

    @Field(displayName = "扩展数据")
    private Map<String, String> extension;

    //fixme 依赖问题
    @JsonIgnore
    private Object uiView;

    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }

    @Override
    public String stringify() {
        return DiffUtils.stringify(this, "layoutDefinition", "loadLayout", "modelDefinition", "category", "baseLayoutDefinition", "load");
    }

    public static String sign(String model, String name) {
        return model + CharacterConstants.SEPARATOR_DOT + name;
    }

    public static String modelViewType(String model, String viewType) {
        return model + CharacterConstants.SEPARATOR_DOT + viewType;
    }

}
