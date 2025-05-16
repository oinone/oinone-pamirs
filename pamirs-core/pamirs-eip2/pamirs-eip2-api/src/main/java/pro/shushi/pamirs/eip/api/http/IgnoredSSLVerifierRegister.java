package pro.shushi.pamirs.eip.api.http;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.CollectionHelper;
import pro.shushi.pamirs.eip.api.camel.IEipRegister;
import pro.shushi.pamirs.eip.api.camel.RegistryComponentBody;

import java.util.List;

/**
 * 忽略SSL证书验证注册
 *
 * @author Adamancy Zhang at 20:12 on 2021-07-27
 */
@Component
public class IgnoredSSLVerifierRegister implements IEipRegister {

    @Override
    public List<RegistryComponentBody> registers() {
        return CollectionHelper.<RegistryComponentBody>newInstance()
                .add(new RegistryComponentBody(IgnoredSSLVerifier.x509HostnameVerifierId, IgnoredSSLVerifier.x509HostnameVerifier))
                .add(new RegistryComponentBody(IgnoredSSLVerifier.ignoredTLSv1VerifierId, IgnoredSSLVerifier.ignoredTLSv1Verifier))
                .build();
    }
}
