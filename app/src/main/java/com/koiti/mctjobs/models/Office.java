package com.koiti.mctjobs.models;

import java.util.ArrayList;

public class Office {

    private int id;
    private String name;

    public Office(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name.trim();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString(){
        return( name.trim() );
    }

    public static Office filter(ArrayList<Office> officesList, String name) {
        for (Office office : officesList) {
            if ( office.getName().equals(name) ) {
                return office;
            }
        }
        return null;
    }
}
