package pro.shushi.pamirs.boot.web.spi.kernel;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.boot.base.model.*;
import pro.shushi.pamirs.meta.api.dto.meta.api.MetaDataApi;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.util.SetUtils;

import java.util.Map;
import java.util.Set;

/**
 * 元数据编辑扩展逻辑基础实现
 * <p>
 * 2022/3/2 2:26 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order(99)
@SPI.Service
public class BaseMetaDataApi implements MetaDataApi {

    private final static Set<String> actionModelSet = SetUtils
            .asSet(ServerAction.MODEL_MODEL, ViewAction.MODEL_MODEL, UrlAction.MODEL_MODEL, ClientAction.MODEL_MODEL);

    @Override
    public <T extends MetaBaseModel> void whenAddDataItem(Map<String/*meta model group*/, Map<String/*model sign*/, MetaBaseModel>> data,
                                                          String group, String sign, T item) {
//        if (actionModelSet.contains(group)) {
//            MapUtils.computeIfAbsent(data, Action.MODEL_MODEL, k -> new HashMap<>());
//            Map<String/*model sign*/, MetaBaseModel> meta = data.get(Action.MODEL_MODEL);
//            meta.put(sign, ActionUtils.toAction((Action) meta.get(item.getSign()), (Action) item));
//        }
    }

    @Override
    public boolean whenRemoveDataItem(Map<String/*meta model group*/, Map<String/*model sign*/, MetaBaseModel>> data,
                                      String group, String sign) {
        return true;
//        if (!actionModelSet.contains(group)) {
//            return true;
//        }
//        Map<String/*model sign*/, MetaBaseModel> meta = data.get(Action.MODEL_MODEL);
//        if (null == meta) {
//            return false;
//        }
//        return null != meta.remove(sign);
    }

}
