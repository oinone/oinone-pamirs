package pro.shushi.pamirs.framework.connectors.data.elastic.rest.http;

import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;

/**
 * XProductInterceptor
 *
 * @author yakir on 2022/08/31 15:24.
 */
public class XProductInterceptor implements HttpResponseInterceptor {

    @Override
    public void process(HttpResponse response, HttpContext context) {
        if (null != response) {
            response.addHeader("X-Elastic-Product", "Elasticsearch");
        }
    }
}
