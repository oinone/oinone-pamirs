package pro.shushi.pamirs.framework.connectors.cdn.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.cdn.client.AbstractFileClient;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 文件上传允许自定义文件名。
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 * 2022/8/12
 */
@SPI.Service
@Component
@Order(Integer.MAX_VALUE) //默认优先级最低，业务配置需要配置成为优先级高
public class DefaultCdnFileNameApi implements CdnFileNameApi {

    @Override
    public String getNewFilename(String fileName) {
        // MINIO 文件名中存在空格的情况，导入文件报错(根据文件URL找不到对应的文件)
        fileName = fileName.replaceAll("\\s", "");
        String suffix = AbstractFileClient.getSuffix(fileName);
        String newFilename;
        if (CharacterConstants.SEPARATOR_EMPTY.equals(suffix)) {
            newFilename = fileName + CharacterConstants.SEPARATOR_UNDERLINE + System.currentTimeMillis();
        } else {
            fileName = fileName.substring(0, fileName.indexOf(suffix));
            newFilename = fileName + CharacterConstants.SEPARATOR_UNDERLINE + System.currentTimeMillis() + suffix;
        }
        return newFilename;
    }
}
