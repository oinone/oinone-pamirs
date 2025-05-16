package pro.shushi.pamirs.core.common.function.context;

import java.io.Serializable;

public class ArgumentContext implements IArgument, Serializable {

    private static final long serialVersionUID = -6715886447220257860L;

    private String name;

    private String ltype;

    private String ltypeT;

    private String model;

    @Override
    public String getName() {
        return name;
    }

    public ArgumentContext setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getLtype() {
        return ltype;
    }

    public ArgumentContext setLtype(String ltype) {
        this.ltype = ltype;
        return this;
    }

    @Override
    public String getLtypeT() {
        return ltypeT;
    }

    public ArgumentContext setLtypeT(String ltypeT) {
        this.ltypeT = ltypeT;
        return this;
    }

    public String getModel() {
        return model;
    }

    public ArgumentContext setModel(String model) {
        this.model = model;
        return this;
    }
}