document.addEventListener("DOMContentLoaded", function() {
    // 1. Khởi tạo hiệu ứng trượt AOS
    AOS.init({
        once: true,
        offset: 30
    });

    // 2. Tính giá tiền lần đầu khi load trang
    calculateTotal();

    // 3. Lắng nghe sự kiện thay đổi trên các ô Radio và Checkbox
    const inputs = document.querySelectorAll('.calc-price');
    inputs.forEach(input => {
        input.addEventListener('change', calculateTotal);
    });
});

// Hàm tăng/giảm số lượng
function updateQuantity(change) {
    const qtyInput = document.getElementById('quantityInput');
    let currentVal = parseInt(qtyInput.value);
    let newVal = currentVal + change;

    // Giới hạn số lượng từ 1 đến 50
    if(newVal >= 1 && newVal <= 50) {
        qtyInput.value = newVal;
        calculateTotal();
    }
}

// Hàm tính tổng tiền
function calculateTotal() {
    // Lấy giá gốc
    let basePrice = parseFloat(document.getElementById('basePrice').value) || 0;
    let extraPrice = 0;
    let toppingPrice = 0;

    // Lấy tiền Size đang được chọn
    const selectedSize = document.querySelector('input[name="variantId"]:checked');
    if (selectedSize) {
        extraPrice = parseFloat(selectedSize.getAttribute('data-price')) || 0;
    }

    // Lấy tổng tiền các Topping đang được tích
    const selectedToppings = document.querySelectorAll('input[name="toppingIds"]:checked');
    selectedToppings.forEach(topping => {
        toppingPrice += parseFloat(topping.getAttribute('data-price')) || 0;
    });

    // Lấy số lượng
    let quantity = parseInt(document.getElementById('quantityInput').value) || 1;

    // Công thức: (Giá gốc + Giá Size + Giá Topping) * Số lượng
    let totalPrice = (basePrice + extraPrice + toppingPrice) * quantity;

    // Format tiền tệ kiểu Việt Nam (VD: 45.000)
    let formattedPrice = new Intl.NumberFormat('vi-VN').format(totalPrice);

    // Cập nhật giá trị hiển thị lên màn hình
    document.getElementById('displayPrice').innerText = formattedPrice;
    document.getElementById('btnPriceDisplay').innerText = formattedPrice + 'đ';
}