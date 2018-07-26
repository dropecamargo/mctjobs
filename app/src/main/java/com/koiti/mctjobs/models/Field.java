package com.koiti.mctjobs.models;

public class Field {

    private Integer id;
    private Integer id_work;
    private Integer id_step;
    private String title;
    private String type;
    private Boolean mandatory;
    private String domain;
    private String _default;
    private Integer orden;

    public Field(int id, int id_work, int id_step) {
        this.id = id;
        this.id_work = id_work;
        this.id_step = id_step;
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

    public Integer getId_step() {
        return id_step;
    }

    public void setId_step(Integer id_step) {
        this.id_step = id_step;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String get_default() {
        return _default;
    }

    public void set_default(String _default) {
        this._default = _default;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }
}
