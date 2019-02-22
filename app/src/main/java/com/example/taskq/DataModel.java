package com.example.taskq;

public class DataModel {
    private String mTitle;
    private String mRemark;
    private String mType;
    private String mUrgency;
    private String mImportance;
    private String mEasiness;

    public DataModel(){}
    public DataModel(String Title, String Remark,String Type, String Urgency, String Importance, String Easiness){
        mTitle = Title;
        mRemark = Remark;
        mType = Type;
        mUrgency = Urgency;
        mImportance = Importance;
        mEasiness = Easiness;
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

    public String getUrgency() {
        return mUrgency;
    }

    public String getImportance() {
        return mImportance;
    }

    public String getEasiness() {
        return mEasiness;
    }

    public void setTitle(String Title) {
        mTitle = Title;
    }

    public void setEasiness(String mEasiness) {
        this.mEasiness = mEasiness;
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

    public void setUrgency(String Urgency) {
        mUrgency = Urgency;
    }
}
