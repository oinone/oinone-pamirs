package pro.shushi.pamirs.boot.web.service;

import pro.shushi.pamirs.boot.base.tmodel.QuickFillingFailureDetail;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingField;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

/**
 *
 * @author Gesi at 19:21 on 2025/9/10
 */
public interface QuickFillingValueTransformer {

    boolean canTransform(TtypeEnum ttype);

    Object transformObjectValue(QuickFillingField quickFillingField, String value, QuickFillingFailureDetail failureDetail);

}
