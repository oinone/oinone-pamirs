package pro.shushi.pamirs.core.common.loader.view;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.core.common.CollectionHelper;
import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.core.common.directive.DirectiveHelper;
import pro.shushi.pamirs.core.common.dsl.DslConverter;
import pro.shushi.pamirs.core.common.loader.ViewLoader;
import pro.shushi.pamirs.core.common.xstream.TreeNodeXStream;
import pro.shushi.pamirs.core.common.xstream.XMLNodeContent;
import pro.shushi.pamirs.framework.common.entry.TreeNode;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.util.AppClassLoader;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 视图自动加载
 *
 * @author Adamancy Zhang on 2021-05-18 15:02
 */
@Slf4j
public class ViewAutoLoader {

    private static final String DEFAULT_RESOURCE_PATTERN = "classpath*:/pamirs/init/views";

    private static final String XML_FILE_EXTENSION = ".xml";

    private static final String ROOT_KEY = "view";

    private static final String VIEW_ACTION_DATA_PREFIX = "view-action-";

    private static final String SEARCH_ROOT_KEY = "search";

    private static final List<String> VALID_ROOT_KEYS = Collections.unmodifiableList(CollectionHelper.<String>newInstance()
            .add(ROOT_KEY)
            .add(SEARCH_ROOT_KEY)
            .build());

    private static final String VIEW_ACTION_NAMES_KEY = "viewActionNames";

    private static final String DEFAULT_LIST_TITLE_SUFFIX = "列表";

    private static final String DEFAULT_FORM_TITLE_SUFFIX = "表单";

    private static final String DEFAULT_DETAIL_TITLE_SUFFIX = "详情";

    private ViewAutoLoader() {
        //reject create object
    }

    /**
     * 默认加载（推荐使用）
     *
     * @param util 当前模块初始化工具类
     */
    public static void defaultLoad(InitializationUtil util) {
        load(util, ViewLoaderFeature.USING_MODULE_SUFFIX,
                ViewLoaderFeature.ONLY_LOAD_CURRENT_MODULE,
                ViewLoaderFeature.RECURSION_FOLDER,
                ViewLoaderFeature.LOAD_TYPE_BY_FILENAME);
    }

    /**
     * 加载
     *
     * @param util     当前模块初始化工具类
     * @param features 特性 {@link ViewLoaderFeature}
     */
    public static void load(InitializationUtil util, ViewLoaderFeature... features) {
        load(util, DEFAULT_RESOURCE_PATTERN, features);
    }

    /**
     * 指定基本路径加载
     *
     * @param util                当前模块初始化工具类
     * @param baseResourcePattern 基本路径
     * @param features            特性
     */
    public static void load(InitializationUtil util, String baseResourcePattern, ViewLoaderFeature... features) {
        int directive = DirectiveHelper.enable(features);
        String resourcePattern = prepareResourcePattern(baseResourcePattern, util.getModule(), directive);
        PathMatchingResourcePatternResolver finder = new PathMatchingResourcePatternResolver(AppClassLoader.getClassLoader(ViewAutoLoader.class));
        Resource[] resources;
        try {
            resources = finder.getResources(resourcePattern);
        } catch (IOException e) {
            log.error("get resources error. pattern={}", resourcePattern, e);
            return;
        }
        TreeNodeXStream xs = new TreeNodeXStream();
        for (Resource resource : resources) {
            try (InputStream is = resource.getInputStream()) {
                String xml = IOUtils.toString(is);
                TreeNode<XMLNodeContent> root = xs.fromXML(xml);
                if (!VALID_ROOT_KEYS.contains(root.getKey())) {
                    continue;
                }
                XMLNodeContent content = root.getValue();
                Map<String, String> attributes = content.getAttributes();
                View view = createViewByXml(util, attributes, resource.getFilename(), directive);
                if (view == null) {
                    continue;
                }
                if (!ViewLoader.excludeModules.contains(util.getModule())) {
                    xml = DslConverter.convert(util.getMeta(), view.getModel(), xml);
                }
                view.setTemplate(xml);
                bindViewAndViewAction(util, view, attributes);
                util.pushView(view);
            } catch (IOException e) {
                log.error("View load error.", e);
            }
        }
    }

    private static String prepareResourcePattern(String baseResourcePattern, String module, int directive) {
        StringBuilder builder = new StringBuilder(baseResourcePattern);
        if (DirectiveHelper.isEnabled(directive, ViewLoaderFeature.ONLY_LOAD_CURRENT_MODULE)) {
            builder.append(CharacterConstants.SEPARATOR_SLASH)
                    .append(module);
        }
        if (DirectiveHelper.isEnabled(directive, ViewLoaderFeature.RECURSION_FOLDER)) {
            builder.append("/**");
        }
        builder.append("/*");
        builder.append(XML_FILE_EXTENSION);
        return builder.toString();
    }

    private static View createViewByXml(InitializationUtil util, Map<String, String> attributes, String filename, int directive) {
        // 设置模型编码
        String model = attributes.get("model");
        if (StringUtils.isBlank(model)) {
            log.warn("Please set model code model, filename={}", filename);
            return null;
        }

        // 设置视图类型
        ViewTypeEnum viewType = getViewType(attributes, filename, directive);

        // 设置视图名称
        String viewName = attributes.get("viewName");
        if (StringUtils.isBlank(viewName)) {
            viewName = util.getViewNameByModel(model, viewType);
        }
        viewName = util.getViewNameByViewLoader(viewName, DirectiveHelper.isEnabled(directive, ViewLoaderFeature.USING_MODULE_SUFFIX));

        View view = util.getViewOfNullable(model, viewName);
        if (view == null) {
            view = new View();
            view.setModel(model)
                    .setName(viewName)
                    .setType(viewType);
        }

        String value;

        // 设置视图标题
        value = attributes.get("title");
        if (StringUtils.isBlank(value)) {
            value = util.getModelDefinition(view.getModel())
                    .getDisplayName();
            ViewTypeEnum type = view.getType();
            switch (type) {
                case TABLE:
                    value = value + DEFAULT_LIST_TITLE_SUFFIX;
                    break;
                case FORM:
                    value = value + DEFAULT_FORM_TITLE_SUFFIX;
                    break;
                case DETAIL:
                    value = value + DEFAULT_DETAIL_TITLE_SUFFIX;
                    break;
            }
        }
        view.setTitle(value);

        // 设置视图优先级
        value = attributes.get("priority");
        int priority;
        if (value == null) {
            priority = util.getDefaultPriority();
        } else {
            priority = Integer.parseInt(value);
        }
        view.setPriority(priority);
        return view;
    }

    private static ViewTypeEnum getViewType(Map<String, String> attributes, String filename, int directive) {
        String value;
        if (DirectiveHelper.isEnabled(directive, ViewLoaderFeature.LOAD_TYPE_BY_FILENAME)) {
            filename = filename.substring(0, filename.indexOf(XML_FILE_EXTENSION));
            String[] ss = filename.split(CharacterConstants.SEPARATOR_UNDERLINE);
            value = ss[ss.length - 1];
        } else {
            value = attributes.get("widget");
        }
        if (StringUtils.isBlank(value)) {
            return ViewTypeEnum.CUSTOM;
        }
        ViewTypeEnum viewType = null;
        value = value.toUpperCase();
        ViewTypeEnum[] viewTypes = ViewTypeEnum.values();
        for (ViewTypeEnum item : viewTypes) {
            if (item.value().equals(value)) {
                viewType = item;
                break;
            }
        }
        if (viewType == null) {
            viewType = ViewTypeEnum.CUSTOM;
        }
        return viewType;
    }

    private static void bindViewAndViewAction(InitializationUtil util, View view, Map<String, String> attributes) {
        ViewTypeEnum viewType = view.getType();
        String model = view.getModel(),
                viewName = view.getName();
        List<String> viewActionNames = new ArrayList<>();
        String value = attributes.get(VIEW_ACTION_NAMES_KEY);
        if (StringUtils.isBlank(value)) {
            switch (viewType) {
                case TABLE:
                    viewActionNames.add(InitializationUtil.DEFAULT_LIST);
                    break;
                case FORM:
                    viewActionNames.add(InitializationUtil.DEFAULT_CREATE);
                    viewActionNames.add(InitializationUtil.DEFAULT_UPDATE);
                    break;
                case DETAIL:
                    viewActionNames.add(InitializationUtil.DEFAULT_DETAIL);
                    break;
            }
        } else {
            String[] ss = value.split(CharacterConstants.SEPARATOR_COMMA);
            for (String s : ss) {
                viewActionNames.add(s.trim());
            }
        }
        for (String viewActionName : viewActionNames) {
            util.modifyViewAction(model, viewActionName, viewAction -> {
                viewActionDataBinding(viewAction, attributes);
                viewAction.setResView(view)
                        .setResViewName(viewName);
            });
        }
    }

    private static void viewActionDataBinding(ViewAction viewAction, Map<String, String> attributes) {
        String filter = attributes.get(VIEW_ACTION_DATA_PREFIX + "filter");
        if (StringUtils.isNotBlank(filter)) {
            viewAction.setFilter(filter);
        }
    }

}
