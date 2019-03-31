package za.co.moxomo.enums;

public class Constants {
    public static final String EMAIL_ADDRESS = "YOUR_EMAIL_ADDRESS";
    public static final String REST_API_TOKEN = "YOUR-API-TOKEN";

    public static final String REST_API_BASE_URL = "https://zoomconnect.com/app/api/rest/v1/";
    public static final String SEND_JSON_URL = REST_API_BASE_URL + "sms/send.json";
    public static final String SEND_XML_URL = REST_API_BASE_URL + "sms/send.xml";
    public static final String SEND_BULK_URL = REST_API_BASE_URL + "sms/send-bulk.json";
    public static final String ACCOUNT_BALANCE_JSON_URL = REST_API_BASE_URL + "account/balance.json";
    public static final String SEND_VOICE_TEXT_URL = REST_API_BASE_URL + "voice/single-text";
    public static final String SEND_VOICE_AUDIO_URL = REST_API_BASE_URL + "voice/single-audio";

    public static final String YYYY_MM_DD_HH_MM_DATE_FORMAT = "yyyyMMddHHmm";
}
