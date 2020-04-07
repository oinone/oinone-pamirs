package pro.shushi.pamirs.meta.api.dto.protocol;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 请求结果
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/17 1:31 上午
 */
@Data
public class PamirsRequestResult implements Serializable {

    private static final long serialVersionUID = -4726508929316750723L;

    private Map<String, Object> data;

    private List<Map<String, Object>> errors;

}
