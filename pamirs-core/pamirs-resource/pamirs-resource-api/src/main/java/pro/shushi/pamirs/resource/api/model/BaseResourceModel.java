package pro.shushi.pamirs.resource.api.model;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.base.common.CodeModel;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.List;

@Model.MultiTable
@Model.model(BaseResourceModel.MODEL_MODEL)
@Model.Advanced(name = "BaseResourceModel", unique = "code")
@Model(displayName = "带outCode模型抽象基类", summary = "带outCode模型抽象基类")
public class BaseResourceModel extends CodeModel {

    private static final long serialVersionUID = -1894170650736494799L;

    public static final String MODEL_MODEL = "resource.BaseResourceModel";

    @Field.String
    @Field(displayName = "外部编码", store = NullableBoolEnum.FALSE)
    private String outCode;

    /**
     * 根据内部编码找到外部联系
     *
     * @param modelModel
     * @param sourceType
     * @param relationCode
     */
    public static OutResourceRelation fetchRelationByCode(String modelModel, String sourceType, String relationCode) {
        if (StringUtils.isBlank(modelModel)
                || StringUtils.isBlank(sourceType)
                || StringUtils.isBlank(relationCode)) {
            return null;
        }
        List<OutResourceRelation> outResourceRelations = Models.data()
                .queryListByEntity(new OutResourceRelation()
                        .setModel(modelModel)
                        .setSourceType(sourceType)
                        .setRelationCode(relationCode));

        if (CollectionUtils.isNotEmpty(outResourceRelations)) {
            return outResourceRelations.get(0);
        }
        return null;
    }

    /**
     * 根据内部编码找到外部编码
     *
     * @param modelModel
     * @param sourceType
     * @param relationCode
     */
    public static String fetchOutCodeByCode(String modelModel, String sourceType, String relationCode) {
        OutResourceRelation resourceRelation = fetchRelationByCode(modelModel, sourceType, relationCode);
        if (resourceRelation != null) {
            return resourceRelation.getOutCode();
        }
        return null;
    }

    /**
     * 根据外部编码找到内部的关联模型
     *
     * @param modelModel
     * @param sourceType
     * @param outCode
     */
    public static OutResourceRelation fetchRelationByOutCode(String modelModel, String sourceType, String outCode) {
        if (StringUtils.isBlank(modelModel)
                || StringUtils.isBlank(sourceType)
                || StringUtils.isBlank(outCode)) {
            return null;
        }
        List<OutResourceRelation> outResourceRelations = new OutResourceRelation()
                .setModel(modelModel)
                .setSourceType(sourceType)
                .setOutCode(outCode)
                .queryList();
        if (CollectionUtils.isNotEmpty(outResourceRelations)) {
            return outResourceRelations.get(0);
        }
        return null;
    }

    /**
     * 根据外部编码找到内部的关联编码
     *
     * @param modelModel
     * @param sourceType
     * @param outCode
     */
    public static String fetchRelationCodeByOutCode(String modelModel, String sourceType, String outCode) {
        if (StringUtils.isBlank(modelModel)
                || StringUtils.isBlank(sourceType)
                || StringUtils.isBlank(outCode)) {
            return null;
        }
        List<OutResourceRelation> outResourceRelations = new OutResourceRelation()
                .setModel(modelModel)
                .setSourceType(sourceType)
                .setOutCode(outCode)
                .queryList();
        if (CollectionUtils.isNotEmpty(outResourceRelations)) {
            return outResourceRelations.get(0).getRelationCode();
        }
        return null;
    }

    /**
     * 根据扩展字段找到内部的关联编码
     *
     * @param modelModel
     * @param sourceType
     * @param extra
     */
    public static String fetchRelationCodeByExtra(String modelModel, String sourceType, String extra) {
        if (StringUtils.isBlank(modelModel)
                || StringUtils.isBlank(sourceType)
                || StringUtils.isBlank(extra)) {
            return null;
        }
        List<OutResourceRelation> outResourceRelations = new OutResourceRelation()
                .setModel(modelModel)
                .setSourceType(sourceType)
                .setExtra(extra)
                .queryList();
        if (CollectionUtils.isNotEmpty(outResourceRelations)) {
            return outResourceRelations.get(0).getRelationCode();
        }
        return null;
    }

}