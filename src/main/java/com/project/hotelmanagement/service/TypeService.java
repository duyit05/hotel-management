package com.project.hotelmanagement.service;

import com.project.hotelmanagement.dto.request.TypeRequest;
import com.project.hotelmanagement.dto.response.TypeResponse;

import java.util.List;

public interface TypeService {
    List<TypeResponse> getTypes ();
    TypeResponse createType (TypeRequest request);
    boolean deleteType (int id);
    TypeResponse updateType (int id, TypeRequest request);

}
