package pro.shushi.pamirs.file.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author Adamancy Zhang
 * @date 2021-01-22 12:14
 */
@Configuration
@ConfigurationProperties(prefix = FileConfigurationConstant.PAMIRS_FILE_PREFIX)
@Validated
@RefreshScope
public class FileProperties {

    private boolean autoUploadLogo = false;

    //是否创建默认导出模板
    private boolean autoCreateTemplate = true;

    private FileImportProperties importProperty = new FileImportProperties();

    private FileExportProperties exportProperty = new FileExportProperties();

    public boolean getAutoUploadLogo() {
        return autoUploadLogo;
    }

    public void setAutoUploadLogo(boolean autoUploadLogo) {
        this.autoUploadLogo = autoUploadLogo;
    }

    public boolean getAutoCreateTemplate() {
        return autoCreateTemplate;
    }

    public void setAutoCreateTemplate(boolean autoCreateTemplate) {
        this.autoCreateTemplate = autoCreateTemplate;
    }

    public FileImportProperties getImportProperty() {
        return importProperty;
    }

    public void setImportProperty(FileImportProperties importProperty) {
        this.importProperty = importProperty;
    }

    public FileExportProperties getExportProperty() {
        return exportProperty;
    }

    public void setExportProperty(FileExportProperties exportProperty) {
        this.exportProperty = exportProperty;
    }

    public static class FileImportProperties {

        @NotNull
        private boolean defaultEachImport = false;

        @NotNull
        @Min(0)
        private int maxErrorLength = 100;

        public boolean getDefaultEachImport() {
            return defaultEachImport;
        }

        public void setDefaultEachImport(boolean defaultEachImport) {
            this.defaultEachImport = defaultEachImport;
        }

        public int getMaxErrorLength() {
            return maxErrorLength;
        }

        public void setMaxErrorLength(int maxErrorLength) {
            this.maxErrorLength = maxErrorLength;
        }
    }

    public static class FileExportProperties {

        @NotNull
        private boolean defaultClearExportStyle = false;

        @NotNull
        @Min(1)
        private int excelMaxSupportLength = 100000;

        @NotNull
        @Min(1)
        private int csvMaxSupportLength = 1000000;

        public boolean getDefaultClearExportStyle() {
            return defaultClearExportStyle;
        }

        public void setDefaultClearExportStyle(boolean defaultClearExportStyle) {
            this.defaultClearExportStyle = defaultClearExportStyle;
        }

        public int getExcelMaxSupportLength() {
            return excelMaxSupportLength;
        }

        public FileExportProperties setExcelMaxSupportLength(int excelMaxSupportLength) {
            this.excelMaxSupportLength = excelMaxSupportLength;
            return this;
        }

        public int getCsvMaxSupportLength() {
            return csvMaxSupportLength;
        }

        public void setCsvMaxSupportLength(int csvMaxSupportLength) {
            this.csvMaxSupportLength = csvMaxSupportLength;
        }
    }
}
