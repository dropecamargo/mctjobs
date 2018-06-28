package com.koiti.mctjobs.models;

public class Notification {

    private int id;
    private int id_user;
    private int id_work;
    private int id_workstep;
    private boolean modify_step;
    private int id_workphase;
    private boolean modify_phase;
    private int sequence;
    private String report_date;
    private String state;
    private String statebefore;
    private boolean paused;
    private boolean unpaused;
    private String pausetime;
    private String latitude;
    private String longitude;
    private String message;
    private String tittle;
    private String message_wrote;
    private boolean ignored;
    private boolean create_action;
    private String action;
    private String message_action;
    private int pictures;
    private int videos;
    private int report_type;
    private String documents;
    private boolean processing;
    private int id_step_new;

    public static final String type_online = "ONLINE";
    public static final String type_batch = "BATCH";

    public Notification(int id) {
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

    public boolean getModify_step() {
        return modify_step;
    }

    public void setModify_step(boolean modify_step) {
        this.modify_step = modify_step;
    }

    public int getId_workphase() {
        return id_workphase;
    }

    public void setId_workphase(int id_workphase) {
        this.id_workphase = id_workphase;
    }

    public boolean getModify_phase() {
        return modify_phase;
    }

    public void setModify_phase(boolean modify_phase) {
        this.modify_phase = modify_phase;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getReport_date() {
        return report_date;
    }

    public void setReport_date(String report_date) {
        this.report_date = report_date;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatebefore() {
        return statebefore;
    }

    public void setStatebefore(String statebefore) {
        this.statebefore = statebefore;
    }

    public boolean getPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean getUnpaused() {
        return unpaused;
    }

    public void setUnpaused(boolean unpaused) {
        this.unpaused = unpaused;
    }

    public String getPausetime() {
        return pausetime;
    }

    public void setPausetime(String pausetime) {
        this.pausetime = pausetime;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getMessage_wrote() {
        return message_wrote;
    }

    public void setMessage_wrote(String message_wrote) {
        this.message_wrote = message_wrote;
    }

    public boolean getIgnored() {
        return ignored;
    }

    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }

    public boolean getCreate_action() {
        return create_action;
    }

    public void setCreate_action(boolean create_action) {
        this.create_action = create_action;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessage_action() {
        return message_action;
    }

    public void setMessage_action(String message_action) {
        this.message_action = message_action;
    }

    public int getPictures() {
        return pictures;
    }

    public void setPictures(int pictures) {
        this.pictures = pictures;
    }

    public int getVideos() {
        return videos;
    }

    public void setVideos(int videos) {
        this.videos = videos;
    }

    public int getReport_type() {
        return report_type;
    }

    public void setReport_type(int report_type) {
        this.report_type = report_type;
    }

    public String getDocuments() {
        return documents;
    }

    public void setDocuments(String documents) {
        this.documents = documents;
    }

    public boolean getProcessing() {
        return processing;
    }

    public void setProcessing(boolean processing) {
        this.processing = processing;
    }

    public int getId_step_new() {
        return id_step_new;
    }

    public void setId_step_new(int id_step_new) {
        this.id_step_new = id_step_new;
    }
}
