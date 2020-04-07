package pro.shushi.pamirs.meta.dsl.definition.node;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("query")
public class Query extends Code {

	public Query(String id) {
		super();
		this.setId(id);
	}

	@XStreamAsAttribute
	private String model;

	@XStreamAsAttribute
	private String rsql;

	@XStreamAsAttribute
	private String aggs;

	@XStreamAsAttribute
	private Integer page;

	@XStreamAsAttribute
	private Integer size;

	@XStreamAsAttribute
	private String groupBy;

	@XStreamAsAttribute
	private String resultSchema;

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getRsql() {
		return rsql;
	}

	public void setRsql(String rsql) {
		this.rsql = rsql;
	}

	public String getAggs() {
		return aggs;
	}

	public void setAggs(String aggs) {
		this.aggs = aggs;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	public String getResultSchema() {
		return resultSchema;
	}

	public void setResultSchema(String resultSchema) {
		this.resultSchema = resultSchema;
	}

	@Override
	public String toString() {
		return "Query [id=" + this.getId() + ", description=" + this.getDesc() + ", model=" + model + ", rsql=" + rsql + ", aggs=" + aggs + ", page=" + page + ", size=" + size + ", groupBy=" + groupBy + "]";
	}

}
