package pro.shushi.pamirs.eip.api.installer;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.sys.Base;

/**
 * IEipInstaller
 *
 * @author yakir on 2024/06/25 16:12.
 */
@Base
@Fun(IEipInstaller.FUN_NAMESPACE)
public interface IEipInstaller {

    String FUN_NAMESPACE = "base.IEipInstaller";

    @Function
    @Function.Advanced(timeout = 10000)
    Boolean install(String data);
}
