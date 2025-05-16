package pro.shushi.pamirs.meta.api.enmu;

/**
 * 批量提交类型枚举
 * 2020/12/16 9:28 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public enum BatchCommitTypeEnum {
    /**
     * 循环单次单条脚本提交，返回实际影响行数
     */
    useAffectRows,

    /**
     * 循环单次单条脚本提交，返回实际影响行数，若实际影响行数与输入不一致，抛出异常
     */
    useAndJudgeAffectRows,

    /**
     * 将多个单条更新脚本拼接成一个脚本提交，不能返回实际影响行数
     */
    collectionCommit,

    /**
     * 使用单条更新脚本批量提交，不能返回实际影响行数
     */
    batchCommit
}
