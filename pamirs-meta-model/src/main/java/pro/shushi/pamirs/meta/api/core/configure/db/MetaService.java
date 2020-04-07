package pro.shushi.pamirs.meta.api.core.configure.db;

import java.util.List;
import java.util.Map;

/**
 * 系统数据库API
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 *
 */
public interface MetaService {

    Map<String, Object> queryOne(String module);

    Map<String, Object> insertOrUpdate(Map<String, Object> item);

    List<Map<String, Object>> queryInstallModules();

}
