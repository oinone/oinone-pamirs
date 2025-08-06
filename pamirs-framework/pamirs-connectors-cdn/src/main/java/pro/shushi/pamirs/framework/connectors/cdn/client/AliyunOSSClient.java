package pro.shushi.pamirs.framework.connectors.cdn.client;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.comm.RequestMessage;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.internal.RequestParameters;
import com.aliyun.oss.internal.SignUtils;
import com.aliyun.oss.model.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import pro.shushi.pamirs.framework.connectors.cdn.configure.CdnConfig;
import pro.shushi.pamirs.framework.connectors.cdn.constant.FileConstants;
import pro.shushi.pamirs.framework.connectors.cdn.factory.CdnConfigRouter;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.*;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.prefix.KeyPrefixManager;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.util.DateUtils;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static pro.shushi.pamirs.meta.common.constants.CharacterConstants.SEPARATOR_SLASH;

@Slf4j
@Order
@Component
@SPI.Service(AliyunOSSClient.TYPE)
public class AliyunOSSClient extends AbstractFileClient implements FileConstants {

    public static final String TYPE = "OSS";

    private static OSS DEFAULT_OSS_CLIENT;

    private static final Map<String, OSS> OSS_CLIENTS = new ConcurrentHashMap<>(2);

    @Override
    protected Map<String, String> generatorFormData(String fileKey, CdnUploadFileRequest request) {
        String filename = request.getFilename();
        CdnConfig cdnConfig = getCdnConfig();
        OSS oss = getOSS();
        String fileDir = getFileDir(cdnConfig.getMainDir());
        Long validTime = cdnConfig.getValidTime();
        Date policyExpiration = DateUtils.formatDate(System.currentTimeMillis() + validTime);
        final PolicyConditions policyConditions = new PolicyConditions();
        policyConditions.addConditionItem("content-length-range", 0L, 1048576000L);
        policyConditions.addConditionItem(MatchMode.StartWith, "key", fileDir);
        final String policy = oss.generatePostPolicy(policyExpiration, policyConditions);
        String encodedPolicy = BinaryUtil.toBase64String(policy.getBytes(StandardCharsets.UTF_8));
        String signature = oss.calculatePostSignature(policy);
        Map<String, String> formData = new HashMap<>();
        Optional.ofNullable(request.getAccept()).filter(StringUtils::isNotBlank).ifPresent(accept -> formData.put(FileConstants.ACCEPT, accept));
        formData.put("name", filename);
        formData.put("key", fileKey);
        formData.put("policy", encodedPolicy);
        formData.put("OSSAccessKeyId", cdnConfig.getAccessKeyId());
        formData.put("success_action_status", "200");
        formData.put("signature", signature);
        formData.put("x-oss-content-type", getContentType(request));
        return formData;
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
    protected CdnMultipartUploadData generatorMultipartFileFormData(String fileKey, CdnUploadFileRequest request) {
        List<CdnChunkFile> chunkFiles = getChunkFiles(request);
        if (chunkFiles == null) {
            return null;
        }
        CdnConfig cdnConfig = getCdnConfig();
        OSS oss = getOSS();
        String bucket = cdnConfig.getBucket();
        String accessKeyId = cdnConfig.getAccessKeyId();
        String accessKeySecret = cdnConfig.getAccessKeySecret();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(getContentType(request) + ";charset=UTF-8");
        InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(bucket, fileKey, metadata);
        InitiateMultipartUploadResult initiateMultipartUploadResult = oss.initiateMultipartUpload(initiateMultipartUploadRequest);
        String uploadId = initiateMultipartUploadResult.getUploadId();

        String baseUploadUrl = getBaseUploadUrl();
        String uploadUrl = baseUploadUrl + SEPARATOR_SLASH + fileKey;
        String resourcePath = SEPARATOR_SLASH + bucket + SEPARATOR_SLASH + fileKey;

        Date now = new Date();
        String nowString = DateUtil.formatAlternativeIso8601Date(now).replaceAll("-", "").replaceAll(":", "");
        CdnMultipartUploadData multipartUploadData = new CdnMultipartUploadData();

        List<CdnSingleUploadData> uploadDataList = new ArrayList<>();
        for (CdnChunkFile chunkFile : chunkFiles) {
            String partNumber = chunkFile.getPartNumber().toString();
            String fileSize = chunkFile.getFileSize().toString();
            CdnSingleUploadData singleUploadData = new CdnSingleUploadData();
            singleUploadData.setHttpMethod(HttpMethod.PUT.name());
            singleUploadData.setUploadUrl(String.format("%s?partNumber=%s&uploadId=%s", uploadUrl, partNumber, uploadId));
            Map<String, String> uploadHeaders = new LinkedHashMap<>(3);
            uploadHeaders.put(OSSHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_XML_VALUE);
            uploadHeaders.put(OSSHeaders.CONTENT_LENGTH, fileSize);
            uploadHeaders.put(OSSHeaders.DATE, nowString);
            uploadHeaders.put("x-oss-date", nowString);
            uploadSignature(uploadHeaders, bucket, accessKeyId, accessKeySecret, HttpMethod.PUT.name(), fileKey, resourcePath, uploadId, partNumber);
            singleUploadData.setUploadHeaders(uploadHeaders);
            uploadDataList.add(singleUploadData);
        }
        multipartUploadData.setUploadDataList(uploadDataList);

        CdnCompleteUploadData completeUploadData = new CdnCompleteUploadData();
        String uploadCompleteUrl = baseUploadUrl + SEPARATOR_SLASH + fileKey + "?uploadId=" + uploadId;
        completeUploadData.setUploadUrl(uploadCompleteUrl);
        Map<String, String> uploadCompleteHeaders = new LinkedHashMap<>(3);
        uploadCompleteHeaders.put(OSSHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_XML_VALUE);
        uploadCompleteHeaders.put(OSSHeaders.DATE, nowString);
        uploadCompleteHeaders.put("x-oss-date", nowString);
        uploadSignature(uploadCompleteHeaders, bucket, accessKeyId, accessKeySecret, HttpMethod.POST.name(), fileKey, resourcePath, uploadId);
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

    protected void uploadSignature(Map<String, String> headers, String bucket, String accessKeyId, String accessKeySecret,
                                   String httpMethod, String fileKey, String resourcePath, String uploadId) {
        uploadSignature(headers, bucket, accessKeyId, accessKeySecret, httpMethod, fileKey, resourcePath, uploadId, null);
    }

    protected void uploadSignature(Map<String, String> headers, String bucket, String accessKeyId, String accessKeySecret,
                                   String httpMethod, String fileKey, String resourcePath, String uploadId, String partNumber) {
        RequestMessage request = new RequestMessage(bucket, fileKey);
        request.setHeaders(headers);
        if (partNumber != null) {
            request.addParameter(RequestParameters.PART_NUMBER, partNumber);
        }
        request.addParameter(RequestParameters.UPLOAD_ID, uploadId);
        String signature = SignUtils.buildSignature(accessKeySecret, httpMethod, resourcePath, request);
        headers.put(OSSHeaders.AUTHORIZATION, SignUtils.composeRequestAuthorization(accessKeyId, signature));
    }

    @Override
    public CdnFile upload(String fileName, byte[] data) {
        return upload(fileName, new ByteArrayInputStream(data));
    }

    @Override
    public CdnFile upload(String fileName, InputStream inputStream) {
        CdnConfig cdnConfig = getCdnConfig();
        OSS oss = getOSS();
        CdnFile resourceFile = new CdnFile();
        String fileKey = getFileKey(cdnConfig.getMainDir(), fileName);
        String bucket = cdnConfig.getBucket();
        oss.putObject(
                bucket,
                fileKey,
                inputStream);
        //获取文件大小
        SimplifiedObjectMeta objectMeta =
                oss.getSimplifiedObjectMeta(
                        cdnConfig.getBucket(),
                        fileKey);
        resourceFile.setName(fileName);
        resourceFile.setSize(objectMeta.getSize());
        resourceFile.setType(FILE_TYPE);
        resourceFile.setUrl(generatorDownloadUrl(fileKey));
        return resourceFile;
    }

    @Override
    public String uploadByFileName(String fileName, byte[] data) {
        return uploadByFileName(fileName, new ByteArrayInputStream(data));
    }

    @Override
    public String uploadByFileName(String fileName, InputStream inputStream) {
        CdnConfig cdnConfig = getCdnConfig();
        OSS oss = getOSS();
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
        String bucket = cdnConfig.getBucket();
        oss.putObject(
                bucket,
                fileKey,
                inputStream);
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
    public InputStream getDownloadStream(String fileKey) {
        fileKey = prepareDownloadFileKey(fileKey);
        CdnConfig cdnConfig = getCdnConfig();
        OSS oss = getOSS();
        OSSObject object = oss.getObject(cdnConfig.getBucket(), fileKey);
        if (object != null) {
            return object.getObjectContent();
        }
        return null;
    }

    @Override
    public String fetchContent(String fileName) {
        try {
            CdnConfig cdnConfig = getCdnConfig();
            OSS oss = getOSS();
            String bucket = cdnConfig.getBucket();
            String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
            String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
            OSSObject object = oss.getObject(bucket, fileKey);
            if (null != object) {
                InputStream inputStream = object.getObjectContent();
                String content = IOUtils.toString(inputStream);
                inputStream.close();
                return content;
            }
        } catch (IOException e) {
            log.error("阿里云OSS读取文件io异常", e);
        }
        return null;
    }

    @Override
    public void deleteByFolder(String folder) {
        CdnConfig cdnConfig = getCdnConfig();
        OSS oss = getOSS();
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        String folderKey = cdnConfig.getMainDir() + keyPrefix + folder;
        String bucket = cdnConfig.getBucket();
        ObjectListing objectListing = oss.listObjects(bucket, folderKey);
        List<OSSObjectSummary> objectSummaries = objectListing.getObjectSummaries();
        List<String> keys = new ArrayList<>(objectSummaries.size());
        for (OSSObjectSummary summary : objectSummaries) {
            keys.add(summary.getKey());
        }
        if (CollectionUtils.isNotEmpty(keys)) {
            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucket)
                    .withKeys(keys);
            deleteObjectsRequest.setKey(folderKey);
            oss.deleteObjects(deleteObjectsRequest);
        }
    }

    @Override
    public void deleteByFilename(String fileName) {
        CdnConfig cdnConfig = getCdnConfig();
        OSS oss = getOSS();
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
        String bucket = cdnConfig.getBucket();
        oss.deleteObject(bucket, fileKey);
    }

    @Override
    public boolean isExistByFilename(String fileName) {
        CdnConfig cdnConfig = getCdnConfig();
        OSS oss = getOSS();
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
        String bucket = cdnConfig.getBucket();
        return oss.doesObjectExist(bucket, fileKey);
    }

    private void appendParameter(StringBuilder builder, String parameter) {
        if (builder.length() == 0) {
            builder.append("?");
        }
        builder.append(parameter);
    }

    /**
     * 静态文件只有路径/用于初始化文件放置的路径
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

    protected OSS getOSS() {
        String routerKey = CdnConfigRouter.get();
        if (StringUtils.isBlank(routerKey)) {
            return DEFAULT_OSS_CLIENT;
        }
        return OSS_CLIENTS.computeIfAbsent(routerKey, (key) -> generatorOSSClient(getCdnConfig()));
    }

    protected OSS generatorOSSClient(CdnConfig cdnConfig) {
        String accessKeyId = cdnConfig.getAccessKeyId();
        String accessKeySecret = cdnConfig.getAccessKeySecret();
        String endpoint = cdnConfig.getUploadUrl();
        endpoint = endpoint.replace(HTTP, CharacterConstants.SEPARATOR_EMPTY)
                .replace(HTTPS, CharacterConstants.SEPARATOR_EMPTY);
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret, getClientBuilderConfiguration(cdnConfig));
    }

    protected ClientBuilderConfiguration getClientBuilderConfiguration(CdnConfig cdnConfig) {
        ClientBuilderConfiguration config = new ClientBuilderConfiguration();
        Long connectTimeout = cdnConfig.getTimeout();
        if (connectTimeout != null) {
            config.setConnectionTimeout(connectTimeout.intValue());
            config.setConnectionRequestTimeout(connectTimeout.intValue());
            config.setSocketTimeout(connectTimeout.intValue());
            config.setRequestTimeout(connectTimeout.intValue());
            config.setMaxErrorRetry(0);
        }
        String referer = cdnConfig.getReferer();
        if (StringUtils.isNotBlank(referer)) {
            config.getDefaultHeaders().put(HDEAER_REFERER, referer);
        }
        return config;
    }

    @PostConstruct
    public void init() {
        CdnConfig cdnConfig = getCdnConfig();
        if (AliyunOSSClient.TYPE.equals(cdnConfig.getType())) {
            DEFAULT_OSS_CLIENT = generatorOSSClient(cdnConfig);
        }
    }
}
