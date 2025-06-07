package pro.shushi.pamirs.framework.connectors.data.infrastructure.test.mock.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

/**
 * 索引
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 */
@Base(SystemSourceEnum.KERNEL)
@Model.Static
@Model.Advanced(table = "PAMIRS_LOGIC_INDEXES", unique = "model,indexName,columnName")
@Model.Persistence(capitalMode = true)
@Model.model(TestLogicIndex.MODEL_MODEL)
@Model
public class TestLogicIndex extends IdModel {

    public final static String MODEL_MODEL = "logic.TestLogicIndex";
    private static final long serialVersionUID = -3786789242041329275L;

    @Field(index = true)
    private String module;

    @Field
    private String model;

    @Field
    private String indexName;

    @Field
    private String columnName;

    @Field
    private Integer seqInIndex;

    @Field
    private Boolean nonUnique;

}
