package com.project.hotelmanagement.service.impl;

import com.project.hotelmanagement.dto.request.ServiceItemRequest;
import com.project.hotelmanagement.dto.response.ServiceItemResponse;
import com.project.hotelmanagement.exception.AppException;
import com.project.hotelmanagement.mapper.ServiceMapper;
import com.project.hotelmanagement.models.ServiceCategory;
import com.project.hotelmanagement.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.project.hotelmanagement.exception.ErrorCode.SERVICE_HAS_BOOKINGS;
import static com.project.hotelmanagement.exception.ErrorCode.SERVICE_ITEM_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ServiceItemService {
    private final ServiceRepository serviceRepo;
    private final ServiceCategoryService serviceCategoryService;
    private final ServiceMapper mapper;

    
    public List<ServiceItemResponse> getServices() {
        return serviceRepo.findAll().stream().map(mapper::toServiceResponse).toList();
    }

    
    public ServiceItemResponse createService(ServiceItemRequest request) {
        com.project.hotelmanagement.models.ServiceItem serviceItem = mapper.toServiceItem(request);
         ServiceCategory serviceCategory = serviceCategoryService.findById(request.getCategoryId());
        if (serviceItem.getIsAvailable() == null) {
            serviceItem.setIsAvailable(true);
        }
        serviceItem.setCategory(serviceCategory);
        serviceRepo.save(serviceItem);
        return mapper.toServiceResponse(serviceItem);
    }

    public com.project.hotelmanagement.models.ServiceItem getById (int id){
        return serviceRepo.findById(id).orElseThrow(() -> new AppException(SERVICE_ITEM_NOT_FOUND));
    }

    
    public ServiceItemResponse updateService(int id, ServiceItemRequest request) {
        com.project.hotelmanagement.models.ServiceItem serviceItem = getById(id);
        if(serviceItem.getIsAvailable() == null){
            serviceItem.setIsAvailable(true);
        }
        mapper.updateServiceItem(serviceItem, request);
        serviceRepo.save(serviceItem);
        return mapper.toServiceResponse(serviceItem);
    }

    
    public boolean deleteService(int id) {
        try {
            com.project.hotelmanagement.models.ServiceItem serviceItem = getById(id);
            if (serviceItem.getHealthyCare() != null && !serviceItem.getHealthyCare().isEmpty()) {
                throw new AppException(SERVICE_HAS_BOOKINGS);
            }
            serviceRepo.delete(serviceItem);
            log.info("Delete service successfully: {}", id);
            return true;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting service: {}", e.getMessage());
            return false;
        }
    }

    
    public ServiceItemResponse getDetailService(int id) {
       com.project.hotelmanagement.models.ServiceItem serviceItem = findById(id);
        return mapper.toServiceResponse(serviceItem);
    }

    
    public com.project.hotelmanagement.models.ServiceItem findById(int id) {
        return serviceRepo.findById(id).orElseThrow(()-> new AppException(SERVICE_ITEM_NOT_FOUND));
    }
}
