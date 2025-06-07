package pro.shushi.pamirs.framework.connectors.cdn.client;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.internal.RequestParameters;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.Headers;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.auth.COSSigner;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.cdn.configure.CdnConfig;
import pro.shushi.pamirs.framework.connectors.cdn.constant.FileConstants;
import pro.shushi.pamirs.framework.connectors.cdn.factory.CdnConfigRouter;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.*;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.prefix.KeyPrefixManager;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.qcloud.cos.model.CORSRule.AllowedMethods.*;
import static pro.shushi.pamirs.framework.connectors.cdn.enmu.CDNExpEnum.FILE_CDN_RM_ERROR;
import static pro.shushi.pamirs.meta.common.constants.CharacterConstants.SEPARATOR_SLASH;

/**
 * TencentCosClient
 *
 * @author yakir on 2023/08/17 19:50.
 */
@Slf4j
@Component
@SPI.Service(TencentCosClient.TYPE)
public class TencentCosClient extends AbstractFileClient implements FileConstants {

    public static final String TYPE = "TENCENT_COS";

    private static COSClient DEFAULT_TENCENT_COS_CLIENT;

    private static final Map<String, COSClient> TENCENT_OBS_CLIENTS = new ConcurrentHashMap<>(2);


    /**
     * doc: https://cloud.tencent.com/document/product/436/14690
     *
     * @param fileKey 文件存储Key
     * @param request 上传文件请求参数
     * @return FormData提交参数
     */
    @Override
    protected Map<String, String> generatorFormData(String fileKey, CdnUploadFileRequest request) {
        CdnConfig cdnConfig = getCdnConfig();
        long startTimestamp = System.currentTimeMillis() / 1000;
        long endTimestamp = startTimestamp + 30 * 60;
        String endTimestampStr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(endTimestamp * 1000);
        String keyTime = startTimestamp + ";" + endTimestamp;
        String policy = "{\n" +
                "    \"expiration\": \"" + endTimestampStr + "\",\n" +
                "    \"conditions\": [\n" +
                "        { \"bucket\": \"" + cdnConfig.getBucket() + "\" },\n" +
                "        { \"q-sign-algorithm\": \"sha1\" },\n" +
                "        { \"q-ak\": \"" + cdnConfig.getAccessKeyId() + "\" },\n" +
                "        { \"q-sign-time\":\"" + keyTime + "\" }\n" +
                "    ]\n" +
                "}";
        String encodedPolicy = new String(Base64.encodeBase64(policy.getBytes()));
        String signature = new COSSigner().buildPostObjectSignature(cdnConfig.getAccessKeySecret(), keyTime, policy);

        Map<String, String> formData = new HashMap<>();
        formData.put("q-sign-algorithm", "sha1");
        formData.put(FileConstants.KEY, fileKey);
        formData.put(HttpHeaders.CONTENT_TYPE, getContentType(request));
        formData.put("q-ak", cdnConfig.getAccessKeyId());
        formData.put(FileConstants.POLICY, encodedPolicy);
        formData.put("q-key-time", keyTime);
        formData.put(FileConstants.SUCCESS_ACTION_STATUS, HttpStatus.OK.value() + "");
        formData.put("q-signature", signature);
        return formData;
    }

    @Override
    protected CdnMultipartUploadData generatorMultipartFileFormData(String fileKey, CdnUploadFileRequest request) {
        List<CdnChunkFile> chunkFiles = getChunkFiles(request);
        if (chunkFiles == null) {
            return null;
        }
        CdnConfig cdnConfig = getCdnConfig();
        COSClient cosClient = getCosClient();

        String bucket = cdnConfig.getBucket();
        String accessKeyId = cdnConfig.getAccessKeyId();
        String accessKeySecret = cdnConfig.getAccessKeySecret();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(getContentType(request) + ";charset=UTF-8");

        InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(bucket, fileKey);
        InitiateMultipartUploadResult initiateMultipartUploadResult = cosClient.initiateMultipartUpload(initiateMultipartUploadRequest);
        initiateMultipartUploadRequest.setObjectMetadata(objectMetadata);

        String uploadId = initiateMultipartUploadResult.getUploadId();

        String baseUploadUrl = getBaseUploadUrl();
        String uploadUrl = baseUploadUrl + SEPARATOR_SLASH + fileKey;

        CdnMultipartUploadData multipartUploadData = new CdnMultipartUploadData();

        List<CdnSingleUploadData> uploadDataList = new ArrayList<>();
        for (CdnChunkFile chunkFile : chunkFiles) {
            String partNumber = chunkFile.getPartNumber().toString();
            String fileSize = chunkFile.getFileSize().toString();
            CdnSingleUploadData singleUploadData = new CdnSingleUploadData();
            singleUploadData.setHttpMethod(HttpMethod.PUT.name());
            singleUploadData.setUploadUrl(String.format("%s?partNumber=%s&uploadId=%s", uploadUrl, partNumber, uploadId));

            Map<String, String> uploadHeaders = new LinkedHashMap<>(3);
            uploadHeaders.put(Headers.CONTENT_LENGTH, fileSize);
            uploadHeaders.put(HttpHeaders.CONTENT_TYPE, getContentType(request));
            uploadSignature(uploadHeaders, accessKeyId, accessKeySecret, HttpMethod.PUT.name(), fileKey, uploadId, partNumber);
            singleUploadData.setUploadHeaders(uploadHeaders);
            uploadDataList.add(singleUploadData);
        }
        multipartUploadData.setUploadDataList(uploadDataList);

        CdnCompleteUploadData completeUploadData = new CdnCompleteUploadData();
        String uploadCompleteUrl = baseUploadUrl + SEPARATOR_SLASH + fileKey + "?uploadId=" + uploadId;
        completeUploadData.setUploadUrl(uploadCompleteUrl);
        Map<String, String> uploadCompleteHeaders = new LinkedHashMap<>(3);
        uploadCompleteHeaders.put(OSSHeaders.CONTENT_TYPE, getContentType(request));
        uploadSignature(uploadCompleteHeaders, accessKeyId, accessKeySecret, HttpMethod.POST.name(), fileKey, uploadId);
        completeUploadData.setUploadHeaders(uploadCompleteHeaders);
        completeUploadData.setUploadData("`<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<CompleteMultipartUpload>\n" +
                "${parts}" +
                "</CompleteMultipartUpload>`");
        completeUploadData.setUploadPartData("`<Part>\n" +
                "<PartNumber>${partNumber}</PartNumber>\n" +
                "<ETag>${response.partKey}</ETag>\n" +
                "</Part>\n`");
        Map<String, String> uploadCompletePartContext = new HashMap<>();
        uploadCompletePartContext.put("partNumber", "partNumber");
        uploadCompletePartContext.put("response.partKey", "headers.etag");
        completeUploadData.setUploadPartContext(uploadCompletePartContext);
        multipartUploadData.setCompleteUploadData(completeUploadData);

        return multipartUploadData;
    }

    protected void uploadSignature(Map<String, String> headers, String accessKeyId, String accessKeySecret,
                                   String httpMethod, String fileKey, String uploadId) {
        uploadSignature(headers, accessKeyId, accessKeySecret, httpMethod, fileKey, uploadId, null);
    }

    protected void uploadSignature(Map<String, String> headers,
                                   String accessKeyId, String accessKeySecret,
                                   String httpMethod, String fileKey,
                                   String uploadId, String partNumber) {
        COSCredentials cred = new BasicCOSCredentials(accessKeyId, accessKeySecret);
        String resource_path = SEPARATOR_SLASH + fileKey;

        COSSigner signer = new COSSigner();
        Date startTime = new Date(System.currentTimeMillis());
        Date expiredTime = new Date(System.currentTimeMillis() + 30 * 60 * 1000);
        Map<String, String> paramMap = new HashMap<>();
        if (partNumber != null) {
            paramMap.put(RequestParameters.PART_NUMBER, partNumber);
        }
        paramMap.put(RequestParameters.UPLOAD_ID, uploadId);
        String signature = signer.buildAuthorizationStr(HttpMethodName.valueOf(httpMethod), resource_path, headers, paramMap, cred, startTime, expiredTime, false);
        headers.put(Headers.COS_AUTHORIZATION, signature);
    }

    @Override
    protected String appendPictureProcessParameters(String downloadUrl, String imageResizeParameter) {
        URL url = null;
        try {
            url = new URL(downloadUrl);
        } catch (MalformedURLException e) {
            log.warn("Image append resize extension parameter error.", e);
        }
        if (url == null) {
            return downloadUrl;
        }
        return urlToString(url, appendQueryParameter(url.getQuery(), FileConstants.X_OSS_PROCESS, FileConstants.OSS_PRECESS_IMAGE_RESIZE + CharacterConstants.SEPARATOR_COMMA + imageResizeParameter));
    }

    @Override
    public CdnFile upload(String fileName, byte[] data) {
        CdnConfig cdnConfig = getCdnConfig();
        COSClient cosClient = getCosClient();
        CdnFile resourceFile = new CdnFile();
        String fileKey = getFileKey(cdnConfig.getMainDir(), fileName);
        String bucket = cdnConfig.getBucket();
        //cosClient.putObject(bucket, fileKey, new ByteArrayInputStream(data));
        cosClient.putObject(bucket, fileKey, new ByteArrayInputStream(data), new ObjectMetadata());
        //获取文件大小
        ObjectMetadata objectMeta = cosClient.getObjectMetadata(cdnConfig.getBucket(), fileKey);
        resourceFile.setName(fileName);
        resourceFile.setSize(objectMeta.getContentLength());
        resourceFile.setType(FILE_TYPE);
        resourceFile.setUrl(generatorDownloadUrl(fileKey));
        return resourceFile;
    }

    @Override
    public String uploadByFileName(String fileName, byte[] data) {
        CdnConfig cdnConfig = getCdnConfig();
        COSClient cosClient = getCosClient();
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
        String bucket = cdnConfig.getBucket();
        cosClient.putObject(bucket, fileKey, new ByteArrayInputStream(data), new ObjectMetadata());
        return generatorDownloadUrl(fileKey);
    }

    @Override
    public String uploadByFileName(String fileName, InputStream inputStream) {
        CdnConfig cdnConfig = getCdnConfig();
        COSClient cosClient = getCosClient();
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
        String bucket = cdnConfig.getBucket();
        cosClient.putObject(bucket, fileKey, inputStream, new ObjectMetadata());
        return generatorDownloadUrl(fileKey);
    }

    @Override
    public String getDownloadUrl(String fileName) {
        CdnConfig cdnConfig = getCdnConfig();
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
        return generatorDownloadUrl(fileKey);
    }

    @Override
    public String fetchContent(String fileName) {
        CdnConfig cdnConfig = getCdnConfig();
        COSClient cosClient = getCosClient();
        try {
            String bucket = cdnConfig.getBucket();
            String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
            String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
            COSObject object = cosClient.getObject(bucket, fileKey);
            if (null != object) {
                InputStream inputStream = object.getObjectContent();
                String content = IOUtils.toString(inputStream);
                inputStream.close();
                return content;
            }
        } catch (IOException e) {
            log.error("腾讯云读取文件io异常", e);
        }
        return null;
    }

    @Override
    public void deleteByFilename(String fileName) {
        CdnConfig cdnConfig = getCdnConfig();
        COSClient cosClient = getCosClient();
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
        String bucket = cdnConfig.getBucket();
        try {
            cosClient.deleteObject(bucket, fileKey);
        } catch (CosClientException exp) {
            log.error("删除文件发生异常: [{}]", fileName);
            throw PamirsException.construct(FILE_CDN_RM_ERROR).errThrow();
        }
    }

    @Override
    public boolean isExistByFilename(String fileName) {
        CdnConfig cdnConfig = getCdnConfig();
        COSClient cosClient = getCosClient();
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
        String bucket = cdnConfig.getBucket();
        return cosClient.doesObjectExist(bucket, fileKey);
    }

    /**
     * 为保证使用OSS服务和本地资源文件，可以随时切换
     *
     * @return
     */
    @Override
    public String getStaticUrl() {
        CdnConfig cdnConfig = getCdnConfig();
        if (cdnConfig.getAppLogoUseCdn() != null && cdnConfig.getAppLogoUseCdn()) {
            return getBaseDownloadUrl();
        } else {
            return CdnConfig.defaultCdnUrl;
        }
    }

    protected COSClient getCosClient() {
        String routerKey = CdnConfigRouter.get();
        if (StringUtils.isBlank(routerKey)) {
            return DEFAULT_TENCENT_COS_CLIENT;
        }
        return TENCENT_OBS_CLIENTS.computeIfAbsent(routerKey, (key) -> generatorCosClient(getCdnConfig()));
    }

    protected COSClient generatorCosClient(CdnConfig cdnConfig) {
        int connectTimeout = Integer.parseInt(String.valueOf(cdnConfig.getTimeout()));
        String accessKeyId = cdnConfig.getAccessKeyId();
        String accessKeySecret = cdnConfig.getAccessKeySecret();
        String endpoint = cdnConfig.getUploadUrl();
        endpoint = endpoint.replace(HTTP, CharacterConstants.SEPARATOR_EMPTY).replace(HTTPS, CharacterConstants.SEPARATOR_EMPTY);
        COSCredentials cred = new BasicCOSCredentials(accessKeyId, accessKeySecret);
        Region region = Optional.of(endpoint)
                .map(_ep -> _ep.split(".myqcloud.com"))
                .map(_arr -> _arr[0])
                .map(_ep -> _ep.split("cos."))
                .map(_arr -> _arr[1])
                .map(Region::new)
                .orElse(null);
        ClientConfig config = new ClientConfig(region);
        config.setHttpProtocol(HttpProtocol.https);
        config.setRequestTimeout(connectTimeout);
        config.setSocketTimeout(connectTimeout);
        config.setConnectionTimeout(connectTimeout);
        config.setConnectionRequestTimeout(connectTimeout);
        // cors 设置 https://www.qcloud.com/document/product/436/6224
        COSClient cosClient = new COSClient(cred, config);
        if (StringUtils.isNotBlank(cdnConfig.getAllowedOrigin())) {
            BucketCrossOriginConfiguration corsConfig = new BucketCrossOriginConfiguration();
            CORSRule corsRule = new CORSRule();
            corsRule.setAllowedHeaders("*");
            corsRule.setMaxAgeSeconds(60);
            corsRule.setExposedHeaders(Headers.ETAG, Headers.REQUEST_ID, Headers.CONTENT_LENGTH);
            corsRule.setAllowedMethods(PUT, GET, POST, DELETE, HEAD);
            List<String> allowedOrigin = Splitter.on(FileConstants.COMMA).splitToList(cdnConfig.getAllowedOrigin());
            corsRule.setAllowedOrigins(allowedOrigin);
            corsConfig.setRules(Lists.newArrayList(corsRule));
            cosClient.setBucketCrossOriginConfiguration(cdnConfig.getBucket(), corsConfig);
        }
        return cosClient;
    }

    @PostConstruct
    public void init() {
        CdnConfig cdnConfig = getCdnConfig();
        if (TencentCosClient.TYPE.equals(cdnConfig.getType())) {
            DEFAULT_TENCENT_COS_CLIENT = generatorCosClient(cdnConfig);
        }
    }
}
