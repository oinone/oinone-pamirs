package pro.shushi.pamirs.apps.api.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

/**
 * fixme pro.shushi.pamirs.welcome.api.tmodel.WelcomeModule
 */
@Base
@Model.model(TechniqueVi.MODEL_MODEL)
@Model(displayName = "Apps管理module")
public class TechniqueVi extends TransientModel {

    public static final String MODEL_MODEL = "apps.TechniqueVi";

    @Field.one2many
    @Field(displayName = "iPaas")
    private List<TechniqueViValue> iPaaSList;

    @Field.one2many
    @Field(displayName = "uiPaaS")
    private List<TechniqueViValue> uiPaaSList;

    @Field.one2many
    @Field(displayName = "hpaPaaS")
    private List<TechniqueViValue> hpaPaaSList;

    @Field.one2many
    @Field(displayName = "aPaaS")
    private List<TechniqueViValue> aPaaSList;
}
