package pro.shushi.pamirs.framework.connectors.data.mapper.method.util;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.data.constant.DbConstants;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.spi.LogicColumnSqlApi;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.LogicColumnFetcher;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.Holder;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static pro.shushi.pamirs.meta.common.constants.CharacterConstants.NEWLINE;

/**
 * Mapper方法工具类
 * 2021/9/16 6:57 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class LogicColumnUtil {

    private static final Holder<LogicColumnFetcher> logicColumnFetcherHolder = new Holder<>();

    public static LogicColumnFetcher logicColumnFetcher() {
        LogicColumnFetcher logicColumnFetcher = logicColumnFetcherHolder.get();
        if (null == logicColumnFetcher) {
            synchronized (logicColumnFetcherHolder) {
                logicColumnFetcher = logicColumnFetcherHolder.get();
                if (null == logicColumnFetcher) {
                    logicColumnFetcher = Spider.getDefaultExtension(LogicColumnFetcher.class);
                    logicColumnFetcherHolder.set(logicColumnFetcher);
                }
            }
        }
        return logicColumnFetcher;
    }

    public static String fillLogicColumns(String model, String script) {
        Set<String> columns = LogicColumnUtil.logicColumnFetcher().fetchLogicColumnsWithoutLogicDelete(model);
        if (!CollectionUtils.isEmpty(columns)) {
            PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(model);
            String columnFormat = pamirsTableInfo.getColumnFormat();
            boolean needFormat = StringUtils.isNotBlank(columnFormat);
            List<String> columnScripts = new ArrayList<>();
            for (String column : columns) {
                column = needFormat ? String.format(columnFormat, column) : column;
                columnScripts.add(column + CharacterConstants.SEPARATOR_COMMA);
            }
            return script + StringUtils.join(columnScripts, CharacterConstants.NEWLINE);
        }
        return script;
    }

    public static String fillInsertLogicProperties(String model, String script) {
        Set<String> columns = LogicColumnUtil.logicColumnFetcher().fetchLogicColumnsWithoutLogicDelete(model);
        if (!CollectionUtils.isEmpty(columns)) {
            List<String> columnScripts = new ArrayList<>();
            for (String column : columns) {
                column = SqlScriptUtils.safeParam(DbConstants.PARAM_ANNOTATION_EXT + CharacterConstants.SEPARATOR_DOT + column);
                columnScripts.add(column + CharacterConstants.SEPARATOR_COMMA);
            }
            return script + StringUtils.join(columnScripts, CharacterConstants.NEWLINE);
        }
        return script;
    }

    public static String fillOnDuplicateKeyUpdate(String model, String script) {
        Set<String> columns = LogicColumnUtil.logicColumnFetcher().fetchLogicColumnsWithoutLogicDelete(model);
        if (!CollectionUtils.isEmpty(columns)) {
            PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(model);
            String columnFormat = pamirsTableInfo.getColumnFormat();
            boolean needFormat = StringUtils.isNotBlank(columnFormat);
            List<String> columnScripts = new ArrayList<>();
            for (String column : columns) {
                column = needFormat ? String.format(columnFormat, column) : column;
                columnScripts.add(SQLMethodUtils.getInsertOrUpdateSqlColumn(column));
            }
            return script + StringUtils.join(columnScripts, CharacterConstants.NEWLINE);
        }
        return script;
    }

    public static String fillSqlSegment(String model, String script) {
        Set<String> columns = LogicColumnUtil.logicColumnFetcher().fetchLogicColumnsWithoutLogicDelete(model);
        if (!CollectionUtils.isEmpty(columns)) {
            PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(model);
            String columnFormat = pamirsTableInfo.getColumnFormat();
            boolean needFormat = StringUtils.isNotBlank(columnFormat);
            List<String> columnScripts = new ArrayList<>();
            for (String column : columns) {
                String property = DbConstants.PARAM_ANNOTATION_EXT + CharacterConstants.SEPARATOR_DOT + column;
                column = needFormat ? String.format(columnFormat, column) : column;
                String columnScript = Spider.getDefaultExtension(LogicColumnSqlApi.class).LogicColumnScript(model, column, property);
                columnScripts.add(columnScript);
            }
            return script + StringUtils.join(columnScripts, CharacterConstants.NEWLINE) + NEWLINE;
        }
        return script;
    }

}
