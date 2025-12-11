package pro.shushi.pamirs.ux.quickfilling.converter;

import java.util.List;

/**
 * 快速填报列
 *
 * @author Adamancy Zhang at 12:23 on 2025-11-27
 */
public class QuickFillingColumn {

    private final String model;

    private final String field;

    private final String ttype;

    private boolean multi;

    private String dictionary;

    private String references;

    private boolean required;

    private boolean validate;

    private List<String> labelFields;

    public QuickFillingColumn(String model, String field, String ttype) {
        this.model = model;
        this.field = field;
        this.ttype = ttype;
    }

    public String getModel() {
        return model;
    }

    public String getField() {
        return field;
    }

    public String getTtype() {
        return ttype;
    }

    public boolean isMulti() {
        return multi;
    }

    public void setMulti(boolean multi) {
        this.multi = multi;
    }

    public String getDictionary() {
        return dictionary;
    }

    public void setDictionary(String dictionary) {
        this.dictionary = dictionary;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public boolean isRequired() {
        return validate && required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public List<String> getLabelFields() {
        return labelFields;
    }

    public void setLabelFields(List<String> labelFields) {
        this.labelFields = labelFields;
    }
}
