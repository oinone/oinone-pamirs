package pro.shushi.pamirs.boot.standard.entity;

import jakarta.annotation.Nonnull;
import java.io.Serializable;
import java.util.*;

/**
 * 环境Key集合
 *
 * @author Adamancy Zhang at 15:27 on 2024-10-14
 */
public class EnvironmentKeySet extends AbstractCollection<EnvironmentKey> implements Serializable {

    private static final EnvironmentKeySet emptySet = new EnvironmentKeySet(0);

    private static final long serialVersionUID = -1489765470090765495L;

    private final int initialCapacity;

    private final Map<String, EnvironmentKey> keyMap;

    public EnvironmentKeySet() {
        this(8);
    }

    public EnvironmentKeySet(int initialCapacity) {
        this.initialCapacity = initialCapacity;
        this.keyMap = new LinkedHashMap<>(initialCapacity);
    }

    @Override
    public boolean add(EnvironmentKey environmentKey) {
        if (initialCapacity == 0) {
            throw new UnsupportedOperationException();
        }
        return keyMap.put(environmentKey.getKey(), environmentKey) == null;
    }

    @Nonnull
    @Override
    public Iterator<EnvironmentKey> iterator() {
        return keyMap.values().iterator();
    }

    @Override
    public int size() {
        return keyMap.size();
    }

    public Set<String> getKeys() {
        return keyMap.keySet();
    }

    public EnvironmentKey get(String key) {
        return keyMap.get(key);
    }

    public static EnvironmentKeySet emptySet() {
        return emptySet;
    }
}