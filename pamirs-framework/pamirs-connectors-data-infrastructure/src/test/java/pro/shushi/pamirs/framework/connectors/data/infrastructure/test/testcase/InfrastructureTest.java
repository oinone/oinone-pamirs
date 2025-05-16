package pro.shushi.pamirs.framework.connectors.data.infrastructure.test.testcase;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.AssertionErrors;
import pro.shushi.pamirs.AbstractBaseWithSessionTest;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.dialect.ScriptDialectService;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.dialect.mysql.MysqlScriptDialectService;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.domain.fun.ExtPointImplementation;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.module.ModuleCategory;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基础设施生成测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@Slf4j
@DisplayName("基础设施生成测试")
public class InfrastructureTest extends AbstractBaseWithSessionTest {

    @Resource
    private GenericMapper genericMapper;

    @Resource
    private MysqlScriptDialectService mysqlScriptDialectService;

    @Test
    @Order(0)
    @DisplayName("测试生成库表DDL")
    public void testCreateDatabase() {

        // 准备数据
        List<Meta> metaList = new ArrayList<>();

        Meta base = fetchMeta(ModuleConstants.MODULE_BASE);
        metaList.add(base);

        // 执行测试用例
        testDDLUnit(metaList, () -> {
            // 不作修改
        });

        // 结果断言
        AssertionErrors.assertEquals("", "", "");
    }

    @Test
    @Order(0)
    @DisplayName("测试字段排序")
    public void testFieldOrder() {

        // 准备数据
        List<Meta> metaList = new ArrayList<>();

        Meta base = fetchMeta(ModuleConstants.MODULE_BASE);
        metaList.add(base);

        // 执行测试用例
        testDDLUnit(metaList, () -> {
            // 变更字段排序
            metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence").setPriority(99L);
        });

        testDDLUnit(metaList, () -> {
            // 恢复字段排序
            metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence").setPriority(100L);
        });

        // 结果断言
        AssertionErrors.assertEquals("", "", "");
    }

    @Test
    @Order(0)
    @DisplayName("测试添加索引")
    public void testAddIndex() {

        // 准备数据
        List<Meta> metaList = new ArrayList<>();

        Meta base = fetchMeta(ModuleConstants.MODULE_BASE);
        metaList.add(base);

        // 执行测试用例
        testDDLUnit(metaList, () -> {
            // 添加索引
            metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence").setIndex(true);
        });

        testDDLUnit(metaList, () -> {
            // 恢复索引
            metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence").setIndex(false);
        });

        // 结果断言
        AssertionErrors.assertEquals("", "", "");
    }

    @Test
    @Order(0)
    @DisplayName("测试添加唯一索引")
    public void testAddUniqueIndex() {

        // 准备数据
        List<Meta> metaList = new ArrayList<>();

        Meta base = fetchMeta(ModuleConstants.MODULE_BASE);
        metaList.add(base);

        // 执行测试用例
        testDDLUnit(metaList, () -> {
            // 添加索引
            metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence").setUnique(true);
        });

        testDDLUnit(metaList, () -> {
            // 恢复索引
            metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence").setUnique(false);
        });

        // 结果断言
        AssertionErrors.assertEquals("", "", "");
    }

    @Test
    @Order(0)
    @DisplayName("测试修改添加修改复合索引")
    public void testModifyFieldCompositeIndex() {

        // 准备数据
        List<Meta> metaList = new ArrayList<>();

        Meta base = fetchMeta(ModuleConstants.MODULE_BASE);
        metaList.add(base);

        // 执行测试用例
        ModelDefinition modelDefinition = metaList.get(0).getModel(ExtPointImplementation.MODEL_MODEL);
        testDDLUnit(metaList, () -> {
            // 修改复合索引
            modelDefinition.setIndexes(Lists.newArrayList("displayName, name"));
        });

        testDDLUnit(metaList, () -> {
            // 恢复复合索引
            modelDefinition.setIndexes(Lists.newArrayList("namespace, name"));
        });

        // 结果断言
        AssertionErrors.assertEquals("", "", "");
    }

    // 修改复合唯一索引
    @Test
    @Order(0)
    @DisplayName("测试修改添加修改复合唯一索引")
    public void testModifyFieldCompositeUniqueIndex() {

        // 准备数据
        List<Meta> metaList = new ArrayList<>();

        Meta base = fetchMeta(ModuleConstants.MODULE_BASE);
        metaList.add(base);

        // 执行测试用例
        ModelDefinition modelDefinition = metaList.get(0).getModel(ModuleCategory.MODEL_MODEL);
        testDDLUnit(metaList, () -> {
            // 修改复合唯一索引
            modelDefinition.setUniques(Lists.newArrayList("name, code"));
        });

        testDDLUnit(metaList, () -> {
            // 恢复复合唯一索引
            modelDefinition.setUniques(Lists.newArrayList());
        });

        // 结果断言
        AssertionErrors.assertEquals("", "", "");
    }

    @Test
    @Order(0)
    @DisplayName("测试异常回放")
    public void testPlayBack() {

        // 准备数据
        List<Meta> metaList = new ArrayList<>();

        Meta base = fetchMeta(ModuleConstants.MODULE_BASE);
        metaList.add(base);

        testDDLUnit(metaList, () -> {

        });

        Objects.requireNonNull(PamirsSession.getContext()).addModelConfig(new ModelConfig(metaList.get(0).getModel(ModuleCategory.MODEL_MODEL)));
        // 插入重复数据
        genericMapper.insertOrUpdateBatch(Lists.newArrayList(
                new DataMap().setModel(ModuleCategory.MODEL_MODEL).setValue("code", "1").setValue("name", "1").setValue("sequence", 1),
                new DataMap().setModel(ModuleCategory.MODEL_MODEL).setValue("code", "2").setValue("name", "2").setValue("sequence", 1))
        );

        genericMapper.insertOrUpdateBatch(Lists.newArrayList(
                new DataMap().setModel(ModuleCategory.MODEL_MODEL).setValue("code", "1").setValue("name", "1").setValue("sequence", 1),
                new DataMap().setModel(ModuleCategory.MODEL_MODEL).setValue("code", "2").setValue("name", "2").setValue("sequence", 1))
        );

        // 执行测试用例
        // 添加唯一索引
        metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence").setUnique(true);
        try {
            testDDLUnit(metaList, () -> {

            });
        } catch (Exception e) {
            // 索引冲突
            log.debug("索引冲突", e);
            Dialects.component(ScriptDialectService.class, "base").unlockTables("base");
            genericMapper.deleteByEntity(new DataMap().setModel(ModuleCategory.MODEL_MODEL).setValue("code", "2"));
            genericMapper.deleteByEntity(new DataMap().setModel(ModuleCategory.MODEL_MODEL).setValue("code", "1"));
        }

        // 添加索引
        testDDLUnit(metaList, () -> {
            // 恢复索引
            metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence").setUnique(false);
        });

        // 结果断言
        AssertionErrors.assertEquals("", "", "");
    }

    @Test
    @Order(0)
    @DisplayName("测试修改表名")
    public void testTableRename() {

        // 准备数据
        List<Meta> metaList = new ArrayList<>();

        Meta base = fetchMeta(ModuleConstants.MODULE_BASE);
        metaList.add(base);

        // 执行测试用例
        String table = metaList.get(0).getModel(ModuleCategory.MODEL_MODEL).getTable();
        testDDLUnit(metaList, () -> {
            // 修改表名
            metaList.get(0).getModel(ModuleCategory.MODEL_MODEL).setTable(table + 1);
        });

        testDDLUnit(metaList, () -> {
            // 恢复表名
            metaList.get(0).getModel(ModuleCategory.MODEL_MODEL).setTable(table);
        });

        // 结果断言
        AssertionErrors.assertEquals("", "", "");
    }

    // 测试修改表名，修改索引，修改唯一索引，修改表备注，添加字段，删除字段，修改字段名，修改字段类型，修改字段备注
    @Test
    @Order(0)
    @DisplayName("测试复杂修改")
    public void testComplexModify() {

        // 准备数据
        List<Meta> metaList = new ArrayList<>();

        Meta base = fetchMeta(ModuleConstants.MODULE_BASE);
        metaList.add(base);

        // 执行测试用例
        String table = metaList.get(0).getModel(ModuleCategory.MODEL_MODEL).getTable();
        String summary = metaList.get(0).getModel(ModuleCategory.MODEL_MODEL).getSummary();
        ModelDefinition modelDefinition = metaList.get(0).getModel(ModuleCategory.MODEL_MODEL);
        ModelField sequenceModelField = metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence");

        ModelField modelField = metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence");
        final ModelField newField = JsonUtils.parseObject(JsonUtils.toJSONString(modelField), ModelField.class);
        newField.setColumn("test2").setField("test2").setName("test2").setSign("base.ModuleCategory.test2");
        testDDLUnit(metaList, () -> {
            // 修改表名
            metaList.get(0).getModel(ModuleCategory.MODEL_MODEL).setTable(table + 1);
            // 修改表备注
            metaList.get(0).getModel(ModuleCategory.MODEL_MODEL).setSummary(summary + 1);
            // 添加索引
            metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence").setIndex(true);
            // 添加唯一索引
            metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence").setUnique(false);
            // 修改主键
            modelDefinition.setPk(Lists.newArrayList("id", "sequence"));
            sequenceModelField.setPk(true).setPkIndex(1);
            // 添加字段
            metaList.get(0).getModel(ModuleCategory.MODEL_MODEL).getModelFields().add(newField);
            // 修改字段名
            metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence").setColumn("test");
            // 修改字段类型
            metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence").setTtype(TtypeEnum.STRING).setSize(128);
            // 修改字段备注
            metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence").setSummary(summary + 1);
        });

        testDDLUnit(metaList, () -> {
            // 恢复表名
            metaList.get(0).getModel(ModuleCategory.MODEL_MODEL).setTable(table);
            // 恢复表备注
            metaList.get(0).getModel(ModuleCategory.MODEL_MODEL).setSummary(summary);
            // 添加索引
            metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence").setIndex(true);
            // 添加唯一索引
            metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence").setUnique(false);
            // 恢复主键
            modelDefinition.setPk(Lists.newArrayList("id"));
            sequenceModelField.setPk(false);
            // 删除字段，无效果，因为已经不做退化处理
            metaList.get(0).getModel(ModuleCategory.MODEL_MODEL).getModelFields().remove(newField);
            // 修改字段名
            metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence").setColumn("sequence");
            // 恢复字段类型
            metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence").setTtype(TtypeEnum.INTEGER);
            // 恢复字段备注
            metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence").setSummary(summary);
        });

        // 结果断言
        AssertionErrors.assertEquals("", "", "");
    }

    @Test
    @Order(0)
    @DisplayName("测试修改表备注")
    public void testTableRemark() {

        // 准备数据
        List<Meta> metaList = new ArrayList<>();

        Meta base = fetchMeta(ModuleConstants.MODULE_BASE);
        metaList.add(base);

        // 执行测试用例
        String summary = metaList.get(0).getModel(ModuleCategory.MODEL_MODEL).getSummary();
        testDDLUnit(metaList, () -> {
            // 修改表备注
            metaList.get(0).getModel(ModuleCategory.MODEL_MODEL).setSummary(summary + 1);
        });

        testDDLUnit(metaList, () -> {
            // 恢复表备注
            metaList.get(0).getModel(ModuleCategory.MODEL_MODEL).setSummary(summary);
        });

        // 结果断言
        AssertionErrors.assertEquals("", "", "");
    }

    @Test
    @Order(0)
    @DisplayName("测试添加删除字段")
    public void testAddField() {

        // 准备数据
        List<Meta> metaList = new ArrayList<>();

        Meta base = fetchMeta(ModuleConstants.MODULE_BASE);
        metaList.add(base);

        // 执行测试用例
        ModelField modelField = metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence");
        final ModelField newField = JsonUtils.parseObject(JsonUtils.toJSONString(modelField), ModelField.class);
        newField.setColumn("test1").setField("test1").setName("test1").setSign("base.ModuleCategory.test1");
        testDDLUnit(metaList, () -> {
            // 添加字段
            metaList.get(0).getModel(ModuleCategory.MODEL_MODEL).getModelFields().add(newField);
        });

        testDDLUnit(metaList, () -> {
            // 删除字段，无效果，因为已经不做退化处理
            metaList.get(0).getModel(ModuleCategory.MODEL_MODEL).getModelFields().remove(newField);
        });

        // 结果断言
        AssertionErrors.assertEquals("", "", "");
    }

    @Test
    @Order(0)
    @DisplayName("测试修改字段名")
    public void testModifyFieldName() {

        // 准备数据
        List<Meta> metaList = new ArrayList<>();

        Meta base = fetchMeta(ModuleConstants.MODULE_BASE);
        metaList.add(base);

        // 执行测试用例
        testDDLUnit(metaList, () -> {
            // 修改字段名
            metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence").setColumn("test");
        });

        testDDLUnit(metaList, () -> {
            // 恢复字段名
            metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence").setColumn("sequence");
        });

        // 结果断言
        AssertionErrors.assertEquals("", "", "");
    }

    @Test
    @Order(0)
    @DisplayName("测试修改字段类型")
    public void testModifyFieldTtype() {

        // 准备数据
        List<Meta> metaList = new ArrayList<>();

        Meta base = fetchMeta(ModuleConstants.MODULE_BASE);
        metaList.add(base);

        // 执行测试用例
        testDDLUnit(metaList, () -> {
            // 修改字段类型
            metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence").setTtype(TtypeEnum.STRING).setSize(128);
        });

        testDDLUnit(metaList, () -> {
            // 恢复字段类型
            metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence").setTtype(TtypeEnum.INTEGER);
        });

        // 结果断言
        AssertionErrors.assertEquals("", "", "");
    }

    @Test
    @Order(0)
    @DisplayName("测试修改字段备注")
    public void testModifyFieldRemark() {

        // 准备数据
        List<Meta> metaList = new ArrayList<>();

        Meta base = fetchMeta(ModuleConstants.MODULE_BASE);
        metaList.add(base);

        // 执行测试用例
        ModelField modelField = metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence");
        String summary = modelField.getSummary();
        testDDLUnit(metaList, () -> {
            // 修改字段备注
            metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence").setSummary(summary + 1);
        });

        testDDLUnit(metaList, () -> {
            // 恢复字段备注
            metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence").setSummary(summary);
        });

        // 结果断言
        AssertionErrors.assertEquals("", "", "");
    }

    @Test
    @Order(0)
    @DisplayName("测试修改添加修改主键")
    public void testModifyFieldPrimaryKey() {

        // 准备数据
        List<Meta> metaList = new ArrayList<>();

        Meta base = fetchMeta(ModuleConstants.MODULE_BASE);
        metaList.add(base);

        mysqlScriptDialectService.run("base", "delete from module_category;");

        // 执行测试用例
        ModelDefinition modelDefinition = metaList.get(0).getModel(ModuleCategory.MODEL_MODEL);
        ModelField idModelField = metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "id");
        ModelField sequenceModelField = metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence");
        testDDLUnit(metaList, () -> {
            // 修改主键
            modelDefinition.setPk(Lists.newArrayList("sequence"));
            idModelField.setPk(false);
            sequenceModelField.setPk(true);
        });

        testDDLUnit(metaList, () -> {
            // 恢复主键
            modelDefinition.setPk(Lists.newArrayList("id"));
            idModelField.setPk(true);
            sequenceModelField.setPk(false);
        });

        // 结果断言
        AssertionErrors.assertEquals("", "", "");
    }

    @Test
    @Order(0)
    @DisplayName("测试修改添加修改复合主键")
    public void testModifyFieldCompositePrimaryKey() {

        // 准备数据
        List<Meta> metaList = new ArrayList<>();

        Meta base = fetchMeta(ModuleConstants.MODULE_BASE);
        metaList.add(base);

        // 执行测试用例
        ModelDefinition modelDefinition = metaList.get(0).getModel(ModuleCategory.MODEL_MODEL);
        ModelField sequenceModelField = metaList.get(0).getModelField(ModuleCategory.MODEL_MODEL, "sequence");
        testDDLUnit(metaList, () -> {
            // 修改主键
            modelDefinition.setPk(Lists.newArrayList("id", "sequence"));
            sequenceModelField.setPk(true).setPkIndex(1);
        });

        testDDLUnit(metaList, () -> {
            // 恢复主键
            modelDefinition.setPk(Lists.newArrayList("id"));
            sequenceModelField.setPk(false);
        });

        // 结果断言
        AssertionErrors.assertEquals("", "", "");
    }

    @Test
    @Order(0)
    @DisplayName("测试分库分表")
    public void testSharding() {

        // 准备数据
        String baseModule = "base";
        MetaData baseMeta = fetchMetaData(baseModule);

        String test1Module = "test1";
        Map<String, MetaData> dependencyMetaData1 = new ConcurrentHashMap<>();
        dependencyMetaData1.put(baseModule, baseMeta);
        Meta test1Meta = fetchMeta(test1Module, dependencyMetaData1);

        List<Meta> metaList = new ArrayList<>();
        metaList.add(test1Meta);

        // 执行测试用例
        testDDLUnit(metaList, () -> {

        });

        // 结果断言
        AssertionErrors.assertEquals("", "", "");
    }

    @Test
    @Order(0)
    @DisplayName("测试分库分表修改字段")
    public void testShardingAndModifyField() {

        // 准备数据
        String baseModule = "base";
        MetaData baseMeta = fetchMetaData(baseModule);

        String test1Module = "test1";
        Map<String, MetaData> dependencyMetaData1 = new ConcurrentHashMap<>();
        dependencyMetaData1.put(baseModule, baseMeta);
        Meta test1Meta = fetchMeta(test1Module, dependencyMetaData1);

        String test2Module = "test2";
        Map<String, MetaData> dependencyMetaData2 = new ConcurrentHashMap<>();
        dependencyMetaData2.put(baseModule, baseMeta);
        dependencyMetaData2.put(test1Module, test1Meta.getCurrentModuleData());
        Meta test2Meta = fetchMeta(test2Module, dependencyMetaData2);

        List<Meta> metaList = new ArrayList<>();
        metaList.add(test2Meta);

        // 执行测试用例
        testDDLUnit(metaList, () -> {
            // 修改字段名
            metaList.get(0).getModelField("test.Model2", "field").setColumn("test");
        });

        testDDLUnit(metaList, () -> {
            // 恢复字段名
            metaList.get(0).getModelField("test.Model2", "field").setColumn("field");
        });

        // 结果断言
        AssertionErrors.assertEquals("", "", "");
    }

}
