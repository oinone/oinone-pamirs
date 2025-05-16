package pro.shushi.pamirs.channel.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

/**
 * ChannelField
 *
 * @author yakir on 2020/04/17 14:33.
 */
@Base
@Model.model("base.ChannelField")
@Model.Advanced(
        type = ModelTypeEnum.STORE,
        unique = {
                "channelModelId,rowName"
        }
)
@Model(displayName = "传输增强模型属性", summary = "传输增强模型属性")
public class ChannelField extends IdModel {

    private static final long serialVersionUID = 162011577924390770L;

    @Field.Integer
    @Field(displayName = "传输模型ID")
    private Long channelModelId;

    @Field.many2one
    @Field(displayName = "传输模型")
    @Field.Relation(relationFields = {"channelModelId"}, referenceFields = {"id"})
    private ChannelModel channelModel;

    @Field.String
    @Field(displayName = "属性名称")
    private String name;

    @Field.String
    @Field(displayName = "属性行名称")
    private String rowName;

    @Field.String
    @Field(displayName = "属性类型")
    private String type;

    @Field.Enum
    @Field(displayName = "属性行类型")
    private TtypeEnum rowType;

    @Field.String
    @Field(displayName = "属性关联模型")
    private String relation; // nested

    /**
     * {@link pro.shushi.pamirs.channel.constant.IkAnalyzer}
     */
    @Field.String
    @Field(displayName = "分词器")
    private String analyzer;

    /**
     * {@link pro.shushi.pamirs.channel.constant.IkSearchAnalyzer}
     */
    @Field.String
    @Field(displayName = "搜索分词器")
    private String ikSearchAnalyzer;
}
