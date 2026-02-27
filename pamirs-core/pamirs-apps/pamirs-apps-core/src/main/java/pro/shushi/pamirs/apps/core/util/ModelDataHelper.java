package pro.shushi.pamirs.apps.core.util;

import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.domain.ModelData;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import java.util.Date;

/**
 * @author Adamancy Zhang at 20:51 on 2026-02-26
 */
public class ModelDataHelper {

    private ModelDataHelper() {
        // reject create object
    }

    public static ModelData initModelData(String model, String module, MetaBaseModel meta) {
        ModelData modelData = new ModelData();
        modelData.setLowCode(Boolean.TRUE);
        modelData.setSource(SystemSourceEnum.UI);
        modelData.setModule(module);
        modelData.setLoadModule(module);
        modelData.setDateInit(new Date());
        modelData.setDateUpdate(modelData.getDateInit());

        modelData.setModel(model);
        modelData.setResId(meta.getId());
        modelData.code(modelData.getModel(), meta.getSign());
        modelData.construct();
        return modelData.create();
    }
}
