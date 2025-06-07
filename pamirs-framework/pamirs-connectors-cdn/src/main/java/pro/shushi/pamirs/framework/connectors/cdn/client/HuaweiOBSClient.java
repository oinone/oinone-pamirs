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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import pro.shushi.pamirs.framework.connectors.cdn.configure.CdnConfig;
import pro.shushi.pamirs.framework.connectors.cdn.constant.FileConstants;
import pro.shushi.pamirs.framework.connectors.cdn.constant.HuaweiConstants;
import pro.shushi.pamirs.framework.connectors.cdn.factory.CdnConfigRouter;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.*;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.prefix.KeyPrefixManager;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.util.JsonUtils;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static pro.shushi.pamirs.framework.connectors.cdn.enmu.CDNExpEnum.FILE_CDN_RM_ERROR;
import static pro.shushi.pamirs.meta.common.constants.CharacterConstants.SEPARATOR_SLASH;

/**
 * HuaweiOBSClient
 *
 * @author wangxian on 2022/05/01
 */
@Slf4j
@Component
@SPI.Service(HuaweiOBSClient.TYPE)
public class HuaweiOBSClient extends AbstractFileClient implements FileConstants {

    public static final String TYPE = "HUAWEI_OBS";

    private static ObsClient DEFAULT_HW_OBS_CLIENT;

    private static final Map<String, ObsClient> HW_OBS_CLIENTS = new ConcurrentHashMap<>(2);

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
            uploadHeaders.put("x-obs-date", dateFormat.format(now));
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
        uploadCompleteHeaders.put("x-obs-date", dateFormat.format(now));
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


    /**
     * doc: https://support.huaweicloud.com/api-obs/obs_04_0012.html
     *
     * @param fileKey 文件存储Key
     * @param request 上传文件请求参数
     * @return FormData提交参数
     */
    @Override
    protected Map<String, String> generatorFormData(String fileKey, CdnUploadFileRequest request) {
        CdnConfig cdnConfig = getCdnConfig();
        ObsClient obsClient = getObsClient();

        PostSignatureRequest signatureRequest = new PostSignatureRequest();
        Map<String, Object> signatureFormParams = new HashMap<>();
        signatureFormParams.put(HttpHeaders.CONTENT_TYPE, getContentType(request));
        signatureFormParams.put(HuaweiConstants.X_OBS_ACL, Constants.ACL_PUBLIC_READ);
        signatureFormParams.put(FileConstants.SUCCESS_ACTION_STATUS, String.valueOf(HttpStatus.OK.value()));
        signatureRequest.setExpires(3600);
        signatureRequest.setFormParams(signatureFormParams);
        PostSignatureResponse signatureResponse = obsClient.createPostSignature(signatureRequest);

        Map<String, String> formData = new LinkedHashMap<>();
        formData.put(FileConstants.KEY, fileKey);
        signatureFormParams.forEach((key, value) -> formData.put(key, String.valueOf(value)));
        formData.put(FileConstants.POLICY, signatureResponse.getPolicy());
        formData.put(FileConstants.SIGNATURE, signatureResponse.getSignature());
        formData.put(FileConstants.ACCESS_KEY_ID, cdnConfig.getAccessKeyId());
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
    public CdnFile upload(String fileName, byte[] data) {
        CdnConfig cdnConfig = getCdnConfig();
        ObsClient obsClient = getObsClient();
        CdnFile resourceFile = new CdnFile();
        String fileKey = getFileKey(cdnConfig.getMainDir(), fileName);
        String bucket = cdnConfig.getBucket();
        //obsClient.putObject(bucket, fileKey, new ByteArrayInputStream(data));
        PutObjectRequest request = new PutObjectRequest(bucket, fileKey, new ByteArrayInputStream(data));
        request.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ);
        obsClient.putObject(request);
        //获取文件大小
        ObjectMetadata objectMeta = obsClient.getObjectMetadata(cdnConfig.getBucket(), fileKey);
        resourceFile.setName(fileName);
        resourceFile.setSize(objectMeta.getContentLength());
        resourceFile.setType(FILE_TYPE);
        resourceFile.setUrl(generatorDownloadUrl(fileKey));
        return resourceFile;
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
    public String getDownloadUrl(String fileName) {
        CdnConfig cdnConfig = getCdnConfig();
        String bucket = cdnConfig.getBucket();
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
        return generatorDownloadUrl(fileKey);
    }

    @Override
    public InputStream getDownloadStream(String fileKey) {
        CdnConfig cdnConfig = getCdnConfig();
        ObsClient obsClient = getObsClient();
        String bucket = cdnConfig.getBucket();
        ObsObject object = obsClient.getObject(bucket, fileKey);
        if (null != object) {
            return object.getObjectContent();
        }
        return null;
    }

    @Override
    public String fetchContent(String fileName) {
        try {
            CdnConfig cdnConfig = getCdnConfig();
            ObsClient obsClient = getObsClient();
            String bucket = cdnConfig.getBucket();
            String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
            String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
            ObsObject object = obsClient.getObject(bucket, fileKey);
            if (null != object) {
                InputStream inputStream = object.getObjectContent();
                String content = IOUtils.toString(inputStream);
                inputStream.close();
                return content;
            }
        } catch (IOException e) {
            log.error("华为云OBS读取文件io异常", e);
        }
        return null;
    }

    @Override
    public void deleteByFolder(String folder) {
        CdnConfig cdnConfig = getCdnConfig();
        ObsClient obsClient = getObsClient();
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        String folderKey = cdnConfig.getMainDir() + keyPrefix + folder;
        if (!keyPrefix.endsWith(SEPARATOR_SLASH)) {
            folderKey = folderKey + SEPARATOR_SLASH;
        }

        try {
            String bucket = cdnConfig.getBucket();
            // 删除目录，注意目录路径需要以斜杠结尾
            obsClient.deleteObject(bucket, folderKey);
            log.info("deleteByFolder folder:[{}] 成功", folder);
        } catch (Exception e) {
            log.error("deleteByFolder folder:[{" + folder + "}] 失败", e);
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
            log.error("删除文件发生异常: [{" + fileName + "}]", exp);
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

    protected ObsClient getObsClient() {
        String routerKey = CdnConfigRouter.get();
        if (StringUtils.isBlank(routerKey)) {
            return DEFAULT_HW_OBS_CLIENT;
        }
        return HW_OBS_CLIENTS.computeIfAbsent(routerKey, (key) -> generatorObsClient(getCdnConfig()));
    }

    protected ObsClient generatorObsClient(CdnConfig cdnConfig) {
        Long connectTimeout = cdnConfig.getTimeout();
        String accessKeyId = cdnConfig.getAccessKeyId();
        String accessKeySecret = cdnConfig.getAccessKeySecret();
        String endpoint = cdnConfig.getUploadUrl();
        endpoint = endpoint.replace(HTTP, CharacterConstants.SEPARATOR_EMPTY).replace(HTTPS, CharacterConstants.SEPARATOR_EMPTY);
        ObsConfiguration config = new ObsConfiguration();
        config.setSocketTimeout(30000);
        config.setConnectionTimeout(10000);
        config.setEndPoint(endpoint);
        config.setConnectionTimeout(connectTimeout.intValue());
        config.setSocketTimeout(connectTimeout.intValue());
        config.setMaxErrorRetry(0);
        config.setAuthType(AuthTypeEnum.OBS);
        /**
         String referer = cdnConfig.getReferer();
         if (StringUtils.isNotBlank(referer)) {
         config.getDefaultHeaders().put(HDEAER_REFERER, referer);
         }**/
        ObsClient obsClient = new ObsClient(accessKeyId, accessKeySecret, config);
        try {
            BucketCors cors = huaweiBucketCors(cdnConfig);
            if (cors != null) {
                obsClient.setBucketCors(cdnConfig.getBucket(), cors);
                obsClient.setBucketAcl(cdnConfig.getBucket(), AccessControlList.REST_CANNED_PUBLIC_READ);
            }
        } catch (Exception e) {
            log.warn("HuaweiOBS SET_CANNED_PUBLIC_READ ERROR", e);
        }
        return obsClient;
    }

    // https://support.huaweicloud.com/sdk-java-devg-obs/obs_21_1402.html
    private BucketCors huaweiBucketCors(CdnConfig cdnConfig) {
        if (StringUtils.isBlank(cdnConfig.getAllowedOrigin())) {
            return null;
        }
        BucketCors cors = new BucketCors();
        List<BucketCorsRule> rules = new ArrayList<BucketCorsRule>();
        BucketCorsRule rule = new BucketCorsRule();
        // 指定允许跨域请求的来源
        List<String> allowedOrigin = new ArrayList<String>();
        String allowedOriginCfg = cdnConfig.getAllowedOrigin();
        if (StringUtils.isNotBlank(allowedOriginCfg)) {
            allowedOrigin = Splitter.on(FileConstants.COMMA).splitToList(allowedOriginCfg);
            rule.setAllowedOrigin(allowedOrigin);
        }

        ArrayList<String> allowedMethod = new ArrayList<String>();
        // 指定允许的跨域请求方法(GET/PUT/DELETE/POST/HEAD)
        allowedMethod.add(RequestMethod.GET.name());
        allowedMethod.add(RequestMethod.PUT.name());
        allowedMethod.add(RequestMethod.DELETE.name());
        allowedMethod.add(RequestMethod.POST.name());
        allowedMethod.add(RequestMethod.HEAD.name());
        rule.setAllowedMethod(allowedMethod);

        ArrayList<String> allowedHeader = new ArrayList<String>();
        // 控制在OPTIONS预取指令中Access-Control-Request-Headers头中指定的header是否被允许使用
        allowedHeader.add("*");
        rule.setAllowedHeader(allowedHeader);
        ArrayList<String> exposeHeader = new ArrayList<String>();
        // 指定允许用户从应用程序中访问的header
        exposeHeader.add(HuaweiConstants.X_OBS_EXPOSE_HEADER);
        exposeHeader.add(HuaweiConstants.ETAG);
        exposeHeader.add(HuaweiConstants.CONTENT_LENGTH);

        rule.setExposeHeader(exposeHeader);
        // 指定浏览器对特定资源的预取(OPTIONS)请求返回结果的缓存时间,单位为秒
        rule.setMaxAgeSecond(10);
        rules.add(rule);
        cors.setRules(rules);
        return cors;
    }

    @PostConstruct
    public void init() {
        CdnConfig cdnConfig = getCdnConfig();
        if (HuaweiOBSClient.TYPE.equals(cdnConfig.getType())) {
            DEFAULT_HW_OBS_CLIENT = generatorObsClient(cdnConfig);
        }
    }

}
