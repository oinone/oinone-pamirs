package pro.shushi.pamirs.meta.model.test.utils;

import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.common.enmu.BitEnum;

/**
 * @author shier
 * date  2020/12/29 8:57 下午
 */
public class PersonEnum extends BaseEnum<PersonEnum,Long> implements BitEnum {

    private static final long serialVersionUID = -886103079302155131L;
   
    // 关系类型
    public final static PersonEnum O2O = create("O2O", 2<<0, "一对一", "一对一");
    public final static PersonEnum O2M = create("O2M", 2<<1, "一对多", "一对多");
    public final static PersonEnum M2O = create("M2O", 2<<2, "多对一", "多对一");
    public final static PersonEnum M2M = create("M2M", 2<<3, "多对多", "多对多");

}
