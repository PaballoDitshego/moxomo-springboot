package za.co.moxomo.jaxb;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import za.co.moxomo.enums.Constants;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JsonDateDeserializer extends JsonDeserializer<Date> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.YYYY_MM_DD_HH_MM_DATE_FORMAT);

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        try {
            return dateFormat.parse(node.textValue());
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format");
        }
    } }