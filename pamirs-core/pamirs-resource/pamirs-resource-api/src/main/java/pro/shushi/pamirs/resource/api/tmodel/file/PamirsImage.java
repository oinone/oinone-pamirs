package pro.shushi.pamirs.resource.api.tmodel.file;

import pro.shushi.pamirs.boot.base.resource.PamirsFile;
import pro.shushi.pamirs.meta.annotation.Model;

@Model.model(PamirsImage.MODEL_MODEL)
@Model(displayName = "图片", labelFields = "name")
@Model.Code(sequence = "UUID")
public class PamirsImage extends PamirsFile {

    private static final long serialVersionUID = 2793373770519768798L;

    public static final String MODEL_MODEL = "base.PamirsImage";

    public PamirsImage(String url) {
        setUrl(url);
    }
}
