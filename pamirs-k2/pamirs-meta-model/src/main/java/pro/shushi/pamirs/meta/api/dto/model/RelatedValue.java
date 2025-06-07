package pro.shushi.pamirs.meta.api.dto.model;

import java.io.Serializable;

public class RelatedValue implements Serializable {

    private static final long serialVersionUID = -8451491244490105414L;

    private String relatedModel;

    private Object relatedValue;

    public String getRelatedModel() {
        return relatedModel;
    }

    public RelatedValue setRelatedModel(String relatedModel) {
        this.relatedModel = relatedModel;
        return this;
    }

    public Object getRelatedValue() {
        return relatedValue;
    }

    public RelatedValue setRelatedValue(Object relatedValue) {
        this.relatedValue = relatedValue;
        return this;
    }

}