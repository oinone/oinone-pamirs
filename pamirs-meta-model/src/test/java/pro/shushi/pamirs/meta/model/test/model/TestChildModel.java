package pro.shushi.pamirs.meta.model.test.model;

import java.util.List;

/**
 * 2020/10/21 10:42 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class TestChildModel extends TestParentModel {

    private List<TestChildParam> paramList;

    public List<TestChildParam> getParamListForTestChildModel() {
        return paramList;
    }

    public void setParamListForTestChildModel(List<TestChildParam> paramList) {
        this.paramList = paramList;
    }

}
