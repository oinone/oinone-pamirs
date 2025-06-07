package pro.shushi.pamirs.core.common.http;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 忽略SSL证书验证
 *
 * @author Adamancy Zhang at 14:36 on 2021-06-10
 */
public class IgnoredSSLVerifier {

    public static final HostnameVerifier x509HostnameVerifier = new X509HostnameVerifier();

    private IgnoredSSLVerifier() {
        //reject create object
    }

    private static final X509TrustManager TRUST_MANAGER = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };

    private static class X509HostnameVerifier implements HostnameVerifier {

        private X509HostnameVerifier() {
            //reject create object
        }

        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }

    public static SSLContext createSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, new TrustManager[]{TRUST_MANAGER}, null);
        return sslContext;
    }
}
