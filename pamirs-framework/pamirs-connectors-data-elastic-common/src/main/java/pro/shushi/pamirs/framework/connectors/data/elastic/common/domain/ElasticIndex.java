package pro.shushi.pamirs.framework.connectors.data.elastic.common.domain;

import java.io.Serializable;
import java.util.Date;

import static pro.shushi.pamirs.meta.common.constants.CharacterConstants.SEPARATOR_UNDERLINE;

/**
 * ElasticIndex
 *
 * @author yakir on 2020/04/16 17:55.
 */
public class ElasticIndex implements Serializable {

    private static final long serialVersionUID = -7639521675080536451L;

    private String  tenant;
    private String  index;
    private String  alias;
    private Integer pos = 0;
    private String  shards;
    private String  replicas;
    private Date    createDate;
    private Date    editDate;

    public String relIndex() {
        return index + SEPARATOR_UNDERLINE + pos;
    }

    public String getTenant() {
        return tenant;
    }

    public ElasticIndex setTenant(String tenant) {
        this.tenant = tenant;
        return this;
    }

    public String getIndex() {
        return index;
    }

    public ElasticIndex setIndex(String index) {
        this.index = index;
        return this;
    }

    public String getAlias() {
        return alias;
    }

    public ElasticIndex setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public Integer getPos() {
        return pos;
    }

    public ElasticIndex setPos(Integer pos) {
        this.pos = pos;
        return this;
    }

    public String getShards() {
        return shards;
    }

    public ElasticIndex setShards(String shards) {
        this.shards = shards;
        return this;
    }

    public String getReplicas() {
        return replicas;
    }

    public ElasticIndex setReplicas(String replicas) {
        this.replicas = replicas;
        return this;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public ElasticIndex setCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }

    public Date getEditDate() {
        return editDate;
    }

    public ElasticIndex setEditDate(Date editDate) {
        this.editDate = editDate;
        return this;
    }

    @Override
    public String toString() {
        return "ElasticIndex{" +
                "tenant='" + tenant + '\'' +
                ", index='" + index + '\'' +
                ", alias='" + alias + '\'' +
                ", pos=" + pos +
                ", shards=" + shards +
                ", replicas=" + replicas +
                ", createDate=" + createDate +
                ", editDate=" + editDate +
                '}';
    }
}
