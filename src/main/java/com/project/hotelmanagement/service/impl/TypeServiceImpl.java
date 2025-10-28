package com.project.hotelmanagement.service.impl;

import com.project.hotelmanagement.dto.request.TypeRequest;
import com.project.hotelmanagement.dto.response.TypeResponse;
import com.project.hotelmanagement.exception.AppException;
import com.project.hotelmanagement.exception.InvalidDataException;
import com.project.hotelmanagement.models.Type;
import com.project.hotelmanagement.repository.TypeRepository;
import com.project.hotelmanagement.service.TypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.project.hotelmanagement.exception.ErrorCode.TYPE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TypeServiceImpl implements TypeService {
    private final TypeRepository typeRepository;

    @Override
    public List<TypeResponse> getTypes() {
        return typeRepository.findAll().stream().map(
                type -> TypeResponse.builder()
                        .id(type.getId())
                        .name(type.getName())
                        .basicPrice(type.getBasicPrice())
                        .amenities(type.getAmenities())
                        .maxOccupancy(type.getMaxOccupancy())
                        .type(type.getType())
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public TypeResponse createType(TypeRequest request) {
        Type type = new Type();
        type.setName(request.getName());
        type.setType(request.getType());
        type.setAmenities(request.getAmenities());
        type.setMaxOccupancy(request.getMaxOccupancy());
        type.setBasicPrice(request.getBasicPrice());

        typeRepository.save(type);
        return TypeResponse.builder()
                .id(type.getId())
                .name(type.getName())
                .amenities(type.getAmenities())
                .maxOccupancy(type.getMaxOccupancy())
                .basicPrice(type.getBasicPrice())
                .build();

    }

    private Type findById(int id) {
        return typeRepository.findById(id).orElseThrow(() -> new AppException(TYPE_NOT_FOUND));
    }

    @Override
    public boolean deleteType(int id) {
        try {
            Type type = findById(id);
            typeRepository.delete(type);
            log.info("Delete type success");
            return true;
        } catch (InvalidDataException e) {
            log.error("error: {}", e.getCause().getMessage());
        }
        return false;
    }

    @Override
    public TypeResponse updateType(int id, TypeRequest request) {
        Type type = findById(id);
        type.setName(request.getName());
        type.setType(request.getType());
        type.setAmenities(request.getAmenities());
        type.setMaxOccupancy(request.getMaxOccupancy());
        type.setBasicPrice(request.getBasicPrice());

        typeRepository.save(type);
        return TypeResponse.builder()
                .id(type.getId())
                .name(type.getName())
                .type(type.getType())
                .amenities(type.getAmenities())
                .maxOccupancy(type.getMaxOccupancy())
                .basicPrice(type.getBasicPrice())
                .build();
    }
}
