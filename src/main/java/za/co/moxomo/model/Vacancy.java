package co.moxomo.model;

import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * Created by paballo on 2016/11/16.
 */
public class Vacancy {

    @Id
    private String id;
    private String jobTitle;
    private String description;
    private String adId;
    private String companyName;
    private String refNumber;
    private String location;
    private String province;
    private String minQual;
    private String category;
    private String duties;
    private String keyCompetencies;
    private Date advertDate;
    private Date closingDate;
    private String website;
    private String imageUrl;
    private Long companyId;
    private Long agentId;
    private String status;
    private String remuneration;
    private String source;






}

