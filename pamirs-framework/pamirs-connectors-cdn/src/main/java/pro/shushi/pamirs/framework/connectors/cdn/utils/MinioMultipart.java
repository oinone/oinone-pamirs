package pro.shushi.pamirs.framework.connectors.cdn.utils;

import com.google.common.io.BaseEncoding;
import io.minio.http.Method;
import io.minio.messages.InitiateMultipartUploadResult;
import okhttp3.*;
import okio.BufferedSink;
import org.springframework.util.StringUtils;
import org.xmlpull.v1.XmlPullParserException;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MinioMultipart {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final String SHA256 = "x-amz-content-sha256";
    public static final String AMZ_DATE = "x-amz-date";
    private static final String HOST = "Host";
    private static final String MD5 = "MD5";
    private static final String CONTENT_MD5 = "Content-MD5";
    private static final String UNSIGNED_PAYLOAD = "UNSIGNED-PAYLOAD";
    private static final String REGION = "us-east-1";

    public static String initMultipartUpload(String baseUploadUrl, String objectName, String accessKey, String secretKey, String date) {
        if (StringUtils.isEmpty(baseUploadUrl)) return null;
        String url = baseUploadUrl + "/" + objectName + "?uploads=";
        Map<String, String> requestMap = new HashMap<>();
        try {
            requestMap.put(CONTENT_MD5, md5Hash());
            generateRequestHeader(requestMap, url, Method.POST.toString(), accessKey, secretKey, date);
        } catch (MalformedURLException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Minio 请求头构建失败", e);
            return null;
        }

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        for (String key : requestMap.keySet()) {
            requestBuilder.header(key, requestMap.get(key));
        }
        // 设置请求头
        requestBuilder.method(Method.POST.toString(), new HttpRequestBody());
        Request request = requestBuilder.build();
        OkHttpClient okHttpClient = new OkHttpClient();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.body() != null) {
                InitiateMultipartUploadResult result = new InitiateMultipartUploadResult();
                result.parseXml(response.body().charStream());
                response.body().close();
                return result.uploadId();
            }
        } catch (IOException | XmlPullParserException e) {
            log.error("minio 请求 uploadId 失败", e);
        }
        return null;
    }

    public static void generateRequestHeader(Map<String, String> requestMap, String url, String method, String accessKey, String secretKey, String date) throws MalformedURLException, NoSuchAlgorithmException, InvalidKeyException {
        URL urlObject = new URL(url);
        int port = urlObject.getPort();
        String host;
        if (port == -1) {
            host = urlObject.getHost();
        } else {
            host = urlObject.getHost() + CharacterConstants.SEPARATOR_COLON + port;
        }
        requestMap.put(CONTENT_TYPE, APPLICATION_OCTET_STREAM);
        requestMap.put(HOST, host);
        requestMap.put(SHA256, UNSIGNED_PAYLOAD);
        requestMap.put(AMZ_DATE, date);
        MinioSignV4.signV4(requestMap, method, url, REGION, accessKey, secretKey);
    }


    private static String md5Hash() throws NoSuchAlgorithmException {
        MessageDigest md5Digest = MessageDigest.getInstance(MD5);
        md5Digest.update(new byte[0], 0, 0);
        return BaseEncoding.base64().encode(md5Digest.digest());
    }


    private static class HttpRequestBody extends RequestBody {

        @Override
        public MediaType contentType() {
            return MediaType.parse(APPLICATION_OCTET_STREAM);
        }

        @Override
        public long contentLength() {
            return 0L;
        }

        @Override
        public void writeTo(BufferedSink sink) {
        }
    }
}
