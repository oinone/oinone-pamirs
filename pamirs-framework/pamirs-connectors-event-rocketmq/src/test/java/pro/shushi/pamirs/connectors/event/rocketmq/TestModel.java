package pro.shushi.pamirs.connectors.event.rocketmq;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestModel implements Serializable {

    private Long testLongObject = 1024L;

    private Integer testIntegerObject = 1024;

    private Double testDoubleObject = 1024.1024D;

    private Float testFloatObject = 1024.1024F;

    private long testLong = 1024L;

    private int testInteger = 1024;

    private double testDouble = 1024.1024D;

    private float testFloat = 1024.1024F;

    private String testString = "abc";

    private TestEnum testEnum = TestEnum.A;

    private TestModel testModel = null;

    private List<TestModel> testModelList = new ArrayList<>();

    private Map<String, Object> testMap = new HashMap<>();

    public Long getTestLongObject() {
        return testLongObject;
    }

    public void setTestLongObject(Long testLongObject) {
        this.testLongObject = testLongObject;
    }

    public Integer getTestIntegerObject() {
        return testIntegerObject;
    }

    public void setTestIntegerObject(Integer testIntegerObject) {
        this.testIntegerObject = testIntegerObject;
    }

    public Double getTestDoubleObject() {
        return testDoubleObject;
    }

    public void setTestDoubleObject(Double testDoubleObject) {
        this.testDoubleObject = testDoubleObject;
    }

    public Float getTestFloatObject() {
        return testFloatObject;
    }

    public void setTestFloatObject(Float testFloatObject) {
        this.testFloatObject = testFloatObject;
    }

    public long getTestLong() {
        return testLong;
    }

    public void setTestLong(long testLong) {
        this.testLong = testLong;
    }

    public int getTestInteger() {
        return testInteger;
    }

    public void setTestInteger(int testInteger) {
        this.testInteger = testInteger;
    }

    public double getTestDouble() {
        return testDouble;
    }

    public void setTestDouble(double testDouble) {
        this.testDouble = testDouble;
    }

    public float getTestFloat() {
        return testFloat;
    }

    public void setTestFloat(float testFloat) {
        this.testFloat = testFloat;
    }

    public String getTestString() {
        return testString;
    }

    public void setTestString(String testString) {
        this.testString = testString;
    }

    public TestEnum getTestEnum() {
        return testEnum;
    }

    public void setTestEnum(TestEnum testEnum) {
        this.testEnum = testEnum;
    }

    public TestModel getTestModel() {
        return testModel;
    }

    public void setTestModel(TestModel testModel) {
        this.testModel = testModel;
    }

    public List<TestModel> getTestModelList() {
        return testModelList;
    }

    public void setTestModelList(List<TestModel> testModelList) {
        this.testModelList = testModelList;
    }

    public Map<String, Object> getTestMap() {
        return testMap;
    }

    public void setTestMap(Map<String, Object> testMap) {
        this.testMap = testMap;
    }
}
