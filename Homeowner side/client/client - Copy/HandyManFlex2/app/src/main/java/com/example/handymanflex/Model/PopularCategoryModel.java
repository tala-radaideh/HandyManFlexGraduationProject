package com.example.handymanflex.Model;

public class PopularCategoryModel {
    private String menu_id,  services_id,  name,  image;

    public PopularCategoryModel() {
    }

    public PopularCategoryModel(String menu_id, String services_id, String name, String image) {
        this.menu_id = menu_id;
        this.services_id = services_id;
        this.name = name;
        this.image = image;
    }


    public String getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(String menu_id) {
        this.menu_id = menu_id;
    }

    public String getServices_id() {
        return services_id;
    }

    public void setServices_id(String services_id) {
        this.services_id = services_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
