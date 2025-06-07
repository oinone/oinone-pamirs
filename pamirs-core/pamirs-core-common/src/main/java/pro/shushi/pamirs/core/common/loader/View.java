package pro.shushi.pamirs.core.common.loader;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * 作为一段视图描述的顶级（根）节点存在，控制整个容器的外观和行为；
 */
@XStreamAlias("view")
public class View {

    @XStreamAsAttribute
    private String widget;

    @XStreamAsAttribute
    private String model;

    @XStreamAsAttribute
    private String path;

    @XStreamAsAttribute
    private String viewName;

    @XStreamAsAttribute
    private String filter;

    @XStreamAsAttribute
    private String menu;

    //如果存在多个searchView, 则search必须为true，且把对应search.xml放在对应菜单目录下
    @XStreamAsAttribute
    private String search;

    @XStreamAsAttribute
    private String pagePath;

    @XStreamAsAttribute
    private String title;

    @XStreamAsAttribute
    private Integer priority;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPagePath() {
        return pagePath;
    }

    public void setPagePath(String pagePath) {
        this.pagePath = pagePath;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
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

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getWidget() {
        return widget;
    }

    public void setWidget(String widget) {
        this.widget = widget;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
