package pro.shushi.pamirs.middleware.schedule.core.dialect;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ResultMap;

import java.util.List;

/**
 * 抽象脚本执行方言服务
 *
 * @author Adamancy Zhang at 11:57 on 2023-06-28
 */
public abstract class AbstractSQLDialectService implements ScheduleSQLDialectService {

    private static final String LIMIT_PROPERTY = "pageSize";

    private static final String OFFSET_PROPERTY = "start";

    protected abstract String getOriginType();

    protected abstract DbType getTargetType();

    protected abstract SQLASTVisitor getSQLVisitor(List<ResultMap> resultMaps);

    protected SQLUtils.FormatOption getFormatOption() {
        return new SQLUtils.FormatOption(VisitorFeature.OutputUCase);
    }

    @Override
    public String resolve(String sql, BoundSql boundSql, List<ResultMap> resultMaps) {
        List<SQLStatement> statements = SQLUtils.parseStatements(sql, getOriginType());
        SQLASTVisitor visitor = getSQLVisitor(resultMaps);
        for (SQLStatement statement : statements) {
            statement.accept(visitor);
        }
        return SQLUtils.toSQLString(statements, getTargetType(), getFormatOption());
    }

    protected void swapLimitOffset(BoundSql boundSql) {
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        int limitIndex = -1, offsetIndex = -1;
        for (int i = 0; i < parameterMappings.size(); i++) {
            ParameterMapping parameterMapping = parameterMappings.get(i);
            if (LIMIT_PROPERTY.equals(parameterMapping.getProperty())) {
                limitIndex = i;
            } else if (OFFSET_PROPERTY.equals(parameterMapping.getProperty())) {
                offsetIndex = i;
            }
        }
        if (limitIndex != -1 && offsetIndex != -1) {
            swap(parameterMappings, limitIndex, offsetIndex);
        }
    }

    protected static <T> void swap(List<T> list, int index1, int index2) {
        if (index1 > index2) {
            int temp = index1;
            index1 = index2;
            index2 = temp;
        }
        T item2 = list.get(index2);
        T item1 = list.remove(index1);
        list.add(index1, item2);
        list.remove(index2);
        list.add(index2, item1);
    }
}
