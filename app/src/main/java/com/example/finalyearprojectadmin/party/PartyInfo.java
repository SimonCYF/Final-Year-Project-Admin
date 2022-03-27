package com.example.finalyearprojectadmin.party;

public class PartyInfo {

    public String partyHistory;
    public String partyName;
    public String partyPropaganda;
    public String partyImage;

    //Getter and Settle
    public String getPartyName(){return partyName;}

    public void setPartyName(String name){this.partyName = name; }

    public String getPartyHistory(){return partyHistory;}

    public void setPartyHistory(String history){this.partyHistory = history;}

    public String getPartyPropaganda(){return partyPropaganda;}

    public void setPartyPropaganda(String propaganda){this.partyPropaganda = propaganda;}

    public String getPartyImage() {
        return partyImage;
    }

    public void setPartyImage(String partyImage) {
        this.partyImage = partyImage;
    }


}
