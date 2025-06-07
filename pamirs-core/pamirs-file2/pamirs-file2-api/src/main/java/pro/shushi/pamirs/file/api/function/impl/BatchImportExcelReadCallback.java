package pro.shushi.pamirs.file.api.function.impl;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.file.api.easyexcel.ExcelAnalysisEventListener;
import pro.shushi.pamirs.file.api.extpoint.ExcelImportDataExtPoint;
import pro.shushi.pamirs.file.api.function.ExcelReadCallback;
import pro.shushi.pamirs.meta.api.Ext;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * <h>默认Excel读取回调</h>
 * <p>线程不安全</p>
 *
 * @author Adamancy Zhang at 15:53 on 2021-08-17
 */
public class BatchImportExcelReadCallback implements ExcelReadCallback {

    private Collection<Object> totalData = new ArrayList<>();

    @Override
    public void process(ExcelImportContext importContext, String modelModel, Map<String, Object> data) {
        ExcelAnalysisEventListener listener = importContext.getCurrentListener();
        if (listener.hasNext()) {
            totalData.add(convertMapToModel(data, modelModel));
            return;
        } else {
            totalData.add(convertMapToModel(data, modelModel));
        }
        call(importContext, listener);
    }


    public Object convertMapToModel(Map<String, Object> data, String modelModel) {
        Models.api().setModel(data, modelModel);
        Models.api().setDataModel(modelModel, data);
        Class<?> cls = null;
        String lname = Optional.ofNullable(PamirsSession.getContext()).map(v -> v.getModelConfig(modelModel)).map(ModelConfig::getLname).orElse(null);
        if (StringUtils.isNotBlank(lname)) {
            try {
                cls = Class.forName(lname);
            } catch (ClassNotFoundException ignored) {
            }
        }
        Object modelData;
        if (cls == null) {
            modelData = data;
        } else {
            modelData = JsonUtils.parseObject(JsonUtils.toJSONString(data), cls);
        }
        return modelData;
    }

    protected void call(ExcelImportContext importContext, ExcelAnalysisEventListener listener) {
        Boolean isSuccess = Ext.run(ExcelImportDataExtPoint<Object>::importData, importContext, totalData);
        if (isSuccess == null || !isSuccess) {
            listener.interrupt();
        }
    }
}
