package pro.shushi.pamirs.boot.web.spi.api;

import pro.shushi.pamirs.boot.base.model.*;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Adamancy Zhang
 * @date 2021-01-11 11:55
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface TranslateService {

    /**
     * 获取当前语言编码
     *
     * @return 语言编码
     */
    String getCurrentLang();

    /**
     * 获取当前语言编码
     *
     * @return 语言编码
     */
    default Boolean needTranslate()  {
        return Boolean.FALSE;
    }

    /**
     * 翻译模型元数据
     *
     * @param lang            语言编码
     * @param modelDefinition 模型
     */
    default <T extends ModelDefinition> void translateModelDefinition(String lang, T modelDefinition) {
        translateModelDefinitions(lang, Collections.singletonList(modelDefinition));
    }

    /**
     * 翻译模型元数据（使用当前语言编码）
     *
     * @param modelDefinition 模型
     */
    default <T extends ModelDefinition> void translateModelDefinition(T modelDefinition) {
        translateModelDefinitions(getCurrentLang(), Collections.singletonList(modelDefinition));
    }

    /**
     * 翻译模型元数据
     *
     * @param lang             语言编码
     * @param modelDefinitions 模型列表
     */
    default <T extends ModelDefinition> void translateModelDefinitions(String lang, List<T> modelDefinitions) {
        simpleTranslate(lang, modelDefinitions, ModelDefinition::getDisplayName, ModelDefinition::setDisplayName,
                UeModel.MODEL_MODEL, ModelDefinition.MODEL_MODEL);
    }

    /**
     * 翻译模型元数据（使用当前语言编码）
     *
     * @param modelDefinitions 模型列表
     */
    default <T extends ModelDefinition> void translateModelDefinitions(List<T> modelDefinitions) {
        translateModelDefinitions(getCurrentLang(), modelDefinitions);
    }

    /**
     * 翻译模型属性元数据
     *
     * @param lang       语言编码
     * @param modelField 模型属性
     */
    default <T extends ModelField> void translateModelField(String lang, T modelField) {
        translateModelFields(lang, Collections.singletonList(modelField));
    }

    /**
     * 翻译模型属性元数据（使用当前语言编码）
     *
     * @param modelField 模型属性
     */
    default <T extends ModelField> void translateModelField(T modelField) {
        translateModelFields(getCurrentLang(), Collections.singletonList(modelField));
    }

    /**
     * 翻译模型属性元数据
     *
     * @param lang        语言编码
     * @param modelFields 模型属性列表
     */
    default <T extends ModelField> void translateModelFields(String lang, List<T> modelFields) {
        simpleTranslate(lang, modelFields, ModelField::getDisplayName, ModelField::setDisplayName, ModelField.MODEL_MODEL);
    }

    /**
     * 翻译模型属性元数据（使用当前语言编码）
     *
     * @param modelFields 模型属性列表
     */
    default <T extends ModelField> void translateModelFields(List<T> modelFields) {
        translateModelFields(getCurrentLang(), modelFields);
    }

    /**
     * 翻译菜单元数据
     *
     * @param lang 语言编码
     * @param menu 菜单项
     */
    default <T extends Menu> void translateMenu(String lang, T menu) {
        translateMenus(lang, Collections.singletonList(menu));
    }

    /**
     * 翻译菜单元数据（使用当前语言编码）
     *
     * @param menu 菜单项
     */
    default <T extends Menu> void translateMenu(T menu) {
        translateMenus(getCurrentLang(), Collections.singletonList(menu));
    }

    /**
     * 翻译菜单元数据
     *
     * @param lang  语言编码
     * @param menus 菜单项列表
     */
    default <T extends Menu> void translateMenus(String lang, List<T> menus) {
        simpleTranslate(lang, menus, Menu::getDisplayName, Menu::setDisplayName, Menu.MODEL_MODEL);
    }

    /**
     * 翻译菜单元数据（使用当前语言编码）
     *
     * @param menus 菜单项列表
     */
    default <T extends Menu> void translateMenus(List<T> menus) {
        translateMenus(getCurrentLang(), menus);
    }

    /////////////////////////////////////////////////////////////////////////////////////
    /**
     * 翻译窗口动作元数据
     *
     * @param lang       语言编码
     * @param viewAction 窗口动作
     */
    default <T extends ViewAction> void translateViewAction(String lang, T viewAction) {
        translateViewActions(lang, Collections.singletonList(viewAction));
    }

    /**
     * 翻译窗口动作元数据（使用当前语言编码）
     *
     * @param viewAction 窗口动作
     */
    default <T extends ViewAction> void translateViewAction(T viewAction) {
        translateViewActions(getCurrentLang(), Collections.singletonList(viewAction));
    }

    /**
     * 翻译窗口动作元数据
     *
     * @param lang        语言编码
     * @param viewActions 窗口动作列表
     */
    default <T extends ViewAction> void translateViewActions(String lang, List<T> viewActions) {
        simpleTranslate(lang, viewActions, ViewAction::getDisplayName, ViewAction::setDisplayName, ViewAction.MODEL_MODEL);
    }

    /**
     * 翻译窗口动作元数据（使用当前语言编码）
     *
     * @param viewActions 窗口动作列表
     */
    default <T extends ViewAction> void translateViewActions(List<T> viewActions) {
        translateViewActions(getCurrentLang(), viewActions);
    }

    /////////////////////////////////////////////////////////////////////////////
    /**
     * 翻译服务器动作元数据
     *
     * @param lang         语言编码
     * @param serverAction 服务器动作
     */
    default <T extends ServerAction> void translateServerAction(String lang, T serverAction) {
        translateServerActions(lang, Collections.singletonList(serverAction));
    }

    /**
     * 翻译服务器动作元数据（使用当前语言编码）
     *
     * @param serverAction 服务器动作
     */
    default <T extends ServerAction> void translateServerAction(T serverAction) {
        translateServerActions(getCurrentLang(), Collections.singletonList(serverAction));
    }

    /**
     * 翻译服务器动作元数据
     *
     * @param lang          语言编码
     * @param serverActions 服务器动作列表
     */
    default <T extends ServerAction> void translateServerActions(String lang, List<T> serverActions) {
        simpleTranslate(lang, serverActions, ServerAction::getDisplayName, ServerAction::setDisplayName, ServerAction.MODEL_MODEL);
    }

    /**
     * 翻译服务器动作元数据（使用当前语言编码）
     *
     * @param serverActions 服务器动作列表
     */
    default <T extends ServerAction> void translateServerActions(List<T> serverActions) {
        translateServerActions(getCurrentLang(), serverActions);
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 翻译客户端动作元数据
     *
     * @param lang         语言编码
     * @param clientAction 客户端动作
     */
    default <T extends ClientAction> void translateClientAction(String lang, T clientAction) {
        translateClientActions(lang, Collections.singletonList(clientAction));
    }

    /**
     * 翻译客户端动作元数据（使用当前语言编码）
     *
     * @param clientAction 客户端动作
     */
    default <T extends ClientAction> void translateClientAction(T clientAction) {
        translateClientActions(getCurrentLang(), Collections.singletonList(clientAction));
    }

    /**
     * 翻译客户端动作元数据
     *
     * @param lang          语言编码
     * @param clientActions 客户端动作列表
     */
    default <T extends ClientAction> void translateClientActions(String lang, List<T> clientActions) {
        simpleTranslate(lang, clientActions, ClientAction::getDisplayName, ClientAction::setDisplayName, ClientAction.MODEL_MODEL);
    }

    /**
     * 翻译客户端动作元数据（使用当前语言编码）
     *
     * @param clientActions 客户端动作列表
     */
    default <T extends ClientAction> void translateClientActions(List<T> clientActions) {
        translateClientActions(getCurrentLang(), clientActions);
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * 翻译客户端动作元数据
     *
     * @param lang         语言编码
     * @param urlAction    URL动作
     */
    default <T extends UrlAction> void translateUrlAction(String lang, T urlAction) {
        translateUrlActions(lang, Collections.singletonList(urlAction));
    }

    /**
     * 翻译URL动作元数据（使用当前语言编码）
     *
     * @param urlAction URL动作
     */
    default <T extends UrlAction> void translateUrlAction(T urlAction) {
        translateUrlActions(getCurrentLang(), Collections.singletonList(urlAction));
    }

    /**
     * 翻译URL动作元数据
     *
     * @param lang        语言编码
     * @param urlActions  URL动作列表
     */
    default <T extends UrlAction> void translateUrlActions(String lang, List<T> urlActions) {
        simpleTranslate(lang, urlActions, UrlAction::getDisplayName, UrlAction::setDisplayName, UrlAction.MODEL_MODEL);
    }

    /**
     * 翻译URL动作元数据（使用当前语言编码）
     *
     * @param urlActions URL动作列表
     */
    default <T extends UrlAction> void translateUrlActions(List<T> urlActions) {
        translateUrlActions(getCurrentLang(), urlActions);
    }
    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 翻译视图元数据
     *
     * @param lang 语言编码
     * @param view 视图
     */
    default <T extends View> void translateView(String lang, T view) {
        translateViews(lang, Collections.singletonList(view));
    }

    /**
     * 翻译视图元数据（使用当前语言编码）
     *
     * @param view 视图
     */
    default <T extends View> void translateView(T view) {
        translateViews(getCurrentLang(), Collections.singletonList(view));
    }

    /////////////////////////////////////////////////////////////////////////////////////
    /**
     * 翻译枚举字典元数据
     *
     * @param lang 语言编码
     * @param dictionary 枚举字典
     */
    default <T extends DataDictionary> void translateDictionary(String lang, T dictionary) {
        translateDictionaries(lang, Collections.singletonList(dictionary));
    }

    /**
     * 翻译枚举字典元数据（使用当前语言编码）
     *
     * @param dictionary 枚举字典
     */
    default <T extends DataDictionary> T translateDictionary(T dictionary) {
        List<T> list = Collections.singletonList(dictionary);
        translateDictionaries(getCurrentLang(), list);
       return list.get(0);
    }

    /**
     * 翻译枚举字典元数据
     *
     * @param lang  语言编码
     * @param dictionaries 枚举字典列表
     */
    default <T extends DataDictionary> List<T> translateDictionaries(String lang, List<T> dictionaries) {
        simpleTranslate(lang, dictionaries, DataDictionary::getDisplayName, DataDictionary::setDisplayName, DataDictionary.MODEL_MODEL);
        return dictionaries;
    }

    /**
     * 翻译枚举字典元数据（使用当前语言编码）
     *
     * @param dictionaries 枚举字典列表
     */
    default <T extends DataDictionary> void translateDictionaries(List<T> dictionaries) {
        translateDictionaries(getCurrentLang(), dictionaries);
    }

    /////////////////////////////////////////////////////////////////////////////////////

    /**
     * 翻译视图元数据
     *
     * @param lang  语言编码
     * @param views 视图列表
     */
    default <T extends View> void translateViews(String lang, List<T> views) {
        simpleTranslate(lang, views, View::getTitle, View::setTitle,
                View.MODEL_MODEL);
    }

    /**
     * 翻译视图元数据（使用当前语言编码）
     *
     * @param views 视图列表
     */
    default <T extends View> void translateViews(List<T> views) {
        translateViews(getCurrentLang(), views);
    }

    /**
     * 翻译模块定义元数据
     *
     * @param lang             语言编码
     * @param moduleDefinition 模块定义
     */
    default <T extends ModuleDefinition> void translateModuleDefinition(String lang, T moduleDefinition) {
        translateModuleDefinitions(lang, Collections.singletonList(moduleDefinition));
    }

    /**
     * 翻译模块定义元数据（使用当前语言编码）
     *
     * @param moduleDefinition 模块定义
     */
    default <T extends ModuleDefinition> void translateModuleDefinition(T moduleDefinition) {
        translateModuleDefinitions(getCurrentLang(), Collections.singletonList(moduleDefinition));
    }

    /**
     * 翻译模块定义元数据
     *
     * @param lang              语言编码
     * @param moduleDefinitions 模块定义列表
     */
    default <T extends ModuleDefinition> void translateModuleDefinitions(String lang, List<T> moduleDefinitions) {
        simpleTranslate(lang, moduleDefinitions, ModuleDefinition::getDisplayName, ModuleDefinition::setDisplayName,
                UeModule.MODEL_MODEL, ModuleDefinition.MODEL_MODEL);
    }

    /**
     * 翻译模块定义元数据（使用当前语言编码）
     *
     * @param moduleDefinitions 模块定义列表
     */
    default <T extends ModuleDefinition> void translateModuleDefinitions(List<T> moduleDefinitions) {
        translateModuleDefinitions(getCurrentLang(), moduleDefinitions);
    }

    /**
     * 简单批量翻译
     *
     * @param lang   语言编码
     * @param list   翻译源列表
     * @param getter getter方法
     * @param setter setter方法
     * @param models 模型编码
     * @param <T>    任意翻译源类型
     */
    <T> void simpleTranslate(String lang, List<T> list, Function<T, String> getter, BiConsumer<T, String> setter, String... models);

    /**
     * 通用模型数据翻译
     *
     * @param lang  语言编码
     * @param list  翻译源列表
     * @param model 模型
     */
    void generalDataTranslate(String lang, List<?> list, String model);


}
