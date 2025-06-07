package pro.shushi.pamirs.framework.connectors.cdn.client;

import pro.shushi.pamirs.framework.connectors.cdn.factory.CdnConfigRouter;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.CdnFile;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.CdnFileForm;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.io.InputStream;
import java.util.function.Supplier;

/**
 * dynamic file client
 *
 * @author Adamancy Zhang at 11:50 on 2023-08-14
 */
public class DynamicFileClient implements FileClient {

    private final String key;

    private final FileClient client;

    public DynamicFileClient(String key, String type) {
        this.key = key;
        this.client = Spider.getExtension(FileClient.class, type);
    }

    @Override
    public CdnFileForm getFormData(String fileName) {
        return usingRouter(() -> client.getFormData(fileName));
    }

    @Override
    public CdnFile upload(String fileName, byte[] data) {
        return usingRouter(() -> client.upload(fileName, data));
    }

    @Override
    public CdnFile upload(String fileName, InputStream inputStream) {
        return usingRouter(() -> client.upload(fileName, inputStream));
    }

    @Override
    public String uploadByFileName(String fileName, byte[] data) {
        return usingRouter(() -> client.uploadByFileName(fileName, data));
    }

    @Override
    public String uploadByFileName(String fileName, InputStream inputStream) {
        return usingRouter(() -> client.uploadByFileName(fileName, inputStream));
    }

    @Override
    public String getDownloadUrl(String fileName) {
        return usingRouter(() -> client.getDownloadUrl(fileName));
    }

    @Override
    public InputStream getDownloadStream(String fileKey) {
        return usingRouter(() -> client.getDownloadStream(fileKey));
    }

    @Override
    public String fetchContent(String fileName) {
        return usingRouter(() -> client.fetchContent(fileName));
    }

    @Override
    public void deleteByFolder(String folder) {
        usingRouterWithoutResult(() -> client.deleteByFolder(folder));
    }

    @Override
    public void deleteByFilename(String filename) {
        usingRouterWithoutResult(() -> client.deleteByFilename(filename));
    }

    @Override
    public boolean isExistByFilename(String filename) {
        return usingRouter(() -> client.isExistByFilename(filename));
    }

    @Override
    public String getStaticUrl() {
        return usingRouter(client::getStaticUrl);
    }

    private <T> T usingRouter(Supplier<T> supplier) {
        try (CdnConfigRouter router = CdnConfigRouter.use(key)) {
            return supplier.get();
        }
    }

    private void usingRouterWithoutResult(Runnable runnable) {
        try (CdnConfigRouter router = CdnConfigRouter.use(key)) {
            runnable.run();
        }
    }
}
