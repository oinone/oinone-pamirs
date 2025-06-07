package pro.shushi.pamirs.meta.base.manager.data;

import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;

/**
 * ID数据管理器
 * <p>
 * 2020/5/7 11:58 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class IdDataManager extends DataManager {

    // ID模型数据管理器
    private volatile static IdDataManager DATA_MANAGER;

    public static IdDataManager getInstance() {
        if (null == DATA_MANAGER) {
            synchronized (Models.class) {
                if (null == DATA_MANAGER) {
                    DATA_MANAGER = new IdDataManager();
                }
            }
        }
        return DATA_MANAGER;
    }

    public <T> T queryById(T data) {
        return Models.directive().run(() -> Fun.run(Models.api().getModel(data), queryById, data));
    }

    public <T> Integer updateById(T data) {
        return Models.directive().run(() -> Fun.run(Models.api().getModel(data), updateById, data));
    }

    public <T> Boolean deleteById(T data) {
        return Models.directive().run(() -> Fun.run(Models.api().getModel(data), deleteById, data));
    }

    public <T> T generateId(T data, String keyGenerator) {
        return Models.directive().run(() -> Fun.run(Models.api().getModel(data), generateId, data, keyGenerator));
    }

}
