package pro.shushi.pamirs.boot.base.model;

import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.domain.fun.ExtPoint;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.util.DiffUtils;

import java.util.List;

/**
 * 动作定义
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@MetaSimulator(onlyBasicTypeField = false)
@MetaModel(priority = 9)
@Base
@Model.MultiTableInherited(type = "SERVER")
@Model.Advanced(name = "serverAction", unique = {"model,name"}, priority = 20)
@Model.model(ServerAction.MODEL_MODEL)
@Model(displayName = "服务器动作", summary = "服务器动作", labelFields = "displayName")
public class ServerAction extends Action {

    private static final long serialVersionUID = 8895103002746581772L;

    public static final String MODEL_MODEL = "base.ServerAction";

    @Base
    @Field.Enum
    @Field(displayName = "类型", defaultValue = "SERVER", invisible = true)
    private ActionTypeEnum actionType;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"model", "fun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "函数", required = true, invisible = true)
    private FunctionDefinition functionDefinition;

    @Base
    @Field.String
    @Field(displayName = "函数编码", required = true, invisible = true)
    private String fun;

    @Base
    @Field.Related(related = {"functionDefinition", "extPointList"})
    @Field(displayName = "扩展点", invisible = true)
    private List<ExtPoint> extPointList;

    @Function(openLevel = FunctionOpenEnum.API)
    @Function.Advanced(displayName = "初始化数据", type = FunctionTypeEnum.QUERY)
    public ServerAction construct(ServerAction data) {
//        if (null == data || null == data.getFunctionDefinition()) {
//            return data;
//        }
//        FunctionDefinition functionDefinition = data.getFunctionDefinition();
//        if (StringUtils.isBlank(functionDefinition.getDisplayName())) {
//            functionDefinition.setDisplayName(data.getDisplayName());
//        }
//        if (StringUtils.isBlank(functionDefinition.getName())) {
//            functionDefinition.setName(data.getName());
//        }
//        if (CollectionUtils.isEmpty(functionDefinition.getArgumentList())) {
//            functionDefinition.setArgumentList(new ArrayList<>(Lists.newArrayList((Argument) new Argument()
//                    .setName(VariableNameConstants.model)
//                    .setLtype(HashMap.class.getName())
//                    .setTtype(TtypeEnum.M2O)
//                    .setModel(data.getModel()))));
//        }
//        if (null == functionDefinition.getReturnType()) {
//            functionDefinition.setReturnType(new Type()
//                    .setLtype(HashMap.class.getName())
//                    .setTtype(TtypeEnum.M2O)
//                    .setModel(data.getModel()));
//        }
        return data;
    }

    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }

    @Override
    public String stringify() {
        return DiffUtils.stringify(this, "functionDefinition", "modelDefinition", "bindingView");
    }

}
