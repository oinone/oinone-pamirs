package pro.shushi.pamirs.core.common.loader;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.core.common.dsl.model.UdView;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ViewLoader {

    private static final String MATCH_VIEW_PATH = "pamirs/init/views";

    private static final String MENU_INFO_FILENAME = "menu.info";

    public static final List<String> excludeModules = ImmutableList.of(
            "apps",
            "model_designer",
            "logic_designer",
            "workflow_designer",
            "ui_designer",
            "data_designer",
            "ai_designer",
            "microflow_designer"
    );

    public static void load(String moduleModule, UdView view, InitializationUtil util, URI uri) {
        load(moduleModule, view, util, uri, false);
    }

    public static void load(String moduleModule, UdView view, InitializationUtil util, URI uri, boolean useModuleSuffix) {
        boolean v2 = true;
        if (excludeModules.contains(moduleModule)) {
            v2 = false;
        }
        String uriString = uri.toString();
        int basePathIndex = uriString.indexOf(MATCH_VIEW_PATH);
        if (basePathIndex == -1) {
            log.warn("Ignore file path prefix that cannot be loaded: {}", uriString);
            return;
        }
        String xmlPath = uriString.substring(basePathIndex);
        basePathIndex = uriString.indexOf(MATCH_VIEW_PATH);
        int beginIndex = basePathIndex + (MATCH_VIEW_PATH + CharacterConstants.SEPARATOR_SLASH + moduleModule + CharacterConstants.SEPARATOR_SLASH).length();
        int endIndex = uriString.lastIndexOf(CharacterConstants.SEPARATOR_SLASH);
        String[] menus;
        if (endIndex == -1 || endIndex <= beginIndex) {
            menus = new String[0];
        } else {
            menus = uriString.substring(beginIndex, endIndex).split(CharacterConstants.SEPARATOR_SLASH);
        }
        String title = StringUtils.isBlank(view.getTitle()) ? view.getViewName() : view.getTitle();

        view.setViewName(util.getViewNameByViewLoader(view.getViewName(), useModuleSuffix));

        ViewTypeEnum viewTypeEnum = ViewTypeEnum.valueOf(xmlPath.substring(xmlPath.lastIndexOf(CharacterConstants.SEPARATOR_UNDERLINE) + 1, xmlPath.lastIndexOf(CharacterConstants.SEPARATOR_DOT)).toUpperCase());
        //保持view
        if (view.getPriority() == null) {
            //不传会用默认值,但是传null那就是null
            util.createView(view.getModel(), title, view.getViewName(), InitializationUtil.filePrefix + xmlPath, viewTypeEnum, v2);
        } else {
            util.createView(view.getModel(), title, view.getViewName(), InitializationUtil.filePrefix + xmlPath, viewTypeEnum, view.getPriority(), v2);
        }

        if (view.getWidget().toUpperCase().equals(ViewTypeEnum.TABLE.value()) && !(Boolean.FALSE.toString().equals(view.getMenu())) || StringUtils.isNotBlank(view.getMenu()) && !(Boolean.FALSE.toString().equals(view.getMenu()))) {
            //保存menu
            int i = 0;
            String parentMenuNameStr = null;
            String menuNameStr = null;
            String menuName = null;

            String icon = null;
            for (String menu : menus) {
                long priority = 0L;
                String[] tempMenu = menu.split("_");
                if (tempMenu.length == 2) {
                    priority = Long.parseLong(tempMenu[0]) * 100L;
                    menuNameStr = tempMenu[1];
                } else {
                    menuNameStr = menu;
                }
                menuName = menuNameStr;
                String menuInfoUri = uri.toString().substring(uri.toString().indexOf(MATCH_VIEW_PATH), uri.toString().indexOf(menu) + menu.length()) + CharacterConstants.SEPARATOR_SLASH + MENU_INFO_FILENAME;
                try (InputStream inputStream = ViewLoader.class.getClassLoader().getResourceAsStream(menuInfoUri)) {
                    if (inputStream != null) {
                        String content = IOUtils.toString(inputStream);
                        if (StringUtils.isNotBlank(content)) {
                            String[] menuInfos = content.trim().split(System.lineSeparator());
                            if (ArrayUtils.isNotEmpty(menuInfos)) {
                                for (String menuInfo : menuInfos) {
                                    if (StringUtils.isNotBlank(menuInfo)) {
                                        String[] menuKV = menuInfo.trim().split("=");
                                        if (menuKV != null && menuKV.length == 2) {
                                            String key = menuKV[0].trim();
                                            String value = menuKV[1].trim();
                                            if (StringUtils.isNotBlank(value)) {
                                                if ("name".equals(key)) {
                                                    menuName = value;
                                                } else if ("priority".equals(key)) {
                                                    priority = Long.parseLong(value);
                                                } else if ("icon".equals(key)) {
                                                    icon = value;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }
                } catch (IOException e) {
                    log.error(CharacterConstants.LOG_PLACEHOLDER, menuInfoUri, e);
                }

                String parentMenuName = null;
                if (i != 0) {
                    parentMenuName = parentMenuNameStr;
                }
                i++;
                if (menus.length == i) {
                    if (StringUtils.isNotBlank(view.getPagePath())) {
                        pro.shushi.pamirs.boot.base.model.View searchView = null;
                        if ("true".equals(view.getSearch())) {
                            String search = InitializationUtil.filePrefix + xmlPath.substring(0, xmlPath.lastIndexOf("_")) + "_search.xml";
                            searchView = util.createView(view.getModel(), title, view.getViewName() + "search", search, ViewTypeEnum.SEARCH);
                        }
                        List<pro.shushi.pamirs.boot.base.model.View> searchViewList = new ArrayList<>();
                        if (searchView != null) {
                            searchViewList.add(searchView);
                        }
                        util.createViewActionMenu(menuName, menuNameStr, priority, parentMenuName, view.getModel(), view.getViewName(),
                                // TODO @shier page已被删除
                                v -> v.setOptionViewList(searchViewList)
                                        .setFilter((String) view.get_d().get("filter"))
                                        .setViewType(viewTypeEnum)
                        ).setIcon(icon);
                    } else {
                        util.createViewActionMenu(menuName, menuNameStr, priority, parentMenuName, view.getModel(), view.getViewName(),
                                v -> v.setViewType(viewTypeEnum).setFilter((String) view.get_d().get("filter"))).setIcon(icon);
                    }
                } else {
                    util.createViewActionMenu(menuName, menuNameStr, priority, parentMenuName).setIcon(icon);
                }
                parentMenuNameStr = menuName;

                icon = null;
            }
        }
    }

    /**
     * 为viewAction的目标页面指定一个布局
     *
     * @param util
     * @param originModelModel 跳转前的页面所在的模型
     * @param targetModelModel 跳转目标页面的模型
     * @param pageName         布局页面的名称
     * @param pageFileName     布局页面模板文件名
     * @param viewActionName   跳转的viewAction
     */
    public static void addPage2customViewAction(InitializationUtil util, String originModelModel, String targetModelModel, String pageName, String pageFileName, String viewActionName) {
        try {
            util.modifyViewAction(originModelModel, viewActionName, viewAction -> {
                // TODO @shier page已被删除
//                viewAction.setPage(util.createPage(pageName, targetModelModel, "file:pamirs/init/views/" + util.getUeModule().getModule() + "/page/" + pageFileName));
            });
        } catch (Exception e) {
            log.error("Specified model does not exist, originModelModel:{} viewActionName:{}, err", originModelModel, viewActionName, e);
        }
    }
}
