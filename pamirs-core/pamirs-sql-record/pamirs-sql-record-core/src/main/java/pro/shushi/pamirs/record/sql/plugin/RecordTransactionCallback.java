package pro.shushi.pamirs.record.sql.plugin;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.List;

/**
 * RecordTransactionCallback
 *
 * @author yakir on 2023/07/03 11:54.
 */
public class RecordTransactionCallback implements TransactionCallback<List<DataMap>> {

    private final QueryWrapper<DataMap> queryWrapper;

    public RecordTransactionCallback(QueryWrapper<DataMap> queryWrapper) {
        this.queryWrapper = queryWrapper;
    }

    @Override
    public List<DataMap> doInTransaction(TransactionStatus status) {

        try {
            return BeanDefinitionUtils.getBean(GenericMapper.class).selectList(queryWrapper);
        } catch (Throwable throwable) {
            return null;
        }
    }
}
