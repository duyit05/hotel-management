package com.project.hotelmanagement.service.impl;

import com.project.hotelmanagement.dto.request.DiscountRequest;
import com.project.hotelmanagement.dto.response.DiscountResponse;
import com.project.hotelmanagement.exception.AppException;
import com.project.hotelmanagement.exception.InvalidDataException;
import com.project.hotelmanagement.mapper.DiscountMapper;
import com.project.hotelmanagement.models.Discount;
import com.project.hotelmanagement.repository.DiscountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.project.hotelmanagement.exception.ErrorCode.DISTCOUNT_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;

    
    public List<DiscountResponse> getDiscounts() {
        return discountRepository.findAll().stream().map(discountMapper::toDiscountResponse).toList();
    }

    
    public DiscountResponse createDiscount(DiscountRequest request) {
        Discount discount = discountMapper.toDiscount(request);
        discountRepository.save(discount);
        return discountMapper.toDiscountResponse(discount);

    }

    
    public Discount findById (int id){
        return discountRepository.findById(id).orElseThrow(() -> new AppException(DISTCOUNT_NOT_FOUND));
    }

    
    public DiscountResponse updateDiscount(int id, DiscountRequest request) {
        Discount discount = findById(id);
        discountMapper.updateDiscount(discount,request);
        discountRepository.save(discount);
        return discountMapper.toDiscountResponse(discount);
    }

    
    public boolean deleteDiscount(int id) {
        try {
            Discount discount = findById(id);
            discountRepository.delete(discount);
            log.info("discount deleted");
            return true;
        }catch (InvalidDataException e){
            log.error("error: {}", e.getMessage());
        }
        return false;
    }
}
