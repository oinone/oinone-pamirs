package pro.shushi.pamirs.sid.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.KeyGeneratorEnum;
import pro.shushi.pamirs.sid.enmu.WorkerNodeType;

import java.util.Date;

import static pro.shushi.pamirs.sid.model.WorkerNode.MODEL_MODEL;

@MetaSimulator(onlyBasicTypeField = false)
@Base
@Model.model(MODEL_MODEL)
@Model(displayName = "主键生成工作节点")
public class WorkerNode extends IdModel {

    private static final long serialVersionUID = 7826402505848316994L;

    public final static String MODEL_MODEL = "base.WorkerNode";

    @Base
    @Field.PrimaryKey(keyGenerator = KeyGeneratorEnum.AUTO_INCREMENT)
    @Field.Integer
    @Field(displayName = "ID", summary = "ID字段，唯一自增索引", required = true, priority = 5)
    protected Long id;

    /**
     * Type of CONTAINER: HostName, ACTUAL : IP.
     */
    @Field(displayName = "主机名称")
    @Field.String
    private String hostName;

    /**
     * Type of CONTAINER: Port, ACTUAL : Timestamp + Random(0-10000)
     */
    @Field(displayName = "主机端口")
    @Field.String
    private String port;

    /**
     * type of {@link WorkerNodeType}
     */
    @Field(displayName = "节点类型")
    @Field.Integer
    private Integer type;

    /**
     * Worker launch date, default now
     */
    @Field(displayName = "节点启动时间")
    @Field.Date
    @Field.Advanced(columnDefinition = "datetime(6)")
    private Date launchDate;


    @Override
    public String toString() {
        return "WorkerNode{" +
                "hostName='" + hostName + '\'' +
                ", port='" + port + '\'' +
                ", type=" + type +
                ", launchDate=" + launchDate +
                '}';
    }
}
