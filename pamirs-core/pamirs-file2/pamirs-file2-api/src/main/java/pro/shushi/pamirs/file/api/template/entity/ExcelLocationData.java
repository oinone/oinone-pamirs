package pro.shushi.pamirs.file.api.template.entity;

import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * Excel国际化数据
 *
 * @author Adamancy Zhang at 19:07 on 2024-06-01
 */
@Data
public class ExcelLocationData {

    private String model;

    private String name;

    private String displayName;

    private String originLang;

    private String origin;

    private String targetLang;

    private String target;
}
