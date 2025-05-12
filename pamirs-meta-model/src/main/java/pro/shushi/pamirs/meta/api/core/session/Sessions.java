package pro.shushi.pamirs.meta.api.core.session;

import org.apache.commons.collections.MapUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.container.StaticModelConfigContainer;
import pro.shushi.pamirs.meta.api.core.session.spi.SessionMetaCollectSpi;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestVariables;
import pro.shushi.pamirs.meta.api.enmu.BatchCommitTypeEnum;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.api.session.cache.api.*;
import pro.shushi.pamirs.meta.api.session.cache.extend.SessionCacheForPutAll;
import pro.shushi.pamirs.meta.api.session.cache.spi.SessionCacheFactoryApi;
import pro.shushi.pamirs.meta.api.session.cache.spi.SessionFillExtendApi;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.fun.*;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pro.shushi.pamirs.meta.common.util.TypeReferences.TR_MAP_SS;

/**
 * session元数据计算器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@Slf4j
public class Sessions {

    /**
     * 获取元数据配置
     *
     * @param context      元数据上下文
     * @param metaDataList 元数据列表
     * @return 返回值
     */
    @SuppressWarnings("unchecked")
    public static Result<RequestContext> fetchRequestContext(RequestContext context, List<MetaData> metaDataList) {
        Result<RequestContext> result = new Result<>();
        ModuleCacheApi moduleCache = context.getModuleCache();
        ModelCacheApi modelCache = context.getModelCache();
        SequenceConfigCacheApi sequenceConfigCache = context.getSequenceConfigCache();
        StandaloneFunCacheApi standaloneFunCache = context.getStandaloneFunCache();
        TxConfigCacheApi txConfigCache = context.getTxConfigCache();
        DataDictionaryCacheApi dictCache = context.getDictCache();
        InterfaceCacheApi interfaceCache = context.getInterfaceCache();
        HooksCacheApi hookCache = context.getHookCache();
        ExtPointImplementationCacheApi extPointImplementationCache = context.getExtPointImplementationCache();
        ExpressionDefinitionCacheApi expressionDefinitionCache = context.getExpressionDefinitionCache();
        ComputeDefinitionCacheApi computeDefinitionCache = context.getComputeDefinitionCache();
        result.setData(context);

        List<FunctionDefinition> functionDefinitionList = new ArrayList<>();
        for (MetaData metaData : metaDataList) {
            ModuleDefinition moduleDefinition = metaData.getModule();
            if (moduleDefinition==null) {
                //TODO:metaDataList中存在metaData对象中所有数据为空的情况,需要从上游找根本原因.
               continue;
            }
            moduleCache.put(moduleDefinition.getModule(), moduleDefinition);
            if (!CollectionUtils.isEmpty(metaData.getModelList())) {
                ModelConfig modelConfig;
                for (ModelDefinition modelDefinition : metaData.getModelList()) {
                    modelConfig = SessionsHelper.fetchModelConfig(modelDefinition);
                    String modelModel = modelConfig.getModel();
                    modelCache.put(modelModel, modelConfig);
                    if (ModuleConstants.MODULE_BASE.equals(modelConfig.getModule())) {
                        StaticModelConfigContainer.setBaseModelConfig(modelModel, modelConfig);
                    }
                }
            }

            List<FunctionDefinition> standAloneFunctionList = metaData.getStandAloneFunctionList();
            if (!CollectionUtils.isEmpty(standAloneFunctionList)) {
                functionDefinitionList.addAll(standAloneFunctionList);
            }

            List<DataDictionary> dataDictionaries = metaData.getDataList(DataDictionary.MODEL_MODEL);
            if (!CollectionUtils.isEmpty(dataDictionaries)) {
                for (DataDictionary dataDictionary : dataDictionaries) {
                    dictCache.put(dataDictionary.getDictionary(), dataDictionary);
                }
            }
            List<InterfaceDefinition> interfaces = metaData.getDataList(InterfaceDefinition.MODEL_MODEL);
            if (!CollectionUtils.isEmpty(interfaces)) {
                for (InterfaceDefinition interfaceDefinition : interfaces) {
                    interfaceCache.put(interfaceDefinition.getNamespace(), interfaceDefinition.getFun(), new Function(interfaceDefinition));
                }
            }
            List<Hook> hookList = metaData.getDataList(Hook.MODEL_MODEL);
            if (!CollectionUtils.isEmpty(hookList)) {
                hookCache.addAll(hookList);
            }
            List<ExtPointImplementation> extImplementationList = metaData.getDataList(ExtPointImplementation.MODEL_MODEL);
            if (!CollectionUtils.isEmpty(extImplementationList)) {
                for (ExtPointImplementation extPointImplementation : extImplementationList) {
                    extPointImplementationCache.putIfAbsent(extPointImplementation.getNamespace(), extPointImplementation.getName(), new ArrayList<>());
                    extPointImplementationCache.get(extPointImplementation.getNamespace(), extPointImplementation.getName()).add(extPointImplementation);
                }
            }
            Map<String, SequenceConfig> sequenceConfigMapPerModule = metaData.getDataMap(SequenceConfig.MODEL_MODEL);
            if (!MapUtils.isEmpty(sequenceConfigMapPerModule)) {
                ((SessionCacheForPutAll<SequenceConfig>) sequenceConfigCache).putAll(sequenceConfigMapPerModule);
            }
            Map<String, TransactionConfig> transactionConfigMapPerModule = metaData.getDataMap(TransactionConfig.MODEL_MODEL);
            if (!MapUtils.isEmpty(transactionConfigMapPerModule)) {
                for (String txKey : transactionConfigMapPerModule.keySet()) {
                    txConfigCache.put(txKey, SessionsHelper.convert(transactionConfigMapPerModule.get(txKey)));
                }
            }
            ((SessionCacheForPutAll<List<ExpressionDefinition>>) expressionDefinitionCache).putAll(metaData.getValidateExpressionMap());
            ((SessionCacheForPutAll<List<ComputeDefinition>>) computeDefinitionCache).putAll(metaData.getValidateFunMap());

            for (SessionMetaCollectSpi sessionMetaCollectSpi : Spider.getLoader(SessionMetaCollectSpi.class).getOrderedExtensions()) {
                sessionMetaCollectSpi.collect(metaData, context);
            }
        }
        if (!CollectionUtils.isEmpty(functionDefinitionList)) {
            for (FunctionDefinition functionDefinition : functionDefinitionList) {
                ModelConfig modelOfFunction = context.getModelConfig(functionDefinition.getNamespace());
                Function function = Fun.generate(functionDefinition);
                if (null != modelOfFunction) {
                    // SessionsHelper.fetchFunctionDefinitionManager().metaAdd(modelOfFunction, modelOfFunction.getFunctionList(), function);
                    List<Function> mFunctionList = modelOfFunction.getFunctionList();
                    Function isExist = mFunctionList.stream().filter(f -> f.fetchDslKey().equals(function.fetchDslKey())).findFirst().orElse(null);
                    if (isExist == null) {
                        SessionsHelper.fetchFunctionDefinitionManager().metaAdd(modelOfFunction, mFunctionList, function);
                        context.getModelConfig(functionDefinition.getNamespace()).setFunctionList(mFunctionList);
                    }
                } else {
                    String key = FunctionDefinition.sign(functionDefinition.getNamespace(), function.getFun());
                    standaloneFunCache.put(key, function);
                }
            }
        }
        return result;
    }

    /**
     * 将元数据填充到上下文(for test)
     *
     * @param metaDataList 元数据列表
     * @return 返回值
     */
    public static Result<Void> fillSession(List<MetaData> metaDataList) {
        SessionCacheFactoryApi sessionCacheFactoryApi = Spider.getDefaultExtension(SessionCacheFactoryApi.class);
        RequestContext context = new RequestContext().init(sessionCacheFactoryApi);
        Result<RequestContext> fetchResult = fetchRequestContext(context, metaDataList);
        if (fetchResult.isSuccess()) {
            if (null != PamirsSession.getContext()) {
                PamirsSession.getContext().clear();
            }
            PamirsSession.setContext(fetchResult.getData());
        }

        return new Result<Void>().fill(fetchResult);
    }

    /**
     * 将元数据填充到上下文
     *
     * @param context      元数据上下文
     * @param metaDataList 元数据列表
     * @return 返回值
     */
    public static Result<Void> fillSession(RequestContext context, List<MetaData> metaDataList, Boolean distSession, Boolean loadMeta) {
        Result<RequestContext> fetchResult = fetchRequestContext(context, metaDataList);
        if (fetchResult.isSuccess()) {
            if (null != PamirsSession.getContext()) {
                PamirsSession.getContext().clear();
            }
            PamirsSession.setContext(fetchResult.getData());
        }
        if (distSession) {
            // Session填充分布式缓存
            Spider.getDefaultExtension(SessionFillExtendApi.class).fillAllMetaData(context, metaDataList, loadMeta);
        }

        return new Result<Void>().fill(fetchResult);
    }

    public static Result<Void> fillSession(RequestContext context, List<MetaData> metaDataList) {
        return fillSession(context, metaDataList, false, false);
    }

    /**
     * 获取session
     *
     * @return 获取session map
     */
    public static Map<String, String> fetchSessionMap() {
        Map<String, String> context = new HashMap<>();
        //处理session
        SessionApi sessionApi = getSessionApi();
        context.put(PamirsSession.SESSION_ID, sessionApi.getSessionId());
        context.put(PamirsSession.SESSION_PRODUCT, sessionApi.getProduct());
        context.put(PamirsSession.SESSION_APP_ID, sessionApi.getAppId());
        context.put(PamirsSession.SESSION_APP_NAME, sessionApi.getAppName());
        context.put(PamirsSession.SESSION_SERV_APP, sessionApi.getServApp());
        context.put(PamirsSession.SESSION_ENV, sessionApi.getEnv());
        context.put(PamirsSession.SESSION_LANG, sessionApi.getLang());
        context.put(PamirsSession.SESSION_COUNTRY, sessionApi.getCountry());
        if (null != PamirsSession.getUserId()) {
            context.put(PamirsSession.SESSION_USER_ID, TypeUtils.serialize(sessionApi.getUserId()));
            context.put(PamirsSession.SESSION_USER_NAME, sessionApi.getUserName());
            context.put(PamirsSession.SESSION_USER_CODE, sessionApi.getUserCode());
            context.put(PamirsSession.SESSION_ADMIN_TAG, sessionApi.getAdminTag() + "");
        }
        PamirsRequestVariables requestVariables = sessionApi.getRequestVariables();
        if (requestVariables != null) {
            context.put(PamirsSession.REQUEST_VARIABLES, TypeUtils.serialize(requestVariables));
        }
        context.put(PamirsSession.SESSION_DIRECTIVE, TypeUtils.toString(sessionApi.directive().bitValue()));
        context.put(PamirsSession.SESSION_STATIC_CONFIG, Boolean.toString(sessionApi.isStaticConfig()));
        if (null != sessionApi.getBatchOperation()) {
            context.put(PamirsSession.SESSION_BATCH_OPERATION, sessionApi.getBatchOperation().name());
        }
        context.put(PamirsSession.SESSION_KERNEL_EXTEND, JsonUtils.toJSONString(sessionApi.getKernelExtend()));
        context.put(PamirsSession.SESSION_TRANSMITTABLE_EXTEND, JsonUtils.toJSONString(sessionApi.getTransmittableExtend()));
        return context;
    }

    /**
     * 设置session map到 session
     *
     * @param sessionMap session map
     */
    public static void fillSessionFromMap(Map<String, String> sessionMap) {
        String product = sessionMap.get(PamirsSession.SESSION_PRODUCT);
        String sessionId = sessionMap.get(PamirsSession.SESSION_ID);
        String appId = sessionMap.get(PamirsSession.SESSION_APP_ID);
        String appName = sessionMap.get(PamirsSession.SESSION_APP_NAME);
        String servApp = sessionMap.get(PamirsSession.SESSION_SERV_APP);
        String env = sessionMap.get(PamirsSession.SESSION_ENV);
        String lang = sessionMap.get(PamirsSession.SESSION_LANG);
        String country = sessionMap.get(PamirsSession.SESSION_COUNTRY);
        String userIdSerializable = sessionMap.get(PamirsSession.SESSION_USER_ID);
        String requestVariables = sessionMap.get(PamirsSession.REQUEST_VARIABLES);
        String kernelExtend = sessionMap.get(PamirsSession.SESSION_KERNEL_EXTEND);
        String transmittableExtend = sessionMap.get(PamirsSession.SESSION_TRANSMITTABLE_EXTEND);
        //将session值放入session中
        SessionApi sessionApi = getSessionApi();
        sessionApi.setProduct(product);
        sessionApi.setSessionId(sessionId);
        sessionApi.setAppId(appId);
        sessionApi.setAppName(appName);
        sessionApi.setServApp(servApp);
        sessionApi.setEnv(env);
        sessionApi.setLang(lang);
        sessionApi.setCountry(country);
        if (null != userIdSerializable) {
            Serializable userId = TypeUtils.deserialize(userIdSerializable);
            String userName = sessionMap.get(PamirsSession.SESSION_USER_NAME);
            String userCode = sessionMap.get(PamirsSession.SESSION_USER_CODE);
            Boolean adminTag = Boolean.valueOf(sessionMap.get(PamirsSession.SESSION_ADMIN_TAG));
            sessionApi.setUserId(userId);
            sessionApi.setUserName(userName);
            sessionApi.setUserCode(userCode);
            sessionApi.setAdminTag(adminTag);
        }
        if (requestVariables != null) {
            sessionApi.setRequestVariables(TypeUtils.deserialize(requestVariables));
        }
        Long directive = TypeUtils.createLong(sessionMap.get(PamirsSession.SESSION_DIRECTIVE));
        if (directive != null) {
            sessionApi.directive().initMetaBit(directive);
        }
        sessionApi.setStaticConfig(Boolean.parseBoolean(sessionMap.get(PamirsSession.SESSION_STATIC_CONFIG)));
        String batchOperation = sessionMap.get(PamirsSession.SESSION_BATCH_OPERATION);
        if (null != batchOperation) {
            sessionApi.setBatchOperation(BatchCommitTypeEnum.valueOf(batchOperation));
        }
        if (kernelExtend != null) {
            sessionApi.setKernelExtend(JsonUtils.parseObject(kernelExtend, TR_MAP_SS.getType()));
        }
        if (transmittableExtend != null) {
            sessionApi.setTransmittableExtend(JsonUtils.parseObject(transmittableExtend, TR_MAP_SS.getType()));
        }
    }

    private static SessionApi getSessionApi() {
        return Spider.getDefaultExtension(SessionApi.class);
    }

}
