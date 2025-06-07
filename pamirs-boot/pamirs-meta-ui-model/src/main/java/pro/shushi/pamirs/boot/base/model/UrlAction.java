package pro.shushi.pamirs.boot.base.model;

import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.util.DiffUtils;

@MetaSimulator(onlyBasicTypeField = false)
@MetaModel(priority = 19)
@Base
@Model.MultiTableInherited(type = "URL")
@Model.Advanced(unique = "model, name", priority = 22)
@Model.model(UrlAction.MODEL_MODEL)
@Model(displayName = "URL动作", summary = "URL动作", labelFields = "displayName")
public class UrlAction extends Action {

    private static final long serialVersionUID = -3721415544552404634L;

    public final static String MODEL_MODEL = "base.UrlAction";

    @Base
    @Field.Enum
    @Field(displayName = "类型", defaultValue = "URL")
    private ActionTypeEnum actionType;

    @Base
    @Field.Enum
    @Field(displayName = "打开方式", required = true)
    private ActionTargetEnum target;

    @Base
    @Field.Text
    @Field(displayName = "URL", required = true, defaultValue = CharacterConstants.SEPARATOR_OCTOTHORPE)
    private String url;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"model", "compute"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "链接计算函数")
    private FunctionDefinition computeFunction;

    @Base
    @Field.String
    @Field(displayName = "链接计算函数编码", invisible = true)
    private String compute;

    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }

    @Override
    public String stringify() {
        return DiffUtils.stringify(this, "modelDefinition", "bindingView", "computeFunction");
    }

}
