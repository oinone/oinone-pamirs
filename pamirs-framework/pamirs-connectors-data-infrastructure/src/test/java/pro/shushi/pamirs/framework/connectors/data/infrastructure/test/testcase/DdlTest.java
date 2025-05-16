package pro.shushi.pamirs.framework.connectors.data.infrastructure.test.testcase;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.AssertionErrors;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.framework.connectors.data.api.ddl.TableComputer;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.test.mock.model.*;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * DDL生成测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("DDL生成测试")
public class DdlTest extends AbstractBaseTest {

    @Resource
    private TableComputer tableComputer;

    @Test
    @Order(0)
    @DisplayName("测试生成ddl回放表DDL")
    public void testCreateSchemaPlayBack() {
        // 执行测试用例
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(SchemaPlayBack.MODEL_MODEL);
        List<String> ddl = tableComputer.compute(null, null, modelConfig, null).getDdl();

        // 结果断言
        AssertionErrors.assertEquals("测试生成基本创建表DDL功能失败", "CREATE TABLE IF NOT EXISTS `PAMIRS_SCHEMA_PLAY_BACK`(\n" +
                " `ID` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',\n" +
                " `DS_KEY` VARCHAR(128) COMMENT '',\n" +
                " `MODULE` VARCHAR(128) COMMENT '',\n" +
                " `DDL` LONGTEXT COMMENT '',\n" +
                " `ERROR` TEXT COMMENT '',\n" +
                " `LINE` INT(11) COMMENT '',\n" +
                " `PREFIX_COMMAND` VARCHAR(128) COMMENT '',\n" +
                " `OPT_VERSION` BIGINT(20) COMMENT '',\n" +
                " `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',\n" +
                " PRIMARY KEY (`ID`)\n" +
                ")ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '';\n" +
                "CREATE INDEX `PAMIRS_SCHEMA_PLAY_BACK_MODULE` ON `PAMIRS_SCHEMA_PLAY_BACK`(`MODULE`);\n" +
                "CREATE UNIQUE INDEX `PAMIRS_SCHEMA_PLAY_BACK_DS_KEY` ON `PAMIRS_SCHEMA_PLAY_BACK`(`DS_KEY`,`is_deleted`);\n", StringUtils.join(ddl, CharacterConstants.SEPARATOR_EMPTY));
    }

    @Test
    @Order(0)
    @DisplayName("测试生成库表DDL")
    public void testCreateDatabase() {
        // 执行测试用例
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(DatabaseSchema.MODEL_MODEL);
        List<String> ddl = tableComputer.compute(null, null, modelConfig, null).getDdl();

        // 结果断言
        AssertionErrors.assertEquals("测试生成基本创建表DDL功能失败", "CREATE TABLE IF NOT EXISTS `PAMIRS_DATABASES`(\n" +
                " `ID` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',\n" +
                " `SCHEMA_NAME` VARCHAR(128) COMMENT '',\n" +
                " `DEFAULT_CHARACTER_SET_NAME` VARCHAR(128) COMMENT '',\n" +
                " `DEFAULT_COLLATION_NAME` VARCHAR(128) COMMENT '',\n" +
                " `CREATE_DATE` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                " `WRITE_DATE` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',\n" +
                " `CREATE_UID` BIGINT(20) COMMENT '创建人',\n" +
                " `WRITE_UID` BIGINT(20) COMMENT '更新人',\n" +
                " `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',\n" +
                " PRIMARY KEY (`ID`)\n" +
                ")ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '';\n" +
                "CREATE UNIQUE INDEX `PAMIRS_DATABASES_SCHEMA_NAME` ON `PAMIRS_DATABASES`(`SCHEMA_NAME`,`is_deleted`);\n", StringUtils.join(ddl, CharacterConstants.SEPARATOR_EMPTY));
    }

    @Test
    @Order(0)
    @DisplayName("测试生成表DDL")
    public void testCreateTable() {
        // 执行测试用例
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(TestTable.MODEL_MODEL);
        List<String> ddl = tableComputer.compute(null, null, modelConfig, null).getDdl();

        // 结果断言
        AssertionErrors.assertEquals("测试生成基本创建表DDL功能失败", "CREATE TABLE IF NOT EXISTS `PAMIRS_TABLES`(\n" +
                " `ID` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',\n" +
                " `TABLE_SCHEMA` VARCHAR(128) COMMENT '',\n" +
                " `TABLE_NAME` VARCHAR(128) COMMENT '',\n" +
                " `TABLE_COMMENT` VARCHAR(128) COMMENT '',\n" +
                " `MODEL` VARCHAR(128) COMMENT '',\n" +
                " `CREATE_DATE` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                " `WRITE_DATE` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',\n" +
                " `CREATE_UID` BIGINT(20) COMMENT '创建人',\n" +
                " `WRITE_UID` BIGINT(20) COMMENT '更新人',\n" +
                " `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',\n" +
                " PRIMARY KEY (`ID`)\n" +
                ")ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '';\n" +
                "CREATE INDEX `PAMIRS_TABLES_TABLE_SCHEMA` ON `PAMIRS_TABLES`(`TABLE_SCHEMA`);\n" +
                "CREATE INDEX `PAMIRS_TABLES_MODEL` ON `PAMIRS_TABLES`(`MODEL`);\n" +
                "CREATE UNIQUE INDEX `PAMIRS_TABLES_TABLE_NAME_TABLE_SCHEMA` ON `PAMIRS_TABLES`(`TABLE_NAME`,`TABLE_SCHEMA`,`is_deleted`);\n", StringUtils.join(ddl, CharacterConstants.SEPARATOR_EMPTY));
    }

    @Test
    @Order(0)
    @DisplayName("测试生成逻辑表DDL")
    public void testCreateLogicTable() {
        // 执行测试用例
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(TestLogicTable.MODEL_MODEL);
        List<String> ddl = tableComputer.compute(null, null, modelConfig, null).getDdl();

        // 结果断言
        AssertionErrors.assertEquals("测试生成基本创建表DDL功能失败", "CREATE TABLE IF NOT EXISTS `PAMIRS_LOGIC_TABLES`(\n" +
                " `ID` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',\n" +
                " `MODULE` VARCHAR(128) COMMENT '',\n" +
                " `MODEL` VARCHAR(128) COMMENT '',\n" +
                " `TABLE_NAME` VARCHAR(128) COMMENT '',\n" +
                " `TABLE_COMMENT` VARCHAR(128) COMMENT '',\n" +
                " `CREATE_DATE` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                " `WRITE_DATE` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',\n" +
                " `CREATE_UID` BIGINT(20) COMMENT '创建人',\n" +
                " `WRITE_UID` BIGINT(20) COMMENT '更新人',\n" +
                " `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',\n" +
                " PRIMARY KEY (`ID`)\n" +
                ")ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '';\n" +
                "CREATE INDEX `PAMIRS_LOGIC_TABLES_MODULE` ON `PAMIRS_LOGIC_TABLES`(`MODULE`);\n" +
                "CREATE UNIQUE INDEX `PAMIRS_LOGIC_TABLES_MODEL` ON `PAMIRS_LOGIC_TABLES`(`MODEL`,`is_deleted`);\n", StringUtils.join(ddl, CharacterConstants.SEPARATOR_EMPTY));
    }

    @Test
    @Order(0)
    @DisplayName("测试生成逻辑列DDL")
    public void testCreateLogicColumn() {
        // 执行测试用例
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(TestColumn.MODEL_MODEL);
        List<String> ddl = tableComputer.compute(null, null, modelConfig, null).getDdl();

        // 结果断言
        AssertionErrors.assertEquals("测试生成基本创建表DDL功能失败", "CREATE TABLE IF NOT EXISTS `PAMIRS_LOGIC_COLUMNS`(\n" +
                " `ID` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',\n" +
                " `MODULE` VARCHAR(128) COMMENT '',\n" +
                " `MODEL` VARCHAR(128) COMMENT '',\n" +
                " `FIELD` VARCHAR(128) COMMENT '',\n" +
                " `TABLE_SCHEMA` VARCHAR(128) COMMENT '',\n" +
                " `TABLE_NAME` VARCHAR(128) COMMENT '',\n" +
                " `COLUMN_NAME` VARCHAR(128) COMMENT '',\n" +
                " `COLUMN_TYPE` VARCHAR(128) COMMENT '',\n" +
                " `IS_NULLABLE` TINYINT(1) COMMENT '',\n" +
                " `COLUMN_DEFAULT` VARCHAR(128) COMMENT '',\n" +
                " `EXTRA` VARCHAR(128) COMMENT '',\n" +
                " `ORDINAL_POSITION` BIGINT(20) COMMENT '',\n" +
                " `COLUMN_COMMENT` VARCHAR(128) COMMENT '',\n" +
                " `CREATE_DATE` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                " `WRITE_DATE` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',\n" +
                " `CREATE_UID` BIGINT(20) COMMENT '创建人',\n" +
                " `WRITE_UID` BIGINT(20) COMMENT '更新人',\n" +
                " `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',\n" +
                " PRIMARY KEY (`ID`)\n" +
                ")ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT 'testColumn';\n" +
                "CREATE INDEX `PAMIRS_LOGIC_COLUMNS_MODULE` ON `PAMIRS_LOGIC_COLUMNS`(`MODULE`);\n" +
                "CREATE UNIQUE INDEX `PAMIRS_LOGIC_COLUMNS_MODEL_FIELD` ON `PAMIRS_LOGIC_COLUMNS`(`MODEL`,`FIELD`,`is_deleted`);\n", StringUtils.join(ddl, CharacterConstants.SEPARATOR_EMPTY));
    }

    @Test
    @Order(0)
    @DisplayName("测试生成逻辑索引DDL")
    public void testCreateLogicIndex() {
        // 执行测试用例
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(TestLogicIndex.MODEL_MODEL);
        List<String> ddl = tableComputer.compute(null, null, modelConfig, null).getDdl();

        // 结果断言
        AssertionErrors.assertEquals("测试生成基本创建表DDL功能失败", "CREATE TABLE IF NOT EXISTS `PAMIRS_LOGIC_INDEXES`(\n" +
                " `ID` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',\n" +
                " `MODULE` VARCHAR(128) COMMENT '',\n" +
                " `MODEL` VARCHAR(128) COMMENT '',\n" +
                " `INDEX_NAME` VARCHAR(128) COMMENT '',\n" +
                " `COLUMN_NAME` VARCHAR(128) COMMENT '',\n" +
                " `SEQ_IN_INDEX` INT(11) COMMENT '',\n" +
                " `NON_UNIQUE` TINYINT(1) COMMENT '',\n" +
                " `CREATE_DATE` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                " `WRITE_DATE` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',\n" +
                " `CREATE_UID` BIGINT(20) COMMENT '创建人',\n" +
                " `WRITE_UID` BIGINT(20) COMMENT '更新人',\n" +
                " `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',\n" +
                " PRIMARY KEY (`ID`)\n" +
                ")ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '';\n" +
                "CREATE INDEX `PAMIRS_LOGIC_INDEXES_MODULE` ON `PAMIRS_LOGIC_INDEXES`(`MODULE`);\n" +
                "CREATE UNIQUE INDEX `PAMIRS_LOGIC_INDEXES_MODEL_INDEX_NAME_COLUMN_NAME` ON `PAMIRS_LOGIC_INDEXES`(`MODEL`,`INDEX_NAME`,`COLUMN_NAME`,`is_deleted`);\n", StringUtils.join(ddl, CharacterConstants.SEPARATOR_EMPTY));
    }

}
