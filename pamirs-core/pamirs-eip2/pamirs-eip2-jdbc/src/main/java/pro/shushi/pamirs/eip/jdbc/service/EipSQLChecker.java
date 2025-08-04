package pro.shushi.pamirs.eip.jdbc.service;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import pro.shushi.pamirs.eip.jdbc.entity.SQLPrepareEntity;

import java.util.List;

/**
 * SQL检查器
 *
 * @author Adamancy Zhang at 17:11 on 2024-06-06
 */
public interface EipSQLChecker {

    String dbType();

    default List<String> secondaryDbTypes() {
        return null;
    }

    SQLPrepareEntity prepare(String sql);

    SQLASTVisitor visitor(SQLPrepareEntity prepareEntity);

    List<SQLStatement> parser(String sql);

    String toSQLString(List<SQLStatement> statements);

    default DbType toDruidDbType() {
        return DbType.valueOf(dbType().toLowerCase());
    }
}
