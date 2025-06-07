package pro.shushi.pamirs.resource.api.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.resource.api.model.*;

import java.util.List;

@Model.model(ResourceRegionProxyModel.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
@Model(displayName = "地区代理模型")
public class ResourceRegionProxyModel extends ResourceRegion {

    public static final String MODEL_MODEL = "resource.ResourceRegionProxyModel";

    @Field.Text
    @Field(displayName = "前端地址js地址")
    private String downloadUrl;


    /**
     * @Field.many2one
     * @Field.Relation(store = false)
     * @Field(displayName = "国家", store = NullableBoolEnum.FALSE)
     * private ResourceCountry country;
     * <p>
     * fixme zbh 以上代码将导致父模型的country字段不存储，需要review
     */
    @Field.many2one
    @Field.Relation(relationFields = {"countryCode"}, referenceFields = {"code"})
    @Field(displayName = "国家")
    private ResourceCountry country;

    @Field.many2one
    @Field.Relation(store = false)
    @Field(displayName = "省/州", store = NullableBoolEnum.FALSE)
    private ResourceProvince province;

    @Field.many2one
    @Field.Relation(store = false)
    @Field(displayName = "市", store = NullableBoolEnum.FALSE)
    private ResourceCity city;

    @Field.many2one
    @Field.Relation(store = false)
    @Field(displayName = "区", store = NullableBoolEnum.FALSE)
    private ResourceDistrict district;

    @Field.many2one
    @Field.Relation(store = false)
    @Field(displayName = "街道", store = NullableBoolEnum.FALSE)
    private ResourceStreet street;

    @Field.one2many
    @Field(displayName = "外部资源关联列表")
    @Field.Relation(relationFields = {"code", CharacterConstants.SEPARATOR_OCTOTHORPE + ResourceRegionProxyModel.MODEL_MODEL + CharacterConstants.SEPARATOR_OCTOTHORPE}, referenceFields = {"relationCode", "model"})
    private List<OutResourceRelation> outResourceRelationList;
}
