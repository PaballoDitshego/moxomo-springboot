
package za.co.moxomo.crawlers.model.mrprice;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "City",
    "Country",
    "Line1",
    "Line2",
    "PostalCode",
    "State"
})
public class AddressDetails {

    @JsonProperty("City")
    private String city;
    @JsonProperty("Country")
    private String country;
    @JsonProperty("Line1")
    private String line1;
    @JsonProperty("Line2")
    private String line2;
    @JsonProperty("PostalCode")
    private String postalCode;
    @JsonProperty("State")
    private String state;

    @JsonProperty("City")
    public String getCity() {
        return city;
    }

    @JsonProperty("City")
    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty("Country")
    public String getCountry() {
        return country;
    }

    @JsonProperty("Country")
    public void setCountry(String country) {
        this.country = country;
    }

    @JsonProperty("Line1")
    public String getLine1() {
        return line1;
    }

    @JsonProperty("Line1")
    public void setLine1(String line1) {
        this.line1 = line1;
    }

    @JsonProperty("Line2")
    public String getLine2() {
        return line2;
    }

    @JsonProperty("Line2")
    public void setLine2(String line2) {
        this.line2 = line2;
    }

    @JsonProperty("PostalCode")
    public String getPostalCode() {
        return postalCode;
    }

    @JsonProperty("PostalCode")
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @JsonProperty("State")
    public String getState() {
        return state;
    }

    @JsonProperty("State")
    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "AddressDetails{" +
                "city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", line1='" + line1 + '\'' +
                ", line2='" + line2 + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
