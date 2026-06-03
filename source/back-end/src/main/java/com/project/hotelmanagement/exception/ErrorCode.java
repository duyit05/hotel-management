package com.project.hotelmanagement.exception;

import com.project.hotelmanagement.enums.PaymentMethod;
import com.project.hotelmanagement.enums.PaymentStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ErrorCode {
    // ERROR SYSTEM
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST(8888,"Invalid request", HttpStatus.INTERNAL_SERVER_ERROR),
    // ERROR USER
    USER_NOT_EXIST(1005, "User not existed", HttpStatus.NOT_FOUND),
    USER_INACTIVE(1006, "User is inactive", HttpStatus.BAD_REQUEST),
    USER_BLOCK(1007, "User is block", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1008, "User existed", HttpStatus.BAD_REQUEST),

    USERNAME_NOT_BLANK(1010,"Username is not blank", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1011,"Password at least 6 characters", HttpStatus.BAD_REQUEST),
    FIRST_NAME_INVALID(1012,"First name is not blank", HttpStatus.BAD_REQUEST),
    LAST_NAME_INVALID(1013,"Last name is not blank", HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_INVALID(1014,"Phone number invalid", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1015,"Email invalid", HttpStatus.BAD_REQUEST),
    DATE_OR_BIRTH_NOT_NULL(1016,"Date or birth must not be null", HttpStatus.BAD_REQUEST),
    NATIONAL_INVALID(1017,"National is not blank", HttpStatus.BAD_REQUEST),
    ID_CARD_INVALID(1018, "Id card is not blank", HttpStatus.BAD_REQUEST),


    // AUTHENTICATION
    PASSWORD_INCORRECT(2001, "Password incorrect", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(2002, "You do not have a permission", HttpStatus.FORBIDDEN),
    UNAUTHORIZED(2003,"Unauthorized - Token is missing or invalid", HttpStatus.UNAUTHORIZED),
    OLD_PASSWORD_INVALID(2003,"Old password invalid", HttpStatus.UNAUTHORIZED),
    NEW_PASSWORD_NOT_MATCH(2004,"New password not match", HttpStatus.BAD_REQUEST),

    // ENUM GENDER TYPE
    VALUE_GENDER_TYPE_INVALID (3001,"Value gender type must contains in (0, 1, 2)", HttpStatus.BAD_REQUEST),
    VALUE_USER_STATUS_INVALID (3002,"Value user status must contains in (0, 1, 2)", HttpStatus.BAD_REQUEST),
    VALUE_USER_RANK_INVALID (3004,"Value user rank must contains in (0, 1, 2)", HttpStatus.BAD_REQUEST),

    // ENUM ROLE TYPE
    ROLE_NOT_FOUND (4001,"Role not found", HttpStatus.BAD_REQUEST),


    // TYPE
    TYPE_NOT_FOUND(5001,"Type not found", HttpStatus.BAD_REQUEST),
    TYPE_INVALID(5002, "Type not be not blank", HttpStatus.BAD_REQUEST),
    NAME_INVALID(5003, "Name not be not blank", HttpStatus.BAD_REQUEST),
    AMENITIES_INVALID(5004,"Amenities not be not blank", HttpStatus.BAD_REQUEST),
    BASIC_PRICE_INVALID(5005,"Basic price must be not null",HttpStatus.BAD_REQUEST),
    BASIC_PRICE_TO_LOW(5006,"Basic price must greater than 100.000",HttpStatus.BAD_REQUEST),
    MAX_OCCUPANCY(5007,"Occupancy must be not null", HttpStatus.BAD_REQUEST),
    MAX_OCCUPANCY_TOO_SMALL(5008,"Occupancy must >= 2", HttpStatus.BAD_REQUEST),
    MAX_OCCUPANCY_TOO_LARGE(5009,"Occupancy must <= 8", HttpStatus.BAD_REQUEST),

    // ROOM
    ROOM_NOT_FOUND(6000,"Room not found", HttpStatus.BAD_REQUEST),
    CODE_INVALID(6001,"Code must not be blank", HttpStatus.BAD_REQUEST),
    NUMBER_ROOM_INVALID(6002, "Number room must not be blank", HttpStatus.BAD_REQUEST),
    ROME_NAME_INVALID(6003, "Name must be not blank", HttpStatus.BAD_REQUEST),
    ADDRESS_INVALID(6004,"Address must not be blank", HttpStatus.BAD_REQUEST),
    ROOM_STATUS_NOT_WORK(6005,"Room is maintenance or booked", HttpStatus.BAD_REQUEST),

    // DISCOUNT
    CODE_DISCOUNT_INVALID(7000,"Code discount must not be blank", HttpStatus.BAD_REQUEST),
    NAME_DISCOUNT_INVALID(7001,"Name discount must not be blank", HttpStatus.BAD_REQUEST),

    DISCOUNT_VALUE_INVALID(7002,"Discount value must be not null",HttpStatus.BAD_REQUEST),
    DISCOUNT_VALUE_TO_LOW(7003,"Discount value must greater than 5%",HttpStatus.BAD_REQUEST),
    DISCOUNT_VALUE_TO_MAX(7004,"Discount value must less than 20%",HttpStatus.BAD_REQUEST),

    MAX_DISCOUNT_AMOUNT_INVALID(7005, "Max discount must be not null",HttpStatus.BAD_REQUEST),
    MAX_DISCOUNT_AMOUNT_TO_LOW(7006,"Min discount amount must be greater than or equal to 100,000", HttpStatus.BAD_REQUEST),
    MAX_DISCOUNT_AMOUNT_TO_MAX(7007,"Min discount amount must be lesser than or equal to 500.000", HttpStatus.BAD_REQUEST),

    MIN_ORDER_AMOUNT_INVALID(7008,"Min order amount must be not null",HttpStatus.BAD_REQUEST),
    MIN_ORDER_AMOUNT_TO_LOW(7009,"Min order amount must be greater than or equal to 1.000.000", HttpStatus.BAD_REQUEST),
    MIN_ORDER_AMOUNT_TO_MAX(7010,"Min discount amount must be lesser than or equal to 2.000.000", HttpStatus.BAD_REQUEST),

    USING_LIMIT(7011,"Limit must be not null",HttpStatus.BAD_REQUEST),
    USING_LIMIT_MIN(7012,"Limit must be greater than or equal to 2",HttpStatus.BAD_REQUEST),
    USING_LIMIT_MAX(7013,"Limit must be lesser than or equal to 5",HttpStatus.BAD_REQUEST),

    START_DATE_INVALID(7014,"Start date must be not null", HttpStatus.BAD_REQUEST),
    END_DATE_INVALID(7015, "End date must be not null", HttpStatus.BAD_REQUEST),
    DISTCOUNT_NOT_FOUND(7016,"Discount not found", HttpStatus.BAD_REQUEST),

    // PAYMENT
    PAYMENT_CODE_INVALID(8000, "Payment code must be not null", HttpStatus.BAD_REQUEST),
    PAYMENT_METHOD_INVALID(8001, "Payment method must contains in (0, 1, 2,3)", HttpStatus.BAD_REQUEST),
    PAYMENT_STATUS_INVALID(8002, "Payment status must contains in (0, 1, 2,3)", HttpStatus.BAD_REQUEST),
    PAYMENT_DATE_INVALID(8003, "Payment date must not be null", HttpStatus.BAD_REQUEST),
    PAYMENT_NOT_FOUND(8004, "Payment not found", HttpStatus.BAD_REQUEST),

    // SERVICE CATEGORY
    SERVICE_CATEGORY_NOT_FOUND(11001, "Service category not found", HttpStatus.NOT_FOUND),
    SERVICE_CATEGORY_NAME_INVALID(11002, "Service category name must not be blank", HttpStatus.BAD_REQUEST),
    SERVICE_CATEGORY_NAME_EXISTED(11003, "Service category name already existed", HttpStatus.BAD_REQUEST),
    SERVICE_CATEGORY_HAS_SERVICES(11004, "Cannot delete category that has services", HttpStatus.BAD_REQUEST),

    // SERVICE
    SERVICE_ITEM_NOT_FOUND(11005, "Service not found", HttpStatus.NOT_FOUND),
    SERVICE_ITEM_NAME_INVALID(11006, "Service name must not be blank", HttpStatus.BAD_REQUEST),
    SERVICE_ITEM_PRICE_INVALID(11007, "Service price must be greater than 100.000", HttpStatus.BAD_REQUEST),
    SERVICE_ITEM_PRICE_NULL(11008, "Service price must not be null", HttpStatus.BAD_REQUEST),
    SERVICE_ITEM_UNIT_INVALID(11009, "Service unit must greater than 1", HttpStatus.BAD_REQUEST),
    SERVICE_CATEGORY_NOT_FOUND_FOR_SERVICE(11010, "Service category not found", HttpStatus.NOT_FOUND),
    SERVICE_CATEGORY_INACTIVE(11011, "Service category is inactive", HttpStatus.BAD_REQUEST),
    SERVICE_HAS_BOOKINGS(11012, "Cannot delete service that has been used in bookings", HttpStatus.BAD_REQUEST),
    SERVICE_ALREADY_EXISTS(11013, "Service name already exists in this category", HttpStatus.BAD_REQUEST),

    // BOOKING
    BOOKING_NOT_FOUND(1200, "Booking not found", HttpStatus.NOT_FOUND),
    BOOKING_CODE_INVALID(1201, "Booking code must not be blank", HttpStatus.BAD_REQUEST),
    BOOKING_CODE_EXISTED(1202, "Booking code already existed", HttpStatus.BAD_REQUEST),
    CHECK_IN_DATE_INVALID(1203, "Check-in date must be today or future", HttpStatus.BAD_REQUEST),
    CHECK_OUT_DATE_INVALID(1204, "Check-out date must be after check-in date", HttpStatus.BAD_REQUEST),
    NUMBER_OF_GUESTS_INVALID(1205, "Number of guests exceeds room capacity", HttpStatus.BAD_REQUEST),
    ROOM_NOT_AVAILABLE(1206, "Room is not available for selected dates", HttpStatus.BAD_REQUEST),
    ROOM_ALREADY_BOOKED(1207, "Room is already booked for selected dates", HttpStatus.BAD_REQUEST),
    ROOM_STATUS_NOT_AVAILABLE(1208, "Room status must be AVAILABLE", HttpStatus.BAD_REQUEST),
    BOOKING_CANNOT_CANCEL(1209, "Cannot cancel confirmed or completed booking", HttpStatus.BAD_REQUEST),
    BOOKING_STATUS_INVALID(1210, "Invalid booking status", HttpStatus.BAD_REQUEST),
    BOOKING_OVERLAP(1211, "Booking dates overlap with existing confirmed booking", HttpStatus.BAD_REQUEST),
    ROOM_ID_IS_NOT_NULL(1212,"Room id must not be null", HttpStatus.BAD_REQUEST),
    ROOM_ALREADY_BOOKED_IN_THIS_TIME(1213, "Room is already booked in this time", HttpStatus.BAD_REQUEST),
    DISCOUNT_CODE_INVALID (1214,"Discount code invalid or expiry time or used or inactive", HttpStatus.BAD_REQUEST),
    DISCOUNT_CODE_OUT_EXPIRY_TIME(1215, "Out expiry time to using this code", HttpStatus.BAD_REQUEST),
    TOTAL_AMOUNT_TO_LOW(1216,"Total not enough to using this discount", HttpStatus.BAD_REQUEST),
    CHANGE_STATUS_INVALID(1217, "Can't change status in this time", HttpStatus.BAD_REQUEST),
    BOOKING_ALREADY_CHECKED(1218, "Booking already checked", HttpStatus.BAD_REQUEST),
    CANCEL_BOOKING_FAIL(1219,"You booked so can't booking in this time", HttpStatus.BAD_REQUEST),
    DELETE_BOOKING_FAIL(1230,"Can't delete because booking is booked", HttpStatus.BAD_REQUEST),
    BOOKING_NOT_PENDING(1231,"This booking not pending can't confirm", HttpStatus.BAD_REQUEST),
    BOOKING_NOT_CONFIRMED(1232,"Can't check in because booking don't confirm" , HttpStatus.BAD_REQUEST),
    CHECK_IN_DATE_NOT_YET(1233,"Check in date not yey", HttpStatus.BAD_REQUEST),
    BOOKING_ALREADY_CHECKED_OUT(1234,"This booking already checkout", HttpStatus.BAD_REQUEST),
    BOOKING_NOT_CHECKED_IN(1235,"Can't check out because this booking not yet check in", HttpStatus.BAD_REQUEST),
    // HEALTHY_CARE
    HEALTHY_CARE_NOT_FOUND(1301,"Healthy care not found", HttpStatus.BAD_REQUEST),
    HEALTHY_CARE_NOT_WORK(1302, "This healthy care service is not work", HttpStatus.BAD_REQUEST),
    QUANTITY_INVALID(1303,"Quantity must be greater than 0", HttpStatus.BAD_REQUEST),
    ADD_HEALTHY_FAIL(1304, "Your booking cancelled can't using", HttpStatus.BAD_REQUEST);
    int code;
    String message;
    HttpStatusCode statusCode;
}
