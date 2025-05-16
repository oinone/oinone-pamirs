package pro.shushi.pamirs.meta.domain.fun;

import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;

import static pro.shushi.pamirs.meta.domain.fun.InterfaceDefinition.MODEL_MODEL;

/**
 * 接口定义
 * <p>
 * 2021/7/6 1:22 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@MetaSimulator(onlyBasicTypeField = false)
@MetaModel(30)
@Base
@Model.ChangeTableInherited
@Model.Advanced(index = {"sys", "systemSource"}, priority = 15)
@Model.model(MODEL_MODEL)
@Model(displayName = "接口", summary = "接口", labelFields = {"displayName", "name"})
public class InterfaceDefinition extends FunctionDefinition {

    private static final long serialVersionUID = 8160482667206554954L;

    public static final String MODEL_MODEL = "base.Interfaces";

    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }

}
