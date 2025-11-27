package pro.shushi.pamirs.ux.filling.converter;

/**
 * @author Gesi at 9:35 on 2025/9/11
 */
public class BooleanConverter extends AbstractValueConverter implements QuickFillingValueConverter {

    public static final QuickFillingValueConverter INSTANCE = new BooleanConverter();

    @Override
    public Object singleValueConvert(QuickFillingContext context, String value) {
        if ("TRUE".equalsIgnoreCase(value) || "1".equals(value) || "是".equals(value) || "Y".equalsIgnoreCase(value)) {
            return Boolean.TRUE;
        } else if ("FALSE".equalsIgnoreCase(value) || "0".equals(value) || "否".equals(value) || "N".equalsIgnoreCase(value)) {
            return Boolean.FALSE;
        }
        context.fail();
        return null;
    }
}
