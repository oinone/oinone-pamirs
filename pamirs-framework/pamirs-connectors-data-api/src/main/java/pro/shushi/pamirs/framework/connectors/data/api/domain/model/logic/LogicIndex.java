package pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic;

import pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.Index;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import java.util.List;

/**
 * 索引
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 */
@Base(SystemSourceEnum.KERNEL)
@Model.Advanced(type = ModelTypeEnum.TRANSIENT, priority = 54)
@Model.model(LogicIndex.MODEL_MODEL)
@Model("逻辑索引定义")
public class LogicIndex extends TransientModel {

    public final static String MODEL_MODEL = "system.LogicIndex";
    private static final long serialVersionUID = 9144893702901251379L;

    @Field
    private String tableName;

    @Field
    private String indexName;

    @Field
    private List<String> column;

    @Field
    private List<Index> indexList;

    @Field
    private Boolean unique;

}
