package pro.shushi.pamirs.resource.api.tmodel.lang;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.Map;

/**
 * @author WuXin at 14:18 on 2024/11/1
 */
@Model(displayName = "时间格式")
@Model.model(ResourceTimeFormat.MODEL_MODEL)
public class ResourceTimeFormat extends TransientModel {

    public static final String MODEL_MODEL = "resource.ResourceTimeFormat";

    @Field.String
    @Field(displayName = "标准冒号格式", summary = "HH:mm:ss")
    private String colonNormal;

    @Field.String
    @Field(displayName = "标准冒号毫秒格式", summary = "HH:mm:ss.SSS")
    private String colonNormalSss;

    @Field.String
    @Field(displayName = "简写冒号格式", summary = "HH:mm")
    private String colonShort;

    @Field.String
    @Field(displayName = "标准冒号格式（AP样式）", summary = "A hh:mm:ss")
    private String apColonNormal;

    @Field.String
    @Field(displayName = "标准冒号毫秒格式（AP样式）", summary = "A hh:mm:ss.SSS")
    private String apColonNormalSss;

    @Field.String
    @Field(displayName = "简写冒号格式（AP样式）", summary = "A hh:mm")
    private String apColonShort;

    @Field(displayName = "标准冒号格式", summary = "前端展示字段")
    private Map<String, Object> colonNormalMap;

    @Field(displayName = "标准冒号格式", summary = "前端展示字段")
    private Map<String, Object> colonNormalSssMap;

    @Field(displayName = "标准冒号格式", summary = "前端展示字段")
    private Map<String, Object> colonShortMap;

    @Field(displayName = "标准冒号格式（AP样式）", summary = "前端展示字段")
    private Map<String, Object> apColonNormalMap;

    @Field(displayName = "标准冒号格式（AP样式）", summary = "前端展示字段")
    private Map<String, Object> apColonNormalSssMap;

    @Field(displayName = "简写冒号格式（AP样式）", summary = "前端展示字段")
    private Map<String, Object> apColonShortMap;

}