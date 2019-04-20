package com.example.taskq;

public class DataModel {
    private String mTitle;
    private String mRemark;

    private String mType;
    private String mRepeat;
    private String mTypeId;
    private String mRepeatId;
    private String mTargetTimestamp;
    private String mDoneTimestamp;

    public DataModel(){}

    public DataModel(String Title, String Remark, String Type, String Repeat,
                     String Taskid, String Repeatid, String TargetTimestamp) {
        mTitle = Title;
        mRemark = Remark;
        mType = Type;
        mRepeat = Repeat;
        mRepeatId = Repeatid;
        mTypeId = Taskid;
        mTargetTimestamp = TargetTimestamp;
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


    public String getRepeat() {
        return mRepeat;
    }

    public String getRepeatId() {
        return mRepeatId;
    }

    public void setRepeatId(String mRepeatid) {
        this.mRepeatId = mRepeatid;
    }

    public String getTargetTimestamp() {
        return mTargetTimestamp;
    }

    public String getTaskId() {
        return mTypeId;
    }

    public void setTargetTimestamp(String mTargetTimestamp) {
        this.mTargetTimestamp = mTargetTimestamp;
    }

    public void setType(String mType) {
        this.mType = mType;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setRemark(String mRemark) {
        this.mRemark = mRemark;
    }

    public void setRepeat(String mRepeat) {
        this.mRepeat = mRepeat;
    }

    public String getDoneTimestamp() {
        return mDoneTimestamp;
    }

    public void setDoneTimestamp(String mDoneTimestamp) {
        this.mDoneTimestamp = mDoneTimestamp;
    }

    public void setTypeId(String mTaskid) {
        this.mTypeId = mTaskid;
    }
}
