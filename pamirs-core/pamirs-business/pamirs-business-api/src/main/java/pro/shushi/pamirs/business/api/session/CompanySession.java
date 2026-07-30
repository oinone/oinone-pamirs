package pro.shushi.pamirs.business.api.session;

import pro.shushi.pamirs.meta.api.session.PamirsSession;

/**
 * 当前公司会话上下文
 *
 * @author Adamancy Zhang at 12:29 on 2025-12-02
 */
public class CompanySession {

    private static final String ID_KEY = "CURRENT_COMPANY_ID";

    private static final String CODE_KEY = "CURRENT_COMPANY_CODE";

    public static String getCompanyId() {
        return PamirsSession.getTransmittableExtend().get(ID_KEY);
    }

    public static void setCompanyId(Long id) {
        if (id == null) {
            PamirsSession.getTransmittableExtend().remove(ID_KEY);
            return;
        }
        PamirsSession.getTransmittableExtend().put(ID_KEY, String.valueOf(id));
    }

    public static String getCompanyCode() {
        return PamirsSession.getTransmittableExtend().get(CODE_KEY);
    }

    public static void setCompanyCode(String code) {
        if (code == null) {
            PamirsSession.getTransmittableExtend().remove(CODE_KEY);
            return;
        }
        PamirsSession.getTransmittableExtend().put(CODE_KEY, code);
    }

    public static void clear() {
        PamirsSession.getTransmittableExtend().remove(ID_KEY);
        PamirsSession.getTransmittableExtend().remove(CODE_KEY);
    }
}
