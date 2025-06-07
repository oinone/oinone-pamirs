package pro.shushi.pamirs.resource.api.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonRootBean {

    private String id;
    private String name;
    private String css_prefix_text;
    private String description;
    private List<Glyphs> glyphs;


}
