package pro.shushi.pamirs.meta.api.core.session;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;

import java.util.List;
import java.util.Set;

/**
 * session中元数据构造器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:41 下午
 */
public interface SessionMetaConstructor extends CommonApi {

    /**
     * 构造
     *
     * @return
     */
    List<Meta> construct();

    /**
     * 构造
     *
     * @return
     */
    List<Meta> construct(Set<String> modules);

}
