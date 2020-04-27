package fr.glog.aourir_infos.model;

import java.util.HashMap;

public class Rdv {

    public String title;
    public  String date;
    public  String text;
    public String owner;
    public String search;
    public String id_rdv;
    public String adress;
    public String Type;
    public HashMap<String,String> particip;



    public Rdv(String title, String date, String text, String owner, String search, String id_rdv, String adress,HashMap<String,String> particip, String Type) {
        this.title = title;
        this.date = date;
        this.text = text;
        this.owner = owner;
        this.search = search;
        this.id_rdv = id_rdv;
        this.adress = adress;
        this.particip = particip;
        this.Type = Type;
    }

    public Rdv() {
    }

    public HashMap<String,String>getParticip() {
        return particip;
    }

    public void setParticip(HashMap<String,String> particip) {
        this.particip = particip;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getId_rdv() {
        return id_rdv;
    }

    public void setId_rdv(String id_rdv) {
        this.id_rdv = id_rdv;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }


}
