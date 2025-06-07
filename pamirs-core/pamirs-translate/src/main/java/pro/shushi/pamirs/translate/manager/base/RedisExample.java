package pro.shushi.pamirs.translate.manager.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RedisExample {

    @Autowired
    private RedisTemplate redisTemplate;

    public void saveData(String key, Map<String, Map<String, Map<String, String>>> data, Long timeout) {
        redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] keyBytes = redisTemplate.getKeySerializer().serialize(key);

                // 开启管道
                connection.openPipeline();

                // 执行命令
                for (Map.Entry<String, Map<String, Map<String, String>>> entry : data.entrySet()) {
                    byte[] fieldBytes = entry.getKey().getBytes();
                    for (Map.Entry<String, Map<String, String>> innerEntry : entry.getValue().entrySet()) {
                        byte[] innerFieldBytes = innerEntry.getKey().getBytes();
                        for (Map.Entry<String, String> innerMostEntry : innerEntry.getValue().entrySet()) {
                            byte[] innerMostFieldBytes = innerMostEntry.getKey().getBytes();
                            byte[] valueBytes = innerMostEntry.getValue().getBytes();

                            connection.hSet(keyBytes, fieldBytes, valueBytes);
                            connection.expire(keyBytes, timeout);
                        }
                    }
                }

                // 执行管道
                connection.closePipeline();

                return null;
            }
        });
    }


    public Map<String, Map<String, Map<String, String>>> getData(String key) {
        Map<Object, Object> redisData = redisTemplate.opsForHash().entries(key);
        return convertToJavaMap(redisData);
    }

    // 将 Redis 中的 Map 转换为 Java 中的嵌套 Map
    private Map<String, Map<String, Map<String, String>>> convertToJavaMap(Map<Object, Object> redisMap) {
        Map<String, Map<String, Map<String, String>>> javaMap = new HashMap<>();
        for (Map.Entry<Object, Object> entry : redisMap.entrySet()) {
            javaMap.put((String) entry.getKey(), convertToJavaSubMap((Map<Object, Object>) entry.getValue()));
        }
        return javaMap;
    }

    private Map<String, Map<String, String>> convertToJavaSubMap(Map<Object, Object> redisSubMap) {
        Map<String, Map<String, String>> javaSubMap = new HashMap<>();
        for (Map.Entry<Object, Object> entry : redisSubMap.entrySet()) {
            javaSubMap.put((String) entry.getKey(), (Map<String, String>) entry.getValue());
        }
        return javaSubMap;
    }

    // 将 Java 中的嵌套 Map 转换为 Redis 中的 Map
    private Map<String, Map<String, String>> convertToRedisMap(Map<String, Map<String, Map<String, String>>> javaMap) {
        Map<String, Map<String, String>> redisMap = new HashMap<>();
        for (Map.Entry<String, Map<String, Map<String, String>>> entry : javaMap.entrySet()) {
            redisMap.put(entry.getKey(), convertToRedisSubMap(entry.getValue()));
        }
        return redisMap;
    }

    private Map<String, String> convertToRedisSubMap(Map<String, Map<String, String>> javaSubMap) {
        Map<String, String> redisSubMap = new HashMap<>();
        for (Map.Entry<String, Map<String, String>> entry : javaSubMap.entrySet()) {
            redisSubMap.putAll(entry.getValue());
        }
        return redisSubMap;
    }

    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }
}
