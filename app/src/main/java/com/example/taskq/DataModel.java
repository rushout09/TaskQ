package com.example.taskq;

public class DataModel {
    private String Title;
    private String Remark;
    private String Type;
    private String Urgency;
    private String Importance;
    private String Easiness;
    public DataModel(String Title, String Remark,String Type, String Urgency, String Importance, String Easiness){
        this.Title = Title;
        this.Remark = Remark;
        this.Type = Type;
        this.Urgency = Urgency;
        this.Importance = Importance;
        this.Easiness = Easiness;
    }

    public String getTitle() {
        return Title;
    }

    public String getRemark() {
        return Remark;
    }

    public String getType() {
        return Type;
    }

    public String getUrgency() {
        return Urgency;
    }

    public String getImportance() {
        return Importance;
    }

    public String getEasiness() {
        return Easiness;
    }
}
