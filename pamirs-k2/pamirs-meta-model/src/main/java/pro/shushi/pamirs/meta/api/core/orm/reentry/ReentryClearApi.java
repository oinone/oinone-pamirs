package pro.shushi.pamirs.meta.api.core.orm.reentry;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.template.DataComputeTemplate;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.HashMap;
import java.util.Map;

import static pro.shushi.pamirs.meta.common.enmu.BaseEnum.caseValue;
import static pro.shushi.pamirs.meta.common.enmu.BaseEnum.cases;
import static pro.shushi.pamirs.meta.enmu.TtypeEnum.*;

/**
 * @author haibo(xf.z @ shushi.pro)
 * @date 2024/1/27 19:44
 */
@Component
public class ReentryClearApi {

    public <T> T _clear(String model, Object obj) {
        if (obj == null) return null;
        int objId = System.identityHashCode(obj);
        if (getReentryMap(objId) != null) {// 判断是否重入
            return (T) getReentryMap(objId);
        }
        return DataComputeTemplate.getInstance().compute(model, obj,
                this::_clear,
                (oModel, oObj) -> {
                    Models.modelDirective().disableOrmReentry(oObj);// 防重入
                    getReentryMap().put(objId, oObj);
                    return oObj;// 模型化
                },
                (oModel, oObj) -> {
                    return oObj;
                },
                (context, fieldConfig, dMap) -> {
                    // 处理引用字段类型
                    String ttype = fieldConfig.getTtype();
                    if (TtypeEnum.isRelatedType(fieldConfig.getTtype())) {
                        ttype = fieldConfig.getRelatedTtype();
                    }
                    TtypeEnum.switches(ttype, caseValue(),
                            cases(O2M, M2O, M2M, O2O).to(() -> {
                                String lname = fieldConfig.getLname();
                                Object value = dMap.get(lname);
                                _clear(fieldConfig.getReferences(), value);
                            })// 关系类型处理
                    );
                }
        );
    }

    public <T> T clear(String model, Object obj) {
        clear();
        try {
            return _clear(model, obj);
        } finally {
            clear();
        }
    }

    private final static ThreadLocal<Map<Integer, Object>> reentryMap = new ThreadLocal<>();

    private void clear() {
        if (reentryMap.get() != null) {
            reentryMap.get().clear();
        }
    }

    private Object getReentryMap(Integer i) {
        init();
        return reentryMap.get().get(i);
    }

    private Map<Integer, Object> getReentryMap() {
        init();
        return reentryMap.get();
    }

    private void init() {
        if (null == reentryMap.get()) {
            reentryMap.set(new HashMap<Integer, Object>());
        }
    }

}
