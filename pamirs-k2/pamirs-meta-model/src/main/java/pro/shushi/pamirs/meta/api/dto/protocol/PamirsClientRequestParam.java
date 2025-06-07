package pro.shushi.pamirs.meta.api.dto.protocol;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 前端请求参数
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/17 1:31 上午
 */
@Data
public class PamirsClientRequestParam implements Serializable {

    private static final long serialVersionUID = 2598923299951615110L;

    private String query;

    private Map<String, Object> variables;

}
