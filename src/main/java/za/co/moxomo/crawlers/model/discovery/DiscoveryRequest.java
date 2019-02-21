package za.co.moxomo.crawlers.model.discovery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "provinces",
        "locations",
        "categories"
})
public class DiscoveryRequest {

    @JsonProperty("provinces")
    private List<String> provinces = Collections.singletonList("all");
    @JsonProperty("locations")
    private List<String> locations = Collections.singletonList("all");
    @JsonProperty("categories")
    private List<String> categories =Collections.singletonList("all");

    @JsonProperty("provinces")
    public List<String> getProvinces() {
        return provinces;
    }

    @JsonProperty("provinces")
    public void setProvinces(List<String> provinces) {
        this.provinces = provinces;
    }

    @JsonProperty("locations")
    public List<String> getLocations() {
        return locations;
    }

    @JsonProperty("locations")
    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    @JsonProperty("categories")
    public List<String> getCategories() {
        return categories;
    }

    @JsonProperty("categories")
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

}