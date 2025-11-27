package pro.shushi.pamirs.ux.filling.converter;

/**
 * @author Gesi at 9:35 on 2025/9/11
 */
public class StringConverter extends AbstractValueConverter implements QuickFillingValueConverter {

    public static final QuickFillingValueConverter INSTANCE = new StringConverter();

    @Override
    public Object singleValueConvert(QuickFillingContext context, String value) {
        return value;
    }
}
