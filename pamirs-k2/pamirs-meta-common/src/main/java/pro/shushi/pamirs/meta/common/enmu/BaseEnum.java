package pro.shushi.pamirs.meta.common.enmu;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.shushi.pamirs.meta.common.enmu.api.BaseEnumApi;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.util.AppClassLoader;
import pro.shushi.pamirs.meta.common.util.BitUtil;

import jakarta.annotation.Nonnull;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 抽象枚举
 * <p>
 * 请大家注意，不建议出现继承同一枚举类的name相同的情况
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/17 1:53 上午
 */
@SuppressWarnings("rawtypes")
public abstract class BaseEnum<E extends BaseEnum<E, T>, T extends Serializable> implements Comparable<E>, IEnum<T>, Serializable {

    private static final long serialVersionUID = -4247792692954746321L;
    private static final Logger logger = LoggerFactory.getLogger(BaseEnum.class);
    private static final String EMPTY = "_EMPTY";

    /**
     * <code>Map</code>, key of dictionary, value of <code>Entry</code>.
     */
    private static Map<String, Entry> cEnumClasses = new WeakHashMap<>();

    /**
     * <code>Map</code>, key of class name, value of dictionary.
     */
    private static Map<Class<BaseEnum>, String> cEnumDictionaries = new WeakHashMap<>();


    private String name; //英文名称
    private String displayName; //显示名称
    private T value; //值
    private String help;  //描述
    private Map<String, Object> attributes; //属性
    private int ordinal;  //排序

    protected transient String iToString = null;


    private static class Entry {

        String rootDictionary;

        final Map<String, BaseEnum> map = new HashMap<>();

        final Map<String, BaseEnum> unmodifiableMap = Collections.unmodifiableMap(map);

        final List<BaseEnum> list = new ArrayList<>(25);

        final List<BaseEnum> unmodifiableList = Collections.unmodifiableList(list);

        protected Entry() {
            super();
        }

        private void populateNames(Class enumClass) {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (enumClass) {
                Field[] fields = enumClass.getFields();
                for (Field field : fields) {
                    int modifier = field.getModifiers();
                    if (Modifier.isPublic(modifier) && Modifier.isFinal(modifier) && Modifier.isStatic(modifier)) {
                        try {
                            Object value = field.get(null);
                            String fname = field.getName();
                            for (BaseEnum enumObject : unmodifiableList) {
                                if (value == enumObject && (enumObject.name == null) && !unmodifiableMap.containsKey(fname)) {
                                    enumObject.name = fname;
                                    //noinspection StringOperationCanBeSimplified
                                    map.put(new String(fname), enumObject);
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                        }
                    }
                }
            }
        }

    }

    public static <E extends BaseEnum> E create() {
        return create(null, null, null, null);
    }

    public static <E extends BaseEnum> E create(String name) {
        return create(name, null, null, null);
    }

    public static <E extends BaseEnum, T extends Serializable> E create(String name, T value) {
        return create(name, value, null, null);
    }

    public static <E extends BaseEnum, T extends Serializable> E create(T value, String displayName) {
        return create(null, value, displayName, null);
    }

    public static <E extends BaseEnum, T extends Serializable> E create(String name, T value, String displayName) {
        return create(name, value, displayName, null);
    }

    public static <E extends BaseEnum, T extends Serializable> E create(String name, T value, String displayName, String help) {
        return create(name, value, displayName, help, null);
    }

    public static <E extends BaseEnum, T extends Serializable> E create(String name, T value, String displayName, String help, Map<String, Object> attributes) {
        return create(name, value, displayName, help, attributes, 0);
    }

    @SuppressWarnings("unchecked")
    public static <E extends BaseEnum, T extends Serializable> E create(String name, T value, String displayName, String help, Map<String, Object> attributes, int ordinal) {
        BaseEnum menum = init(name);
        fill(name, value, displayName, help, attributes, ordinal, menum);
        return (E) menum;
    }

    public static <T extends Serializable> BaseEnum enumerate(String dictionary, String name, T value,
                                                              String displayName, String help,
                                                              Map<String, Object> attributes) {
        return enumerate(BaseEnum.class, dictionary, null, name, value, displayName, help, attributes);
    }

    public static <E extends BaseEnum, T extends Serializable> E enumerate(Class<E> enumClass, String dictionary, String name, T value,
                                                                           String displayName, String help,
                                                                           Map<String, Object> attributes) {
        return enumerate(enumClass, dictionary, null, name, value, displayName, help, attributes);
    }

    @SuppressWarnings("unchecked")
    private static <E extends BaseEnum, T extends Serializable> E enumerate(Class<E> enumClass, String dictionary, Entry entry,
                                                                            String name, T value,
                                                                            String displayName, String help,
                                                                            Map<String, Object> attributes) {
        if (null == entry) {
            entry = getEntry(dictionary);
        }
        BaseEnum menum = null;
        if (null != entry) {
            menum = entry.map.get(name);
        }
        if (null == menum) {
            menum = init(enumClass, dictionary, entry, name);
            fill(name, value, displayName, help, attributes, 0, menum);
        }
        return (E) menum;
    }

    private static <T extends Serializable> void fill(String name, T value, String displayName, String help, Map<String, Object> attributes, int ordinal, BaseEnum menum) {
        if (StringUtils.isNotBlank(name)) {
            menum.name = name;
        }
        menum.displayName = displayName;
        menum.value = value;
        menum.help = help;
        menum.ordinal = ordinal;
        menum.attributes = attributes;
        menum.refreshIToString();
    }

    public static <T extends Serializable> BaseEnum construct(String name, T value) {
        BaseEnum baseEnum = new BaseEnum() {
            private static final long serialVersionUID = 5890844175818444313L;

            @Override
            public int compareTo(@Nonnull Object other) {
                if (other == this) {
                    return 0;
                }
                if (other.getClass() != this.getClass()) {
                    throw new ClassCastException(
                            "Different enum class '" + ClassUtils.getShortClassName(other.getClass()) + "'");
                }
                return name.compareTo(((BaseEnum) other).name);
            }
        };
        if (StringUtils.isNotBlank(name)) {
            baseEnum.name = name;
        }
        baseEnum.value = value;
        return baseEnum;
    }

    @SuppressWarnings("unchecked")
    public static <E extends BaseEnum> E ref(BaseEnum refEnum) {
        Class<E> currentEnumClass;
        try {
            currentEnumClass = (Class<E>) ClassUtils.getClass(AppClassLoader.getClassLoader(BaseEnum.class), getCallerClassName());
            if (currentEnumClass == null) {
                throw new IllegalArgumentException("EnumClass must not be null");
            }

            return ref(currentEnumClass, refEnum, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <E extends BaseEnum> E ref(Class<E> resEnumClass, BaseEnum refEnum, Entry entry) {
        return (E) enumerate(resEnumClass, fetchDictionary(resEnumClass), entry,
                refEnum.name, refEnum.value, refEnum.displayName, refEnum.help, refEnum.attributes);
    }

    @SuppressWarnings("unchecked")
    private static <E extends BaseEnum> E init(String name) {
        Class<E> enumClass;
        try {
            enumClass = (Class<E>) ClassUtils.getClass(AppClassLoader.getClassLoader(BaseEnum.class), getCallerClassName());
            if (enumClass == null) {
                throw new IllegalArgumentException("EnumClass must not be null");
            }

            if (enumClass == BaseEnum.class) {
                throw new IllegalArgumentException("EnumClass should not be BaseEnum");
            }

            String dictionary = fetchDictionary(enumClass);
            if (null == dictionary) {
                dictionary = Spider.getDefaultExtension(BaseEnumApi.class).fetchDictionaryFromClass(enumClass);
            }
            return init(enumClass, dictionary, null, name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <E extends BaseEnum> E init(Class<E> enumClass, String dictionary, Entry existEntry, String name) {
        try {
            Entry entry;
            if (null != existEntry) {
                entry = existEntry;
            } else {
                synchronized (BaseEnum.class) {
                    entry = cEnumClasses.get(dictionary);
                    if (entry == null) {
                        if (enumClass != BaseEnum.class) {
                            Map dictMap = new WeakHashMap(cEnumDictionaries);
                            dictMap.put(enumClass, dictionary);
                            cEnumDictionaries = dictMap;
                        }
                        entry = createEntry(enumClass);
                        // we avoid the (Map) constructor to achieve JDK 1.2 support
                        Map myMap = new WeakHashMap(cEnumClasses);
                        myMap.put(dictionary, entry);
                        cEnumClasses = myMap;
                    }
                }
            }

            if (EMPTY.equals(name)) {
                return null;
            }

            BaseEnum enumObject = enumClass.newInstance();

            if (StringUtils.isNotEmpty(name)) {
                if (!entry.map.containsKey(name)) {
                    enumObject.name = name;
                    //noinspection StringOperationCanBeSimplified
                    entry.map.put(new String(name), enumObject);
                } else {
                    throw new IllegalArgumentException("The Enum name must be unique, '" + name + "' has already been added");
                }
            }
            //NAME为空的对象在MAP中没有，在对象实例化后，VALUE取LIST中的值，KEY取当前CLASS的FIELD PUT进去
            entry.list.add(enumObject);

            return (E) enumObject;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getCallerClassName() {
        StackTraceElement[] callers = new Throwable().getStackTrace();
        String enumClass = BaseEnum.class.getName();
        for (StackTraceElement caller : callers) {
            String className = caller.getClassName();
            String methodName = caller.getMethodName();
            if (!enumClass.equals(className) && "<clinit>".equals(methodName)) {
                return className;
            }
        }

        throw new IllegalArgumentException(enumClass);
    }

    @SuppressWarnings("unchecked")
    protected Object readResolve() {
        String dictionary = fetchDictionary(getEnumClass());
        Entry entry = cEnumClasses.get(dictionary);
        if (entry == null) {
            return null;
        }
        return entry.map.get(name());
    }

    private Class getEnumClass() {
        Class enumClass = getClass();
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (enumClass) {
            return enumClass;
        }
    }

    public static <E extends BaseEnum> String fetchDictionary(Class<E> enumClass) {
        String dictionary = cEnumDictionaries.get(enumClass);
        if (null == dictionary) {
            loadEnumClass(enumClass);
            return cEnumDictionaries.get(enumClass);
        }
        return dictionary;
    }

    //-----------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private static <E extends BaseEnum> Entry getEntry(Class<E> enumClass) {
        if (enumClass == null) {
            throw new IllegalArgumentException("The Enum Class must not be null");
        }
        if (!BaseEnum.class.isAssignableFrom(enumClass)) {
            throw new IllegalArgumentException("The Class must be a subclass of Enum");
        }
        String dictionary = fetchDictionary((Class<BaseEnum>) enumClass);
        Entry entry = cEnumClasses.get(dictionary);

        if (entry == null) {
            loadEnumClass(enumClass);
            dictionary = fetchDictionary((Class<BaseEnum>) enumClass);
            entry = cEnumClasses.get(dictionary);
            if (entry == null) {
                entry = createEntry(enumClass);
            }
        }

        return entry;
    }

    private static <E extends BaseEnum> void loadEnumClass(Class<E> enumClass) {
        try {
            Class.forName(enumClass.getName(), true, enumClass.getClassLoader());
        } catch (Exception e) {
            // Ignore
        }
    }

    private static Entry getEntry(String dictionary) {
        if (null == dictionary) {
            throw new IllegalArgumentException("The Enum dictionary must not be null");
        }
        return cEnumClasses.get(dictionary);
    }


    @SuppressWarnings("unchecked")
    private static Entry createEntry(Class enumClass) {
        Entry entry = new Entry();
        Class cls = enumClass.getSuperclass();
        entry.rootDictionary = fetchDictionary(enumClass);
        while (cls != null && cls != BaseEnum.class) {
            String dictionary = fetchDictionary(cls);
            Entry loopEntry = cEnumClasses.get(dictionary);
            if (loopEntry != null) {
                for (String name : loopEntry.map.keySet()) {
                    if (!entry.map.containsKey(name)) {
                        BaseEnum baseEnum = loopEntry.map.get(name);
                        ref(enumClass, baseEnum, entry);
                    }
                }
                entry.rootDictionary = loopEntry.rootDictionary;
                break;
            }
            cls = cls.getSuperclass();
        }
        return entry;
    }

    //--------------------------------------------------------------------------------

    /**
     * 根据枚举name获取枚举
     *
     * @param enumClass 枚举类
     * @param name      枚举值
     * @return 枚举
     */
    @SuppressWarnings("unchecked")
    public static <E extends BaseEnum> E getEnum(Class<E> enumClass, String name) {
        Entry entry = getEntry(enumClass);
        return (E) entry.map.get(name);
    }

    /**
     * 根据枚举值获取枚举
     *
     * @param dictionary 数据字典编码
     * @param value      枚举值
     * @return 枚举
     */
    public static <E extends BaseEnum, T extends Serializable> E getEnumByValue(String dictionary, T value) {
        if (dictionary == null) {
            throw new IllegalArgumentException("The Enum Dictionary must not be null");
        }
        if (value == null) {
            return null;
        }
        String valueString = String.valueOf(value);
        List<E> list = BaseEnum.getEnumList(dictionary);
        for (E enumeration : list) {
            if (valueString.equals(String.valueOf(enumeration.value()))) {
                return enumeration;
            }
        }
        return null;
    }

    /**
     * 根据枚举值获取枚举
     *
     * @param enumClass 枚举类
     * @param value     枚举值
     * @return 枚举
     */
    public static <E extends BaseEnum, T extends Serializable> E getEnumByValue(Class<E> enumClass, T value) {
        if (enumClass == null) {
            throw new IllegalArgumentException("The Enum Class must not be null");
        }
        String dictionary = fetchDictionary(enumClass);
        return getEnumByValue(dictionary, value);
    }

    /**
     * @param enumClass   枚举类
     * @param displayName 显示名称
     * @return 枚举
     */
    public static <E extends BaseEnum> E getEnumByDisplayName(Class<E> enumClass, String displayName) {
        if (enumClass == null) {
            throw new IllegalArgumentException("The Enum Class must not be null");
        }
        if (StringUtils.isBlank(displayName)) {
            return null;
        }

        List<E> list = BaseEnum.getEnumList(enumClass);
        for (E enumeration : list) {
            if (displayName.equals(enumeration.displayName())) {
                return enumeration;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <E extends BaseEnum> Map<String, E> getEnumMap(Class<E> enumClass) {
        Entry entry = getEntry(enumClass);
        return (Map<String, E>) entry.unmodifiableMap;
    }


    @SuppressWarnings("unchecked")
    public static <E extends BaseEnum> List<E> getEnumList(Class<E> enumClass) {
        Entry entry = getEntry(enumClass);
        return (List<E>) entry.unmodifiableList;
    }

    @SuppressWarnings("unchecked")
    public static <E extends BaseEnum> List<E> getEnumList(String dictionary) {
        Entry entry = getEntry(dictionary);
        if (entry == null) {
            return Collections.EMPTY_LIST;
        }
        return (List<E>) entry.unmodifiableList;
    }

    protected static Iterator<BaseEnum> iterator(Class<BaseEnum> enumClass) {
        return BaseEnum.getEnumList(enumClass).iterator();
    }

    public static <E extends BaseEnum, T extends Serializable> boolean validate(Class<E> enumClass, T val) {
        if (val == null) {
            return false;
        }
        List<E> list = getEnumList(enumClass);
        for (IEnum mEnum : list) {
            if (val.equals(mEnum.value())) {
                return true;
            }
        }
        return false;
    }

    @SafeVarargs
    public static <E extends IEnum, T> boolean isIn(T obj, BiFunction<T, E, Boolean> equalsFunction, E... enums) {
        for (E iEnum : enums) {
            if (equalsFunction.apply(obj, iEnum)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public final boolean equals(Object other) {
        if (other == this) {
            return true;
        } else if (other == null) {
            return false;
        } else if (other.getClass() == this.getClass()) {
            // Ok to do a class cast to Enum here since the test above
            // guarantee both
            // classes are in the same class loader.
            return this.name.equals(((BaseEnum) other).name);
        } else if (other instanceof BaseEnum) {
            // This and other are in different class loaders, we must check indirectly
            //noinspection unchecked
            return this.name.equals(((BaseEnum) other).name)
                    && getEntry(this.getClass()).rootDictionary
                    .equals(getEntry((Class<? extends BaseEnum>) other.getClass()).rootDictionary);
        } else if (other instanceof String) {
            return this.name.equals(other);
        } else {
            return false;
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(E other) {
        if (other == this) {
            return 0;
        }
        if (other.getClass() != this.getClass()) {
            throw new ClassCastException(
                    "Different enum class '" + ClassUtils.getShortClassName(other.getClass()) + "'");
        }
        return this.name.compareTo(((BaseEnum) other).name);
    }


    @Override
    public String toString() {
        if (iToString == null) {
            refreshIToString();
        }
        return iToString;
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode() ^ value.hashCode();
    }

    //-----------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Override
    public final String name() {
        if (name == null) {
            Class enumClass = getEnumClass();
            String dictionary = fetchDictionary(enumClass);
            Entry entry = cEnumClasses.get(dictionary);
            entry.populateNames(enumClass);
        }
        return this.name;
    }

    @Override
    public String displayName() {
        return this.displayName;
    }

    @Override
    public final T value() {
        return this.value;
    }

    @Override
    public String help() {
        return help;
    }

    @Override
    public Map<String, Object> attributes() {
        return attributes;
    }

    @Override
    public int ordinal() {
        return ordinal;
    }

    public static Object getValue(Object object) {
        if (null == object) {
            return null;
        }
        if (object instanceof IEnum) {
            //noinspection rawtypes
            return ((IEnum) object).value();
        } else if (object.getClass().isEnum()) {
            //noinspection rawtypes
            return ((Enum) object).name();
        }
        return object;
    }

    private void refreshIToString() {
//        String shortName = ClassUtils.getShortClassName(getEnumClass());
//        iToString = shortName + "[" + name() + "]";
        iToString = name();
    }

    //-----------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public static <T> void switches(T obj, BiFunction<T, BaseEnum, Boolean> caseFunction, CaseEntry... consumers) {
        switch0(obj, false, caseFunction, consumers);
    }

    @SafeVarargs
    public static <T, R> R switchGet(T obj, BiFunction<T, BaseEnum, Boolean> caseFunction, CaseEntry<BaseEnum, R>... consumers) {
        return BaseEnum.switch0(obj, true, caseFunction, consumers);
    }

    @SuppressWarnings("unchecked")
    public static <T> void switches(T obj, CaseEntry... consumers) {
        switch0(obj, false, null, consumers);
    }

    @SafeVarargs
    public static <T, R> R switchGet(T obj, CaseEntry<T, R>... consumers) {
        return BaseEnum.switch0(obj, true, null, consumers);
    }

    @SafeVarargs
    private static <T, E, R> R switch0(T obj, boolean needReturnResult, BiFunction<T, E, Boolean> caseFunction,
                                       CaseEntry<E, R>... consumers) {
        if (null == consumers) {
            return null;
        }
        boolean caseEqual = null == caseFunction;
        int size = consumers.length;
        int i = 0;
        for (CaseEntry<E, R> consumer : consumers) {
            i++;
            if (null == consumer) {
                if (i == size) {
                    return null;
                } else {
                    continue;
                }
            }
            E[] cases = consumer.getCases();
            if (i == size && null == cases) {
                return switchConsume(needReturnResult, consumer);
            }
            if (needReturnResult) {
                Supplier<R> supplier = consumer.getSwitchSupplier();
                if (null == supplier) {
                    return null;
                }
            } else {
                ConsumerWithoutResult consumerWithoutResult = consumer.getSwitchOperator();
                if (null == consumerWithoutResult) {
                    return null;
                }
            }
            if (caseEqual) {
                Set<?> caseSet = new HashSet<>(Arrays.asList(cases));
                if (caseSet.contains(obj)) {
                    return switchConsume(needReturnResult, consumer);
                }
            } else {
                for (E baseEnum : cases) {
                    if (caseFunction.apply(obj, baseEnum)) {
                        return switchConsume(needReturnResult, consumer);
                    }
                }
            }
        }
        return null;
    }

    private static <T, R> R switchConsume(boolean needReturnResult, CaseEntry<T, R> consumer) {
        if (needReturnResult) {
            Supplier<R> supplier = consumer.getSwitchSupplier();
            if (null == supplier) {
                return null;
            }
            return supplier.get();
        } else {
            ConsumerWithoutResult consumerWithoutResult = consumer.getSwitchOperator();
            if (null == consumerWithoutResult) {
                return null;
            }
            consumerWithoutResult.accept();
            return null;
        }
    }

    public static final BiFunction<IEnum, IEnum, Boolean> CASE_ENUM = (t, baseEnum) -> baseEnum.value().equals(t.value());

    @SuppressWarnings("unchecked")
    public static <T extends IEnum> BiFunction<T, IEnum, Boolean> caseEnum() {
        return (BiFunction<T, IEnum, Boolean>) CASE_ENUM;
    }

    public static final BiFunction<?, BaseEnum, Boolean> CASE_VALUE = (t, baseEnum) -> baseEnum.value().equals(t);

    @SuppressWarnings("unchecked")
    public static <T> BiFunction<T, BaseEnum, Boolean> caseValue() {
        return (BiFunction<T, BaseEnum, Boolean>) CASE_VALUE;
    }

    public static final BiFunction<?, BaseEnum, Boolean> CASE_NAME = (t, baseEnum) -> baseEnum.name().equals(t);

    @SuppressWarnings("unchecked")
    public static <T> BiFunction<T, BaseEnum, Boolean> caseName() {
        return (BiFunction<T, BaseEnum, Boolean>) CASE_NAME;
    }

    @SafeVarargs
    public static <T, R> CaseEntry<T, R> cases(T... cases) {
        CaseEntry<T, R> entry = new CaseEntry<>();
        entry.setCases(cases);
        return entry;
    }

    public static CaseEntry defaults() {
        return new CaseEntry();
    }

    public static CaseEntry defaults(ConsumerWithoutResult switchConsumer) {
        CaseEntry entry = new CaseEntry();
        entry.to(switchConsumer);
        return entry;
    }

    public static <T, R> CaseEntry<T, R> defaults(Supplier<R> switchSupplier) {
        CaseEntry<T, R> entry = new CaseEntry<>();
        entry.to(switchSupplier);
        return entry;
    }

    public interface ConsumerWithoutResult {
        void accept();
    }

    public static class CaseEntry<T, R> {
        private T[] cases;
        private Supplier<R> switchSupplier;
        private ConsumerWithoutResult switchOperator;

        private static final Supplier nullSwitchSupplier = () -> null;

        protected T[] getCases() {
            return cases;
        }

        protected void setCases(T[] cases) {
            this.cases = cases;
        }

        protected Supplier<R> getSwitchSupplier() {
            return switchSupplier;
        }

        protected ConsumerWithoutResult getSwitchOperator() {
            return switchOperator;
        }

        public CaseEntry to(Supplier<R> switchSupplier) {
            this.switchSupplier = switchSupplier;
            return this;
        }

        public CaseEntry to(ConsumerWithoutResult switchOperator) {
            this.switchOperator = switchOperator;
            return this;
        }

        @SuppressWarnings("unchecked")
        public CaseEntry toNull() {
            this.switchSupplier = nullSwitchSupplier;
            return this;
        }
    }

    //-----------------------------------------------------------------------

    public static <E extends BaseEnum> boolean isHasName(Class<E> enumClass, String name) {
        if (StringUtils.isNotBlank(name)) {
            IEnum mEnum = getEnum(enumClass, name);
            return null != mEnum;
        }
        return false;
    }

    public static <E extends BaseEnum, T extends Serializable> String getNameByValue(Class<E> enumClass, T val) {
        if (null != val) {
            IEnum mEnum = getEnumByValue(enumClass, val);
            if (null != mEnum) {
                return mEnum.name();
            }
        }
        return null;
    }

    public static <E extends BaseEnum, T extends Serializable> String getDisplayNameByValue(Class<E> enumClass, T val, String defaultDisplayName) {
        if (null != val) {
            IEnum mEnum = getEnumByValue(enumClass, val);
            if (null != mEnum) {
                return mEnum.displayName();
            }
        }
        return defaultDisplayName;
    }

    public static <E extends BaseEnum, T extends Serializable> String getDisplayNameByValue(Class<E> enumClass, T val) {
        return getDisplayNameByValue(enumClass, val, "未定义");
    }

    public static <E extends BaseEnum> String getDisplayNameByName(Class<E> enumClass, String name, String defaultDisplayName) {
        if (StringUtils.isNotBlank(name)) {
            IEnum mEnum = getEnum(enumClass, name);
            if (null != mEnum) {
                return mEnum.displayName();
            }
        }
        return defaultDisplayName;
    }

    public static <E extends BaseEnum> String getDisplayNameByName(Class<E> enumClass, String name) {
        return getDisplayNameByName(enumClass, name, "未定义");
    }

    @SuppressWarnings("unchecked")
    public static <E extends BaseEnum, T extends Serializable> T getValueByName(Class<E> enumClass, String name) {
        if (StringUtils.isNotBlank(name)) {
            IEnum mEnum = getEnum(enumClass, name);
            if (null != mEnum) {
                return (T) mEnum.value();
            }
        }
        return null;
    }

    /**
     * 获取按位与的所有displayName
     *
     * @param enumClass 枚举类
     * @param bits      属性位
     * @return 显示名称
     */
    public static <E extends IEnum> List<String> getDisplayNamesByBits(Class<E> enumClass, Long bits) {
        return dealEnumsByBits(enumClass, bits, IEnum::displayName);
    }

    public static <E extends IEnum> List<E> getEnumsByBits(Class<E> enumClass, Long bits) {
        return dealEnumsByBits(enumClass, bits, e -> e);
    }

    public static <E extends IEnum> List<String> getNamesByBits(Class<E> enumClass, Long bits) {
        return dealEnumsByBits(enumClass, bits, IEnum::name);
    }

    public static <E extends IEnum> List<Long> getValuesByBits(Class<E> enumClass, Long bits) {
        return dealEnumsByBits(enumClass, bits, e -> BitUtil.longValue(e.value()));
    }

    public static <E extends IEnum, R> List<R> dealEnumsByBits(Class<E> enumClass, Long bits, Function<E, R> function) {
        if (null == bits) {
            return null;
        }
        List<R> itemList = new ArrayList<>();
        List<E> list = Enums.getEnumList(enumClass);
        for (E tmp : list) {
            if (BitUtil.has(bits, ((Number) tmp.value()).longValue())) {
                itemList.add(function.apply(tmp));
            }
        }
        return itemList;
    }

    @SuppressWarnings("unused")
    public static List<String> getDisplayNamesByBits(String dictionary, Long bits) {
        return dealEnumsByBits(dictionary, bits, IEnum::displayName);
    }

    @SuppressWarnings("unchecked")
    public static <E extends BaseEnum> List<E> getEnumsByBits(String dictionary, Long bits) {
        return dealEnumsByBits(dictionary, bits, e -> (E) e);
    }

    public static List<String> getNamesByBits(String dictionary, Long bits) {
        return dealEnumsByBits(dictionary, bits, IEnum::name);
    }

    public static List<Long> getValuesByBits(String dictionary, Long bits) {
        return dealEnumsByBits(dictionary, bits, e -> BitUtil.longValue(e.value()));
    }

    public static <E extends BaseEnum, R> List<R> dealEnumsByBits(String dictionary, Long bits, Function<E, R> function) {
        if (null == bits) {
            return null;
        }
        List<R> itemList = new ArrayList<>();
        List<E> list = BaseEnum.getEnumList(dictionary);
        for (E tmp : list) {
            if (BitUtil.has(bits, ((Number) tmp.value()).longValue())) {
                itemList.add(function.apply(tmp));
            }
        }
        return itemList;
    }

    public String toEnumString() {
        return "name:" + name + ", displayName:" + displayName + ", value:" + value + ", help:" + help;
    }

}
