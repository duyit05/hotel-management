package com.project.hotelmanagement.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GenderType {
    @JsonProperty("male")
    MALE,
    @JsonProperty("female")
    FEMALE,
    @JsonProperty("other")
    OTHER

}
