package com.example.taskq;

public class DataModel {
    private String mTitle;
    private String mRemark;

    private String mType;
    private String mRepeat;
    private String mTaskid;
    private String mRepeatid;

    private String mImportance;
    private String mInitialTimestamp;
    private String mTargetTimestamp;

    public DataModel(){}

    public DataModel(String Title, String Remark, String Type, String Repeat, String Importance,
                     String Taskid, String Repeatid, String TargetTimestamp, String InitialTimestamp) {
        mTitle = Title;
        mRemark = Remark;
        mType = Type;
        mRepeat = Repeat;
        mImportance = Importance;
        mRepeatid = Repeatid;
        mTaskid = Taskid;
        mTargetTimestamp = TargetTimestamp;
        mInitialTimestamp = InitialTimestamp;
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

    public String getRepeat() {
        return mRepeat;
    }

    public String getRepeatid() {
        return mRepeatid;
    }

    public String getTaskid() {
        return mTaskid;
    }

    public String getTargetTimestamp() {
        return mTargetTimestamp;
    }

    public void setTargetTimestamp(String mTargetTimestamp) {
        this.mTargetTimestamp = mTargetTimestamp;
    }

    public String getInitialTimestamp() {
        return mInitialTimestamp;
    }

    public void setInitialTimestamp(String mInitialTimestamp) {
        this.mInitialTimestamp = mInitialTimestamp;
    }
}
