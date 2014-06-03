package pl.statedebug;

import com.sun.tools.jdi.*;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValueToCodeTest {
    @Test
    public void shouldConvertNullValue() {
        assertThat(new ValueToCode().convert(null)).isNull();
    }

    @Test
    public void shouldConvertIntegerValue() {
        IntegerValueImpl integerValue = mock(IntegerValueImpl.class);
        when(integerValue.value()).thenReturn(123);

        assertThat(new ValueToCode().convert(integerValue)).isEqualTo("123");
    }

    @Test
    public void shouldConvertDoubleValue() {
        DoubleValueImpl doubleValue = mock(DoubleValueImpl.class);
        when(doubleValue.value()).thenReturn(1.1);

        assertThat(new ValueToCode().convert(doubleValue)).isEqualTo("1.1");
    }

    @Test
    public void shouldConvertBooleanTrueValue() {
        BooleanValueImpl booleanValue = mock(BooleanValueImpl.class);
        when(booleanValue.value()).thenReturn(true);

        assertThat(new ValueToCode().convert(booleanValue)).isEqualTo("true");
    }

    @Test
    public void shouldConvertBooleanFalseValue() {
        BooleanValueImpl booleanValue = mock(BooleanValueImpl.class);
        when(booleanValue.value()).thenReturn(false);

        assertThat(new ValueToCode().convert(booleanValue)).isEqualTo("false");
    }

    @Test
    public void shouldConvertByteValue() {
        ByteValueImpl byteValue = mock(ByteValueImpl.class);
        when(byteValue.value()).thenReturn(new Byte("11"));

        assertThat(new ValueToCode().convert(byteValue)).isEqualTo("11");
    }

    @Test
    public void shouldConvertCharValue() {
        CharValueImpl charValue = mock(CharValueImpl.class);
        when(charValue.value()).thenReturn('c');

        assertThat(new ValueToCode().convert(charValue)).isEqualTo("'c'");
    }

    @Test
    public void shouldConvertFloatValue() {
        FloatValueImpl floatValue = mock(FloatValueImpl.class);
        when(floatValue.value()).thenReturn(123.45f);

        assertThat(new ValueToCode().convert(floatValue)).isEqualTo("123.45f");
    }

    @Test
    public void shouldConvertLongValue() {
        LongValueImpl longValue = mock(LongValueImpl.class);
        when(longValue.value()).thenReturn(2348576l);

        assertThat(new ValueToCode().convert(longValue)).isEqualTo("2348576l");
    }

    @Test
    public void shouldConvertStringValue() {
        StringReferenceImpl stringReference = mock(StringReferenceImpl.class);
        when(stringReference.value()).thenReturn("some string");

        assertThat(new ValueToCode().convert(stringReference)).isEqualTo("\"some string\"");
    }

    @Test
    public void shouldConvertShortValue() {
        ShortValueImpl shortValue = mock(ShortValueImpl.class);
        when(shortValue.value()).thenReturn(new Short("44"));

        assertThat(new ValueToCode().convert(shortValue)).isEqualTo("44");
    }

    @Test
    public void shouldConvertClassWithIntegerField() {
        ObjectReferenceImpl objectValue = new ObjectReferenceBuilder()
                .ofType("pl.statedebug.Foo")
                .withIntegerField("intVal", 123)
                .build();

        assertThat(new ValueToCode().convert(objectValue)).isEqualTo(
                "Foo foo = new Foo();\n"
                        + "foo.setIntVal(123);\n"
        );
    }

    @Test
    public void shouldConvertClassWithStringField() {
        ObjectReferenceImpl objectValue = new ObjectReferenceBuilder()
                .ofType("pl.statedebug.Foo")
                .withStringField("stringField", "string value")
                .build();

        assertThat(new ValueToCode().convert(objectValue)).isEqualTo(
                "Foo foo = new Foo();\n"
                        + "foo.setStringField(\"string value\");\n"
        );
    }

    @Test
    public void shouldConvertClassWithObjectField() {
        ObjectReferenceImpl objectValue = new ObjectReferenceBuilder()
                .ofType("pl.statedebug.Foo")
                .withObjectField(
                        "bar",
                        new ObjectReferenceBuilder()
                                .ofType("pl.statedebug.Bar")
                                .withIntegerField("intVal", 111)
                                .build())
                .build();

        assertThat(new ValueToCode().convert(objectValue)).isEqualTo(
                "Foo foo = new Foo();\n"
                        + "Bar bar = new Bar();\n"
                        + "bar.setIntVal(111);\n"
                        + "foo.setBar(bar);\n"
        );
    }

}