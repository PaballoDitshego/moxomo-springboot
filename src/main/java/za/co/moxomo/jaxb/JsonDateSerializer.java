package za.co.moxomo.jaxb;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import za.co.moxomo.enums.Constants;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JsonDateSerializer extends JsonSerializer<Date> {
    private SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.YYYY_MM_DD_HH_MM_DATE_FORMAT);

    @Override
    public void serialize(Date value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        jsonGenerator.writeString(dateFormat.format(value));
    }
}
