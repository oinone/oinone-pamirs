package pro.shushi.pamirs.meta.base.manager.data;

import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.constant.FunctionConstants;

/**
 * 编码数据管理器
 * <p>
 * 2020/5/7 11:58 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class CodeDataManager extends IdDataManager implements FunctionConstants {

    // Code模型数据管理器
    private volatile static CodeDataManager DATA_MANAGER;

    public static CodeDataManager getInstance() {
        if (null == DATA_MANAGER) {
            synchronized (Models.class) {
                if (null == DATA_MANAGER) {
                    DATA_MANAGER = new CodeDataManager();
                }
            }
        }
        return DATA_MANAGER;
    }

    public <T> T queryByCode(T data) {
        return Models.directive().run(() -> Fun.run(Models.api().getModel(data), queryByCode, data));
    }

    public <T> Integer updateByCode(T data) {
        return Models.directive().run(() -> Fun.run(Models.api().getModel(data), updateByCode, data));
    }

    public <T> Boolean deleteByCode(T data) {
        return Models.directive().run(() -> Fun.run(Models.api().getModel(data), deleteByCode, data));
    }

    public <T> T generateCode(T data, String sequence, String configCode) {
        return Models.directive().run(() -> Fun.run(Models.api().getModel(data), generateCode, data, sequence, configCode));
    }

}
