package pro.shushi.pamirs.meta.base;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.manager.data.IdDataManager;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

/**
 * id为主键的关系模型，抽象基类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.model(IdRelation.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.ABSTRACT, index = {"createDate"}, ordering = "createDate DESC, id DESC", priority = 42)
@Model(displayName = "基础关系模型", summary = "该模型用于标识其子模型为关系模型类型，是所有关系模型的父模型")
public abstract class IdRelation extends BaseRelation {

    private static final long serialVersionUID = -1154599157311860190L;

    public static final String MODEL_MODEL = "base.IdRelation";

    @Base
    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "ID", summary = "ID字段，唯一自增索引", required = true, priority = 5)
    private Long id;

    /**
     * 数据管理器
     */
    protected final static IdDataManager manager = IdDataManager.getInstance();

    @SuppressWarnings("unchecked")
    public <T extends IdRelation> T queryById(Long id) {
        return manager.queryById((T) TypeUtils.getNewInstance(this.getClass()).setId(id));
    }

    @SuppressWarnings("unchecked")
    public <T extends IdRelation> T queryById() {
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

}
