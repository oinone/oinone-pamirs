package pro.shushi.pamirs.framework.common.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import pro.shushi.pamirs.framework.common.utils.kryo.KryoUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.io.ByteArrayOutputStream;

/**
 * KryoSerializer
 *
 * @author yakir on 2023/01/04 10:40.
 */
@Slf4j
public class KryoSerializer {

    public static <T> byte[] serialize(T t) throws RuntimeException {
        if (t == null) {
            return new byte[0];
        }
        Kryo kryo = KryoUtils.get();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Output output = new Output(baos, 65535)) {
            kryo.writeClassAndObject(output, t);
            output.flush();
            return baos.toByteArray();
        } catch (Exception ex) {
            log.error("serialize", ex);
            return null;
        } finally {
            kryo = null;
        }
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T deserialize(byte[] bytes) throws RuntimeException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        Kryo kryo = KryoUtils.get();
        try (Input input = new Input(bytes)) {
            Object obj = kryo.readClassAndObject(input);
            return (T) obj;
        } catch (Exception ex) {
            log.error("deserialize", ex);
            return null;
        } finally {
            kryo = null;
        }
    }

    public static <T> T copy(T object) {
        return KryoSerializer.deserialize(KryoSerializer.serialize(object));
    }

}
