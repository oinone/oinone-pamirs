package pro.shushi.pamirs.framework.connectors.data.infrastructure.test.mock.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import static pro.shushi.pamirs.framework.connectors.data.infrastructure.test.mock.model.TestLogicTable.TABLE_NAME;

/**
 * 表结构
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 */
@Base(SystemSourceEnum.KERNEL)
@Model.Static
@Model.Advanced(table = TABLE_NAME)
@Model.Persistence(capitalMode = true)
@Model.model(TestLogicTable.MODEL_MODEL)
@Model
public class TestLogicTable extends IdModel {

    public final static String MODEL_MODEL = "logic.TestLogicTable";
    public final static String TABLE_NAME = "PAMIRS_LOGIC_TABLES";
    private static final long serialVersionUID = -6388657208996560063L;

    @Field(index = true)
    private String module;

    @Field(unique = true)
    private String model;

    @Field
    private String tableName;

    @Field
    private String tableComment;

}
