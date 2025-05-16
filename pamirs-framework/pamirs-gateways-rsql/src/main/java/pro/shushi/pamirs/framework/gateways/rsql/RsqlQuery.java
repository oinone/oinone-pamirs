package pro.shushi.pamirs.framework.gateways.rsql;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.constant.RSqlConstants;
import pro.shushi.pamirs.meta.constant.SqlConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * TypeHelper供Rsql专用，所以使用继承
 * @author deng
 */
@Data
public class RsqlQuery {
    
    private StringBuilder where;

    private StringBuilder condition;

    private List paramValues;

    RsqlQuery(){
        this.where = new StringBuilder();
        this.paramValues = new ArrayList();
    }

    RsqlQuery(String sql){
        this.where = new StringBuilder(sql);
        this.paramValues = new ArrayList();
    }

    public RsqlQuery append(String word){
        this.where.append(StringUtils.SPACE).append(word);
        return this;
    }

    public RsqlQuery append(StringBuilder word){
        return append(word.toString());
    }

    public RsqlQuery prepend(String word){
        return prepend(new StringBuilder(word));
    }

    public RsqlQuery prepend(StringBuilder word){
        where = word.append(StringUtils.SPACE).append(where.toString());
        return this;
    }

    public RsqlQuery isNull(){
        return append(SqlConstants.IS).append(SqlConstants.NULL);
    }

    public RsqlQuery equal(Object query){
        return append("=").append(query.toString());
    }

    public RsqlQuery and(RsqlQuery query){
        this.getParamValues().addAll(query.getParamValues());
        this.condition.append(StringUtils.SPACE).append(RSqlConstants.AND).append(StringUtils.SPACE).append(RSqlConstants.LEFT_PARENTHESES).append(query.getCondition()).append(RSqlConstants.RIGHT_PARENTHESES);
        return append(SqlConstants.AND).append(query.where);
    }

    public RsqlQuery or(RsqlQuery query) {
        this.getParamValues().addAll(query.getParamValues());
        this.condition.append(StringUtils.SPACE).append(RSqlConstants.OR).append(StringUtils.SPACE).append(RSqlConstants.LEFT_PARENTHESES).append(query.getCondition()).append(RSqlConstants.RIGHT_PARENTHESES);
        return prepend(RSqlConstants.LEFT_PARENTHESES).append(SqlConstants.OR).append(RSqlConstants.LEFT_PARENTHESES).append(query.where).append(RSqlConstants.RIGHT_PARENTHESES).append(RSqlConstants.RIGHT_PARENTHESES);
    }

    public RsqlQuery like(String query){
        return append(SqlConstants.LIKE).append(query);
    }

    public RsqlQuery notLike(String query){
        return append(SqlConstants.NOT).append(SqlConstants.LIKE).append((query));
    }

    public RsqlQuery isNotNull(){
        return append(SqlConstants.IS).append(SqlConstants.NOT).append(SqlConstants.NULL);
    }

    public RsqlQuery notEqual(Object query){
        return append(SqlConstants.NE).append(query.toString());
    }

    public RsqlQuery greaterThan(Object query){
        return append(SqlConstants.GT).append(query.toString());
    }

    public RsqlQuery greaterThanOrEqualTo(Object query){
        return append(SqlConstants.GE).append(query.toString());
    }

    public RsqlQuery lessThan(Object query){
        return append(SqlConstants.LT).append(query.toString());
    }

    public RsqlQuery lessThanOrEqualTo(Object query){
        return append(SqlConstants.LE).append(query.toString());
    }

    public RsqlQuery in(String args){
        return append(SqlConstants.IN).append(args);// fixme 性能 exist
    }

    public RsqlQuery notIn(String args){
        return append(SqlConstants.NOT).append(SqlConstants.IN).append(args);
    }
}
