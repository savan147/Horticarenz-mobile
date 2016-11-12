package com.chrisoft.horticare.model;

import java.io.Serializable;

/**
 * Model used to store Recipe Information
 * Created by Dahye on 11/08/2015.
 */
public class Recipe implements Serializable {
    private String id; //RECIPE ID
    private String name; //RECIPE NAME
    private String description; //RECIPE DESCRIPTION
    private String ingredients; //RECIPE INGREDIENTS
    private String procedure; //RECIPE PREPARATION PROCEDURE
    private String image; //RECIPE IMAGE NAME
    private String authorId; //RECIPE AUTHOR ID
    private String author; //RECIPE AUTHOR NAME
    private String rating; //RECIPE RATING
    private String category; //RECIPE CATEGORY ID
    private String cuisine; //RECIPE CUISINE ID

    public Recipe(String id, String name, String description, String author) {
        setId(id);
        setName(name);
        setDescription(description);
        setAuthor(author);
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
