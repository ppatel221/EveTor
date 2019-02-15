package com.example.parth.evetor;

/**
 * Created by parth on 2017-12-04.
 */

public class Event {

    String EveName = null;
    String EveCategory = null;
    String EveDate = null;

    public Event(String name, String category, String Date) {

        super();

        this.EveName = name;

        this.EveCategory = category;

        this.EveDate = Date;
    }

    public String getEveName() {

        return EveName;

    }
    public void setEveName(String code) {

        this.EveName = code;

    }
    public String getEveCategory() {

        return EveCategory;

    }
    public void setEveCategory(String name) {

        this.EveCategory = name;

    }
    public String getEveDate() {

        return EveDate;

    }
    public void setEveDate(String date) {

        this.EveDate = date;

    }

    @Override
    public String toString() {

        return  EveName + " " + EveCategory + " " + EveDate;

    }
}
