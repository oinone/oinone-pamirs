package pro.shushi.pamirs.ux.quickfilling.converter;

/**
 * 抽象非基础类型快速填报转换器
 *
 * @author Adamancy Zhang at 16:20 on 2025-11-27
 */
public abstract class AbstractNonBasicQuickFillingConverter extends AbstractQuickFillingConverter implements QuickFillingConverter {

    public AbstractNonBasicQuickFillingConverter(QuickFillingColumn column) {
        super(column);
    }

    @Override
    public boolean isBasicConverter() {
        return false;
    }

    @Override
    public void convert(QuickFillingRow row, String value) {
        throw new UnsupportedOperationException();
    }
}
