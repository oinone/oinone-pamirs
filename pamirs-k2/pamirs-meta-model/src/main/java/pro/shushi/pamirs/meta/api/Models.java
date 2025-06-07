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
import java.util.function.Supplier;

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
    private final static Supplier<BatchApi> BATCH_API = () -> Spider.getDefaultExtension(BatchApi.class);

    public static ModelModelApi api() {
        return Models.getApi(ModelModelApi.class, MODEL_MODEL_API, () -> Spider.getLoader(ModelModelApi.class).getDefaultExtension());
    }

    public static ModelComputeApi compute() {
        return Models.getApi(ModelComputeApi.class, MODEL_COMPUTE_API, () -> Spider.getLoader(ModelComputeApi.class).getDefaultExtension());
    }

    public static ModelCheckApi check() {
        return Models.getApi(ModelCheckApi.class, MODEL_CHECK_API, () -> Spider.getLoader(ModelCheckApi.class).getDefaultExtension());
    }

    public static DirectiveApi directive() {
        return Models.getApi(DirectiveApi.class, DIRECTIVE_API, () -> Spider.getLoader(DirectiveApi.class).getDefaultExtension());
    }

    public static ModelDirectiveApi modelDirective() {
        return Models.getApi(ModelDirectiveApi.class, MODEL_DIRECTIVE_API, () -> Spider.getLoader(ModelDirectiveApi.class).getDefaultExtension());
    }

    public static OrmApi orm() {
        return Models.getApi(OrmApi.class, ORM_API, () -> Spider.getLoader(OrmApi.class).getDefaultExtension());
    }

    public static OrmApi mono() {
        return Models.getApi(OrmApi.class, MONO_ORM_API, () -> Spider.getLoader(OrmApi.class).getExtension(NamespaceConstants.spiMono));
    }

    public static PopsApi pops() {
        return Models.getApi(PopsApi.class, POPS_API, () -> Spider.getLoader(PopsApi.class).getDefaultExtension());
    }

    public static ModelInheritedApi inherited() {
        return Models.getApi(ModelInheritedApi.class, MODEL_INHERITED_API, () -> Spider.getLoader(ModelInheritedApi.class).getDefaultExtension());
    }

    public static TypeProcessor types() {
        return Models.getApi(TypeProcessor.class, TYPE_PROCESSOR, () -> Spider.getLoader(TypeProcessor.class).getDefaultExtension());
    }

    public static <T> EnumProcessor<T> enums() {
        //noinspection unchecked
        return Models.getApi(EnumProcessor.class, ENUM_PROCESSOR, () -> Spider.getLoader(EnumProcessor.class).getDefaultExtension());
    }

    public static BatchApi batch() {
        return getApiBySupplier(BatchApi.class, BATCH_API);
    }

    public static BatchApi ne() {
        return getApiBySupplier(null, () -> null);
    }

    private static <T> T getApi(@SuppressWarnings("unused") Class<T> api, HoldKeeper<T> keeper, Supplier<T> supplier) {
        return keeper.supply(supplier);
    }

    private static <T> T getApiBySupplier(@SuppressWarnings("unused") Class<T> api, Supplier<T> defaultApiSupplier) {
        return defaultApiSupplier.get();
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
