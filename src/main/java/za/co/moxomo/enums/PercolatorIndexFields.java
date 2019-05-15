package za.co.moxomo.enums;


import lombok.Getter;

@Getter
public enum PercolatorIndexFields {

    PERCOLATOR_QUERY("query", "percolator"),
    KEYWORD("key_word", "text"),
    COMPANY("company", "text"),
    LOCATION("location", "text"),
    GEOPOINT("geopoint", "geo_point");


    private final String fieldName;
    private final String fieldType;

    PercolatorIndexFields(String fieldName, String fieldType) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

}