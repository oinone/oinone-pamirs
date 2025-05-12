package pro.shushi.pamirs.meta.base;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.base.manager.data.IdDataManager;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import static pro.shushi.pamirs.meta.base.IdModel.MODEL_MODEL;

/**
 * id为主键的模型，抽象基类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@MetaSimulator(onlyBasicTypeField = false)
@Base
@Model.model(MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.ABSTRACT, index = {"createDate"}, ordering = "createDate DESC, id DESC", priority = 35)
@Model(displayName = "基础模型", summary = "基础模型")
public abstract class IdModel extends BaseModel {

    public static final String MODEL_MODEL = "base.IdModel";

    private static final long serialVersionUID = 5402388389497370372L;

    /**
     * 数据管理器
     */
    protected final static IdDataManager manager = IdDataManager.getInstance();

    @Base
    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "ID", summary = "ID字段，唯一自增索引", priority = 5)
    private Long id;

    @SuppressWarnings("unchecked")
    public <T extends IdModel> T queryById(Long id) {
        return manager.queryById((T) TypeUtils.getNewInstance(this.getClass()).setId(id));
    }

    @SuppressWarnings("unchecked")
    public <T extends IdModel> T queryById() {
        return manager.queryById((T) this);
    }

    public Integer updateById() {
        return manager.updateById(this);
    }

    public Boolean deleteById(Long id) {
        return manager.deleteById(TypeUtils.getNewInstance(this.getClass()).setId(id));
    }

    public Boolean deleteById() {
        return manager.deleteById(this);
    }

    @SuppressWarnings("unchecked")
    public <T extends IdModel> T generateId(String keyGenerator) {
        return manager.generateId((T) this, keyGenerator);
    }

}
