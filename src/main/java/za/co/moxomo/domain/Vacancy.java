package za.co.moxomo.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import za.co.moxomo.utils.Util;

import java.util.Date;
import java.util.UUID;

import static org.springframework.data.elasticsearch.annotations.FieldType.Keyword;
import static org.springframework.data.elasticsearch.annotations.FieldType.Text;


@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Document(indexName = "job_ads", type = "vacancy")
@Data

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
    @MultiField(
            mainField = @Field(type = Text, fielddata = true),
            otherFields = {
                    @InnerField(suffix = "verbatim", type = Keyword)
            }
    )
    private String company;
    private String reference;
    private String location;
    private String province;
    private String qualifications;
    private String responsibilities;

    @Field(type = FieldType.Date)
    private Date advertDate;
    private String link;
    private String contractType;
    private String imageUrl;
    private Long companyId;
    private Long agentId;
    private String url;
    private String remuneration;
    private String source;
    private String affirmativeAction;
    private String additionalTokens;
    private Date closingDate;
    private boolean webViewViewable = true;
    @GeoPointField
    @ApiModelProperty(hidden=true)
    private GeoPoint geoPoint;
    @Transient
    private String distance;

    public Vacancy() {

    }

    public Vacancy(String jobTitle, String description, String offerId, String company, String location,
                   String province, String qualifications, String responsibilities, Date advertDate,
                   String contractType, String imageUrl, String remuneration, String source, String additionalTokens, String affirmativeAction, String url) {

        this.id = UUID.randomUUID().toString();
        this.jobTitle = jobTitle.trim();
        this.description = Util.removeBadChars(description.trim());
        this.offerId = offerId.trim();
        this.company = company.trim();
        this.location = location.trim();
        this.province = province;
        this.qualifications =Util.removeBadChars(qualifications);
        this.responsibilities = Util.removeBadChars(responsibilities);
        this.advertDate = advertDate;
        this.contractType = contractType;
        this.imageUrl = imageUrl.trim();
        this.remuneration = remuneration;
        this.source = source;
        this.link = url;
        this.additionalTokens = additionalTokens;
        this.affirmativeAction = affirmativeAction;
        this.url = url.trim();
    }



}

