package pro.shushi.pamirs.meta.api.core.orm.systems.orm;

import pro.shushi.pamirs.meta.common.spi.HoldKeeper;

/**
 * ORM 处理器容器
 * 2021/9/24 12:00 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class OrmProcessorHolder {

    private final static HoldKeeper<DefaultOrmMappingProcessor> DEFAULT_ORM_MAPPING_PROCESSOR = new HoldKeeper<>();

    private final static HoldKeeper<DefaultOrmModelingProcessor> DEFAULT_ORM_MODELING_PROCESSOR = new HoldKeeper<>();

    private final static HoldKeeper<DefaultOrmObjectingProcessor> DEFAULT_ORM_OBJECTING_PROCESSOR = new HoldKeeper<>();

    public static DefaultOrmMappingProcessor mappingProcessor() {
        return DEFAULT_ORM_MAPPING_PROCESSOR.supply(DefaultOrmMappingProcessor::new);
    }

    public static DefaultOrmModelingProcessor modelingProcessor() {
        return DEFAULT_ORM_MODELING_PROCESSOR.supply(DefaultOrmModelingProcessor::new);
    }

    public static DefaultOrmObjectingProcessor objectingProcessor() {
        return DEFAULT_ORM_OBJECTING_PROCESSOR.supply(DefaultOrmObjectingProcessor::new);
    }

}
