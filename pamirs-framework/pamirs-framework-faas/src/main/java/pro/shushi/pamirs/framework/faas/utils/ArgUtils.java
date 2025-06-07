package pro.shushi.pamirs.framework.faas.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.common.serialize.KryoSerializer;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.convert.ClientDataConverter;
import pro.shushi.pamirs.meta.api.core.orm.template.context.ModelComputeContext;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Arg;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.base.K2;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.util.UnsafeUtil;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static pro.shushi.pamirs.framework.faas.enmu.FaasExpEnumerate.BASE_ARGUMENT_NUM_ERROR;

/**
 * 参数处理工具类
 * <p>
 * 2020/7/24 10:32 上午
 *
 * @author d@shushi.pro
 * @author cpc@shushi.pro
 * @version 1.0.0
 */
@Slf4j
public class ArgUtils {

    private static final String MAP_NAME = Map.class.getName();

    public static Object[] handleArgs(List<Arg> argList, Object... args) {
        if (null == argList) {
            return args;
        }
        int argListSize = argList.size();
        if (null == args && argListSize > 0) {
            args = new Object[argListSize];
        }
        int i = 0;
        for (Arg arg : argList) {
            String model = arg.getModel();
            if (K2.MODEL_MODEL.equals(model) || null == model) {
                i++;
                continue;
            }
            try {
                if (null != args[i]) {
                    String objModel = Models.api().getModel(args[i]);
                    if (null != objModel) {
                        model = objModel;
                    }
                    if (!IWrapper.MODEL_MODEL.equals(model) && !MAP_NAME.equals(arg.getLtype())) {
                        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
                        model = getLocalModel(model, modelConfig);
                        args[i] = Models.mono().objecting(model, args[i]);
                    }
                } else {
                    args[i] = null;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                throw PamirsException.construct(BASE_ARGUMENT_NUM_ERROR, e).errThrow();
            }
            i++;
        }
        return args;
    }

    private static String getLocalModel(String model, ModelConfig modelConfig) {
        if (pro.shushi.pamirs.meta.util.ClassUtils.isNoClass(modelConfig.getLname())) {
            //类找不到
            for (String s : modelConfig.getSuperModels()) {
                ModelConfig superModel = PamirsSession.getContext().getModelConfig(s);
                if (!pro.shushi.pamirs.meta.util.ClassUtils.isNoClass(superModel.getLname())) {
                    return s;
                } else {
                    return getLocalModel(s, superModel);
                }
            }
        }
        return model;
    }

    public static Object[] convert(String originModel, String targetModel, Object... args) {
        return mapsToModels(targetModel, modelsToMaps(originModel, args));
    }

    @SuppressWarnings("unchecked")
    public static <O extends D, T extends D> T convert(final String originModel, final String targetModel, O arg) {
        return (T) Optional.ofNullable(arg)
                .map(_arg -> modelsToMaps(originModel, new Object[]{_arg}))
                .map(_objects -> mapsToModels(targetModel, _objects))
                .filter(_objects -> _objects.length > 0)
                .map(_objects -> _objects[0])
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    public static <O extends D, T extends D> List<T> convert(final String originModel, final String targetModel, List<O> arg) {
        return (List<T>) Optional.ofNullable(arg)
                .map(_arg -> modelsToMaps(originModel, new Object[]{_arg}))
                .map(_objects -> mapsToModels(targetModel, _objects))
                .filter(_objects -> _objects.length > 0)
                .map(_objects -> _objects[0])
                .orElse(null);
    }

    public static Object[] modelsToMaps(String model, Object[] args) {
        Object[] mappingArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof IWrapper) {
                IWrapper<?> wrapper = ((IWrapper<?>) arg);
                UnsafeUtil.setValue(wrapper, FieldConstants.ENTITY, Models.orm().mapping(model, wrapper.getEntity()));
                UnsafeUtil.setValue(wrapper, FieldConstants.ENTITY_CLASS, null);
                mappingArgs[i] = wrapper.setModel(model);
            } else if (arg instanceof Pagination) {
                Pagination<?> pagination = ((Pagination<?>) arg);
                pagination.setContent(listHandler(model, pagination.getContent(), (m, a) -> Models.orm().mapping(m, a)));
                mappingArgs[i] = pagination.setModel(model);
            } else {
                mappingArgs[i] = Models.orm().mapping(model, args[i]);
            }
        }
        return mappingArgs;
    }

    public static Object[] modelsToDataModelMaps(String dataModel, Object[] args) {
        Object[] mappingArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof IWrapper) {
                IWrapper<?> wrapper = ((IWrapper<?>) arg);
                Models.api().setDataModel(dataModel, wrapper.getEntity());
                UnsafeUtil.setValue(wrapper, FieldConstants.ENTITY, Models.orm().mapping(dataModel, wrapper.getEntity()));
                UnsafeUtil.setValue(wrapper, FieldConstants.ENTITY_CLASS, null);
                mappingArgs[i] = wrapper.setModel(dataModel);
            } else if (arg instanceof Pagination) {
                Pagination<?> pagination = ((Pagination<?>) arg);
                pagination.setContent(listHandler(dataModel, pagination.getContent(), (m, a) -> {
                    Models.api().setDataModel(m, a);
                    return Models.orm().mapping(m, a);
                }));
                mappingArgs[i] = pagination.setModel(dataModel);
            } else {
                Models.api().setDataModel(dataModel, args[i]);
                mappingArgs[i] = Models.orm().mapping(dataModel, args[i]);
            }
        }
        return mappingArgs;
    }

    public static Object[] mapsToModels(String model, Object[] args) {
        Object[] objArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof IWrapper) {
                IWrapper<?> wrapper = ((IWrapper<?>) arg);
                wrapper.setModel(model);
                Object entity = wrapper.getEntity();
                UnsafeUtil.setValue(wrapper, FieldConstants.ENTITY, Models.orm().objecting(model, entity));
                Class<?> entityClass = null == wrapper.getEntity() ? null : wrapper.getEntity().getClass();
                if (null != entityClass) {
                    UnsafeUtil.setValue(wrapper, FieldConstants.ENTITY_CLASS, entityClass);
                }
                objArgs[i] = wrapper;
            } else if (arg instanceof Pagination) {
                Pagination<?> pagination = ((Pagination<?>) arg);
                pagination.setModel(model);
                objArgs[i] = pagination.setContent(listHandler(model, pagination.getContent(),
                        (m, a) -> Models.orm().objecting(model, a)));
            } else {
                objArgs[i] = Models.orm().objecting(model, args[i]);
            }
        }
        return objArgs;
    }

    public static Object result(String model, Object result) {
        return result(model, new Object[]{result})[0];
    }

    public static Object[] result(String model, Object[] args) {
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof IWrapper) {
                IWrapper<?> wrapper = ((IWrapper<?>) arg);
                UnsafeUtil.setValue(wrapper, FieldConstants.ENTITY, Models.orm().objecting(model, wrapper.getEntity()));
                Class<?> entityClass = null == wrapper.getEntity() ? null : wrapper.getEntity().getClass();
                if (null != entityClass) {
                    UnsafeUtil.setValue(wrapper, FieldConstants.ENTITY_CLASS, entityClass);
                }
                args[i] = wrapper.setModel(model);
            } else if (arg instanceof Pagination) {
                Pagination<?> pagination = ((Pagination<?>) arg);
                pagination.setContent(listHandler(model, pagination.getContent(), (m, a) -> Models.orm().objecting(m, a)));
                args[i] = pagination.setModel(model);
            } else {
                args[i] = Models.orm().objecting(model, arg);
            }
        }
        return args;
    }

    public static Object remoteProducerResultToDataMap(String model, Object result) {
        try {
            if (result == null) {
                return null;
            }
            ClientDataConverter dataConverter = ClientDataConverter.get();
            if (result instanceof IWrapper) {
                IWrapper<?> wrapper = ((IWrapper<?>) result);
                Object entity = wrapper.getEntity();
                entity = dataConverter.out(model, entity, ClientDataConverter.CLIENT_TYPE_RPC);
                UnsafeUtil.setValue(wrapper, FieldConstants.ENTITY, entity);
            } else if (result instanceof Pagination) {
                Pagination<?> pagination = ((Pagination<?>) result);
                pagination.setContent(dataConverter.out(model, pagination.getContent(), ClientDataConverter.CLIENT_TYPE_RPC));
            } else if (result instanceof Result) {
                Result<?> result1 = (Result<?>) result;
                Object data = result1.getData();
                result1.setData(dataConverter.out(model, data, ClientDataConverter.CLIENT_TYPE_RPC));
            } else {
                result = dataConverter.out(model, result, ClientDataConverter.CLIENT_TYPE_RPC);
            }
            return result;
        } catch (Exception e) {
            log.error("remote result model:[{}],class:[{}],obj:[{}]", model, result.getClass(), JsonUtils.toJSONString(result));
            throw e;
        }
    }

    public static Object remoteClientResultToDataMap(String model, Object result) {
        try {
            if (result == null) {
                return null;
            }
            ClientDataConverter dataConverter = ClientDataConverter.get();
            if (result instanceof IWrapper) {
                IWrapper<?> wrapper = ((IWrapper<?>) result);
                Object entity = wrapper.getEntity();
                entity = dataConverter.in(new ModelComputeContext(), model, entity, ClientDataConverter.CLIENT_TYPE_RPC);
                UnsafeUtil.setValue(wrapper, FieldConstants.ENTITY, entity);
            } else if (result instanceof Pagination) {
                Pagination<?> pagination = ((Pagination<?>) result);
                pagination.setContent(dataConverter.in(new ModelComputeContext(), model, pagination.getContent(), ClientDataConverter.CLIENT_TYPE_RPC));
            } else if (result instanceof Result) {
                Result<?> result1 = (Result<?>) result;
                Object data = result1.getData();
                result1.setData(dataConverter.in(new ModelComputeContext(), model, data, ClientDataConverter.CLIENT_TYPE_RPC));
            } else {
                result = dataConverter.in(new ModelComputeContext(), model, result, ClientDataConverter.CLIENT_TYPE_RPC);
            }
            return result;
        } catch (Exception e) {
            log.error("remote result model:[{}],class:[{}],obj:[{}]", model, result.getClass(), JsonUtils.toJSONString(result));
            throw e;
        }
    }

    public static Object[] remoteClientArgToDataMap(String defaultModel, Object[] args, List<Arg> funArgs) {
        try {
            if (args == null) {
                return null;
            }
            Object[] objArgs = new Object[args.length];
            ClientDataConverter dataConverter = ClientDataConverter.get();
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg instanceof IWrapper) {
                    IWrapper<?> wrapper = ((IWrapper<?>) arg);
                    Object entity = wrapper.getEntity();
                    if (entity == null) {
                        objArgs[i] = arg;
                        continue;
                    }
                    String model = getModel(defaultModel, entity, wrapper::getModel, wrapper::setModel);
                    if (StringUtils.isNotBlank(model)) {
                        UnsafeUtil.setValue(wrapper, FieldConstants.ENTITY, dataConverter.out(model, KryoSerializer.copy(entity), ClientDataConverter.CLIENT_TYPE_RPC));
                    }
                    Class<?> entityClass = entity.getClass();
                    if (null != entityClass) {
                        UnsafeUtil.setValue(wrapper, FieldConstants.ENTITY_CLASS, entityClass);
                    }
                    objArgs[i] = arg;
                } else if (arg instanceof Pagination) {
                    Pagination<?> pagination = ((Pagination<?>) arg);
                    List<?> content = pagination.getContent();
                    if (CollectionUtils.isEmpty(content)) {
                        objArgs[i] = arg;
                        continue;
                    }
                    String model = getModel(defaultModel, pagination.getContent(), pagination::getModel, pagination::setModel);
                    if (StringUtils.isNotBlank(model)) {
                        pagination.setContent(dataConverter.out(model, KryoSerializer.copy(pagination.getContent()), ClientDataConverter.CLIENT_TYPE_RPC));
                    }
                    objArgs[i] = arg;
                } else {
                    if (StringUtils.isNotBlank(funArgs.get(i).getModel())) {
                        String dataModel = Models.api().getDataModel(arg);
                        dataModel = dataModel == null ? Models.api().getModel(arg) : dataModel;
                        dataModel = dataModel == null ? funArgs.get(i).getModel() : dataModel;
                        objArgs[i] = dataConverter.out(dataModel, KryoSerializer.copy(arg), ClientDataConverter.CLIENT_TYPE_RPC);
                    } else {
                        objArgs[i] = arg;
                    }
                }
            }
            return objArgs;
        } catch (Exception e) {
            log.error("remote arg model:[{}],obj:[{}]", defaultModel, JsonUtils.toJSONString(args));
            throw e;
        }
    }

    public static Object[] remoteProducerArgDataMapToModel(String defaultModel, Object[] args, List<Arg> funArgs) {
        try {
            if (args == null) {
                return null;
            }
            Object[] objArgs = new Object[args.length];
            ClientDataConverter dataConverter = ClientDataConverter.get();
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg instanceof IWrapper) {
                    IWrapper<?> wrapper = ((IWrapper<?>) arg);
                    Object entity = wrapper.getEntity();
                    if (entity == null) {
                        objArgs[i] = arg;
                        continue;
                    }
                    String model = getModel(defaultModel, entity, wrapper::getModel, wrapper::setModel);
                    if (StringUtils.isNotBlank(model)) {
                        UnsafeUtil.setValue(wrapper, FieldConstants.ENTITY, dataConverter.in(new ModelComputeContext(), model, entity, ClientDataConverter.CLIENT_TYPE_RPC));
                    }
                    Class<?> entityClass = null == wrapper.getEntity() ? null : wrapper.getEntity().getClass();
                    if (null != entityClass) {
                        UnsafeUtil.setValue(wrapper, FieldConstants.ENTITY_CLASS, entityClass);
                    }
                    objArgs[i] = arg;
                } else if (arg instanceof Pagination) {
                    Pagination<?> pagination = ((Pagination<?>) arg);
                    String model = getModel(defaultModel, pagination.getContent(), pagination::getModel, pagination::setModel);
                    if (StringUtils.isNotBlank(model)) {
                        pagination.setContent(dataConverter.in(new ModelComputeContext(), model, pagination.getContent(), ClientDataConverter.CLIENT_TYPE_RPC));
                    }
                    objArgs[i] = arg;
                } else {
                    String argModel = funArgs.get(i).getModel();
                    if (StringUtils.isNotBlank(argModel)) {
                        objArgs[i] = dataConverter.in(new ModelComputeContext(), argModel, arg, ClientDataConverter.CLIENT_TYPE_RPC);
                        String dataModel = Models.api().getDataModel(arg);
                        if (StringUtils.isNotBlank(dataModel) && !argModel.equals(dataModel)) {
                            Models.api().setDataModel(argModel, objArgs[i]);
                        }
                    } else {
                        objArgs[i] = arg;
                    }
                }
            }
            return objArgs;
        } catch (Exception e) {
            log.error("remote arg model:[{}],obj:[{}]", defaultModel, JsonUtils.toJSONString(args));
            throw e;
        }
    }

    public static String getModel(String defaultModel, Object entity, Supplier<String> getter, Consumer<String> setter) {
        String model;
        ModelConfig modelConfig = PamirsSession.getContext().getSimpleModelConfig(defaultModel);
        if (modelConfig != null) {
            model = defaultModel;
        } else {
            model = getter.get();
            if (StringUtils.isBlank(model) ||
                    Pagination.MODEL_MODEL.equals(model) ||
                    IWrapper.MODEL_MODEL.equals(model)) {
                model = Models.api().getModel(entity);
                if (StringUtils.isNotBlank(model)) {
                    setter.accept(model);
                }
            }
        }
        return model;
    }

    public static <T, R> List<R> listHandler(String model, List<T> args, BiFunction<String, T, R> handler) {
        List<R> resultList = new ArrayList<>();
        for (T arg : args) {
            resultList.add(handler.apply(model, arg));
        }
        return resultList;
    }
}
