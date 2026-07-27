package pro.shushi.pamirs.resource.api.util;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * iconfont 压缩包解压工具。
 * <p>
 * 兼容阿里 iconfont.cn 官方下载包结构，并过滤 macOS 压缩产生的元数据文件，
 * 避免 {@code __MACOSX}、{@code ._*}、{@code .DS_Store} 被误解析导致导入失败。
 *
 * @author yexiu at 19:26 on 2024/12/16
 */
public final class IconUnZipUtils {

    private static final String ICONFONT_PREFIX = "iconfont.";
    private static final String MAC_METADATA_DIR = "__macosx/";
    private static final String DS_STORE = ".ds_store";
    private static final String APPLE_DOUBLE_PREFIX = "._";

    private static final String EXT_CSS = ".css";
    private static final String EXT_JS = ".js";
    private static final String EXT_JSON = ".json";

    private static final Set<String> FONT_EXTENSIONS = Set.of(".ttf", ".woff", ".woff2");

    private IconUnZipUtils() {
    }

    public static Result unzipFromStream(InputStream inputStream) throws IOException {
        Result result = new Result();
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                try {
                    if (zipEntry.isDirectory()) {
                        continue;
                    }
                    String entryName = zipEntry.getName();
                    if (!isAcceptableEntry(entryName)) {
                        continue;
                    }
                    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                        extractFileToStream(zipInputStream, outputStream);
                        addFile(result, entryName, outputStream.toByteArray());
                    }
                } finally {
                    zipInputStream.closeEntry();
                }
            }
        }
        return result;
    }

    private static boolean isAcceptableEntry(String entryName) {
        if (StringUtils.isBlank(entryName) || isMacMetadataEntry(entryName) || !isSafeZipPath(entryName)) {
            return false;
        }
        return isIconfontResource(resolveBaseName(normalizePath(entryName)));
    }

    /**
     * 过滤 macOS 压缩包常见元数据：
     * <ul>
     *     <li>{@code __MACOSX/} 目录及其内容</li>
     *     <li>AppleDouble 资源叉文件（{@code ._*}）</li>
     *     <li>{@code .DS_Store}</li>
     * </ul>
     */
    private static boolean isMacMetadataEntry(String entryName) {
        String normalized = normalizePath(entryName).toLowerCase(Locale.ROOT);
        if (normalized.contains(MAC_METADATA_DIR)) {
            return true;
        }
        String baseName = resolveBaseName(normalized);
        return DS_STORE.equals(baseName) || baseName.startsWith(APPLE_DOUBLE_PREFIX);
    }

    /**
     * 防止路径穿越污染 CDN 上传路径。
     */
    private static boolean isSafeZipPath(String entryName) {
        String normalized = normalizePath(entryName);
        return !normalized.startsWith("/")
                && !normalized.contains("../")
                && !normalized.contains("./");
    }

    /**
     * 仅接受阿里 iconfont 官方命名资源：iconfont.css / .js / .json / .ttf|woff|woff2。
     */
    private static boolean isIconfontResource(String baseName) {
        String lowerBaseName = baseName.toLowerCase(Locale.ROOT);
        if (!lowerBaseName.startsWith(ICONFONT_PREFIX)) {
            return false;
        }
        return lowerBaseName.endsWith(EXT_CSS)
                || lowerBaseName.endsWith(EXT_JS)
                || lowerBaseName.endsWith(EXT_JSON)
                || endsWithAny(lowerBaseName, FONT_EXTENSIONS);
    }

    private static void addFile(Result result, String entryName, byte[] fileData) {
        String baseName = resolveBaseName(normalizePath(entryName)).toLowerCase(Locale.ROOT);
        if (baseName.endsWith(EXT_CSS)) {
            result.getCssNameMap().put(entryName, fileData);
        } else if (baseName.endsWith(EXT_JS)) {
            result.getJsNameMap().put(entryName, fileData);
        } else if (baseName.endsWith(EXT_JSON)) {
            result.getJsonNameMap().put(entryName, fileData);
        } else if (endsWithAny(baseName, FONT_EXTENSIONS)) {
            result.getFontNameMap().put(entryName, fileData);
        }
    }

    private static String normalizePath(String entryName) {
        return entryName.replace('\\', '/');
    }

    private static String resolveBaseName(String normalizedPath) {
        int index = normalizedPath.lastIndexOf('/');
        return index >= 0 ? normalizedPath.substring(index + 1) : normalizedPath;
    }

    private static boolean endsWithAny(String value, Set<String> suffixes) {
        for (String suffix : suffixes) {
            if (value.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    private static void extractFileToStream(ZipInputStream zipIn, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = zipIn.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
    }

    @Data
    public static class Result {
        private final Map<String, byte[]> cssNameMap = new HashMap<>();
        private final Map<String, byte[]> jsNameMap = new HashMap<>();
        private final Map<String, byte[]> jsonNameMap = new HashMap<>();
        private final Map<String, byte[]> fontNameMap = new HashMap<>();
    }
}
