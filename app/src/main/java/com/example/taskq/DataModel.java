package com.example.taskq;

public class DataModel {
    private String mTitle;
    private String mRemark;
    private String mType;
    private String mImportance;


    public DataModel(){}

    public DataModel(String Title, String Remark, String Type, String Importance) {
        mTitle = Title;
        mRemark = Remark;
        mType = Type;
        mImportance = Importance;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getRemark() {
        return mRemark;
    }

    public String getType() {
        return mType;
    }

    public String getImportance() {
        return mImportance;
    }

    public void setTitle(String Title) {
        mTitle = Title;
    }

    public void setImportance(String Importance) {
        mImportance = Importance;
    }

    public void setRemark(String Remark) {
        mRemark = Remark;
    }

    public void setType(String Type) {
        mType = Type;
    }


}
