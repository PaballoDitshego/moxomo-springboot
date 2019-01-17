
package za.co.moxomo.crawlers.model.mrprice;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "EffectiveDate",
    "ExpirationDate",
    "Name",
    "URL"
})
public class CareerSite {

    @JsonProperty("EffectiveDate")
    private String effectiveDate;
    @JsonProperty("ExpirationDate")
    private String expirationDate;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("URL")
    private String uRL;

    @JsonProperty("EffectiveDate")
    public String getEffectiveDate() {
        return effectiveDate;
    }

    @JsonProperty("EffectiveDate")
    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @JsonProperty("ExpirationDate")
    public String getExpirationDate() {
        return expirationDate;
    }

    @JsonProperty("ExpirationDate")
    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    @JsonProperty("Name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("URL")
    public String getURL() {
        return uRL;
    }

    @JsonProperty("URL")
    public void setURL(String uRL) {
        this.uRL = uRL;
    }

    @Override
    public String toString() {
        return "CareerSite{" +
                "effectiveDate='" + effectiveDate + '\'' +
                ", expirationDate='" + expirationDate + '\'' +
                ", name='" + name + '\'' +
                ", uRL='" + uRL + '\'' +
                '}';
    }
}
