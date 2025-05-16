package pro.shushi.pamirs.translate.visitor;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.boot.base.ux.model.UIView;
import pro.shushi.pamirs.boot.base.ux.model.UIWidget;
import pro.shushi.pamirs.boot.base.ux.model.part.UIOption;
import pro.shushi.pamirs.boot.base.ux.model.view.UIAction;
import pro.shushi.pamirs.boot.base.ux.model.view.UIField;
import pro.shushi.pamirs.boot.base.ux.model.view.UIPack;
import pro.shushi.pamirs.boot.base.ux.model.view.UITemplate;

import java.util.List;

/**
 * DslNodeVisitor代理
 *
 * @author Adamancy Zhang at 13:51 on 2024-01-16
 */
public class DslNodeVisitorProxy implements DslNodeVisitor {

    private final DslNodeVisitor visitor;

    public DslNodeVisitorProxy(DslNodeVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public boolean visit(UIView node) {
        if (visitor.visit(node)) {
            this.visitWidgets(node.getWidgets());
            visitor.endVisit(node);
        }
        return true;
    }

    @Override
    public boolean visit(UITemplate node) {
        if (visitor.visit(node)) {
            this.visitWidgets(node.getWidgets());
            visitor.endVisit(node);
        }
        return true;
    }

    @Override
    public boolean visit(UIField node) {
        if (visitor.visit(node)) {
            this.visitWidgets(node.getWidgets());
            visitor.endVisit(node);
        }
        return true;
    }

    @Override
    public boolean visit(UIAction node) {
        if (visitor.visit(node)) {
            this.visitWidgets(node.getWidgets());
            visitor.endVisit(node);
        }
        return true;
    }

    @Override
    public boolean visit(UIPack node) {
        if (visitor.visit(node)) {
            this.visitWidgets(node.getWidgets());
            visitor.endVisit(node);
        }
        return true;
    }


    @Override
    public boolean visit(UIWidget node) {
        if (node instanceof UIView) {
            this.visit((UIView) node);
        } else if (node instanceof UITemplate) {
            this.visit((UITemplate) node);
        } else if (node instanceof UIAction) {
            this.visit((UIAction) node);
        } else if (node instanceof UIField) {
            this.visit((UIField) node);
        } else if (node instanceof UIPack) {
            this.visit((UIPack) node);
        } else {
            if (visitor.visit(node)) {
                this.visitWidgets(node.getWidgets());
                visitor.endVisit(node);
            }
        }
        return true;
    }

    private void visitWidgets(List<UIWidget> widgets) {
        if (CollectionUtils.isEmpty(widgets)) {
            return;
        }
        for (UIWidget widget : widgets) {
            visit(widget);
        }
    }
}
