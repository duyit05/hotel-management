package com.project.hotelmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceCategoryRequest {
    @NotBlank(message = "SERVICE_CATEGORY_NAME_INVALID")
    private String name;
    private String description;
    private Boolean isActive;

}
