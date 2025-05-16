package pro.shushi.pamirs.framework.connectors.data.ddl.test.testcase;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.AssertionErrors;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.framework.connectors.data.api.ddl.TableComputer;
import pro.shushi.pamirs.framework.connectors.data.ddl.test.mock.model.*;
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
    @DisplayName("测试生成基本创建表DDL功能，带乐观锁")
    public void testCreateTable(){
        // 执行测试用例
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(TestModel.modelModel);
        List<String> ddl = tableComputer.compute(null, null, modelConfig, null).getDdl();

        // 结果断言
        AssertionErrors.assertEquals("测试生成基本创建表DDL功能失败", "CREATE TABLE IF NOT EXISTS `test_model`(\n" +
                " `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',\n" +
                " `module` VARCHAR(128) COMMENT '模块',\n" +
                " `version` INT(11) COMMENT '版本',\n" +
                " `relation` TINYINT(1) COMMENT '关系',\n" +
                " `date` DATETIME COMMENT '时间',\n" +
                " `opt_version` BIGINT(20) COMMENT '乐观锁',\n" +
                " `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                " `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',\n" +
                " `create_uid` BIGINT(20) COMMENT '创建人',\n" +
                " `write_uid` BIGINT(20) COMMENT '更新人',\n" +
                " `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',\n" +
                " PRIMARY KEY (`id`)\n" +
                ")ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '测试模型';\n" +
                "CREATE UNIQUE INDEX `test_model_module` ON `test_model`(`module`,`is_deleted`);\n", StringUtils.join(ddl, CharacterConstants.SEPARATOR_EMPTY));
    }

    @Test
    @Order(0)
    @DisplayName("测试生成创建pk表DDL功能")
    public void testCreatePkTable(){
        // 执行测试用例
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(TestIdModel.modelModel);
        List<String> ddl = tableComputer.compute(null, null, modelConfig, null).getDdl();

        // 结果断言
        AssertionErrors.assertEquals("测试生成创建pk表DDL功能失败", "CREATE TABLE IF NOT EXISTS `test_id_model`(\n" +
                " `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',\n" +
                " `module` VARCHAR(128) COMMENT '模块',\n" +
                " `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                " `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',\n" +
                " `create_uid` BIGINT(20) COMMENT '创建人',\n" +
                " `write_uid` BIGINT(20) COMMENT '更新人',\n" +
                " `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',\n" +
                " PRIMARY KEY (`id`)\n" +
                ")ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '测试模型';\n" +
                "CREATE UNIQUE INDEX `test_id_model_module` ON `test_id_model`(`module`,`is_deleted`);\n", StringUtils.join(ddl, CharacterConstants.SEPARATOR_EMPTY));
    }

    @Test
    @Order(0)
    @DisplayName("测试生成分表DDL功能")
    public void testCreateShardingTable(){
        // 执行测试用例
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(TestShardingModel.modelModel);
        List<String> ddl = tableComputer.compute(null, null, modelConfig, null).getDdl();

        // 结果断言
        AssertionErrors.assertEquals("测试生成分表DDL功能失败", "CREATE TABLE IF NOT EXISTS `test_sharding_model_${tableSuffix}`(\n" +
                " `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID字段，唯一自增索引',\n" +
                " `module` VARCHAR(128) COMMENT '模块',\n" +
                " `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                " `write_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',\n" +
                " `create_uid` BIGINT(20) COMMENT '创建人',\n" +
                " `write_uid` BIGINT(20) COMMENT '更新人',\n" +
                " `is_deleted` bigint(20) default 0 COMMENT '逻辑删除',\n" +
                " PRIMARY KEY (`id`)\n" +
                ")ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '测试模型';\n" +
                "CREATE UNIQUE INDEX `test_sharding_model_module` ON `test_sharding_model_${tableSuffix}`(`module`,`is_deleted`);\n", StringUtils.join(ddl, CharacterConstants.SEPARATOR_EMPTY));
    }

    @Test
    @Order(0)
    @DisplayName("测试生成系统数据库表DDL功能")
    public void testCreateSystemDatabase(){
        // 执行测试用例
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(DatabaseSchema.MODEL_MODEL);
        List<String> ddl = tableComputer.compute(null, null, modelConfig, null).getDdl();

        // 结果断言
        AssertionErrors.assertEquals("测试生成系统数据库表DDL功能失败", "CREATE TABLE IF NOT EXISTS `PAMIRS_DATABASES`(\n" +
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
    @DisplayName("测试生成系统表DDL功能")
    public void testCreateSystemTable(){
        // 执行测试用例
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(TestTable.MODEL_MODEL);
        List<String> ddl = tableComputer.compute(null, null, modelConfig, null).getDdl();

        // 结果断言
        AssertionErrors.assertEquals("测试生成系统表DDL功能失败", "CREATE TABLE IF NOT EXISTS `PAMIRS_TABLES`(\n" +
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

}
