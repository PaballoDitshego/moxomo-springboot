
package za.co.moxomo.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.Getter;

import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "City",
        "AccentCity",
        "ProvinceName",
        "Latitude",
        "Longitude",
        "ProvinceID"
})
@Data
@Getter
@Document
@NoArgsConstructor
public class GeoLocation {

    @Id
    public String id;
    @JsonProperty("City")
    public String city;
    @Indexed
    @JsonProperty("AccentCity")
    public String accentCity;
    @Indexed
    @JsonProperty("ProvinceName")
    public String provinceName;
    @JsonProperty("Latitude")
    public Double latitude;
    @JsonProperty("Longitude")
    public Double longitude;
    @JsonProperty("ProvinceID")
    public Integer provinceID;

    @GeoSpatialIndexed
    private double[] point;


    public GeoLocation(String city, String accentCity, String provinceName, Double latitude, Double longitude, Integer provinceID) {
        this.city = city;
        this.accentCity = accentCity;
        this.provinceName = provinceName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.provinceID = provinceID;
        this.point = new double[2];
        point[0]= latitude;
        point[1]= longitude;

    }
}