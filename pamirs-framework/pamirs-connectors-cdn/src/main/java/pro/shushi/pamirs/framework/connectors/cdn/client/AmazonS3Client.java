//package pro.shushi.pamirs.framework.connectors.cdn.client;
//
//import org.apache.commons.collections4.CollectionUtils;
//import org.apache.commons.io.IOUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.MediaType;
//import org.springframework.http.MediaTypeFactory;
//import org.springframework.stereotype.Component;
//import pro.shushi.pamirs.framework.connectors.cdn.configure.CdnConfig;
//import pro.shushi.pamirs.framework.connectors.cdn.constant.FileConstants;
//import pro.shushi.pamirs.framework.connectors.cdn.factory.CdnConfigRouter;
//import pro.shushi.pamirs.framework.connectors.cdn.pojo.*;
//import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
//import pro.shushi.pamirs.meta.api.prefix.KeyPrefixManager;
//import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
//import pro.shushi.pamirs.meta.common.spi.SPI;
//import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
//import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
//import software.amazon.awssdk.core.ResponseInputStream;
//import software.amazon.awssdk.core.sync.RequestBody;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.S3ClientBuilder;
//import software.amazon.awssdk.services.s3.model.*;
//import software.amazon.awssdk.services.s3.presigner.S3Presigner;
//import software.amazon.awssdk.services.s3.presigner.model.CompleteMultipartUploadPresignRequest;
//import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest;
//
//import jakarta.annotation.PostConstruct;
//import javax.crypto.Mac;
//import javax.crypto.spec.SecretKeySpec;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URI;
//import java.nio.charset.StandardCharsets;
//import java.time.Duration;
//import java.time.ZoneOffset;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//
//import static pro.shushi.pamirs.meta.common.constants.CharacterConstants.SEPARATOR_SLASH;
//
///**
// * Amazon S3 对象存储客户端实现
// *
// * <h3>区域与端点解析优先级：</h3>
// * <ol>
// *   <li>优先使用配置项 {@code cdn.oss.region}（显式指定）</li>
// *   <li>次之从 {@code cdn.oss.uploadUrl} 中解析 region</li>
// *   <li>兜底返回 {@code us-east-1}</li>
// * </ol>
// *
// * <h3>配置示例（application.yml）：</h3>
// * <pre>
// * cdn:
// *   oss:
// *     type: S3
// *     bucket: your-bucket-name
// *     region: ap-southeast-1
// *     accessKeyId: AKIAIOSFODNN7EXAMPLE
// *     accessKeySecret: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
// *     mainDir: uploads/
// *     validTime: 3600000
// *     timeout: 30000
// *     appLogoUseCdn: true
// *     # uploadUrl/downloadUrl 可不填，自动构建为 https://{bucket}.s3.{region}.amazonaws.com
// *     # 如需覆盖，提供完整 URL（含协议+bucket），两者语义完全一致
// *     # uploadUrl: https://your-bucket-name.s3.ap-southeast-1.amazonaws.com
// *     # downloadUrl: https://your-bucket-name.s3.ap-southeast-1.amazonaws.com  # 或自定义CDN: https://files.example.com
// *     others:
// *       tenantA-s3:
// *         type: S3
// *         bucket: tenantA-bucket
// *         region: ap-southeast-1
// *         accessKeyId: tenantA-access-key
// *         accessKeySecret: tenantA-secret-key
// *         mainDir: tenantA/
// * </pre>
// */
//@Slf4j
////@Order
////@Component
////@SPI.Service(AmazonS3Client.TYPE)
//public class AmazonS3Client extends AbstractFileClient implements FileConstants {
//
//    public static final String TYPE = "S3";
//
//    private static S3Client DEFAULT_S3_CLIENT;
//    private static S3Presigner DEFAULT_S3_PRESIGNER;
//
//    private static final Map<String, S3Client> S3_CLIENTS = new ConcurrentHashMap<>(2);
//    private static final Map<String, S3Presigner> S3_PRESIGNERS = new ConcurrentHashMap<>(2);
//
//    @Override
//    protected Map<String, String> generatorFormData(String fileKey, CdnUploadFileRequest request) {
//        CdnConfig cdnConfig = getCdnConfig();
//        String bucket = cdnConfig.getBucket();
//        String accessKeyId = cdnConfig.getAccessKeyId();
//        String accessKeySecret = cdnConfig.getAccessKeySecret();
//        String region = resolveRegion(cdnConfig);
//        Long validTime = cdnConfig.getValidTime();
//
//        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
//        String dateStamp = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//        String amzDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));
//        String credential = accessKeyId + "/" + dateStamp + "/" + region + "/s3/aws4_request";
//
//        String expiration = now.plusNanos(validTime * 1_000_000L)
//                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
//
//        String contentType = getContentType(request, "application/octet-stream");
//
//        String policy = "{\"expiration\":\"" + expiration + "\","
//                + "\"conditions\":["
//                + "{\"bucket\":\"" + bucket + "\"},"
//                + "[\"starts-with\",\"$key\",\"\"],"
//                + "{\"x-amz-credential\":\"" + credential + "\"},"
//                + "{\"x-amz-algorithm\":\"AWS4-HMAC-SHA256\"},"
//                + "{\"x-amz-date\":\"" + amzDate + "\"},"
//                + "[\"starts-with\",\"$Content-Type\",\"\"],"
//                + "{\"success_action_status\":\"200\"}"
//                + "]}";
//
//        String encodedPolicy = Base64.getEncoder().encodeToString(policy.getBytes(StandardCharsets.UTF_8));
//        String signature = computeSignature(dateStamp, region, accessKeySecret, encodedPolicy);
//
//        Map<String, String> formData = new LinkedHashMap<>();
//        formData.put("key", fileKey);
//        formData.put("Content-Type", contentType);
//        formData.put("x-amz-credential", credential);
//        formData.put("x-amz-algorithm", "AWS4-HMAC-SHA256");
//        formData.put("x-amz-date", amzDate);
//        formData.put("policy", encodedPolicy);
//        formData.put("x-amz-signature", signature);
//        formData.put("success_action_status", "200");
//        return formData;
//    }
//
//    private String computeSignature(String dateStamp, String region, String secretKey, String encodedPolicy) {
//        byte[] dateKey = hmacSha256(("AWS4" + secretKey).getBytes(StandardCharsets.UTF_8), dateStamp);
//        byte[] regionKey = hmacSha256(dateKey, region);
//        byte[] serviceKey = hmacSha256(regionKey, "s3");
//        byte[] signingKey = hmacSha256(serviceKey, "aws4_request");
//        byte[] signature = hmacSha256(signingKey, encodedPolicy);
//        return hexEncode(signature);
//    }
//
//    private byte[] hmacSha256(byte[] key, String data) {
//        try {
//            Mac mac = Mac.getInstance("HmacSHA256");
//            mac.init(new SecretKeySpec(key, "HmacSHA256"));
//            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to compute HMAC-SHA256", e);
//        }
//    }
//
//    private String hexEncode(byte[] bytes) {
//        StringBuilder sb = new StringBuilder(bytes.length * 2);
//        for (byte b : bytes) {
//            sb.append(String.format("%02x", b));
//        }
//        return sb.toString();
//    }
//
//    @Override
//    protected CdnMultipartUploadData generatorMultipartFileFormData(String fileKey, CdnUploadFileRequest request) {
//        List<CdnChunkFile> chunkFiles = getChunkFiles(request);
//        if (chunkFiles == null) {
//            return null;
//        }
//
//        CdnConfig cdnConfig = getCdnConfig();
//        S3Client s3Client = getS3Client();
//        S3Presigner presigner = getS3Presigner();
//        String bucket = cdnConfig.getBucket();
//        Long validTime = cdnConfig.getValidTime();
//
//        CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
//                .bucket(bucket)
//                .key(fileKey)
//                .contentType(getContentType(request))
//                .build();
//
//        CreateMultipartUploadResponse createResponse = s3Client.createMultipartUpload(createRequest);
//        String uploadId = createResponse.uploadId();
//
//        CdnMultipartUploadData multipartUploadData = new CdnMultipartUploadData();
//        List<CdnSingleUploadData> uploadDataList = new ArrayList<>();
//
//        for (CdnChunkFile chunkFile : chunkFiles) {
//            int partNumber = chunkFile.getPartNumber();
//            UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
//                    .bucket(bucket)
//                    .key(fileKey)
//                    .uploadId(uploadId)
//                    .partNumber(partNumber)
//                    .build();
//
//            UploadPartPresignRequest partPresignRequest = UploadPartPresignRequest.builder()
//                    .signatureDuration(Duration.ofMillis(validTime))
//                    .uploadPartRequest(uploadPartRequest)
//                    .build();
//
//            String partUrl = presigner.presignUploadPart(partPresignRequest).url().toString();
//
//            CdnSingleUploadData singleUploadData = new CdnSingleUploadData();
//            singleUploadData.setHttpMethod("PUT");
//            singleUploadData.setUploadUrl(partUrl);
//            Map<String, String> uploadHeaders = new LinkedHashMap<>();
//            uploadHeaders.put("Content-Length", chunkFile.getFileSize().toString());
//            singleUploadData.setUploadHeaders(uploadHeaders);
//            uploadDataList.add(singleUploadData);
//        }
//        multipartUploadData.setUploadDataList(uploadDataList);
//
//        CdnCompleteUploadData completeUploadData = new CdnCompleteUploadData();
//        CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest.builder()
//                .bucket(bucket)
//                .key(fileKey)
//                .uploadId(uploadId)
//                .build();
//
//        CompleteMultipartUploadPresignRequest completePresignRequest = CompleteMultipartUploadPresignRequest.builder()
//                .signatureDuration(Duration.ofMillis(validTime))
//                .completeMultipartUploadRequest(completeRequest)
//                .build();
//
//        String completeUrl = presigner.presignCompleteMultipartUpload(completePresignRequest).url().toString();
//        completeUploadData.setHttpMethod("POST");
//        completeUploadData.setUploadUrl(completeUrl);
//        completeUploadData.setUploadData("`<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<CompleteMultipartUpload>\n" +
//                "${parts}" +
//                "</CompleteMultipartUpload>`");
//        completeUploadData.setUploadPartData("`<Part>\n" +
//                "<PartNumber>${partNumber}</PartNumber>\n" +
//                "<ETag>${response.partKey}</ETag>\n" +
//                "</Part>\n`");
//        Map<String, String> uploadCompletePartContext = new HashMap<>();
//        uploadCompletePartContext.put("partNumber", "partNumber");
//        uploadCompletePartContext.put("response.partKey", "headers.etag");
//        completeUploadData.setUploadPartContext(uploadCompletePartContext);
//        multipartUploadData.setCompleteUploadData(completeUploadData);
//
//        return multipartUploadData;
//    }
//
//    @Override
//    public CdnFile upload(String fileName, byte[] data) {
//        return upload(fileName, new ByteArrayInputStream(data));
//    }
//
//    @Override
//    public CdnFile upload(String fileName, InputStream inputStream) {
//        CdnConfig cdnConfig = getCdnConfig();
//        S3Client s3Client = getS3Client();
//        CdnFile resourceFile = new CdnFile();
//        String fileKey = getFileKey(cdnConfig.getMainDir(), fileName);
//        String bucket = cdnConfig.getBucket();
//
//        try {
//            byte[] data = IOUtils.toByteArray(inputStream);
//            String contentType = MediaTypeFactory.getMediaType(fileName)
//                    .orElse(MediaType.APPLICATION_OCTET_STREAM).toString();
//
//            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                    .bucket(bucket)
//                    .key(fileKey)
//                    .contentType(contentType)
//                    .build();
//
//            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(data));
//
//            resourceFile.setName(fileName);
//            resourceFile.setSize((long) data.length);
//            resourceFile.setType(FILE_TYPE);
//            resourceFile.setUrl(generatorDownloadUrl(fileKey));
//        } catch (IOException e) {
//            log.error("Amazon S3 file upload IO exception", e);
//            throw new RuntimeException("Failed to upload file to S3", e);
//        }
//        return resourceFile;
//    }
//
//    @Override
//    public String uploadByFileName(String fileName, byte[] data) {
//        return uploadByFileName(fileName, new ByteArrayInputStream(data));
//    }
//
//    @Override
//    public String uploadByFileName(String fileName, InputStream inputStream) {
//        CdnConfig cdnConfig = getCdnConfig();
//        S3Client s3Client = getS3Client();
//        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
//        String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
//        String bucket = cdnConfig.getBucket();
//
//        try {
//            byte[] data = IOUtils.toByteArray(inputStream);
//            String contentType = MediaTypeFactory.getMediaType(fileName)
//                    .orElse(MediaType.APPLICATION_OCTET_STREAM).toString();
//
//            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                    .bucket(bucket)
//                    .key(fileKey)
//                    .contentType(contentType)
//                    .build();
//
//            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(data));
//            return generatorDownloadUrl(fileKey);
//        } catch (IOException e) {
//            log.error("Amazon S3 file upload by fileName IO exception", e);
//            throw new RuntimeException("Failed to upload file to S3", e);
//        }
//    }
//
//    @Override
//    public String getDownloadUrl(String fileName) {
//        CdnConfig cdnConfig = getCdnConfig();
//        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
//        String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
//        return generatorDownloadUrl(fileKey);
//    }
//
//    @Override
//    public InputStream getDownloadStream(String fileKey) {
//        fileKey = prepareDownloadFileKey(fileKey);
//        CdnConfig cdnConfig = getCdnConfig();
//        S3Client s3Client = getS3Client();
//
//        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
//                .bucket(cdnConfig.getBucket())
//                .key(fileKey)
//                .build();
//
//        try {
//            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest);
//            return response;
//        } catch (NoSuchKeyException e) {
//            log.warn("Amazon S3 file not found: {}", fileKey);
//            return null;
//        } catch (Exception e) {
//            log.error("Amazon S3 get download stream error", e);
//            return null;
//        }
//    }
//
//    @Override
//    public String fetchContent(String fileName) {
//        try {
//            CdnConfig cdnConfig = getCdnConfig();
//            S3Client s3Client = getS3Client();
//            String bucket = cdnConfig.getBucket();
//            String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
//            String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
//
//            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
//                    .bucket(bucket)
//                    .key(fileKey)
//                    .build();
//
//            try (ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest)) {
//                return IOUtils.toString(response);
//            }
//        } catch (NoSuchKeyException e) {
//            log.warn("Amazon S3 file not found: {}", fileName);
//            return null;
//        } catch (IOException e) {
//            log.error("Amazon S3 read file IO exception", e);
//            return null;
//        } catch (Exception e) {
//            log.error("Amazon S3 fetch content error", e);
//            return null;
//        }
//    }
//
//    @Override
//    public void deleteByFolder(String folder) {
//        CdnConfig cdnConfig = getCdnConfig();
//        S3Client s3Client = getS3Client();
//        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
//        String folderKey = cdnConfig.getMainDir() + keyPrefix + folder;
//        String bucket = cdnConfig.getBucket();
//
//        try {
//            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
//                    .bucket(bucket)
//                    .prefix(folderKey)
//                    .build();
//
//            ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);
//            List<S3Object> s3Objects = listResponse.contents();
//
//            if (CollectionUtils.isNotEmpty(s3Objects)) {
//                List<ObjectIdentifier> objectIdentifiers = new ArrayList<>(s3Objects.size());
//                for (S3Object s3Object : s3Objects) {
//                    objectIdentifiers.add(ObjectIdentifier.builder().key(s3Object.key()).build());
//                }
//
//                DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
//                        .bucket(bucket)
//                        .delete(Delete.builder().objects(objectIdentifiers).build())
//                        .build();
//
//                s3Client.deleteObjects(deleteRequest);
//            }
//        } catch (Exception e) {
//            log.error("Amazon S3 delete by folder error", e);
//            throw new RuntimeException("Failed to delete folder from S3", e);
//        }
//    }
//
//    @Override
//    public void deleteByFilename(String fileName) {
//        CdnConfig cdnConfig = getCdnConfig();
//        S3Client s3Client = getS3Client();
//        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
//        String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
//        String bucket = cdnConfig.getBucket();
//
//        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
//                .bucket(bucket)
//                .key(fileKey)
//                .build();
//
//        s3Client.deleteObject(deleteRequest);
//    }
//
//    @Override
//    public boolean isExistByFilename(String fileName) {
//        CdnConfig cdnConfig = getCdnConfig();
//        S3Client s3Client = getS3Client();
//        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
//        String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
//        String bucket = cdnConfig.getBucket();
//
//        try {
//            HeadObjectRequest headRequest = HeadObjectRequest.builder()
//                    .bucket(bucket)
//                    .key(fileKey)
//                    .build();
//
//            s3Client.headObject(headRequest);
//            return true;
//        } catch (NoSuchKeyException e) {
//            return false;
//        } catch (Exception e) {
//            log.error("Amazon S3 check file existence error", e);
//            return false;
//        }
//    }
//
//    @Override
//    public String getStaticUrl() {
//        CdnConfig cdnConfig = getCdnConfig();
//        if (cdnConfig.getAppLogoUseCdn() != null && cdnConfig.getAppLogoUseCdn()) {
//            return getBaseDownloadUrl();
//        } else {
//            return CdnConfig.defaultCdnUrl;
//        }
//    }
//
//    @Override
//    protected String getDefaultBaseDownloadUrl(CdnConfig cdnConfig) {
//        String downloadUrl = cdnConfig.getDownloadUrl();
//        if (StringUtils.isNotBlank(downloadUrl)) {
//            // 已含协议前缀：直接使用
//            if (downloadUrl.startsWith(HTTP) || downloadUrl.startsWith(HTTPS)) {
//                return downloadUrl;
//            }
//            // 裸域名：补全协议
//            return HTTPS + downloadUrl;
//        }
//        // 未配置：从 bucket + region 自动构建
//        String region = resolveRegion(cdnConfig);
//        return HTTPS + cdnConfig.getBucket() + ".s3." + region + ".amazonaws.com";
//    }
//
//    @Override
//    protected String getDefaultBaseUploadUrl(CdnConfig cdnConfig) {
//        String uploadUrl = cdnConfig.getUploadUrl();
//        if (StringUtils.isNotBlank(uploadUrl)) {
//            // 已含协议前缀：直接使用
//            if (uploadUrl.startsWith(HTTP) || uploadUrl.startsWith(HTTPS)) {
//                return uploadUrl;
//            }
//            // 裸域名：补全协议
//            return HTTPS + uploadUrl;
//        }
//        // 未配置：从 bucket + region 自动构建
//        String region = resolveRegion(cdnConfig);
//        return HTTPS + cdnConfig.getBucket() + ".s3." + region + ".amazonaws.com";
//    }
//
//    private String stripProtocol(String url) {
//        if (StringUtils.isBlank(url)) {
//            return url;
//        }
//        return url.replace(HTTP, CharacterConstants.SEPARATOR_EMPTY)
//                .replace(HTTPS, CharacterConstants.SEPARATOR_EMPTY);
//    }
//
//    /**
//     * 解析 S3 区域：优先使用配置项 region，次之从 uploadUrl 解析，兜底 us-east-1
//     */
//    private String resolveRegion(CdnConfig cdnConfig) {
//        if (StringUtils.isNotBlank(cdnConfig.getRegion())) {
//            return cdnConfig.getRegion();
//        }
//        return extractRegionFromUrl(cdnConfig.getUploadUrl());
//    }
//
//    /**
//     * 从 S3 endpoint URL 中提取 region
//     * 支持格式：
//     * - s3.{region}.amazonaws.com
//     * - {bucket}.s3.{region}.amazonaws.com
//     */
//    private String extractRegionFromUrl(String endpoint) {
//        if (StringUtils.isBlank(endpoint)) {
//            return "us-east-1";
//        }
//        String url = stripProtocol(endpoint);
//        // 格式: s3.{region}.amazonaws.com
//        if (url.startsWith("s3.")) {
//            int endIndex = url.indexOf(".amazonaws.com");
//            if (endIndex > 3) {
//                return url.substring(3, endIndex);
//            }
//        }
//        // 格式: {bucket}.s3.{region}.amazonaws.com
//        int s3DotIndex = url.indexOf(".s3.");
//        if (s3DotIndex >= 0) {
//            int startIndex = s3DotIndex + 4;
//            int endIndex = url.indexOf(".amazonaws.com");
//            if (endIndex > startIndex) {
//                return url.substring(startIndex, endIndex);
//            }
//        }
//        return "us-east-1";
//    }
//
//    /**
//     * 解析 S3 服务端点 URI（用于 SDK endpointOverride）
//     * <p>注意：这里使用的是服务端点 s3.{region}.amazonaws.com，
//     * SDK 会自动处理 virtual-hosted-style（将 bucket 作为子域名）</p>
//     */
//    private URI resolveServiceEndpoint(CdnConfig cdnConfig) {
//        String uploadUrl = cdnConfig.getUploadUrl();
//        if (StringUtils.isNotBlank(uploadUrl)) {
//            String stripped = stripProtocol(uploadUrl);
//            // 如果是 bucket-style URL，转换为 service endpoint
//            // 举例: bucket.s3.region.amazonaws.com -> s3.region.amazonaws.com
//            int s3DotIndex = stripped.indexOf(".s3.");
//            if (s3DotIndex >= 0) {
//                stripped = stripped.substring(s3DotIndex + 1); // s3.region.amazonaws.com
//            }
//            return URI.create(HTTPS + stripped);
//        }
//        // 没有 uploadUrl，用 region 构建标准 endpoint
//        String region = resolveRegion(cdnConfig);
//        return URI.create(HTTPS + "s3." + region + ".amazonaws.com");
//    }
//
//    protected S3Client getS3Client() {
//        String routerKey = CdnConfigRouter.get();
//        if (StringUtils.isBlank(routerKey)) {
//            return DEFAULT_S3_CLIENT;
//        }
//        return S3_CLIENTS.computeIfAbsent(routerKey, (key) -> generatorS3Client(getCdnConfig()));
//    }
//
//    protected S3Presigner getS3Presigner() {
//        String routerKey = CdnConfigRouter.get();
//        if (StringUtils.isBlank(routerKey)) {
//            return DEFAULT_S3_PRESIGNER;
//        }
//        return S3_PRESIGNERS.computeIfAbsent(routerKey, (key) -> generatorS3Presigner(getCdnConfig()));
//    }
//
//    protected S3Client generatorS3Client(CdnConfig cdnConfig) {
//        String accessKeyId = cdnConfig.getAccessKeyId();
//        String accessKeySecret = cdnConfig.getAccessKeySecret();
//        Region region = Region.of(resolveRegion(cdnConfig));
//        URI serviceEndpoint = resolveServiceEndpoint(cdnConfig);
//
//        S3ClientBuilder builder = S3Client.builder().region(region)
//                .credentialsProvider(StaticCredentialsProvider.create(
//                        AwsBasicCredentials.create(accessKeyId, accessKeySecret)
//                ))
//                .endpointOverride(serviceEndpoint);
//
//        Long timeout = cdnConfig.getTimeout();
//        if (timeout != null) {
//            builder.overrideConfiguration(config -> config
//                    .apiCallTimeout(Duration.ofMillis(timeout))
//                    .apiCallAttemptTimeout(Duration.ofMillis(timeout))
//            );
//        }
//
//        return builder.build();
//    }
//
//    protected S3Presigner generatorS3Presigner(CdnConfig cdnConfig) {
//        String accessKeyId = cdnConfig.getAccessKeyId();
//        String accessKeySecret = cdnConfig.getAccessKeySecret();
//        Region region = Region.of(resolveRegion(cdnConfig));
//        URI serviceEndpoint = resolveServiceEndpoint(cdnConfig);
//
//        return S3Presigner.builder().region(region)
//                .credentialsProvider(StaticCredentialsProvider.create(
//                        AwsBasicCredentials.create(accessKeyId, accessKeySecret)
//                ))
//                .endpointOverride(serviceEndpoint)
//                .build();
//    }
//
//    @PostConstruct
//    public void init() {
//        CdnConfig cdnConfig = getCdnConfig();
//        if (AmazonS3Client.TYPE.equals(cdnConfig.getType())) {
//            DEFAULT_S3_CLIENT = generatorS3Client(cdnConfig);
//            DEFAULT_S3_PRESIGNER = generatorS3Presigner(cdnConfig);
//        }
//    }
//}
