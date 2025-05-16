package pro.shushi.pamirs.auth.view.model;

import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.boot.base.model.ClientAction;
import pro.shushi.pamirs.boot.base.model.ServerAction;
import pro.shushi.pamirs.boot.base.model.UrlAction;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * 自定义资源权限项
 *
 * @author Adamancy Zhang at 12:43 on 2024-08-09
 */
@Base
@Model.model(AuthCustomResourcePermissionItem.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
@Model(displayName = "自定义资源权限项")
public class AuthCustomResourcePermissionItem extends AuthResourcePermission {

    private static final long serialVersionUID = 487704304469283305L;

    public static final String MODEL_MODEL = "auth.AuthCustomResourcePermissionItem";

    @Field.many2one
    @Field.Relation(relationFields = {"module"}, referenceFields = {"module"})
    @Field(displayName = "资源模型")
    private ModuleDefinition moduleDefinition;

    @Field.many2one
    @Field.Relation(relationFields = {"model"}, referenceFields = {"model"})
    @Field(displayName = "资源模型")
    private ModelDefinition modelDefinition;

    @Field.many2one
    @Field.Relation(relationFields = {"model", "name"}, referenceFields = {"model", "name"})
    @Field(displayName = "服务器动作")
    private ServerAction serverAction;

    @Field.many2one
    @Field.Relation(relationFields = {"model", "name"}, referenceFields = {"model", "name"})
    @Field(displayName = "窗口动作")
    private ViewAction viewAction;

    @Field.many2one
    @Field.Relation(relationFields = {"model", "name"}, referenceFields = {"model", "name"})
    @Field(displayName = "URL动作")
    private UrlAction urlAction;

    @Field.many2one
    @Field.Relation(relationFields = {"model", "name"}, referenceFields = {"model", "name"})
    @Field(displayName = "客户端动作")
    private ClientAction clientAction;

    @Field.many2one
    @Field.Relation(relationFields = {"model", "name"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "函数")
    private FunctionDefinition functionDefinition;
}
