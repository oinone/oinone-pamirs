package pro.shushi.pamirs.framework.orm.extend;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.gen.SequenceGenerator;
import pro.shushi.pamirs.meta.base.GenericModel;
import pro.shushi.pamirs.meta.base.common.CodeModel;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.constant.SqlConstants;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import static pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate.BASE_CODE_IS_NOT_EXISTS_ERROR;
import static pro.shushi.pamirs.meta.constant.FunctionConstants.*;

/**
 * 编码数据管理器
 * <p>
 * 2020/5/7 11:58 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Fun(CodeModel.MODEL_MODEL)
@Component
public class DefaultCodeDataManager {

    @Function.Advanced(displayName = "根据编码查询记录", type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(queryByCode)
    @Function
    public <T> T queryByCode(T data) {
        String model = Models.api().getModel(data);
        return Models.origin().queryOne(generateCodeQuery(model, checkCode(data)));
    }

    @Function.Advanced(displayName = "根据编码更新记录", type = FunctionTypeEnum.UPDATE, managed = true)
    @Function.fun(updateByCode)
    @Function
    public <T> Integer updateByCode(T data) {
        String model = Models.api().getModel(data);
        return Models.origin().updateByEntity(data, generateCodeQuery(model, checkCode(data)));
    }

    @SuppressWarnings("unused")
    @Function.Advanced(displayName = "根据编码删除记录", type = FunctionTypeEnum.DELETE, managed = true)
    @Function.fun(deleteByCode)
    @Function
    public <T> Boolean deleteByCode(T data) {
        String model = Models.api().getModel(data);
        return Models.origin().deleteByUniqueField(generateCodeQuery(model, checkCode(data)));
    }

    @SuppressWarnings("unused")
    @Function.Advanced(displayName = "生成编码", type = FunctionTypeEnum.QUERY)
    @Function(openLevel = FunctionOpenEnum.LOCAL)
    public <T> T generateCode(T data, String sequence, String configCode) {
        if (StringUtils.isBlank((String) FieldUtils.getFieldValue(data, FieldConstants.CODE))) {
            Object sequenceValue = CommonApiFactory.getApi(SequenceGenerator.class).generate(sequence, configCode);
            if (null != sequenceValue) {
                FieldUtils.setFieldValue(data, FieldConstants.CODE, TypeUtils.stringValueOf(sequenceValue));
            }
        }
        return data;
    }

    private <T> String checkCode(T data) {
        String code = (String) FieldUtils.getFieldValue(data, SqlConstants.CODE);
        if (null == code) {
            throw PamirsException.construct(BASE_CODE_IS_NOT_EXISTS_ERROR).errThrow();
        }
        return code;
    }

    private <T> T generateCodeQuery(String model, String code) {
        @SuppressWarnings("unchecked") T query = (T) new GenericModel(model);
        FieldUtils.setFieldValue(query, SqlConstants.CODE, code);
        return query;
    }

}
