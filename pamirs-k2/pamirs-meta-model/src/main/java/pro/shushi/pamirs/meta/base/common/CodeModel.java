package pro.shushi.pamirs.meta.base.common;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.base.manager.data.CodeDataManager;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import static pro.shushi.pamirs.meta.base.common.CodeModel.MODEL_MODEL;

/**
 * id为主键且带code的模型，抽象基类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.model(MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.ABSTRACT, priority = 36)
@Model(displayName = "带编码的基础模型", summary = "带编码的基础模型")
public abstract class CodeModel extends IdModel {

    public static final String MODEL_MODEL = "base.CodeModel";
    private static final long serialVersionUID = 297150384405575307L;

    @Base
    @Field.String
    @Field(displayName = "编码", unique = true, required = true, priority = 90)
    private String code;

    /**
     * 数据管理器
     */
    protected final static CodeDataManager manager = CodeDataManager.getInstance();

    @SuppressWarnings("unchecked")
    public <T extends CodeModel> T queryByCode() {
        return manager.queryByCode((T) this);
    }

    @SuppressWarnings("unchecked")
    public <T extends CodeModel> T queryByCode(String code) {
        return manager.queryByCode((T) TypeUtils.getNewInstance(this.getClass()).setCode(code));
    }

    @SuppressWarnings("unchecked")
    public <T extends CodeModel> Integer updateByCode() {
        return manager.updateByCode((T) this);
    }

    public Boolean deleteByCode() {
        return manager.deleteByCode(this);
    }

    public Boolean deleteByCode(String code) {
        return manager.deleteByCode(TypeUtils.getNewInstance(this.getClass()).setCode(code));
    }

    @SuppressWarnings("unchecked")
    public <T extends CodeModel> T generateCode(String sequence, String configCode) {
        return manager.generateCode((T) this, sequence, configCode);
    }

}
