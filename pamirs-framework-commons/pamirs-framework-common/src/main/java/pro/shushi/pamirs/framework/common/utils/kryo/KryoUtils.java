package pro.shushi.pamirs.framework.common.utils.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class KryoUtils {
    private static final AbstractKryoFactory kryoFactory = new ThreadLocalKryoFactory();

    public static Kryo get() {
        return kryoFactory.getKryo();
    }

    public static void release(Kryo kryo) {
        kryoFactory.returnKryo(kryo);
    }

    public static void register(Class<?> clazz) {
        kryoFactory.registerClass(clazz);
    }

    //获取当前线程中Kryo对象时,假如线程中没有此对象
    //此时会调用initialValue创建对象并通过set方法绑定当前线程
    //static Kryo kryo = new Kryo();//线程共享
    private static final ThreadLocal<Kryo> KRYOS = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        // kryo.setReferences(true);//支持循环引用
        // Configure the Kryo instance.
        return kryo;
    });

    //序列化
    public static <T extends Serializable> byte[] serialize(T t) {
        //1.构建kryo对象
        //Kryo k = new Kryo();
        //k.setRegistrationRequired(false);
        //2.构建字节数组输出流(内置可扩容数组)
        ByteArrayOutputStream bos =
                new ByteArrayOutputStream();
        //3.构建处理流output对象
        Output output = new Output(bos);
        //4.将对象序列化
        get().writeObject(output, t);
        output.flush();
        byte[] array = bos.toByteArray();
        output.close();
        return array;
    }

    //反序列化
    public static <T> T deserialize(byte[] array, Class<T> cls) {
        //1.构建kryo对象
        //Kryo k=new Kryo();
        //k.setRegistrationRequired(false);
        //2.构建input对象(负责读字节数据)
        Input input = new Input(array);
        //3.反序列化数据
        T t = get().readObject(input, cls);
        input.close();
        return t;
    }

}
