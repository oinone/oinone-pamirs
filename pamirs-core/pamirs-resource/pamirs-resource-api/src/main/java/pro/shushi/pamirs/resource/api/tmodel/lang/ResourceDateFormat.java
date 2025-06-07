package pro.shushi.pamirs.resource.api.tmodel.lang;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.Map;

/**
 * @author WuXin at 14:11 on 2024/11/1
 */
@Model(displayName = "日期格式")
@Model.model(ResourceDateFormat.MODEL_MODEL)
public class ResourceDateFormat extends TransientModel {

    public static final String MODEL_MODEL = "resource.ResourceDateFormat";

    @Field.String
    @Field(displayName = "中文格式", summary = "YYYY年MM月DD日")
    private String chinese;

    @Field.String
    @Field(displayName = "中文年月格式", summary = "YYYY年MM月")
    private String chineseYearMonth;

    @Field.String
    @Field(displayName = "连字符年月格式", summary = "YYYY-MM")
    private String hyphenYearMonth;

    @Field.String
    @Field(displayName = "斜杠年月格式", summary = "YYYY/MM")
    private String slashYearMonth;

    @Field.String
    @Field(displayName = "连字符", summary = "YYYY-MM-DD")
    private String hyphen;

    @Field.String
    @Field(displayName = "斜杠", summary = "YYYY/MM/DD")
    private String slash;

    @Field(displayName = "中文格式", summary = "前端展示字段")
    private Map<String, Object> chineseMap;

    @Field(displayName = "中文格式", summary = "前端展示字段")
    private Map<String, Object> chineseYearMonthMap;

    @Field(displayName = "连字符年月格式", summary = "前端展示字段")
    private Map<String, Object> hyphenYearMonthMap;

    @Field(displayName = "斜杠年月格式", summary = "前端展示字段")
    private Map<String, Object> slashYearMonthMap;

    @Field(displayName = "连字符", summary = "前端展示字段")
    private Map<String, Object> hyphenMap;

    @Field(displayName = "斜杠", summary = "前端展示字段")
    private Map<String, Object> slashMap;

}