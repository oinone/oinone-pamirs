package pro.shushi.pamirs.dev.tools.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

@Model.model(RedisInfo.MODEL_MODEL)
@Model(displayName = "redis信息")
public class RedisInfo extends TransientModel {

    private static final long serialVersionUID = 4426820094920651523L;

    public static final String MODEL_MODEL = "tools.RedisInfo";

    @Field.String
    @Field(displayName = "host")
    private String host;

    @Field.Integer
    @Field(displayName = "database")
    private Integer database;

    @Field.Integer
    @Field(displayName = "端口")
    private Integer port;

    @Field.String
    @Field(displayName = "前缀")
    private String prefix;

}
