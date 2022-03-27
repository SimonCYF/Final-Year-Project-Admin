package com.example.finalyearprojectadmin.admin;

public class AdminInfo {

    public String adminEmail, adminIc, adminPass, adminConfirmPass;

    public String getAdminEmail(){return this.adminEmail;}

    public void setAdminEmail(String a){
        this.adminEmail = a;
    }

    public String getAdminIc(){
        return this.adminIc;
    }

    public void setAdminIc(String a){
        this.adminIc = a;
    }

    public String getAdminPass(){
        return this.adminPass;
    }

    public void setAdminPass(String a){
        this.adminPass = a;
    }

}
