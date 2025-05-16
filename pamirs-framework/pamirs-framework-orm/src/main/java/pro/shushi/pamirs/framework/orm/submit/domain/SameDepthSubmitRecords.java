package pro.shushi.pamirs.framework.orm.submit.domain;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 同层待处理记录列表
 * <p>
 * 2022/5/11 5:16 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class SameDepthSubmitRecords {

    private String path;

    private Map<String/* model */, SubmitRecord<?>> records;

    public SameDepthSubmitRecords(String path) {
        this.setPath(path);
        this.setRecords(new HashMap<>());
    }

}
