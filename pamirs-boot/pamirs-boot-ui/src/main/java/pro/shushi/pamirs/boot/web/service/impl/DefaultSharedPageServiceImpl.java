package pro.shushi.pamirs.boot.web.service.impl;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.SharedPage;
import pro.shushi.pamirs.boot.base.model.SharedPageViewAction;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.base.ux.cache.api.ActionCacheApi;
import pro.shushi.pamirs.boot.web.enmu.SharedExpEnumerate;
import pro.shushi.pamirs.boot.web.service.SharedPageService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import jakarta.annotation.Nullable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * 默认分享页面服务
 *
 * @author Adamancy Zhang at 22:08 on 2024-04-11
 */
@Order
@Service
@Fun(SharedPageService.FUN_NAMESPACE)
public class DefaultSharedPageServiceImpl implements SharedPageService {

    private static final String HEADER_ORIGIN_KEY = "origin";

    private static final String SHARED_MODEL_KEY = "model";

    private static final String SHARED_ACTION_NAME_KEY = "action";

    private static final String SHARED_ORIGIN_ROUTE = "/shared";

    private static final String SEPARATOR_EQUAL = "=";

    private static final String SHARED_URL_PARAMETER_CODE = "code";

    @Function
    @Override
    public SharedPage init(SharedPage page) {
        return shared(page);
    }

    @Function
    @Override
    public SharedPage shared(SharedPage page) {
        String sharedOrigin = getSharedOrigin(page);
        ViewAction shareAction = getShareAction(page);
        Map<String, Object> parameters = getParameters(page);
        ViewAction sharedViewAction = getSharedViewAction(page);
        return shared(page, sharedOrigin, shareAction, sharedViewAction, parameters);
    }

    @Function
    @Override
    public SharedPageViewAction load(SharedPageViewAction page) {
        String sharedCode = page.getSharedCode();
        if (StringUtils.isBlank(sharedCode)) {
            throw PamirsException.construct(SharedExpEnumerate.INVALID_SHARED_CODE).errThrow();
        }
        return load(sharedCode);
    }

    /**
     * 生成分享信息
     *
     * @param page             指定动作页面信息
     * @param sharedOrigin     分享链接源地址
     * @param shareAction      「分享」动作
     * @param sharedViewAction 被分享动作
     * @param parameters       页面参数
     * @return 分享信息
     */
    protected SharedPage shared(SharedPage page, String sharedOrigin, ViewAction shareAction, ViewAction sharedViewAction, Map<String, Object> parameters) {
        Map<String, Object> sharedParameters = generatorSharedParameters(page, shareAction, sharedViewAction, parameters);
        page.setUrl(generatorUrl(sharedOrigin, sharedParameters));
        page.setLinkText(generatorLinkText(sharedOrigin, sharedParameters));
        return page;
    }

    /**
     * 生成分享页面参数
     *
     * @param page             指定动作页面信息
     * @param shareAction      「分享」动作
     * @param sharedViewAction 被分享动作
     * @param parameters       页面参数
     * @return 分享页面参数
     */
    protected Map<String, Object> generatorSharedParameters(SharedPage page, ViewAction shareAction, ViewAction sharedViewAction, Map<String, Object> parameters) {
        String sharedCode = page.getSharedCode();
        if (StringUtils.isBlank(sharedCode)) {
            sharedCode = generatorSharedCode();
            page.setSharedCode(sharedCode);
        }
        Map<String, Object> sharedParameters = new HashMap<>(1);
        sharedParameters.put(SHARED_URL_PARAMETER_CODE, sharedCode);
        return sharedParameters;
    }

    protected String generatorSharedCode() {
        throw new UnsupportedOperationException("Invalid shared parameters.");
    }

    /**
     * 生成链接文本
     *
     * @param sharedOrigin     分享链接源地址
     * @param sharedParameters 分享页面参数
     * @return 链接文本
     */
    protected String generatorLinkText(String sharedOrigin, Map<String, Object> sharedParameters) {
        return generatorUrl(sharedOrigin, sharedParameters);
    }

    /**
     * 加载分享页面
     *
     * @param sharedCode 分享码
     * @return 分享页面元数据
     */
    protected SharedPageViewAction load(String sharedCode) {
        throw new UnsupportedOperationException("Invalid shared parameters.");
    }

    /**
     * 获取链接源地址
     *
     * @param page 指定动作页面信息
     * @return 源地址
     */
    protected String getOrigin(SharedPage page) {
        String origin = Optional.ofNullable(PamirsSession.getRequestVariables())
                .map(v -> v.getHeader(HEADER_ORIGIN_KEY))
                .filter(StringUtils::isNotBlank)
                .orElse(page.getOrigin());
        if (StringUtils.isBlank(origin)) {
            throw PamirsException.construct(SharedExpEnumerate.INVALID_SHARED_ORIGIN).errThrow();
        }
        return origin;
    }

    /**
     * 获取分享链接源地址
     *
     * @param page 指定动作页面信息
     * @return 分享链接源地址
     */
    protected String getSharedOrigin(SharedPage page) {
        return getOrigin(page) + SHARED_ORIGIN_ROUTE;
    }

    /**
     * 获取页面参数
     *
     * @param page 指定动作页面信息
     * @return 页面参数
     */
    protected Map<String, Object> getParameters(SharedPage page) {
        Map<String, Object> parameters = page.parseParameters();
        if (MapUtils.isEmpty(parameters)) {
            throw PamirsException.construct(SharedExpEnumerate.INVALID_SHARED_PARAMETERS).errThrow();
        }
        return parameters;
    }

    /**
     * 获取字符串类型参数值
     *
     * @param parameters 页面参数
     * @param key        指定key
     * @return 页面参数值
     */
    protected String getStringParameters(Map<String, Object> parameters, String key) {
        return Optional.ofNullable(parameters.get(key)).map(String::valueOf).filter(StringUtils::isNotBlank).orElse(null);
    }

    /**
     * 获取当前「分享」动作
     *
     * @param page 指定动作页面信息
     * @return 「分享」动作
     */
    @Nullable
    protected ViewAction getShareAction(SharedPage page) {
        String shareActionModel = page.getShareActionModel();
        String shareActionName = page.getShareActionName();
        if (StringUtils.isAnyBlank(shareActionModel, shareActionName)) {
            return null;
        }
        Action cacheAction = PamirsSession.getContext().getExtendCache(ActionCacheApi.class).get(shareActionModel, shareActionName);
        if (!(cacheAction instanceof ViewAction)) {
            return null;
        }
        return (ViewAction) cacheAction;
    }

    /**
     * 获取被分享动作（页面参数中指定的动作）
     *
     * @param page 指定动作页面信息
     * @return 被分享动作
     */
    protected ViewAction getSharedViewAction(SharedPage page) {
        Map<String, Object> parameters = getParameters(page);
        String model = getStringParameters(parameters, SHARED_MODEL_KEY);
        String action = getStringParameters(parameters, SHARED_ACTION_NAME_KEY);
        if (StringUtils.isAnyBlank(model, action)) {
            throw PamirsException.construct(SharedExpEnumerate.INVALID_SHARED_ACTION_PARAMETERS).errThrow();
        }
        Action cacheAction = PamirsSession.getContext().getExtendCache(ActionCacheApi.class).get(model, action);
        if (!(cacheAction instanceof ViewAction)) {
            throw PamirsException.construct(SharedExpEnumerate.INVALID_SHARED_ACTION).errThrow();
        }
        ViewAction viewAction = (ViewAction) cacheAction;
        if (viewAction.getId() == null) {
            viewAction = Models.origin().queryOneByWrapper(Pops.<ViewAction>lambdaQuery()
                    .from(ViewAction.MODEL_MODEL)
                    .eq(ViewAction::getModel, model)
                    .eq(ViewAction::getName, action));
            if (viewAction == null) {
                throw PamirsException.construct(SharedExpEnumerate.INVALID_SHARED_ACTION).errThrow();
            }
        }
        return viewAction;
    }

    /**
     * 生成分享链接
     *
     * @param sharedOrigin     分享链接源地址
     * @param sharedParameters 分享页面参数
     * @return 分享链接
     */
    protected String generatorUrl(String sharedOrigin, Map<String, Object> sharedParameters) {
        StringBuilder builder = new StringBuilder(sharedOrigin);
        builder.append(CharacterConstants.SEPARATOR_SEMICOLON);
        return generatorUrlParameters(builder, sharedParameters);
    }

    /**
     * 生成URL参数字符串
     *
     * @param builder    前缀Builder
     * @param parameters 页面参数
     * @return URL参数字符串
     */
    protected String generatorUrlParameters(StringBuilder builder, Map<String, Object> parameters) {
        if (builder == null) {
            builder = new StringBuilder();
        }
        Iterator<Map.Entry<String, Object>> iterator = parameters.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            String value = urlParameterValueConvert(key, entry.getValue());
            builder.append(key).append(SEPARATOR_EQUAL).append(value);
            if (iterator.hasNext()) {
                builder.append(CharacterConstants.SEPARATOR_SEMICOLON);
            }
        }
        return builder.toString();
    }

    /**
     * URL参数值转换
     *
     * @param key   参数key
     * @param value 参数值
     * @return 参数值字符串
     */
    protected String urlParameterValueConvert(String key, Object value) {
        if (value instanceof String) {
            return (String) value;
        }
        return JSON.toJSONString(value);
    }
}
