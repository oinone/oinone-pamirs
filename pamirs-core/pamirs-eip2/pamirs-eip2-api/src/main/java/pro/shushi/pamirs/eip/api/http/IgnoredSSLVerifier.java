package pro.shushi.pamirs.eip.api.http;

import org.apache.camel.CamelContext;
import org.apache.camel.support.jsse.SSLContextParameters;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * 忽略SSL证书验证
 *
 * @author Adamancy Zhang at 14:36 on 2021-06-10
 */
public class IgnoredSSLVerifier {

    public static final String HOSTNAME_VERIFIER_KEY = "x509HostnameVerifier";

    public static final String x509HostnameVerifierId = "ignoredX509HostnameVerifier";

    public static final HostnameVerifier x509HostnameVerifier = pro.shushi.pamirs.core.common.http.IgnoredSSLVerifier.x509HostnameVerifier;

    public static final String SSL_CONTEXT_PARAMETERS_KEY = "sslContextParameters";

    public static final String ignoredTLSv1VerifierId = "ignoredTLSv1Verifier";

    public static final SSLContextParameters ignoredTLSv1Verifier = new IgnoredTLSv1Verifier();

    public static final String URL_PARAMETER = HOSTNAME_VERIFIER_KEY + "=#" + x509HostnameVerifierId + "&" + SSL_CONTEXT_PARAMETERS_KEY + "=#" + ignoredTLSv1VerifierId;

    private IgnoredSSLVerifier() {
        //reject create object
    }

    private static class IgnoredTLSv1Verifier extends SSLContextParameters {

        private IgnoredTLSv1Verifier() {
            //reject create object
        }

        @Override
        public SSLContext createSSLContext(CamelContext camelContext) throws GeneralSecurityException, IOException {
            return pro.shushi.pamirs.core.common.http.IgnoredSSLVerifier.createSSLContext();
        }
    }
}
