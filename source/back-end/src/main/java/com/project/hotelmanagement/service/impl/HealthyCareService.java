package com.project.hotelmanagement.service.impl;

import com.project.hotelmanagement.dto.request.HealthyCareRequest;
import com.project.hotelmanagement.dto.response.BookingResponse;
import com.project.hotelmanagement.dto.response.HealthyCareResponse;
import com.project.hotelmanagement.dto.response.ServiceItemResponse;
import com.project.hotelmanagement.exception.AppException;
import com.project.hotelmanagement.mapper.BookingMapper;
import com.project.hotelmanagement.mapper.HealthyCareMapper;
import com.project.hotelmanagement.mapper.ServiceMapper;
import com.project.hotelmanagement.models.Booking;
import com.project.hotelmanagement.models.HealthyCare;
import com.project.hotelmanagement.models.ServiceItem;
import com.project.hotelmanagement.repository.HealthyCareRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.project.hotelmanagement.enums.BookingStatus.CONFIRMED;
import static com.project.hotelmanagement.enums.BookingStatus.PENDING;
import static com.project.hotelmanagement.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthyCareService{
    private final HealthyCareRepository healthyCareRepository;
    private final BookingService bookingService;
    private final ServiceItemService serviceItem;
    private final HealthyCareMapper mapper;
    private final BookingMapper bookingMapper;
    private final ServiceMapper serviceMapper;

    
    public List<HealthyCareResponse> getHealthyCares() {
        return null;
    }

    
    public HealthyCareResponse createHealthyCare(HealthyCareRequest request) {
        Booking booking = bookingService.findById(request.getBookingId());
        ServiceItem service = serviceItem.findById(request.getServiceId());

        if (!service.getIsAvailable()) {
            throw new AppException(HEALTHY_CARE_NOT_WORK);
        }
        if (booking.getStatus() != CONFIRMED && booking.getStatus() != PENDING) {
            throw new AppException(ADD_HEALTHY_FAIL);
        }
        int quantity = request.getQuantity();
        double unitPrice = service.getPrice();
        HealthyCare existingHealthyCare = healthyCareRepository.findByBookingAndService(
                request.getBookingId(),
                request.getServiceId()
        );
        HealthyCare healthyCare;
        if (existingHealthyCare != null) {
            // Đã tồn tại → Update quantity
            existingHealthyCare.setQuantity(existingHealthyCare.getQuantity() + quantity);
            existingHealthyCare.setTotalPrice(existingHealthyCare.getQuantity() * unitPrice);
            healthyCare = existingHealthyCare;
        } else {
            healthyCare = mapper.toHealthyCare(request);
            healthyCare.setBooking(booking);
            healthyCare.setService(service);
            healthyCare.setQuantity(quantity);
            healthyCare.setUnitPrice(unitPrice);
            healthyCare.setTotalPrice(quantity * unitPrice);
            healthyCare.setRegisterDate(LocalDate.now());
        }
        healthyCareRepository.save(healthyCare);
        HealthyCareResponse response = mapper.toHealthyCareResponse(healthyCare);
        if (healthyCare.getBooking() != null) {
            BookingResponse bookingResponse = bookingMapper.toBookingResponse(healthyCare.getBooking());
            response.setBooking(bookingResponse);
        }
        if(healthyCare.getService() != null){
            ServiceItemResponse serviceItemResponse = serviceMapper.toServiceResponse(healthyCare.getService());
            response.setService(serviceItemResponse);
        }
        return response;
    }

    
    public HealthyCareResponse updateHealThyCate(int id, HealthyCareRequest request) {
        HealthyCare healthyCare = findById(id);
        Booking booking = bookingService.findById(request.getBookingId());
        ServiceItem service = serviceItem.findById(request.getServiceId());
        healthyCare.setBooking(booking);
        healthyCare.setService(service);
        mapper.updateHealthyCare(healthyCare, request);
        healthyCareRepository.save(healthyCare);
        return mapper.toHealthyCareResponse(healthyCare);
    }

    
    public boolean deleteHealThyCare(int id) {
        try {
            HealthyCare healthyCare = findById(id);
            healthyCareRepository.delete(healthyCare);
            log.info("Delete healthy care success");
            return true;
        }catch (AppException e){
            log.error("Error: {}", e.getMessage());
        }
        return false;
    }

    
    public HealthyCareResponse getDetailHealThyCate(int id) {
         HealthyCare healthyCare = findById(id);
         HealthyCareResponse response = mapper.toHealthyCareResponse(healthyCare);
         if(healthyCare.getBooking() != null){
             BookingResponse bookingResponse = bookingMapper.toBookingResponse(healthyCare.getBooking());
             response.setBooking(bookingResponse);

         }
         if(healthyCare.getService() != null){
             ServiceItemResponse serviceItemResponse = serviceMapper.toServiceResponse(healthyCare.getService());
             response.setService(serviceItemResponse);
         }
         return response;
    }

    
    public HealthyCare findById(int id) {
        return healthyCareRepository.findById(id).orElseThrow(() -> new AppException(HEALTHY_CARE_NOT_FOUND));
    }
}
