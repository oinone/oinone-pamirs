package pro.shushi.pamirs.framework.orm.test;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.systems.directive.SystemDirectiveEnum;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.bit.SessionMetaBit;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

/**
 * 指令测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("指令测试")
public class DirectiveTest extends AbstractBaseTest {

    @Test
    @Order(0)
    @DisplayName("测试指令")
    public void testDirectiveOperation() {

        SessionMetaBit sessionMetaBit = PamirsSession.directive();
        ModelDefinition modelDefinition = new ModelDefinition();

        check(sessionMetaBit);

        Assert.assertTrue("指令默认值错误", modelDefinition.isDirty());
        modelDefinition.disableDirty();
        Assert.assertFalse("失效指令错误", modelDefinition.isDirty());
        modelDefinition.enableDirty();
        Assert.assertTrue("生效指令错误", modelDefinition.isDirty());

        sessionMetaBit.initMetaBit(SystemDirectiveEnum.getInitValue());
        modelDefinition.initMetaBit();

        Assert.assertFalse("指令默认值错误", modelDefinition.isReentry());
        modelDefinition.enableReentry();
        Assert.assertTrue("生效指令错误", modelDefinition.isReentry());
        modelDefinition.disableReentry();
        Assert.assertFalse("失效指令错误", modelDefinition.isReentry());

        checkInit(sessionMetaBit);

        Assert.assertFalse("指令默认值错误", modelDefinition.isReentry());
        modelDefinition.disableReentry();
        Assert.assertFalse("失效指令错误", modelDefinition.isReentry());
        modelDefinition.enableReentry();
        Assert.assertTrue("生效指令错误", modelDefinition.isReentry());

        Assert.assertTrue("指令默认值错误", modelDefinition.isDirty());
        modelDefinition.disableDirty();
        Assert.assertFalse("失效指令错误", modelDefinition.isDirty());
        modelDefinition = modelDefinition.enableDirty();
        Assert.assertTrue("生效指令错误", modelDefinition.isDirty());

    }

    @Test
    @Order(0)
    @DisplayName("测试保护指令与初始化")
    public void testDirectiveProtectAndClear() {

        SessionMetaBit sessionMetaBit = PamirsSession.directive().clearMetaBit();
        ModelDefinition modelDefinition = new ModelDefinition();

        check(sessionMetaBit);

        Assert.assertTrue("指令默认值错误", modelDefinition.isDirty());
        modelDefinition.disableDirty();
        Assert.assertFalse("生效指令错误", modelDefinition.isDirty());

        sessionMetaBit.initMetaBit(SystemDirectiveEnum.getInitValue());
        modelDefinition.initMetaBit();

        Assert.assertFalse("指令默认值错误", modelDefinition.isReentry());
        modelDefinition.enableReentry();
        Assert.assertTrue("生效指令错误", modelDefinition.isReentry());
        modelDefinition.disableReentry();
        Assert.assertFalse("失效指令错误", modelDefinition.isReentry());

        checkInit(sessionMetaBit);

        Assert.assertFalse("指令默认值错误", modelDefinition.isReentry());
        modelDefinition.disableReentry();
        Assert.assertFalse("失效指令错误", modelDefinition.isReentry());
        modelDefinition.enableReentry();
        Assert.assertTrue("生效指令错误", modelDefinition.isReentry());

        Assert.assertTrue("指令默认值错误", modelDefinition.isDirty());
        modelDefinition.disableDirty();
        Assert.assertFalse("生效指令错误", modelDefinition.isDirty());

        sessionMetaBit.clearMetaBit();
        modelDefinition.clearMetaBit();

        check(sessionMetaBit);

        Assert.assertTrue("指令默认值错误", modelDefinition.isDirty());

    }

    private void check(SessionMetaBit directive) {
        Assert.assertTrue("指令默认值错误", directive.isSudo());
        directive.disableSudo();
        Assert.assertFalse("失效指令错误", directive.isSudo());
        directive.sudo();
        Assert.assertTrue("生效指令错误", directive.isSudo());

        Assert.assertTrue("指令默认值错误", directive.isOptimisticLocker());
        directive.disableOptimisticLocker();
        Assert.assertFalse("失效指令错误", directive.isOptimisticLocker());
        directive.enableOptimisticLocker();
        Assert.assertTrue("生效指令错误", directive.isOptimisticLocker());

        Assert.assertFalse("指令默认值错误", directive.isDoCheck());
        directive.enableCheck();
        Assert.assertTrue("生效指令错误", directive.isDoCheck());
        directive.disableCheck();
        Assert.assertFalse("失效指令错误", directive.isDoCheck());

        Assert.assertFalse("指令默认值错误", directive.isDoDefaultValue());
        directive.enableDefaultValue();
        Assert.assertTrue("生效指令错误", directive.isDoDefaultValue());
        directive.disableDefaultValue();
        Assert.assertFalse("失效指令错误", directive.isDoDefaultValue());

        Assert.assertFalse("指令默认值错误", directive.isDoExtPoint());
        directive.enableExtPoint();
        Assert.assertTrue("生效指令错误", directive.isDoExtPoint());
        directive.disableExtPoint();
        Assert.assertFalse("失效指令错误", directive.isDoExtPoint());

        Assert.assertFalse("指令默认值错误", Models.modelDirective().isDoColumn(directive));
        Models.modelDirective().enableColumn(directive);
        Assert.assertTrue("生效指令错误", Models.modelDirective().isDoColumn(directive));
        Models.modelDirective().disableColumn(directive);
        Assert.assertFalse("失效指令错误", Models.modelDirective().isDoColumn(directive));

        Assert.assertFalse("指令默认值错误", directive.isUsePkStrategy());
        directive.enableUsePkStrategy();
        Assert.assertTrue("生效指令错误", directive.isUsePkStrategy());
        directive.disableUsePkStrategy();
        Assert.assertFalse("失效指令错误", directive.isUsePkStrategy());
    }

    private void checkInit(SessionMetaBit directive) {
        Assert.assertFalse("指令默认值错误", directive.isSudo());
        directive.sudo();
        Assert.assertTrue("生效指令错误", directive.isSudo());
        directive.disableSudo();
        Assert.assertFalse("失效指令错误", directive.isSudo());

        Assert.assertTrue("指令默认值错误", directive.isOptimisticLocker());
        directive.disableOptimisticLocker();
        Assert.assertFalse("失效指令错误", directive.isOptimisticLocker());
        directive.enableOptimisticLocker();
        Assert.assertTrue("生效指令错误", directive.isOptimisticLocker());

        Assert.assertTrue("指令默认值错误", directive.isDoCheck());
        directive.disableCheck();
        Assert.assertFalse("失效指令错误", directive.isDoCheck());
        directive.enableCheck();
        Assert.assertTrue("生效指令错误", directive.isDoCheck());

        Assert.assertTrue("指令默认值错误", directive.isDoDefaultValue());
        directive.disableDefaultValue();
        Assert.assertFalse("失效指令错误", directive.isDoDefaultValue());
        directive.enableDefaultValue();
        Assert.assertTrue("生效指令错误", directive.isDoDefaultValue());

        Assert.assertTrue("指令默认值错误", directive.isDoExtPoint());
        directive.disableExtPoint();
        Assert.assertFalse("失效指令错误", directive.isDoExtPoint());
        directive.enableExtPoint();
        Assert.assertTrue("生效指令错误", directive.isDoExtPoint());

        Assert.assertFalse("指令默认值错误", Models.modelDirective().isDoColumn(directive));
        Models.modelDirective().disableColumn(directive);
        Assert.assertFalse("失效指令错误", Models.modelDirective().isDoColumn(directive));
        Models.modelDirective().enableColumn(directive);
        Assert.assertTrue("生效指令错误", Models.modelDirective().isDoColumn(directive));

        Assert.assertTrue("指令默认值错误", directive.isUsePkStrategy());
        directive.disableUsePkStrategy();
        Assert.assertFalse("失效指令错误", directive.isUsePkStrategy());
        directive.enableUsePkStrategy();
        Assert.assertTrue("生效指令错误", directive.isUsePkStrategy());
    }

}
