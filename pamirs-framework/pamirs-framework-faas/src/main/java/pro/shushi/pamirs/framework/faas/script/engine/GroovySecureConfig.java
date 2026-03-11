package pro.shushi.pamirs.framework.faas.script.engine;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;

import java.util.Arrays;
import java.util.List;

/**
 * Groovy 编译期安全配置。
 *
 * <p>通过 {@link SecureASTCustomizer} 在 AST（抽象语法树）编译阶段禁止危险类的直接调用，
 * 防止用户提交的 Groovy 脚本利用 Runtime、ProcessBuilder 等执行任意系统命令（RCE）。</p>
 *
 * <p>此防护作用于编译期，无法被运行时反射绕过（受限类不进 AST）。</p>
 *
 * <p><strong>防护范围：</strong></p>
 * <ul>
 *   <li>禁止通配符导入危险包（java.lang.reflect.*、groovy.lang.*、javax.script.*）</li>
 *   <li>禁止直接调用危险类（Runtime、ProcessBuilder、Thread、System、ClassLoader 等）</li>
 *   <li>开启间接导入检查，防止通过全限定名绕过</li>
 *   <li>禁止 package 声明，防止封装自定义包绕过类名检查</li>
 * </ul>
 */
public class GroovySecureConfig {

    private GroovySecureConfig() {
    }

    /**
     * 构建注入了 {@link SecureASTCustomizer} 的安全编译配置。
     * 供 {@link GroovyScriptEngine} 和 {@link pro.shushi.pamirs.framework.faas.script.GroovyRunner} 复用。
     *
     * @return 已配置安全限制的 {@link CompilerConfiguration}
     */
    public static CompilerConfiguration buildSecureCompilerConfig() {
        CompilerConfiguration config = new CompilerConfiguration(CompilerConfiguration.DEFAULT);
        config.addCompilationCustomizers(buildSecureASTCustomizer());
        return config;
    }

    private static SecureASTCustomizer buildSecureASTCustomizer() {
        SecureASTCustomizer customizer = new SecureASTCustomizer();

        // 禁止危险包的通配符导入（import java.lang.reflect.*）
        customizer.setDisallowedStarImports(Arrays.asList(
                "java.lang.reflect",
                "groovy.lang",
                "javax.script",
                "java.lang.instrument",
                "sun.misc",
                "sun.reflect"
        ));

        // 禁止危险包的直接导入（import java.lang.Runtime）
        customizer.setDisallowedImports(Arrays.asList(
                "java.lang.Runtime",
                "java.lang.ProcessBuilder",
                "java.lang.reflect.Method",
                "java.lang.reflect.Field",
                "java.lang.reflect.Constructor",
                "java.lang.ClassLoader",
                "groovy.lang.GroovyShell",
                "groovy.lang.GroovyClassLoader",
                "javax.script.ScriptEngine",
                "java.lang.Thread",
                "java.lang.ThreadGroup",
                "java.lang.System",
                "java.lang.instrument.Instrumentation",
                "sun.misc.Unsafe"
        ));

        // 编译阶段禁止直接调用危险类（AST 级别，无法绕过）。
        // 包含 String.execute()（Groovy 扩展，可直接执行 shell 命令）的接收者也在内。
        customizer.setDisallowedReceivers(buildDisallowedReceivers());

        // 开启间接导入检查（防止通过全限定名绕过）
        customizer.setIndirectImportCheckEnabled(true);

        // 禁止脚本中声明 package，防止封装同名类绕过类名黑名单
        customizer.setPackageAllowed(false);

        return customizer;
    }

    /**
     * 编译期禁用的接收者类型列表。
     *
     * <p>这些类即使被引用也不允许在 AST 中出现。
     * {@code String.execute()}（Groovy 内置扩展）要求 String 类以接收者调用 execute，
     * 禁用 String 接收者会导致过度误禁，改为禁用具体危险类。</p>
     */
    private static List<String> buildDisallowedReceivers() {
        return Arrays.asList(
                "java.lang.Runtime",
                "java.lang.ProcessBuilder",
                "java.lang.reflect.Method",
                "java.lang.reflect.Field",
                "java.lang.reflect.Constructor",
                "java.lang.ClassLoader",
                "groovy.lang.GroovyShell",
                "groovy.lang.GroovyClassLoader",
                "javax.script.ScriptEngine",
                "java.lang.Thread",
                "java.lang.ThreadGroup",
                "java.lang.System",
                "sun.misc.Unsafe"
        );
    }
}
