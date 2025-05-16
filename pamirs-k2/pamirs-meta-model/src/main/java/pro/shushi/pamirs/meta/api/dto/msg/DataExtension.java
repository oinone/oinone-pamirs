package pro.shushi.pamirs.meta.api.dto.msg;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.dto.common.Message;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 详细信息
 * <p>
 * 2021/3/13 10:37 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class DataExtension implements Serializable {

    private static final long serialVersionUID = -749539917741234751L;

    /**
     * 信息列表
     */
    private List<Message> messages;

    /**
     * 扩展内容
     */
    private Map<Object, Object> extensions;

    /**
     * 指令集
     */
    private Set<String> directives;

}
