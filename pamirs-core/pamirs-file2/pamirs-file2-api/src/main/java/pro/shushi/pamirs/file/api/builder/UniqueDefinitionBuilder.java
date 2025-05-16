package pro.shushi.pamirs.file.api.builder;

import pro.shushi.pamirs.core.common.builder.IBuilder;
import pro.shushi.pamirs.file.api.model.ExcelUniqueDefinition;

import java.util.ArrayList;
import java.util.List;

public class UniqueDefinitionBuilder<T> extends AbstractBaseBuilder<T> implements IBuilder<ExcelUniqueDefinition> {

    private final String model;

    private final List<String> uniques = new ArrayList<>();

    public UniqueDefinitionBuilder<T> addUnique(String field) {
        uniques.add(field);
        return this;
    }

    public UniqueDefinitionBuilder(T builder, String model) {
        super(builder);
        this.model = model;
    }

    @Override
    public ExcelUniqueDefinition build() {
        return new ExcelUniqueDefinition()
                .setModel(model)
                .setUniques(uniques);
    }
}
