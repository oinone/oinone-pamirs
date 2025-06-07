package pro.shushi.pamirs.user.api.service;

/**
 * 用户操作记录
 *
 * @author shier
 * date  2022/6/1 下午4:51
 */
public interface UserOperationRecordService {

    /**
     * 记录登录错误次数
     *
     * @param login
     * @return
     */
    Integer recordLoginErrorCount(String login);

    /**
     * 获取登录错误次数
     *
     * @param login
     * @return
     */
    Integer getLoginErrorCount(String login);
}
