package pro.shushi.pamirs.core.common;

import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.util.UnsafeUtil;

/**
 * @author Adamancy Zhang on 2021-04-27 20:00
 */
public class WrapperHelper {

    private WrapperHelper() {
        //reject create object
    }

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
}
