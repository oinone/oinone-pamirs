package pro.shushi.pamirs.framework.gateways.graph.coercing;

import com.alibaba.fastjson.JSONObject;
import graphql.Assert;
import graphql.language.*;
import graphql.scalars.util.Kit;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapScalarCoercing implements Coercing<Object, Object> {

    public final static String GraphQLMap = "Map";

    public Object serialize(Object input) throws CoercingSerializeException {
        return input;
    }

    public Object parseValue(Object input) throws CoercingParseValueException {
        return input;
    }

    public Object parseLiteral(Object input) throws CoercingParseLiteralException {
        return this.parseLiteral(input, Collections.emptyMap());
    }

    public Object parseLiteral(Object input, Map<String, Object> variables) throws CoercingParseLiteralException {
        if (!(input instanceof Value)) {
            throw new CoercingParseLiteralException("Expected AST type 'StringValue' but was '" + Kit.typeName(input) + "'.");
        } else if (input instanceof NullValue) {
            return null;
        } else if (input instanceof ObjectValue) {
            List<ObjectField> values = ((ObjectValue) input).getObjectFields();
            Map<String, Object> parsedValues = new LinkedHashMap<>();
            values.forEach((fld) -> {
                Object parsedValue = mapParseLiteral(fld.getValue(), variables);
                parsedValues.put(fld.getName(), parsedValue);
            });
            return parsedValues;
        } else if (input instanceof StringValue) {
            String value = ((StringValue) input).getValue();
            if (JSONObject.isValidObject(value)) {
                return JsonUtils.parseMap(value);
            } else if (JSONObject.isValidArray(value)) {
                return JsonUtils.parseMapList(value);
            } else {
                return Assert.assertShouldNeverHappen("Map type is not support this value.");
            }
        } else {
            return Assert.assertShouldNeverHappen("Map type is not support this value.");
        }
    }

    public static Object mapParseLiteral(Object input, Map<String, Object> variables) throws CoercingParseLiteralException {
        if (!(input instanceof Value)) {
            throw new CoercingParseLiteralException("Expected AST type 'StringValue' but was '" + Kit.typeName(input) + "'.");
        } else if (input instanceof NullValue) {
            return null;
        } else if (input instanceof FloatValue) {
            return ((FloatValue) input).getValue();
        } else if (input instanceof StringValue) {
            return ((StringValue) input).getValue();
        } else if (input instanceof IntValue) {
            return ((IntValue) input).getValue();
        } else if (input instanceof BooleanValue) {
            return ((BooleanValue) input).isValue();
        } else if (input instanceof EnumValue) {
            return ((EnumValue) input).getName();
        } else if (input instanceof VariableReference) {
            String varName = ((VariableReference) input).getName();
            return variables.get(varName);
        } else {
            if (input instanceof ArrayValue) {
                @SuppressWarnings("rawtypes") List<Value> values = ((ArrayValue) input).getValues();
                return values.stream().map((v) -> MapScalarCoercing.mapParseLiteral(v, variables)).collect(Collectors.toList());
            } else if (input instanceof ObjectValue) {
                List<ObjectField> values = ((ObjectValue) input).getObjectFields();
                Map<String, Object> parsedValues = new LinkedHashMap<>();
                values.forEach((fld) -> {
                    Object parsedValue = MapScalarCoercing.mapParseLiteral(fld.getValue(), variables);
                    parsedValues.put(fld.getName(), parsedValue);
                });
                return parsedValues;
            } else {
                return Assert.assertShouldNeverHappen("We have covered all Value types");
            }
        }
    }


}

