package com.koiti.mctjobs.models;

public class Step {
    private Integer id;
    private Integer id_work;
    private String phase_text;
    private Integer sequence;
    private String message;
    private Boolean sendemail;
    private Boolean pausable;
    private String title;
    private Integer id_phase;
    private Boolean ignored;
    private Boolean ignorable;
    private String step_name;
    private Boolean textareamandatory;
    private Integer report;
    private String receiveremail;
    private Boolean textarea;
    private String date;
    private Boolean photosmandatory;
    private String expecteddate;
    private Integer pausetime;
    private Boolean paused;
    private Integer id_step;
    private Integer index;
    private Boolean photos;
    private Boolean pendingsync;

    public Step(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId_work() {
        return id_work;
    }

    public void setId_work(Integer id_work) {
        this.id_work = id_work;
    }

    public String getPhase_text() {
        return phase_text;
    }

    public void setPhase_text(String phase_text) {
        this.phase_text = phase_text;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSendemail() {
        return sendemail;
    }

    public void setSendemail(Boolean sendemail) {
        this.sendemail = sendemail;
    }

    public Boolean getPausable() {
        return pausable;
    }

    public void setPausable(Boolean pausable) {
        this.pausable = pausable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getId_phase() {
        return id_phase;
    }

    public void setId_phase(Integer id_phase) {
        this.id_phase = id_phase;
    }

    public Boolean getIgnored() {
        return ignored;
    }

    public void setIgnored(Boolean ignored) {
        this.ignored = ignored;
    }

    public Boolean getIgnorable() {
        return ignorable;
    }

    public void setIgnorable(Boolean ignorable) {
        this.ignorable = ignorable;
    }

    public String getStep_name() {
        return step_name;
    }

    public void setStep_name(String step_name) {
        this.step_name = step_name;
    }

    public Boolean getTextareamandatory() {
        return textareamandatory;
    }

    public void setTextareamandatory(Boolean textareamandatory) {
        this.textareamandatory = textareamandatory;
    }

    public Integer getReport() {
        return report;
    }

    public void setReport(Integer report) {
        this.report = report;
    }

    public String getReceiveremail() {
        return receiveremail;
    }

    public void setReceiveremail(String receiveremail) {
        this.receiveremail = receiveremail;
    }

    public Boolean getTextarea() {
        return textarea;
    }

    public void setTextarea(Boolean textarea) {
        this.textarea = textarea;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getPhotosmandatory() {
        return photosmandatory;
    }

    public void setPhotosmandatory(Boolean photosmandatory) {
        this.photosmandatory = photosmandatory;
    }

    public String getExpecteddate() {
        return expecteddate;
    }

    public void setExpecteddate(String expecteddate) {
        this.expecteddate = expecteddate;
    }

    public Integer getPausetime() {
        return pausetime;
    }

    public void setPausetime(Integer pausetime) {
        this.pausetime = pausetime;
    }

    public Boolean getPaused() {
        return paused;
    }

    public void setPaused(Boolean paused) {
        this.paused = paused;
    }

    public Integer getId_step() {
        return id_step;
    }

    public void setId_step(Integer id_step) {
        this.id_step = id_step;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Boolean getPhotos() {
        return photos;
    }

    public void setPhotos(Boolean photos) {
        this.photos = photos;
    }

    public Boolean getPendingsync() {
        return pendingsync;
    }

    public void setPendingsync(Boolean pendingsync) {
        this.pendingsync = pendingsync;
    }
}