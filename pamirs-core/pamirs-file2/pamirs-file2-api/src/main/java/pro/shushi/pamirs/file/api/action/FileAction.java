package pro.shushi.pamirs.file.api.action;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.URLHelper;
import pro.shushi.pamirs.file.api.enmu.FileExpEnumerate;
import pro.shushi.pamirs.file.api.model.ResourceChunkFile;
import pro.shushi.pamirs.file.api.model.ResourceFileForm;
import pro.shushi.pamirs.framework.connectors.cdn.client.FileClient;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.CdnChunkFile;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.CdnFileForm;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.CdnSingleUploadData;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.CdnUploadFileRequest;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.util.TypeReferences;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Model.model(ResourceFileForm.MODEL_MODEL)
public class FileAction {

    private static final String[] SPECIAL_SYMBOLS = {"+"};

    @Autowired
    private HttpServletResponse response;

    @Function.Advanced(displayName = "前端上传数据", type = FunctionTypeEnum.QUERY)
    @Function(openLevel = {FunctionOpenEnum.API, FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE})
    public ResourceFileForm uploadFormData(ResourceFileForm resourceFileForm) {
        FileClient fileClient = FileClientFactory.getClient(resourceFileForm.getCdnKey());
        if (fileClient == null) {
            throw PamirsException.construct(FileExpEnumerate.FILE_SERVER_NOT_FOUND_ERROR).errThrow();
        }
        String filename = Optional.ofNullable(resourceFileForm.getFilename()).filter(StringUtils::isNotBlank).orElse(null);
        if (StringUtils.isBlank(filename)) {
            throw PamirsException.construct(FileExpEnumerate.FILENAME_IS_NULL_ERROR).errThrow();
        }
        for (String specialSymbol : SPECIAL_SYMBOLS) {
            filename = filename.replace(specialSymbol, CharacterConstants.SEPARATOR_EMPTY);
        }
        if (filename.contains("../") || filename.contains("..\\")) {
            throw PamirsException.construct(FileExpEnumerate.FILE_NOT_EXIST).errThrow();
        }
        CdnFileForm result = fileClient.getFormData(new CdnUploadFileRequest()
                .setFilename(filename)
                .setSize(resourceFileForm.getFileSize())
                .setContentType(resourceFileForm.getContentType())
                .setChunkFiles(convertChunkFiles(resourceFileForm.getChunkFiles())));
        if (StringUtils.isBlank(result.getSingleUploadJson())) {
            CdnSingleUploadData singleUploadData = new CdnSingleUploadData();
            singleUploadData.setUploadUrl(result.getUploadUrl());
            singleUploadData.setUploadFormData(JsonUtils.parseObject(result.getFormDataJson(), TypeReferences.TR_MAP_SS));
            result.setSingleUploadJson(JsonUtils.toJSONString(singleUploadData));
        }
        return resourceFileForm.setDownloadUrl(result.getDownloadUrl())
                .setFilename(result.getFilename())
                .setSingleUploadJson(result.getSingleUploadJson())
                .setMultipartUploadJson(result.getMultipartUploadJson());
    }

    private List<CdnChunkFile> convertChunkFiles(List<ResourceChunkFile> resourceChunkFiles) {
        if (CollectionUtils.isEmpty(resourceChunkFiles)) {
            return null;
        }
        List<CdnChunkFile> chunkFiles = new ArrayList<>();
        for (ResourceChunkFile resourceChunkFile : resourceChunkFiles) {
            chunkFiles.add(new CdnChunkFile()
                    .setPartNumber(resourceChunkFile.getPartNumber())
                    .setFileSize(resourceChunkFile.getFileSize()));
        }
        return chunkFiles;
    }

    @Function.Advanced(displayName = "前端下载数据", type = FunctionTypeEnum.QUERY)
    @Function(openLevel = {FunctionOpenEnum.API, FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE})
    public ResourceFileForm downloadFormData(ResourceFileForm resourceFileForm) {
        FileClient fileClient = FileClientFactory.getClient(resourceFileForm.getCdnKey());
        if (fileClient == null) {
            throw PamirsException.construct(FileExpEnumerate.FILE_SERVER_NOT_FOUND_ERROR).errThrow();
        }
        String fileName = Optional.ofNullable(resourceFileForm.getFilename()).filter(StringUtils::isNotBlank).orElse(null);
        String downloadUrl = resourceFileForm.getDownloadUrl();
        boolean isGeneratorFilename = StringUtils.isBlank(fileName);
        if (StringUtils.isBlank(downloadUrl)) {
            throw PamirsException.construct(FileExpEnumerate.FILE_NOT_EXIST).errThrow();
        }
        URL url = null;
        try {
            url = new URL(downloadUrl);
        } catch (MalformedURLException ignored) {
        }
        if (url == null) {
            if (isGeneratorFilename) {
                fileName = Optional.of(downloadUrl.split("/")).map(v -> v[v.length - 1]).orElse(null);
                if (StringUtils.isBlank(fileName)) {
                    throw PamirsException.construct(FileExpEnumerate.FILE_DOWNLOAD_ERROR).appendMsg("无法获取文件名").errThrow();
                }
            }
        } else {
            if (isGeneratorFilename) {
                fileName = Optional.of(url).map(URL::getPath).map(v -> v.split("/")).map(v -> v[v.length - 1]).orElse(null);
                if (StringUtils.isBlank(fileName)) {
                    throw PamirsException.construct(FileExpEnumerate.FILE_DOWNLOAD_ERROR).appendMsg("无法获取文件名").errThrow();
                }
            }
            downloadUrl = URLHelper.repairRelativePath(url.getPath());
        }
        if (downloadUrl.contains("../") || downloadUrl.contains("..\\")) {
            throw PamirsException.construct(FileExpEnumerate.FILE_NOT_EXIST).errThrow();
        }
        InputStream inputStream = fileClient.getDownloadStream(downloadUrl);
        if (inputStream != null) {
            try {
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
                IOUtils.copy(inputStream, response.getOutputStream());
                return new ResourceFileForm();
            } catch (Throwable e) {
                throw PamirsException.construct(FileExpEnumerate.FILE_DOWNLOAD_ERROR, e).errThrow();
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }
        throw PamirsException.construct(FileExpEnumerate.FILE_DOWNLOAD_ERROR).errThrow();
    }
}
