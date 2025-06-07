package pro.shushi.pamirs.boot.test.action;

import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;


/**
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/17 5:02 下午
 */
@Model.model("test.TestModel")
public class TestAction {

    //@ExtPoint.Using({"testExtPoint"})
//    @ExtPoint
    @Action
    public Integer test() {
        return null;
    }

    @Function
    public Integer testFunction() {
        return null;
    }

}
