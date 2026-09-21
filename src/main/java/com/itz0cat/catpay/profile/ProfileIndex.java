package com.itz0cat.catpay.profile;

import java.util.ArrayList;
import java.util.List;

public class ProfileIndex {
    private int version = 1;
    private List<ProfileEntry> profiles = new ArrayList<>();

    public static class ProfileEntry {
        private String id;
        private String name;
        private String file;
        private boolean isDefault;

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

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public boolean isDefault() {
            return isDefault;
        }

        public void setDefault(boolean aDefault) {
            isDefault = aDefault;
        }
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<ProfileEntry> getProfiles() {
        return profiles != null ? profiles : new ArrayList<>();
    }

    public void setProfiles(List<ProfileEntry> profiles) {
        this.profiles = profiles;
    }
}
