package pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;


/**
 * 库结构
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 */
@Base(SystemSourceEnum.KERNEL)
@Model.Advanced(type = ModelTypeEnum.TRANSIENT, priority = 48)
@Model.model(Database.MODEL_MODEL)
@Model("数据库定义")
public class Database extends TransientModel {

    public final static String MODEL_MODEL = "system.Database";
    private static final long serialVersionUID = 1574479081412977202L;

    @Field
    private String schemaName;

    @Field
    private String characterSetName;

    @Field
    private String collationName;

}