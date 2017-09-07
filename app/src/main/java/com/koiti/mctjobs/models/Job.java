package com.koiti.mctjobs.models;

public class Job {
    private int id;
    private int id_user;
    private String notes;
    private String worktypename;
    private String state;
    private String step;
    private String created;
    private String document;
    private String formatdate;
    private String abstrac;
    private String details;
    private Boolean pendingsync;

    public Job(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getWorktypename() {
        return worktypename;
    }

    public void setWorktypename(String worktypename) {
        this.worktypename = worktypename;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getFormatdate() {
        return formatdate;
    }

    public void setFormatdate(String formatdate) {
        this.formatdate = formatdate;
    }

    public String getAbstrac() {
        return abstrac;
    }

    public void setAbstrac(String abstrac) {
        this.abstrac = abstrac;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Boolean getPendingsync() {
        return pendingsync;
    }

    public void setPendingsync(Boolean pendingsync) {
        this.pendingsync = pendingsync;
    }
}
