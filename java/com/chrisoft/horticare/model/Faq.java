package com.chrisoft.horticare.model;

import java.io.Serializable;

/**
 * The persistent class for the tbl_plantsdetails database table.
 *
 */
public class Faq implements Serializable{
    private int id;
    private String question;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    private String answer;
}
