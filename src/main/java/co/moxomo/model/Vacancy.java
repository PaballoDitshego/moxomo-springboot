package co.moxomo.model;

import java.util.Date;

/**
 * Created by paballo on 2016/11/16.
 */
public class Vacancy {


    private Long id;
    private Long _id;
    private String job_title;
    private String description;
    private String ad_id;

    public String getAd_id() {
        return ad_id;
    }

    public void setAd_id(String ad_id) {
        this.ad_id = ad_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAdvertDate(Date advertDate) {
        this.advertDate = advertDate;
    }

    private String company_name;

    String tweet;

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    private String ref_number;
    private String location;
    private String province;
    private String min_qual;
    private String category;
    private String duties;
    private String key_competencies;
    private Date advertDate;
    private Date closingDate;
    private String website;

    private String imageUrl;
    private Long company_id;
    private Long agent_id;
    private String status;

    private String remuneration;

    private String source;


    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getMin_qual() {
        return min_qual;
    }

    public void setMin_qual(String min_qual) {
        this.min_qual = min_qual;
    }

    public String getKey_competencies() {
        return key_competencies;
    }

    public void setKey_competencies(String key_competencies) {
        this.key_competencies = key_competencies;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRemuneration() {
        return remuneration;
    }

    public void setRemuneration(String remuneration) {
        this.remuneration = remuneration;
    }


    private String uniqueName = agent_id + "_" + new Date().getTime();

    public Vacancy() {

        this(null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public void setCompany_id(Long company_id) {
        this.company_id = company_id;
    }

    public void setAgent_id(Long agent_id) {
        this.agent_id = agent_id;
    }


    public String getJob_title() {
        return job_title;
    }

    public void setJob_title(String job_title) {
        this.job_title = job_title;
    }

    public String getRecruiter_name() {
        return company_name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setRecruiter_name(String recruiter_name) {
        this.company_name = recruiter_name;
    }

    public String getRef_number() {
        return ref_number;
    }

    public void setRef_number(String ref_number) {
        this.ref_number = ref_number;
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

    public String getMin_qualifications() {
        return min_qual;
    }

    public void setMin_qualifications(String min_qualifications) {
        this.min_qual = min_qualifications;
    }

    public String getDuties() {
        return duties;
    }

    public void setDuties(String duties) {
        this.duties = duties;
    }

    public String getCompetencies() {
        return key_competencies;
    }

    public void setCompetencies(String competencies) {
        this.key_competencies = competencies;
    }

    public Date getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(Date closingDate) {
        this.closingDate = closingDate;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getImageURL() {
        return imageUrl;
    }

    public void setImageURL(String imageURL) {
        this.imageUrl = imageURL;
    }

    public Long getCompany_id() {
        return company_id;
    }

    public Long getAgent_id() {
        return agent_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getAdvertDate() {
        return advertDate;
    }

    public Vacancy(Vacancy vacancy) {
        this(vacancy.getId(), vacancy.getJob_title(), vacancy
                        .getRecruiter_name(), vacancy.getRef_number(), vacancy
                        .getLocation(), vacancy.getProvince(), vacancy
                        .getMin_qualifications(), vacancy.getDuties(), vacancy
                        .getCompetencies(), vacancy.getClosingDate(), vacancy
                        .getWebsite(), vacancy.getImageURL(), vacancy.getCompany_id(),
                vacancy.getAgent_id(), vacancy.getStatus());
    }

    public Vacancy(Long id, String job_title, String recruiter_name,
                   String ref_number, String location, String province,
                   String min_qualifications, String duties, String competencies,
                   Date closingDate, String website, String imageURL,

                   Long company_id, Long agent_id, String status) {

        this.id = id;
        this.job_title = job_title;
        this.company_name = recruiter_name;
        this.ref_number = ref_number;
        this.location = location;
        this.province = province;
        this.min_qual = min_qualifications;
        this.duties = duties;
        this.key_competencies = competencies;
        this.closingDate = closingDate;
        this.website = website;
        this.imageUrl = imageURL;
        this.company_id = company_id;
        this.agent_id = agent_id;
        this.status = status;
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

}

