package pro.shushi.pamirs.meta.model.test.model;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

import java.util.List;
import java.util.Map;

/**
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/12 10:45 上午
 */
@Data
public class TestTypeModel {

    private Integer integer;

    private int integerI;

    private Map<String, Object> mapO;

    private Map map;

    private ModelDefinition modelDefinition;

    private List<Map> mapList;

    private List<Map<String, Object>> mapOList;

    private List<ModelDefinition> models;

    public class TestInner {

        private String test;

    }

}
