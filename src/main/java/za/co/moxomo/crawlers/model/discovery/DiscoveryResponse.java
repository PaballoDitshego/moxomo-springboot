
package za.co.moxomo.crawlers.model.discovery;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sourceSystem",
    "referenceNumber",
    "positionType",
    "businessUnit",
    "department",
    "closingDate",
    "positionDescription",
    "positionRequirements",
    "positionQualifications",
    "function",
    "location",
    "province",
    "experience",
    "applicationUrl",
    "id"
})
public class DiscoveryResponse {

    @JsonProperty("sourceSystem")
    private String sourceSystem;
    @JsonProperty("referenceNumber")
    private String referenceNumber;
    @JsonProperty("positionType")
    private String positionType;
    @JsonProperty("businessUnit")
    private String businessUnit;
    @JsonProperty("department")
    private String department;
    @JsonProperty("closingDate")
    private ClosingDate closingDate;
    @JsonProperty("positionDescription")
    private String positionDescription;
    @JsonProperty("positionRequirements")
    private String positionRequirements;
    @JsonProperty("positionQualifications")
    private String positionQualifications;
    @JsonProperty("function")
    private List<String> function = null;
    @JsonProperty("location")
    private String location;
    @JsonProperty("province")
    private String province;
    @JsonProperty("experience")
    private String experience;
    @JsonProperty("applicationUrl")
    private String applicationUrl;
    @JsonProperty("id")
    private Long id;

    @JsonProperty("sourceSystem")
    public String getSourceSystem() {
        return sourceSystem;
    }

    @JsonProperty("sourceSystem")
    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    @JsonProperty("referenceNumber")
    public String getReferenceNumber() {
        return referenceNumber;
    }

    @JsonProperty("referenceNumber")
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    @JsonProperty("positionType")
    public String getPositionType() {
        return positionType;
    }

    @JsonProperty("positionType")
    public void setPositionType(String positionType) {
        this.positionType = positionType;
    }

    @JsonProperty("businessUnit")
    public String getBusinessUnit() {
        return businessUnit;
    }

    @JsonProperty("businessUnit")
    public void setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
    }

    @JsonProperty("department")
    public String getDepartment() {
        return department;
    }

    @JsonProperty("department")
    public void setDepartment(String department) {
        this.department = department;
    }

    @JsonProperty("closingDate")
    public ClosingDate getClosingDate() {
        return closingDate;
    }

    @JsonProperty("closingDate")
    public void setClosingDate(ClosingDate closingDate) {
        this.closingDate = closingDate;
    }

    @JsonProperty("positionDescription")
    public String getPositionDescription() {
        return positionDescription;
    }

    @JsonProperty("positionDescription")
    public void setPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
    }

    @JsonProperty("positionRequirements")
    public String getPositionRequirements() {
        return positionRequirements;
    }

    @JsonProperty("positionRequirements")
    public void setPositionRequirements(String positionRequirements) {
        this.positionRequirements = positionRequirements;
    }

    @JsonProperty("positionQualifications")
    public String getPositionQualifications() {
        return positionQualifications;
    }

    @JsonProperty("positionQualifications")
    public void setPositionQualifications(String positionQualifications) {
        this.positionQualifications = positionQualifications;
    }

    @JsonProperty("function")
    public List<String> getFunction() {
        return function;
    }

    @JsonProperty("function")
    public void setFunction(List<String> function) {
        this.function = function;
    }

    @JsonProperty("location")
    public String getLocation() {
        return location;
    }

    @JsonProperty("location")
    public void setLocation(String location) {
        this.location = location;
    }

    @JsonProperty("province")
    public String getProvince() {
        return province;
    }

    @JsonProperty("province")
    public void setProvince(String province) {
        this.province = province;
    }

    @JsonProperty("experience")
    public String getExperience() {
        return experience;
    }

    @JsonProperty("experience")
    public void setExperience(String experience) {
        this.experience = experience;
    }

    @JsonProperty("applicationUrl")
    public String getApplicationUrl() {
        return applicationUrl;
    }

    @JsonProperty("applicationUrl")
    public void setApplicationUrl(String applicationUrl) {
        this.applicationUrl = applicationUrl;
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "DiscoveryResponse{" +
                "sourceSystem='" + sourceSystem + '\'' +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", positionType='" + positionType + '\'' +
                ", businessUnit='" + businessUnit + '\'' +
                ", department='" + department + '\'' +
                ", closingDate=" + closingDate +
                ", positionDescription='" + positionDescription + '\'' +
                ", positionRequirements='" + positionRequirements + '\'' +
                ", positionQualifications='" + positionQualifications + '\'' +
                ", function=" + function +
                ", location='" + location + '\'' +
                ", province='" + province + '\'' +
                ", experience='" + experience + '\'' +
                ", applicationUrl='" + applicationUrl + '\'' +
                ", id=" + id +
                '}';
    }
}
