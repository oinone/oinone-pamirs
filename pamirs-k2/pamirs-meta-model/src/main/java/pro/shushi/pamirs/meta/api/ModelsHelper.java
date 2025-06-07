package pro.shushi.pamirs.meta.api;

import pro.shushi.pamirs.meta.base.manager.construct.ConstructManager;
import pro.shushi.pamirs.meta.base.manager.data.DataManager;
import pro.shushi.pamirs.meta.base.manager.data.OriginDataManager;

/**
 * Model帮助类
 * <p>
 * 2021/9/16 11:08 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ModelsHelper {

    // 源数据管理器
    private volatile static OriginDataManager ORIGIN_DATA_MANAGER;

    // 数据管理器
    private volatile static DataManager DATA_MANAGER;

    // 构造管理器
    private volatile static ConstructManager CONSTRUCTOR;

    public static DataManager data() {
        if (null == DATA_MANAGER) {
            synchronized (Models.class) {
                if (null == DATA_MANAGER) {
                    ModelsHelper.DATA_MANAGER = new DataManager();
                }
            }
        }
        return ModelsHelper.DATA_MANAGER;
    }

    public static OriginDataManager origin() {
        if (null == ORIGIN_DATA_MANAGER) {
            synchronized (Models.class) {
                if (null == ORIGIN_DATA_MANAGER) {
                    ModelsHelper.ORIGIN_DATA_MANAGER = CommonApiFactory.getApi(OriginDataManager.class);
                }
            }
        }
        return ModelsHelper.ORIGIN_DATA_MANAGER;
    }

    public static ConstructManager constructor() {
        if (null == CONSTRUCTOR) {
            synchronized (Models.class) {
                if (null == CONSTRUCTOR) {
                    ModelsHelper.CONSTRUCTOR = CommonApiFactory.getApi(ConstructManager.class);
                }
            }
        }
        return ModelsHelper.CONSTRUCTOR;
    }

}
