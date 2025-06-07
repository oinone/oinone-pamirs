package pro.shushi.pamirs.file.api.model;

import pro.shushi.pamirs.file.api.util.ExcelCellRangeHelper;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * <h>Excel单元格范围定义</h>
 * <p>
 * 1、格式转换: (beginRowIndex,beginColumnIndex):(endRowIndex,endColumnIndex)。例如"A1:B3"等价于"(0,0):(2,1)"。<br>
 * 2、固定索引: 使用"$"对索引进行固定，不再跟随范围扩展而扩展。例如"$A$1:B3"，固定左上角坐标，只能向右和向下扩展。此时fixedBeginRowIndex和fixedBeginColumnIndex为true。<br>
 * </p>
 *
 * @author Adamancy Zhang at 10:42 on 2021-08-16
 */
@Base
@Model.model(ExcelCellRangeDefinition.MODEL_MODEL)
@Model(displayName = "Excel单元格范围定义")
public class ExcelCellRangeDefinition extends TransientModel {

    private static final long serialVersionUID = -7349848772611594596L;

    public static final String MODEL_MODEL = "file.ExcelCellRangeDefinition";

    @Field(displayName = "起始行索引")
    private Integer beginRowIndex;

    @Field(displayName = "结束行索引")
    private Integer endRowIndex;

    @Field(displayName = "起始列索引")
    private Integer beginColumnIndex;

    @Field(displayName = "结束列索引")
    private Integer endColumnIndex;

    @Field(displayName = "固定起始行索引")
    private Boolean fixedBeginRowIndex;

    @Field(displayName = "固定结束行索引")
    private Boolean fixedEndRowIndex;

    @Field(displayName = "固定起始列索引")
    private Boolean fixedBeginColumnIndex;

    @Field(displayName = "固定结束列索引")
    private Boolean fixedEndColumnIndex;

    public ExcelCellRangeDefinition() {
        setFixedBeginRowIndex(false);
        setFixedEndRowIndex(false);
        setFixedBeginColumnIndex(false);
        setFixedEndColumnIndex(false);
    }

    public ExcelCellRangeDefinition(int beginRowIndex, int endRowIndex, int beginColumnIndex, int endColumnIndex) {
        this();
        setBeginRowIndex(beginRowIndex);
        setEndRowIndex(endRowIndex);
        setBeginColumnIndex(beginColumnIndex);
        setEndColumnIndex(endColumnIndex);
    }

    public ExcelCellRangeDefinition(String coordinate) {
        ExcelCellRangeDefinition rangeDefinition = ExcelCellRangeHelper.analysis(coordinate);
        if (rangeDefinition == null) {
            throw new IllegalArgumentException("Invalid coordinate value. coordinate=" + coordinate);
        }
        this.setBeginRowIndex(rangeDefinition.getBeginRowIndex());
        this.setEndRowIndex(rangeDefinition.getEndRowIndex());
        this.setBeginColumnIndex(rangeDefinition.getBeginColumnIndex());
        this.setEndColumnIndex(rangeDefinition.getEndColumnIndex());
        this.setFixedBeginRowIndex(rangeDefinition.getFixedBeginRowIndex());
        this.setFixedEndRowIndex(rangeDefinition.getFixedEndRowIndex());
        this.setFixedBeginColumnIndex(rangeDefinition.getFixedBeginColumnIndex());
        this.setFixedEndColumnIndex(rangeDefinition.getFixedEndColumnIndex());
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public ExcelCellRangeDefinition clone() {
        return new ExcelCellRangeDefinition()
                .setBeginRowIndex(getBeginRowIndex())
                .setEndRowIndex(getEndRowIndex())
                .setBeginColumnIndex(getBeginColumnIndex())
                .setEndColumnIndex(getEndColumnIndex())
                .setFixedBeginRowIndex(getFixedBeginRowIndex())
                .setFixedEndRowIndex(getFixedEndRowIndex())
                .setFixedBeginColumnIndex(getFixedBeginColumnIndex())
                .setFixedEndColumnIndex(getFixedEndColumnIndex());
    }
}
