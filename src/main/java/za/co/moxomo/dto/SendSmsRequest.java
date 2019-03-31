package za.co.moxomo.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import za.co.moxomo.jaxb.JaxbDateSerializer;
import za.co.moxomo.jaxb.JsonDateDeserializer;
import za.co.moxomo.jaxb.JsonDateSerializer;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

public class SendSmsRequest {

    private String recipientNumber;
    private String message;
    private Date dateToSend;
    private String campaign;
    private String dataField;

    public String getRecipientNumber() {
        return recipientNumber;
    }

    public void setRecipientNumber(String recipientNumber) {
        this.recipientNumber = recipientNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDateToSend() {
        return dateToSend;
    }

    @XmlJavaTypeAdapter(JaxbDateSerializer.class)
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    public void setDateToSend(Date dateToSend) {
        this.dateToSend = dateToSend;
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public String getDataField() {
        return dataField;
    }

    public void setDataField(String dataField) {
        this.dataField = dataField;
    }
}
