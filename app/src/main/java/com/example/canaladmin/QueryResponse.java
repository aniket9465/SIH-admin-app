package com.example.canaladmin;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QueryResponse{

    @SerializedName("responseList")
    @Expose
    private List<QueryItem> responseList = null;

    public List<QueryItem> getResponseList() {
        return responseList;
    }

    public void setResponseList(List<QueryItem> responseList) {
        this.responseList = responseList;
    }

}