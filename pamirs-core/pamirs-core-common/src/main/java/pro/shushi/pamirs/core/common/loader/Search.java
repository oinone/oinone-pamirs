package pro.shushi.pamirs.core.common.loader;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * 作为一段视图描述的顶级（根）节点存在，控制整个容器的外观和行为；
 */
@XStreamAlias("search")
public class Search {

    @XStreamAsAttribute
    private String model;

    @XStreamAsAttribute
    private String path;

    @XStreamAsAttribute
    private String viewName;

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getModel() {
        return model;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setModel(String model) {
        this.model = model;
    }

}
