
package za.co.moxomo.crawlers.model.pnet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "logoImageUrl",
        "isListingAnonymous",
        "logoImageLink",
        "companyName",
        "companyNameLink",
        "jobTitle",
        "theme",
        "previewMode"
})
public class AdditionalInfo {

    @JsonProperty("logoImageUrl")
    private String logoImageUrl;
    @JsonProperty("isListingAnonymous")
    private Boolean isListingAnonymous;
    @JsonProperty("logoImageLink")
    private String logoImageLink;
    @JsonProperty("companyName")
    private String companyName;
    @JsonProperty("companyNameLink")
    private String companyNameLink;
    @JsonProperty("jobTitle")
    private String jobTitle;
    @JsonProperty("theme")
    private String theme;
    @JsonProperty("previewMode")
    private Boolean previewMode;

    @JsonProperty("logoImageUrl")
    public String getLogoImageUrl() {
        return logoImageUrl;
    }

    @JsonProperty("logoImageUrl")
    public void setLogoImageUrl(String logoImageUrl) {
        this.logoImageUrl = logoImageUrl;
    }

    @JsonProperty("isListingAnonymous")
    public Boolean getIsListingAnonymous() {
        return isListingAnonymous;
    }

    @JsonProperty("isListingAnonymous")
    public void setIsListingAnonymous(Boolean isListingAnonymous) {
        this.isListingAnonymous = isListingAnonymous;
    }

    @JsonProperty("logoImageLink")
    public String getLogoImageLink() {
        return logoImageLink;
    }

    @JsonProperty("logoImageLink")
    public void setLogoImageLink(String logoImageLink) {
        this.logoImageLink = logoImageLink;
    }

    @JsonProperty("companyName")
    public String getCompanyName() {
        return companyName;
    }

    @JsonProperty("companyName")
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @JsonProperty("companyNameLink")
    public String getCompanyNameLink() {
        return companyNameLink;
    }

    @JsonProperty("companyNameLink")
    public void setCompanyNameLink(String companyNameLink) {
        this.companyNameLink = companyNameLink;
    }

    @JsonProperty("jobTitle")
    public String getJobTitle() {
        return jobTitle;
    }

    @JsonProperty("jobTitle")
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    @JsonProperty("theme")
    public String getTheme() {
        return theme;
    }

    @JsonProperty("theme")
    public void setTheme(String theme) {
        this.theme = theme;
    }

    @JsonProperty("previewMode")
    public Boolean getPreviewMode() {
        return previewMode;
    }

    @JsonProperty("previewMode")
    public void setPreviewMode(Boolean previewMode) {
        this.previewMode = previewMode;
    }

}