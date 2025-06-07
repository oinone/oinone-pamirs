package pro.shushi.pamirs.framework.connectors.data.dialect;

import pro.shushi.pamirs.framework.connectors.data.dialect.api.SQLMethodDialectService;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.api.DeleteMethod;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.api.InsertMethod;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.api.SelectMethod;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.api.UpdateMethod;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.mysql.MysqlDeleteMethod;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.mysql.MysqlInsertMethod;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.mysql.MysqlSelectMethod;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.mysql.MysqlUpdateMethod;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;

/**
 * 抽象脚本模板方言服务
 *
 * @author Adamancy Zhang at 09:49 on 2023-06-26
 */
public abstract class AbstractSQLMethodDialectService implements SQLMethodDialectService {

    @Override
    public SelectMethod getSelectMethod(ModelConfig modelConfig) {
        return new MysqlSelectMethod(modelConfig);
    }

    @Override
    public InsertMethod getInsertMethod(ModelConfig modelConfig) {
        return new MysqlInsertMethod(modelConfig);
    }

    @Override
    public UpdateMethod getUpdateMethod(ModelConfig modelConfig) {
        return new MysqlUpdateMethod(modelConfig);
    }

    @Override
    public DeleteMethod getDeleteMethod(ModelConfig modelConfig) {
        return new MysqlDeleteMethod(modelConfig);
    }
}
