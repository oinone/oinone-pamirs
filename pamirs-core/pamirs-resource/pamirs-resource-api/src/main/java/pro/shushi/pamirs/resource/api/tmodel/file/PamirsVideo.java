package pro.shushi.pamirs.resource.api.tmodel.file;

import pro.shushi.pamirs.boot.base.resource.PamirsFile;
import pro.shushi.pamirs.meta.annotation.Model;

@Model.model(PamirsVideo.MODEL_MODEL)
@Model(displayName = "视频", labelFields = "name")
@Model.Code(sequence = "UUID")
public class PamirsVideo extends PamirsFile {

    private static final long serialVersionUID = 2793373374519722798L;

    public static final String MODEL_MODEL = "base.PamirsVideo";

    public PamirsVideo(String url) {
        setUrl(url);
    }
}
