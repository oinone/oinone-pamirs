package pro.shushi.pamirs.user.core.base.init;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.enmu.SequenceEnum;
import pro.shushi.pamirs.user.api.UserModule;

import java.util.Map;

import static pro.shushi.pamirs.user.core.base.service.UserLoginSequenceBase.BC_USER_LOGIN_SEQ;


/**
 * UserMetaDataInit
 *
 * @author yakir on 2022/09/26 14:12.
 */
@Component
public class UserMetaDataInit implements MetaDataEditor {

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {

        InitializationUtil util = InitializationUtil.get(metaMap, UserModule.MODULE_MODULE, UserModule.MODULE_NAME);
        if (util == null) {
            return;
        }

        util.createSequenceConfig("用户Login序列", BC_USER_LOGIN_SEQ, SequenceEnum.ORDERLY_SEQ, 10)
                .setPrefix("u")
                .setStep(10)
                .setInitial(8000000L)
                .setIsRandomStep(true)
        ;
    }
}
