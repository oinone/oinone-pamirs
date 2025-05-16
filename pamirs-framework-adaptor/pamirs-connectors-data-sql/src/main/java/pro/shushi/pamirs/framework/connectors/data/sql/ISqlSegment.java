package pro.shushi.pamirs.framework.connectors.data.sql;


import java.io.Serializable;

/**
 * SQL 片段接口
 */
@FunctionalInterface
public interface ISqlSegment extends Serializable {

    /**
     * SQL 片段
     */
    String getSqlSegment();
}
