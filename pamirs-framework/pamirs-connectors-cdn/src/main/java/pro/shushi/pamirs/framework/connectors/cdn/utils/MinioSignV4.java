package pro.shushi.pamirs.framework.connectors.cdn.utils;

import com.google.common.base.Joiner;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.io.BaseEncoding;
import io.minio.DateFormat;
import okhttp3.HttpUrl;
import org.joda.time.DateTime;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static pro.shushi.pamirs.framework.connectors.cdn.utils.MinioMultipart.AMZ_DATE;
import static pro.shushi.pamirs.framework.connectors.cdn.utils.MinioMultipart.SHA256;

/**
 * @author: xuxin
 * @createTime: 2024/06/05 13:41
 */
public class MinioSignV4 {
    private static final Set<String> IGNORED_HEADERS = new HashSet<>();

    static {
        IGNORED_HEADERS.add("authorization");
        IGNORED_HEADERS.add("content-type");
        IGNORED_HEADERS.add("content-length");
        IGNORED_HEADERS.add("user-agent");
    }

    private Map<String, String> requestMap;
    private String contentSha256;
    private DateTime date;
    private String region;
    private String accessKey;
    private String secretKey;
    private String prevSignature;

    private String scope;
    private Map<String, String> canonicalHeaders;
    private String signedHeaders;
    private HttpUrl url;
    private String canonicalQueryString;
    private String canonicalRequest;
    private String canonicalRequestHash;
    private String stringToSign;
    private byte[] signingKey;
    private String signature;
    private String authorization;

    public static void signV4(Map<String, String> requestMap, String method, String url, String region, String accessKey, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        String contentSha256 = requestMap.get(SHA256);
        DateTime date = DateFormat.AMZ_DATE_FORMAT.parseDateTime(requestMap.get(AMZ_DATE));
        MinioSignV4 signer = new MinioSignV4(requestMap, contentSha256, date, region, accessKey, secretKey, null);
        signer.setScope();
        signer.setCanonicalRequest(requestMap, method, url);
        signer.setStringToSign();
        signer.setSigningKey();
        signer.setSignature();
        signer.setAuthorization();
        requestMap.put("Authorization", signer.authorization);
    }

    public MinioSignV4(Map<String, String> requestMap, String contentSha256, DateTime date, String region, String accessKey, String secretKey, String prevSignature) {
        this.requestMap = requestMap;
        this.contentSha256 = contentSha256;
        this.date = date;
        this.region = region;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.prevSignature = prevSignature;
    }

    private void setAuthorization() {
        this.authorization = "AWS4-HMAC-SHA256 Credential=" + this.accessKey + "/" + this.scope + ", SignedHeaders=" + this.signedHeaders + ", Signature=" + this.signature;
    }


    private void setSignature() throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] digest = sumHmac(this.signingKey, this.stringToSign.getBytes(StandardCharsets.UTF_8));
        this.signature = BaseEncoding.base16().encode(digest).toLowerCase(Locale.US);
    }

    private void setSigningKey() throws NoSuchAlgorithmException, InvalidKeyException {
        String aws4SecretKey = "AWS4" + this.secretKey;

        byte[] dateKey = sumHmac(aws4SecretKey.getBytes(StandardCharsets.UTF_8), this.date.toString(DateFormat.SIGNER_DATE_FORMAT).getBytes(StandardCharsets.UTF_8));

        byte[] dateRegionKey = sumHmac(dateKey, this.region.getBytes(StandardCharsets.UTF_8));

        byte[] dateRegionServiceKey = sumHmac(dateRegionKey, "s3".getBytes(StandardCharsets.UTF_8));

        this.signingKey = sumHmac(dateRegionServiceKey, "aws4_request".getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] sumHmac(byte[] key, byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");

        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        mac.update(data);

        return mac.doFinal();
    }

    private void setStringToSign() {
        this.stringToSign = "AWS4-HMAC-SHA256" + "\n" + this.date.toString(DateFormat.AMZ_DATE_FORMAT) + "\n" + this.scope + "\n" + this.canonicalRequestHash;
    }

    private void setScope() {
        this.scope = this.date.toString(DateFormat.SIGNER_DATE_FORMAT) + "/" + this.region + "/s3/aws4_request";
    }

    private void setCanonicalRequest(Map<String, String> requestMap, String method, String url) throws NoSuchAlgorithmException {
        setCanonicalHeaders(requestMap);
        this.url = HttpUrl.get(url);
        setCanonicalQueryString();
        this.canonicalRequest = method + "\n" + this.url.encodedPath() + "\n" + this.canonicalQueryString + "\n" + Joiner.on("\n").withKeyValueSeparator(":").join(this.canonicalHeaders) + "\n\n" + this.signedHeaders + "\n" + this.contentSha256;
        this.canonicalRequestHash = MinioSignV4.sha256Hash(this.canonicalRequest);
    }

    public static String sha256Hash(String string) throws NoSuchAlgorithmException {
        byte[] data = string.getBytes(StandardCharsets.UTF_8);
        MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
        sha256Digest.update((byte[]) data, 0, data.length);
        return BaseEncoding.base16().encode(sha256Digest.digest()).toLowerCase(Locale.US);
    }

    private void setCanonicalHeaders(Map<String, String> requestMap) {
        this.canonicalHeaders = new TreeMap<>();

        for (String key : requestMap.keySet()) {
            String signedHeader = key.toLowerCase(Locale.US);
            if (!IGNORED_HEADERS.contains(signedHeader)) {
                this.canonicalHeaders.put(signedHeader, requestMap.get(key));
            }
        }
        this.signedHeaders = Joiner.on(";").join(this.canonicalHeaders.keySet());
    }

    private void setCanonicalQueryString() {
        String encodedQuery = this.url.encodedQuery();
        if (encodedQuery == null) {
            this.canonicalQueryString = "";
            return;
        }

        // Building a multimap which only order keys, ordering values is not performed
        // until MinIO server supports it.
        Multimap<String, String> signedQueryParams = MultimapBuilder.treeKeys().arrayListValues().build();

        for (String queryParam : encodedQuery.split("&")) {
            String[] tokens = queryParam.split("=");
            if (tokens.length > 1) {
                signedQueryParams.put(tokens[0], tokens[1]);
            } else {
                signedQueryParams.put(tokens[0], "");
            }
        }

        this.canonicalQueryString = Joiner.on("&").withKeyValueSeparator("=").join(signedQueryParams.entries());
    }

}
