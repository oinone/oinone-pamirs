package pro.shushi.pamirs.framework.rsql;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLHelper;
import pro.shushi.pamirs.framework.gateways.rsql.RsqlParseHelper;
import pro.shushi.pamirs.framework.gateways.rsql.connector.RSQLToSQLNodeConnector;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.api.session.cache.api.DataDictionaryCacheApi;
import pro.shushi.pamirs.meta.api.session.cache.api.ModelCacheApi;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adamancy Zhang at 09:42 on 2025-04-09
 */
public class RsqlParserTest extends AbstractStaticSessionTest {

    private static final List<String> rsqlList = Lists.newArrayList(
            "1 == 1",
            "1 != 1",
            "a == '123'",
            "a != '123'",
            "a =in= ('123','456','789')",
            "a =out= ('123','456','789')",
            "a =isnull= true",
            "a =isnull= false",
            "a =notnull= true",
            "a =notnull= false",
            "a =like= '123'",
            "a =starts= '123'",
            "a =ends= '123'",
            "a =notlike= '123'",
            "a =notstarts= '123'",
            "a =notends= '123'",
            "a =cole= b",
            "a =colnot= b",
            "b =ge= '111' and b =le= '333'",
            "c =ge= '111' and c =le= '333'",
            "d =ge= '111' and d =le= '333'",
            "b =gt= '111' and b =lt= '333'",
            "c =gt= '111' and c =lt= '333'",
            "d =gt= '111' and d =lt= '333'",
            "e1 == 'A'",
            "e2 == 'A' or e2 == 'B'",
            "e3 =bit= ('A', 'B')",
            "e3 =notbit= ('A', 'B')",
            "e3 =has= ('A', 'B')",
            "e3 =hasnt= ('A', 'B')",
            "e3 =hasor= ('A', 'B')",
            "e3 =hasntor= ('A', 'B')",

            "a == null",
            "a != null",
            "a =like= null",
            "a =starts= null",
            "a =ends= null",
            "a =notlike= null",
            "a =notstarts= null",
            "a =notends= null",

//            "e3 =bit= null",
//            "e3 =notbit= null",
//            "e3 =has= null",
//            "e3 =hasnt= null",
//            "e3 =hasor= null",
//            "e3 =hasntor= null",

            "o1.a == '123'",

            "os1.a == '123'",
            "os1.a != '123'",
            "os1.a =in= ('123','456','789')",
            "os1.a =out= ('123','456','789')",
            "os1.a =isnull= true",
            "os1.a =isnull= false",
            "os1.a =notnull= true",
            "os1.a =notnull= false",
            "os1.a =like= '123'",
            "os1.a =starts= '123'",
            "os1.a =ends= '123'",
            "os1.a =notlike= '123'",
            "os1.a =notstarts= '123'",
            "os1.a =notends= '123'",

            "os3.a == '123'",
            "os3.a != '123'",
//            "os3.a =in= ('123','456','789')", // db unsupported
//            "os3.a =out= ('123','456','789')", // db unsupported
            "os3.a =isnull= true",
            "os3.a =isnull= false",
            "os3.a =notnull= true",
            "os3.a =notnull= false",
            "os3.a =like= '123'",
//            "os3.a =starts= '123'", // db unsupported
//            "os3.a =ends= '123'", // db unsupported
            "os3.a =notlike= '123'",
//            "os3.a =notstarts= '123'", // db unsupported
//            "os3.a =notends= '123'", // db unsupported

            "os1.a =cole= b",
            "os1.a =colnot= b",
            "os1.b =ge= '111' and os1.b =le= '333'",
            "os1.c =ge= '111' and os1.c =le= '333'",
            "os1.d =ge= '111' and os1.d =le= '333'",
            "os1.b =gt= '111' and os1.b =lt= '333'",
            "os1.c =gt= '111' and os1.c =lt= '333'",
            "os1.d =gt= '111' and os1.d =lt= '333'",
            "os1.e1 == 'A'",
            "os1.e2 == 'A' or os1.e2 == 'B'",
            "os1.e3 =bit= ('A', 'B')",
            "os1.e3 =notbit= ('A', 'B')",
            "os1.e3 =has= ('A', 'B')",
            "os1.e3 =hasnt= ('A', 'B')",
            "os1.e3 =hasor= ('A', 'B')",
            "os1.e3 =hasntor= ('A', 'B')",

            "" // the end
    );
    private static final List<String> sqlList = Lists.newArrayList(
            "1 = 1",
            "1 <> 1",
            "`a` = '123'",
            "`a` <> '123'",
            "`a` IN ('123','456','789')",
            "`a` NOT IN ('123','456','789')",
            "`a` IS NULL",
            "`a` IS NOT NULL",
            "`a` IS NOT NULL",
            "`a` IS NULL",
            "`a` LIKE '%123%'",
            "`a` LIKE '123%'",
            "`a` LIKE '%123'",
            "`a` NOT LIKE '%123%'",
            "`a` NOT LIKE '123%'",
            "`a` NOT LIKE '%123'",
            "`a` = `b`",
            "`a` <> `b`",
            "`b` >= '111' AND `b` <= '333'",
            "`c` >= '111' AND `c` <= '333'",
            "`d` >= '111' AND `d` <= '333'",
            "`b` > '111' AND `b` < '333'",
            "`c` > '111' AND `c` < '333'",
            "`d` > '111' AND `d` < '333'",
            "`e1` = 'A'",
            "`e2` = 1 OR `e2` = 2",
            "`e3` = 3",
            "`e3` <> 3",
            "`e3` & 3 = 3",
            "`e3` & 3 <> 3",
            "`e3` & 3 > 0",
            "`e3` & 3 = 0",

            "`a` = 'null'",
            "`a` <> 'null'",
            "`a` LIKE '%null%'",
            "`a` LIKE 'null%'",
            "`a` LIKE '%null'",
            "`a` NOT LIKE '%null%'",
            "`a` NOT LIKE 'null%'",
            "`a` NOT LIKE '%null'",

//            "`e3` = 'null'",
//            "`e3` <> 3",
//            "`e3` & 3 = 3",
//            "`e3` & 3 <> 3",
//            "`e3` & 3 > 0",
//            "`e3` & 3 = 0",

            "`o1_id` IN (1,2,3)",

            "JSON_EXTRACT(`os1`, '$.a') = '123'",
            "JSON_EXTRACT(`os1`, '$.a') <> '123'",
            "JSON_EXTRACT(`os1`, '$.a') IN ('123','456','789')",
            "JSON_EXTRACT(`os1`, '$.a') NOT IN ('123','456','789')",
            "JSON_EXTRACT(`os1`, '$.a') IS NULL",
            "JSON_EXTRACT(`os1`, '$.a') IS NOT NULL",
            "JSON_EXTRACT(`os1`, '$.a') IS NOT NULL",
            "JSON_EXTRACT(`os1`, '$.a') IS NULL",
            "JSON_EXTRACT(`os1`, '$.a') LIKE '%123%'",
            "JSON_EXTRACT(`os1`, '$.a') LIKE '123%'",
            "JSON_EXTRACT(`os1`, '$.a') LIKE '%123'",
            "JSON_EXTRACT(`os1`, '$.a') NOT LIKE '%123%'",
            "JSON_EXTRACT(`os1`, '$.a') NOT LIKE '123%'",
            "JSON_EXTRACT(`os1`, '$.a') NOT LIKE '%123'",

            "JSON_CONTAINS(JSON_EXTRACT(`os3`, '$[*].a'), '\"123\"', '$') = true",
            "JSON_CONTAINS(JSON_EXTRACT(`os3`, '$[*].a'), '\"123\"', '$') = false",
//            "",
//            "",
            "JSON_CONTAINS(JSON_EXTRACT(`os3`, '$[*].a'), '\"\"', '$') IS NULL",
            "JSON_CONTAINS(JSON_EXTRACT(`os3`, '$[*].a'), '\"\"', '$') IS NOT NULL",
            "JSON_CONTAINS(JSON_EXTRACT(`os3`, '$[*].a'), '\"\"', '$') IS NOT NULL",
            "JSON_CONTAINS(JSON_EXTRACT(`os3`, '$[*].a'), '\"\"', '$') IS NULL",
            "JSON_SEARCH(JSON_EXTRACT(`os3`, '$[*].a'), '123', 'one', NULL, '$') IS NOT NULL",
//            "JSON_EXTRACT(`os3`, '$.a') LIKE '123%'",
//            "JSON_EXTRACT(`os3`, '$.a') LIKE '%123'",
            "JSON_SEARCH(JSON_EXTRACT(`os3`, '$[*].a'), '123', 'one', NULL, '$') IS NULL",
//            "JSON_EXTRACT(`os3`, '$.a') NOT LIKE '123%'",
//            "JSON_EXTRACT(`os3`, '$.a') NOT LIKE '%123'",

            "JSON_EXTRACT(`os1`, '$.a') = `b`",
            "JSON_EXTRACT(`os1`, '$.a') <> `b`",
            "JSON_EXTRACT(`os1`, '$.b') >= '111' AND JSON_EXTRACT(`os1`, '$.b') <= '333'",
            "JSON_EXTRACT(`os1`, '$.c') >= '111' AND JSON_EXTRACT(`os1`, '$.c') <= '333'",
            "JSON_EXTRACT(`os1`, '$.d') >= '111' AND JSON_EXTRACT(`os1`, '$.d') <= '333'",
            "JSON_EXTRACT(`os1`, '$.b') > '111' AND JSON_EXTRACT(`os1`, '$.b') < '333'",
            "JSON_EXTRACT(`os1`, '$.c') > '111' AND JSON_EXTRACT(`os1`, '$.c') < '333'",
            "JSON_EXTRACT(`os1`, '$.d') > '111' AND JSON_EXTRACT(`os1`, '$.d') < '333'",
            "JSON_EXTRACT(`os1`, '$.e1') = 'A'",
            "JSON_EXTRACT(`os1`, '$.e2') = 1 OR JSON_EXTRACT(`os1`, '$.e2') = 2",
            "JSON_EXTRACT(`os1`, '$.e3') = 3",
            "JSON_EXTRACT(`os1`, '$.e3') <> 3",
            "JSON_EXTRACT(`os1`, '$.e3') & 3 = 3",
            "JSON_EXTRACT(`os1`, '$.e3') & 3 <> 3",
            "JSON_EXTRACT(`os1`, '$.e3') & 3 > 0",
            "JSON_EXTRACT(`os1`, '$.e3') & 3 = 0",

            "" // the end
    );

    @Test
    public void test() {
        initSession();
        test0();
    }

    private void test0() {
        int l = rsqlList.size();
        for (int i = 0; i < l; i++) {
            test0(i);
        }
    }

    private void test0(int i) {
        test0(i, false);
    }

    private void test0(int i, boolean printResult) {
        String rsql = null;
        String sql = null;
        try {
            rsql = rsqlList.get(i);
            if (StringUtils.isBlank(rsql)) {
                return;
            }
            sql = sqlList.get(i);
        } catch (Throwable e) {
            System.out.printf("error assert.\nrsql: %s\nsql: %s\n\n", rsql, sql);
            throw e;
        }
        String originResult = null;
        String result = null;
        try {
            originResult = RsqlParseHelper.parseRsql2Sql(TEST_MODEL, rsql);
        } catch (Throwable e) {
            System.out.printf("origin parser unsupported operation. %d\nrsql: %s\nsql: %s\n\n", i, rsql, sql);
            e.printStackTrace();
        }
        try {
            result = RSQLHelper.toTargetString(RSQLHelper.parse(TEST_MODEL, rsql), RSQLToSQLNodeConnector.INSTANCE);
        } catch (Throwable e) {
            System.out.printf("not equals. %d\nrsql: %s\nsql: %s\noriginResult: %s\nresult: %s\n\n", i, rsql, sql, originResult, result);
            throw e;
        }
        assert sql.equals(result) : String.format("result not equals. %d\nrsql: %s\nsql: %s\noriginResult: %s\nresult: %s", i, rsql, sql, originResult, result);
        if (originResult != null && !sql.equals(originResult)) {
            System.out.printf("format not equals. %d\nrsql: %s\nsql: %s\noriginResult: %s\nresult: %s\n\n", i, rsql, sql, originResult, result);
        }
        if (printResult) {
            printInfo("test result", i, rsql, sql, originResult, result);
        }
    }

    private void printInfo(String title, int i, String rsql, String sql, String originResult, String result) {
        System.out.printf("%s. %d\nrsql: %s\nsql: %s\noriginResult: %s\nresult: %s\n\n", title, i, rsql, sql, originResult, result);
    }

    private static final String TEST_MODEL = "test.TestModel";

    private static final String REFERENCE_MODEL = "test.TestReferenceModel";

    private static final String DICTIONARY1 = "test.TestStringEnum";

    private static final String DICTIONARY2 = "test.TestIntegerEnum";

    private static final String DICTIONARY3 = "test.TestBitEnum";

    private void initSession() {
        DataDictionaryCacheApi dataDictionaryCache = PamirsSession.getContext().getDictCache();
        DataDictionary dataDictionary1 = generatorDataDictionary(DICTIONARY1, "A", "B", "C", "D", "E");
        dataDictionaryCache.put(dataDictionary1.getDictionary(), dataDictionary1);
        DataDictionary dataDictionary2 = generatorDataDictionary(DICTIONARY2, 1, 2, 3, 4, 5);
        dataDictionaryCache.put(dataDictionary2.getDictionary(), dataDictionary2);
        DataDictionary dataDictionary3 = generatorDataDictionary(DICTIONARY3, 1L, 2L, 4L, 8L);
        dataDictionaryCache.put(dataDictionary3.getDictionary(), dataDictionary3);

        ModelCacheApi modelCache = PamirsSession.getContext().getModelCache();

        ModelConfig testModelConfig = generatorModelConfig(TEST_MODEL);
        modelCache.put(testModelConfig.getModel(), testModelConfig);

        ModelConfig testReferenceModelConfig = generatorModelConfig(REFERENCE_MODEL);
        modelCache.put(testReferenceModelConfig.getModel(), testReferenceModelConfig);

        addReferenceFields(testModelConfig, testReferenceModelConfig);
    }

    private void addReferenceFields(ModelConfig relationModel, ModelConfig referenceModel) {
        addReferenceFields(generatorModelFieldByReference(relationModel.getModel(), "o1", TtypeEnum.O2O, referenceModel), relationModel, referenceModel);
        addReferenceFields(generatorModelFieldByReference(relationModel.getModel(), "o2", TtypeEnum.M2O, referenceModel), relationModel, referenceModel);
        addReferenceFields(generatorModelFieldByReference(relationModel.getModel(), "o3", TtypeEnum.O2M, referenceModel), relationModel, referenceModel);
        addReferenceFields(generatorModelFieldByReference(relationModel.getModel(), "o4", TtypeEnum.M2M, referenceModel), relationModel, referenceModel);

        addReferenceFields(generatorModelFieldByReferenceWithStore(relationModel.getModel(), "os1", TtypeEnum.O2O, referenceModel), relationModel, referenceModel);
        addReferenceFields(generatorModelFieldByReferenceWithStore(relationModel.getModel(), "os2", TtypeEnum.M2O, referenceModel), relationModel, referenceModel);
        addReferenceFields(generatorModelFieldByReferenceWithStore(relationModel.getModel(), "os3", TtypeEnum.O2M, referenceModel), relationModel, referenceModel);
        addReferenceFields(generatorModelFieldByReferenceWithStore(relationModel.getModel(), "os4", TtypeEnum.M2M, referenceModel), relationModel, referenceModel);
    }

    private void addReferenceFields(List<ModelField> modelFields, ModelConfig relationModel, ModelConfig referenceModel) {
        List<ModelField> relationModelFields = relationModel.getModelDefinition().getModelFields();
        List<ModelField> referenceModelFields = referenceModel.getModelDefinition().getModelFields();
        for (ModelField modelField : modelFields) {
            if (relationModel.getModel().equals(modelField.getModel())) {
                relationModelFields.add(modelField);
            } else if (referenceModel.getModel().equals(modelField.getModel())) {
                referenceModelFields.add(modelField);
            } else {
                throw new IllegalArgumentException("Invalid model field.");
            }
        }
    }

    @SafeVarargs
    private final <T> DataDictionary generatorDataDictionary(String dictionary, T... values) {
        DataDictionary dataDictionary = new DataDictionary();
        dataDictionary.setDictionary(dictionary);
        dataDictionary.setName(dictionary);
        dataDictionary.setLname(dictionary);
        dataDictionary.setDisplayName(dictionary);
        dataDictionary.setBit(Boolean.FALSE);
        String names = "ABCDE";
        List<DataDictionaryItem> options = new ArrayList<>();
        for (int i = 0; i < names.length(); i++) {
            if (values.length <= i) {
                break;
            }
            T value = values[i];
            if (i == 0) {
                if (value instanceof String) {
                    dataDictionary.setValueType(TtypeEnum.STRING);
                } else if (value instanceof Integer) {
                    dataDictionary.setValueType(TtypeEnum.INTEGER);
                } else if (value instanceof Long) {
                    dataDictionary.setValueType(TtypeEnum.INTEGER);
                    dataDictionary.setBit(Boolean.TRUE);
                } else {
                    throw new UnsupportedOperationException("Invalid value type.");
                }
            }
            options.add(generatorDataDictionaryItem(String.valueOf(names.charAt(i)), String.valueOf(value)));
        }
        dataDictionary.setOptions(options);
        return dataDictionary;
    }

    private DataDictionaryItem generatorDataDictionaryItem(String name, String value) {
        DataDictionaryItem item = new DataDictionaryItem();
        item.setDisplayName(name);
        item.setName(name);
        item.setValue(value);
        item.setState(ActiveEnum.ACTIVE);
        item.setSource(SystemSourceEnum.MANUAL);
        return item;
    }

    private ModelConfig generatorModelConfig(String model) {
        ModelDefinition modelDefinition = new ModelDefinition();
        modelDefinition.setModel(model);
        modelDefinition.setPk(Lists.newArrayList("id"));
        modelDefinition.setModelFields(Lists.newArrayList(
                generatorModelField(model, "id", TtypeEnum.INTEGER),
                generatorModelField(model, "a", TtypeEnum.STRING),
                generatorModelField(model, "b", TtypeEnum.INTEGER),
                generatorModelField(model, "c", TtypeEnum.FLOAT),
                generatorModelField(model, "d", TtypeEnum.MONEY),
                generatorModelFieldByEnum(model, "e1", TtypeEnum.ENUM, DICTIONARY1),
                generatorModelFieldByEnum(model, "e2", TtypeEnum.ENUM, DICTIONARY2),
                generatorModelFieldByEnum(model, "e3", TtypeEnum.ENUM, DICTIONARY3)
        ));
        return new ModelConfig(modelDefinition);
    }

    private ModelField generatorModelField(String model, String field, TtypeEnum ttype) {
        ModelField modelField = new ModelField();
        modelField.setModel(model);
        modelField.setTtype(ttype);
        modelField.setField(field);
        modelField.setLname(field);
        modelField.setName(field);
        modelField.setColumn(PStringUtils.fieldName2Column(field));
        modelField.setMulti(Boolean.FALSE);
        modelField.setOnlyColumn(Boolean.TRUE);
        modelField.setStore(Boolean.TRUE);
        modelField.setRelationStore(Boolean.FALSE);
        modelField.setInsertStrategy(FieldStrategyEnum.DEFAULT);
        modelField.setBatchStrategy(FieldStrategyEnum.NOT_CHANGE);
        modelField.setUpdateStrategy(FieldStrategyEnum.DEFAULT);
        modelField.setWhereStrategy(FieldStrategyEnum.DEFAULT);
        return modelField;
    }

    private ModelField generatorModelFieldByEnum(String model, String field, TtypeEnum ttype, String dictionary) {
        ModelField modelField = generatorModelField(model, field, ttype);
        DataDictionary dataDictionary = PamirsSession.getContext().getDictionary(dictionary);
        modelField.setDictionary(dictionary);
        modelField.setOptions(dataDictionary.getOptions());
        if (dataDictionary.getBit()) {
            modelField.setMulti(Boolean.TRUE);
            modelField.setStoreSerialize(SerializeEnum.BIT.value());
        }
        return modelField;
    }

    private List<ModelField> generatorModelFieldByReference(String model, String field, TtypeEnum ttype, ModelConfig referenceModel) {
        List<ModelField> modelFields = new ArrayList<>();
        ModelField modelField = generatorModelField(model, field, ttype);
        modelField.setReferences(referenceModel.getModel());
        modelField.setStore(Boolean.FALSE);
        modelField.setRelationStore(Boolean.TRUE);
        modelFields.add(modelField);

        if (TtypeEnum.isRelationOne(ttype)) {
            ModelField relationField = generatorModelField(model, field + "Id", TtypeEnum.INTEGER);
            modelFields.add(relationField);
            modelField.setRelationFields(Lists.newArrayList(relationField.getField()));
            modelField.setReferenceFields(Lists.newArrayList("id"));
        }
        return modelFields;
    }

    private List<ModelField> generatorModelFieldByReferenceWithStore(String model, String field, TtypeEnum ttype, ModelConfig referenceModel) {
        List<ModelField> modelFields = new ArrayList<>();
        ModelField modelField = generatorModelField(model, field, ttype);
        modelField.setReferences(referenceModel.getModel());
        modelField.setStoreSerialize(Field.serialize.JSON);
        modelFields.add(modelField);
        return modelFields;
    }
}
