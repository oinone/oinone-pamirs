package pro.shushi.pamirs.channel.core.manager;

import pro.shushi.pamirs.channel.model.ChannelModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ChannelModelCache
 *
 * @author yakir on 2024/03/21 15:02.
 */
public class ChannelModelCache {

    private static final Map<String, ChannelModel> channelModelCache = new ConcurrentHashMap<>();

    public static ChannelModel getByOrigin(String origin) {

        if (channelModelCache.containsKey(origin)) {
            return channelModelCache.get(origin);
        }

        ChannelModel query = new ChannelModel();
        query.setOrigin(origin);
        ChannelModel fromDb = query.queryOne();
        if (null != fromDb && fromDb.getId() > 0) {
            channelModelCache.put(origin, fromDb);
            return fromDb;
        }
        return null;
    }
}
