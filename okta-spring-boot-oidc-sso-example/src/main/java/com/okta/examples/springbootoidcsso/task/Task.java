package com.okta.examples.springbootoidcsso.task;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
public class Task {

    @Id
    @GeneratedValue
    private Long id;
    private String userId;
    private String text;
    private Date created;

    public Task() {}

    public Task(String userId, String text, Date created) {
        this.userId = userId;
        this.text = text;
        this.created = created;
    }

    // Explicitly add the setText method
    public void setText(String text) {
        this.text = text;
    }
}