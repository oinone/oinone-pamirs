package pro.shushi.pamirs.meta.api.core.orm.template;

import pro.shushi.pamirs.meta.common.spi.HoldKeeper;

/**
 * 数据模板容器
 * <p>
 * 2021/9/23 10:28 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * @deprecated please using {@link DataComputeTemplate#getInstance()}
 */
@Deprecated
public class DataTemplateHolder {

    private final static HoldKeeper<DataComputeTemplate> dataTemplateHolder = new HoldKeeper<>();

    public static DataComputeTemplate getDataComputeTemplate() {
        return dataTemplateHolder.supply(DataComputeTemplate::getInstance);
    }

}
