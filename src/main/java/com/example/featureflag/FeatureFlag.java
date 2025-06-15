package com.example.featureflag;

import java.util.Date;

public class FeatureFlag {
    private String id;
    private String name;
    private boolean enabled;
    private Date createdAt;
    private Date updatedAt;

    public FeatureFlag(String id, String name, boolean enabled, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public FeatureFlag() {
    }


    public String getId() { return id; }
    public String getName() { return name; }
    public boolean isEnabled() { return enabled; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}

