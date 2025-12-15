package pro.shushi.pamirs.ux.quickfilling.converter;

import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.ux.quickfilling.converter.defaults.*;
import pro.shushi.pamirs.ux.quickfilling.enumeration.QuickFillingExpEnumerate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 快速填报转换API工厂
 *
 * @author Adamancy Zhang at 12:26 on 2025-11-27
 */
@Slf4j
public class QuickFillingConverterFactory {

    private static final String REFERENCES_KEY_PREFIX = "references#";

    private static final class QuickFillingConverterApiFactoryHolder {

        private static final QuickFillingConverterFactory INSTANCE = init();

        private static QuickFillingConverterFactory init() {
            QuickFillingConverterFactory factory = new QuickFillingConverterFactory();
            factory.register(TtypeEnum.STRING, StringConverter.class);
            factory.register(TtypeEnum.TEXT, StringConverter.class);
            factory.register(TtypeEnum.HTML, StringConverter.class);
            factory.register(TtypeEnum.PHONE, StringConverter.class);
            factory.register(TtypeEnum.EMAIL, StringConverter.class);
            factory.register(TtypeEnum.INTEGER, NumberConverter.class);
            factory.register(TtypeEnum.FLOAT, NumberConverter.class);
            factory.register(TtypeEnum.MONEY, NumberConverter.class);
            factory.register(TtypeEnum.UID, NumberConverter.class);
            factory.register(TtypeEnum.BOOLEAN, BooleanConverter.class);
            factory.register(TtypeEnum.DATETIME, DatetimeConverter.class);
            factory.register(TtypeEnum.DATE, DateConverter.class);
            factory.register(TtypeEnum.TIME, TimeConverter.class);
            factory.register(TtypeEnum.YEAR, YearConverter.class);
            factory.register(TtypeEnum.ENUM, EnumConverter.class);
            factory.register(TtypeEnum.MAP, MapConverter.class);
            factory.register(TtypeEnum.O2O, M2OConverter.class);
            factory.register(TtypeEnum.M2O, M2OConverter.class);
            factory.register(TtypeEnum.O2M, M2MConverter.class);
            factory.register(TtypeEnum.M2M, M2MConverter.class);
            List<QuickFillingConverterRegister> registers = BeanDefinitionUtils.getBeansOfTypeByOrdered(QuickFillingConverterRegister.class);
            for (QuickFillingConverterRegister register : registers) {
                register.register(factory);
            }
            return factory;
        }
    }

    private final Map<String, Class<?>> storage;

    private QuickFillingConverterFactory() {
        this.storage = new HashMap<>();
    }

    public <API extends AbstractQuickFillingConverter> void register(TtypeEnum ttype, Class<API> clazz) {
        storage.put(ttype.value(), clazz);
    }

    public <API extends AbstractQuickFillingConverter> void registerByReferences(String references, Class<API> clazz) {
        storage.put(REFERENCES_KEY_PREFIX + references, clazz);
    }

    public <API extends AbstractQuickFillingConverter> void registerByReferences(TtypeEnum ttype, String references, Class<API> clazz) {
        storage.put(REFERENCES_KEY_PREFIX + references + CharacterConstants.SEPARATOR_OCTOTHORPE + ttype.value(), clazz);
    }

    public <API extends AbstractQuickFillingConverter> void registerByField(String model, String field, Class<API> clazz) {
        storage.put(model + CharacterConstants.SEPARATOR_OCTOTHORPE + field, clazz);
    }

    private QuickFillingConverter selector(QuickFillingColumn column) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clazz = selector0(column);
        Constructor<?> constructor = clazz.getConstructor(QuickFillingColumn.class);
        return (QuickFillingConverter) constructor.newInstance(column);
    }

    private Class<?> selector0(QuickFillingColumn column) {
        String model = column.getModel();
        String field = column.getField();
        String ttype = column.getTtype();
        Class<?> clazz = storage.get(model + CharacterConstants.SEPARATOR_OCTOTHORPE + field);
        if (clazz != null) {
            return clazz;
        }
        String references = column.getReferences();
        if (references != null) {
            clazz = storage.get(REFERENCES_KEY_PREFIX + references + CharacterConstants.SEPARATOR_OCTOTHORPE + ttype);
            if (clazz != null) {
                return clazz;
            }
        }
        clazz = storage.get(ttype);
        if (clazz == null) {
            throw new UnsupportedOperationException("Invalid quick filling converter. model: " + model + ", field: " + field + ", ttype: " + ttype);
        }
        return clazz;
    }

    public static QuickFillingConverter getApi(QuickFillingColumn column) {
        try {
            return QuickFillingConverterApiFactoryHolder.INSTANCE.selector(column);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof PamirsException) {
                throw (PamirsException) cause;
            }
            throw PamirsException.construct(QuickFillingExpEnumerate.SELECT_CONVERTER_ERROR, column.getModel(), column.getField(), column.getTtype(), e).errThrow();
        } catch (Throwable e) {
            throw PamirsException.construct(QuickFillingExpEnumerate.SELECT_CONVERTER_ERROR, column.getModel(), column.getField(), column.getTtype(), e).errThrow();
        }
    }
}
