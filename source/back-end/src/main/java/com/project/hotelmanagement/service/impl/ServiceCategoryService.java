package com.project.hotelmanagement.service.impl;

import com.project.hotelmanagement.dto.request.ServiceCategoryRequest;
import com.project.hotelmanagement.dto.response.ServiceCategoryResponse;
import com.project.hotelmanagement.exception.AppException;
import com.project.hotelmanagement.exception.InvalidDataException;
import com.project.hotelmanagement.mapper.ServiceCategoryMapper;
import com.project.hotelmanagement.models.ServiceCategory;
import com.project.hotelmanagement.repository.ServiceCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.project.hotelmanagement.exception.ErrorCode.SERVICE_CATEGORY_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ServiceCategoryService {
    private final ServiceCategoryRepository serviceCategoryRepo;
    private final ServiceCategoryMapper mapper;
    
    public List<ServiceCategoryResponse> getServiceCategories() {
        return serviceCategoryRepo.findAll().stream().map(mapper::toServiceCategoryResponse).toList();
    }

    
    public ServiceCategoryResponse createServiceCategory(ServiceCategoryRequest request) {
        ServiceCategory serviceCategory = mapper.toServiceCategory(request);
        serviceCategoryRepo.save(serviceCategory);
        return mapper.toServiceCategoryResponse(serviceCategory);
    }

    
    public ServiceCategory findById (int id){
        return serviceCategoryRepo.findById(id).orElseThrow(() -> new AppException(SERVICE_CATEGORY_NOT_FOUND));
    }
    
    public ServiceCategoryResponse updateServiceCategory(int id, ServiceCategoryRequest request) {
        ServiceCategory serviceCategory = findById(id);
        mapper.updateServiceCategory(serviceCategory, request);
        serviceCategoryRepo.save(serviceCategory);
        return mapper.toServiceCategoryResponse(serviceCategory);
    }

    
    public boolean deleteServiceCategory(int id) {
        try {
            ServiceCategory serviceCategory = findById(id);
            serviceCategoryRepo.delete(serviceCategory);
            return true;
        }catch (InvalidDataException e){
            log.error("error: {}", e.getMessage());
        }
        return false;
    }
}
