package pro.shushi.pamirs.core.common.loader;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.core.common.dsl.DslConverter;
import pro.shushi.pamirs.core.common.dsl.model.UdSearch;
import pro.shushi.pamirs.core.common.dsl.model.UdView;
import pro.shushi.pamirs.core.common.xstream.AutoIgnoreXStream;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.util.AppClassLoader;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class BizInitLoader {

    public static void init(InitializationUtil util, String moduleModule) {
        init(util, moduleModule, false);
    }

    public static void init(InitializationUtil util, String moduleModule, boolean useModuleSuffix) {
        XStream xs = new AutoIgnoreXStream();
        xs.processAnnotations(new Class[]{View.class, Search.class});
        PathMatchingResourcePatternResolver finder = new PathMatchingResourcePatternResolver(AppClassLoader.getClassLoader(BizInitLoader.class));
        try {
            Resource[] resources = finder.getResources("classpath*:/pamirs/init/views/" + moduleModule + "/**/*.xml");
            log.warn("[View Definition Loader] scanning views files....total: " + resources.length);
            for (Resource resource : resources) {
                try (InputStream is = resource.getInputStream()) {
                    String viewDfs = IOUtils.toString(is);
                    if (StringUtils.isBlank(viewDfs)) {
                        continue;
                    }
                    //FIXME CPC
                    viewDfs = viewDfs.replaceAll("<=>", "");
                    if (viewDfs.indexOf("</host>") > 0 || viewDfs.indexOf("</mask>") > 0) {
                        //是page的xml,忽略
                        continue;
                    }

                    Object obj = DslConverter.fromXML(viewDfs);
                    if (obj == null) {
                        continue;
                    }

                    if (obj instanceof UdSearch) {
                        UdSearch search = (UdSearch) obj;
                        String xmlPath = resource.getURI().toString().substring(resource.getURI().toString().indexOf("pamirs/init/views"));
                        String title = search.getViewName();
                        if (StringUtils.isNotBlank(search.getViewName()) && StringUtils.isNotBlank(search.getModel())) {
                            //保持search
                            util.createView(search.getModel(), title, search.getViewName(), InitializationUtil.filePrefix + xmlPath, ViewTypeEnum.SEARCH);
                        }
                    }else if (obj instanceof UdView) {
                        UdView viewDf = (UdView) obj;

                        if (StringUtils.isNotBlank(viewDf.getViewName()) && StringUtils.isNotBlank(viewDf.getModel())) {

                            ViewLoader.load(moduleModule, viewDf, util, resource.getURI(), useModuleSuffix);
                            log.warn("[View Definition Loader] " + resource.getFilename() + " loaded successfully");
                        } else {
                            log.error("[View Definition Loader] " + resource.getFilename() + " loaded error");
                        }

                    }

                } catch (Exception e) {
                    log.error("[Method Definition Loader] " + resource.getFilename() + " is not valid method definition file!!!", e);
                }
            }
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

}
