package pl.statedebug;

import com.google.common.base.CaseFormat;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.sun.jdi.*;

import java.util.Map;

public class ValueToCode {
    public String convert(Value value) {
        if (value == null) {
            return null;
        }
        if (value instanceof IntegerValue) {
            return String.valueOf(((IntegerValue) value).value());
        }
        if (value instanceof DoubleValue) {
            return String.valueOf(((DoubleValue) value).value());
        }
        if (value instanceof FloatValue) {
            return String.valueOf(((FloatValue) value).value()) + "f";
        }
        if (value instanceof ShortValue) {
            return String.valueOf(((ShortValue) value).value());
        }
        if (value instanceof ByteValue) {
            return String.valueOf(((ByteValue) value).value());
        }
        if (value instanceof LongValue) {
            return String.valueOf(((LongValue) value).value()) + "l";
        }
        if (value instanceof CharValue) {
            return "'" + String.valueOf(((CharValue) value).value()) + "'";
        }
        if (value instanceof BooleanValue) {
            return String.valueOf(((BooleanValue) value).value());
        }
        if (value instanceof StringReference) {
            return "\"" + ((StringReference) value).value() + "\"";
        }
        if (value instanceof ObjectReference) {
            ObjectReference objectValue = (ObjectReference) value;
            String code = getCodeForObjectCreation(getClassTypeName((ClassType) objectValue.type()));
            code += getCodeForObjectFields(objectValue);
            return code;
        }

        throw new UnsupportedOperationException();
    }

    private String getClassTypeName(ClassType type) {
        return Iterables.getLast(Splitter.on(".").split(type.name()));
    }

    private String getCodeForObjectCreation(String typeName) {
        return String.format("%s %s = new %s();\n", typeName, toVarName(typeName), typeName);
    }

    private String getCodeForObjectFields(ObjectReference objectValue) {
        ClassType type = (ClassType) objectValue.type();
        StringBuilder codeBuilder = new StringBuilder();
        for (Map.Entry<Field, Value> fieldValueEntry : objectValue.getValues(type.fields()).entrySet()) {
            codeBuilder.append(getCodeForObjectField(type, fieldValueEntry));
        }
        return codeBuilder.toString();
    }

    private String getCodeForObjectField(ClassType type, Map.Entry<Field, Value> fieldValueEntry) {
        if (fieldValueEntry.getValue() instanceof PrimitiveValue || fieldValueEntry.getValue() instanceof StringReference) {
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
