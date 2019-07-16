package com.vmware;

public enum ObjectTypes
{
    Folder("Folder"),
    Datacenter("https://sit.domain.com:2019/"),
    CIT("https://cit.domain.com:8080/"),
    DEV("https://dev.domain.com:21323/");

    private String url;

    ObjectTypes(String envUrl) {
        this.url = envUrl;
    }

    public String getUrl() {
        return url;
    }
}
