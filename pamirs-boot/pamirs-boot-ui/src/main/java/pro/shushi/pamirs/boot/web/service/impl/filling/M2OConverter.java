package pro.shushi.pamirs.boot.web.service.impl.filling;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingFailureDetail;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingField;
import pro.shushi.pamirs.boot.web.service.QuickFillingValueConverter;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

/**
 * @author Gesi at 9:35 on 2025/9/11
 */
@Service
public class M2OConverter extends AbstractValueConverter implements QuickFillingValueConverter {

    @Override
    public boolean canTransform(TtypeEnum ttype) {
        return TtypeEnum.M2O.equals(ttype);
    }

    @Override
    public Object transformObjectValue(QuickFillingField quickFillingField, String value, QuickFillingFailureDetail failureDetail) {
        return null;
    }
}
