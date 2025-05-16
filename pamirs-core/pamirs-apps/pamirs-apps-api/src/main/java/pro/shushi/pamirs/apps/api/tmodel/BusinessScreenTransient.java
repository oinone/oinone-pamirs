package pro.shushi.pamirs.apps.api.tmodel;

import pro.shushi.pamirs.apps.api.pmodel.AppsModuleCategoryProxy;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.List;

@Base
@Model.Advanced(type = ModelTypeEnum.TRANSIENT)
@Model.model(BusinessScreenTransient.MODEL_MODEL)
@Model(displayName = "业务大屏")
public class BusinessScreenTransient extends TransientModel {

    public static final String MODEL_MODEL = "apps.model.BusinessScreenTransient";

    @Field.one2many
    @Field(displayName = "分类")
    private List<AppsModuleCategoryProxy> categories;

    //todo 历史字段,和前端对接时再删
    @Deprecated
    @Field.Integer
    @Field(displayName = "传递")
    private Long id;

}
