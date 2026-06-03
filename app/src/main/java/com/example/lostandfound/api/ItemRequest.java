package com.example.lostandfound.api;

public class ItemRequest {


private String title;
private String description;
private String location;
private String phone;
private String type;

public ItemRequest(String title, String description, String location, String phone, String type) {
    this.title = title;
    this.description = description;
    this.location = location;
    this.phone = phone;
    this.type = type;
}

public String getTitle() {
    return title;
}

public String getDescription() {
    return description;
}

public String getLocation() {
    return location;
}

public String getPhone() {
    return phone;
}

public String getType() {
    return type;
}


}
