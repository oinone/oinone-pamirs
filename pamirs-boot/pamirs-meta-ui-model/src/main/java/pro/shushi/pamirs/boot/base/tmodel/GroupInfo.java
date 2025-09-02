package pro.shushi.pamirs.boot.base.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

/**
 * @author Gesi at 15:46 on 2025/9/1
 */
@Model(displayName = "分组信息")
@Model.model(GroupInfo.MODEL_MODEL)
public class GroupInfo extends TransientModel {

    public static final String MODEL_MODEL = "base.GroupInfo";

    @Field(displayName = "字段名")
    private String field;

    @Field(displayName = "当前分组数据统计值", summary = "转换成字符串")
    private String dataStatisticStr;

    /**
     * 当前分组数据统计值
     */
    private Object dataStatistic;

    @Field(displayName = "当前分组的所有值", summary = "转换成Json字符串")
    private String valuesJson;

    /**
     * 当前分组的所有值
     */
    private List<Object> values;

    @Field(displayName = "下一级分组信息")
    private List<GroupInfo> groups;

}
