package pro.shushi.pamirs.meta.api.enmu;

public enum ScriptType {

    LOCAL("LOCAL"/** local bean or non bean object */),
    DSL("DSL"/** USER DEFINE CODE */),
    REMOTE("REMOTE"/** rpc */),
    EL("EL"/** expression language */),
    GROOVY("GROOVY"/** groovy */),
    JS("JS"/** javascript */),
    SCRIPT("SCRIPT"/** script */);

    private String type;

    public String getType(){
        return type;
    }

    ScriptType(String value){
        this.type =value;
    }

}
