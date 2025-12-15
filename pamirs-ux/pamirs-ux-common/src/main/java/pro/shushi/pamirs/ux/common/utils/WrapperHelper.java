package pro.shushi.pamirs.ux.common.utils;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.util.UnsafeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper帮助类
 *
 * @author Adamancy Zhang on 2021-04-27 20:00
 */
public class WrapperHelper {

    private static final String AS = " as ";

    public static <T> LambdaQueryWrapper<T> lambda(IWrapper<T> wrapper) {
        LambdaQueryWrapper<T> lambdaWrapper;
        if (wrapper instanceof QueryWrapper) {
            lambdaWrapper = ((QueryWrapper<T>) wrapper).lambda();
        } else {
            lambdaWrapper = (LambdaQueryWrapper<T>) wrapper;
        }
        return lambdaWrapper;
    }

    public static <T> QueryWrapper<T> wrapper(IWrapper<T> wrapper) {
        QueryWrapper<T> queryWrapper;
        if (wrapper instanceof LambdaQueryWrapper) {
            LambdaQueryWrapper<T> lambdaWrapper = (LambdaQueryWrapper<T>) wrapper;
            queryWrapper = Pops.query(lambdaWrapper.getEntity());
            queryWrapper.setModel(lambdaWrapper.getModel());
            queryWrapper.setSelects(lambdaWrapper.getSelects());
            queryWrapper.setBatchSize(lambdaWrapper.getBatchSize());
            queryWrapper.setOriginRsql(lambdaWrapper.getOriginRsql());
            UnsafeUtil.setValue(queryWrapper, "paramNameSeq", UnsafeUtil.getValue(lambdaWrapper, "paramNameSeq"));
            UnsafeUtil.setValue(queryWrapper, "paramNameValuePairs", UnsafeUtil.getValue(lambdaWrapper, "paramNameValuePairs"));
            UnsafeUtil.setValue(queryWrapper, "expression", UnsafeUtil.getValue(lambdaWrapper, "expression"));
            UnsafeUtil.setValue(queryWrapper, "lastSql", UnsafeUtil.getValue(lambdaWrapper, "lastSql"));
            UnsafeUtil.setValue(queryWrapper, "sqlComment", UnsafeUtil.getValue(lambdaWrapper, "sqlComment"));
        } else {
            queryWrapper = (QueryWrapper<T>) wrapper;
        }
        return queryWrapper;
    }

    /**
     * 追加 select 列
     */
    public static <T> void withSelect(QueryWrapper<T> queryWrapper, String... columns) {
        String sqlSelect = queryWrapper.getSqlSelect();
        if (StringUtils.isBlank(sqlSelect)) {
            queryWrapper.select(columns);
        } else {
            String[] nextColumns = new String[columns.length + 1];
            nextColumns[0] = sqlSelect;
            System.arraycopy(columns, 0, nextColumns, 1, columns.length);
            queryWrapper.select(nextColumns);
        }
    }

    public static String getColumAsField(String column, String asField) {
        return column + AS + asField;
    }

    public static String getColumAsField(List<String> relationColumns, List<String> relationAsFields) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < relationColumns.size(); i++) {
            String column = relationColumns.get(i);
            String asField = relationAsFields.get(i);
            if (i != 0) {
                builder.append(CharacterConstants.SEPARATOR_COMMA);
            }
            builder.append(column).append(AS).append(asField);
        }
        return builder.toString();
    }

    public static List<String> getColumAsFields(List<String> columns, List<String> asFields) {
        List<String> columnsAsFields = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            String column = columns.get(i);
            String asField = asFields.get(i);
            columnsAsFields.add(getColumAsField(column, asField));
        }
        return columnsAsFields;
    }
}
