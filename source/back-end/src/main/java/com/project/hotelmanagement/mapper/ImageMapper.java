package com.project.hotelmanagement.mapper;

import com.project.hotelmanagement.dto.request.ImageRequest;
import com.project.hotelmanagement.dto.response.ImageResponse;
import com.project.hotelmanagement.models.Image;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    ImageResponse toImageResponse (ImageRequest request);

    List<ImageResponse> toListImageResponse (List<Image> images);
}
