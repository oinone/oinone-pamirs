package pro.shushi.pamirs.auth.view.pmodel;

import pro.shushi.pamirs.auth.view.model.AuthResourcePermissionItem;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * 资源访问权限项
 *
 * @author Adamancy Zhang at 21:27 on 2024-02-02
 */
@Base
@Model.model(AuthAccessResourceItem.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
@Model(displayName = "资源访问权限项", labelFields = {"displayName"})
public class AuthAccessResourceItem extends AuthResourcePermissionItem {

    private static final long serialVersionUID = 7912691096720509801L;

    public static final String MODEL_MODEL = "auth.AuthAccessResourceItem";
}
