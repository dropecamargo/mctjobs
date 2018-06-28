package com.koiti.mctjobs.models;

public class Phase {
    private Integer id;
    private Integer id_phase;
    private Integer id_work;
    private Integer step;
    private String name;
    private Boolean unsorted;
    private String begindate;

    public Phase(int id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId_phase() {
        return id_phase;
    }

    public void setId_phase(Integer id_phase) {
        this.id_phase = id_phase;
    }

    public Integer getId_work() {
        return id_work;
    }

    public void setId_work(Integer id_work) {
        this.id_work = id_work;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getUnsorted() {
        return unsorted;
    }

    public void setUnsorted(Boolean unsorted) {
        this.unsorted = unsorted;
    }

    public String getBegindate() {
        return begindate;
    }

    public void setBegindate(String begindate) {
        this.begindate = begindate;
    }
}
