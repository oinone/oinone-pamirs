package pro.shushi.pamirs.eip.api.constant;

public interface EipConnGroupConstant {

    /**
     * 内置编码. 自定义编码从10000开始累计
     */
    interface Code {
        String SYS_CODE_OA = "DOM00001";
        String SYS_CODE_ERP = "DOM00002";
        String SYS_CODE_STORAGE = "DOM00003";
        String SYS_CODE_MANUFACTURE = "DOM00004";
        String SYS_CODE_TRANSACTION = "DOM00005";
        String SYS_CODE_TOOL = "DOM00006";

        String SYS_CODE_OTHER = "DOM09999";
    }
}
