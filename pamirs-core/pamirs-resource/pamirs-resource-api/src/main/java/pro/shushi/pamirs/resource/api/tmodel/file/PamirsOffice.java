package pro.shushi.pamirs.resource.api.tmodel.file;

import pro.shushi.pamirs.boot.base.resource.PamirsFile;
import pro.shushi.pamirs.meta.annotation.Model;

@Model.model(PamirsOffice.MODEL_MODEL)
@Model(displayName = "office文件", labelFields = "name", summary = "office/wps/pdf等办公文件")
@Model.Code(sequence = "UUID")
public class PamirsOffice extends PamirsFile {

    private static final long serialVersionUID = 3793273770519682798L;

    public static final String MODEL_MODEL = "base.PamirsOffice";

    public PamirsOffice(String url) {
        setUrl(url);
    }
}
