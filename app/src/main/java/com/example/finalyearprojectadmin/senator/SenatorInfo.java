package com.example.finalyearprojectadmin.senator;

public class SenatorInfo {

    public String getSenatorPostcode() {
        return senatorPostcode;
    }

    public void setSenatorPostcode(String senatorPostcode) {
        this.senatorPostcode = senatorPostcode;
    }

    public String getSenatorImage() {
        return senatorImage;
    }

    public String senatorPostcode, senatorName;
    public String senatorState;
    public String senatorImage;
    public Integer senatorTotalVotes;

    public void setSenatorImage(String senatorImage) {
        this.senatorImage = senatorImage;
    }

    public String getSenatorName() {
        return senatorName;
    }

    public void setSenatorName(String senatorName) {
        this.senatorName = senatorName;
    }

    public String getSenatorState() {
        return senatorState;
    }

    public void setSenatorState(String senatorState) {
        this.senatorState = senatorState;
    }


    public Integer getSenatorTotalVotes() {
        return senatorTotalVotes;
    }

    public void setSenatorTotalVotes(Integer senatorTotalVotes) {
        this.senatorTotalVotes = senatorTotalVotes;
    }
}
