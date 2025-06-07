package pro.shushi.pamirs.business.core.init;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.SystemBootAfterInit;
import pro.shushi.pamirs.business.api.BusinessModule;
import pro.shushi.pamirs.business.api.entity.PamirsCompany;
import pro.shushi.pamirs.business.api.entity.PamirsPerson;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.business.api.model.PamirsOrganization;
import pro.shushi.pamirs.business.api.model.PamirsPosition;
import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.core.common.function.FunctionConstant;

/**
 * {@link BusinessModule}事务初始化
 *
 * @author Adamancy Zhang at 14:28 on 2021-08-31
 */
@Component
public class BusinessTxInit implements SystemBootAfterInit {

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public boolean init(AppLifecycleCommand command) {
        String[] namespaces = new String[]{
                PamirsCompany.MODEL_MODEL,
                PamirsPerson.MODEL_MODEL,

                PamirsOrganization.MODEL_MODEL,
                PamirsDepartment.MODEL_MODEL,
                PamirsPosition.MODEL_MODEL,
                PamirsEmployee.MODEL_MODEL
        };
        String[] funs = new String[]{
                FunctionConstant.create,
                FunctionConstant.update,
                FunctionConstant.delete,
                FunctionConstant.deleteOne
        };
        for (String namespace : namespaces) {
            InitializationUtil.addTxConfig(namespace, funs);
        }
        return true;
    }
}
