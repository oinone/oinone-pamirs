package pro.shushi.pamirs.eip.api.converter;

import org.apache.camel.ExtendedExchange;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.springframework.core.io.ByteArrayResource;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipInOutConverter;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

/**
 * Body格式为multipart/form-data参数转换，用于提交表单数据或上传文件
 *
 * @author yeshenyue on 2024/4/12 18:30
 */
@Fun(EipFunctionConstant.FUNCTION_NAMESPACE)
public class DefaultMultipartFormDataInOutConverter extends DefaultInOutConverter implements IEipInOutConverter {

    public static final ContentType PLAIN_TEXT_UTF_8_CONTENT_TYPE =
            ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), StandardCharsets.UTF_8);

    @Function.fun(EipFunctionConstant.DEFAULT_MULTIPART_FORM_DATA_IN_OUT_CONVERTER_FUN)
    @Function.Advanced(displayName = "默认multipart/form-data输入输出转换器")
    @Function(name = EipFunctionConstant.DEFAULT_MULTIPART_FORM_DATA_IN_OUT_CONVERTER_FUN)
    @Override
    public Object exchangeObject(ExtendedExchange exchange, Object inObject) throws Exception {
        if (inObject instanceof Map) {
            SuperMap bodyMap = (SuperMap) inObject;
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            for (Map.Entry<String, Object> entry : bodyMap.entrySet()) {
                if (entry.getValue() instanceof ByteArrayResource) {
                    processBinaryBody(builder, entry);
                } else {
                    processTextBody(builder, entry);
                }
            }
            return builder.build();
        }
        return null;
    }

    /**
     * 处理文件流，将文件信息构建到builder对象中
     */
    private void processBinaryBody(MultipartEntityBuilder builder, Map.Entry<String, Object> entry) {
        ByteArrayResource fileArrayRes = (ByteArrayResource) entry.getValue();
        String fileName = Optional.ofNullable(fileArrayRes.getFilename()).filter(StringUtils::isNotBlank).orElse("file");
        builder.addBinaryBody(entry.getKey(), fileArrayRes.getByteArray(), ContentType.DEFAULT_BINARY, fileName);
    }

    /**
     * 处理文本信息，将文本构建到builder对象中
     */
    private void processTextBody(MultipartEntityBuilder builder, Map.Entry<String, Object> entry) {
        builder.addTextBody(entry.getKey(), String.valueOf(entry.getValue()), PLAIN_TEXT_UTF_8_CONTENT_TYPE);
    }
}
