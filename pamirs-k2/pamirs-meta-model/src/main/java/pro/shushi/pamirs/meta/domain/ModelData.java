package pro.shushi.pamirs.meta.domain;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.constant.MetaCheckConstants;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import java.util.Date;

import static pro.shushi.pamirs.meta.domain.ModelData.MODEL_MODEL;
import static pro.shushi.pamirs.meta.domain.ModelData.TABLE_NAME;

@Base
@MetaSimulator(onlyBasicTypeField = false)
@Model.Advanced(table = TABLE_NAME, priority = 6, index = {"model,lowCode", "resId,model", "loadModule"})
@Model.model(MODEL_MODEL)
@Model(displayName = "元数据安装注册表", summary = "元数据安装注册表")
public class ModelData extends IdModel implements MetaCheckConstants {

    private static final long serialVersionUID = 5328983551714737691L;
    public final static String MODEL_MODEL = "base.ModelData";

    public static final String TABLE_NAME = "base_model_data";

    @Field.Boolean
    @Field(summary = "是否来自low code", defaultValue = "0")
    private Boolean lowCode;

    @Field.Enum
    @Field(summary = "元数据来源")
    private SystemSourceEnum source;

    @Field.String(size = 512)
    @Field(summary = "唯一编码", unique = true)
    private String code;

    @Validation(check = checkModuleModule)
    @Field.String
    @Field(summary = "模块编码")
    private String module;

    @Validation(check = checkModuleModule)
    @Field.String
    @Field(summary = "加载模块编码")
    private String loadModule;

    @Validation(check = checkModelModel)
    @Field.String
    @Field(summary = "模型编码")
    private String model;

    @Field(summary = "元数据目标表的数据ID")
    private Long resId;

    @Field.Date
    @Field(summary = "元数据初始化时间")
    private Date dateInit;

    @Field.Date
    @Field(summary = "元数据更新时间")
    private Date dateUpdate;

    public ModelData code(String model, String sign) {
        this.setCode(generateCode(model, sign));
        return this;
    }

    public static String generateCode(String model, String sign) {
        return model + CharacterConstants.SEPARATOR_OCTOTHORPE + sign;
    }

}
