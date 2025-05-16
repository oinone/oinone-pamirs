package pro.shushi.pamirs.core.common.business.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;


@Model.model(ShowPartners.MODEL_MODEL)
@Model(displayName = "所有公司信息",summary = "所有公司信息")
public class ShowPartners extends TransientModel {

    public static final String MODEL_MODEL = "core.common.ShowPartners";

    @Field(displayName = "当前公司信息")
    private CurrentPartner partner;

    @Field(displayName = "公司列表")
    @Field.one2many
    private List<CurrentPartner> partnerList;
}
