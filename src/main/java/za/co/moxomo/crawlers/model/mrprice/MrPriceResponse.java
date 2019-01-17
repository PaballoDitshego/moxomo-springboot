
package za.co.moxomo.crawlers.model.mrprice;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "AdditionalLocations",
    "Address",
    "AddressDetails",
    "ApplicantCount",
    "CanAppy",
    "CareerSites",
    "Compensation",
    "ContactPhone",
    "CreateDateLocal",
    "Currency",
    "CurrencySymbol",
    "DaysOpen",
    "DefaultEffectiveDate",
    "DefaultExpirationDate",
    "DefaultLanguage",
    "DefaultName",
    "DefaultURL",
    "Division",
    "DivisionId",
    "EEOCategory",
    "EmploymentStatus",
    "EmploymentType",
    "ExternalAd",
    "ExternalDescription",
    "Grade",
    "GradeId",
    "HiringManager",
    "Id",
    "IdealQualification",
    "InternalAd",
    "InternalDescription",
    "JobResponsibilities",
    "Keywords",
    "LastModificationDate",
    "Location",
    "LocationId",
    "MetaPageDesc",
    "MetaPageTitle",
    "MinimumQualification",
    "MobileAd",
    "NewSubmissionCount",
    "Ongoing",
    "OpenDateLocal",
    "OpenPostingCount",
    "Openings",
    "Position",
    "PositionId",
    "Priority",
    "PriorityName",
    "RangeHigh",
    "RangeLow",
    "Ref",
    "ReferalBonus",
    "RequisitionTemplate",
    "RequisitionTemplateID",
    "Status",
    "SuggestedReferralCount",
    "TargetHireDate",
    "Title"
})
public class MrPriceResponse {

    @JsonProperty("AdditionalLocations")
    private List<Object> additionalLocations = null;
    @JsonProperty("Address")
    private String address;
    @JsonProperty("AddressDetails")
    private AddressDetails addressDetails;
    @JsonProperty("ApplicantCount")
    private Integer applicantCount;
    @JsonProperty("CanAppy")
    private Boolean canAppy;
    @JsonProperty("CareerSites")
    private List<CareerSite> careerSites = null;
    @JsonProperty("Compensation")
    private String compensation;
    @JsonProperty("ContactPhone")
    private String contactPhone;
    @JsonProperty("CreateDateLocal")
    private String createDateLocal;
    @JsonProperty("Currency")
    private String currency;
    @JsonProperty("CurrencySymbol")
    private String currencySymbol;
    @JsonProperty("DaysOpen")
    private Integer daysOpen;
    @JsonProperty("DefaultEffectiveDate")
    private String defaultEffectiveDate;
    @JsonProperty("DefaultExpirationDate")
    private String defaultExpirationDate;
    @JsonProperty("DefaultLanguage")
    private String defaultLanguage;
    @JsonProperty("DefaultName")
    private String defaultName;
    @JsonProperty("DefaultURL")
    private String defaultURL;
    @JsonProperty("Division")
    private String division;
    @JsonProperty("DivisionId")
    private String divisionId;
    @JsonProperty("EEOCategory")
    private String eEOCategory;
    @JsonProperty("EmploymentStatus")
    private String employmentStatus;
    @JsonProperty("EmploymentType")
    private String employmentType;
    @JsonProperty("ExternalAd")
    private String externalAd;
    @JsonProperty("ExternalDescription")
    private String externalDescription;
    @JsonProperty("Grade")
    private String grade;
    @JsonProperty("GradeId")
    private String gradeId;
    @JsonProperty("HiringManager")
    private Object hiringManager;
    @JsonProperty("Id")
    private Integer id;
    @JsonProperty("IdealQualification")
    private String idealQualification;
    @JsonProperty("InternalAd")
    private String internalAd;
    @JsonProperty("InternalDescription")
    private String internalDescription;
    @JsonProperty("JobResponsibilities")
    private List<Object> jobResponsibilities = null;
    @JsonProperty("Keywords")
    private String keywords;
    @JsonProperty("LastModificationDate")
    private String lastModificationDate;
    @JsonProperty("Location")
    private String location;
    @JsonProperty("LocationId")
    private String locationId;
    @JsonProperty("MetaPageDesc")
    private String metaPageDesc;
    @JsonProperty("MetaPageTitle")
    private String metaPageTitle;
    @JsonProperty("MinimumQualification")
    private String minimumQualification;
    @JsonProperty("MobileAd")
    private String mobileAd;
    @JsonProperty("NewSubmissionCount")
    private Integer newSubmissionCount;
    @JsonProperty("Ongoing")
    private Boolean ongoing;
    @JsonProperty("OpenDateLocal")
    private String openDateLocal;
    @JsonProperty("OpenPostingCount")
    private Integer openPostingCount;
    @JsonProperty("Openings")
    private Integer openings;
    @JsonProperty("Position")
    private String position;
    @JsonProperty("PositionId")
    private String positionId;
    @JsonProperty("Priority")
    private String priority;
    @JsonProperty("PriorityName")
    private String priorityName;
    @JsonProperty("RangeHigh")
    private Integer rangeHigh;
    @JsonProperty("RangeLow")
    private Integer rangeLow;
    @JsonProperty("Ref")
    private String ref;
    @JsonProperty("ReferalBonus")
    private Integer referalBonus;
    @JsonProperty("RequisitionTemplate")
    private String requisitionTemplate;
    @JsonProperty("RequisitionTemplateID")
    private Integer requisitionTemplateID;
    @JsonProperty("Status")
    private String status;
    @JsonProperty("SuggestedReferralCount")
    private Integer suggestedReferralCount;
    @JsonProperty("TargetHireDate")
    private String targetHireDate;
    @JsonProperty("Title")
    private String title;

    @JsonProperty("AdditionalLocations")
    public List<Object> getAdditionalLocations() {
        return additionalLocations;
    }

    @JsonProperty("AdditionalLocations")
    public void setAdditionalLocations(List<Object> additionalLocations) {
        this.additionalLocations = additionalLocations;
    }

    @JsonProperty("Address")
    public String getAddress() {
        return address;
    }

    @JsonProperty("Address")
    public void setAddress(String address) {
        this.address = address;
    }

    @JsonProperty("AddressDetails")
    public AddressDetails getAddressDetails() {
        return addressDetails;
    }

    @JsonProperty("AddressDetails")
    public void setAddressDetails(AddressDetails addressDetails) {
        this.addressDetails = addressDetails;
    }

    @JsonProperty("ApplicantCount")
    public Integer getApplicantCount() {
        return applicantCount;
    }

    @JsonProperty("ApplicantCount")
    public void setApplicantCount(Integer applicantCount) {
        this.applicantCount = applicantCount;
    }

    @JsonProperty("CanAppy")
    public Boolean getCanAppy() {
        return canAppy;
    }

    @JsonProperty("CanAppy")
    public void setCanAppy(Boolean canAppy) {
        this.canAppy = canAppy;
    }

    @JsonProperty("CareerSites")
    public List<CareerSite> getCareerSites() {
        return careerSites;
    }

    @JsonProperty("CareerSites")
    public void setCareerSites(List<CareerSite> careerSites) {
        this.careerSites = careerSites;
    }

    @JsonProperty("Compensation")
    public String getCompensation() {
        return compensation;
    }

    @JsonProperty("Compensation")
    public void setCompensation(String compensation) {
        this.compensation = compensation;
    }

    @JsonProperty("ContactPhone")
    public String getContactPhone() {
        return contactPhone;
    }

    @JsonProperty("ContactPhone")
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    @JsonProperty("CreateDateLocal")
    public String getCreateDateLocal() {
        return createDateLocal;
    }

    @JsonProperty("CreateDateLocal")
    public void setCreateDateLocal(String createDateLocal) {
        this.createDateLocal = createDateLocal;
    }

    @JsonProperty("Currency")
    public String getCurrency() {
        return currency;
    }

    @JsonProperty("Currency")
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @JsonProperty("CurrencySymbol")
    public String getCurrencySymbol() {
        return currencySymbol;
    }

    @JsonProperty("CurrencySymbol")
    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    @JsonProperty("DaysOpen")
    public Integer getDaysOpen() {
        return daysOpen;
    }

    @JsonProperty("DaysOpen")
    public void setDaysOpen(Integer daysOpen) {
        this.daysOpen = daysOpen;
    }

    @JsonProperty("DefaultEffectiveDate")
    public String getDefaultEffectiveDate() {
        return defaultEffectiveDate;
    }

    @JsonProperty("DefaultEffectiveDate")
    public void setDefaultEffectiveDate(String defaultEffectiveDate) {
        this.defaultEffectiveDate = defaultEffectiveDate;
    }

    @JsonProperty("DefaultExpirationDate")
    public String getDefaultExpirationDate() {
        return defaultExpirationDate;
    }

    @JsonProperty("DefaultExpirationDate")
    public void setDefaultExpirationDate(String defaultExpirationDate) {
        this.defaultExpirationDate = defaultExpirationDate;
    }

    @JsonProperty("DefaultLanguage")
    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    @JsonProperty("DefaultLanguage")
    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    @JsonProperty("DefaultName")
    public String getDefaultName() {
        return defaultName;
    }

    @JsonProperty("DefaultName")
    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    @JsonProperty("DefaultURL")
    public String getDefaultURL() {
        return defaultURL;
    }

    @JsonProperty("DefaultURL")
    public void setDefaultURL(String defaultURL) {
        this.defaultURL = defaultURL;
    }

    @JsonProperty("Division")
    public String getDivision() {
        return division;
    }

    @JsonProperty("Division")
    public void setDivision(String division) {
        this.division = division;
    }

    @JsonProperty("DivisionId")
    public String getDivisionId() {
        return divisionId;
    }

    @JsonProperty("DivisionId")
    public void setDivisionId(String divisionId) {
        this.divisionId = divisionId;
    }

    @JsonProperty("EEOCategory")
    public String getEEOCategory() {
        return eEOCategory;
    }

    @JsonProperty("EEOCategory")
    public void setEEOCategory(String eEOCategory) {
        this.eEOCategory = eEOCategory;
    }

    @JsonProperty("EmploymentStatus")
    public String getEmploymentStatus() {
        return employmentStatus;
    }

    @JsonProperty("EmploymentStatus")
    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    @JsonProperty("EmploymentType")
    public String getEmploymentType() {
        return employmentType;
    }

    @JsonProperty("EmploymentType")
    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    @JsonProperty("ExternalAd")
    public String getExternalAd() {
        return externalAd;
    }

    @JsonProperty("ExternalAd")
    public void setExternalAd(String externalAd) {
        this.externalAd = externalAd;
    }

    @JsonProperty("ExternalDescription")
    public String getExternalDescription() {
        return externalDescription;
    }

    @JsonProperty("ExternalDescription")
    public void setExternalDescription(String externalDescription) {
        this.externalDescription = externalDescription;
    }

    @JsonProperty("Grade")
    public String getGrade() {
        return grade;
    }

    @JsonProperty("Grade")
    public void setGrade(String grade) {
        this.grade = grade;
    }

    @JsonProperty("GradeId")
    public String getGradeId() {
        return gradeId;
    }

    @JsonProperty("GradeId")
    public void setGradeId(String gradeId) {
        this.gradeId = gradeId;
    }

    @JsonProperty("HiringManager")
    public Object getHiringManager() {
        return hiringManager;
    }

    @JsonProperty("HiringManager")
    public void setHiringManager(Object hiringManager) {
        this.hiringManager = hiringManager;
    }

    @JsonProperty("Id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("Id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("IdealQualification")
    public String getIdealQualification() {
        return idealQualification;
    }

    @JsonProperty("IdealQualification")
    public void setIdealQualification(String idealQualification) {
        this.idealQualification = idealQualification;
    }

    @JsonProperty("InternalAd")
    public String getInternalAd() {
        return internalAd;
    }

    @JsonProperty("InternalAd")
    public void setInternalAd(String internalAd) {
        this.internalAd = internalAd;
    }

    @JsonProperty("InternalDescription")
    public String getInternalDescription() {
        return internalDescription;
    }

    @JsonProperty("InternalDescription")
    public void setInternalDescription(String internalDescription) {
        this.internalDescription = internalDescription;
    }

    @JsonProperty("JobResponsibilities")
    public List<Object> getJobResponsibilities() {
        return jobResponsibilities;
    }

    @JsonProperty("JobResponsibilities")
    public void setJobResponsibilities(List<Object> jobResponsibilities) {
        this.jobResponsibilities = jobResponsibilities;
    }

    @JsonProperty("Keywords")
    public String getKeywords() {
        return keywords;
    }

    @JsonProperty("Keywords")
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    @JsonProperty("LastModificationDate")
    public String getLastModificationDate() {
        return lastModificationDate;
    }

    @JsonProperty("LastModificationDate")
    public void setLastModificationDate(String lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    @JsonProperty("Location")
    public String getLocation() {
        return location;
    }

    @JsonProperty("Location")
    public void setLocation(String location) {
        this.location = location;
    }

    @JsonProperty("LocationId")
    public String getLocationId() {
        return locationId;
    }

    @JsonProperty("LocationId")
    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    @JsonProperty("MetaPageDesc")
    public String getMetaPageDesc() {
        return metaPageDesc;
    }

    @JsonProperty("MetaPageDesc")
    public void setMetaPageDesc(String metaPageDesc) {
        this.metaPageDesc = metaPageDesc;
    }

    @JsonProperty("MetaPageTitle")
    public String getMetaPageTitle() {
        return metaPageTitle;
    }

    @JsonProperty("MetaPageTitle")
    public void setMetaPageTitle(String metaPageTitle) {
        this.metaPageTitle = metaPageTitle;
    }

    @JsonProperty("MinimumQualification")
    public String getMinimumQualification() {
        return minimumQualification;
    }

    @JsonProperty("MinimumQualification")
    public void setMinimumQualification(String minimumQualification) {
        this.minimumQualification = minimumQualification;
    }

    @JsonProperty("MobileAd")
    public String getMobileAd() {
        return mobileAd;
    }

    @JsonProperty("MobileAd")
    public void setMobileAd(String mobileAd) {
        this.mobileAd = mobileAd;
    }

    @JsonProperty("NewSubmissionCount")
    public Integer getNewSubmissionCount() {
        return newSubmissionCount;
    }

    @JsonProperty("NewSubmissionCount")
    public void setNewSubmissionCount(Integer newSubmissionCount) {
        this.newSubmissionCount = newSubmissionCount;
    }

    @JsonProperty("Ongoing")
    public Boolean getOngoing() {
        return ongoing;
    }

    @JsonProperty("Ongoing")
    public void setOngoing(Boolean ongoing) {
        this.ongoing = ongoing;
    }

    @JsonProperty("OpenDateLocal")
    public String getOpenDateLocal() {
        return openDateLocal;
    }

    @JsonProperty("OpenDateLocal")
    public void setOpenDateLocal(String openDateLocal) {
        this.openDateLocal = openDateLocal;
    }

    @JsonProperty("OpenPostingCount")
    public Integer getOpenPostingCount() {
        return openPostingCount;
    }

    @JsonProperty("OpenPostingCount")
    public void setOpenPostingCount(Integer openPostingCount) {
        this.openPostingCount = openPostingCount;
    }

    @JsonProperty("Openings")
    public Integer getOpenings() {
        return openings;
    }

    @JsonProperty("Openings")
    public void setOpenings(Integer openings) {
        this.openings = openings;
    }

    @JsonProperty("Position")
    public String getPosition() {
        return position;
    }

    @JsonProperty("Position")
    public void setPosition(String position) {
        this.position = position;
    }

    @JsonProperty("PositionId")
    public String getPositionId() {
        return positionId;
    }

    @JsonProperty("PositionId")
    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    @JsonProperty("Priority")
    public String getPriority() {
        return priority;
    }

    @JsonProperty("Priority")
    public void setPriority(String priority) {
        this.priority = priority;
    }

    @JsonProperty("PriorityName")
    public String getPriorityName() {
        return priorityName;
    }

    @JsonProperty("PriorityName")
    public void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
    }

    @JsonProperty("RangeHigh")
    public Integer getRangeHigh() {
        return rangeHigh;
    }

    @JsonProperty("RangeHigh")
    public void setRangeHigh(Integer rangeHigh) {
        this.rangeHigh = rangeHigh;
    }

    @JsonProperty("RangeLow")
    public Integer getRangeLow() {
        return rangeLow;
    }

    @JsonProperty("RangeLow")
    public void setRangeLow(Integer rangeLow) {
        this.rangeLow = rangeLow;
    }

    @JsonProperty("Ref")
    public String getRef() {
        return ref;
    }

    @JsonProperty("Ref")
    public void setRef(String ref) {
        this.ref = ref;
    }

    @JsonProperty("ReferalBonus")
    public Integer getReferalBonus() {
        return referalBonus;
    }

    @JsonProperty("ReferalBonus")
    public void setReferalBonus(Integer referalBonus) {
        this.referalBonus = referalBonus;
    }

    @JsonProperty("RequisitionTemplate")
    public String getRequisitionTemplate() {
        return requisitionTemplate;
    }

    @JsonProperty("RequisitionTemplate")
    public void setRequisitionTemplate(String requisitionTemplate) {
        this.requisitionTemplate = requisitionTemplate;
    }

    @JsonProperty("RequisitionTemplateID")
    public Integer getRequisitionTemplateID() {
        return requisitionTemplateID;
    }

    @JsonProperty("RequisitionTemplateID")
    public void setRequisitionTemplateID(Integer requisitionTemplateID) {
        this.requisitionTemplateID = requisitionTemplateID;
    }

    @JsonProperty("Status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("Status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("SuggestedReferralCount")
    public Integer getSuggestedReferralCount() {
        return suggestedReferralCount;
    }

    @JsonProperty("SuggestedReferralCount")
    public void setSuggestedReferralCount(Integer suggestedReferralCount) {
        this.suggestedReferralCount = suggestedReferralCount;
    }

    @JsonProperty("TargetHireDate")
    public String getTargetHireDate() {
        return targetHireDate;
    }

    @JsonProperty("TargetHireDate")
    public void setTargetHireDate(String targetHireDate) {
        this.targetHireDate = targetHireDate;
    }

    @JsonProperty("Title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("Title")
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "MrPriceResponse{" +
                "additionalLocations=" + additionalLocations +
                ", address='" + address + '\'' +
                ", addressDetails=" + addressDetails +
                ", applicantCount=" + applicantCount +
                ", canAppy=" + canAppy +
                ", careerSites=" + careerSites +
                ", compensation='" + compensation + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", createDateLocal='" + createDateLocal + '\'' +
                ", currency='" + currency + '\'' +
                ", currencySymbol='" + currencySymbol + '\'' +
                ", daysOpen=" + daysOpen +
                ", defaultEffectiveDate='" + defaultEffectiveDate + '\'' +
                ", defaultExpirationDate='" + defaultExpirationDate + '\'' +
                ", defaultLanguage='" + defaultLanguage + '\'' +
                ", defaultName='" + defaultName + '\'' +
                ", defaultURL='" + defaultURL + '\'' +
                ", division='" + division + '\'' +
                ", divisionId='" + divisionId + '\'' +
                ", eEOCategory='" + eEOCategory + '\'' +
                ", employmentStatus='" + employmentStatus + '\'' +
                ", employmentType='" + employmentType + '\'' +
                ", externalAd='" + externalAd + '\'' +
                ", externalDescription='" + externalDescription + '\'' +
                ", grade='" + grade + '\'' +
                ", gradeId='" + gradeId + '\'' +
                ", hiringManager=" + hiringManager +
                ", id=" + id +
                ", idealQualification='" + idealQualification + '\'' +
                ", internalAd='" + internalAd + '\'' +
                ", internalDescription='" + internalDescription + '\'' +
                ", jobResponsibilities=" + jobResponsibilities +
                ", keywords='" + keywords + '\'' +
                ", lastModificationDate='" + lastModificationDate + '\'' +
                ", location='" + location + '\'' +
                ", locationId='" + locationId + '\'' +
                ", metaPageDesc='" + metaPageDesc + '\'' +
                ", metaPageTitle='" + metaPageTitle + '\'' +
                ", minimumQualification='" + minimumQualification + '\'' +
                ", mobileAd='" + mobileAd + '\'' +
                ", newSubmissionCount=" + newSubmissionCount +
                ", ongoing=" + ongoing +
                ", openDateLocal='" + openDateLocal + '\'' +
                ", openPostingCount=" + openPostingCount +
                ", openings=" + openings +
                ", position='" + position + '\'' +
                ", positionId='" + positionId + '\'' +
                ", priority='" + priority + '\'' +
                ", priorityName='" + priorityName + '\'' +
                ", rangeHigh=" + rangeHigh +
                ", rangeLow=" + rangeLow +
                ", ref='" + ref + '\'' +
                ", referalBonus=" + referalBonus +
                ", requisitionTemplate='" + requisitionTemplate + '\'' +
                ", requisitionTemplateID=" + requisitionTemplateID +
                ", status='" + status + '\'' +
                ", suggestedReferralCount=" + suggestedReferralCount +
                ", targetHireDate='" + targetHireDate + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
