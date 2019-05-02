package za.co.moxomo.enums;


import lombok.Getter;

@Getter
public enum PercolatorIndexFields {

    PERCOLATOR_QUERY("query", "percolator"),
    JOB_TITLE("jobTitle", "text"),
    LOCATION("location", "text"),
    TAGS("tags", "text");

    private final String fieldName;
    private final String fieldType;

    PercolatorIndexFields(String fieldName, String fieldType) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

}