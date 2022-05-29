package com.mustafakaya.fiform.model;

public class PostFile {

    public String email;
    public String pdfName;
    public String downloadUrl;

    public PostFile(String email, String pdfName, String downloadUrl) {
        this.email = email;
        this.pdfName = pdfName;
        this.downloadUrl = downloadUrl;
    }
}
