package pro.shushi.pamirs.meta.api.dto.protocol;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.io.Serializable;

/**
 * 请求参数
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/17 1:31 上午
 */
@Data
public class PamirsRequestParam implements Serializable {

    private static final long serialVersionUID = 341278211891814930L;

    private String query;

    private PamirsRequestVariables variables;

    private PamirsRequestResult result;
}
