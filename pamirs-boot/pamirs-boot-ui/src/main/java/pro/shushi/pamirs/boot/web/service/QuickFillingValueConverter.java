package pro.shushi.pamirs.boot.web.service;

import pro.shushi.pamirs.boot.base.tmodel.QuickFillingFailureDetail;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingField;

/**
 * @author Gesi at 19:21 on 2025/9/10
 */
public interface QuickFillingValueConverter {

    Object transformObjectValue(QuickFillingField quickFillingField, String value, QuickFillingFailureDetail failureDetail);

}
