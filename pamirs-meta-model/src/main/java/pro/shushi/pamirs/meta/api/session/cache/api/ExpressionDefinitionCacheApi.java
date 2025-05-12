package pro.shushi.pamirs.meta.api.session.cache.api;

import pro.shushi.pamirs.meta.api.session.cache.SessionCacheInitApi;
import pro.shushi.pamirs.meta.api.session.cache.SessionCacheWithTripleKey;
import pro.shushi.pamirs.meta.domain.fun.ExpressionDefinition;

import java.util.List;

/**
 * 表达式定义缓存接口
 *
 * <p>key为type#model#sign
 * 2021/8/19 2:40 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface ExpressionDefinitionCacheApi extends SessionCacheWithTripleKey<List<ExpressionDefinition>>, SessionCacheInitApi {

}
