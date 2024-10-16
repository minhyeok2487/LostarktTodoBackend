package lostark.todo.global.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class DoubleDeserializer extends JsonDeserializer<Double> {
    @Override
    public Double deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        try {
            NumberFormat format = NumberFormat.getInstance(Locale.US);
            Number number = format.parse(value);
            return number.doubleValue();
        } catch (ParseException e) {
            throw new IOException("Error parsing double value: " + value, e);
        }
    }
}
