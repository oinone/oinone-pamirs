package pro.shushi.pamirs.meta.base.common;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdRelation;
import pro.shushi.pamirs.meta.base.manager.data.CodeDataManager;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;


/**
 * id为主键且带code的关系模型，抽象基类
 *
 * @author wangxian@shushi.pro
 * @version 1.0.0
 * date 2023/03/15
 */
@Base
@Model.model(CodeRelation.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.ABSTRACT, priority = 35)
@Model(displayName = "带编码的关系模型", summary = "带编码的关系模型")
public abstract class CodeRelation extends IdRelation {

    public static final String MODEL_MODEL = "base.CodeRelation";

    @Base
    @Field.String
    @Field(displayName = "编码", unique = true, required = true, priority = 90)
    private String code;

    /**
     * 数据管理器
     */
    protected final static CodeDataManager manager = CodeDataManager.getInstance();

    @SuppressWarnings("unchecked")
    public <T extends CodeRelation> T queryByCode() {
        return manager.queryByCode((T) this);
    }

    @SuppressWarnings("unchecked")
    public <T extends CodeRelation> T queryByCode(String code) {
        return manager.queryByCode((T) TypeUtils.getNewInstance(this.getClass()).setCode(code));
    }

    @SuppressWarnings("unchecked")
    public <T extends CodeRelation> Integer updateByCode() {
        return manager.updateByCode((T) this);
    }

    public Boolean deleteByCode() {
        return manager.deleteByCode(this);
    }

    public Boolean deleteByCode(String code) {
        return manager.deleteByCode(TypeUtils.getNewInstance(this.getClass()).setCode(code));
    }

    @SuppressWarnings("unchecked")
    public <T extends CodeRelation> T generateCode(String sequence, String configCode) {
        return manager.generateCode((T) this, sequence, configCode);
    }

}
