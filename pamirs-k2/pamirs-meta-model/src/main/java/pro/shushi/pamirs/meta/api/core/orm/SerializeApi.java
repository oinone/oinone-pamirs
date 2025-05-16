package pro.shushi.pamirs.meta.api.core.orm;

/**
 * 对象关系映射API
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
public interface SerializeApi {

    <T> T serialize(String model, T origin);

    <T> T deserialize(String model, T origin);

}
