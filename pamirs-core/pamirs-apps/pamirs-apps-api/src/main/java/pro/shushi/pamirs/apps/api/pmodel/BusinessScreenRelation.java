package pro.shushi.pamirs.apps.api.pmodel;

import pro.shushi.pamirs.apps.api.tmodel.ModuleRelationPath;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.ArrayList;
import java.util.List;

@Base
@Model.Advanced(type = ModelTypeEnum.PROXY)
@Model.model(BusinessScreenRelation.MODEL_MODEL)
@Model(displayName = "模块依赖、互斥、被依赖关系")
public class BusinessScreenRelation extends UeModule {

    public static final String MODEL_MODEL = "apps.model.BusinessScreenRelation";

    /**
     * 当前Module依赖的Module列表
     */
    private List<BusinessScreenRelation> downRelationList;

    /**
     * 依赖当前Module的Module列表
     */
    private List<BusinessScreenRelation> upRelationList;

    //点和线
    @Field.one2many
    @Field(displayName = "依赖模块列表")
    private List<UeModule> downNodes;

    @Field.one2many
    @Field(displayName = "依赖模块列表")
    private List<ModuleRelationPath> downPaths;

    @Field.one2many
    @Field(displayName = "依赖模块列表")
    private List<UeModule> upNodes;

    @Field.one2many
    @Field(displayName = "依赖模块列表")
    private List<ModuleRelationPath> upPaths;

    public void addDownRelation(BusinessScreenRelation relation) {
        if (getDownRelationList() == null) {
            setDownRelationList(new ArrayList<>());
        }
        //直接比对象
        if (!getDownRelationList().contains(relation)) {
            getDownRelationList().add(relation);
        }
    }

    public void addUpRelation(BusinessScreenRelation relation) {
        if (getUpRelationList() == null) {
            setUpRelationList(new ArrayList<>());
        }
        //直接比对象
        if (!getUpRelationList().contains(relation)) {
            getUpRelationList().add(relation);
        }
    }
}
