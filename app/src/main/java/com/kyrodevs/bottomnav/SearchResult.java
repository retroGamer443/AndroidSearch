package com.kyrodevs.bottomnav;

public class SearchResult {
    private String username;
    private String userid;

    public SearchResult(String name, String id) {
        this.username = name;
        this.userid = id;
    }


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
