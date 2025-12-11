package pro.shushi.pamirs.meta.api;

import pro.shushi.pamirs.meta.api.core.compute.systems.enmu.EnumProcessor;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.TypeProcessor;
import pro.shushi.pamirs.meta.api.core.orm.BatchApi;
import pro.shushi.pamirs.meta.api.core.orm.OrmApi;
import pro.shushi.pamirs.meta.api.core.orm.systems.*;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.base.GenericModel;
import pro.shushi.pamirs.meta.base.manager.construct.ConstructManager;
import pro.shushi.pamirs.meta.base.manager.data.DataManager;
import pro.shushi.pamirs.meta.base.manager.data.OriginDataManager;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.Map;

import static pro.shushi.pamirs.meta.enmu.MetaExpEnumerate.BASE_UN_SUPPORT_DATA_TYPE_ERROR;

/**
 * 模型系统
 * <p>
 * 2020/6/30 3:54 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class Models {

    // 模型编码管理
    private final static HoldKeeper<ModelModelApi> MODEL_MODEL_API = new HoldKeeper<>();

    // 模型计算
    private final static HoldKeeper<ModelComputeApi> MODEL_COMPUTE_API = new HoldKeeper<>();

    // 模型校验
    private final static HoldKeeper<ModelCheckApi> MODEL_CHECK_API = new HoldKeeper<>();

    // 元位系统
    private final static HoldKeeper<DirectiveApi> DIRECTIVE_API = new HoldKeeper<>();

    // 模型元位系统
    private final static HoldKeeper<ModelDirectiveApi> MODEL_DIRECTIVE_API = new HoldKeeper<>();

    // ORM计算
    private final static HoldKeeper<OrmApi> ORM_API = new HoldKeeper<>();

    // 单层ORM计算
    private final static HoldKeeper<OrmApi> MONO_ORM_API = new HoldKeeper<>();

    // Pops
    private final static HoldKeeper<PopsApi> POPS_API = new HoldKeeper<>();

    // 继承系统
    private final static HoldKeeper<ModelInheritedApi> MODEL_INHERITED_API = new HoldKeeper<>();

    // 类型系统
    private final static HoldKeeper<TypeProcessor> TYPE_PROCESSOR = new HoldKeeper<>();

    @SuppressWarnings("rawtypes")
    // 枚举系统
    private final static HoldKeeper<EnumProcessor> ENUM_PROCESSOR = new HoldKeeper<>();

    // 批量操作命令
    private final static HoldKeeper<BatchApi> BATCH_API = new HoldKeeper<>();

    public static ModelModelApi api() {
        return MODEL_MODEL_API.supply(() -> Spider.getLoader(ModelModelApi.class).getDefaultExtension());
    }

    public static ModelComputeApi compute() {
        return MODEL_COMPUTE_API.supply(() -> Spider.getLoader(ModelComputeApi.class).getDefaultExtension());
    }

    public static ModelCheckApi check() {
        return MODEL_CHECK_API.supply(() -> Spider.getLoader(ModelCheckApi.class).getDefaultExtension());
    }

    public static DirectiveApi directive() {
        return DIRECTIVE_API.supply(() -> Spider.getLoader(DirectiveApi.class).getDefaultExtension());
    }

    public static ModelDirectiveApi modelDirective() {
        return MODEL_DIRECTIVE_API.supply(() -> Spider.getLoader(ModelDirectiveApi.class).getDefaultExtension());
    }

    public static OrmApi orm() {
        return ORM_API.supply(() -> Spider.getLoader(OrmApi.class).getDefaultExtension());
    }

    public static OrmApi mono() {
        return MONO_ORM_API.supply(() -> Spider.getLoader(OrmApi.class).getExtension(NamespaceConstants.spiMono));
    }

    public static PopsApi pops() {
        return POPS_API.supply(() -> Spider.getLoader(PopsApi.class).getDefaultExtension());
    }

    public static ModelInheritedApi inherited() {
        return MODEL_INHERITED_API.supply(() -> Spider.getLoader(ModelInheritedApi.class).getDefaultExtension());
    }

    public static TypeProcessor types() {
        return TYPE_PROCESSOR.supply(() -> Spider.getLoader(TypeProcessor.class).getDefaultExtension());
    }

    public static <T> EnumProcessor<T> enums() {
        //noinspection unchecked
        return ENUM_PROCESSOR.supply(() -> Spider.getLoader(EnumProcessor.class).getDefaultExtension());
    }

    public static BatchApi batch() {
        return BATCH_API.supply(() -> Spider.getDefaultExtension(BatchApi.class));
    }

    public static BatchApi ne() {
        return null;
    }

    public static GenericModel generic(String model, Object data) {
        if (null == data) {
            return null;
        }
        if (Map.class.isAssignableFrom(data.getClass())) {
            //noinspection unchecked
            return new GenericModel(model, (Map<String, Object>) data);
        } else if (GenericModel.class.isAssignableFrom(data.getClass())) {
            return (GenericModel) data;
        } else if (D.class.isAssignableFrom(data.getClass())) {
            return new GenericModel(model, ((D) data).get_d());
        } else {
            throw PamirsException.construct(BASE_UN_SUPPORT_DATA_TYPE_ERROR).errThrow();
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> d(Object data) {
        if (data instanceof Map) {
            return (Map<String, Object>) data;
        } else {
            return (Map<String, Object>) FieldUtils.getDValue(data);
        }
    }

    @SuppressWarnings("unchecked")
    public static void setD(Object data, Map<String, Object> dMap) {
        if (data instanceof Map) {
            ((Map<String, Object>) data).putAll(dMap);
        } else {
            FieldUtils.setDValue(data, dMap);
        }
    }

    public static DataManager data() {
        return ModelsHelper.data();
    }

    public static OriginDataManager origin() {
        return ModelsHelper.origin();
    }

    public static ConstructManager constructor() {
        return ModelsHelper.constructor();
    }

}
