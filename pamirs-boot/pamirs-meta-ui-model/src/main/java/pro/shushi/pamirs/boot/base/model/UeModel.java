package pro.shushi.pamirs.boot.base.model;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.util.DiffUtils;

import java.util.List;

import static pro.shushi.pamirs.boot.base.model.UeModel.MODEL_MODEL;

/**
 * 前端模型定义
 *
 * @author d@shushi.pro
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
//@MetaSimulator(onlyBasicTypeField = false)
//@MetaModel(priority = 15)
@Base
@Model.Advanced(name = "model")
@Model.model(MODEL_MODEL)
@Model(displayName = "视图模型", summary = "模型", labelFields = "displayName")
public class UeModel extends ModelDefinition {

    private static final long serialVersionUID = -4411768930484473628L;

    public static final String MODEL_MODEL = UE_MODEL_MODEL;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = {"model", "labelFields"}, referenceFields = {"model", "name"}, domain = "model=eq=@{model}")
    @Field(displayName = "数据标题字段列表")
    private List<ModelField> labelFieldList;

    @Field.one2many
    @Field.Relation(relationFields = {"model"})
    @Field(displayName = "视图引用")
    private List<View> viewList;

    @Field.one2many(pageSize = 100)
    @Field.Relation(relationFields = {"model"})
    @Field(displayName = "窗口动作")
    private List<ViewAction> viewActionList;

    @Field.one2many(pageSize = 100)
    @Field.Relation(relationFields = "model")
    @Field(displayName = "服务器动作")
    private List<ServerAction> serverActionList;

    @Field.one2many
    @Field.Relation(relationFields = "model")
    @Field(displayName = "URL动作")
    private List<UrlAction> urlActionList;

    @Field.one2many
    @Field.Relation(relationFields = "model")
    @Field(displayName = "客户端动作")
    private List<ClientAction> clientActionList;

    @Function(openLevel = FunctionOpenEnum.API)
    @Function.Advanced(displayName = "初始化数据", type = FunctionTypeEnum.QUERY)
    public UeModel construct(UeModel data) {
        super.construct(data);

        if (null != data && null == data.getLabelFields() && !CollectionUtils.isEmpty(data.getPk())) {
            data.setLabelFields(data.getPk());
        }
        return data;
    }
}
