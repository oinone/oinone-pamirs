package pro.shushi.pamirs.grouping.entity;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

import java.util.List;

/**
 * 表格分组模型
 *
 * @author Adamancy Zhang at 12:00 on 2025-11-20
 */
public class TableGroupingModel {

    private final String model;

    private final String columnFormat;

    private final List<String> pks;

    private final List<String> pkColumns;

    private final List<String> pkAsFields;

    public TableGroupingModel(String model) {
        this.model = model;

        PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(model);
        this.columnFormat = pamirsTableInfo.getColumnFormat();

        ModelConfig modelConfig = PamirsSession.getContext().getSimpleModelConfig(model);
        List<String> finalPks = modelConfig.getPk();
        List<String> finalPkColumns = null;
        List<String> finalPkAsFields = null;
        if (CollectionUtils.isNotEmpty(finalPks)) {
            FieldColumnsWrapper pkFieldsWrapper = FieldColumnsWrapper.resolveColumns(model, finalPks, columnFormat);
            if (pkFieldsWrapper != null) {
                finalPkColumns = pkFieldsWrapper.getColumns();
                finalPkAsFields = pkFieldsWrapper.getAsFields();
            }
        }
        this.pks = finalPks;
        this.pkColumns = finalPkColumns;
        this.pkAsFields = finalPkAsFields;
    }

    public String getModel() {
        return model;
    }

    public String getColumnFormat() {
        return columnFormat;
    }

    public List<String> getPks() {
        return pks;
    }

    public List<String> getPkColumns() {
        return pkColumns;
    }

    public List<String> getPkAsFields() {
        return pkAsFields;
    }
}
