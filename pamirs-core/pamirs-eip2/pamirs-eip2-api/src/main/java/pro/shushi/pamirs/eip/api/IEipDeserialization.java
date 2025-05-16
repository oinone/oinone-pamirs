package pro.shushi.pamirs.eip.api;

public interface IEipDeserialization<T> {

    /**
     * 反序列化
     *
     * @param outObject 出参对象
     * @return 反序列化结果
     */
    Object deserialization(T outObject);
}
