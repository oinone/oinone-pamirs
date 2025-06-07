package pro.shushi.pamirs.eip.api.camel;

/**
 * 注册组件对象
 *
 * @author Adamancy Zhang at 20:06 on 2021-07-27
 */
public class RegistryComponentBody {

    private final String id;

    private final Class<?> clazz;

    private final Object bean;

    public RegistryComponentBody(String id, Object bean) {
        this(id, null, bean);
    }

    public RegistryComponentBody(String id, Class<?> clazz, Object bean) {
        this.id = id;
        this.clazz = clazz;
        this.bean = bean;
    }

    public String getId() {
        return id;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Object getBean() {
        return bean;
    }
}
