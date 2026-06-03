package com.example.lostandfound.models;

public class ItemModel {

    private String _id;
    private String title;
    private String description;
    private String location;
    private String status;
    private String createdAt;
    private String type;
    private String phone;
    private String image;

    //  NEW MATCH DETAILS
    private MatchDetails matchDetails;

    //  OLD (KEEP)
    private ItemModel matchedWith;

    // OLD FINDER (KEEP)
    private Finder finder;

    // ================= GETTERS =================

    public String get_id() {
        return _id;
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

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getType() {
        return type;
    }

    public String getPhone() {
        return phone;
    }

    public String getImage() {
        return image;
    }

    //  MATCH DETAILS
    public MatchDetails getMatchDetails() {
        return matchDetails;
    }

    //  OLD MATCHED ITEM
    public ItemModel getMatchedWith() {
        return matchedWith;
    }

    // ================= SETTERS =================

    public void setImage(String image) {
        this.image = image;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setMatchedWith(ItemModel matchedWith) {
        this.matchedWith = matchedWith;
    }

    // ================= MATCH DETAILS CLASS =================

    public static class MatchDetails {

        private String title;
        private String description;
        private String phone;
        private String location;
        private String image;
        private String type;

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getPhone() {
            return phone;
        }

        public String getLocation() {
            return location;
        }

        public String getImage() {
            return image;
        }

        public String getType() {
            return type;
        }
    }

    // ================= FINDER CLASS =================

    public Finder getFinder() {
        return finder;
    }

    public static class Finder {

        private String name;
        private String phone;

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }
    }

    // ================= NOTIFICATION MODEL =================

    public static class NotificationModel {

        private String message;
        private String createdAt;

        public String getMessage() {
            return message;
        }

        public String getCreatedAt() {
            return createdAt;
        }
    }
}