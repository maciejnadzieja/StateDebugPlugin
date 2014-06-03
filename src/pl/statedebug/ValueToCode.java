package pl.statedebug;

import com.google.common.base.CaseFormat;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.sun.jdi.Field;
import com.sun.jdi.Value;
import com.sun.tools.jdi.*;

import java.util.Map;

public class ValueToCode {
    public String convert(Value value) {
        if (value == null) {
            return null;
        }
        if (value instanceof IntegerValueImpl) {
            return String.valueOf(((IntegerValueImpl) value).value());
        }
        if (value instanceof DoubleValueImpl) {
            return String.valueOf(((DoubleValueImpl) value).value());
        }
        if (value instanceof FloatValueImpl) {
            return String.valueOf(((FloatValueImpl) value).value()) + "f";
        }
        if (value instanceof ShortValueImpl) {
            return String.valueOf(((ShortValueImpl) value).value());
        }
        if (value instanceof ByteValueImpl) {
            return String.valueOf(((ByteValueImpl) value).value());
        }
        if (value instanceof LongValueImpl) {
            return String.valueOf(((LongValueImpl) value).value()) + "l";
        }
        if (value instanceof CharValueImpl) {
            return "'" + String.valueOf(((CharValueImpl) value).value()) + "'";
        }
        if (value instanceof BooleanValueImpl) {
            return String.valueOf(((BooleanValueImpl) value).value());
        }
        if (value instanceof StringReferenceImpl) {
            return "\"" + ((StringReferenceImpl) value).value() + "\"";
        }
        if (value instanceof ObjectReferenceImpl) {
            ObjectReferenceImpl objectValue = (ObjectReferenceImpl) value;
            String code = getCodeForObjectCreation(getClassTypeName((ClassTypeImpl) objectValue.type()));
            code += getCodeForObjectFields(objectValue);
            return code;
        }

        throw new UnsupportedOperationException();
    }

    private String getClassTypeName(ClassTypeImpl type) {
        return Iterables.getLast(Splitter.on(".").split(type.name()));
    }

    private String getCodeForObjectCreation(String typeName) {
        return String.format("%s %s = new %s();\n", typeName, toVarName(typeName), typeName);
    }

    private String getCodeForObjectFields(ObjectReferenceImpl objectValue) {
        ClassTypeImpl type = (ClassTypeImpl) objectValue.type();
        String code = "";
        for (Map.Entry<Field, Value> fieldValueEntry : objectValue.getValues(type.fields()).entrySet()) {
            code = getCodeForObjectField(type, fieldValueEntry);
        }
        return code;
    }

    private String getCodeForObjectField(ClassTypeImpl type, Map.Entry<Field, Value> fieldValueEntry) {
        if (fieldValueEntry.getValue() instanceof PrimitiveValueImpl || fieldValueEntry.getValue() instanceof StringReferenceImpl) {
            return String.format("%s.%s(%s);\n",
                    toVarName(getClassTypeName(type)),
                    toSetterName(fieldValueEntry),
                    convert(fieldValueEntry.getValue()));
        } else {
            return convert(fieldValueEntry.getValue())
                    + String.format("%s.%s(%s);\n",
                    toVarName(getClassTypeName(type)),
                    toSetterName(fieldValueEntry),
                    toVarName(fieldValueEntry.getKey().name()));
        }
    }

    private String toSetterName(Map.Entry<Field, Value> fieldValueEntry) {
        return "set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, fieldValueEntry.getKey().name());
    }

    private String toVarName(String typeName) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, typeName);
    }

}
