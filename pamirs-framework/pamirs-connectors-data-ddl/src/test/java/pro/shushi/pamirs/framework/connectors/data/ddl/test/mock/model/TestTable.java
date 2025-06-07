package pro.shushi.pamirs.framework.connectors.data.ddl.test.mock.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

/**
 * 表结构
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 */
@Base(SystemSourceEnum.KERNEL)
@Model.Static
@Model.Advanced(table = "PAMIRS_TABLES", unique = "tableName,tableSchema")
@Model.Persistence(capitalMode = true)
@Model.model(TestTable.MODEL_MODEL)
@Model
public class TestTable extends IdModel {

    public final static String MODEL_MODEL = "system.TestTable";
    private static final long serialVersionUID = -6388657208996560063L;

    @Field(index = true)
    private String tableSchema;

    @Field
    private String tableName;

    @Field
    private String tableComment;

    @Field(index = true)
    private String model;

}
