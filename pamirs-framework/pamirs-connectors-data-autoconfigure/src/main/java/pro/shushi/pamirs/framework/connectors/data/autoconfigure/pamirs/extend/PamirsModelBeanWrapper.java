package pro.shushi.pamirs.framework.connectors.data.autoconfigure.pamirs.extend;

import org.apache.ibatis.reflection.*;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.reflection.wrapper.BaseWrapper;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;

import java.util.List;
import java.util.Map;

/**
 * Pamirs Model BeanWrapper is override {@link org.apache.ibatis.reflection.wrapper.BeanWrapper}
 *
 * @author Adamancy Zhang at 11:26 on 2025-07-14
 */
public class PamirsModelBeanWrapper extends BaseWrapper {

    private final Map<String, String> columnToLnameMapping;

    private MetaObject metaObject;

    private Object object;

    private MetaClass metaClass;

    public PamirsModelBeanWrapper(ModelConfig modelConfig) {
        super(null);
        this.columnToLnameMapping = OrmColumnToLnameCache.getMapping(modelConfig);
    }

    public void apply(MetaObject metaObject, Object object) {
        this.metaObject = metaObject;
        this.object = object;
        this.metaClass = MetaClass.forClass(object.getClass(), metaObject.getReflectorFactory());
    }

    @Override
    protected Object resolveCollection(PropertyTokenizer prop, Object object) {
        if ("".equals(prop.getName())) {
            return object;
        }
        return metaObject.getValue(prop.getName());
    }


    @Override
    public Object get(PropertyTokenizer prop) {
        if (prop.hasNext()) {
            return getChildValue(prop);
        } else if (prop.getIndex() != null) {
            return getCollectionValue(prop, resolveCollection(prop, object));
        } else {
            return getBeanProperty(prop, object);
        }
    }

    @Override
    public void set(PropertyTokenizer prop, Object value) {
        if (prop.hasNext()) {
            setChildValue(prop, value);
        } else if (prop.getIndex() != null) {
            setCollectionValue(prop, resolveCollection(prop, object), value);
        } else {
            setBeanProperty(prop, object, value);
        }
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        String lname = columnToLnameMapping.get(name);
        if (lname == null) {
            return metaClass.findProperty(name, useCamelCaseMapping);
        }
        return lname;
    }

    @Override
    public String[] getGetterNames() {
        return metaClass.getGetterNames();
    }

    @Override
    public String[] getSetterNames() {
        return metaClass.getSetterNames();
    }

    @Override
    public Class<?> getSetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                return metaClass.getSetterType(name);
            } else {
                return metaValue.getSetterType(prop.getChildren());
            }
        } else {
            return metaClass.getSetterType(name);
        }
    }

    @Override
    public Class<?> getGetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                return metaClass.getGetterType(name);
            } else {
                return metaValue.getGetterType(prop.getChildren());
            }
        } else {
            return metaClass.getGetterType(name);
        }
    }

    @Override
    public boolean hasSetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            if (metaClass.hasSetter(prop.getIndexedName())) {
                MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
                if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                    return metaClass.hasSetter(name);
                } else {
                    return metaValue.hasSetter(prop.getChildren());
                }
            } else {
                return false;
            }
        } else {
            return metaClass.hasSetter(name);
        }
    }

    @Override
    public boolean hasGetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            if (metaClass.hasGetter(prop.getIndexedName())) {
                MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
                if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                    return metaClass.hasGetter(name);
                } else {
                    return metaValue.hasGetter(prop.getChildren());
                }
            } else {
                return false;
            }
        } else {
            return metaClass.hasGetter(name);
        }
    }

    @Override
    public MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory) {
        MetaObject metaValue;
        Class<?> type = getSetterType(prop.getName());
        try {
            Object newObject = objectFactory.create(type);
            metaValue = MetaObject.forObject(newObject, metaObject.getObjectFactory(), metaObject.getObjectWrapperFactory(), metaObject.getReflectorFactory());
            set(prop, newObject);
        } catch (Exception e) {
            throw new ReflectionException("Cannot set value of property '" + name + "' because '" + name + "' is null and cannot be instantiated on instance of " + type.getName() + ". Cause:" + e.toString(), e);
        }
        return metaValue;
    }

    private Object getBeanProperty(PropertyTokenizer prop, Object object) {
        try {
            Invoker method = metaClass.getGetInvoker(prop.getName());
            try {
                return method.invoke(object, NO_ARGUMENTS);
            } catch (Throwable t) {
                throw ExceptionUtil.unwrapThrowable(t);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable t) {
            throw new ReflectionException("Could not get property '" + prop.getName() + "' from " + object.getClass() + ".  Cause: " + t.toString(), t);
        }
    }

    private void setBeanProperty(PropertyTokenizer prop, Object object, Object value) {
        try {
            Invoker method = metaClass.getSetInvoker(prop.getName());
            Object[] params = {value};
            try {
                method.invoke(object, params);
            } catch (Throwable t) {
                throw ExceptionUtil.unwrapThrowable(t);
            }
        } catch (Throwable t) {
            throw new ReflectionException("Could not set property '" + prop.getName() + "' of '" + object.getClass() + "' with value '" + value + "' Cause: " + t.toString(), t);
        }
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public void add(Object element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E> void addAll(List<E> list) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object getChildValue(PropertyTokenizer prop) {
        MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
        if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
            return null;
        }
        return metaValue.getValue(prop.getChildren());
    }

    @Override
    protected void setChildValue(PropertyTokenizer prop, Object value) {
        MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
        if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
            if (value == null) {
                // don't instantiate child path if value is null
                return;
            }
            metaValue = instantiatePropertyValue(null, new PropertyTokenizer(prop.getName()), metaObject.getObjectFactory());
        }
        metaValue.setValue(prop.getChildren(), value);
    }
}
