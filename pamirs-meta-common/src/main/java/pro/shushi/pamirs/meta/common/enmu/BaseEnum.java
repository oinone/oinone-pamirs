package pro.shushi.pamirs.meta.common.enmu;

import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.common.util.AppClassLoader;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 抽象枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/17 1:53 上午
 */
public abstract class BaseEnum<T extends Serializable> implements IEnum<T> {

    public final static Logger log = LoggerFactory.getLogger(BaseEnum.class.getName());

    private String name;

    private T value;

    private String displayName;

    private String help;

    private String icon;

    private String color;

    private String extend1;

    private String extend2;

    protected static <T extends Serializable, E extends BaseEnum<T>> E of(Class<E> enumClass){
        try {
            final Constructor construct = enumClass.getDeclaredConstructor();
            java.security.AccessController.doPrivileged(
                    (PrivilegedAction<Void>) () -> {
                        construct.setAccessible(true);
                        return null;
                    });
            E ienum = (E) construct.newInstance();
            return ienum;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public <E extends BaseEnum<T>> E init(IEnum<T> ienum){
        this.setName(ienum.name());
        this.setValue(ienum.value());
        this.setDisplayName(ienum.displayName());
        this.setHelp(ienum.help());
        this.setColor(ienum.color());
        this.setIcon(ienum.icon());
        this.setExtend1(ienum.extend1());
        this.setExtend2(ienum.extend2());
        return (E)this;
    }

    public <E extends BaseEnum<T>> E init(String name, T value, String displayName, String help){
        this.setName(name);
        this.setValue(value);
        this.setDisplayName(displayName);
        this.setHelp(help);
        return (E)this;
    }

    protected static final Class getCurrentClass(){
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        Class current = null;
        boolean flag = false;
        for(StackTraceElement stackTraceElement : stackTraceElements){
            Class enumClass;
            try {
                enumClass = Class.forName(stackTraceElement.getClassName());
            } catch (ClassNotFoundException e) {
                break;
            }
            if(IEnum.class.isAssignableFrom(enumClass)){
                current = enumClass;
                flag = true;
            }
            if(flag && !IEnum.class.isAssignableFrom(enumClass)){
                break;
            }
        }
        return current;
    }

    protected static <E extends IEnum> E valueOf(String name){
        Class current = getCurrentClass();
        if(null == current || BaseEnum.class.equals(current)){
            throw new RuntimeException("请在枚举实现类中重写valueOf方法，public static 枚举类 valueOf(String name){return BaseEnum.valueOf(name);}");
        }
        return (E)valueOf(current, name);
    }

    public static <E extends IEnum> E valueOf(Class<E> enumType, String name){
        if(!IEnum.class.isAssignableFrom(enumType)){
            return null;
        }
        for(Field field : enumType.getFields()){
            try {
                if(IEnum.class.isAssignableFrom(field.getType())){
                    E e = (E)field.get(enumType);
                    if(e.name().equals(name)){
                        return e;
                    }
                }
            } catch (IllegalAccessException e) {
                log.error(enumType.getName() + ",name:" + name, e);
                return null;
            }
        }
        return null;
    }

    protected static <E extends IEnum> E valueFor(String value){
        Class<E> current = getCurrentClass();
        if(null == current || BaseEnum.class.equals(current)){
            throw new RuntimeException("请在枚举实现类中重写valueFor方法，public static 枚举类 valueFor(String value){return BaseEnum.valueFor(value);}");
        }
        return valueFor(current, value);
    }

    public static <E extends IEnum> E valueFor(Class<E> enumType, String value){
        for(Field field : enumType.getFields()){
            try {
                if(IEnum.class.isAssignableFrom(field.getType())){
                    E e = (E)field.get(enumType);
                    if(e.value().equals(value)){
                        return e;
                    }
                }
            } catch (IllegalAccessException e) {
                log.error(enumType.getName() + ",value:" + value, e);
                return null;
            }
        }
        return null;
    }

    protected static <E extends IEnum> E[] values(Class<E> enumType){
        Field[] fields = enumType.getFields();
        List<E> result = new ArrayList<>();
        for( Field field : fields ){
            try {
                if(BaseEnum.class.isAssignableFrom(field.getType())){
                    result.add((E)field.get(enumType.getClass()));
                }
            } catch (IllegalAccessException e) {
                log.error(enumType.getClass().getName(), e);
                return null;
            }
        }
        try {
            Class arrayClass = Class.forName("[L" + enumType.getName() + ";");
            return (E[])Arrays.copyOf(result.toArray(), result.size(), arrayClass);
        } catch (ClassNotFoundException e) {
            log.error(enumType.getClass().getName(), e);
        }
        return null;
    }

    protected static <E extends IEnum> E[] values(){
        Class<E> current = getCurrentClass();
        if(null == current || BaseEnum.class.equals(current)){
            throw new RuntimeException("请在枚举实现类中重写values方法，public static 枚举类[] values(){return BaseEnum.values();}");
        }
        return values(current);
    }

    @Override
    public String displayName(){
        return this.displayName;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public T value(){
        return this.value;
    }

    @Override
    public String help(){
        return this.help;
    }

    @Override
    public String color(){
        return this.color;
    }

    @Override
    public String icon(){
        return this.icon;
    }

    @Override
    public String extend1(){
        return this.extend1;
    }

    @Override
    public String extend2(){
        return this.extend2;
    }

    @Override
    public int ordinal(){
        int i = 0;
        for(IEnum item : values(this.getClass())){
            if(item.name().equals(name)){
                return i;
            }
            i++;
        }
        return i;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setExtend1(String extend1) {
        this.extend1 = extend1;
    }

    public void setExtend2(String extend2) {
        this.extend2 = extend2;
    }

}
