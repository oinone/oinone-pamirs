package pro.shushi.pamirs.middleware.schedule.core.dialect;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import pro.shushi.pamirs.middleware.schedule.core.dialect.entity.DialectVersion;

import java.util.List;

/**
 * 脚本执行方言服务
 *
 * @author Adamancy Zhang at 21:28 on 2023-06-27
 */
public interface ScheduleSQLDialectService {

    boolean isSupported(DialectVersion dialectVersion, MappedStatement mappedStatement);

    String resolve(String sql, BoundSql boundSql, List<ResultMap> resultMaps);

}
