package pro.shushi.pamirs.framework.gateways.convert.impl;

import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

/**
 * 抽象Rsql值转换器
 *
 * @author Adamancy Zhang at 15:17 on 2021-09-03
 */
public abstract class AbstractRsqlValueConverter {

    protected String fetchRealTtype(ModelFieldConfig modelField) {
        String ttype = modelField.getTtype();
        if (TtypeEnum.isRelatedType(ttype)) {
            ttype = modelField.getTtype();
        }
        return ttype;
    }
}
