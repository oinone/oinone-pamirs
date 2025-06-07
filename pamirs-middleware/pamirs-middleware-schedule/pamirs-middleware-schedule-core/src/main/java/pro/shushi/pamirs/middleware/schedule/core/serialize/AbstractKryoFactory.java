package pro.shushi.pamirs.middleware.schedule.core.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.serializers.ClosureSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import de.javakaffee.kryoserializers.*;
import org.apache.dubbo.common.serialize.kryo.CompatibleKryo;
import org.apache.dubbo.common.serialize.support.SerializableClassRegistry;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationHandler;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public abstract class AbstractKryoFactory {

    private final Set<Class> registrations = new LinkedHashSet<Class>();

    private boolean registrationRequired;

    private volatile boolean kryoCreated;

    public AbstractKryoFactory() {

    }

    /**
     * only supposed to be called at startup time
     * <p>
     * later may consider adding support for custom serializer, custom id, etc
     */
    public void registerClass(Class clazz) {

        if (kryoCreated) {
            throw new IllegalStateException("Can't register class after creating kryo instance");
        }
        registrations.add(clazz);
    }

    public Kryo create() {
        if (!kryoCreated) {
            kryoCreated = true;
        }

        Kryo kryo = new CompatibleKryo();

        // TODO
//        kryo.setReferences(false);
        kryo.setRegistrationRequired(registrationRequired);

        kryo.register(Arrays.asList("").getClass(), new ArraysAsListSerializer());
        kryo.register(GregorianCalendar.class, new GregorianCalendarSerializer());
        kryo.register(InvocationHandler.class, new JdkProxySerializer());
        kryo.register(BigDecimal.class, new DefaultSerializers.BigDecimalSerializer());
        kryo.register(BigInteger.class, new DefaultSerializers.BigIntegerSerializer());
        kryo.register(Pattern.class, new RegexSerializer());
        kryo.register(BitSet.class, new BitSetSerializer());
        kryo.register(URI.class, new URISerializer());
        kryo.register(UUID.class, new UUIDSerializer());
        UnmodifiableCollectionsSerializer.registerSerializers(kryo);
        SynchronizedCollectionsSerializer.registerSerializers(kryo);

        // now just added some very common classes
        // TODO optimization
        kryo.register(HashMap.class);
        kryo.register(ArrayList.class);
        kryo.register(LinkedList.class);
        kryo.register(HashSet.class);
        kryo.register(TreeSet.class);
        kryo.register(Hashtable.class);
        kryo.register(Date.class);
        kryo.register(Calendar.class);
        kryo.register(ConcurrentHashMap.class);
        kryo.register(SimpleDateFormat.class);
        kryo.register(GregorianCalendar.class);
        kryo.register(Vector.class);
        kryo.register(BitSet.class);
        kryo.register(StringBuffer.class);
        kryo.register(StringBuilder.class);
        kryo.register(Object.class);
        kryo.register(Object[].class);
        kryo.register(String[].class);
        kryo.register(byte[].class);
        kryo.register(char[].class);
        kryo.register(int[].class);
        kryo.register(float[].class);
        kryo.register(double[].class);

        // Closures add by @huidao
        kryo.register(Object[].class);
        kryo.register(Class.class);
        kryo.register(SerializedLambda.class);
        kryo.register(ClosureSerializer.Closure.class, new ClosureSerializer());
//        kryo.register(CapturingClass.class);

        // 注册扩展
        for (KryoRegisterApi kryoRegisterApi : Spider.getLoader(KryoRegisterApi.class).getOrderedExtensions()) {
            kryoRegisterApi.register(kryo);
        }

        kryo.setReferences(true);
        //kryo.setCopyReferences(true);
        kryo.setDefaultSerializer(CompatibleFieldSerializer.class);

        for (Class clazz : registrations) {
            kryo.register(clazz);
        }

        SerializableClassRegistry.getRegisteredClasses().forEach((clazz, ser) -> {
            if (ser == null) {
                kryo.register(clazz);
            } else {
                kryo.register(clazz, (Serializer) ser);
            }
        });

        return kryo;
    }

    public void setRegistrationRequired(boolean registrationRequired) {
        this.registrationRequired = registrationRequired;
    }

    public abstract void returnKryo(Kryo kryo);

    public abstract Kryo getKryo();

}
