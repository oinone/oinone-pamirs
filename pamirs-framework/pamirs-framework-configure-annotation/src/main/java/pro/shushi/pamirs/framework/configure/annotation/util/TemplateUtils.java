package pro.shushi.pamirs.framework.configure.annotation.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_TEMPLATE_RENDER_ERROR;


/**
 * 模板引擎工具类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/16 2:38 下午
 */
@SuppressWarnings({"unused"})
@Slf4j
public class TemplateUtils {

    public static <T> String process(String templateString, T entity) {
        Configuration cfg = new Configuration();
        try {
            StringWriter result = new StringWriter();
            Template t = new Template("template", new StringReader(templateString), cfg);
            t.process(entity, result);
            return t.toString();
        } catch (IOException | TemplateException e) {
            throw PamirsException.construct(BASE_TEMPLATE_RENDER_ERROR, e).errThrow();
        }
    }

}
