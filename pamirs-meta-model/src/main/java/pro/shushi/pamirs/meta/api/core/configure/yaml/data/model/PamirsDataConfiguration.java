package pro.shushi.pamirs.meta.api.core.configure.yaml.data.model;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.Map;

/**
 * pamirs数据持久层配置信息
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 11:03 上午
 */
@Data
public class PamirsDataConfiguration {

    private PamirsTableInfo globalTableInfo;

    private Boolean environmentCheck;

    private String environmentColumn;

    private Boolean tenantCheck;

    private String tenantColumn;

    private Map properties;

}
