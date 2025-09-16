package pro.shushi.pamirs.file.api.util;

import org.springframework.util.ResourceUtils;
import pro.shushi.pamirs.core.common.constant.CommonConstants;
import pro.shushi.pamirs.framework.connectors.cdn.client.FileClient;
import pro.shushi.pamirs.framework.connectors.cdn.client.LocalFileClient;
import pro.shushi.pamirs.framework.connectors.cdn.constant.FileConstants;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

@Slf4j
public class FileUtil {

    public static BufferedInputStream getRemoteBufferedInputStream(String fileUrl) throws IOException {
        if (fileUrl.startsWith(FileConstants.LOCAL_PREFIX)) {
            FileClient client = FileClientFactory.getClient();
            if (client instanceof LocalFileClient) {
                return new BufferedInputStream(client.getDownloadStream(fileUrl));
            }
        }
        URL url;
        if (fileUrl.startsWith(CommonConstants.CLASSPATH_PROTOCOL)) {
            url = ResourceUtils.getURL(fileUrl);
        } else {
            url = new URL(fileUrl);
        }
        URLConnection connection = url.openConnection();
        if (connection instanceof HttpURLConnection) {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setInstanceFollowRedirects(false);
            int statusCode = httpConnection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_MOVED_PERM || statusCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                // 解决请求被重定向到新地址的情况，如:nginx配置了http自动重定向到https
                String redirectUrl = httpConnection.getHeaderField("Location");
                url = new URL(redirectUrl);
            }
            return new BufferedInputStream(url.openStream());
        }
        return new BufferedInputStream(connection.getInputStream());
    }
}
