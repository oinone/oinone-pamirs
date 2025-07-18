package pro.shushi.pamirs.framework.connectors.data.autoconfigure.pamirs.extend;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.reflection.wrapper.BaseWrapper;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pamirs Model MapWrapper is override {@link org.apache.ibatis.reflection.wrapper.MapWrapper}
 *
 * @author Adamancy Zhang at 12:25 on 2025-07-14
 */
public class PamirsModelMapWrapper extends BaseWrapper {

    private final ModelConfig modelConfig;

    private final Map<String, Object> map;

    private final boolean usingAsProperty;

    private final Map<String, String> columnToLnameMapping;

    private final Map<String, Class<?>> lnameToLtypeMapping;

    private MetaObject metaObject;

    public PamirsModelMapWrapper(ModelConfig modelConfig, Map<String, Object> map) {
        super(null);
        this.modelConfig = modelConfig;
        this.map = map;
        this.usingAsProperty = this.modelConfig.getModel().equals(PamirsSession.getAsProperty());
        if (this.usingAsProperty) {
            this.columnToLnameMapping = OrmColumnToLnameCache.getMapping(modelConfig);
            this.lnameToLtypeMapping = OrmLnameToLtypeCache.getMapping(modelConfig);
        } else {
            this.columnToLnameMapping = null;
            this.lnameToLtypeMapping = null;
        }
    }

    public void apply(MetaObject metaObject) {
        this.metaObject = metaObject;
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
        if (prop.getIndex() != null) {
            Object collection = resolveCollection(prop, map);
            return getCollectionValue(prop, collection);
        } else {
            return map.get(prop.getName());
        }
    }

    @Override
    public void set(PropertyTokenizer prop, Object value) {
        if (prop.getIndex() != null) {
            Object collection = resolveCollection(prop, map);
            setCollectionValue(prop, collection, value);
        } else {
            map.put(prop.getName(), value);
        }
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        if (this.usingAsProperty) {
            String lname = columnToLnameMapping.get(name);
            if (lname == null) {
                return name;
            }
            return lname;
        }
        return name;
    }

    @Override
    public String[] getGetterNames() {
        return map.keySet().toArray(new String[map.keySet().size()]);
    }

    @Override
    public String[] getSetterNames() {
        return map.keySet().toArray(new String[map.keySet().size()]);
    }

    @Override
    public Class<?> getSetterType(String name) {
        if (this.usingAsProperty) {
            Class<?> setterType = lnameToLtypeMapping.get(name);
            if (setterType != null) {
                if (PamirsSession.isStaticConfig()) {
                    return setterType;
                }
                if (TypeUtils.isIEnumClass(setterType)) {
                    if (map.get(name) != null) {
                        return map.get(name).getClass();
                    }
                    return Object.class;
                }
                return setterType;
            }
        }
        return getSetterType0(name);
    }

    protected Class<?> getSetterType0(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                return Object.class;
            } else {
                return metaValue.getSetterType(prop.getChildren());
            }
        } else {
            if (map.get(name) != null) {
                return map.get(name).getClass();
            } else {
                return Object.class;
            }
        }
    }

    @Override
    public Class<?> getGetterType(String name) {
        if (this.usingAsProperty) {
            Class<?> getterType = lnameToLtypeMapping.get(name);
            if (getterType != null) {
                if (PamirsSession.isStaticConfig()) {
                    return getterType;
                }
                if (TypeUtils.isIEnumClass(getterType)) {
                    if (map.get(name) != null) {
                        return map.get(name).getClass();
                    }
                    return Object.class;
                }
                return getterType;
            }
        }
        return getGetterType0(name);
    }

    protected Class<?> getGetterType0(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                return Object.class;
            } else {
                return metaValue.getGetterType(prop.getChildren());
            }
        } else {
            if (map.get(name) != null) {
                return map.get(name).getClass();
            } else {
                return Object.class;
            }
        }
    }

    @Override
    public boolean hasSetter(String name) {
        return true;
    }

    @Override
    public boolean hasGetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            if (map.containsKey(prop.getIndexedName())) {
                MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
                if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                    return true;
                } else {
                    return metaValue.hasGetter(prop.getChildren());
                }
            } else {
                return false;
            }
        } else {
            return map.containsKey(prop.getName());
        }
    }

    @Override
    public MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory) {
        HashMap<String, Object> map = new HashMap<>();
        set(prop, map);
        return MetaObject.forObject(map, metaObject.getObjectFactory(), metaObject.getObjectWrapperFactory(), metaObject.getReflectorFactory());
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
    public <E> void addAll(List<E> element) {
        throw new UnsupportedOperationException();
    }
}
