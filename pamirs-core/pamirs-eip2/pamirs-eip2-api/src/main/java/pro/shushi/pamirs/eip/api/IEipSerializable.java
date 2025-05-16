package pro.shushi.pamirs.eip.api;

public interface IEipSerializable<T> {

    /**
     * 序列化
     *
     * @param inObject 入参对象
     * @return 序列化结果
     */
    T serializable(Object inObject);
}
