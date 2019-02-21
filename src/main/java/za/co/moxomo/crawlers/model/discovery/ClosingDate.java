
package za.co.moxomo.crawlers.model.discovery;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "date",
    "day",
    "hours",
    "minutes",
    "month",
    "seconds",
    "time",
    "timezoneOffset",
    "year"
})
public class ClosingDate {

    @JsonProperty("date")
    private Long date;
    @JsonProperty("day")
    private Long day;
    @JsonProperty("hours")
    private Long hours;
    @JsonProperty("minutes")
    private Long minutes;
    @JsonProperty("month")
    private Long month;
    @JsonProperty("seconds")
    private Long seconds;
    @JsonProperty("time")
    private Long time;
    @JsonProperty("timezoneOffset")
    private Long timezoneOffset;
    @JsonProperty("year")
    private Long year;

    @JsonProperty("date")
    public Long getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(Long date) {
        this.date = date;
    }

    @JsonProperty("day")
    public Long getDay() {
        return day;
    }

    @JsonProperty("day")
    public void setDay(Long day) {
        this.day = day;
    }

    @JsonProperty("hours")
    public Long getHours() {
        return hours;
    }

    @JsonProperty("hours")
    public void setHours(Long hours) {
        this.hours = hours;
    }

    @JsonProperty("minutes")
    public Long getMinutes() {
        return minutes;
    }

    @JsonProperty("minutes")
    public void setMinutes(Long minutes) {
        this.minutes = minutes;
    }

    @JsonProperty("month")
    public Long getMonth() {
        return month;
    }

    @JsonProperty("month")
    public void setMonth(Long month) {
        this.month = month;
    }

    @JsonProperty("seconds")
    public Long getSeconds() {
        return seconds;
    }

    @JsonProperty("seconds")
    public void setSeconds(Long seconds) {
        this.seconds = seconds;
    }

    @JsonProperty("time")
    public Long getTime() {
        return time;
    }

    @JsonProperty("time")
    public void setTime(Long time) {
        this.time = time;
    }

    @JsonProperty("timezoneOffset")
    public Long getTimezoneOffset() {
        return timezoneOffset;
    }

    @JsonProperty("timezoneOffset")
    public void setTimezoneOffset(Long timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    @JsonProperty("year")
    public Long getYear() {
        return year;
    }

    @JsonProperty("year")
    public void setYear(Long year) {
        this.year = year;
    }

}
