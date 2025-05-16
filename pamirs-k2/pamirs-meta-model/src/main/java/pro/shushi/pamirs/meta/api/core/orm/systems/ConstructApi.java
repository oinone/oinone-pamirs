package pro.shushi.pamirs.meta.api.core.orm.systems;

/**
 * 模型数据构造API
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
public interface ConstructApi {

    <T> T construct(T data);

}
