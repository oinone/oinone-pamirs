package pro.shushi.pamirs.framework.connectors.cdn.client;

import com.upyun.*;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.cdn.configure.CdnConfig;
import pro.shushi.pamirs.framework.connectors.cdn.constant.FileConstants;
import pro.shushi.pamirs.framework.connectors.cdn.factory.CdnConfigRouter;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.CdnFile;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.CdnUploadFileRequest;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.prefix.KeyPrefixManager;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.util.JsonUtils;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.upyun.RestManager.PARAMS.X_UPYUN_FILE_SIZE;
import static pro.shushi.pamirs.framework.connectors.cdn.enmu.CDNExpEnum.*;
import static pro.shushi.pamirs.meta.common.constants.CharacterConstants.SEPARATOR_SLASH;

@Slf4j
@Order
@Component
@SPI.Service(UpyunOSSClient.TYPE)
public class UpyunOSSClient extends AbstractFileClient implements FileConstants {

    public static final String TYPE = "UPYUN";

    private static final String PICTURE_SUFFIX = "!800";

    private static RestManager DEFAULT_UPYUN_OSS_CLIENT;

    private static final Map<String, RestManager> UPYUN_OSS_CLIENTS = new ConcurrentHashMap<>(2);

    @Override
    protected String generatorFrontUploadFileKey(String filename) {
        CdnConfig cdnConfig = getCdnConfig();
        return getUpYunFileKey(cdnConfig.getMainDir(), filename);
    }

    @Override
    protected String generatorFrontUploadUrl() {
        CdnConfig cdnConfig = getCdnConfig();
        String bucket = cdnConfig.getBucket();
        return HTTPS + cdnConfig.getUploadUrl() + SEPARATOR_SLASH + bucket;
    }

    @Override
    protected Map<String, String> generatorFormData(String fileKey, CdnUploadFileRequest request) {
        CdnConfig cdnConfig = getCdnConfig();

        String bucket = cdnConfig.getBucket();
        String userName = cdnConfig.getAccessKeyId();
        String password = cdnConfig.getAccessKeySecret();
        Long validTime = cdnConfig.getValidTime();
        Map<String, Object> params = new LinkedHashMap<>();
        params.put(Params.BUCKET, bucket);
        params.put(Params.SAVE_KEY, fileKey);
        params.put(Params.EXPIRATION, (System.currentTimeMillis() + validTime) / 1000);

        String policy = UpYunUtils.getPolicy(params);
        String authorization = "";

        try {
            String raw = ("POST" + AND + SEPARATOR_SLASH + bucket + AND + policy).trim();
            byte[] hmac = UpYunUtils.calculateRFC2104HMACRaw(UpYunUtils.md5(password), raw);
            authorization = Base64Coder.encodeLines(hmac);
        } catch (SignatureException | NoSuchAlgorithmException | InvalidKeyException exp) {
            throw PamirsException.construct(CDN_UPYUN_SIGN_ERROR).errThrow();
        }

        Map<String, String> formData = new HashMap<>();
        formData.put("policy", policy);
        formData.put("authorization", "UPYUN " + userName + ":" + authorization.trim());
        formData.put("file", getUploadFilename(request));
        return formData;
    }

    @Override
    protected String appendPictureProcessParameters(String downloadUrl, String imageResizeParameter) {
        return downloadUrl + PICTURE_SUFFIX;
    }

    @Override
    public CdnFile upload(String fileName, byte[] data) {
        return upload(fileName, new ByteArrayInputStream(data));
    }

    @Override
    public CdnFile upload(String fileName, InputStream inputStream) {
        CdnConfig cdnConfig = getCdnConfig();
        RestManager restManager = getRestManager();
        String encodedFileName = null;
        try {
            // 又拍云不支持中文, 中文文件名需要encode
            encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ignored) {
        }

        String fileKey = getUpYunFileKey(cdnConfig.getMainDir(), encodedFileName);
        try (Response writeFileResponse = restManager.writeFile(fileKey, inputStream, null)) {
            if (!writeFileResponse.isSuccessful()) {
                log.error("上传upyun失败 文件名[{}] [{}]", fileName, writeFileResponse.headers());
                throw PamirsException.construct(CDN_UPYUN_UPLOAD_FILE_ERROR).errThrow();
            }
        } catch (IOException | UpException exp) {
            log.error("上传upyun异常 文件名[{}] Err", fileName, exp);
            throw PamirsException.construct(CDN_UPYUN_UPLOAD_FILE_ERROR).errThrow();
        }

        CdnFile resourceFile = new CdnFile();
        //获取文件大小
        try (Response response = restManager.getFileInfo(fileKey)) {
            Long size = Optional.ofNullable(response)
                    .filter(Response::isSuccessful)
                    .map(_response -> _response.header(X_UPYUN_FILE_SIZE.getValue()))
                    .filter(StringUtils::isNotBlank)
                    .map(Long::valueOf)
                    .orElse(0L);

            resourceFile.setName(fileName);
            resourceFile.setSize(size);
            resourceFile.setType(FILE_TYPE);
            resourceFile.setUrl(generatorDownloadUrl(fileKey));

        } catch (IOException | UpException exp) {
            log.error("上传upyun异常 文件名[{" + fileName + "}] Err", exp);
            throw PamirsException.construct(CDN_UPYUN_UPLOAD_FILE_FETCH_INFO_ERROR).errThrow();
        }

        return resourceFile;
    }

    @Override
    public String uploadByFileName(String fileName, byte[] data) {
        return uploadByFileName(fileName, new ByteArrayInputStream(data));
    }

    @Override
    public String uploadByFileName(String fileName, InputStream inputStream) {
        CdnConfig cdnConfig = getCdnConfig();
        RestManager restManager = getRestManager();
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
        try (Response writeFileResponse = restManager.writeFile(fileKey, inputStream, null)) {
            if (!writeFileResponse.isSuccessful()) {
                log.error("上传upyun失败 文件名[{}] [{}]", fileName, writeFileResponse.headers());
                throw PamirsException.construct(CDN_UPYUN_UPLOAD_FILE_ERROR).errThrow();
            }
            return generatorDownloadUrl(fileKey);
        } catch (IOException | UpException exp) {
            throw PamirsException.construct(CDN_UPYUN_UPLOAD_FILE_ERROR).errThrow();
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
        RestManager restManager = getRestManager();
        Response response;
        try {
            if (!fileKey.startsWith(SEPARATOR_SLASH)) {
                fileKey = SEPARATOR_SLASH + fileKey;
            }
            response = restManager.readFile(fileKey);
            if (response != null && response.isSuccessful() && response.body() != null) {
                return response.body().byteStream();
            }
        } catch (Throwable e) {
            log.error("UPYUN读取文件IO异常", e);
        }
        return null;
    }

    @Override
    public String fetchContent(String fileName) {
        CdnConfig cdnConfig = getCdnConfig();
        RestManager restManager = getRestManager();
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
        try (Response response = restManager.readFile(fileKey)) {
            if (null != response && response.isSuccessful() && null != response.body()) {
                try (InputStream inputStream = response.body().byteStream()) {
                    return IOUtils.toString(inputStream);
                }
            }
        } catch (IOException | UpException e) {
            log.error("upyun oss读取文件异常", e);
            throw PamirsException.construct(CDN_UPYUN_UPLOAD_FILE_FETCH_INFO_ERROR).errThrow();
        }
        return null;
    }

    @Override
    public void deleteByFolder(String folder) {
        if (StringUtils.isBlank(folder)) {
            return;
        }
        if (!StringUtils.startsWith(folder, SEPARATOR_SLASH)) {
            folder = SEPARATOR_SLASH + folder;
        }
        RestManager restManager = getRestManager();
        try (Response response = restManager.rmDir(folder)) {
            if (null == response || !response.isSuccessful()) {
                log.error("删除upyun文件夹失败: [{}]", folder);
                throw PamirsException.construct(CDN_UPYUN_RM_DIR_ERROR).errThrow();
            }
        } catch (IOException | UpException exp) {
            log.error("删除upyun文件夹发生异常: [{}]", folder);
            throw PamirsException.construct(CDN_UPYUN_RM_DIR_ERROR).errThrow();
        }
    }

    @Override
    public void deleteByFilename(String fileName) {
        CdnConfig cdnConfig = getCdnConfig();
        RestManager restManager = getRestManager();
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
        try (Response response = restManager.deleteFile(fileKey, null)) {
            if (!response.isSuccessful()) {
                log.error("删除upyun文件失败: [{}], response:{}", fileName, JsonUtils.toJSONString(response));
                throw PamirsException.construct(CDN_UPYUN_RM_FILE_ERROR).errThrow();
            }
        } catch (IOException | UpException exp) {
            log.error("删除upyun文件发生异常: [{" + fileName + "}]", exp);
            throw PamirsException.construct(CDN_UPYUN_RM_FILE_ERROR).errThrow();
        }
    }

    @Override
    public boolean isExistByFilename(String fileName) {
        CdnConfig cdnConfig = getCdnConfig();
        RestManager restManager = getRestManager();
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        String fileKey = cdnConfig.getMainDir() + keyPrefix + fileName;
        try (Response response = restManager.getFileInfo(fileKey)) {
            return response.isSuccessful();
        } catch (IOException | UpException exp) {
            return false;
        }
    }

    private String getUpYunFileKey(String mainDir, String filename) {
        String keyPrefix = KeyPrefixManager.generate(SEPARATOR_SLASH, SEPARATOR_SLASH);
        if (StringUtils.isBlank(keyPrefix)) {
            keyPrefix = SEPARATOR_SLASH;
        }
        return CharacterConstants.SEPARATOR_SLASH + getFileDir(mainDir) + keyPrefix + filename;
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
            return HTTPS + cdnConfig.getUploadUrl() + SEPARATOR_SLASH + cdnConfig.getBucket();
        } else {
            return CdnConfig.defaultCdnUrl;
        }
    }

    protected RestManager getRestManager() {
        String routerKey = CdnConfigRouter.get();
        if (StringUtils.isBlank(routerKey)) {
            return DEFAULT_UPYUN_OSS_CLIENT;
        }
        return UPYUN_OSS_CLIENTS.computeIfAbsent(routerKey, (key) -> generatorUpyunOSSClient(getCdnConfig()));
    }

    protected RestManager generatorUpyunOSSClient(CdnConfig cdnConfig) {
        Long connectTimeout = cdnConfig.getTimeout();
        String accessKeyId = cdnConfig.getAccessKeyId();
        String accessKeySecret = cdnConfig.getAccessKeySecret();
        String bucket = cdnConfig.getBucket();
        RestManager restManager = new RestManager(bucket, accessKeyId, accessKeySecret);
        restManager.debug = true;

        if (connectTimeout != null) {
            restManager.setTimeout(Math.toIntExact(connectTimeout / 1000));
        }
        return restManager;
    }

    @PostConstruct
    public void init() {
        CdnConfig cdnConfig = getCdnConfig();
        if (UpyunOSSClient.TYPE.equals(cdnConfig.getType())) {
            DEFAULT_UPYUN_OSS_CLIENT = generatorUpyunOSSClient(cdnConfig);
        }
    }
}
