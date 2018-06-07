package com.koiti.mctjobs.models;

public class Document {
    private int id;
    private int id_work;
    private int id_workstep;
    private int id_report;
    private String name;
    private String type;
    private String content;
    private boolean processing;

    public Document(int id) {
        this.id = id;
    }

    public int getId_work() {
        return id_work;
    }

    public void setId_work(int id_work) {
        this.id_work = id_work;
    }

    public int getId_workstep() {
        return id_workstep;
    }

    public void setId_workstep(int id_workstep) {
        this.id_workstep = id_workstep;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_report() {
        return id_report;
    }

    public void setId_report(int id_report) {
        this.id_report = id_report;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean getProcessing() {
        return processing;
    }

    public void setProcessing(boolean processing) {
        this.processing = processing;
    }
}
