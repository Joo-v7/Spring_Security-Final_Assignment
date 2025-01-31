package com.homework.rest_security_final.rest_security_final.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    ADMIN, MEMBER, GOOGLE;

    @JsonValue // Serialize
    public String toJson() {
        return this.name().toLowerCase();
    }

    @JsonCreator // DeSerialize
    public static Role fromString(String str) {
        for(Role value : Role.values()) {
            if(value.name().equalsIgnoreCase(str)) {
                return value;
            }
        }
        return MEMBER; // default
    }

}
