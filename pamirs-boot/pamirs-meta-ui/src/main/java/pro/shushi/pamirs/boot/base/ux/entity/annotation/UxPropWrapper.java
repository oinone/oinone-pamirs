package pro.shushi.pamirs.boot.base.ux.entity.annotation;

import com.alibaba.fastjson.annotation.JSONField;
import pro.shushi.pamirs.meta.annotation.Prop;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.io.Serializable;
import java.lang.annotation.Annotation;

/**
 * @author Adamancy Zhang at 21:15 on 2025-06-16
 */
@Slf4j
@Data
public class UxPropWrapper implements Prop, Serializable {

    private static final long serialVersionUID = -230013803300950407L;

    private String name;

    private String value;

    private String clazz;

    @JSONField(serialize = false)
    private transient Class<?> type;

    @Override
    public String name() {
        return name;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public Class<?> type() {
        if (type == null) {
            try {
                type = Class.forName(clazz);
            } catch (ClassNotFoundException e) {
                log.error("Invalid clazz value.", e);
                type = String.class;
            }
        }
        return type;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Prop.class;
    }

    public static UxPropWrapper wrap(Prop prop) {
        UxPropWrapper uxProp = new UxPropWrapper();
        uxProp.name = prop.name();
        uxProp.value = prop.value();
        uxProp.clazz = prop.type().getName();
        uxProp.type = prop.type();
        return uxProp;
    }
}
