package pro.shushi.pamirs.file.api.function.impl;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.MapHelper;
import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.file.api.easyexcel.ExcelAnalysisEventListener;
import pro.shushi.pamirs.file.api.extpoint.ExcelImportDataExtPoint;
import pro.shushi.pamirs.file.api.function.ExcelReadCallback;
import pro.shushi.pamirs.meta.api.Ext;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * <h>默认Excel读取回调</h>
 * <p>线程不安全</p>
 *
 * @author Adamancy Zhang at 15:53 on 2021-08-17
 */
public class DefaultExcelReadCallback implements ExcelReadCallback {

    private Map<String, Object> onceData;

    @Override
    public void process(ExcelImportContext importContext, String modelModel, Map<String, Object> data) {
        ExcelAnalysisEventListener listener = importContext.getCurrentListener();
        if (listener.getCurrentSheet().getOnceFetchData()) {
            if (listener.hasNext()) {
                if (onceData == null) {
                    onceData = data;
                } else {
                    mergeData(onceData, data);
                }
                return;
            } else {
                if (data == null) {
                    data = onceData;
                } else {
                    if (onceData != null) {
                        mergeData(onceData, data);
                        data = onceData;
                    }
                }
            }
        }
        process(importContext, listener, modelModel, data);
    }

    protected void mergeData(Map<String, Object> origin, Map<String, Object> data) {
        MapHelper.deepMerge(origin, data);
    }

    protected void process(ExcelImportContext importContext, ExcelAnalysisEventListener listener, String modelModel, Map<String, Object> data) {
        Class<?> cls = null;
        ModelConfig modelConfig = Optional.ofNullable(PamirsSession.getContext()).map(v -> v.getModelConfig(modelModel)).orElse(null);
        if (modelConfig == null) {
            cls = classForName(modelModel);
        } else {
            Models.api().setModel(data, modelModel);
            Models.api().setDataModel(modelModel, data);
            String lname = modelConfig.getLname();
            if (StringUtils.isNotBlank(lname) && !HashMap.class.getName().equals(lname)) {
                cls = classForName(lname);
            }
        }
        Object importData;
        if (cls == null) {
            importData = data;
        } else {
            importData = JsonUtils.parseObject(JsonUtils.toJSONString(data), cls);
        }
        call(importContext, listener, importData);
    }

    protected void call(ExcelImportContext importContext, ExcelAnalysisEventListener listener, Object importData) {
        Boolean isSuccess = Ext.run(ExcelImportDataExtPoint<Object>::importData, importContext, importData);
        if (isSuccess == null || !isSuccess) {
            listener.interrupt();
        }
    }

    private Class<?> classForName(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException ignored) {
        }
        return null;
    }
}
