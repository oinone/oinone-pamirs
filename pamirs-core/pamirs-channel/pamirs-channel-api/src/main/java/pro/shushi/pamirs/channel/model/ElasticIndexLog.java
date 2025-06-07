package pro.shushi.pamirs.channel.model;


import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.Date;

/**
 * ElasticIndexLog
 *
 * @author yakir on 2020/04/16 17:55.
 */
@Model.model("channel.ElasticIndexLog")
@Model.Advanced(type = ModelTypeEnum.STORE, name = "elasticIndexLog")
@Model(displayName = "Elastic索引日志", summary = "Elastic索引")
public class ElasticIndexLog extends IdModel {

    private static final long serialVersionUID = -2100023887402045696L;

    @Field.String
    @Field(displayName = "租户", invisible = true)
    private String tenant;

    @Field.String
    @Field(displayName = "索引")
    private String index;

    @Field.String
    @Field(displayName = "别名")
    private String alias;

    @Field.String
    @Field(displayName = "下标")
    private Integer pos;

    @Field.String
    @Field(displayName = "分片")
    private String shards;

    @Field.String
    @Field(displayName = "副本")
    private String replicas;

    @Field.Integer
    @Field(displayName = "最大ID")
    private Long idMax;

    @Field.Integer
    @Field(displayName = "最小ID")
    private Long idMin;

    @Field.Integer
    @Field(displayName = "总数")
    private Long idCount;

    @Field.Date
    @Field(displayName = "索引创建时间")
    private Date cdate;

}