package pro.shushi.pamirs.user.api.model.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.resource.api.model.ResourceLang;

import java.util.List;

/**
 * @author shier
 * date 2019-07-26
 */
@Base
@Model
@Model.Advanced(name = "topBarLangTransientModel")
@Model.model(TopBarLangTransientModel.MODEL_MODEL)
public class TopBarLangTransientModel extends TransientModel {

    public static final String MODEL_MODEL = "user.TopBarLangTransientModel";

    @Field.Integer
    @Field(store = NullableBoolEnum.FALSE, displayName = "选中的语言包")
    Long id;

    @Field.one2many
    @Field(store = NullableBoolEnum.FALSE, displayName = "lang列表")
    List<ResourceLang> langList;

}
