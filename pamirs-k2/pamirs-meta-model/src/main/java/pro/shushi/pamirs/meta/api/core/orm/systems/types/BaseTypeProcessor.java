package pro.shushi.pamirs.meta.api.core.orm.systems.types;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.TypeProcessor;
import pro.shushi.pamirs.meta.base.K2;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static pro.shushi.pamirs.meta.enmu.MetaExpEnumerate.BASE_FIELD_LTTYPE_CONFIG_ERROR;
import static pro.shushi.pamirs.meta.enmu.MetaExpEnumerate.BASE_FIELD_TCTYPE_CONFIG_ERROR;
import static pro.shushi.pamirs.meta.enmu.TtypeEnum.*;

/**
 * 类型处理器默认实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:48 上午
 */
@SuppressWarnings("unused")
@Slf4j
@SPI.Service
@Component
public class BaseTypeProcessor implements TypeProcessor {

    @Override
    public String defaultTtypeFromLtype(String ltype, String ltypeT, String serialize) {
        if (!StringUtils.isBlank(serialize)) {
            return TtypeEnum.STRING.value();
        }
        String type = ltype;
        boolean multi = false;
        if (TypeUtils.isCollection(ltype) || TypeUtils.isArrayType(ltype)) {
            type = ltypeT;
            multi = true;
        }
        if (StringUtils.isBlank(type)) {
            return TtypeEnum.STRING.value();
        } else if (TypeUtils.isModelOrMapModelClass(type) || K2.class.getName().equals(type)) {
            if (multi) {
                return O2M.value();
            } else {
                return M2O.value();
            }
        } else if (TypeUtils.isMap(type)) {
            return MAP.value();
        } else if (TypeUtils.isIEnumClass(type)) {
            return ENUM.value();
        } else if (Object.class.getName().equals(type) || TypeUtils.isComplexTypeExceptMap(type)) {
            return OBJ.value();
        }
        switch (type) {
            case "java.lang.String":
                return STRING.value();
            case "java.lang.Byte":
                return BINARY.value();
            case "java.lang.Integer":
            case "java.lang.Short":
            case "java.lang.Long":
            case "java.math.BigInteger":
                return INTEGER.value();
            case "java.lang.Float":
            case "java.lang.Double":
            case "java.math.BigDecimal":
                return FLOAT.value();
            case "java.lang.Boolean":
                return BOOLEAN.value();
            case "java.util.Date":
            case "java.sql.Timestamp":
                return DATETIME.value();
            case "java.sql.Date":
                return DATE.value();
            case "java.sql.Time":
                return TIME.value();
            case "void":
            case "java.lang.Void":
                return VOID.value();
            default:
                throw PamirsException.construct(BASE_FIELD_LTTYPE_CONFIG_ERROR)
                        .appendMsg(I18nUtils.getMessage("pamirs.meta.model.unsupportedType", ltype, ltypeT)).errThrow();
        }
    }

    @Override
    public String defaultColumnTypeFromTtype(final String ttype, final String ltype, final Boolean multi, final Boolean bit,
                                             final Integer size, final Integer decimal) {
        if (TypeUtils.isCollection(ltype) || TypeUtils.isArrayType(ltype)) {
            return "varchar(" + (isNullOrNegative(size) ? DEFAULT_MULTI : size) + ")";
        }
        if (TtypeEnum.ENUM.value().equals(ttype)) {
            if (TypeUtils.isIEnumClass(ltype)) {
                String valueType = Objects.requireNonNull(TypeUtils.getEnumValueType(ltype)).getTypeName();
                String valueTtype = defaultTtypeFromLtype(valueType, null, null);
                return defaultColumnTypeFromTtype(valueTtype, valueType, multi, bit, size, decimal);
            } else if (null != multi && multi && Long.class.getName().equals(ltype) || null != bit && bit) {
                return "bigint";
            } else {
                int computeSize = isNullOrNegative(size) ? DEFAULT_MULTI : size;
                return "varchar(" + computeSize + ")";
            }
        }
        return BaseEnum.<String, String>switchGet(ttype, caseValue(),
                cases(RELATED).to(() -> {
                    String fieldType = defaultTtypeFromLtype(ltype, null, null);
                    return defaultColumnTypeFromTtype(fieldType, ltype, multi, bit, size, decimal);
                }),
                cases(BINARY).to(() -> multi ? "blob" : "tinyint"),
                cases(INTEGER).to(() -> {
                    Integer computeSize = fetchDefaultSizeForInteger(ltype, size);
                    if (computeSize <= DEFAULT_SHORT) {
                        return "smallint";
                    } else if (computeSize <= DEFAULT_INTEGER) {
                        return "int";
                    } else if (computeSize <= DEFAULT_BIGINT_LIMIT) {
                        return "bigint";
                    } else {
                        return "decimal(" + computeSize + ",0)";
                    }
                }),
                cases(UID).to(() -> "bigint"),
                cases(FLOAT).to(() -> {
                    Integer computeSize = fetchDefaultSizeForFloat(ltype, size);
                    Integer newDecimal = fetchDefaultDecimal(computeSize, decimal);
                    String columnDefinition = null;
                    if (Float.class.getName().equals(ltype)) {
                        columnDefinition = "float";
                    } else if (Double.class.getName().equals(ltype)) {
                        columnDefinition = "double";
                    } else if (BigDecimal.class.getName().equals(ltype)) {
                        columnDefinition = "decimal";
                    } else {
                        if (computeSize <= DEFAULT_FLOAT) {
                            return "float";
                        } else if (computeSize <= DEFAULT_DOUBLE) {
                            return "double";
                        } else {
                            return "decimal";
                        }
                    }
                    return columnDefinition + "(" + computeSize + "," + newDecimal + ")";

                }),
                cases(BOOLEAN).to(() -> "tinyint(1)"),
                cases(DATETIME).to(() -> {
                    String prefix = "datetime";
                    if (Long.class.getName().equals(ltype)) {
                        return "bigint";
                    } else if (Timestamp.class.getName().equals(ltype)) {
                        prefix = "timestamp";
                    }
                    if (isNullOrNegative(decimal)) {
                        return prefix;
                    }
                    return prefix + "(" + decimal + ")";
                }),
                cases(YEAR).to(() -> {
                    if (Long.class.getName().equals(ltype)) {
                        return "bigint";
                    }
                    return "year";
                }),
                cases(DATE).to(() -> {
                    if (Long.class.getName().equals(ltype)) {
                        return "bigint";
                    }
                    return "date";
                }),
                cases(TIME).to(() -> {
                    if (Long.class.getName().equals(ltype)) {
                        return "bigint";
                    }
                    if (isNullOrNegative(decimal)) {
                        return "time";
                    }
                    return "time(" + decimal + ")";
                }),
                cases(MONEY).to(() -> {
                    int computeSize = isNullOrNegative(size) ? DEFAULT_DECIMAL : size;
                    int newDecimal = isNullOrLessThanZero(decimal) ? DEFAULT_DECIMAL_DECIMAL : decimal;
                    return "decimal(" + computeSize + "," + newDecimal + ")";
                }),
                cases(HTML, TEXT, O2O, O2M, M2O, M2M, OBJ, MAP).to(() -> "text"),
                cases(STRING, PHONE).to(() -> {
                    int computeSize = isNullOrNegative(size) ? DEFAULT_STRING : size;
                    return "varchar(" + computeSize + ")";
                }),
                cases(EMAIL).to(() -> "varchar(" + DEFAULT_EMAIL + ")"),
                defaults(() -> {
                    throw PamirsException.construct(BASE_FIELD_TCTYPE_CONFIG_ERROR).appendMsg(I18nUtils.getMessage("pamirs.meta.model.unsupportedType.ttype", ttype)).errThrow();
                })
        );
    }

    @Override
    public Integer fetchDefaultDecimal(Integer size, Integer decimal) {
        if (isNullOrNegative(decimal)) {
            if (size <= DEFAULT_FLOAT) {
                decimal = DEFAULT_FLOAT_DECIMAL;
            } else if (size <= DEFAULT_DOUBLE) {
                decimal = DEFAULT_DOUBLE_DECIMAL;
            } else {
                decimal = DEFAULT_DECIMAL_DECIMAL;
            }

            if (size.equals(decimal) || size < decimal) {
                decimal = 0;
            }
        }
        return decimal;
    }

    @Override
    public Integer fetchDefaultSize(final TtypeEnum ttype, final String ltype, Boolean multi) {
        if (null != multi && multi) {
            return null;
        }
        return BaseEnum.<TtypeEnum, Integer>switchGet(ttype,
                cases(INTEGER).to(() -> fetchDefaultSizeForInteger(ltype, null)),
                cases(FLOAT).to(() -> fetchDefaultSizeForFloat(ltype, null)),
                cases(MONEY).to(() -> DEFAULT_DECIMAL),
                cases(STRING).to(() -> DEFAULT_STRING),
                defaults(() -> null)
        );
    }

    @Override
    public Integer fetchDefaultSizeForFloat(String ltype, Integer size) {
        if (Float.class.getName().equals(ltype)) {
            if (null == size || size <= 0 || size > DEFAULT_FLOAT) {
                size = DEFAULT_FLOAT;
            }
        } else if (Double.class.getName().equals(ltype)) {
            if (null == size || size <= 0 || size > DEFAULT_DOUBLE) {
                size = DEFAULT_DOUBLE;
            }
        } else if (BigDecimal.class.getName().equals(ltype)) {
            if (null == size || size <= 0 || size > DEFAULT_DECIMAL) {
                size = DEFAULT_DECIMAL;
            }
        }
        return size;
    }

    @Override
    public Integer fetchDefaultSizeForInteger(String ltype, Integer size) {
        if (Short.class.getName().equals(ltype)) {
            if (null == size || size <= 0 || size > DEFAULT_SHORT) {
                size = DEFAULT_SHORT;
            }
        } else if (Integer.class.getName().equals(ltype)) {
            if (null == size || size <= 0 || size > DEFAULT_INTEGER) {
                size = DEFAULT_INTEGER;
            }
        } else if (Long.class.getName().equals(ltype)) {
            if (null == size || size <= 0 || size > DEFAULT_BIGINT) {
                size = DEFAULT_BIGINT;
            }
        } else if (BigInteger.class.getName().equals(ltype)) {
            if (null == size || size <= 0 || size > DEFAULT_DECIMAL) {
                size = DEFAULT_DECIMAL;
            }
        }
        return size;
    }

    @Override
    public String columnDefinition(String columnType, Boolean nullable, String defaultValue, String extra) {
        List<String> defs = new ArrayList<>();
        defs.add(StringUtils.upperCase(columnType));
        if ((null == nullable || nullable) && null == defaultValue) {
            defs.add("DEFAULT NULL");
        } else if (null != nullable && !nullable) {
            defs.add("NOT NULL");
        }
        if (null != defaultValue) {
            defs.add("DEFAULT");
            if (Boolean.TRUE.toString().equals(defaultValue)) {
                defs.add("'1'");
            } else if (Boolean.FALSE.toString().equals(defaultValue)) {
                defs.add("'0'");
            } else if ("CURRENT_TIMESTAMP".equals(defaultValue)) {
                defs.add(defaultValue);
            } else {
                defs.add("'" + defaultValue + "'");
            }
        }
        if (null != extra) {
            String extraLowerCase = extra.toLowerCase();
            if (extraLowerCase.contains("on update current_timestamp")) {
                defs.add("ON UPDATE CURRENT_TIMESTAMP");
            } else if (extraLowerCase.contains("auto_increment")) {
                defs.add("AUTO_INCREMENT");
            }
        }
        return StringUtils.join(defs, CharacterConstants.SEPARATOR_BLANK);
    }

    @Override
    public boolean isRelationField(String ttype) {
        return BaseEnum.<String, Boolean>switchGet(ttype, caseValue(),
                cases(O2O, O2M, M2O, M2M).to(() -> true),
                defaults(() -> false)
        );
    }

    @Override
    public boolean isRelatedField(String ttype) {
        return TtypeEnum.isRelatedType(ttype);
    }

    @Override
    public boolean isRelationRelatedField(String ttype, String relatedTtype) {
        return isRelatedField(ttype) && isRelationField(relatedTtype);
    }

    @Override
    public boolean isEnumField(String ttype) {
        return ENUM.value().equals(ttype);
    }

    @Override
    public boolean isBasicField(String ttype) {
        return TtypeEnum.isBasicType(ttype);
    }

    @SuppressWarnings("unused")
    protected boolean isNullOrNegative(Short value) {
        return null == value || value <= 0;
    }

    protected boolean isNullOrNegative(Integer value) {
        return null == value || value <= 0;
    }

    protected boolean isNullOrLessThanZero(Integer value) {
        return null == value || value < 0;
    }

    @SuppressWarnings("unused")
    private Boolean isNullOrZero(Short value) {
        return null == value || 0 == value;
    }

    private Boolean isNullOrZero(Integer value) {
        return null == value || 0 == value;
    }

}
