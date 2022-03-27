package com.example.finalyearprojectadmin.voter;

public class VoterInfo {

    public String voterIcNum;
    public String voterName;
    public String voterState;
    public String voterEmail;
    public String voterHpNumber;
    public String voterAddress;
    public String voterPostcode;
    public String voterVoteStatus;
    public String voterVoteCandidate;
    public String voterVerifiedStatus;

    public String getVoterEmail() {
        return voterEmail;
    }

    public void setVoterEmail(String voterEmail) {
        this.voterEmail = voterEmail;
    }


    public String getVoterFrontIc() {
        return voterFrontIc;
    }

    public void setVoterFrontIc(String voterFrontIc) {
        this.voterFrontIc = voterFrontIc;
    }

    public String getVoterBackIc() {
        return voterBackIc;
    }

    public void setVoterBackIc(String voterBackIc) {
        this.voterBackIc = voterBackIc;
    }

    public String voterFrontIc;
    public String voterBackIc;
   // public String voterIcNum, voterName, voterState, voterVoteStatus, voterVoteCandidate, voterVerifiedStatus, voterHpNumber;

    public VoterInfo(){}

    //Getter n Settle

    public String getVoterAddress() {
        return voterAddress;
    }

    public void setVoterAddress(String voterAddress) {
        this.voterAddress = voterAddress;
    }

    public String getVoterPostcode() {
        return voterPostcode;
    }

    public void setVoterPostcode(String voterPostcode) {
        this.voterPostcode = voterPostcode;
    }

    public String getVoterIcNum() {
        return voterIcNum;
    }

    public void setVoterIcNum(String voterIcNum) {
        this.voterIcNum = voterIcNum;
    }

    public String getVoterName() {
        return voterName;
    }

    public void setVoterName(String voterName) {
        this.voterName = voterName;
    }

    public String getVoterState() {
        return voterState;
    }

    public void setVoterState(String voterState) {
        this.voterState = voterState;
    }

    public String getVoterHpNumber() {
        return voterHpNumber;
    }

    public void setVoterHpNumber(String voterHpNumber) {
        this.voterHpNumber = voterHpNumber;
    }

    public String getVoterVoteStatus() {
        return voterVoteStatus;
    }

    public void setVoterVoteStatus(String voterVoteStatus) {
        this.voterVoteStatus = voterVoteStatus;
    }

    public String getVoterVoteCandidate() {
        return voterVoteCandidate;
    }

    public void setVoterVoteCandidate(String voterVoteCandidate) {
        this.voterVoteCandidate = voterVoteCandidate;
    }

    public String getVoterVerifiedStatus() {
        return voterVerifiedStatus;
    }

    public void setVoterVerifiedStatus(String voterVerifiedStatus) {
        this.voterVerifiedStatus = voterVerifiedStatus;
    }
}

