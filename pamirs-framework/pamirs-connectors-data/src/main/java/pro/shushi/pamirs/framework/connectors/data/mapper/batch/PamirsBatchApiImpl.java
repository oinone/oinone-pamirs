package pro.shushi.pamirs.framework.connectors.data.mapper.batch;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.connectors.data.api.configure.PamirsFrameworkDataConfiguration;
import pro.shushi.pamirs.framework.connectors.data.configure.mapper.PamirsMapperConfiguration;
import pro.shushi.pamirs.framework.connectors.data.enmu.DataExpEnumerate;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.framework.connectors.data.mapper.PamirsMapper;
import pro.shushi.pamirs.framework.connectors.data.mapper.context.MapperContext;
import pro.shushi.pamirs.framework.connectors.data.util.DataConfigurationHelper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.enmu.BatchOpTypeEnum;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;

import javax.annotation.Resource;
import java.util.List;
import java.util.function.BiFunction;

/**
 * 2020/12/15 8:33 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@SPI.Service
@Component
public class PamirsBatchApiImpl implements PamirsBatchApi {

    @Resource
    private PamirsFrameworkDataConfiguration pamirsFrameworkDataConfiguration;

    @Resource
    private PamirsMapperConfiguration pamirsMapperConfiguration;

    @Resource
    private SqlSessionFactory sqlSessionFactory;

    @SuppressWarnings("rawtypes")
    @Override
    public int batchCommit(BatchOpTypeEnum opType, List entityList, BiFunction<PamirsMapper, Object, Integer> function) {
        if (CollectionUtils.isEmpty(entityList)) {
            return 0;
        }
        String model = MapperContext.model(entityList);
        int count = 0;
        try (SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            try {
                hintDsKey(model);

                GenericMapper mapper = session.getMapper(GenericMapper.class);
                boolean autoIncrement = BatchOpTypeEnum.insert.equals(opType) && PamirsTableInfo.isAutoIncrementModel(model);
                for (Object obj : entityList) {
                    Models.api().setModel(obj, model);
                    int c = function.apply(mapper, obj);
                    if (autoIncrement) {
                        session.flushStatements();
                    }
                    count += c;
                }
                session.commit();
            } catch (Exception e) {
                session.rollback();
                if (e instanceof PamirsException) {
                    throw (PamirsException) e;
                }
                throw PamirsException.construct(DataExpEnumerate.BASE_BATCH_EXECUTE_ERROR, e).errThrow();
            } finally {
                session.clearCache();
                closeHintDsKey();
                if (StringUtils.isNotBlank(model)) {
                    MapperContext.setModel(model, entityList);
                }
            }
        }
        return count;
    }

    private void closeHintDsKey() {
        PamirsSession.clearDsKey();
        if (log.isDebugEnabled()) {
            log.debug("恢复数据源-" + PamirsBatchApiImpl.class.getName());
        }
    }

    private void hintDsKey(String model) {
        String dsKey = DataConfigurationHelper.getDsKey(model);
        if (log.isDebugEnabled()) {
            log.debug("使用数据源(" + dsKey + ")-" + PamirsBatchApiImpl.class.getName());
        }
        PamirsSession.pushDsKey(dsKey);
    }

}
