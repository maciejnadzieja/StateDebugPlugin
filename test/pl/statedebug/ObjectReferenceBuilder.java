package pl.statedebug;

import com.google.common.collect.Maps;
import com.sun.jdi.Field;
import com.sun.jdi.Value;
import com.sun.tools.jdi.*;

import java.util.Map;

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ObjectReferenceBuilder {
    private ObjectReferenceImpl value;
    private Map<Field, Value> fieldValues = Maps.newHashMap();

    public ObjectReferenceBuilder ofType(String typeName) {
        this.value = mockJdiObject(typeName);
        return this;
    }

    public ObjectReferenceImpl build() {
        when(value.getValues(anyListOf(Field.class))).thenReturn(fieldValues);
        return value;
    }

    public ObjectReferenceBuilder withObjectField(String fieldName, ObjectReferenceImpl value) {
        FieldImpl objectField = mock(FieldImpl.class);
        when(objectField.name()).thenReturn(fieldName);

        fieldValues.put(objectField, value);

        return this;
    }

    public ObjectReferenceBuilder withIntegerField(String fieldName, int intValue) {
        FieldImpl intField = mock(FieldImpl.class);
        when(intField.name()).thenReturn(fieldName);

        IntegerValueImpl integerValue = mock(IntegerValueImpl.class);
        when(integerValue.value()).thenReturn(intValue);

        fieldValues.put(intField, integerValue);
        return this;
    }

    public ObjectReferenceBuilder withStringField(String fieldName, String value) {
        FieldImpl field = mock(FieldImpl.class);
        when(field.name()).thenReturn(fieldName);

        StringReferenceImpl objectValue = mock(StringReferenceImpl.class);
        when(objectValue.value()).thenReturn(value);
        ArrayTypeImpl type = mock(ArrayTypeImpl.class);
        when(type.name()).thenReturn("String");
        when(objectValue.type()).thenReturn(type);

        fieldValues.put(field, objectValue);
        return this;
    }

    private ObjectReferenceImpl mockJdiObject(String typeName) {
        ObjectReferenceImpl objectValue = mock(ObjectReferenceImpl.class);
        ClassTypeImpl type = mock(ClassTypeImpl.class);
        when(type.name()).thenReturn(typeName);
        when(objectValue.type()).thenReturn(type);
        return objectValue;
    }
}
