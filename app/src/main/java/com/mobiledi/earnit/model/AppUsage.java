package com.mobiledi.earnit.model;

import com.google.gson.annotations.SerializedName;
import com.mobiledi.earnit.activity.usageStats.CustomUsageStats;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AppUsage {

    @SerializedName("name")
    private String name;

    @SerializedName("usage")
    private List<UsageTime> usage;

    public AppUsage from(CustomUsageStats customUsageStats){
        name = customUsageStats.getAppName();
        usage = new ArrayList<>();
        String[] timeUsage = customUsageStats.getUsingTimeAsString();
        usage.add(new UsageTime(timeUsage[0], timeUsage[1]));
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UsageTime> getUsage() {
        return usage;
    }

    public void setUsage(List<UsageTime> usage) {
        this.usage = usage;
    }

    public long getAppUsageDurationInMillis(){
        long millis = 0;
        if (usage != null || usage.size() != 0){
            for (UsageTime usageTime : usage){
                millis += usageTime.getUsageTimeInMillis();
            }
        }
        return millis;
    }

    public String getAppUsageDurationAsString(){
        long millis = 0;
        if (usage != null || usage.size() != 0){
            for (UsageTime usageTime : usage){
                millis += usageTime.getUsageTimeInMillis();
            }
        }
        Period period = new Period(millis);
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendDays()
                .appendSuffix(" day", " days")
                .appendMinutes()
                .appendSuffix(" minute", " minutes")
                .appendSeconds()
                .appendSuffix(" second", " seconds")
                .toFormatter();
        return formatter.print(period);
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\":\"" + name + '\"' +
                ", \"usage\":" + usage +
                '}';
    }

    class UsageTime {

        @SerializedName("startDateTimeWithZone")
        String startDateTimeWithZone;
        @SerializedName("endDateTimeWithZone")
        String endDateTimeWithZone;

        public UsageTime() {
        }

        public UsageTime(String startDateTimeWithZone, String endDateTimeWithZone) {
            this.startDateTimeWithZone = startDateTimeWithZone;
            this.endDateTimeWithZone = endDateTimeWithZone;
        }

        private String mDateFormat = "yyyy-MM-dd'T'HH:mm:ssZZ";

        public String getStartDateTimeWithZone() {
            return startDateTimeWithZone;
        }

        public void setStartDateTimeWithZone(String startDateTimeWithZone) {
            this.startDateTimeWithZone = startDateTimeWithZone;
        }

        public String getEndDateTimeWithZone() {
            return endDateTimeWithZone;
        }

        public void setEndDateTimeWithZone(String endDateTimeWithZone) {
            this.endDateTimeWithZone = endDateTimeWithZone;
        }

        public DateTime getStartDateTimeWithZoneAsDateTime() {
            DateTimeFormatter formatter = DateTimeFormat.forPattern(mDateFormat).withLocale(Locale.US);
            DateTime dt = formatter.parseDateTime(startDateTimeWithZone);
            return dt;
        }

        public void setStartDateTimeWithZoneAsDateTime(DateTime startDateTimeWithZone) {
            this.startDateTimeWithZone = startDateTimeWithZone.toString(mDateFormat);
        }

        public DateTime getEndDateTimeWithZoneAsDateTime() {
            DateTimeFormatter formatter = DateTimeFormat.forPattern(mDateFormat).withLocale(Locale.US);
            DateTime dt = formatter.parseDateTime(endDateTimeWithZone);
            return dt;
        }

        public void setEndDateTimeWithZoneAsDateTime(DateTime endDateTimeWithZone) {
            this.endDateTimeWithZone = endDateTimeWithZone.toString(mDateFormat);
        }

        public long getUsageTimeInMillis(){
            return new Duration(getStartDateTimeWithZoneAsDateTime(), getEndDateTimeWithZoneAsDateTime()).getMillis();
        }

        @Override
        public String toString() {
            return "{" +
                    "\"startDateTimeWithZone\":\"" + startDateTimeWithZone + '\"' +
                    ", \"endDateTimeWithZone\":\"" + endDateTimeWithZone + '\"' +
                    '}';
        }
    }
}
