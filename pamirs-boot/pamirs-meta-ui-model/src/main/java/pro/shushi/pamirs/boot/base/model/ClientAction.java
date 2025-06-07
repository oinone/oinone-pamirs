package pro.shushi.pamirs.boot.base.model;

import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.util.DiffUtils;

/**
 * 客户端函数
 * <p>
 * name以"__"开头
 * <p>
 * 2021/5/26 12:22 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@MetaSimulator(onlyBasicTypeField = false)
@MetaModel(priority = 20)
@Base
@Model.MultiTableInherited(type = "CLIENT")
@Model.Advanced(unique = "model, name", priority = 23)
@Model.model(ClientAction.MODEL_MODEL)
@Model(displayName = "客户端动作", summary = "客户端动作", labelFields = "displayName")
public class ClientAction extends Action {

    private static final long serialVersionUID = -3121702676078082403L;

    public final static String MODEL_MODEL = "base.ClientAction";

    @Base
    @Field.Enum
    @Field(displayName = "类型", defaultValue = "CLIENT")
    private ActionTypeEnum actionType;

    @Base
    @Field(displayName = "客户端函数编码")
    private String fun;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"model", "compute"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "计算函数")
    private FunctionDefinition computeFunction;

    @Base
    @Field(displayName = "计算函数编码")
    private String compute;


    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }

    @Override
    public String stringify() {
        return DiffUtils.stringify(this,
                "modelDefinition", "bindingView");
    }

}
