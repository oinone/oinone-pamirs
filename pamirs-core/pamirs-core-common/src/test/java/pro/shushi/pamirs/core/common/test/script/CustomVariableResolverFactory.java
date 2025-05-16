package pro.shushi.pamirs.core.common.test.script;

import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.impl.StaticMethodImportResolver;
import org.mvel2.integration.impl.StaticMethodImportResolverFactory;
import org.mvel2.util.MethodStub;

/**
 * @author Adamancy Zhang at 20:47 on 2024-07-16
 */
public class CustomVariableResolverFactory extends StaticMethodImportResolverFactory {

    private static final long serialVersionUID = 2399269084424506954L;

    @Override
    public VariableResolver createVariable(String name, Object value) {
        try {
            return new StaticMethodImportResolver(name, new MethodStub(ScriptRunnerTest.class.getMethod("computeSum", Integer.class, Integer.class)));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isTarget(String name) {
        return false;
    }

    @Override
    public boolean isResolveable(String name) {
        return "computeSum".equals(name);
    }
}
