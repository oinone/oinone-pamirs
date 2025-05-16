package pro.shushi.pamirs.file.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Base
@Model.model(ExcelRowDefinition.MODEL_MODEL)
@Model(displayName = "Excel行")
public class ExcelRowDefinition extends TransientModel {

    private static final long serialVersionUID = 3854911345092649973L;

    public static final String MODEL_MODEL = "file.ExcelRowDefinition";

    @Field(displayName = "单元格列表")
    private List<ExcelCellDefinition> cellList;

    @Field(displayName = "样式", summary = "在一行中默认使用全局样式，若设置单元格样式，则覆盖全局样式；在表头行中，水平表头行设置整列样式；垂直表头行设置整行样式；若表头行和数据行同时设置样式，则已数据行的样式为全局样式；")
    private ExcelStyleDefinition style;

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public ExcelRowDefinition clone() {
        List<ExcelCellDefinition> originCellList = getCellList();
        List<ExcelCellDefinition> copyCellList = null;
        if (originCellList != null) {
            copyCellList = new ArrayList<>(originCellList.size());
            for (ExcelCellDefinition originCell : originCellList) {
                copyCellList.add(originCell.clone());
            }
        }
        return new ExcelRowDefinition()
                .setCellList(copyCellList)
                .setStyle(Optional.ofNullable(getStyle()).map(ExcelStyleDefinition::clone).orElse(null));
    }
}
