package pro.shushi.pamirs.framework.connectors.data.plugin.sequence;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.ArrayUtil;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import pro.shushi.pamirs.framework.connectors.data.constant.DbConstants;
import pro.shushi.pamirs.framework.connectors.data.dialect.DefaultJdbc3KeyGeneratorDialectService;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Jdbc3KeyGeneratorDialectService;
import pro.shushi.pamirs.framework.connectors.data.util.DataConfigurationHelper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * 主键回填
 * <p>
 * 2020/7/1 4:26 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class PamirsJdbc3KeyGenerator extends Jdbc3KeyGenerator {

    @SuppressWarnings("unused")
    public static final Jdbc3KeyGenerator INSTANCE = new PamirsJdbc3KeyGenerator();
    private static final String MSG_TOO_MANY_KEYS = "Too many keys are generated. There are only %d target objects. You either specified a wrong 'keyProperty' or encountered a driver bug like #1523.";
    private final static String FIELD_UTIL_NAME = "pro.shushi.pamirs.framework.connectors.data.plugin.util.FieldUtil";

    public PamirsJdbc3KeyGenerator() {
    }

    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
    }

    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        this.processBatch(ms, stmt, parameter);
    }

    @Override
    public void processBatch(MappedStatement ms, Statement stmt, Object parameter) {
        final String[] keyProperties = KeyPropertiesGetter.get(ms, parameter);
        if (keyProperties == null || keyProperties.length == 0) {
            return;
        }
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            final ResultSetMetaData rsmd = rs.getMetaData();
            final Configuration configuration = ms.getConfiguration();
            //noinspection StatementWithEmptyBody
            if (rsmd.getColumnCount() < keyProperties.length) {
                // Error?
            } else {
                assignKeys(configuration, rs, rsmd, keyProperties, parameter);
            }
        } catch (Exception e) {
            throw new ExecutorException("Error getting generated key or setting result to parameter object. Cause: " + e, e);
        }
    }

    @SuppressWarnings("unchecked")
    private void assignKeys(Configuration configuration, ResultSet rs, ResultSetMetaData rsmd, String[] keyProperties, Object parameter) throws SQLException {
        //noinspection deprecation
        if (parameter instanceof MapperMethod.ParamMap || parameter instanceof DefaultSqlSession.StrictMap) {
            // Multi-param or single param with @Param
            assignKeysToParamMap(configuration, rs, rsmd, keyProperties, (Map<String, ?>) parameter);
        } else if (parameter instanceof ArrayList && !((ArrayList<?>) parameter).isEmpty()
                && ((ArrayList<?>) parameter).get(0) instanceof MapperMethod.ParamMap) {
            // Multi-param or single param with @Param in batch operation
            assignKeysToParamMapList(configuration, rs, rsmd, keyProperties, (ArrayList<MapperMethod.ParamMap<?>>) parameter);
        } else {
            // Single param without @Param
            assignKeysToParam(configuration, rs, rsmd, keyProperties, parameter);
        }
    }

    private void assignKeysToParam(Configuration configuration, ResultSet rs, ResultSetMetaData rsmd, String[] keyProperties, Object parameter) throws SQLException {
        Collection<?> params = collectionize(parameter);
        if (params.isEmpty()) {
            return;
        }
        List<Jdbc3KeyGeneratorDialectService.KeyAssigner> assignerList = new ArrayList<>();
        for (int i = 0; i < keyProperties.length; i++) {
            int columnPosition = i + 1;
            String columnName = rsmd.getColumnName(columnPosition);
            assignerList.add(generatorKeyAssigner(configuration, rsmd, columnPosition, null, keyProperties[i], columnName));
        }
        Iterator<?> iterator = params.iterator();
        while (rs.next()) {
            if (!iterator.hasNext()) {
                throw new ExecutorException(String.format(MSG_TOO_MANY_KEYS, params.size()));
            }
            Object param = iterator.next();
            assignerList.forEach(x -> x.assign(rs, param));
        }
    }

    private void assignKeysToParamMapList(Configuration configuration, ResultSet rs, ResultSetMetaData rsmd, String[] keyProperties, ArrayList<MapperMethod.ParamMap<?>> paramMapList) throws SQLException {
        Iterator<MapperMethod.ParamMap<?>> iterator = paramMapList.iterator();
        List<Jdbc3KeyGeneratorDialectService.KeyAssigner> assignerList = new ArrayList<>();

        for (long counter = 0L; rs.next(); ++counter) {
            if (!iterator.hasNext()) {
                throw new ExecutorException(String.format("Too many keys are generated. There are only %d target objects. You either specified a wrong 'keyProperty' or encountered a driver bug like #1523.", counter));
            }

            MapperMethod.ParamMap<?> paramMap = iterator.next();
            if (assignerList.isEmpty()) {
                for (int i = 0; i < keyProperties.length; ++i) {
                    int columnPosition = i + 1;
                    String columnName = rsmd.getColumnName(columnPosition);
                    assignerList.add(this.getAssignerForParamMap(configuration, rsmd, columnPosition, paramMap,
                            keyProperties[i], columnName, keyProperties, false).getValue());
                }
            }

            assignerList.forEach((x) -> x.assign(rs, paramMap));
        }

    }

    private void assignKeysToParamMap(Configuration configuration, ResultSet rs, ResultSetMetaData rsmd, String[] keyProperties, Map<String, ?> paramMap) throws SQLException {
        if (paramMap.isEmpty()) {
            return;
        }
        Map<String, Map.Entry<Iterator<?>, List<Jdbc3KeyGeneratorDialectService.KeyAssigner>>> assignerMap = new HashMap<>();
        for (int i = 0; i < keyProperties.length; i++) {
            int columnPosition = i + 1;
            String columnName = rsmd.getColumnName(columnPosition);
            Map.Entry<String, Jdbc3KeyGeneratorDialectService.KeyAssigner> entry = getAssignerForParamMap(configuration, rsmd, columnPosition, paramMap,
                    keyProperties[i], columnName, keyProperties, true);
            Map.Entry<Iterator<?>, List<Jdbc3KeyGeneratorDialectService.KeyAssigner>> iteratorPair = assignerMap.computeIfAbsent(entry.getKey(),
                    k -> entry(collectionize(paramMap.get(k)).iterator(), new ArrayList<>()));
            iteratorPair.getValue().add(entry.getValue());
        }
        long counter = 0;
        while (rs.next()) {
            for (Map.Entry<Iterator<?>, List<Jdbc3KeyGeneratorDialectService.KeyAssigner>> pair : assignerMap.values()) {
                if (!pair.getKey().hasNext()) {
                    throw new ExecutorException(String.format(MSG_TOO_MANY_KEYS, counter));
                }
                Object param = pair.getKey().next();
                pair.getValue().forEach(x -> x.assign(rs, param));
            }
            counter++;
        }
    }

    private Map.Entry<String, Jdbc3KeyGeneratorDialectService.KeyAssigner> getAssignerForParamMap(Configuration config, ResultSetMetaData rsmd, int columnPosition, Map<String, ?> paramMap,
                                                                                                  String keyProperty, String keyColumn, String[] keyProperties, boolean omitParamName) {
        boolean singleParam = paramMap.values().stream().distinct().count() == (paramMap.containsKey(DbConstants.PARAM_ANNOTATION_EXT) ? 3L : 2L);
        int firstDot = keyProperty.indexOf(46);
        if (firstDot == -1) {
            if (singleParam) {
                return this.getAssignerForSingleParam(config, rsmd, columnPosition, paramMap, keyProperty, keyColumn, omitParamName);
            } else {
                throw new ExecutorException("Could not determine which parameter to assign generated keys to. Note that when there are multiple parameters, 'keyProperty' must include the parameter name (e.g. 'param.id'). Specified key properties are " + ArrayUtil.toString(keyProperties) + " and available parameters are " + paramMap.keySet());
            }
        } else {
            String paramName = keyProperty.substring(0, firstDot);
            if (paramMap.containsKey(paramName)) {
                String argParamName = omitParamName ? null : paramName;
                String argKeyProperty = keyProperty.substring(firstDot + 1);
                return entry(paramName, generatorKeyAssigner(config, rsmd, columnPosition, argParamName, argKeyProperty, keyColumn));
            } else if (singleParam) {
                return this.getAssignerForSingleParam(config, rsmd, columnPosition, paramMap, keyProperty, keyColumn, omitParamName);
            } else {
                throw new ExecutorException("Could not find parameter '" + paramName + "'. Note that when there are multiple parameters, 'keyProperty' must include the parameter name (e.g. 'param.id'). Specified key properties are " + ArrayUtil.toString(keyProperties) + " and available parameters are " + paramMap.keySet());
            }
        }
    }

    private Map.Entry<String, Jdbc3KeyGeneratorDialectService.KeyAssigner> getAssignerForSingleParam(Configuration config, ResultSetMetaData rsmd, int columnPosition, Map<String, ?> paramMap, String keyProperty, String keyColumn, boolean omitParamName) {
        String singleParamName = nameOfSingleParam(paramMap);
        String argParamName = omitParamName ? null : singleParamName;
        return entry(singleParamName, generatorKeyAssigner(config, rsmd, columnPosition, argParamName, keyProperty, keyColumn));
    }

    private static String nameOfSingleParam(Map<String, ?> paramMap) {
        String name = null;
        for (String key : paramMap.keySet()) {
            Object tool = paramMap.get(key);
            if (!tool.getClass().getName().equals(FIELD_UTIL_NAME)) {
                name = key;
            }
        }
        return name;
    }

    private static Collection<?> collectionize(Object param) {
        if (param instanceof Collection) {
            return (Collection<?>) param;
        } else if (param instanceof Object[]) {
            return Arrays.asList((Object[]) param);
        } else {
            return Collections.singletonList(param);
        }
    }

    private static <K, V> Map.Entry<K, V> entry(K key, V value) {
        // Replace this with Map.entry(key, value) in Java 9.
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }

    private Jdbc3KeyGeneratorDialectService.KeyAssigner generatorKeyAssigner(Configuration configuration, ResultSetMetaData rsmd, int columnPosition, String paramName,
                                                                             String propertyName, String columnName) {
        Jdbc3KeyGeneratorDialectService service = Dialects.component(Jdbc3KeyGeneratorDialectService.class, DataConfigurationHelper.getDsKey());
        if (service == null) {
            service = DefaultJdbc3KeyGeneratorDialectService.INSTANCE;
        }
        return service.generatorKeyAssigner(configuration, rsmd, columnPosition, paramName, propertyName, columnName);
    }
}
