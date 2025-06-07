package pro.shushi.pamirs.international.tmodel;


import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.resource.api.enmu.DirectionEnum;
import pro.shushi.pamirs.resource.api.enmu.TimeZoneTypeEnum;
import pro.shushi.pamirs.resource.api.enmu.WeekStartEnum;

@Model.model("resource.I18NDateConfig")
@Model(displayName = "日期配置")
public class I18NDateConfig extends TransientModel {

    @Field.Enum
    @Field(displayName = "时区")
    private TimeZoneTypeEnum timeZoneType;

    @Field.Enum
    @Field(displayName = "基准时区")
    private TimeZoneTypeEnum baseTimezoneType;

    @Field.Enum
    @Field(displayName = "书写方向")
    private DirectionEnum direction;

    @Field.String
    @Field(displayName = "日期格式")
    private String dateFormat;

    @Field.String
    @Field(displayName = "时间格式")
    private String timeFormat;

    @Field.Enum
    @Field(displayName = "一周开始的日期")
    private WeekStartEnum weekStart;
}
