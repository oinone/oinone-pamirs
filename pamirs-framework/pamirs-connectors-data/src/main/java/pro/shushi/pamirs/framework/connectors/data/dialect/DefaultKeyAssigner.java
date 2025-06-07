package pro.shushi.pamirs.framework.connectors.data.dialect;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Jdbc3KeyGeneratorDialectService;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DefaultKeyAssigner implements Jdbc3KeyGeneratorDialectService.KeyAssigner {

    protected final Configuration configuration;
    protected final ResultSetMetaData rsmd;
    protected final TypeHandlerRegistry typeHandlerRegistry;
    protected final int columnPosition;
    protected final String paramName;
    protected final String propertyName;
    protected final String columnName;
    protected TypeHandler<?> typeHandler;

    protected DefaultKeyAssigner(Configuration configuration, ResultSetMetaData rsmd, int columnPosition, String paramName,
                                 String propertyName, String columnName) {
        super();
        this.configuration = configuration;
        this.rsmd = rsmd;
        this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        this.columnPosition = columnPosition;
        this.paramName = paramName;
        this.propertyName = propertyName;
        this.columnName = columnName;
    }

    @Override
    public void assign(ResultSet rs, Object param) {
        if (paramName != null) {
            // If paramName is set, param is ParamMap
            param = ((MapperMethod.ParamMap<?>) param).get(paramName);
        }
        MetaObject metaParam = configuration.newMetaObject(param);
        try {
            if (typeHandler == null) {
                if (metaParam.hasSetter(propertyName)) {
                    Class<?> propertyType = metaParam.getSetterType(propertyName);
                    typeHandler = typeHandlerRegistry.getTypeHandler(propertyType,
                            JdbcType.forCode(rsmd.getColumnType(columnPosition)));
                } else {
                    throw new ExecutorException("No setter found for the keyProperty '" + propertyName + "' in '"
                            + metaParam.getOriginalObject().getClass().getName() + "'.");
                }
            }
            //noinspection StatementWithEmptyBody
            if (typeHandler == null) {
                // Error?
            } else {
                Object value = typeHandler.getResult(rs, columnPosition);
                metaParam.setValue(propertyName, value);
            }
        } catch (SQLException e) {
            throw new ExecutorException("Error getting generated key or setting result to parameter object. Cause: " + e,
                    e);
        }
    }
}