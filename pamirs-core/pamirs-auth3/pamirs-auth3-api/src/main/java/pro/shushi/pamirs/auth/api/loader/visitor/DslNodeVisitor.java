package pro.shushi.pamirs.auth.api.loader.visitor;

import pro.shushi.pamirs.boot.base.ux.model.UIView;
import pro.shushi.pamirs.boot.base.ux.model.UIWidget;
import pro.shushi.pamirs.boot.base.ux.model.view.UIAction;
import pro.shushi.pamirs.boot.base.ux.model.view.UIField;
import pro.shushi.pamirs.boot.base.ux.model.view.UITemplate;

/**
 * DslNodeVisitor
 *
 * @author Adamancy Zhang at 13:39 on 2024-01-16
 */
public interface DslNodeVisitor {

    default boolean visit(UIView node) {
        return true;
    }

    default void endVisit(UIView node) {
    }

    default boolean visit(UITemplate node) {
        return true;
    }

    default void endVisit(UITemplate node) {
    }

    default boolean visit(UIField node) {
        return true;
    }

    default void endVisit(UIField node) {
    }

    default boolean visit(UIAction node) {
        return true;
    }

    default void endVisit(UIAction node) {
    }

    default boolean visit(UIWidget node) {
        return true;
    }

    default void endVisit(UIWidget node) {
    }
}
