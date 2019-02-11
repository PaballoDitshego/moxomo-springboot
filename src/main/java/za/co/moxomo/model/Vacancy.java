package za.co.moxomo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.Date;
import java.util.UUID;

import static org.springframework.data.elasticsearch.annotations.FieldType.Keyword;
import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Document(indexName = "jobs", type = "vacancy")
public class Vacancy {

    @Id
    private String id;
    @MultiField(
            mainField = @Field(type = Text, fielddata = true),
            otherFields = {
                    @InnerField(suffix = "verbatim", type = Keyword)
            }
    )
    private String jobTitle;
    private String description;
    private String offerId;
    private String company;
    private String reference;
    private String location;
    private String province;
    private String qualifications;
    private String responsibilities;

    @Field(type = FieldType.Date)
    private Date advertDate;
    //private Date closingDate;
    private String link;
    private String contractType;
    private String imageUrl;
    private Long companyId;
    private Long agentId;
    private String  url;
    private String remuneration;
    private String source;
    private String affirmativeAction;
    private String additionalTokens;
    private Date closingDate;
    private boolean webViewViewable =true;

   public Vacancy(){

   }
    public Vacancy(String jobTitle, String description, String offerId, String company, String location,
                   String province, String qualifications, String responsibilities, Date advertDate,
                   String contractType, String imageUrl, String remuneration, String source, String additionalTokens, String affirmativeAction,String url) {

        this.id= UUID.randomUUID().toString();
        this.jobTitle = jobTitle;
        this.description = description;
        this.offerId = offerId;
        this.company = company;
        this.location = location;
        this.province = province;
        this.qualifications = qualifications;
        this.responsibilities = responsibilities;
        this.advertDate = advertDate;
        this.contractType = contractType;
        this.imageUrl = imageUrl;
        this.remuneration = remuneration;
        this.source = source;
        this.link=url;
        this.additionalTokens = additionalTokens;
        this.affirmativeAction=affirmativeAction;
        this.url=url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getQualifications() {
        return qualifications;
    }

    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }


    public String getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(String responsibilities) {
        this.responsibilities = responsibilities;
    }

    public Date getAdvertDate() {
        return advertDate;
    }

    public void setAdvertDate(Date advertDate) {
        this.advertDate = advertDate;
    }

   /* public Date getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(Date closingDate) {
        this.closingDate = closingDate;
    }*/

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public String getRemuneration() {
        return remuneration;
    }

    public void setRemuneration(String remuneration) {
        this.remuneration = remuneration;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getUrl() {
        return url;
    }

    public String getAdditionalTokens() {
        return additionalTokens;
    }

    public void setAdditionalTokens(String additionalTokens) {
        this.additionalTokens = additionalTokens;
    }

    public String getContractType() {
        return contractType;

    }



    public String getAffirmativeAction() {
        return affirmativeAction;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isWebViewViewable() {
        return webViewViewable;
    }

    public void setWebViewViewable(boolean webViewViewable) {
        this.webViewViewable = webViewViewable;
    }

    public void setAffirmativeAction(String affirmativeAction) {
        this.affirmativeAction = affirmativeAction;




    }

    @Override
    public String toString() {
        return "Vacancy{" +
                "id='" + id + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", description='" + description + '\'' +
                ", offerId='" + offerId + '\'' +
                ", company='" + company + '\'' +
                ", reference='" + reference + '\'' +
                ", location='" + location + '\'' +
                ", province='" + province + '\'' +
                ", qualifications='" + qualifications + '\'' +
                ", responsibilities='" + responsibilities + '\'' +
                ", advertDate=" + advertDate +
               // ", closingDate=" + closingDate +
                ", link='" + link + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", companyId=" + companyId +
                ", agentId=" + agentId +
                ", remuneration='" + remuneration + '\'' +
                ", source='" + source + '\'' +
                '}';
    }

    public Date getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(Date closingDate) {
        this.closingDate = closingDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


}

