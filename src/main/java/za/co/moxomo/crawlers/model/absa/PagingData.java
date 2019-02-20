
package za.co.moxomo.crawlers.model.absa;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "currentPageNo",
    "pageSize",
    "totalCount"
})
public class PagingData {

    @JsonProperty("currentPageNo")
    private Integer currentPageNo;
    @JsonProperty("pageSize")
    private Integer pageSize;
    @JsonProperty("totalCount")
    private Integer totalCount;

    @JsonProperty("currentPageNo")
    public Integer getCurrentPageNo() {
        return currentPageNo;
    }

    @JsonProperty("currentPageNo")
    public void setCurrentPageNo(Integer currentPageNo) {
        this.currentPageNo = currentPageNo;
    }

    @JsonProperty("pageSize")
    public Integer getPageSize() {
        return pageSize;
    }

    @JsonProperty("pageSize")
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @JsonProperty("totalCount")
    public Integer getTotalCount() {
        return totalCount;
    }

    @JsonProperty("totalCount")
    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

}
