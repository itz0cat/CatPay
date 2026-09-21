package com.itz0cat.catpay.profile;

import java.util.ArrayList;
import java.util.List;

public class PaymentProfile {
    private String id = "default";
    private String name = "Default";
    private List<String> serverMatchers = new ArrayList<>();
    private IconConfig icon = new IconConfig();
    private PaymentConfig payment = new PaymentConfig();

    public static class IconConfig {
        private boolean enabled = true;
        private String type = "unicode";
        private String glyph = "\uE058";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getType() {
            return type != null ? type : "unicode";
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getGlyph() {
            return glyph != null ? glyph : "";
        }

        public void setGlyph(String glyph) {
            this.glyph = glyph;
        }
    }

    public static class PaymentConfig {
        private String sent = "{icon} ${amount} has been sent to {user}.";
        private String received = "{icon} ${amount} has been received from {user}.";

        public String getSent() {
            return sent != null ? sent : "{icon} ${amount} has been sent to {user}.";
        }

        public void setSent(String sent) {
            this.sent = sent;
        }

        public String getReceived() {
            return received != null ? received : "{icon} ${amount} has been received from {user}.";
        }

        public void setReceived(String received) {
            this.received = received;
        }
    }

    public String getId() {
        return id != null ? id : "default";
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name != null ? name : "Default";
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getServerMatchers() {
        return serverMatchers != null ? serverMatchers : new ArrayList<>();
    }

    public void setServerMatchers(List<String> serverMatchers) {
        this.serverMatchers = serverMatchers;
    }

    public IconConfig getIcon() {
        return icon != null ? icon : new IconConfig();
    }

    public void setIcon(IconConfig icon) {
        this.icon = icon;
    }

    public PaymentConfig getPayment() {
        return payment != null ? payment : new PaymentConfig();
    }

    public void setPayment(PaymentConfig payment) {
        this.payment = payment;
    }
}
