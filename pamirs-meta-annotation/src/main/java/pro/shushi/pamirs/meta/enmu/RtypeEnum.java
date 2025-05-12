package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;

/**
 * 字段关系类型枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.Rtype", displayName = "关系类型")
public class RtypeEnum extends BaseEnum<RtypeEnum, String> {

    private static final long serialVersionUID = 4803907083565085485L;

    // 关系类型
    public final static RtypeEnum O2O = create("O2O", "o2o", "一对一", "一对一");
    public final static RtypeEnum O2M = create("O2M", "o2m", "一对多", "一对多");
    public final static RtypeEnum M2O = create("M2O", "m2o", "多对一", "多对一");
    public final static RtypeEnum M2M = create("M2M", "m2m", "多对多", "多对多");

    public static boolean isRelationOne(String ttype) {
        return BaseEnum.isIn(ttype, caseValue(), M2O, O2O);
    }

    public static boolean isRelationOne(RtypeEnum ttype) {
        return BaseEnum.isIn(ttype, caseEnum(), M2O, O2O);
    }

    public static boolean isRelationMany(String ttype) {
        return BaseEnum.isIn(ttype, caseValue(), O2M, M2M);
    }

    public static boolean isRelationMany(RtypeEnum ttype) {
        return BaseEnum.isIn(ttype, caseEnum(), O2M, M2M);
    }

    public static boolean isRelationType(String ttype) {
        return BaseEnum.isIn(ttype, caseValue(), O2M, M2O, M2M, O2O);
    }

    public static boolean isRelationType(RtypeEnum ttype) {
        return BaseEnum.isIn(ttype, caseEnum(), O2M, M2O, M2M, O2O);
    }

    public static boolean isRelationOneCaseName(String ttype) {
        return BaseEnum.isIn(ttype, caseName(), M2O, O2O);
    }

    public static boolean isRelationManyCaseName(String ttype) {
        return BaseEnum.isIn(ttype, caseName(), O2M, M2M);
    }

    public static boolean isRelationTypeCaseName(String ttype) {
        return BaseEnum.isIn(ttype, caseName(), O2M, M2O, M2M, O2O);
    }

}