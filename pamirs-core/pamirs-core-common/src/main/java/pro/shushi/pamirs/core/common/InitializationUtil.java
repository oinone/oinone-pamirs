package pro.shushi.pamirs.core.common;

import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import pro.shushi.pamirs.boot.base.constants.ViewActionConstants;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.model.*;
import pro.shushi.pamirs.boot.base.ux.model.UIView;
import pro.shushi.pamirs.boot.web.manager.UiIoManager;
import pro.shushi.pamirs.boot.web.signer.*;
import pro.shushi.pamirs.boot.web.utils.ViewXmlUtils;
import pro.shushi.pamirs.core.common.dsl.DslConverter;
import pro.shushi.pamirs.core.common.enmu.ModuleLifecycleEnum;
import pro.shushi.pamirs.framework.configure.annotation.core.sign.ModelDefinitionSigner;
import pro.shushi.pamirs.framework.configure.annotation.core.sign.SequenceConfigSigner;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.api.dto.config.TxConfig;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.api.dto.meta.api.MetaDataApi;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 元数据编辑初始化工具类
 *
 * @author Adamancy Zhang at 17:05 on 2021-05-08
 */
@Slf4j
@Validated
public class InitializationUtil {

    private static Map<String, InitializationUtil> context = new HashMap<>();

    /**
     * 文件路径前缀
     */
    public static final String filePrefix = "file:";

    /**
     * 默认列表页
     */
    public static final String DEFAULT_LIST = ViewActionConstants.redirectTablePage.name;

    /**
     * 默认创建页
     */
    public static final String DEFAULT_CREATE = ViewActionConstants.redirectCreatePage.name;

    /**
     * 默认编辑页
     */
    public static final String DEFAULT_UPDATE = ViewActionConstants.redirectUpdatePage.name;

    /**
     * 默认详情页
     */
    public static final String DEFAULT_DETAIL = ViewActionConstants.redirectDetailPage.name;

    /**
     * 默认导入页
     */
    public static final String DEFAULT_IMPORT = "redirectImportPage";

    /**
     * 默认导入页名称
     */
    public static final String DEFAULT_IMPORT_NAME = "导入";

    /**
     * 默认导出页
     */
    public static final String DEFAULT_EXPORT = "redirectExportPage";

    /**
     * 默认导出页名称
     */
    public static final String DEFAULT_EXPORT_NAME = "导出";

    /**
     * 默认删除按钮
     */
    public static final String DEFAULT_DELETE = FunctionConstants.delete;

    private final ClassLoader currentClassloader = InitializationUtil.class.getClassLoader();

    private final String module;
    private final String moduleName;
    private int defaultPriority;

    private final Map<String, Meta> metaMap;
    private final Meta meta;
    private final MetaData metadata;
    private final MetaData[] metadataDependencies;

    private final MetaDataApi metaDataApi;

    private InitializationUtil(Map<String, Meta> metaMap, Meta meta, MetaData metadata, MetaData[] metadataDependencies, String module, String moduleName) {
        this.metaMap = metaMap;
        this.meta = meta;
        this.metadata = metadata;
        this.metadataDependencies = metadataDependencies;
        this.module = module;
        this.moduleName = moduleName;
        this.defaultPriority = 10;
        this.metaDataApi = Spider.getDefaultExtension(MetaDataApi.class);
    }

    /**
     * 获取当前模块的初始化工具类
     *
     * @param metaMap    元数据上下文
     * @param module     模块编码
     * @param moduleName 模块名称
     * @return 初始化工具类
     */
    public static synchronized InitializationUtil get(Map<String, Meta> metaMap, String module, String moduleName) {
        Map<String, MetaData> metadataMap = Optional.ofNullable(metaMap)
                .map(v -> v.get(module))
                .map(Meta::getData)
                .orElseGet(HashMap::new);
        MetaData metadata = metadataMap.get(module);
        if (metadata == null) {
            return null;
        }
        int moduleDependencyCount = metadataMap.keySet().size() - 1;
        MetaData[] metadataDependencies = new MetaData[moduleDependencyCount];
        int i = 0;
        for (Map.Entry<String, MetaData> entry : metadataMap.entrySet()) {
            if (module.equals(entry.getKey())) {
                continue;
            }
            metadataDependencies[i++] = entry.getValue();
        }
        Meta meta = Optional.ofNullable(metaMap).map(v -> v.get(module)).orElseThrow(() -> new IllegalArgumentException("Module metadata is not found. module: " + module));
        return context.computeIfAbsent(fetchKey(module, moduleName), k -> new InitializationUtil(metaMap, meta, metadata, metadataDependencies, module, moduleName));
    }

    /**
     * 清理启动过程中保留的元数据上下文引用
     */
    public static synchronized void gc() {
        context.clear();
        context = null;
    }

    /**
     * 获取默认优先级
     *
     * @return 默认优先级
     */
    public int getDefaultPriority() {
        return defaultPriority;
    }

    /**
     * 设置默认优先级
     *
     * @param defaultPriority 默认优先级
     */
    public void setDefaultPriority(int defaultPriority) {
        this.defaultPriority = defaultPriority;
    }

    public Meta getMeta() {
        return meta;
    }

    /**
     * 获取模块编码
     *
     * @return 模块编码
     */
    public String getModule() {
        return module;
    }

    /**
     * 获取模块名称
     *
     * @return 模块名称
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * 获取所有模块
     *
     * @return 所有模块
     */
    public List<String> getAllModules() {
        return metaMap.values().stream()
                .map(Meta::getCurrentModule)
                .map(ModuleDefinition::getModule)
                .collect(Collectors.toList());
    }

    //region 视图

    /**
     * 创建视图
     *
     * @param model    模型编码
     * @param title    标题
     * @param name     视图名称
     * @param xmlPath  xml路径
     * @param viewType 视图类型
     * @param priority 优先级
     * @return 视图
     */
    public View createView(@NotBlank String model, @NotBlank String title, @NotBlank String name,
                           @NotBlank String xmlPath, @NotNull ViewTypeEnum viewType,
                           @NotNull Integer priority) {
        return createView(model, title, name, xmlPath, viewType, priority, true);
    }

    public View createView(@NotBlank String model, @NotBlank String title, @NotBlank String name,
                           @NotBlank String xmlPath, @NotNull ViewTypeEnum viewType,
                           @NotNull Integer priority, boolean v2) {
        return createViewByTemplate(model, title, name, getXmlTemplate(model, xmlPath, v2), viewType, priority);
    }

    /**
     * 创建视图
     *
     * @param model    模型编码
     * @param title    标题
     * @param name     视图名称
     * @param xmlPath  xml路径
     * @param viewType 视图类型
     * @return 视图
     */
    public View createView(@NotBlank String model, @NotBlank String title, @NotBlank String name,
                           @NotBlank String xmlPath, @NotNull ViewTypeEnum viewType) {
        return createView(model, title, name, xmlPath, viewType, defaultPriority, true);
    }

    public View createView(@NotBlank String model, @NotBlank String title, @NotBlank String name,
                           @NotBlank String xmlPath, @NotNull ViewTypeEnum viewType, boolean v2) {
        return createView(model, title, name, xmlPath, viewType, defaultPriority, v2);
    }

    /**
     * 创建视图
     *
     * @param model    模型编码
     * @param title    标题
     * @param name     视图名称
     * @param template xml
     * @param viewType 视图类型
     * @param priority 优先级
     * @return 视图
     */
    public View createViewByTemplate(@NotBlank String model, @NotBlank String title, @NotBlank String name,
                                     @NotBlank String template, @NotNull ViewTypeEnum viewType,
                                     @NotNull Integer priority) {
        View view = finder(View.MODEL_MODEL, fetchViewSign(model, name));
        if (view == null) {
            view = new View();
        }
        view.setModel(model)
                .setTitle(title)
                .setName(name)
                .setType(viewType)
                .setTemplate(template)
                .setPriority(priority)
                .setActive(ActiveEnum.ACTIVE);
        pushView(view);
        return view;
    }

    public View createViewByTemplate(@NotBlank String model, @NotBlank String title, @NotBlank String name,
                                     @NotBlank String template, @NotNull ViewTypeEnum viewType) {
        return createViewByTemplate(model, title, name, template, viewType, defaultPriority);
    }

    /**
     * 复制视图
     *
     * @param originModel    原模型编码
     * @param originViewName 原视图名称
     * @param targetModel    目标模型编码
     * @param targetViewName 目标视图名称
     * @return 新视图
     */
    public View copyView(String originModel, String originViewName, String targetModel, String targetViewName) {
        View view = getView(originModel, originViewName);
        if (view == null) {
            return null;
        }
        String template = view.getTemplate();
        if (StringUtils.isNotBlank(template)) {
            UIView uiView = BeanDefinitionUtils.getBean(UiIoManager.class).parseTemplate(template);
            uiView.setModel(targetModel);
            uiView.setName(targetViewName);
            template = ViewXmlUtils.toXML(uiView);
        }
        return createViewByTemplate(targetModel, view.getTitle(), targetViewName, template, view.getType(), view.getPriority());
    }

    /**
     * <p>获取视图</p>
     * <p>
     * 实现{@link pro.shushi.pamirs.boot.common.extend.MetaDataEditor}接口，必须声明执行顺序
     * <code>@Order(Ordered.LOWEST_PRECEDENCE - 70)</code>
     * 使其在{@link pro.shushi.pamirs.boot.web.spi.meta.LoadViewEditor}后执行</p>
     * </p>
     *
     * @param model 模型
     * @param name  视图名称
     * @return 视图
     */
    public View getView(String model, String name) {
        View view = finder(View.MODEL_MODEL, fetchViewSign(model, name));
        if (view == null) {
            log.warn("View is not found. model: {}, name: {}", model, name);
        }
        return view;
    }

    /**
     * 获取视图（可能为空）
     *
     * @param model 模型
     * @param name  视图名称
     * @return 视图
     */
    public View getViewOfNullable(String model, String name) {
        return finder(View.MODEL_MODEL, fetchViewSign(model, name));
    }

    /**
     * 获取通过{@link pro.shushi.pamirs.core.common.loader.ViewLoader}加载的视图名称，默认添加模块后缀
     *
     * @param name 视图名称
     * @return 视图名称
     */
    public String getViewNameByViewLoader(String name) {
        return getViewNameByViewLoader(name, true);
    }

    /**
     * 获取通过{@link pro.shushi.pamirs.core.common.loader.ViewLoader}加载的视图名称
     *
     * @param name            视图名称
     * @param useModuleSuffix 是否使用模块后缀
     * @return 视图名称
     */
    public String getViewNameByViewLoader(String name, boolean useModuleSuffix) {
        if (useModuleSuffix) {
            return name + CharacterConstants.SEPARATOR_UNDERLINE + module;
        }
        return name;
    }

    /**
     * 通过模型编码获取视图名称
     *
     * @param model 模型编码
     * @param type  视图类型
     * @return 视图名称
     */
    public String getViewNameByModel(String model, ViewTypeEnum type) {
        String[] ss = model.split(CharacterConstants.SEPARATOR_ESCAPE_DOT);
        model = ss[ss.length - 1];
        return model + CharacterConstants.SEPARATOR_OCTOTHORPE + type.value().toLowerCase();
    }

    /**
     * 新增元数据
     *
     * @param mateData
     */
    public void pushMeta(MetaBaseModel mateData) {
//        mateData.getSign();
        push(mateData.getSignModel(), mateData);
    }

    /**
     * 新增视图
     *
     * @param view 视图
     */
    public void pushView(View view) {
        view.setSign(fetchViewSign(view));
        push(View.MODEL_MODEL, view);
    }

    private String fetchViewSign(String model, String name) {
        return fetchViewSign(new View().setModel(model).setName(name));
    }

    private String fetchViewSign(View view) {
        return fetchSign(ViewSigner.class, view);
    }

    //endregion

    //region 窗口动作

    /**
     * 创建窗口动作
     *
     * @param name            窗口动作名称
     * @param displayName     按钮显示名称
     * @param originModel     源模型编码
     * @param originViewTypes 源视图类型
     * @param targetModel     目标模型编码
     * @param targetViewType  目标视图类型
     * @param contextType     上下文类型
     * @param pageTarget      页面打开方式
     * @param resViewName     指定视图名称
     * @return 窗口动作
     */
    public ViewAction createViewAction(@NotBlank String name, @NotBlank String displayName,
                                       @NotBlank String originModel, @NotEmpty List<ViewTypeEnum> originViewTypes, @NotBlank String targetModel, @NotNull ViewTypeEnum targetViewType,
                                       @NotNull ActionContextTypeEnum contextType, @NotNull ActionTargetEnum pageTarget,
                                       String resViewName) {
        return createViewAction(name, displayName, originModel, originViewTypes, targetModel, targetViewType, contextType, pageTarget, resViewName, null, null);
    }

    /**
     * 创建窗口动作
     *
     * @param name            窗口动作名称
     * @param displayName     按钮显示名称
     * @param originModel     源模型编码
     * @param originViewTypes 源视图类型
     * @param targetModel     目标模型编码
     * @param targetViewType  目标视图类型
     * @param contextType     上下文类型
     * @param pageTarget      页面打开方式
     * @param resViewName     指定视图名称
     * @param rule            按钮显示规则
     * @return 窗口动作
     * @deprecated 2.3.0
     */
    public ViewAction createViewAction(@NotBlank String name, @NotBlank String displayName,
                                       @NotBlank String originModel, @NotEmpty List<ViewTypeEnum> originViewTypes, @NotBlank String targetModel, @NotNull ViewTypeEnum targetViewType,
                                       @NotNull ActionContextTypeEnum contextType, @NotNull ActionTargetEnum pageTarget,
                                       String resViewName, @Deprecated String rule) {
        return createViewAction(name, displayName, originModel, originViewTypes, targetModel, targetViewType, contextType, pageTarget, resViewName, rule, null);
    }

    public ViewAction createViewAction(@NotBlank String name, @NotBlank String displayName,
                                       @NotBlank String originModel, @NotEmpty List<ViewTypeEnum> originViewTypes, @NotBlank String targetModel, @NotNull ViewTypeEnum targetViewType,
                                       @NotNull ActionContextTypeEnum contextType, @NotNull ActionTargetEnum pageTarget,
                                       String resViewName, @Deprecated String rule, Consumer<ViewAction> viewActionConsumer) {
        ViewAction viewAction = finder(ViewAction.MODEL_MODEL, fetchViewActionSign(originModel, name));
        if (viewAction == null) {
            viewAction = new ViewAction();
        }
        viewAction.setActionType(ActionTypeEnum.VIEW)
                .setModule(module)
                .setModuleName(moduleName)
                .setResModel(targetModel)
                .setResModule(null)
                .setResModuleName(null)
                .setViewType(targetViewType)
                .setTarget(pageTarget)
                .setModel(originModel)
                .setBindingType(originViewTypes)
                .setName(name)
                .setDisplayName(displayName)
                .setContextType(contextType);
        if (StringUtils.isNotBlank(resViewName)) {
            viewAction.setResView(getView(targetModel, resViewName))
                    .setResModel(targetModel)
                    .setResViewName(resViewName);
        }
        if (StringUtils.isBlank(viewAction.getLabel()) && StringUtils.isNotBlank(viewAction.getDisplayName())) {
            viewAction.setLabel(viewAction.getDisplayName());
        }
        if (StringUtils.isNotBlank(rule)) {
            viewAction.setRule(rule);
            //表达式取反
            viewAction.setInvisible("!(" + rule + ")");
        }
        //回调可能修改name,在生成前面之前处理回调
        if (viewActionConsumer != null) {
            viewActionConsumer.accept(viewAction);
        }
        pushViewAction(viewAction);
        return viewAction;
    }

    /**
     * 修改窗口动作
     *
     * @param model    模型编码
     * @param name     窗口动作名称
     * @param consumer 修改逻辑
     */
    public void modifyViewAction(String model, String name, Consumer<ViewAction> consumer) {
        if (consumer == null) {
            return;
        }
        ViewAction viewAction = getViewActionNullable(model, name);
        if (viewAction == null) {
            return;
        }
        consumer.accept(viewAction);
        if (viewAction.getResView() == null && StringUtils.isNotBlank(viewAction.getResViewName())) {
            viewAction.setResView(getView(model, viewAction.getResViewName()));
        }
    }

    /**
     * 获取窗口动作
     *
     * @param model 模型编码
     * @param name  窗口动作名称
     * @return 窗口动作
     */
    public ViewAction getViewAction(String model, String name) {
        ViewAction viewAction = getViewActionNullable(model, name);
        if (viewAction == null) {
            throw new IllegalArgumentException("ViewAction is not found. model: " + model + ", name: " + name);
        }
        return viewAction;
    }

    public ViewAction getViewActionNullable(String model, String name) {
        return finder(ViewAction.MODEL_MODEL, fetchViewActionSign(model, name));
    }

    private void pushViewAction(ViewAction viewAction) {
        viewAction.setSign(fetchViewActionSign(viewAction));
        push(ViewAction.MODEL_MODEL, viewAction);
    }

    private String fetchViewActionSign(String model, String name) {
        return fetchViewActionSign(new ViewAction().setModel(model).setName(name));
    }

    private String fetchViewActionSign(ViewAction viewAction) {
        return fetchSign(ViewActionSigner.class, viewAction);
    }

    //endregion

    //region 服务器动作

    /**
     * 修改服务器动作
     *
     * @param model    模型编码
     * @param name     服务器动作名称
     * @param consumer 修改逻辑
     */
    public void modifyServerAction(String model, String name, Consumer<ServerAction> consumer) {
        if (consumer == null) {
            return;
        }
        ServerAction serverAction = getServerAction(model, name);
        consumer.accept(serverAction);
    }

    /**
     * 获取服务器动作
     *
     * @param model 模型编码
     * @param name  服务器动作名称
     * @return 服务器动作
     */
    public ServerAction getServerAction(String model, String name) {
        ServerAction serverAction = getServerActionNullable(model, name);
        if (serverAction == null) {
            throw new IllegalArgumentException("ServerAction is not found. model: " + model + ", name: " + name);
        }
        return serverAction;
    }

    public ServerAction getServerActionNullable(String model, String name) {
        return finder(ServerAction.MODEL_MODEL, fetchServerActionSign(model, name));
    }

    private void pushServerAction(ServerAction serverAction) {
        serverAction.setSign(fetchServerActionSign(serverAction));
        push(ServerAction.MODEL_MODEL, serverAction);
    }

    private String fetchServerActionSign(String model, String name) {
        return fetchServerActionSign((ServerAction) new ServerAction().setModel(model).setName(name));
    }

    private String fetchServerActionSign(ServerAction serverAction) {
        return fetchSign(ServerActionSigner.class, serverAction);
    }

    //endregion

    //region URL动作

    /**
     * 创建URL动作
     *
     * @param name            窗口动作名称
     * @param displayName     按钮显示名称
     * @param url             目标URL
     * @param model           模型编码
     * @param viewTypes       视图类型
     * @param contextType     上下文类型
     * @param pageTarget      页面打开方式
     * @param bindingViewName 绑定视图名称（表示该动作仅在指定视图中展示）
     * @return URL动作
     */
    public UrlAction createUrlAction(@NotBlank String name, @NotBlank String displayName, @NotBlank String url,
                                     @NotBlank String model, @NotEmpty List<ViewTypeEnum> viewTypes,
                                     @NotNull ActionContextTypeEnum contextType, @NotNull ActionTargetEnum pageTarget,
                                     String bindingViewName) {
        return createUrlAction(name, displayName, url, model, viewTypes, contextType, pageTarget, bindingViewName, null);
    }

    /**
     * 创建URL动作
     *
     * @param name            窗口动作名称
     * @param displayName     按钮显示名称
     * @param url             目标URL
     * @param model           模型编码
     * @param viewTypes       视图类型
     * @param contextType     上下文类型
     * @param pageTarget      页面打开方式
     * @param bindingViewName 绑定视图名称（表示该动作仅在指定视图中展示）
     * @param rule            按钮显示规则
     * @return URL动作
     * @deprecated 2.3.0
     */
    public UrlAction createUrlAction(@NotBlank String name, @NotBlank String displayName, @NotBlank String url,
                                     @NotBlank String model, @NotEmpty List<ViewTypeEnum> viewTypes,
                                     @NotNull ActionContextTypeEnum contextType, @NotNull ActionTargetEnum pageTarget,
                                     String bindingViewName, @Deprecated String rule) {
        UrlAction urlAction = finder(UrlAction.MODEL_MODEL, fetchUrlActionSign(model, name));
        if (urlAction == null) {
            urlAction = new UrlAction();
        }
        urlAction.setUrl(url)
                .setTarget(pageTarget)
                .setActionType(ActionTypeEnum.URL)
                .setDisplayName(displayName)
                .setModel(model)
                .setBindingType(viewTypes)
                .setName(name)
                .setContextType(contextType);
        if (StringUtils.isNotBlank(bindingViewName)) {
            urlAction.setBindingView(getView(model, bindingViewName))
                    .setBindingViewName(bindingViewName);
        }
        if (StringUtils.isNotBlank(rule)) {
            urlAction.setRule(rule);
            //表达式取反
            urlAction.setInvisible("!(" + rule + ")");
        }
        pushUrlAction(urlAction);
        return urlAction;
    }

    /**
     * 获取链接动作
     *
     * @param model 模型编码
     * @param name  链接动作名称
     * @return 链接动作
     */
    public UrlAction getUrlAction(String model, String name) {
        UrlAction urlAction = getUrlActionNullable(model, name);
        if (urlAction == null) {
            throw new IllegalArgumentException("UrlAction is not found. model: " + model + ", name: " + name);
        }
        return urlAction;
    }

    public UrlAction getUrlActionNullable(String model, String name) {
        return finder(UrlAction.MODEL_MODEL, fetchUrlActionSign(model, name));
    }

    private void pushUrlAction(UrlAction urlAction) {
        urlAction.setSign(fetchUrlActionSign(urlAction));
        push(UrlAction.MODEL_MODEL, urlAction);
    }

    private String fetchUrlActionSign(String model, String name) {
        return fetchUrlActionSign((UrlAction) new UrlAction().setModel(model).setName(name));
    }

    private String fetchUrlActionSign(UrlAction urlAction) {
        return fetchSign(UrlActionSigner.class, urlAction);
    }

    //endregion


    //region 客户端动作

    /**
     * 创建客户端动作
     *
     * @param name            客户端动作名称
     * @param displayName     按钮显示名称
     * @param model           模型编码
     * @param viewTypes       视图类型
     * @param contextType     上下文类型
     * @param bindingViewName 绑定视图名称（表示该动作仅在指定视图中展示）
     * @return 客户端动作
     */
    public ClientAction createClientAction(@NotBlank String name, @NotBlank String displayName,
                                           @NotBlank String model, @NotEmpty List<ViewTypeEnum> viewTypes,
                                           @NotNull ActionContextTypeEnum contextType,
                                           String bindingViewName) {
        return createClientAction(name, displayName, model, viewTypes, contextType, bindingViewName, null);
    }

    /**
     * 创建客户端动作
     *
     * @param name            客户端动作名称
     * @param displayName     按钮显示名称
     * @param model           模型编码
     * @param viewTypes       视图类型
     * @param contextType     上下文类型
     * @param bindingViewName 绑定视图名称（表示该动作仅在指定视图中展示）
     * @param rule            按钮显示规则
     * @return 客户端动作
     * @deprecated 2.3.0
     */
    public ClientAction createClientAction(@NotBlank String name, @NotBlank String displayName,
                                           @NotBlank String model, @NotEmpty List<ViewTypeEnum> viewTypes,
                                           @NotNull ActionContextTypeEnum contextType,
                                           String bindingViewName, @Deprecated String rule) {
        ClientAction clintAction = finder(ClientAction.MODEL_MODEL, fetchClientActionSign(model, name));
        if (clintAction == null) {
            clintAction = new ClientAction();
        }
        clintAction
                .setActionType(ActionTypeEnum.CLIENT)
                .setLabel(displayName)
                .setDisplayName(displayName)
                .setModel(model)
                .setBindingType(viewTypes)
                .setName(name)
                .setContextType(contextType);
        if (StringUtils.isNotBlank(bindingViewName)) {
            clintAction.setBindingView(getView(model, bindingViewName))
                    .setBindingViewName(bindingViewName);
        }
        if (StringUtils.isNotBlank(rule)) {
            clintAction.setRule(rule);
            //表达式取反
            clintAction.setInvisible("!(" + rule + ")");
        }
        pushClientAction(clintAction);
        return clintAction;
    }

    /**
     * 获取客户端动作
     *
     * @param model 模型编码
     * @param name  客户端名称
     * @return 客户端动作
     */
    public ClientAction getClientAction(String model, String name) {
        ClientAction clientAction = getClientActionNullable(model, name);
        if (clientAction == null) {
            throw new IllegalArgumentException("ClientAction is not found. model: " + model + ", name: " + name);
        }
        return clientAction;
    }

    public ClientAction getClientActionNullable(String model, String name) {
        return finder(ClientAction.MODEL_MODEL, fetchClientActionSign(model, name));
    }

    private void pushClientAction(ClientAction clientAction) {
        clientAction.setSign(fetchClientActionSign(clientAction));
        push(ClientAction.MODEL_MODEL, clientAction);
    }

    private String fetchClientActionSign(String model, String name) {
        return fetchClientActionSign((ClientAction) new ClientAction().setModel(model).setName(name));
    }

    private String fetchClientActionSign(ClientAction clientAction) {
        return fetchSign(ClientActionSigner.class, clientAction);
    }

    //endregion

    /**
     * 删除指定动作
     *
     * @param model      模型编码
     * @param actionName 动作名称
     * @param actionType 动作类型
     */
    public void deleteAction(String model, String actionName, ActionTypeEnum actionType) {
        switch (actionType) {
            case VIEW:
                delete(ViewAction.MODEL_MODEL, fetchViewActionSign(model, actionName));
                break;
            case SERVER:
                delete(ServerAction.MODEL_MODEL, fetchServerActionSign(model, actionName));
                break;
            case URL:
                delete(UrlAction.MODEL_MODEL, fetchUrlActionSign(model, actionName));
                break;
            case CLIENT:
                delete(ClientAction.MODEL_MODEL, fetchClientActionSign(model, actionName));
                break;
            default:
                throw new UnsupportedOperationException("Invalid action type.");
        }
    }

    //region 菜单

    /**
     * 创建窗口动作菜单项（一般用于叶菜单项）
     *
     * @param menuName           菜单名称
     * @param displayName        菜单项显示名称
     * @param priority           优先级
     * @param parentMenuName     上级菜单名称
     * @param model              模型编码
     * @param resViewName        指定视图名称
     * @param viewActionConsumer 窗口动作编辑逻辑
     * @return 菜单项
     * @deprecated please using @UxMenus and @UxMenu create menu and view action
     */
    @Deprecated
    public Menu createViewActionMenu(@NotBlank String menuName, @NotBlank String displayName, @NotNull Long priority,
                                     String parentMenuName, String model, String resViewName,
                                     Consumer<ViewAction> viewActionConsumer) {
        ModelDefinition modelDefinition = finder(ModelDefinition.MODEL_MODEL, model);
        String title = Optional.ofNullable(modelDefinition).map(v -> v.getDisplayName() + ViewActionConstants.redirectTablePage.title).orElse(null);
        return createViewActionMenu(menuName, displayName, priority, parentMenuName, model, title, resViewName, viewActionConsumer);
    }

    /**
     * @deprecated please using @UxMenus and @UxMenu create menu and view action
     */
    @Deprecated
    public Menu createViewActionMenu(@NotBlank String menuName, @NotBlank String displayName, @NotNull Long priority,
                                     String parentMenuName, String model, String title, String resViewName,
                                     Consumer<ViewAction> viewActionConsumer) {
        Menu menu = finder(Menu.MODEL_MODEL, fetchMenuSign(module, menuName));
        if (menu == null) {
            menu = new Menu();
        }
        menu.setModule(module)
                .setDisplayName(displayName)
                .setName(menuName)
                .setParentName(parentMenuName)
                .setPriority(priority)
                .setClientTypes(Arrays.asList(ClientTypeEnum.PC, ClientTypeEnum.MOBILE, ClientTypeEnum.PAD))
                .setDefaultShow(ActiveEnum.ACTIVE);
        pushMenu(menu);
        if (StringUtils.isNotBlank(model)) {
            String viewActionTitle = Optional.ofNullable(title).orElse(menu.getDisplayName());
            String originViewActionName = getMenuViewActionName(menuName);
            ViewAction viewAction = createViewAction(originViewActionName, displayName,
                    model, getTableOptions(), model, ViewTypeEnum.TABLE,
                    ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.ROUTER,
                    resViewName, null,
                    v -> {
                        //对viewAction的修改,需要在此回调中全部完成,避免生成签名后修改相关属性
                        v.setTitle(viewActionTitle)
                                .setOptionViewTypes(getTableOptions())
                                .setBindingType(null);

                        if (viewActionConsumer != null) {
                            viewActionConsumer.accept(v);
                        }
                    });
            if (!originViewActionName.equals(viewAction.getName())) {
                throw new IllegalArgumentException("Modify the name of the ViewAction using metadata editing will result in a difference exception. Please create Menu and ViewAction and bind them accordingly.");
            }
            menu.setActionType(ActionTypeEnum.VIEW)
                    .setActionName(viewAction.getName())
                    .setModel(viewAction.getModel());
        }
        return menu;
    }

    /**
     * 创建窗口动作菜单项（一般用于根菜单项）
     *
     * @param menuName    菜单名称
     * @param displayName 菜单项显示名称
     * @param priority    优先级
     * @return 菜单项
     * @deprecated please using @UxMenus and @UxMenu create menu and view action
     */
    @Deprecated
    public Menu createViewActionMenu(@NotBlank String menuName, @NotBlank String displayName, @NotNull Long priority) {
        return createViewActionMenu(menuName, displayName, priority, null, null, null, null);
    }

    /**
     * 创建窗口动作菜单项（一般用于非叶菜单项）
     *
     * @param menuName       菜单名称
     * @param displayName    菜单项显示名称
     * @param priority       优先级
     * @param parentMenuName 上级菜单名称
     * @return 菜单项
     * @deprecated please using @UxMenus and @UxMenu create menu and view action
     */
    @Deprecated
    public Menu createViewActionMenu(@NotBlank String menuName, @NotBlank String displayName, @NotNull Long priority,
                                     String parentMenuName) {
        return createViewActionMenu(menuName, displayName, priority, parentMenuName, null, null, null);
    }

    /**
     * 获取菜单项
     *
     * @param name 菜单名称
     * @return 菜单项
     * @deprecated please using @UxMenus and @UxMenu create menu and view action
     */
    @Deprecated
    public Menu getMenu(String name) {
        Menu menu = finder(Menu.MODEL_MODEL, fetchMenuSign(module, name));
        if (menu == null) {
            throw new IllegalArgumentException("Menu is not found. module: " + module + ", name: " + name);
        }
        return menu;
    }

    /**
     * 获取菜单项的窗口动作名称
     *
     * @param name 菜单名称
     * @return 菜单项的窗口动作名称
     * @deprecated please using @UxMenus and @UxMenu create menu and view action
     */
    @Deprecated
    public String getMenuViewActionName(String name) {
        return fetchMenuSign(module, name);
    }

    /**
     * @deprecated please using @UxMenus and @UxMenu create menu and view action
     */
    @Deprecated
    private void pushMenu(Menu menu) {
        menu.setSign(fetchMenuSign(menu));
        push(Menu.MODEL_MODEL, menu);
    }

    /**
     * @deprecated please using @UxMenus and @UxMenu create menu and view action
     */
    @Deprecated
    private String fetchMenuSign(String module, String name) {
        return module + CharacterConstants.SEPARATOR_OCTOTHORPE + name;
    }

    /**
     * @deprecated please using @UxMenus and @UxMenu create menu and view action
     */
    @Deprecated
    private String fetchMenuSign(Menu menu) {
        return fetchMenuSign(menu.getModule(), menu.getName());
    }

    //endregion

    //region 页面

    /**
     * 创建母版
     *
     * @param name    页面名称
     * @param xmlPath xml路径
     * @return 页面
     */
    public MaskDefinition createMask(String name, String xmlPath) {
        MaskDefinition mask = finder(MaskDefinition.MODEL_MODEL, name);
        if (mask == null) {
            mask = new MaskDefinition();
        }
        mask.setName(name)
                .setTitle(name)
                .setTemplate(getXmlTemplate(MaskDefinition.MODEL_MODEL, xmlPath, false))
                .setActive(ActiveEnum.ACTIVE);
        pushMask(mask);
        return mask;
    }

    private void pushMask(MaskDefinition mask) {
        mask.setSign(mask.getName());
        push(MaskDefinition.MODEL_MODEL, mask);
    }

    //region 数据编码

    /**
     * 创建序列
     *
     * @param code     编码
     * @param sequence 序列类型
     * @param size     长度
     * @return 页面
     */
    public SequenceConfig createSequenceConfig(String displayName, String code, SequenceEnum sequence, Integer size) {
        SequenceConfig sequenceConfig = finder(SequenceConfig.MODEL_MODEL, fetchSequenceConfigSign(code));
        if (sequenceConfig == null) {
            sequenceConfig = new SequenceConfig();
        }
        sequenceConfig.setDisplayName(displayName)
                .setCode(code)
                .setModule(module)
                .setPrefix(CharacterConstants.SEPARATOR_EMPTY)
                .setSuffix(CharacterConstants.SEPARATOR_EMPTY)
                .setSize(size)
                .setFormat(CharacterConstants.SEPARATOR_EMPTY)
                .setStep(1)
                .setInitial(1L)
                .setSequence(sequence.value())
                .setShow(ActiveEnum.ACTIVE);
        pushSequenceConfig(sequenceConfig);
        return sequenceConfig;
    }

    private void pushSequenceConfig(SequenceConfig sequenceConfig) {
        sequenceConfig.setSign(fetchSequenceConfigSign(sequenceConfig));
        push(SequenceConfig.MODEL_MODEL, sequenceConfig);
    }

    private String fetchSequenceConfigSign(String code) {
        return fetchSequenceConfigSign(new SequenceConfig().setCode(code));
    }

    private String fetchSequenceConfigSign(SequenceConfig sequenceConfig) {
        return fetchSign(SequenceConfigSigner.class, sequenceConfig);
    }

    //endregion

    //region 模型

    /**
     * 获取模型定义
     *
     * @param model 模型编码
     * @return 模型定义
     */
    public ModelDefinition getModelDefinition(String model) {
        ModelDefinition modelDefinition = finder(ModelDefinition.MODEL_MODEL, fetchModelSign(model));
        if (modelDefinition == null) {
            throw new IllegalArgumentException("Model definition is not found. model: " + model);
        }
        return modelDefinition;
    }

    private String fetchModelSign(String model) {
        return fetchModelSign(new ModelDefinition().setModel(model));
    }

    private String fetchModelSign(ModelDefinition modelDefinition) {
        return fetchSign(ModelDefinitionSigner.class, modelDefinition);
    }

    //endregion

    // region 模块

    /**
     * 获取指定模块
     *
     * @param module 指定模块
     * @return 模块元数据
     */
    public UeModule getUeModule(String module) {
        return Optional.ofNullable(metaMap.get(module))
                .map(Meta::getData)
                .map(v -> v.get(module))
                .map(v -> getUeModuleNullable(v, module))
                .orElse(null);
    }

    public ModuleDefinition getModuleDefinition(String module) {
        return Optional.ofNullable(metaMap.get(module))
                .map(Meta::getData)
                .map(v -> v.get(module))
                .map(v -> getModuleDefinitionNullable(v, module))
                .orElse(null);
    }

    // endregion

    /**
     * 通过菜单名称设置模块首页
     *
     * @param menuName 菜单名称
     * @return 当前模块
     */
    public UeModule setHomepageByMenu(String menuName) {
        Menu menu = getMenu(menuName);
        return setHomepageByMenu(menu);
    }

    /**
     * 通过菜单设置模块首页
     *
     * @param menu 指定首页菜单
     * @return 当前模块
     */
    public UeModule setHomepageByMenu(Menu menu) {
        UeModule ueModule = getUeModule();
        ViewAction viewAction = menu.getViewAction();
        String homepageModel, homepageName;
        if (viewAction == null) {
            homepageModel = menu.getModel();
            homepageName = menu.getActionName();
        } else {
            homepageModel = viewAction.getModel();
            homepageName = viewAction.getName();
        }
        if (StringUtils.isAllBlank(homepageModel, homepageName)) {
            throw new IllegalArgumentException("The specified menu cannot be set as the module homepage. menu: " + menu.getName());
        }
        ueModule.disableMetaCompleted();
        ueModule.enableMetaDiffing();
        return (UeModule) ueModule.unsetHomePage()
                .setDefaultHomePageModel(homepageModel)
                .setDefaultHomePageName(homepageName);
    }

    /**
     * 通过窗口动作名称设置模块首页
     *
     * @param model      模型编码
     * @param actionName 窗口动作名称
     * @param maskName   母版名称
     * @return 当前模块
     */
    public UeModule setHomepageByViewAction(String model, String actionName, String maskName) {
        UeModule ueModule = getUeModule();
        ViewAction viewAction = getViewAction(model, actionName).setMask(maskName);
        return (UeModule) ueModule.unsetHomePage()
                .setDefaultHomePageModel(viewAction.getModel())
                .setDefaultHomePageName(viewAction.getName());
    }

    /**
     * 获取当前模块（当模块不是{@link UeModule}时，将进行自动转换）
     *
     * @return 当前模块
     */
    public UeModule getUeModule() {
        UeModule ueModule = getUeModuleNullable(module);
        if (ueModule == null) {
            throw new IllegalArgumentException("Invalid module definition. module: " + module);
        }
        return ueModule;
    }

    private UeModule getUeModuleNullable(String module) {
        return getUeModuleNullable(metadata, module);
    }

    private UeModule getUeModuleNullable(MetaData metadata, String module) {
        ModuleDefinition moduleDefinition = getModuleDefinitionNullable(metadata, module);
        if (moduleDefinition == null) {
            return null;
        }
        UeModule ueModule = CopyHelper.transfer(moduleDefinition, new UeModule());
        ueModule.setSign(moduleDefinition.getSign());
        ueModule.setHash(moduleDefinition.getHash());
        ueModule.setStringify(moduleDefinition.getStringify());
        return ueModule;
    }

    private ModuleDefinition getModuleDefinitionNullable(MetaData metadata, String module) {
        return finder(metadata, ModuleDefinition.MODEL_MODEL, module);
    }

    /**
     * 获取视图类型列表
     *
     * @param viewTypes 视图类型
     * @return 视图类型列表
     */
    public static List<ViewTypeEnum> getOptions(ViewTypeEnum... viewTypes) {
        return Lists.newArrayList(viewTypes);
    }

    /**
     * 获取列表页默认视图类型列表
     *
     * @return 列表页默认视图类型列表
     */
    public static List<ViewTypeEnum> getTableOptions() {
        return getOptions(ViewTypeEnum.TABLE, ViewTypeEnum.CHART);
    }

    /**
     * 获取表单页默认视图类型列表
     *
     * @return 表单页默认视图类型列表
     */
    public static List<ViewTypeEnum> getFormOptions() {
        return getOptions(ViewTypeEnum.FORM);
    }

    /**
     * 获取详情页默认视图类型列表
     *
     * @return 详情页默认视图类型列表
     */
    public static List<ViewTypeEnum> getDetailOptions() {
        return getOptions(ViewTypeEnum.DETAIL);
    }

    /**
     * 根据指定资源文件路径获取文件内容
     *
     * @param model 模型
     * @param path  资源文件路径
     * @return 文件内容
     */
    public String getXmlTemplate(String model, String path) {
        return getXmlTemplate(model, path, true);
    }

    public String getXmlTemplate(String model, String path, boolean v2) {
        String content;
        if (path.startsWith(filePrefix)) {
            path = path.substring(filePrefix.length());
            try (InputStream inputStream = currentClassloader.getResourceAsStream(path)) {
                if (inputStream == null) {
                    throw new IllegalArgumentException("File is not found. path: " + path);
                }
                content = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
                if (v2) {
                    content = DslConverter.convert(meta, model, content);
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Read file " + path + " error.", e);
            }
        } else {
            throw new IllegalArgumentException("Error file path prefix. path: " + path);
        }
        return content;
    }

    private static String fetchKey(String key, String name) {
        return key + "-" + name;
    }

    /**
     * 添加默认创建和更新的数据库事务
     *
     * @param modelModel 模型编码
     */
    public static void addTxConfigCreateAndDelete(String modelModel) {
        addTxConfig(modelModel, FunctionConstants.create, FunctionConstants.update);
    }

    /**
     * 添加默认创建、更新和删除的数据库事务
     *
     * @param modelModel 模型编码
     */
    public static void addTxConfigAll(String modelModel) {
        addTxConfig(modelModel, FunctionConstants.create, FunctionConstants.update, FunctionConstants.deleteWithFieldBatch);
    }

    /**
     * 添加默认数据库事务
     *
     * @param namespace 函数命名空间
     * @param funs      函数名称组
     */
    public static void addTxConfig(String namespace, String... funs) {
        addTxConfig(TxConfig::new, namespace, funs);
    }

    /**
     * 添加数据库事务
     *
     * @param namespace 函数命名空间
     * @param funs      函数名称组
     */
    public static void addTxConfig(Supplier<TxConfig> supplier, String namespace, String... funs) {
        if (supplier == null) {
            return;
        }
        RequestContext requestContext = PamirsSession.getContext();
        for (String fun : funs) {
            TxConfig txConfig = supplier.get();
            if (txConfig == null) {
                break;
            }
            requestContext.addTxConfig(txConfig.setNamespace(namespace).setFun(fun));
        }
    }

    /**
     * 生命周期完成时初始化
     *
     * @param installModules 按照模块
     * @param upgradeModules 更新模块
     * @param reloadModules  重载模块
     * @param consumer       初始化功能
     * @param moduleModules  触发模块列表
     * @return 是否执行初始化功能
     */
    public static boolean lifecycleCompletedInit(List<ModuleDefinition> installModules, List<ModuleDefinition> upgradeModules, List<ModuleDefinition> reloadModules, BiConsumer<ModuleLifecycleEnum, ModuleDefinition> consumer, String... moduleModules) {
        Set<String> moduleModuleSet = new HashSet<>(Arrays.asList(moduleModules));
        if (moduleModuleSet.isEmpty()) {
            return false;
        }
        if (lifecycleCompletedInit(installModules, ModuleLifecycleEnum.INSTALL, consumer, moduleModuleSet)) {
            return true;
        }
        if (lifecycleCompletedInit(upgradeModules, ModuleLifecycleEnum.UPGRADE, consumer, moduleModuleSet)) {
            return true;
        }
        if (lifecycleCompletedInit(reloadModules, ModuleLifecycleEnum.RELOAD, consumer, moduleModuleSet)) {
            return true;
        }
        return false;
    }

    private static boolean lifecycleCompletedInit(List<ModuleDefinition> modules, ModuleLifecycleEnum moduleState, BiConsumer<ModuleLifecycleEnum, ModuleDefinition> consumer, Set<String> moduleModules) {
        for (ModuleDefinition item : modules) {
            if (moduleModules.contains(item.getModule())) {
                consumer.accept(moduleState, item);
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <M, T extends ModelSigner<M>> String fetchSign(Class<T> cls, M object) {
        List<ModelSigner> signers = Spider.getLoader(ModelSigner.class).getExtensions();
        for (ModelSigner signer : signers) {
            if (cls.isAssignableFrom(signer.getClass())) {
                return signer.sign(object);
            }
        }
        throw new IllegalArgumentException("Model signer is not found. class: " + cls.getName());
    }

    @SuppressWarnings("unchecked")
    public <T extends MetaBaseModel> T finder(String group, String sign) {
        return (T) Optional.ofNullable(finder(metadata, group, sign))
                .orElseGet(() -> {
                    for (MetaData metadata : metadataDependencies) {
                        T target = finder(metadata, group, sign);
                        if (target != null) {
                            return target;
                        }
                    }
                    return null;
                });
    }

    @SuppressWarnings("unchecked")
    private <T extends MetaBaseModel> List<T> finders(String group) {
        return (List<T>) Optional.ofNullable(metadata.getData())
                .map(v -> v.get(group))
                .map(v -> new ArrayList<>(v.values()))
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    private <T extends MetaBaseModel> T finder(MetaData metadata, String group, String sign) {
        return (T) Optional.ofNullable(metadata.getData())
                .map(v -> v.get(group))
                .map(v -> v.get(sign))
                .orElse(null);
    }

    private <T extends MetaBaseModel> void push(String group, T object) {
        push(metadata, group, object);
    }

    private <T extends MetaBaseModel> void push(MetaData metadata, String group, T object) {
        if (!ModelDefinition.MODEL_MODEL.equals(group) && !ModuleDefinition.UE_MODEL_MODEL.equals(group)) {
            object.construct();
        }
        object.disableMetaCompleted();
        object.enableMetaDiffing();
        String sign = object.getSign();
        metaDataApi.whenAddDataItem(metadata.getData(), group, sign, object);
        metadata.getData().computeIfAbsent(group, k -> new ConcurrentHashMap<>()).put(sign, object);
    }

    private <T extends MetaBaseModel> void delete(String group, String sign) {
        Map<String, MetaBaseModel> targetMetadata = null;
        T target = finder(this.metadata, group, sign);
        if (target == null) {
            for (MetaData metadata : metadataDependencies) {
                target = finder(metadata, group, sign);
                if (target != null) {
                    targetMetadata = metadata.getData().get(group);
                    break;
                }
            }
        } else {
            targetMetadata = this.metadata.getData().get(group);
        }
        if (target == null) {
            return;
        }
        if (target.getId() == null) {
            targetMetadata.remove(sign);
        } else {
            Models.modelDirective().enableMetaCompleted(target);
        }
    }
}