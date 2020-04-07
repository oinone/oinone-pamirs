package pro.shushi.pamirs.meta.api.core.compute;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.db.Table;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;

import java.util.Map;

/**
 * 数据表计算器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 *
 */
public interface TableComputer extends CommonApi {

    /**
     * 计算 create table sql
     *
     * @param meta 元数据
     * @param tableColumns 数据表schema
     * @return
     */
    Result<Map<String/*db*/, String/*ddl*/>> compute(Meta meta, Map<String, Table> tableColumns);

}
