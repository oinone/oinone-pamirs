package pro.shushi.pamirs.meta.api.dto.meta;

import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * 主模型命名空间
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 4:16 下午
 */
@Data
public class MetaNames {

    /**
     * 模块编码
     */
    private String module;

    /**
     * 模型编码
     */
    private String model;

    public void clearModel(){
        this.model = null;
    }

}
