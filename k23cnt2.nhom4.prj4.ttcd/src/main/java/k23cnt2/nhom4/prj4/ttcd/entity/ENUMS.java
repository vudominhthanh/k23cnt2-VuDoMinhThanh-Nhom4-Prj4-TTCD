package k23cnt2.nhom4.prj4.ttcd.entity;

public class ENUMS {
    public enum UserRole { ADMIN, STAFF, CUSTOMER }

    public enum OrderStatus { PENDING, CONFIRMED, DELIVERING, COMPLETED, CANCELLED }

    public enum PaymentMethod { CASH, BANK_TRANSFER, MOMO, VNPAY }

    public enum PaymentStatus { PENDING, SUCCESS, FAILED, REFUNDED }

    public enum DiscountType { PERCENTAGE, FIXED_AMOUNT }
}
