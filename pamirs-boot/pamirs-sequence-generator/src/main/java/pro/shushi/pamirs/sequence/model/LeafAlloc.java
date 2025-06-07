package pro.shushi.pamirs.sequence.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.KeyGeneratorEnum;

import java.io.Serializable;

import static pro.shushi.pamirs.sequence.model.LeafAlloc.MODEL_MODEL;

/**
 * LeafAlloc
 *
 * @author yakir on 2020/04/08 15:10.
 * {@link pro.shushi.pamirs.meta.domain.model.SequenceConfig}
 */
@Base
@Model(displayName = "序列")
@Model.model(MODEL_MODEL)
@Model.Advanced(table = "leaf_alloc", unique = "code")
public class LeafAlloc extends IdModel implements Serializable {

    private static final long serialVersionUID = -7299449132736050031L;

    public static final String MODEL_MODEL = "base.LeafAlloc";

    @Base
    @Field.PrimaryKey(keyGenerator = KeyGeneratorEnum.AUTO_INCREMENT)
    @Field.Integer
    @Field(displayName = "ID", summary = "ID字段，唯一自增索引", required = true, priority = 5)
    protected Long id;

    /**
     * 启用归零周期后,周期值会拼接在code上,不同周期会有多条数据. 方便解决临界点并发问题
     */
    @Field(displayName = "序列标识", summary = "序列唯一标识", unique = true, required = true)
    @Field.String(size = 256)
    private String code;

    @Field.Advanced(columnDefinition = "BIGINT(20) DEFAULT '1'")
    @Field(displayName = "号段最大Id值", defaultValue = "1")
    private Long maxId;

    @Field.Advanced(columnDefinition = "BIGINT(20) DEFAULT '500'")
    @Field(displayName = "步长", defaultValue = "500")
    private Integer step;

    @Field(displayName = "序列更新时间")
    private String updateTime;

    @Field(displayName = "是否需要加载", summary = "true: 是, false: 否", defaultValue = "true")
    private Boolean needLoad;

}
