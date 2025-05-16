package pro.shushi.pamirs.user.api.service.picCode;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.user.api.constants.UserConstant;

import java.util.Optional;

/**
 * 图形验证码请求处理器
 *
 * @author shier
 * date  2022/5/31 上午11:44
 */
public abstract class PicVerificationCodeRequestResolver {

    /**
     * 应用的场景
     *
     * @return
     */
    public abstract String scene();

    /**
     * 返回图形验证码在系统中存储的key，eg: 修改密码的时候解析出来的值为账号
     *
     * @return
     */
    public abstract String handleRequest();

    /**
     * 获取图形验证码存储的key值
     *
     * @param code
     * @return
     */
    public String generateStoreKey(String code) {
        String scene = Optional.ofNullable(scene()).filter(StringUtils::isNotBlank).orElse(UserConstant.COMMON_PIC_CODE);
        String tenant = Optional.ofNullable(PamirsTenantSession.getTenant()).orElse(StringUtils.EMPTY);
        return tenant + CharacterConstants.SEPARATOR_DOLLAR + code + CharacterConstants.SEPARATOR_DOLLAR + scene;
    }

}

