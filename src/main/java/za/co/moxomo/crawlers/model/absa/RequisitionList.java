
package za.co.moxomo.crawlers.model.absa;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "hotJob",
    "addedToJobCart",
    "draft",
    "alreadyAppliedOn",
    "toReApply",
    "jobId",
    "contestNo",
    "column",
    "linkedColumn",
    "locationsColumns"
})
public class RequisitionList {

    @JsonProperty("hotJob")
    private Boolean hotJob;
    @JsonProperty("addedToJobCart")
    private Boolean addedToJobCart;
    @JsonProperty("draft")
    private Boolean draft;
    @JsonProperty("alreadyAppliedOn")
    private Boolean alreadyAppliedOn;
    @JsonProperty("toReApply")
    private Boolean toReApply;
    @JsonProperty("jobId")
    private String jobId;
    @JsonProperty("contestNo")
    private String contestNo;
    @JsonProperty("column")
    private List<String> column = null;
    @JsonProperty("linkedColumn")
    private Integer linkedColumn;
    @JsonProperty("locationsColumns")
    private List<Integer> locationsColumns = null;

    @JsonProperty("hotJob")
    public Boolean getHotJob() {
        return hotJob;
    }

    @JsonProperty("hotJob")
    public void setHotJob(Boolean hotJob) {
        this.hotJob = hotJob;
    }

    @JsonProperty("addedToJobCart")
    public Boolean getAddedToJobCart() {
        return addedToJobCart;
    }

    @JsonProperty("addedToJobCart")
    public void setAddedToJobCart(Boolean addedToJobCart) {
        this.addedToJobCart = addedToJobCart;
    }

    @JsonProperty("draft")
    public Boolean getDraft() {
        return draft;
    }

    @JsonProperty("draft")
    public void setDraft(Boolean draft) {
        this.draft = draft;
    }

    @JsonProperty("alreadyAppliedOn")
    public Boolean getAlreadyAppliedOn() {
        return alreadyAppliedOn;
    }

    @JsonProperty("alreadyAppliedOn")
    public void setAlreadyAppliedOn(Boolean alreadyAppliedOn) {
        this.alreadyAppliedOn = alreadyAppliedOn;
    }

    @JsonProperty("toReApply")
    public Boolean getToReApply() {
        return toReApply;
    }

    @JsonProperty("toReApply")
    public void setToReApply(Boolean toReApply) {
        this.toReApply = toReApply;
    }

    @JsonProperty("jobId")
    public String getJobId() {
        return jobId;
    }

    @JsonProperty("jobId")
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @JsonProperty("contestNo")
    public String getContestNo() {
        return contestNo;
    }

    @JsonProperty("contestNo")
    public void setContestNo(String contestNo) {
        this.contestNo = contestNo;
    }

    @JsonProperty("column")
    public List<String> getColumn() {
        return column;
    }

    @JsonProperty("column")
    public void setColumn(List<String> column) {
        this.column = column;
    }

    @JsonProperty("linkedColumn")
    public Integer getLinkedColumn() {
        return linkedColumn;
    }

    @JsonProperty("linkedColumn")
    public void setLinkedColumn(Integer linkedColumn) {
        this.linkedColumn = linkedColumn;
    }

    @JsonProperty("locationsColumns")
    public List<Integer> getLocationsColumns() {
        return locationsColumns;
    }

    @JsonProperty("locationsColumns")
    public void setLocationsColumns(List<Integer> locationsColumns) {
        this.locationsColumns = locationsColumns;
    }

    @Override
    public String toString() {
        return "RequisitionList{" +
                "hotJob=" + hotJob +
                ", addedToJobCart=" + addedToJobCart +
                ", draft=" + draft +
                ", alreadyAppliedOn=" + alreadyAppliedOn +
                ", toReApply=" + toReApply +
                ", jobId='" + jobId + '\'' +
                ", contestNo='" + contestNo + '\'' +
                ", column=" + column +
                ", linkedColumn=" + linkedColumn +
                ", locationsColumns=" + locationsColumns +
                '}';
    }
}
