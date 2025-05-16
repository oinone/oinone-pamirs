package pro.shushi.pamirs.framework.connectors.data.ddl.check;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.FieldColumn;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.FieldWrapper;
import pro.shushi.pamirs.framework.connectors.data.ddl.model.DdlContext;
import pro.shushi.pamirs.framework.connectors.data.ddl.utils.CheckUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

/**
 * 字段校验
 * <p>
 * 2020/6/23 4:32 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Component
public class ColumnChecker {

    public boolean change(DdlContext ddlContext, FieldWrapper modelField) {
        FieldColumn fieldColumn = ddlContext.fetchColumn(modelField.getField());
        if (!owner(ddlContext.useLogicTable().getModel(), fieldColumn)) {
            if (!store(modelField)) {
                return false;
            }
            // 字段可被修改
            if (FieldColumn.isEmpty(fieldColumn)) {
                return true;
            } else if (modelField.getModel().equals(fieldColumn.getModel())) {
                return true;
            } else if (Models.inherited().isPropagationExtendInherited(fieldColumn.getModel(), modelField.getModel())) {
                return true;
            } else {
                return !Models.inherited().isSameExtendSuperModel(modelField.getModel(), fieldColumn.getModel());
            }
        } else {
            if (!store(modelField)) {
                ddlContext.dropColumn(modelField.getField());
                return false;
            }
        }
        return true;
    }

    public boolean drop(String module, String modelFieldModel, FieldColumn fieldColumn) {
        // 字段可被废弃
        if (FieldColumn.isEmpty(fieldColumn)) {
            return false;
        } else if (modelFieldModel.equals(fieldColumn.getModel())) {
            return true;
        } else if (Models.inherited().isSameExtendSuperModel(modelFieldModel, fieldColumn.getModel())) {
            return false;
        } else if (!CheckUtils.isValidMeta(module, fieldColumn.getModule())) {
            return false;
        } else {
            // 修正数据
            fieldColumn.setModule(module);
            fieldColumn.setModel(modelFieldModel);
            fieldColumn.setChanged(true);
            return true;
        }
    }

    public boolean extend(FieldWrapper modelField) {
        // 继承字段
        return SystemSourceEnum.EXTEND_INHERITED.value().equals(modelField.getSource());
    }

    public boolean store(FieldWrapper modelField) {
        // 存储字段
        return modelField.getStore();
    }

    public boolean owner(String model, FieldColumn fieldColumn) {
        // 字段所属模型未变更
        return !FieldColumn.isEmpty(fieldColumn) && null != fieldColumn.getModel() && fieldColumn.getModel().equals(model);
    }

    public boolean override(String model, FieldColumn fieldColumn) {
        // model被fieldColumn所属model重载（覆盖）
        return !FieldColumn.isEmpty(fieldColumn) && null != fieldColumn.getModel() &&
                Models.inherited().isSuperModel(fieldColumn.getModel(), model);
    }

}
