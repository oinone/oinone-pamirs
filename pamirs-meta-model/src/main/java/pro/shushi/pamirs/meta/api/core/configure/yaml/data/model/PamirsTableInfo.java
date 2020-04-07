package pro.shushi.pamirs.meta.api.core.configure.yaml.data.model;

import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * 表配置信息
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 11:05 上午
 */
@Data
public class PamirsTableInfo {

    private Boolean logicDelete;

    private String logicDeleteColumn;

    private String logicDeleteValue;

    private String logicNotDeleteValue;

    private Boolean optimisticLocker;

    private String optimisticLockerColumn;

}
