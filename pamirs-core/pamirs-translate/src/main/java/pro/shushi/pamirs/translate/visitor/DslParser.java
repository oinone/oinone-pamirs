package pro.shushi.pamirs.translate.visitor;

import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.base.ux.model.UIView;
import pro.shushi.pamirs.boot.web.manager.UiIoManager;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

/**
 * DSL解析
 *
 * @author Adamancy Zhang at 13:44 on 2024-01-16
 */
public class DslParser {

    private DslParser() {
        //reject create object
    }

    public static UIView parser(View view) {
        UIView uiView = BeanDefinitionUtils.getBean(UiIoManager.class).parseTemplate(view.getTemplate());
        uiView.setModel(view.getModel());
        uiView.setName(view.getName());
        return uiView;
    }

    public static UIView visit(View view, DslNodeVisitor visitor) {
        return visit(parser(view), visitor);
    }

    public static UIView visit(UIView view, DslNodeVisitor visitor) {
        new DslNodeVisitorProxy(visitor).visit(view);
        return view;
    }
}
