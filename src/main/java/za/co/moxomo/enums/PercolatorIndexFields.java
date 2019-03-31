package za.co.moxomo.enums;


import lombok.Getter;

@Getter
public enum PercolatorIndexFields {

    PERCOLATOR_QUERY("query", "percolator"),
    JOB_TITLE("jobTitle", "text"),
    PROVINCE("province", "keyword"),
    TOWN("town", "keyword"),
    TAGS("tags", "keyword");

    private final String fieldName;
    private final String fieldType;

    PercolatorIndexFields(String fieldName, String fieldType) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

}