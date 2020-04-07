package pro.shushi.pamirs.fun.model;

import pro.shushi.pamirs.fun.enmu.FunType;

import java.util.List;

/**
 * @author deng
 */
public class FunctionDefine {

    private FunType functionType;

    private String namespace;

    private String funName;

    private List<String> argTypes;

    private List<String> args;

    private String returnType;

    private String type;

    private String imports;

    private String context;

    private List<String> codes;

    public FunType getFunctionType() {
        return functionType;
    }

    public void setFunctionType(FunType functionType) {
        this.functionType = functionType;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getFunName() {
        return funName;
    }

    public void setFunName(String funName) {
        this.funName = funName;
    }

    public List<String> getArgTypes() {
        return argTypes;
    }

    public void setArgTypes(List<String> argTypes) {
        this.argTypes = argTypes;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImports() {
        return imports;
    }

    public void setImports(String imports) {
        this.imports = imports;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }
}
