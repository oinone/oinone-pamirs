package pro.shushi.pamirs.resource.api.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import pro.shushi.pamirs.meta.annotation.fun.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Glyphs {

    private String icon_id;
    private String name;
    private String font_class;
    private String unicode;
}
