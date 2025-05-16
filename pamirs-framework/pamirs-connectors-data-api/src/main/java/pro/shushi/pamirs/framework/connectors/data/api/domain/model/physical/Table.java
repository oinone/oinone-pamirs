package pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

/**
 * 表结构
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 */
@Base(SystemSourceEnum.KERNEL)
@Model.Advanced(type = ModelTypeEnum.TRANSIENT, priority = 50)
@Model.model(Table.MODEL_MODEL)
@Model("数据表定义")
public class Table extends TransientModel {

    public final static String MODEL_MODEL = "system.Table";
    private static final long serialVersionUID = 8300531436931684249L;

    @Field
    private String tableSchema;

    @Field
    private String tableName;

    @Field
    private String tableComment;

    @Field
    private String tableCollation;

    @Field
    private String characterSetName;

}
