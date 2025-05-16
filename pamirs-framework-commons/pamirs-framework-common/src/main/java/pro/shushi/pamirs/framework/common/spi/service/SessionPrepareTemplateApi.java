package pro.shushi.pamirs.framework.common.spi.service;

import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * session构造模板方法Api
 * <p>
 * 2022/8/30 10:36 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface SessionPrepareTemplateApi {

    default void before(HttpServletRequest request, String moduleName, PamirsRequestParam requestParam) {

    }

    default void injection(HttpServletRequest request, String moduleName, PamirsRequestParam requestParam) {

    }

    default void after(HttpServletRequest request, String moduleName, PamirsRequestParam requestParam) {

    }

}
