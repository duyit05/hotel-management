package com.project.hotelmanagement.service.impl;

import com.project.hotelmanagement.dto.request.TypeRequest;
import com.project.hotelmanagement.dto.response.TypeResponse;
import com.project.hotelmanagement.exception.AppException;
import com.project.hotelmanagement.exception.InvalidDataException;
import com.project.hotelmanagement.mapper.TypeMapper;
import com.project.hotelmanagement.models.Type;
import com.project.hotelmanagement.repository.TypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.project.hotelmanagement.exception.ErrorCode.TYPE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TypeService {
    private final TypeRepository typeRepository;
    private final TypeMapper typeMapper;

    
    public List<TypeResponse> getTypes() {
        return typeRepository.findAll().stream().map(typeMapper::toTypeResponse).toList();
    }

    
    public TypeResponse createType(TypeRequest request) {
        Type type = typeMapper.toType(request);
        typeRepository.save(type);
        return typeMapper.toTypeResponse(type);
    }

    private Type findById(int id) {
        return typeRepository.findById(id).orElseThrow(() -> new AppException(TYPE_NOT_FOUND));
    }

    
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

    
    public TypeResponse updateType(int id, TypeRequest request) {
        Type type = findById(id);
        typeMapper.updateType(type,request);
        typeRepository.save(type);
        return typeMapper.toTypeResponse(type);
    }
}
