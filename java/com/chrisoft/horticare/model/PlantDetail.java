package com.chrisoft.horticare.model;

import java.io.Serializable;
import java.util.Date;


/**
 * The persistent class for the tbl_plantsdetails database table.
 *
 */
public class PlantDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getModifiedby() {
        return modifiedby;
    }

    public void setModifiedby(String modifiedby) {
        this.modifiedby = modifiedby;
    }

    public Date getModifiedon() {
        return modifiedon;
    }

    public void setModifiedon(Date modifiedon) {
        this.modifiedon = modifiedon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlattypeid() {
        return plattypeid;
    }

    public void setPlattypeid(int plattypeid) {
        this.plattypeid = plattypeid;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public String getSpecie() {
        return specie;
    }

    public void setSpecie(String specie) {
        this.specie = specie;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSubSpecie() {
        return subSpecie;
    }

    public void setSubSpecie(String subSpecie) {
        this.subSpecie = subSpecie;
    }

    private int pid;

    private String author;

    private int cid;

    private String description;

    private String imageURL;

    private String modifiedby;

    private Date modifiedon;

    private String name;

    private int plattypeid;

    private String reference;

    private int season;

    private String specie;

    private int status;

    private String subSpecie;

    public PlantDetail() {
    }


}