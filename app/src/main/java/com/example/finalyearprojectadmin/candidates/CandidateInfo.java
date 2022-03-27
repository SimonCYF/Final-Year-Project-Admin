package com.example.finalyearprojectadmin.candidates;

public class CandidateInfo {

    public String candidateImage, candidateName, candidateState, candidatePropaganda, candidateParty;
    public Integer candidateTotalVotes;

    //Getter and Settle
    public String getCandidateImage(){ return this.candidateImage;}

    public void setCandidateImage(String a){ this.candidateImage = a;}

    public String getCandidateName(){ return this.candidateName; }

    public void setCandidateName(String a){
        this.candidateName = a;
    }

    public String getCandidateState(){
        return this.candidateState;
    }

    public void setCandidateState(String a){
        this.candidateState = a;
    }

    public String getCandidatePropaganda(){
        return this.candidatePropaganda;
    }

    public void setCandidatePropaganda(String a){
        this.candidatePropaganda = a;
    }

    public String getCandidateParty(){
        return this.candidateParty;
    }

    public void setCandidateParty(String a){
        this.candidateParty = a;
    }

    public Integer getCandidateTotalVotes(){return this.candidateTotalVotes;}

    public void setCandidateTotalVotes(Integer a){ this.candidateTotalVotes = a;}

}
