package za.co.moxomo.jaxb;

import za.co.moxomo.enums.Constants;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JaxbDateSerializer extends XmlAdapter<String, Date> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.YYYY_MM_DD_HH_MM_DATE_FORMAT);

    @Override
    public String marshal(Date date) {
        return dateFormat.format(date);
    }

    @Override
    public Date unmarshal(String date) throws ParseException {
        return dateFormat.parse(date);
    }
}