package com.ats.model;

/**
 * Domain model representing a categorized skill.
 */
public class Skill {
    private String name;
    private String category;

    public Skill() {}

    public Skill(String name, String category) {
        this.name = name;
        this.category = category;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String name;
        private String category;

        public Builder name(String name) { this.name = name; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Skill build() { return new Skill(name, category); }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
