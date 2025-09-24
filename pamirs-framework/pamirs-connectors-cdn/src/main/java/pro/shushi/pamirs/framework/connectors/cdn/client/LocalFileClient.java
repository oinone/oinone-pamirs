package pro.shushi.pamirs.framework.connectors.cdn.client;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import pro.shushi.pamirs.framework.connectors.cdn.configure.CdnConfig;
import pro.shushi.pamirs.framework.connectors.cdn.constant.FileConstants;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.CdnFile;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.CdnFileForm;
import pro.shushi.pamirs.framework.connectors.cdn.spi.CdnFileNameApi;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static pro.shushi.pamirs.meta.common.constants.CharacterConstants.SEPARATOR_SLASH;

@Slf4j
@Order
@Component
@SPI.Service(LocalFileClient.TYPE)
@RestController
@RequestMapping("/file")
public class LocalFileClient extends AbstractFileClient implements FileConstants {

    public static final String TYPE = "LOCAL";

    @Override
    public CdnFileForm getFormData(String fileName) {
        CdnConfig cdnConfig = getCdnConfig();
        CdnFileForm fileForm = new CdnFileForm();
        String uniqueFileName = Spider.getDefaultExtension(CdnFileNameApi.class).getNewFilename(fileName);
        String fileKey = getFileKey(cdnConfig.getMainDir(), uniqueFileName);

        //前端获取uploadUrl,上传文件到该地址
        fileForm.setUploadUrl(cdnConfig.getUploadUrl() + "/file/upload");
        //上传后,前端将downloadUrl返回给后端
        fileForm.setDownloadUrl(getDownloadUrl(fileKey));
        fileForm.setFileName(uniqueFileName);
        Map<String, Object> formDataJson = new HashMap<>();
        formDataJson.put("uniqueFileName", uniqueFileName);
        formDataJson.put("key", fileKey);
        fileForm.setFormDataJson(JSON.toJSONString(formDataJson));
        return fileForm;
    }

    @ResponseBody
    @RequestMapping(value = "/upload", produces = "multipart/form-data;charset=UTF-8", method = RequestMethod.POST)
    public String uploadFileToLocal(HttpServletRequest request) {
        String uniqueFileName = request.getParameter("uniqueFileName");
        //将文件缓冲到本地
        MultipartFile file = ((StandardMultipartHttpServletRequest) request).getFile("file");
        return createLocalFile(file, uniqueFileName);
    }

    @RequestMapping(value = "/static/{uniqueFileName}", method = RequestMethod.GET)
    public void getStaticFile(@PathVariable("uniqueFileName") String uniqueFileName,
                              HttpServletRequest request, HttpServletResponse response) throws IOException {
        CdnConfig cdnConfig = getCdnConfig();
        String filePath = cdnConfig.getLocalFolderUrl() + "/" + uniqueFileName;
        File localFile = new File(filePath);
        if (!localFile.exists()) {
            log.warn("[getFileContent][path({}) 文件不存在]", filePath);
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        // 读取内容
        byte[] content = FileUtils.readFileToByteArray(localFile);
        // 设置 header 和 contentType
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(uniqueFileName, StandardCharsets.UTF_8));
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        // 输出附件
        IOUtils.write(content, response.getOutputStream());
    }

    /**
     * 通过上传的文件名，缓冲到本地
     *
     * @param file
     */
    private String createLocalFile(MultipartFile file, String uniqueFileName) {
        CdnConfig cdnConfig = getCdnConfig();
        String fileKey = getFileKey(cdnConfig.getMainDir(), uniqueFileName);
        try {
            InputStream in = file.getInputStream();
            _createLocalFile(in, fileKey);
            return getDownloadUrl(fileKey);
        } catch (Exception e) {
            log.error("createLocalFile 文件上传服务出错!", e);
            return null;
        }
    }

    @Override
    public String uploadByFileName(String fileName, byte[] data) {
        return uploadByFileName(fileName, new ByteArrayInputStream(data));
    }

    @Override
    public String uploadByFileName(String fileName, InputStream inputStream) {
        try {
            _createLocalFile(inputStream, fileName);
            return getDownloadUrl(fileName);
        } catch (Exception e) {
            log.error("uploadByFileName 文件上传服务出错!", e);
            return null;
        }
    }

    @Override
    public void deleteByFolder(String folder) {
        if (StringUtils.isBlank(folder)) {
            return;
        }

        CdnConfig cdnConfig = getCdnConfig();
        String filePath = cdnConfig.getLocalFolderUrl();
        if (!filePath.endsWith(SEPARATOR_SLASH)) {
            filePath = filePath + SEPARATOR_SLASH;
        }
        folder = filePath + folder;
        File fileFolder = new File(folder);
        deleteFolder(fileFolder);

        log.info("deleteByFolder folder:[{}] 成功", folder);
    }

    @Override
    public InputStream getDownloadStream(String fileKey) {
        try {
            URL url = new URL(constructLocalUrl(fileKey));
            return new BufferedInputStream(url.openStream());
        } catch (IOException e) {
            log.error("Remote resource fetch error.", e);
            return null;
        }
    }

    /**
     * 构造url
     *
     * @param downloadUrl 绝对地址
     * @return url
     * @throws MalformedURLException
     */
    private String constructLocalUrl(String downloadUrl) throws MalformedURLException {
        if (!downloadUrl.startsWith(LOCAL_PREFIX)) {
            return downloadUrl;
        }

        CdnConfig cdnConfig = getCdnConfig();
        String backendDownloadUrl = cdnConfig.getBackendDownloadUrl();
        String host = "";
        if (StringUtils.isNotBlank(backendDownloadUrl)) {
            host = backendDownloadUrl;
        } else {
            String referer = PamirsSession.getRequestVariables().getHeader("referer");
            if (referer == null || referer.isEmpty()) {
                throw new IllegalStateException("Missing referer header");
            }
            URL url = new URL(referer);
            int port = url.getPort();
            // 构建主机部分，省略标准端口
            StringBuilder hostsBuilder = new StringBuilder();
            hostsBuilder.append(url.getProtocol()).append("://").append(url.getHost());
            if (port != -1) {
                hostsBuilder.append(CharacterConstants.SEPARATOR_COLON).append(port);
            }
            host = hostsBuilder.toString();
        }
        return host + downloadUrl;
    }


    /**
     * 这个是Excel文件导出的，因为文件名需要和getFormData的保持一致，即文件名前面需要加年月日。
     *
     * @param fileName
     * @param data
     * @return
     */
    @Override
    public CdnFile upload(String fileName, byte[] data) {
        CdnConfig cdnConfig = getCdnConfig();
        String fileKey = getFileKey(cdnConfig.getMainDir(), fileName);
        return this.upload(fileKey, new ByteArrayInputStream(data));
    }

    @Override
    public CdnFile upload(String fileName, InputStream inputStream) {
        try {
            long size;
            try {
                size = inputStream.available();
            } catch (IOException e) {
                size = -1;
                log.error("upload file size read error.", e);
            }
            CdnFile resourceFile = new CdnFile();
            _createLocalFile(inputStream, fileName);
            resourceFile.setName(fileName);
            resourceFile.setSize(size);
            resourceFile.setType(FILE_TYPE);
            resourceFile.setUrl(getDownloadUrl(fileName));
            return resourceFile;
        } catch (Exception e) {
            log.error("upload 文件上传服务出错!", e);
        }

        return null;
    }

    @Override
    public String getDownloadUrl(String fileKey) {
        CdnConfig cdnConfig = getCdnConfig();
        return cdnConfig.getDownloadUrl() + LOCAL_PREFIX + "/" + fileKey;
    }

    /**
     * 为保证使用OSS服务和本地资源文件，可以随时切换
     * <p>
     * 这里约定 用本地静态资源文件是,图片的路径nx中配置代码拦截/static
     * 配置项 DownloadUrl 必须带 协议头：HTTP/HTTPS
     *
     * @return
     */
    @Override
    public String getStaticUrl() {
        CdnConfig cdnConfig = getCdnConfig();
        if (cdnConfig.getAppLogoUseCdn() != null && cdnConfig.getAppLogoUseCdn()) {
            return cdnConfig.getDownloadUrl() + LOCAL_PREFIX;
        } else {
            return CdnConfig.defaultCdnUrl;
        }
    }

    /**
     * 通过上传的文件名，缓冲到本地
     *
     * @param inputStream
     */
    private boolean _createLocalFile(InputStream inputStream, String fileKey) {
        CdnConfig cdnConfig = getCdnConfig();
        String filePath = cdnConfig.getLocalFolderUrl();
        if (!filePath.endsWith(SEPARATOR_SLASH)) {
            filePath = filePath + SEPARATOR_SLASH;
        }

        String path = filePath + fileKey;
        log.info("createLocalFile path = {}", path);

        File localFile = new File(path);
        // 创建目录
        Optional.ofNullable(localFile.getParentFile()).map(File::mkdirs);

        FileOutputStream fos = null;
        try {
            if (localFile.exists()) {
                //如果文件存在删除文件
                boolean delete = localFile.delete();
                if (!delete) {
                    log.error("Delete exist file \"{}\" failed!!!", path, new Exception("Delete exist file \"" + path + "\" failed!!!"));
                }
            }
            //创建文件
            if (!localFile.exists()) {
                //如果文件不存在，则创建新的文件
                localFile.createNewFile();
                log.info("Create file successfully,the file is {}", path);
            }

            //创建文件成功后，写入内容到文件里
            fos = new FileOutputStream(localFile);
            byte[] bytes = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
            }
            fos.flush();
            log.info("Reading uploaded file and buffering to local successfully!");
        } catch (FileNotFoundException e) {
            log.error("文件不存在", e);
            return false;
        } catch (IOException e) {
            log.error("文件异常", e);
            return false;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                log.error("InputStream or OutputStream close error", e);
                return false;
            }
        }

        return true;
    }

    private void deleteFolder(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteFolder(file);
                }
            }
        }
        folder.delete();
    }

}
