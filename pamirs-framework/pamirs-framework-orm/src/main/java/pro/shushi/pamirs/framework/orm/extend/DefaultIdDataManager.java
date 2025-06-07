package pro.shushi.pamirs.framework.orm.extend;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.gen.IdGenerator;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.constant.SqlConstants;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;

import static pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate.BASE_ID_IS_NOT_EXISTS_ERROR;

/**
 * ID数据管理器实现
 * <p>
 * 2020/5/7 11:58 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Fun(IdModel.MODEL_MODEL)
@Component
public class DefaultIdDataManager implements FunctionConstants {

    @Function.Advanced(displayName = "根据ID查询记录", type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(queryById)
    @Function
    public <T> T queryById(T data) {
        checkId(data);
        return Models.origin().queryByPk(data);
    }

    @Function.Advanced(displayName = "根据ID更新记录", type = FunctionTypeEnum.UPDATE, managed = true)
    @Function.fun(updateById)
    @Function
    public <T> Integer updateById(T data) {
        checkId(data);
        return Models.origin().updateByPk(data);
    }

    @Function.Advanced(displayName = "根据ID删除记录", type = FunctionTypeEnum.DELETE, managed = true)
    @Function.fun(deleteById)
    @Function
    public <T> Boolean deleteById(T data) {
        checkId(data);
        return Models.origin().deleteByPk(data);
    }

    @Function.Advanced(displayName = "生成ID", type = FunctionTypeEnum.QUERY)
    @Function(openLevel = FunctionOpenEnum.LOCAL)
    public <T> T generateId(T data, String keyGenerator) {
        if (null == FieldUtils.getFieldValue(data, FieldConstants.ID)) {
            Object sequenceValue = CommonApiFactory.getApi(IdGenerator.class).generate(keyGenerator);
            if (null != sequenceValue) {
                FieldUtils.setFieldValue(data, FieldConstants.ID, Long.valueOf(sequenceValue.toString()));
            }
        }
        return data;
    }

    private <T> void checkId(T data) {
        if (null == FieldUtils.getFieldValue(data, SqlConstants.ID)) {
            throw PamirsException.construct(BASE_ID_IS_NOT_EXISTS_ERROR).errThrow();
        }
    }

}
