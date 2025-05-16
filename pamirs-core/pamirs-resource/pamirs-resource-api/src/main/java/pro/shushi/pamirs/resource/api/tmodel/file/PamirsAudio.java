package pro.shushi.pamirs.resource.api.tmodel.file;

import pro.shushi.pamirs.boot.base.resource.PamirsFile;
import pro.shushi.pamirs.meta.annotation.Model;

@Model.model(PamirsAudio.MODEL_MODEL)
@Model(displayName = "音频", labelFields = "name")
@Model.Code(sequence = "UUID")
public class PamirsAudio extends PamirsFile {

    private static final long serialVersionUID = 2793370370519761798L;

    public static final String MODEL_MODEL = "base.PamirsAudio";

    public PamirsAudio(String url) {
        setUrl(url);
    }
}
