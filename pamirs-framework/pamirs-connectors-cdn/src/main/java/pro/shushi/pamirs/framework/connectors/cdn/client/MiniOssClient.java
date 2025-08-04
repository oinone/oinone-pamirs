package pro.shushi.pamirs.framework.connectors.cdn.client;

import io.minio.DateFormat;
import io.minio.MinioClient;
import io.minio.PostPolicy;
import io.minio.Result;
import io.minio.http.Method;
import io.minio.messages.Item;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.cdn.configure.CdnConfig;
import pro.shushi.pamirs.framework.connectors.cdn.enmu.CDNExpEnum;
import pro.shushi.pamirs.framework.connectors.cdn.factory.CdnConfigRouter;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.*;
import pro.shushi.pamirs.framework.connectors.cdn.utils.MinioMultipart;
import pro.shushi.pamirs.framework.connectors.cdn.utils.URLHelper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.prefix.KeyPrefixManager;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static pro.shushi.pamirs.framework.connectors.cdn.utils.MinioMultipart.generateRequestHeader;
import static pro.shushi.pamirs.meta.common.constants.CharacterConstants.SEPARATOR_SLASH;

@Slf4j
@Order
@Component
@SPI.Service(MiniOssClient.TYPE)
public class MiniOssClient extends AbstractFileClient {

    public static final String TYPE = "MINIO";
    private static final String CONTENT_ENCODING = "Content-Encoding";
    private static final String CHUNKED = "aws-chunked";
    private static final String CONTENT_LENGTH = "x-amz-decoded-content-length";
    private static MinioClient DEFAULT_MINIO_CLIENT;

    private static final Map<String, MinioClient> MINIO_CLIENTS = new ConcurrentHashMap<>(2);

    private static final long MAX_OBJECT_SIZE = 5L * 1024 * 1024 * 1024 * 1024;
    // maxPartSize - maximum part size 5GiB for a single multipart upload operation
    private static final int MAX_MULTIPART_COUNT = 10000;
    // minimum allowed multipart size is 5MiB
    private static final int MIN_MULTIPART_SIZE = 5 * 1024 * 1024;

    // maximum allowed bucket policy size is 12KiB
    @Override
    protected String generatorFrontUploadFileKey(String filename) {
        CdnConfig cdnConfig = getCdnConfig();
        return getMinoFileKey(cdnConfig.getMainDir(), filename);
    }

    @Override
    protected String generatorFrontUploadUrl() {
        String uploadUrl = super.generatorFrontUploadUrl();
        if (!uploadUrl.endsWith(SEPARATOR_SLASH)) {
            uploadUrl = uploadUrl + SEPARATOR_SLASH;
        }
        return uploadUrl;
    }

    @Override
    protected Map<String, String> generatorFormData(String fileKey, CdnUploadFileRequest request) {
        CdnConfig cdnConfig = getCdnConfig();
        MinioClient minioClient = getMinioClient();
        String bucket = cdnConfig.getBucket();
        Long validTime = cdnConfig.getValidTime();
        try {
            PostPolicy postPolicy = new PostPolicy(bucket, fileKey, DateTime.now().plus(validTime));
            Map<String, String> formData = minioClient.presignedPostPolicy(postPolicy);
            formData.remove("bucket");
            formData.put("key", fileKey);
            return formData;
        } catch (Exception e) {
            throw PamirsException.construct(CDNExpEnum.MINIO_GET_FORM_DATA_ERROR, e).errThrow();
        }
    }

    @Override
    protected String getDefaultBaseDownloadUrl(CdnConfig cdnConfig) {
        return cdnConfig.getDownloadUrl() + SEPARATOR_SLASH + cdnConfig.getBucket();
    }

    @Override
    protected String getDefaultBaseUploadUrl(CdnConfig cdnConfig) {
        return cdnConfig.getUploadUrl() + SEPARATOR_SLASH + cdnConfig.getBucket();
    }

    @Override
    protected CdnMultipartUploadData generatorMultipartFileFormData(String fileKey, CdnUploadFileRequest request) {
        List<CdnChunkFile> chunkFiles = getChunkFiles(request);
        if (chunkFiles == null) {
            return null;
        }
        fileKey = URLHelper.encodeFileName(fileKey);
        CdnConfig cdnConfig = getCdnConfig();
        String accessKeyId = cdnConfig.getAccessKeyId();
        String accessKeySecret = cdnConfig.getAccessKeySecret();

        String date = new DateTime().toString(DateFormat.AMZ_DATE_FORMAT);
        String baseUploadUrl = getBaseUploadUrl();
        String uploadUrl = baseUploadUrl + SEPARATOR_SLASH + fileKey;

        //获取uploadId
        String uploadId = MinioMultipart.initMultipartUpload(baseUploadUrl, fileKey, accessKeyId, accessKeySecret, date);
        if (uploadId == null) {
            return null;
        }

        //构建分片上传参数
        CdnMultipartUploadData multipartUploadData = new CdnMultipartUploadData();
        List<CdnSingleUploadData> uploadDataList = new ArrayList<>();
        for (CdnChunkFile chunkFile : chunkFiles) {
            String partNumber = chunkFile.getPartNumber().toString();
            String fileSize = chunkFile.getFileSize().toString();
            CdnSingleUploadData singleUploadData = new CdnSingleUploadData();
            singleUploadData.setHttpMethod(Method.PUT.name());
            String url = String.format("%s?uploadId=%s&partNumber=%s", uploadUrl, uploadId, partNumber);
            singleUploadData.setUploadUrl(url);

            Map<String, String> uploadHeaders = new HashMap<>();
            uploadHeaders.put(CONTENT_ENCODING, CHUNKED);
            uploadHeaders.put(CONTENT_LENGTH, fileSize);
            try {
                generateRequestHeader(uploadHeaders, url, Method.PUT.toString(), accessKeyId, accessKeySecret, date);
            } catch (MalformedURLException | NoSuchAlgorithmException | InvalidKeyException e) {
                log.error("Minio 获取分片 生成请求头失败", e);
                return null;
            }
            singleUploadData.setUploadHeaders(uploadHeaders);
            uploadDataList.add(singleUploadData);
        }
        multipartUploadData.setUploadDataList(uploadDataList);

        //合并所有分片上传参数
        CdnCompleteUploadData completeUploadData = new CdnCompleteUploadData();
        String uploadCompleteUrl = baseUploadUrl + SEPARATOR_SLASH + fileKey + "?uploadId=" + uploadId;
        completeUploadData.setUploadUrl(uploadCompleteUrl);

        Map<String, String> uploadCompleteHeaders = new HashMap<>();
        try {
            generateRequestHeader(uploadCompleteHeaders, uploadCompleteUrl, Method.POST.toString(), accessKeyId, accessKeySecret, date);
        } catch (MalformedURLException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Minio 合并分片 生成请求头失败", e);
            return null;
        }
        completeUploadData.setUploadHeaders(uploadCompleteHeaders);
        completeUploadData.setUploadData("`<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n<CompleteMultipartUpload>\n" + "${parts}" + "</CompleteMultipartUpload>`");
        completeUploadData.setUploadPartData("`<Part>\n" + "<PartNumber>${partNumber}</PartNumber>\n" + "<ETag>${response.partKey}</ETag>\n" + "</Part>\n`");

        Map<String, String> uploadCompletePartContext = new HashMap<>();
        uploadCompletePartContext.put("partNumber", "partNumber");
        uploadCompletePartContext.put("response.partKey", "headers.etag");
        completeUploadData.setUploadPartContext(uploadCompletePartContext);
        multipartUploadData.setCompleteUploadData(completeUploadData);
        return multipartUploadData;
    }

    @Override
    public CdnFile upload(String fileName, byte[] data) {
        return upload(fileName, new ByteArrayInputStream(data));
    }

    @Override
    public CdnFile upload(String fileName, InputStream inputStream) {
        CdnConfig cdnConfig = getCdnConfig();
        MinioClient minioClient = getMinioClient();
        CdnFile cdnFile = new CdnFile();
        String fileKey = getMinoFileKey(cdnConfig.getMainDir(), fileName);
        String bucket = cdnConfig.getBucket();
        try {
            Map<String, String> headerMap = new HashMap<>();
            String contentType = MediaTypeFactory.getMediaType(fileName).orElse(MediaType.APPLICATION_OCTET_STREAM).toString();
            headerMap.put("Content-Type", contentType);
            minioClient.putObject(bucket, fileKey, inputStream, Long.valueOf(inputStream.available()), headerMap, null, contentType);
            String url = getBaseDownloadUrl() + SEPARATOR_SLASH + fileKey;
            cdnFile.setType(FILE_TYPE);
            cdnFile.setName(fileName);
            cdnFile.setUrl(url);
        } catch (Exception e) {
            log.error("MINIO文件上传服务出错!", e);
            try {
                inputStream.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return cdnFile;
    }

    @Override
    public String uploadByFileName(String fileName, byte[] data) {
        return uploadByFileName(fileName, new ByteArrayInputStream(data));
    }

    @Override
    public String uploadByFileName(String fileName, InputStream inputStream) {
        CdnConfig cdnConfig = getCdnConfig();
        MinioClient minioClient = getMinioClient();

        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
        String bucket = cdnConfig.getBucket();
        Map<String, String> headerMap = new HashMap<>();
        String contentType = MediaTypeFactory.getMediaType(fileName).orElse(MediaType.APPLICATION_OCTET_STREAM).toString();
        headerMap.put("Content-Type", contentType);
        try {
            minioClient.putObject(bucket, fileKey, inputStream, contentType);
            return getBaseDownloadUrl() + SEPARATOR_SLASH + fileKey;
        } catch (Exception e) {
            log.error("MINIO文件上传服务出错!", e);
        }
        return null;
    }

    @Override
    public String getDownloadUrl(String fileName) {
        CdnConfig cdnConfig = getCdnConfig();
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
        return generatorDownloadUrl(fileKey);
    }

    @Override
    public void deleteByFolder(String folder) {
        CdnConfig cdnConfig = getCdnConfig();
        MinioClient minioClient = getMinioClient();
        String folderKey = getMinoFolder(cdnConfig.getMainDir(), folder);
        String bucket = cdnConfig.getBucket();

        try {
            // 获取指定前缀下的所有对象
            Iterable<Result<Item>> objects = minioClient.listObjects(bucket, folderKey, true);

            // 遍历对象列表，获取对象名
            List<String> objectNames = new ArrayList<>();
            for (Result<Item> result : objects) {
                Item item = result.get();
                objectNames.add(item.objectName());
            }
            minioClient.removeObjects(bucket, objectNames);
            log.info("deleteByFolder folder:[{}] 成功", folder);
        } catch (Exception e) {
            log.error("deleteByFolder folder:[{" + folder + "}] 失败", e);
        }
    }

    @Override
    public InputStream getDownloadStream(String fileKey) {
        fileKey = prepareDownloadFileKey(fileKey);
        CdnConfig cdnConfig = getCdnConfig();
        MinioClient minioClient = getMinioClient();
        String bucket = cdnConfig.getBucket();
        String bucketPrefix = bucket + SEPARATOR_SLASH;
        if (fileKey.startsWith(bucketPrefix)) {
            fileKey = fileKey.substring(bucketPrefix.length());
        }
        try {
            return minioClient.getObject(bucket, fileKey);
        } catch (Throwable e) {
            log.error("MINIO读取文件IO异常", e);
        }
        return null;
    }

    @Override
    public String fetchContent(String fileName) {
        try {
            CdnConfig cdnConfig = getCdnConfig();
            MinioClient minioClient = getMinioClient();
            String bucket = cdnConfig.getBucket();
            String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
            String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
            InputStream inputStream = minioClient.getObject(bucket, fileKey);
            String content = IOUtils.toString(inputStream);
            inputStream.close();
            return content;
        } catch (Throwable e) {
            log.error("MINIO读取文件IO异常", e);
        }
        return null;
    }

    @Override
    public void deleteByFilename(String filename) {
        CdnConfig cdnConfig = getCdnConfig();
        MinioClient minioClient = getMinioClient();
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        String fileKey = cdnConfig.getMainDir() + keyPrefix + filename;
        String bucket = cdnConfig.getBucket();
        try {
            minioClient.removeObject(bucket, fileKey);
        } catch (Exception e) {
            log.error("MINIO文件上传服务出错!", e);
        }
    }

    @Override
    public boolean isExistByFilename(String filename) {
        CdnConfig cdnConfig = getCdnConfig();
        MinioClient minioClient = getMinioClient();
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        String fileKey = cdnConfig.getMainDir() + keyPrefix + filename;
        String bucket = cdnConfig.getBucket();
        try {
            minioClient.statObject(bucket, fileKey);
            return true;
        } catch (Exception e) {
            log.error("MINIO文件上传服务出错!", e);
        }
        return false;
    }

    /**
     * 静态文件只有路径/用于初始化文件放置的路径
     * <p>
     * 配置项 DownloadUrl 必须带 协议头：HTTP/HTTPS
     *
     * @return
     */
    @Override
    public String getStaticUrl() {
        CdnConfig cdnConfig = getCdnConfig();
        if (cdnConfig.getAppLogoUseCdn() != null && cdnConfig.getAppLogoUseCdn()) {
            return cdnConfig.getUploadUrl() + SEPARATOR_SLASH + cdnConfig.getBucket();
        } else {
            return CdnConfig.defaultCdnUrl;
        }
    }

    // 客户特定场景需要(目前有客户需要)，不要改变开放级别。
    // 正确的获取文件系统Client的方式：FileClientFactory.getClient()
    public MinioClient getMinioClient() {
        String routerKey = CdnConfigRouter.get();
        if (StringUtils.isBlank(routerKey)) {
            return DEFAULT_MINIO_CLIENT;
        }
        return MINIO_CLIENTS.computeIfAbsent(routerKey, (key) -> generatorMinioClient(getCdnConfig()));
    }

    protected MinioClient generatorMinioClient(CdnConfig cdnConfig) {
        MinioClient minioClient = null;
        String accessKeyId = cdnConfig.getAccessKeyId();
        String accessKeySecret = cdnConfig.getAccessKeySecret();
        String endpoint = cdnConfig.getUploadUrl();
        Long timeout = cdnConfig.getTimeout();
        try {
            minioClient = new MinioClient(endpoint, accessKeyId, accessKeySecret);
            minioClient.setTimeout(timeout, timeout, timeout);
            String bucketName = cdnConfig.getBucket();
            boolean isExist = minioClient.bucketExists(bucketName);
            if (!isExist) {
                minioClient.makeBucket(bucketName);
            }
        } catch (Exception e) {
            log.error("Minio 连接异常，bucketName: " + cdnConfig.getBucket() + "", e);
        }
        return minioClient;
    }

    @PostConstruct
    public void init() {
        CdnConfig cdnConfig = getCdnConfig();
        if (MiniOssClient.TYPE.equals(cdnConfig.getType())) {
            DEFAULT_MINIO_CLIENT = generatorMinioClient(cdnConfig);
        }
    }

    //FileKey不能以 / 开头，否则老版本的MINIO会有问题
    protected String getMinoFileKey(String mainDir, String filename) {
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        if (StringUtils.isNotBlank(keyPrefix)) {
            if (!keyPrefix.endsWith(SEPARATOR_SLASH)) {
                keyPrefix = keyPrefix + SEPARATOR_SLASH;
            }
        }
        return getFileDir(mainDir) + SEPARATOR_SLASH + keyPrefix + filename;
    }

    protected String getMinoFolder(String mainDir, String folder) {
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        if (StringUtils.isNotBlank(keyPrefix)) {
            if (!keyPrefix.endsWith(SEPARATOR_SLASH)) {
                keyPrefix = keyPrefix + SEPARATOR_SLASH;
            }
        }
        return getFileDir(mainDir) + SEPARATOR_SLASH + keyPrefix + folder;
    }
}

