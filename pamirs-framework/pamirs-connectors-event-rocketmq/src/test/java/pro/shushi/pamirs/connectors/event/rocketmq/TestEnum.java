package pro.shushi.pamirs.connectors.event.rocketmq;

public enum TestEnum {

    A("A", 1), B("B", 2);

    private String name;
    private Integer value;

    TestEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Integer getValue() {
        return value;
    }
}
