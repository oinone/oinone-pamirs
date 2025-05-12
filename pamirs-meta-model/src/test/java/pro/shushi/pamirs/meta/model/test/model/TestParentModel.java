package pro.shushi.pamirs.meta.model.test.model;

import java.util.List;

/**
 * 父模型
 *
 * 2020/10/21 10:39 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class TestParentModel {

    private List<TestParentParam> paramList;

    public List<TestParentParam> getParamList() {
        return paramList;
    }

    public void setParamList(List<TestParentParam> paramList) {
        this.paramList = paramList;
    }

}
