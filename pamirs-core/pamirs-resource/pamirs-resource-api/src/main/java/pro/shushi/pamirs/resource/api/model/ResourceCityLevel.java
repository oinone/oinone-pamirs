package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.common.CodeModel;
import pro.shushi.pamirs.resource.api.enmu.CityLevelEnum;

import java.util.List;

/**
 * @author drome
 * @date 2021/4/2210:30 下午
 */
@Model.model(ResourceCityLevel.MODEL_MODEL)
@Model(displayName = "城市等级", labelFields = "name")
public class ResourceCityLevel extends CodeModel {
    public static final String MODEL_MODEL = "resource.ResourceCityLevel";

    @Field.Enum
    @Field(displayName = "城市等级", required = true, summary = "城市等级")
    private CityLevelEnum cityLevel;

    @Base
    @Field.String
    @Field(displayName = "城市等级编码", unique = true, required = true, summary = "城市等级编码")
    private String code;

    @Field.String
    @Field(displayName = "城市等级名称", invisible = true, summary = "城市等级名称")
    private String name;

    @Field.many2many(through = "CityLevelRelCity")
    @Field(displayName = "市列表")
    private List<ResourceCity> cityList;
}
