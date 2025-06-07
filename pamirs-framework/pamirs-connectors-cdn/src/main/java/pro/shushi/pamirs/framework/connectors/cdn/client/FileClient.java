package pro.shushi.pamirs.framework.connectors.cdn.client;

import pro.shushi.pamirs.framework.connectors.cdn.pojo.CdnFile;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.CdnFileForm;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.CdnUploadFileRequest;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.io.InputStream;

@SPI(factory = SpringServiceLoaderFactory.class)
public interface FileClient {

    default CdnFileForm getFormData(String filename) {
        throw new UnsupportedOperationException();
    }

    default CdnFileForm getFormData(CdnUploadFileRequest request) {
        return getFormData(request.getFilename());
    }

    default CdnFile upload(String fileName, byte[] data) {
        throw new UnsupportedOperationException();
    }

    default CdnFile upload(String fileName, InputStream inputStream) {
        throw new UnsupportedOperationException();
    }

    default String uploadByFileName(String fileName, byte[] data) {
        throw new UnsupportedOperationException();
    }

    default String uploadByFileName(String fileName, InputStream inputStream) {
        throw new UnsupportedOperationException();
    }

    /**
     * 这个getDownloadUrl只提供 由uploadByFileName上传的文件 的下载地址
     * 通过upload下载的文件去resourceFile里拿url
     */
    default String getDownloadUrl(String fileName) {
        throw new UnsupportedOperationException();
    }

    default InputStream getDownloadStream(String fileKey) {
        throw new UnsupportedOperationException();
    }

    default String fetchContent(String fileName) {
        throw new UnsupportedOperationException();
    }

    /**
     * 删除文件夹下的所有文件
     *
     * @param folder 文件夹
     */
    default void deleteByFolder(String folder) {
        throw new UnsupportedOperationException();
    }

    /**
     * 通过文件名称删除文件
     *
     * @param filename 文件名
     */
    default void deleteByFilename(String filename) {
        throw new UnsupportedOperationException();
    }

    /**
     * 判断文件是否存在
     *
     * @param filename 文件名
     * @return 判断结果
     */
    default boolean isExistByFilename(String filename) {
        throw new UnsupportedOperationException();
    }

    // ************************主要这里的约定****************************
    // 这里约定 用本地静态资源文件是,图片的路径nx中配置代码拦截/static
    default String getStaticUrl() {
        throw new UnsupportedOperationException();
    }

}
