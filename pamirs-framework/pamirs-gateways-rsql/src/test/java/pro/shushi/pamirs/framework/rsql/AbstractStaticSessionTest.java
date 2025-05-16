package pro.shushi.pamirs.framework.rsql;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import pro.shushi.pamirs.AbstractInitSessionTest;
import pro.shushi.pamirs.framework.gateways.convert.RsqlValueConverter;
import pro.shushi.pamirs.framework.gateways.convert.impl.RsqlBooleanConverter;
import pro.shushi.pamirs.framework.gateways.convert.impl.RsqlEnumConverter;
import pro.shushi.pamirs.meta.api.core.faas.FunApi;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.common.lambda.Func;
import pro.shushi.pamirs.meta.common.lambda.ref.*;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Adamancy Zhang at 15:49 on 2025-04-09
 */
public abstract class AbstractStaticSessionTest extends AbstractInitSessionTest {

    @BeforeAll
    @Order(10)
    public static void beforeAll() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
        initSPILoader();
    }

    private static void initSPILoader() throws NoSuchFieldException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        initSPIService(RsqlValueConverter.class, new RsqlBooleanConverter(), RsqlBooleanConverter.SPI_NAME);
        initSPIService(RsqlValueConverter.class, new RsqlEnumConverter(), RsqlEnumConverter.SPI_NAME);
        initSPIService(FunApi.class, new FunApi() {

            @Override
            public Function fetch(String namespace, String fun) {
                return null;
            }

            @Override
            public Function fetchAllowNull(String namespace, String fun) {
                return null;
            }

            @Override
            public <T, P, R> Function fetch(Func<T, P, R> function) {
                return null;
            }

            @Override
            public <T, R> Function fetch(Func0<T, R> function) {
                return null;
            }

            @Override
            public <T, A1, A2, R> Function fetch(Func2<T, A1, A2, R> function) {
                return null;
            }

            @Override
            public <T, A1, A2, A3, R> Function fetch(Func3<T, A1, A2, A3, R> function) {
                return null;
            }

            @Override
            public <T, A1, A2, A3, A4, R> Function fetch(Func4<T, A1, A2, A3, A4, R> function) {
                return null;
            }

            @Override
            public <T, A1, A2, A3, A4, A5, R> Function fetch(Func5<T, A1, A2, A3, A4, A5, R> function) {
                return null;
            }

            @Override
            public <T, A1, A2, A3, A4, A5, A6, R> Function fetch(Func6<T, A1, A2, A3, A4, A5, A6, R> function) {
                return null;
            }

            @Override
            public Function fetch(Method method) {
                return null;
            }

            @Override
            public Function generate(FunctionDefinition functionDefinition) {
                return null;
            }

            @SuppressWarnings("unchecked")
            @Override
            public <T> T run(String namespace, String fun, Object... args) {
                if (FunctionConstants.queryListByWrapper.equals(fun)) {
                    List<DataMap> list = new ArrayList<>();
                    for (int i = 1; i <= 3; i++) {
                        DataMap map = new DataMap();
                        map.put("id", Long.valueOf(String.valueOf(i)));
                        map.put("a", "a" + i);
                        list.add(map);
                    }
                    return (T) list;
                }
                return null;
            }

            @Override
            public <T> T run(Function function, Object... args) {
                return null;
            }

            @Override
            public <T, P, R> R run(Func<T, P, R> function, Object... args) {
                return null;
            }

            @Override
            public <T, R> R run(Func0<T, R> function, Object... args) {
                return null;
            }

            @Override
            public <T, A1, A2, R> R run(Func2<T, A1, A2, R> function, Object... args) {
                return null;
            }

            @Override
            public <T, A1, A2, A3, R> R run(Func3<T, A1, A2, A3, R> function, Object... args) {
                return null;
            }

            @Override
            public <T, A1, A2, A3, A4, R> R run(Func4<T, A1, A2, A3, A4, R> function, Object... args) {
                return null;
            }

            @Override
            public <T, A1, A2, A3, A4, A5, R> R run(Func5<T, A1, A2, A3, A4, A5, R> function, Object... args) {
                return null;
            }

            @Override
            public <T, A1, A2, A3, A4, A5, A6, R> R run(Func6<T, A1, A2, A3, A4, A5, A6, R> function, Object... args) {
                return null;
            }
        });
    }
}
