package com.example.taskq;

import java.util.ArrayList;
import java.util.UUID;

public class DataModel {
    private String Title;
    private String Remark;

    private String Type;
    private String Repeat;
    private String TypeId;
    private String RepeatId;
    private String TargetTimestamp;
    private String DoneTimestamp;
    private ArrayList<Integer> maxStreak;
    private int streakCount;
    private int currentStreak;
    private UUID Uuid;

    public DataModel(){}

    public DataModel(DataModel dataModel) {
        this.Title = dataModel.Title;
        this.Remark = dataModel.Remark;
        this.Type = dataModel.Type;
        this.Repeat = dataModel.Repeat;
        this.TypeId = dataModel.TypeId;
        this.RepeatId = dataModel.RepeatId;
        this.TargetTimestamp = dataModel.TargetTimestamp;
        this.DoneTimestamp = dataModel.DoneTimestamp;
        this.maxStreak = dataModel.maxStreak;
        this.streakCount = dataModel.streakCount;
        this.currentStreak = dataModel.currentStreak;
        this.Uuid = dataModel.Uuid;
    }

    public DataModel(String Title, String Remark, String Type, String Repeat,
                     String Taskid, String Repeatid, String TargetTimestamp) {
        this.Title = Title;
        this.Remark = Remark;
        this.Type = Type;
        this.Repeat = Repeat;
        this.RepeatId = Repeatid;
        this.TypeId = Taskid;
        this.TargetTimestamp = TargetTimestamp;
        this.streakCount = 1;
        this.maxStreak = new ArrayList<Integer>();
        this.maxStreak.add(0);
        this.currentStreak = 0;
        this.DoneTimestamp = "0";

    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String mTitle) {
        this.Title = mTitle;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String mRemark) {
        this.Remark = mRemark;
    }

    public String getType() {
        return Type;
    }

    public void setType(String mType) {
        this.Type = mType;
    }

    public String getRepeat() {
        return Repeat;
    }

    public void setRepeat(String mRepeat) {
        this.Repeat = mRepeat;
    }

    public int getMaxStreakValue() {
        return maxStreak.get(streakCount);
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public ArrayList<Integer> getMaxStreak() {
        return maxStreak;
    }

    public void setMaxStreak(ArrayList<Integer> maxStreak) {
        this.maxStreak = maxStreak;
    }

    public void insertMaxStreak(int maxStreak) {
        this.maxStreak.add(maxStreak);
        streakCount++;
        if (streakCount == 14) {
            this.maxStreak.remove(0);
            streakCount--;
        }
    }

    public String getRepeatId() {
        return RepeatId;
    }

    public void setRepeatId(String mRepeatid) {
        this.RepeatId = mRepeatid;
    }

    public String getTargetTimestamp() {
        return TargetTimestamp;
    }

    public void setTargetTimestamp(String mTargetTimestamp) {
        this.TargetTimestamp = mTargetTimestamp;
    }

    public String getTaskId() {
        return TypeId;
    }

    public String getDoneTimestamp() {
        return DoneTimestamp;
    }

    public void setDoneTimestamp(String mDoneTimestamp) {
        this.DoneTimestamp = mDoneTimestamp;
    }

    public void setTypeId(String mTaskid) {
        this.TypeId = mTaskid;
    }

    public UUID getUuid() {
        return Uuid;
    }

    public void setUuid(UUID uuid) {
        this.Uuid = uuid;
    }
}
