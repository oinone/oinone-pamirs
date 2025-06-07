package pro.shushi.pamirs.apps.api.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.List;

/**
 * AppCategoryModule
 *
 * @author yakir on 2022/11/28 19:51.
 */
@Base
@Model(displayName = "初始化租户应用列表")
@Model.model(AppCategoryModuleList.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.TRANSIENT)
public class AppCategoryModuleList extends TransientModel {

    private static final long serialVersionUID = -595077294279363034L;

    public final static String MODEL_MODEL = "apps.AppCategoryModuleList";

    @Field.String
    @Field(displayName = "分类名称")
    private String name;

    @Field.many2many
    @Field(displayName = "可选的应用")
    private List<AppCategoryModule> moduleList;

}

