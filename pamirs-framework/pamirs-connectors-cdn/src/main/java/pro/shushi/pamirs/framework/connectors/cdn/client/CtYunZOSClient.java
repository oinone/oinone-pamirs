package pro.shushi.pamirs.framework.connectors.cdn.client;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.internal.RequestParameters;
import com.google.common.base.Splitter;
import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.internal.Constants;
import com.obs.services.internal.utils.AbstractAuthentication;
import com.obs.services.model.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import pro.shushi.pamirs.framework.connectors.cdn.configure.CdnConfig;
import pro.shushi.pamirs.framework.connectors.cdn.constant.FileConstants;
import pro.shushi.pamirs.framework.connectors.cdn.factory.CdnConfigRouter;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.*;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.prefix.KeyPrefixManager;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.util.JsonUtils;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static pro.shushi.pamirs.framework.connectors.cdn.enmu.CDNExpEnum.FILE_CDN_RM_ERROR;
import static pro.shushi.pamirs.meta.common.constants.CharacterConstants.SEPARATOR_SLASH;

/**
 * CtYunZOSClient 天翼云ZOS
 * 官方对接文档：https://www.ctyun.cn/document/10026735/10172648
 *
 * @author wangxian on 2026/01/23
 */
@Slf4j
@Order
@Component
@SPI.Service(CtYunZOSClient.TYPE)
public class CtYunZOSClient extends AbstractFileClient implements FileConstants {

    public static final String TYPE = "CTYUN_ZOS";

    private static ObsClient DEFAULT_ZOS_CLIENT;

    private static final Map<String, ObsClient> CT_ZOS_CLIENTS = new ConcurrentHashMap<>(2);

    @PostConstruct
    public void init() {
        CdnConfig cdnConfig = getCdnConfig();
        if (CtYunZOSClient.TYPE.equals(cdnConfig.getType())) {
            DEFAULT_ZOS_CLIENT = generatorZosClient(cdnConfig);
        }
    }

    protected ObsClient getObsClient() {
        String routerKey = CdnConfigRouter.get();
        if (StringUtils.isBlank(routerKey)) {
            return DEFAULT_ZOS_CLIENT;
        }
        return CT_ZOS_CLIENTS.computeIfAbsent(routerKey, (key) -> generatorZosClient(getCdnConfig()));
    }

    protected ObsClient generatorZosClient(CdnConfig cdnConfig) {
        Long connectTimeout = cdnConfig.getTimeout();
        String accessKeyId = cdnConfig.getAccessKeyId();
        String accessKeySecret = cdnConfig.getAccessKeySecret();
        String endpoint = cdnConfig.getUploadUrl();
        endpoint = endpoint.replace(HTTP, "").replace(HTTPS, "");

        ObsConfiguration config = new ObsConfiguration();
        config.setConnectionTimeout(connectTimeout.intValue());
        config.setSocketTimeout(connectTimeout.intValue());
        config.setEndPoint(endpoint);
        config.setMaxErrorRetry(0);
        config.setAuthType(AuthTypeEnum.OBS);

        ObsClient obsClient = new ObsClient(accessKeyId, accessKeySecret, config);

        try {
            BucketCors cors = tianyiBucketCors(cdnConfig);
            if (cors != null) {
                obsClient.setBucketCors(cdnConfig.getBucket(), cors);
                obsClient.setBucketAcl(cdnConfig.getBucket(), AccessControlList.REST_CANNED_PUBLIC_READ);
            }
        } catch (Exception e) {
            log.warn("TianyiZOS SET_CANNED_PUBLIC_READ OR CORS ERROR", e);
        }

        return obsClient;
    }

    private BucketCors tianyiBucketCors(CdnConfig cdnConfig) {
        if (StringUtils.isBlank(cdnConfig.getAllowedOrigin())) {
            return null;
        }
        BucketCors cors = new BucketCors();
        List<BucketCorsRule> rules = new ArrayList<>();
        BucketCorsRule rule = new BucketCorsRule();

        List<String> allowedOrigin = Splitter.on(FileConstants.COMMA).splitToList(cdnConfig.getAllowedOrigin());
        rule.setAllowedOrigin(allowedOrigin);

        List<String> allowedMethod = Arrays.asList(
                RequestMethod.GET.name(),
                RequestMethod.PUT.name(),
                RequestMethod.DELETE.name(),
                RequestMethod.POST.name(),
                RequestMethod.HEAD.name()
        );
        rule.setAllowedMethod(allowedMethod);

        rule.setAllowedHeader(Collections.singletonList("*"));
        rule.setExposeHeader(Arrays.asList("ETag", "Content-Length", "x-oos-request-id"));
        rule.setMaxAgeSecond(10);
        rules.add(rule);
        cors.setRules(rules);
        return cors;
    }

    @Override
    public String uploadByFileName(String fileName, byte[] data) {
        CdnConfig cdnConfig = getCdnConfig();
        ObsClient obsClient = getObsClient();
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
        String bucket = cdnConfig.getBucket();
        obsClient.putObject(bucket, fileKey, new ByteArrayInputStream(data));
        return generatorDownloadUrl(fileKey);
    }

    @Override
    public String uploadByFileName(String fileName, InputStream inputStream) {
        CdnConfig cdnConfig = getCdnConfig();
        ObsClient obsClient = getObsClient();
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
        String bucket = cdnConfig.getBucket();
        obsClient.putObject(bucket, fileKey, inputStream);
        return generatorDownloadUrl(fileKey);
    }

    @Override
    public CdnFile upload(String fileName, byte[] data) {
        CdnConfig cdnConfig = getCdnConfig();
        ObsClient obsClient = getObsClient();
        CdnFile resourceFile = new CdnFile();
        String fileKey = getFileKey(cdnConfig.getMainDir(), fileName);
        String bucket = cdnConfig.getBucket();

        PutObjectRequest request = new PutObjectRequest(bucket, fileKey, new ByteArrayInputStream(data));
        request.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ);
        obsClient.putObject(request);

        ObjectMetadata objectMeta = obsClient.getObjectMetadata(bucket, fileKey);
        resourceFile.setName(fileName);
        resourceFile.setSize(objectMeta.getContentLength());
        resourceFile.setType(FILE_TYPE);
        resourceFile.setUrl(generatorDownloadUrl(fileKey));
        return resourceFile;
    }

    @Override
    protected Map<String, String> generatorFormData(String fileKey, CdnUploadFileRequest request) {
        CdnConfig cdnConfig = getCdnConfig();
        ObsClient obsClient = getObsClient();

        String contentType = getContentType(request);
        if (contentType == null || contentType.trim().isEmpty()) {
            contentType = "application/octet-stream";
        }
        Map<String, Object> signatureFormParams = new HashMap<>();
        signatureFormParams.put(FileConstants.KEY, fileKey);
        signatureFormParams.put(HttpHeaders.CONTENT_TYPE, contentType);
        signatureFormParams.put("ACL", "public-read");// 天翼云必须是大写的ACL
        signatureFormParams.put("success_action_status", "204");
        PostSignatureRequest signatureRequest = new PostSignatureRequest();
        signatureRequest.setExpires(3600);
        signatureRequest.setFormParams(signatureFormParams);

        PostSignatureResponse signatureResponse = obsClient.createPostSignature(signatureRequest);
        // 构造返回给前端的表单数据
        Map<String, String> formData = new LinkedHashMap<>();
        formData.put(FileConstants.KEY, fileKey);
        formData.put(HttpHeaders.CONTENT_TYPE, contentType);
        formData.put("ACL", "public-read");// 天翼云必须是大写的ACL
        formData.put("success_action_status", "204");
        formData.put(FileConstants.POLICY, signatureResponse.getPolicy());
        formData.put(FileConstants.SIGNATURE, signatureResponse.getSignature());
        formData.put("AWSAccessKeyId", cdnConfig.getAccessKeyId());// 天翼云必须是AWSAccessKeyId

        return formData;
    }

    @Override
    protected CdnMultipartUploadData generatorMultipartFileFormData(String fileKey, CdnUploadFileRequest request) {
        List<CdnChunkFile> chunkFiles = getChunkFiles(request);
        if (chunkFiles == null) {
            return null;
        }
        CdnConfig cdnConfig = getCdnConfig();
        ObsClient obsClient = getObsClient();
        String bucket = cdnConfig.getBucket();
        String accessKeyId = cdnConfig.getAccessKeyId();
        String accessKeySecret = cdnConfig.getAccessKeySecret();

        InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(bucket, fileKey);
        ObjectMetadata metadata = new ObjectMetadata();
        initiateMultipartUploadRequest.setMetadata(metadata);
        InitiateMultipartUploadResult result = obsClient.initiateMultipartUpload(initiateMultipartUploadRequest);
        String uploadId = result.getUploadId();

        String baseUploadUrl = getBaseUploadUrl();
        String uploadUrl = baseUploadUrl + SEPARATOR_SLASH + fileKey;
        String resourcePath = SEPARATOR_SLASH + bucket + SEPARATOR_SLASH + fileKey;

        Date now = new Date(System.currentTimeMillis());
        CdnMultipartUploadData multipartUploadData = new CdnMultipartUploadData();
        DateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        List<CdnSingleUploadData> uploadDataList = new ArrayList<>();
        for (CdnChunkFile chunkFile : chunkFiles) {
            String partNumber = chunkFile.getPartNumber().toString();
            CdnSingleUploadData singleUploadData = new CdnSingleUploadData();
            singleUploadData.setHttpMethod(HttpMethod.PUT.name());
            singleUploadData.setUploadUrl(String.format("%s?partNumber=%s&uploadId=%s", uploadUrl, partNumber, uploadId));
            Map<String, String> uploadHeaders = new LinkedHashMap<>(3);
            uploadHeaders.put("Content-Type", MimeTypeUtils.APPLICATION_XML_VALUE);
            uploadHeaders.put("x-oos-date", dateFormat.format(now));
            uploadSignature(uploadHeaders, accessKeyId, accessKeySecret, HttpMethod.PUT.name(), resourcePath, uploadId, partNumber);
            singleUploadData.setUploadHeaders(uploadHeaders);
            uploadDataList.add(singleUploadData);
        }
        multipartUploadData.setUploadDataList(uploadDataList);

        CdnCompleteUploadData completeUploadData = new CdnCompleteUploadData();
        String uploadCompleteUrl = baseUploadUrl + SEPARATOR_SLASH + fileKey + "?uploadId=" + uploadId;
        completeUploadData.setUploadUrl(uploadCompleteUrl);
        Map<String, String> uploadCompleteHeaders = new LinkedHashMap<>(3);
        uploadCompleteHeaders.put(OSSHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_XML_VALUE);
        uploadCompleteHeaders.put("x-oos-date", dateFormat.format(now));
        uploadSignature(uploadCompleteHeaders, accessKeyId, accessKeySecret, HttpMethod.POST.name(), resourcePath, uploadId);
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
                                   String httpMethod, String resourcePath, String uploadId) {
        uploadSignature(headers, accessKeyId, accessKeySecret, httpMethod, resourcePath, uploadId, null);
    }

    protected void uploadSignature(Map<String, String> headers, String accessKeyId, String accessKeySecret,
                                   String httpMethod, String resourcePath, String uploadId, String partNumber) {
        if (partNumber != null) {
            resourcePath += FileConstants.QUESTION_MARK + RequestParameters.PART_NUMBER + FileConstants.EQUAL + partNumber;
        }
        resourcePath += resourcePath.contains(FileConstants.QUESTION_MARK) ? FileConstants.AND : FileConstants.QUESTION_MARK;
        resourcePath += RequestParameters.UPLOAD_ID + FileConstants.EQUAL + uploadId;
        try {
            AbstractAuthentication authentication = Constants.AUTHTICATION_MAP.get(AuthTypeEnum.OBS);
            String stringToSign = authentication.makeServiceCanonicalString(httpMethod, resourcePath, headers, null, Constants.ALLOWED_RESOURCE_PARAMTER_NAMES);
            String signature = "OBS " + accessKeyId + ":" + AbstractAuthentication.calculateSignature(stringToSign, accessKeySecret);
            headers.put("Authorization", signature);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
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
        ObsClient obsClient = getObsClient();
        String bucket = cdnConfig.getBucket();
        ObsObject object = obsClient.getObject(bucket, fileKey);
        return object != null ? object.getObjectContent() : null;
    }

    @Override
    public String fetchContent(String fileName) {
        try {
            CdnConfig cdnConfig = getCdnConfig();
            ObsClient obsClient = getObsClient();
            String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
            String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
            String bucket = cdnConfig.getBucket();
            ObsObject object = obsClient.getObject(bucket, fileKey);
            if (object != null) {
                InputStream inputStream = object.getObjectContent();
                String content = IOUtils.toString(inputStream, "UTF-8");
                inputStream.close();
                return content;
            }
        } catch (IOException e) {
            log.error("天翼云ZOS读取文件io异常", e);
        }
        return null;
    }

    @Override
    protected String appendPictureProcessParameters(String downloadUrl, String imageResizeParameter) {
        return downloadUrl;
    }

    @Override
    public void deleteByFolder(String folder) {
        CdnConfig cdnConfig = getCdnConfig();
        ObsClient obsClient = getObsClient();
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        String folderKey = cdnConfig.getMainDir() + keyPrefix + folder;
        if (!folderKey.endsWith(SEPARATOR_SLASH)) {
            folderKey += SEPARATOR_SLASH;
        }

        try {
            String bucket = cdnConfig.getBucket();
            obsClient.deleteObject(bucket, folderKey);
            log.info("deleteByFolder folder:[{}] 成功", folder);
        } catch (Exception e) {
            log.error("deleteByFolder folder:[{}] 失败", folder, e);
        }
    }

    @Override
    public void deleteByFilename(String fileName) {
        CdnConfig cdnConfig = getCdnConfig();
        ObsClient obsClient = getObsClient();
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
        String bucket = cdnConfig.getBucket();
        try {
            DeleteObjectResult deleteResult = obsClient.deleteObject(bucket, fileKey);
            if (deleteResult.getStatusCode() != 200) {
                log.error("删除文件失败: [{}], response:{}", fileName, JsonUtils.toJSONString(deleteResult));
                throw PamirsException.construct(FILE_CDN_RM_ERROR).errThrow();
            }
        } catch (ObsException exp) {
            log.error("删除文件发生异常: [{}]", fileName, exp);
            throw PamirsException.construct(FILE_CDN_RM_ERROR).errThrow();
        }
    }

    @Override
    public boolean isExistByFilename(String fileName) {
        CdnConfig cdnConfig = getCdnConfig();
        ObsClient obsClient = getObsClient();
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
        String bucket = cdnConfig.getBucket();
        return obsClient.doesObjectExist(bucket, fileKey);
    }

    @Override
    public String getStaticUrl() {
        CdnConfig cdnConfig = getCdnConfig();
        if (cdnConfig.getAppLogoUseCdn() != null && cdnConfig.getAppLogoUseCdn()) {
            return getBaseDownloadUrl();
        } else {
            return CdnConfig.defaultCdnUrl;
        }
    }
}

