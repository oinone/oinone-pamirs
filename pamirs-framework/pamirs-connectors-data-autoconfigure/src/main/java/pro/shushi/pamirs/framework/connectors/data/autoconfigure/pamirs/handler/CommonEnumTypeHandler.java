package pro.shushi.pamirs.framework.connectors.data.autoconfigure.pamirs.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.common.enmu.Enums;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 自定义枚举属性转换器
 */
public class CommonEnumTypeHandler<E extends IEnum<?>> extends BaseTypeHandler<IEnum<?>> {

    private final Class<E> type;

    public CommonEnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, IEnum<?> parameter, JdbcType jdbcType)
            throws SQLException {
//        if (!PamirsSession.isStaticConfig()) {
//            return;
//        }
        if (jdbcType == null) {
            ps.setObject(i, BaseEnum.getValue(parameter));
        } else {
            ps.setObject(i, BaseEnum.getValue(parameter), jdbcType.TYPE_CODE);
        }
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        if (null == rs.getObject(columnName) && rs.wasNull()) {
            return null;
        }
        return this.valueOf(this.type, rs.getObject(columnName));
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        if (null == rs.getObject(columnIndex) && rs.wasNull()) {
            return null;
        }
        return this.valueOf(this.type, rs.getObject(columnIndex));
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        if (null == cs.getObject(columnIndex) && cs.wasNull()) {
            return null;
        }
        return this.valueOf(this.type, cs.getObject(columnIndex));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private E valueOf(Class<E> enumClass, Object value) {
        if (!PamirsSession.isStaticConfig()) {
            return (E) value;
        }
        E iEnum = (E) Enums.getEnumByValue((Class) enumClass, (Serializable) value);
        if (iEnum == null && value instanceof Number) {
            iEnum = (E) Enums.getEnumByValue((Class) enumClass, !"0".equals(String.valueOf(value)));
        }
        return iEnum;
    }

}
