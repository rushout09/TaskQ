package com.example.taskq;

public class DataModel {
    private String mTitle;
    private String mRemark;
    private String mType;
    private String mRepeat;
    private String mOnby;
    private String mTargetdate;
    private String mTargetday;
    private String mTargettime;
    private String mImportance;
    private String mTaskid;
    private String mRepeatid;
    private String mOnbyid;


    public DataModel(){}

    public DataModel(String Title, String Remark, String Type, String Repeat, String Onby, String Targetdate, String Targetday, String Targettime,
                     String Importance, String Taskid, String Repeatid, String Onbyid) {
        mTitle = Title;
        mRemark = Remark;
        mType = Type;
        mRepeat = Repeat;
        mOnby = Onby;
        mTargetdate = Targetdate;
        mTargetday = Targetday;
        mTargettime = Targettime;
        mImportance = Importance;
        mOnbyid = Onbyid;
        mRepeatid = Repeatid;
        mTaskid = Taskid;

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

    public String getOnby() {
        return mOnby;
    }

    public void setOnby(String mOnby) {
        this.mOnby = mOnby;
    }

    public String getRepeat() {
        return mRepeat;
    }

    public void setRepeat(String mRepeat) {
        this.mRepeat = mRepeat;
    }

    public String getTargetdate() {
        return mTargetdate;
    }

    public void setTargetdate(String mTargetdate) {
        this.mTargetdate = mTargetdate;
    }

    public String getTargetday() {
        return mTargetday;
    }

    public void setTargetday(String mTargetday) {
        this.mTargetday = mTargetday;
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

    public String getTargettime() {
        return mTargettime;
    }

    public void setTargettime(String mTargettime) {
        this.mTargettime = mTargettime;
    }

    public String getOnbyid() {
        return mOnbyid;
    }

    public String getRepeatid() {
        return mRepeatid;
    }

    public String getTaskid() {
        return mTaskid;
    }
}
