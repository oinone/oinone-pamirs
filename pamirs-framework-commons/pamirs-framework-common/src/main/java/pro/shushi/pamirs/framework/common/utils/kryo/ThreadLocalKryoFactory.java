package pro.shushi.pamirs.framework.common.utils.kryo;

import com.esotericsoftware.kryo.Kryo;

public class ThreadLocalKryoFactory extends AbstractKryoFactory {

    private final ThreadLocal<Kryo> holder = ThreadLocal.withInitial(this::create);

    @Override
    public void returnKryo(Kryo kryo) {
        // do nothing
    }

    @Override
    public Kryo getKryo() {
        return holder.get();
    }
}