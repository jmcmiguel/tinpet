package com.example.tinpet.entities;

public class ReportItem {

    private String reportee;
    private String reporter;
    private String dateReported;
    private String reportDetails;
    private String reportCategory;
    private String reportImageUrl;

    public ReportItem(String reportee, String reporter, String dateReported, String reportDetails, String reportCategory, String reportImageUrl){
        this.reportee = reportee;
        this.reporter = reporter;
        this.dateReported = dateReported;
        this.reportDetails = reportDetails;
        this.reportCategory = reportCategory;
        this.reportImageUrl = reportImageUrl;
    }

    public String getReportDetails() {
        return reportDetails;
    }

    public void setReportDetails(String reportDetails) {
        this.reportDetails = reportDetails;
    }

    public String getReportCategory() {
        return reportCategory;
    }

    public void setReportCategory(String reportCategory) {
        this.reportCategory = reportCategory;
    }

    public String getReportImageUrl() {
        return reportImageUrl;
    }

    public void setReportImageUrl(String reportImageUrl) {
        this.reportImageUrl = reportImageUrl;
    }


    public String getReportee() {
        return reportee;
    }

    public void setReportee(String reportee) {
        this.reportee = reportee;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getDateReported() {
        return dateReported;
    }

    public void setDateReported(String dateReported) {
        this.dateReported = dateReported;
    }
}
