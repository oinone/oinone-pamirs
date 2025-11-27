package pro.shushi.pamirs.ux.grouping.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

/**
 * 分组配置
 *
 * @author Adamancy Zhang at 11:35 on 2025-11-21
 */
@Configuration
@ConfigurationProperties(prefix = "pamirs.grouping")
@Validated
@RefreshScope
public class GroupingConfiguration {

    private Table table;

    private Map<String, Table> modelTable;

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public Map<String, Table> getModelTable() {
        return modelTable;
    }

    public void setModelTable(Map<String, Table> modelTable) {
        this.modelTable = modelTable;
    }

    public static class Table {

        /**
         * 全量分组查询限制数量，当超过指定数据量时进行懒加载分组
         * <p>
         * 0 表示不使用全量分组查询; -1 表示永远使用全量分组查询;
         */
        private Integer fullQueryCount;

        /**
         * O2M/M2M 关联关系字段进行分组时是否显示空（不显示可提高分组效率）
         * 默认: false 不显示空分组
         */
        private Boolean relationManyShowNull;

        public Integer getFullQueryCount() {
            return fullQueryCount;
        }

        public void setFullQueryCount(Integer fullQueryCount) {
            this.fullQueryCount = fullQueryCount;
        }

        public Boolean getRelationManyShowNull() {
            return relationManyShowNull;
        }

        public void setRelationManyShowNull(Boolean relationManyShowNull) {
            this.relationManyShowNull = relationManyShowNull;
        }
    }
}
