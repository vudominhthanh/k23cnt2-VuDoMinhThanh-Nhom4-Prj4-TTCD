package k23cnt2.nhom4.prj4.ttcd.service;

import k23cnt2.nhom4.prj4.ttcd.dto.CheckoutRequest;
import k23cnt2.nhom4.prj4.ttcd.dto.OrderResponseDTO;
import k23cnt2.nhom4.prj4.ttcd.entity.*;
import k23cnt2.nhom4.prj4.ttcd.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductVariantRepository productVariantRepository;
    @Autowired
    ProductOptionRepository productOptionRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    CartItemRepository cartItemRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    OrderItemOptionRepository orderItemOptionRepository;
    @Autowired
    PaymentRepository paymentRepository;

    @Transactional
    public Order checkout(String email, CheckoutRequest request) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng không có sản phẩm nào để tiến hành thanh toán!");
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());
        order.setOrderStatus(ENUMS.OrderStatus.PENDING);
        order.setOrderCode("ORD" + System.currentTimeMillis());
        order.setCreatedAt(LocalDateTime.now());
        order.setDiscountAmount(BigDecimal.ZERO);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItemsToSave = new ArrayList<>();
        List<OrderItemOption> orderItemOptionsToSave = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            ProductVariant variant = cartItem.getVariant();
            Product product = variant.getProduct();

            BigDecimal basePrice = product.getBasePrice();
            BigDecimal extraPrice = variant.getExtraPrice() != null ? variant.getExtraPrice() : BigDecimal.ZERO;
            BigDecimal singleItemPrice = basePrice.add(extraPrice);

            List<OrderItemOption> currentItemOptions = new ArrayList<>();
            for (ProductOption option : cartItem.getOptions()) {
                BigDecimal optionPrice = option.getAdditionalPrice() != null ? option.getAdditionalPrice() : BigDecimal.ZERO;
                singleItemPrice = singleItemPrice.add(optionPrice);

                OrderItemOption orderItemOption = new OrderItemOption();

                OrderItemOptionId orderItemOptionId = new OrderItemOptionId();
                orderItemOptionId.setOptionName(option.getOptionName());

                orderItemOption.setId(orderItemOptionId);
                orderItemOption.setPriceAtBuy(optionPrice);
                currentItemOptions.add(orderItemOption);
            }

            BigDecimal lineTotal = singleItemPrice.multiply(new BigDecimal(cartItem.getQuantity()));
            totalAmount = totalAmount.add(lineTotal);

            String optionsNote = currentItemOptions.stream()
                    .map(o -> o.getId().getOptionName())
                    .collect(Collectors.joining(", "));


            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductName(product.getName());
            orderItem.setVariantName(variant.getSizeName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtBuy(singleItemPrice);

            orderItemsToSave.add(orderItem);

            for (OrderItemOption oio : currentItemOptions) {
                oio.setOrderItem(orderItem);
                orderItemOptionsToSave.add(oio);
            }
        }

        order.setTotalAmount(totalAmount);
        order.setFinalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        for (OrderItem oi : orderItemsToSave) {
            orderItemRepository.save(oi);
        }

        for (OrderItemOption oio : orderItemOptionsToSave) {
            orderItemOptionRepository.save(oio);
        }

        Payment payment = new Payment();
        payment.setOrder(savedOrder);
        payment.setAmount(savedOrder.getFinalAmount());
        payment.setCreatedAt(LocalDateTime.now());

        ENUMS.PaymentMethod method = ENUMS.PaymentMethod.valueOf(request.getPaymentMethod());
        payment.setPaymentMethod(method);
        payment.setPaymentStatus(ENUMS.PaymentStatus.PENDING);

        if (method == ENUMS.PaymentMethod.CASH) {
            payment.setTransactionId("CASH-" + savedOrder.getOrderCode());
        } else {
            payment.setTransactionId("TXN-" + System.currentTimeMillis());
        }

        paymentRepository.save(payment);

        cartItemRepository.deleteAll(cartItems);

        return savedOrder;
    }

    public List<OrderResponseDTO> getMyOrders(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found!"));

        List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);

        return orders.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderDetail(String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        return mapToResponseDTO(order);
    }

    @Transactional
    public void updateOrderStatus(String orderCode, String newStatus) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        order.setOrderStatus(ENUMS.OrderStatus.valueOf(newStatus));
        orderRepository.save(order);
    }

    private OrderResponseDTO mapToResponseDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setOrderCode(order.getOrderCode());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setFinalAmount(order.getFinalAmount());

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());

        if (orderItems != null && !orderItems.isEmpty()) {
            List<OrderResponseDTO.OrderItemDTO> itemDTOs = orderItems.stream().map(item -> {
                OrderResponseDTO.OrderItemDTO itemDTO = new OrderResponseDTO.OrderItemDTO();
                itemDTO.setProductName(item.getProductName());
                itemDTO.setVariantName(item.getVariantName());
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setPriceAtBuy(item.getPriceAtBuy());
                itemDTO.setNote(item.getNote());
                return itemDTO;
            }).collect(Collectors.toList());

            dto.setItems(itemDTOs);
        }
        return dto;
    }

    @Transactional
    public Order createPOSOrder(String staffEmail, Map<String, Object> payload) {
        // 1. Tìm nhân viên đang trực máy (Lưu vào đơn để sau này tính KPI/Truy vết)
        User staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản nhân viên!"));

        // 2. Khởi tạo Đơn hàng mới
        Order order = new Order();
        order.setUser(staff);
        order.setShippingAddress("Khách mua tại quầy");
        order.setOrderStatus(ENUMS.OrderStatus.PENDING);
        order.setOrderCode("POS" + System.currentTimeMillis());
        order.setCreatedAt(LocalDateTime.now());
        order.setDiscountAmount(BigDecimal.ZERO);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItemsToSave = new ArrayList<>();
        List<OrderItemOption> orderItemOptionsToSave = new ArrayList<>();

        List<Map<String, Object>> items = (List<Map<String, Object>>) payload.get("items");

        for (Map<String, Object> itemData : items) {
            Integer productId = Integer.valueOf(itemData.get("productId").toString());
            Integer variantId = Integer.valueOf(itemData.get("variantId").toString());
            Integer quantity = Integer.valueOf(itemData.get("quantity").toString());
            BigDecimal priceAtBuy = new BigDecimal(itemData.get("priceAtBuy").toString());

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Lỗi: Sản phẩm không tồn tại"));
            ProductVariant variant = productVariantRepository.findById(variantId)
                    .orElseThrow(() -> new RuntimeException("Lỗi: Kích cỡ không tồn tại"));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductName(product.getName());
            orderItem.setVariantName(variant.getSizeName());
            orderItem.setQuantity(quantity);
            orderItem.setPriceAtBuy(priceAtBuy);

            List<String> optionNames = new ArrayList<>();
            if (itemData.get("optionIds") != null) {
                List<Integer> optionIds = (List<Integer>) itemData.get("optionIds");
                for (Integer optId : optionIds) {
                    ProductOption option = productOptionRepository.findById(optId)
                            .orElseThrow(() -> new RuntimeException("Lỗi: Tùy chọn không tồn tại"));

                    OrderItemOption orderItemOption = new OrderItemOption();
                    OrderItemOptionId orderItemOptionId = new OrderItemOptionId();
                    orderItemOptionId.setOptionName(option.getOptionName());
                    orderItemOption.setId(orderItemOptionId);

                    orderItemOption.setPriceAtBuy(option.getAdditionalPrice() != null ? option.getAdditionalPrice() : BigDecimal.ZERO);
                    orderItemOption.setOrderItem(orderItem);

                    orderItemOptionsToSave.add(orderItemOption);
                    optionNames.add(option.getOptionName());
                }
            }

            if (!optionNames.isEmpty()) {
                orderItem.setNote("Tùy chọn: " + String.join(", ", optionNames));
            }

            BigDecimal lineTotal = priceAtBuy.multiply(new BigDecimal(quantity));
            totalAmount = totalAmount.add(lineTotal);

            orderItemsToSave.add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        order.setFinalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        for (OrderItem oi : orderItemsToSave) {
            orderItemRepository.save(oi);
        }
        for (OrderItemOption oio : orderItemOptionsToSave) {
            orderItemOptionRepository.save(oio);
        }

        Payment payment = new Payment();
        payment.setOrder(savedOrder);
        payment.setAmount(savedOrder.getFinalAmount());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setPaymentMethod(ENUMS.PaymentMethod.CASH);
        payment.setPaymentStatus(ENUMS.PaymentStatus.SUCCESS);
        payment.setTransactionId("POS-CASH-" + savedOrder.getOrderCode());

        paymentRepository.save(payment);

        return savedOrder;
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderDetailById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        return mapToResponseDTO(order); // Tái sử dụng hàm mapToResponseDTO đã có sẵn của bạn
    }

    @Transactional
    public Order updateOrderStatusById(Long id, String newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        order.setOrderStatus(ENUMS.OrderStatus.valueOf(newStatus.toUpperCase()));
        return orderRepository.save(order);
    }


    public List<OrderResponseDTO> getActiveOrdersForStaff() {
        List<Order> orders = orderRepository.findByOrderStatusInOrderByCreatedAtDesc(
                java.util.Arrays.asList(
                        ENUMS.OrderStatus.PENDING,
                        ENUMS.OrderStatus.CONFIRMED,
                        ENUMS.OrderStatus.DELIVERING
                )
        );
        return orders.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }
}
