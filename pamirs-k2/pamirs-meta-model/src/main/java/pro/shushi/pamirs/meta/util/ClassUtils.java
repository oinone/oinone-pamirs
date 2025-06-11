package pro.shushi.pamirs.meta.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.common.util.AppClassLoader;
import pro.shushi.pamirs.meta.enmu.MetaExpEnumerate;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.meta.enmu.MetaExpEnumerate.BASE_CLASS_NOT_FOUNT_ERROR;

@Slf4j
public class ClassUtils {

    private static final Map<String, Boolean> clazzCache = new ConcurrentHashMap<>();

    private static final String EXCLUDE_PACK_PREFIX = "com.sun";

    private static final String EXCLUDE_FUN_PACK_PREFIX = "pro.shushi.k2.fun";

    private static final String PROTOCOL_FILE = "file";

    private static final String PROTOCOL_JAR = "jar";

    public static List<Class<?>> getAllClassByInterface(String packageName, Class<?> itf) {
        List<Class<?>> result = new ArrayList<>();
        // 过滤掉com.sum包的类
        if (packageName.startsWith(EXCLUDE_PACK_PREFIX)) {
            return result;
        }

        if (itf.isInterface()) {
            Set<Class<?>> clazzs = ClassUtils.getClasses(packageName);
            if (CollectionUtils.isNotEmpty(clazzs)) {
                for (Class<?> clazz : clazzs) {
                    if (itf.isAssignableFrom(clazz)) {
                        if (!itf.equals(clazz)) {
                            // 自身并不加进去
                            result.add(clazz);
                        }
                    }
                }
            }
        }
        return result;
    }

    public static List<Class<?>> getAllClassByPacksAndInterface(List<String> packs, Class<?> itf) {
        List<Class<?>> result = new ArrayList<>();
        if (itf.isInterface()) {
            Set<Class<?>> clazzs = ClassUtils.getClassesByPacks(packs);
            if (CollectionUtils.isNotEmpty(clazzs)) {
                for (Class<?> clazz : clazzs) {
                    if (itf.isAssignableFrom(clazz)) {
                        if (!itf.equals(clazz)) {
                            // 自身并不加进去
                            result.add(clazz);
                        }
                    }
                }
            }
        }
        return result;
    }

    public static Set<Class<?>> getClassesByPacks(List<String> packs) {
        return ParallelStreamHelper.parallelStream(mergePackages(packs))
                .map(ClassUtils::getClasses)
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static Set<Class<?>> getClassesByPacks(String... packs) {
        return ParallelStreamHelper.parallelStream(mergePackages(Lists.newArrayList(packs)))
                .map(ClassUtils::getClasses)
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static Set<String> mergePackages(List<String> packages) {
        if (packages.size() == 1) {
            return Sets.newHashSet(packages.get(0));
        }
        Map<String, MergedPackage> roots = new LinkedHashMap<>();
        for (String p : packages) {
            String[] ps = p.split("\\.");
            MergedPackage parent = null;
            for (int i = 0; i < ps.length; i++) {
                String s = ps[i];
                if (parent == null) {
                    parent = roots.get(s);
                    if (parent == null) {
                        parent = new MergedPackage(s);
                        roots.put(s, parent);
                    }
                } else {
                    parent = new MergedPackage(s, parent, i == ps.length - 1);
                }
            }
        }
        return roots.values().stream().map(MergedPackage::toPackages).flatMap(Collection::stream).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static Set<Class<?>> getClasses(String pack) {
        // 第一个class类的集合
        Set<Class<?>> classes = new LinkedHashSet<>();
        // 过滤掉com.sum包的类
        if (pack.startsWith(EXCLUDE_PACK_PREFIX)) {
            return classes;
        }
        // 是否循环迭代
        boolean recursive = true;
        // 获取包的名字 并进行替换
        String packageName = pack;
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = AppClassLoader.getClassLoader(ClassUtils.class).getResources(packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                packageName = pack;
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    // System.err.println("file类型的扫描");
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    // 如果是jar包文件.定义一个JarFile
                    // System.err.println("jar类型的扫描");
                    JarFile jar;
                    try {
                        // 获取jar
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        // 从此jar包 得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        // 同样的进行循环迭代
                        while (entries.hasMoreElements()) {
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            // 如果是以/开头的
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    // 获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                // 忽略jdk依赖类所在包
                                if (packageName.startsWith(EXCLUDE_FUN_PACK_PREFIX)) {
                                    continue;
                                }
                                // 如果可以迭代下去 并且是一个包
                                // 如果是一个.class文件 而且不是目录
                                if (name.endsWith(".class") && !entry.isDirectory()) {
                                    // 去掉后面的".class" 获取真正的类名
                                    String className = name.substring(packageName.length() + 1, name.length() - 6);
                                    try {
                                        // 添加到classes
                                        classes.add(Class.forName(packageName + '.' + className, false, AppClassLoader.getClassLoader(ClassUtils.class)));
                                    } catch (ClassNotFoundException e) {
                                        // log.error("添加用户自定义视图类错误 找不到此类的.class文件");
                                        log.error(BASE_CLASS_NOT_FOUNT_ERROR.msg(), e);
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        // log.error("在扫描用户定义视图时从jar包获取文件出错");
                        log.error(MetaExpEnumerate.BASE_IO_ERROR.msg(), e);
                    }
                }
            }
        } catch (IOException e) {
            log.error(MetaExpEnumerate.BASE_IO_ERROR.msg(), e);
        }

        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    private static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            // log.warn("用户定义包名 " + name + " 下没有任何文件");
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
        File[] dirfiles = dir.listFiles(file -> (recursive && file.isDirectory()) || (file.getName().endsWith(".class")));

        if (dirfiles == null) {
            return;
        }

        // 循环所有文件
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                // 忽略jdk依赖类所在包
                if (packageName.startsWith(EXCLUDE_FUN_PACK_PREFIX)) {
                    continue;
                }
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    // 添加到集合中去
                    //classes.add(Class.forName(name + '.' + className));
                    //经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
                    classes.add(AppClassLoader.getClassLoader(ClassUtils.class).loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    // log.error("添加用户自定义视图类错误 找不到此类的.class文件");
                    log.error(BASE_CLASS_NOT_FOUNT_ERROR.msg(), e);
                }
            }
        }
    }

    public static String getInherited(Class<?> modelClass) {
        String superClazz = modelClass.getSuperclass().getName();
        if (!superClazz.equalsIgnoreCase(BaseModel.class.getName())
                && !superClazz.equalsIgnoreCase(IdModel.class.getName())
                && !superClazz.equalsIgnoreCase(TransientModel.class.getName())) {
            return superClazz;
        }
        return null;
    }

    public static boolean isPresent(String clazz) {
        if (StringUtils.isBlank(clazz)) {
            return Boolean.FALSE;
        }
        try {
            AppClassLoader.getClassLoader(ClassUtils.class).loadClass(clazz);
        } catch (ClassNotFoundException e) {
            log.warn("{} 获取服务错误 {}", clazz, BASE_CLASS_NOT_FOUNT_ERROR.msg());
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public static boolean isInterface(String clazz) {
        return isInterface(TypeUtils.getClass(clazz));
    }

    public static boolean isInterface(Class<?> clazz) {
        return Modifier.isInterface(clazz.getModifiers());
    }

    public static boolean isImplementation(String clazz) {
        return isImplementation(TypeUtils.getClass(clazz));
    }

    public static boolean isImplementation(Class<?> clazz) {
        return Modifier.isPublic(clazz.getModifiers()) && !isInterface(clazz);
    }

    public static boolean isMulti(Class<?> clazz) {
        return TypeUtils.isCollection(clazz) || clazz.isArray();
    }

    public static boolean isNoClass(String className) {
        return clazzCache.computeIfAbsent(className, (cls) -> {
            try {
                Class.forName(className);
                return Boolean.FALSE;
            } catch (ClassNotFoundException ignored) {
                return Boolean.TRUE;
            }
        });
    }

    private static class MergedPackage {

        private final String packageName;

        private final MergedPackage parent;

        private final MergedPackage refer;

        private final Map<String, MergedPackage> children = new LinkedHashMap<>();

        private boolean isLeaf = false;

        public MergedPackage(String packageName) {
            this.refer = null;
            this.packageName = packageName;
            this.parent = null;
        }

        public MergedPackage(String packageName, MergedPackage parent, boolean isLeaf) {
            this.packageName = packageName;
            this.parent = parent;
            this.isLeaf = isLeaf;
            if (parent.refer == null) {
                this.refer = parent.add(this, isLeaf);
            } else {
                this.refer = parent.refer.add(this, isLeaf);
            }
        }

        public MergedPackage add(MergedPackage child, boolean isLeaf) {
            MergedPackage exist = this.children.get(child.packageName);
            if (exist == null) {
                this.children.put(child.packageName, child);
                return child;
            }
            if (isLeaf) {
                exist.isLeaf = true;
                exist.children.clear();
                return exist;
            }
            return exist;
        }

        public Set<String> toPackages() {
            if (this.isLeaf) {
                return Sets.newHashSet(this.toString());
            }
            Set<String> packages = new LinkedHashSet<>();
            for (MergedPackage child : this.children.values()) {
                packages.addAll(child.toPackages());
            }
            return packages;
        }

        @Override
        public final boolean equals(Object o) {
            if (!(o instanceof MergedPackage)) {
                return false;
            }
            MergedPackage that = (MergedPackage) o;
            return Objects.equals(packageName, that.packageName) && Objects.equals(parent, that.parent);
        }

        @Override
        public int hashCode() {
            return Objects.hash(packageName, parent);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (parent != null) {
                builder.append(parent).append(".");
            }
            builder.append(this.packageName);
            return builder.toString();
        }
    }
}
