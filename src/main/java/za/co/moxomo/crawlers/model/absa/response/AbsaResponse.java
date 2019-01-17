
package za.co.moxomo.crawlers.model.absa.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "requisitionList",
    "facetResults",
    "pagingData",
    "queryString",
    "careerSectionUnAvailable",
    "supportedLanguages"
})
public class AbsaResponse {

    @JsonProperty("requisitionList")
    private List<RequisitionList> requisitionList = null;
    @JsonProperty("facetResults")
    private List<FacetResult> facetResults = null;
    @JsonProperty("pagingData")
    private PagingData pagingData;
    @JsonProperty("queryString")
    private String queryString;
    @JsonProperty("careerSectionUnAvailable")
    private Boolean careerSectionUnAvailable;
    @JsonProperty("supportedLanguages")
    private List<SupportedLanguage> supportedLanguages = null;

    @JsonProperty("requisitionList")
    public List<RequisitionList> getRequisitionList() {
        return requisitionList;
    }

    @JsonProperty("requisitionList")
    public void setRequisitionList(List<RequisitionList> requisitionList) {
        this.requisitionList = requisitionList;
    }

    @JsonProperty("facetResults")
    public List<FacetResult> getFacetResults() {
        return facetResults;
    }

    @JsonProperty("facetResults")
    public void setFacetResults(List<FacetResult> facetResults) {
        this.facetResults = facetResults;
    }

    @JsonProperty("pagingData")
    public PagingData getPagingData() {
        return pagingData;
    }

    @JsonProperty("pagingData")
    public void setPagingData(PagingData pagingData) {
        this.pagingData = pagingData;
    }

    @JsonProperty("queryString")
    public String getQueryString() {
        return queryString;
    }

    @JsonProperty("queryString")
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    @JsonProperty("careerSectionUnAvailable")
    public Boolean getCareerSectionUnAvailable() {
        return careerSectionUnAvailable;
    }

    @JsonProperty("careerSectionUnAvailable")
    public void setCareerSectionUnAvailable(Boolean careerSectionUnAvailable) {
        this.careerSectionUnAvailable = careerSectionUnAvailable;
    }

    @JsonProperty("supportedLanguages")
    public List<SupportedLanguage> getSupportedLanguages() {
        return supportedLanguages;
    }

    @JsonProperty("supportedLanguages")
    public void setSupportedLanguages(List<SupportedLanguage> supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
    }

}
