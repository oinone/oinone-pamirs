package pro.shushi.pamirs.resource.api.tmodel.file;

import pro.shushi.pamirs.boot.base.resource.PamirsFile;
import pro.shushi.pamirs.meta.annotation.Model;

@Model.model(PamirsExcel.MODEL_MODEL)
@Model(displayName = "excel文件", labelFields = "name")
@Model.Code(sequence = "UUID")
public class PamirsExcel extends PamirsFile {

    private static final long serialVersionUID = 6793373970519782798L;

    public static final String MODEL_MODEL = "base.PamirsExcel";

    public PamirsExcel(String url) {
        setUrl(url);
    }
}
