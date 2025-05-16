package pro.shushi.pamirs.channel.model;

import pro.shushi.pamirs.channel.enmu.DumpStateEnum;
import pro.shushi.pamirs.channel.enmu.IncrementEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * ChannelModel
 *
 * @author yakir on 2020/04/17 14:33.
 */
@Base
@Model.model(ChannelModel.MODEL_MODEL)
@Model(displayName = "传输增强模型", summary = "传输增强模型")
@Model.Advanced(type = ModelTypeEnum.STORE, name = "channelModel",
        unique = {"model", "index"}, index = {"origin"})
public class ChannelModel extends IdModel {

    private static final long serialVersionUID = 9154684492002454659L;

    public static final String MODEL_MODEL = "channel.ChannelModel";

    @Field.String
    @Field(displayName = "租户", invisible = true)
    private String tenant;

    @Field.String
    @Field(displayName = "增强模型所在模块")
    private String module;

    @Field.String
    @Field(displayName = "增强模型名称")
    private String model;

    @Field.String
    @Field(displayName = "被增强模型")
    private String origin;

    @Field.String
    @Field(displayName = "增强模型显示名称")
    private String displayName;

    /**
     * 生成规则
     *
     * <pre>
     *     Lowercase only
     *     Cannot include \, /, *, ?, ", <, >, |, ` ` (space character), ,, #,:
     *     Cannot start with -, _, +
     *     Cannot be . or ..
     *     Cannot be longer than 255 bytes (note it is bytes, so multi-byte characters will count towards the 255 limit faster)
     * </pre>
     *
     * <code>
     * String tenant = "pamirs";
     * String naming = "yakir-foo_search";
     * String index  = IndexNaming.index(tenant, model, 1);
     * Truth.assertThat(index).isEqualTo("pamirs__yakir-foo_search+1");
     * </code>
     * <code>
     * String tenant = "pamirs-test";
     * String naming = "yakir-foo_search";
     * String index  = IndexNaming.index(tenant, model, 1);
     * Truth.assertThat(index).isEqualTo("pamirs-test__yakir-foo_search+1");
     * </code>
     * <p>
     * 以上两段代码中的naming就是实际应该配置的naming,index不做自动生成，需要tenant的信息往前追加，索引游标往后追加
     */
    @Field.String
    @Field(displayName = "增强模型标识")
    private String naming;

    @Field.String
    @Field(displayName = "别名")
    private String alias;

    @Field.String
    @Field(displayName = "索引")
    private String index;

    @Field.Boolean
    @Field(displayName = "迁移别名", summary = "全量同步之后是否迁移别名", defaultValue = "true")
    private Boolean reAlias;

    @Field.Integer
    @Field(displayName = "索引后缀", summary = "索引后缀", defaultValue = "1")
    private Integer pos;

    @Field.Text(max = "65535")
    @Field(displayName = "索引映射", summary = "Elasitcsearch Index Mapping")
    private String mapping;

    @Field.Integer
    @Field(displayName = "全量单批次容量", summary = "全量同步单批次容量")
    private Long batchSize;

    @Field.String
    @Field(displayName = "分片数", summary = "分片数")
    private String shards;

    @Field.String
    @Field(displayName = "副本数", summary = "副本数")
    private String replicas;

    @Field.Date
    @Field.Advanced
    @Field(displayName = "全量同步成功时间", summary = "最后一次全量同步成功时间")
    private Date lastSync;

    @Field.Enum
    @Field(displayName = "增强模型创建方式")
    private SystemSourceEnum systemSource;

    @Field.Enum
    @Field(displayName = "同步状态", summary = "全量同步状态", defaultValue = "INIT")
    private DumpStateEnum dumpState;

    @Field.Enum
    @Field(displayName = "是否开启增量同步", summary = "是否开启增量同步", defaultValue = "CLOSE")
    private IncrementEnum increment;

    @Field(displayName = "分词器", multi = true, serialize = Field.serialize.JSON, store = NullableBoolEnum.TRUE)
    @Field.Advanced(columnDefinition = "varchar(4096)")
    private List<Map<String, String>> analyzers;

}
