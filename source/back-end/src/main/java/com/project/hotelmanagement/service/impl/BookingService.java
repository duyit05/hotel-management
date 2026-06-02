package com.project.hotelmanagement.service.impl;

import com.project.hotelmanagement.dto.request.BookingRequest;
import com.project.hotelmanagement.dto.response.BookingResponse;
import com.project.hotelmanagement.exception.AppException;
import com.project.hotelmanagement.mapper.BookingMapper;
import com.project.hotelmanagement.models.Booking;
import com.project.hotelmanagement.models.Discount;
import com.project.hotelmanagement.models.Room;
import com.project.hotelmanagement.models.User;
import com.project.hotelmanagement.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.project.hotelmanagement.enums.BookingStatus.COMPLETED;
import static com.project.hotelmanagement.enums.BookingStatus.*;
import static com.project.hotelmanagement.enums.DiscountType.PERCENTAGE;
import static com.project.hotelmanagement.enums.RoomStatus.*;
import static com.project.hotelmanagement.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepo;
    private final BookingMapper mapper;
    private final RoomService roomService;
    private final AuthenticationService auth;
    private final DiscountService discountService;

    
    public List<BookingResponse> getBookings() {
        List<Booking> bookings = bookingRepo.findAll();
        List<BookingResponse> responses = new ArrayList<>();
        for (Booking booking : bookings){
            BookingResponse response = mapper.toBookingResponse(booking);
            responses.add(response);
        }
        return responses;
    }

    
    public BookingResponse createBooking(BookingRequest request) {
//        Room room = roomService.findById(request.getRoomId());
//        Discount discount = null;
//        if(request.getDiscountId() != null){
//            discount = discountService.findById(request.getDiscountId());
//        }
//        String username = auth.getUserFromContext();
//        User user = auth.findByUsername(username);
//
//        Booking booking = new Booking();
//        if (request.getCheckInDate().isBefore(LocalDate.now())) {
//            throw new AppException(CHECK_IN_DATE_INVALID);
//        }
//        if (request.getCheckOutDate().isBefore(request.getCheckInDate()) ||
//                request.getCheckOutDate().isEqual(request.getCheckInDate())) {
//            throw new AppException(CHECK_OUT_DATE_INVALID);
//        }
//        if (request.getNumberOfGuests() > room.getType().getMaxOccupancy()) {
//            throw new AppException(NUMBER_OF_GUESTS_INVALID);
//        }
//
//        if (!room.getStatus().equals(AVAILABLE)) {
//            throw new AppException(ROOM_STATUS_NOT_WORK);
//        }
//
//        if (bookingRepo.countOverLappingBookings(room.getId(), request.getCheckInDate(), request.getCheckOutDate()) > 0) {
//            throw new AppException(ROOM_ALREADY_BOOKED_IN_THIS_TIME);
//        }
//
//        long numberDays = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
//        double totalAmount = numberDays * room.getType().getBasicPrice();
//        double discountAmount = 0;
//
//        if (discount != null) {
//            LocalDate today = LocalDate.now();
//            if (!discount.getCode().equals(request.getDiscountCode())
//                    || !discount.getIsActive()
//                    || discount.getUsingLimit() == 0
//                    || today.isBefore(discount.getStartDate())
//                    || today.isAfter(discount.getEndDate())) {
//                throw new AppException(DISCOUNT_CODE_INVALID);
//            }
//
//            if (totalAmount < discount.getMinOrderAmount()) {
//                throw new AppException(TOTAL_AMOUNT_TO_LOW);
//            }
//            if (discount.getDiscount().equals(PERCENTAGE)) {
//                discountAmount = totalAmount * discount.getDiscountValue() / 100;
//                log.info("discountAmount: {}", discountAmount);
//                if (discount.getMaxDiscountAmount() != null) {
//                    discountAmount = Math.min(discountAmount, discount.getMaxDiscountAmount());
//                }
//            } else {
//                discountAmount = Math.min(discount.getDiscountValue(), totalAmount);
//            }
//        }
//        totalAmount -= discountAmount;
//        booking.setBookingCode(generalBookingCode());
//        booking.setCheckInDate(request.getCheckInDate());
//        booking.setCheckOutDate(request.getCheckOutDate());
//        booking.setNumberOfGuests(request.getNumberOfGuests());
//        booking.setSpecialRequest(request.getSpecialRequest());
//        booking.setDiscountAmount(discountAmount);
//        booking.setTotalAmount(totalAmount);
//        booking.setNote(request.getNote());
//        booking.setStatus(PENDING);
//        booking.setUser(user);
//        booking.setRoom(room);
//        booking.setDiscount(discount);
//
//        bookingRepo.save(booking);
//        return mapper.toBookingResponse(booking);
        Booking booking = new Booking();
        Room room = roomService.findById(request.getRoomId());
        User user = auth.findByUsername(auth.getUserFromContext());
        Discount discount = request.getDiscountId() != null ? discountService.findById(request.getDiscountId()) : null;

        validateBooking(request,room);
        validateRoomAvailability(room, request);

        if (bookingRepo.countOverLappingBookings(room.getId(),request.getCheckInDate(), request.getCheckOutDate()) > 0){
            throw new AppException(ROOM_ALREADY_BOOKED_IN_THIS_TIME);
        }

        long numberDays = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        double basicDailyPrice = room.getType().getBasicPrice();
        double totalAmount = numberDays * basicDailyPrice;
        double discountAmount = 0;

        if(discount != null){
            discountAmount = calculateDiscount(request,discount,totalAmount);
            totalAmount -= discountAmount;
        }

        booking.setBookingCode(generalBookingCode());
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setNumberOfGuests(request.getNumberOfGuests());
        booking.setSpecialRequest(request.getSpecialRequest());
        booking.setDiscountAmount(discountAmount);
        booking.setTotalAmount(totalAmount);
        booking.setNote(request.getNote());
        booking.setStatus(PENDING);
        booking.setUser(user);
        booking.setRoom(room);
        booking.setDiscount(discount);
        bookingRepo.save(booking);
        return mapper.toBookingResponse(booking);
    }

    private void validateBooking (BookingRequest request, Room room){
        if(request.getCheckInDate().isBefore(LocalDate.now())){
            throw new AppException(CHECK_IN_DATE_INVALID);
        }
        if(!request.getCheckOutDate().isAfter(request.getCheckInDate())){
            throw new AppException(CHECK_OUT_DATE_INVALID);
        }
        if(request.getNumberOfGuests() > room.getType().getMaxOccupancy()){
            throw new AppException(NUMBER_OF_GUESTS_INVALID);
        }
    }

    private void validateRoomAvailability (Room room, BookingRequest request){
        if(!room.getStatus().equals(AVAILABLE)){
            throw new AppException(ROOM_STATUS_NOT_WORK);
        }
    }

    private Double calculateDiscount (BookingRequest request, Discount discount, double totalAmount) {
        LocalDate today = LocalDate.now();
        if (!discount.getCode().equals(request.getDiscountCode())
                || !discount.getIsActive()
                || discount.getUsingLimit() == 0
                || today.isBefore(discount.getStartDate())
                || today.isAfter(discount.getEndDate())) {
            throw new AppException(DISCOUNT_CODE_INVALID);
        }
        // Kiểm tra điều kiện đơn hàng tối thiểu
        if (totalAmount < discount.getMinOrderAmount()) {
            throw new AppException(TOTAL_AMOUNT_TO_LOW);
        }
        double discountAmount;
        if (discount.getDiscount().equals(PERCENTAGE)) {
            // Giảm giá theo %
            discountAmount = totalAmount * discount.getDiscountValue() / 100;
            // Áp dụng giới hạn giảm giá tối đa
            if (discount.getMaxDiscountAmount() != null) {
                discountAmount = Math.min(discountAmount, discount.getMaxDiscountAmount());
            }
        } else {
            discountAmount = Math.min(discount.getDiscountValue(), totalAmount);
        }
        if(discount.getUsingLimit() > 0){
            discount.setUsingLimit(discount.getUsingLimit() - 1);
            if(discount.getUsingLimit() == 0){
                discount.setIsActive(false);
            }
        }
        return discountAmount;
    }

    
    public BookingResponse updateBooking(int id, BookingRequest request) {
        Booking booking = findById(id);
        if(!booking.getStatus().equals(CONFIRMED)){
            throw new AppException(CHANGE_STATUS_INVALID);
        }
        if(booking.getActualCheckInTime() == null){
            booking.setActualCheckInTime(LocalDateTime.now());
        }else if (booking.getActualCheckOutTime() == null){
            booking.setActualCheckOutTime(LocalDateTime.now());
        }else {
            throw new AppException(BOOKING_ALREADY_CHECKED);
        }
        bookingRepo.save(booking);
        return mapper.toBookingResponse(booking);
    }

    
    public boolean deleteBooking(int id) {
        try {
            Booking booking = findById(id);
            if(booking.getStatus().equals(CONFIRMED) ){
                throw new AppException(DELETE_BOOKING_FAIL);
            }
            bookingRepo.delete(booking);
            return true;
        }catch (AppException e){
            log.error("Error: {}", e.getMessage());
        }
        return false;
    }

    
    public BookingResponse getDetailBooking(int id) {
        return null;
    }

    
    public Booking findById (int id){
        return bookingRepo.findById(id).orElseThrow(() -> new AppException(BOOKING_NOT_FOUND));
    }

    
    public BookingResponse confirmBooking(int id) {
        Booking booking = findById(id);
        if(!booking.getStatus().equals(PENDING)){
            throw new AppException(BOOKING_NOT_PENDING);
        }
        booking.setStatus(CONFIRMED);
        booking.getRoom().setStatus(BOOKED);
        booking.setHandledBy(auth.findByUsername(auth.getUserFromContext()));
        bookingRepo.save(booking);

        return mapper.toBookingResponse(booking);
    }


    private String generalBookingCode() {
        LocalDate today = LocalDate.now();
        // Start of day 00:00:00
        LocalDateTime start = today.atStartOfDay();
        //Start next day 00:00:00
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        int count = bookingRepo.countByDateBooking(
                java.sql.Timestamp.valueOf(start),
                java.sql.Timestamp.valueOf(end)) + 1;

        String datePart = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sequence = String.format("%03d", count);
        return "BOOK-" + datePart + "-" + sequence;
    }

    
    public boolean cancelBooking(int id) {
        try {
            Booking booking = findById(id);
            if (booking.getStatus() != PENDING  && booking.getStatus() != CONFIRMED) {
                throw new AppException(CANCEL_BOOKING_FAIL);
            }
            booking.setStatus(CANCELLED);
            booking.getRoom().setStatus(AVAILABLE);
            bookingRepo.save(booking);
            return true;
        }catch (AppException e){
            log.error("Error: {}", e.getMessage());
        }
        return false;
    }

    public BookingResponse checkInBooking (int id){
        Booking booking = findById(id);
        if(!booking.getStatus().equals(CONFIRMED)){
            throw new AppException(BOOKING_NOT_CONFIRMED);
        }
        if(booking.getActualCheckInTime() != null){
            throw new AppException(BOOKING_ALREADY_CHECKED);
        }
        LocalDate today = LocalDate.now();
        if(booking.getCheckInDate().isAfter(today)){
            throw new AppException(CHECK_IN_DATE_NOT_YET);
        }
        booking.setActualCheckInTime(LocalDateTime.now());
        booking.getRoom().setStatus(OCCUPIED);
        // TODO: Tạo Invoice nếu chưa có
        // TODO: Tính toán service charges
        bookingRepo.save(booking);
        return mapper.toBookingResponse(booking);
    }

    public BookingResponse checkOutBooking (int id){
        Booking booking = findById(id);
        if (booking.getActualCheckInTime() == null) {
            throw new AppException(BOOKING_NOT_CHECKED_IN);
        }
        if (booking.getActualCheckOutTime() != null) {
            throw new AppException(BOOKING_ALREADY_CHECKED_OUT);
        }

        booking.setActualCheckOutTime(LocalDateTime.now());
        booking.getRoom().setStatus(AVAILABLE);
        booking.setStatus(COMPLETED);

        // TODO: Tạo Invoice nếu chưa có
        // TODO: Tính toán service charges

        bookingRepo.save(booking);
        return mapper.toBookingResponse(booking);
    }

}
