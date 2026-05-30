// --- KHỞI TẠO BIẾN TOÀN CỤC ---
let stompClient = null;
let currentSelectedOrderId = null;
let localCart = [];
let currentProductInModal = null;

// --- 1. TIỆN ÍCH QUẢN LÝ BẢO MẬT JWT ---
function getAuthHeaders() {
    const token = localStorage.getItem('token');
    return {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : ''
    };
}

function logoutStaff() {
    if(confirm("Bạn có chắc chắn muốn đăng xuất khỏi ca làm việc?")) {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        window.location.href = '/auth'; // Đường dẫn về trang đăng nhập của bạn
    }
}

// --- 2. KẾT NỐI REAL-TIME WEBSOCKET (Đính kèm JWT) ---
function connect() {
    const socket = new SockJS('/ws-order');
    stompClient = Stomp.over(socket);
    stompClient.debug = null;

    // Gửi kèm JWT Token lên Header để vượt qua lớp lọc bảo mật khi kết nối cổng WS
    const headers = {
        'Authorization': 'Bearer ' + localStorage.getItem('token')
    };

    stompClient.connect(headers, function (frame) {
        console.log('WebSocket Connected: ' + frame);

        // Đăng ký nhận luồng dữ liệu đơn hàng
        stompClient.subscribe('/topic/orders', function (message) {
            const orderUpdate = JSON.parse(message.body);
            updateOrderUI(orderUpdate);
        });
    }, function (error) {
        console.error("Lỗi kết nối WebSocket. Thử lại sau 5 giây...", error);
        setTimeout(connect, 5000);
    });
}

// --- 3. ĐIỀU PHỐI CHẾ ĐỘ HIỂN THỊ (POS vs XEM ĐƠN) ---
function switchToPOSMode() {
    currentSelectedOrderId = null;
    document.getElementById('btn-back-to-pos').style.display = 'none';
    document.getElementById('pos-menu-view').setAttribute('style', 'display: flex !important');
    document.getElementById('order-detail-view').setAttribute('style', 'display: none !important');
    document.getElementById('pos-cart-side').setAttribute('style', 'display: flex !important');
    document.getElementById('order-action-side').setAttribute('style', 'display: none !important');
}

// --- 4. TẢI DỮ LIỆU SẢN PHẨM TỪ DATABASE LÊN MENU POS ---
async function loadProductsToMenu() {
    try {
        const response = await fetch('/api/products/home', {
            method: 'GET',
            headers: getAuthHeaders()
        });

        if (!response.ok) throw new Error("Không thể tải danh sách món");

        const products = await response.json();
        const productListContainer = document.getElementById('product-list');

        if (products.length === 0) {
            productListContainer.innerHTML = '<div class="col-12 text-center text-muted">Không có sản phẩm nào</div>';
            return;
        }

        // Tạo thẻ div cho từng sản phẩm uống dựa trên cấu trúc giao diện n4_product
        productListContainer.innerHTML = products.map(product => `
                    <div class="col">
                        <div class="product-item h-100 d-flex flex-column align-items-center justify-content-between p-3"
                             onclick="openProductOptionsModal(${product.id})">
                            <img src="/images/${product.imageUrl || 'default.jpg'}" class="img-fluid rounded mb-2" style="max-height: 80px; object-fit: cover;">
                            <div class="product-name fw-bold small text-dark text-center">${product.name}</div>
                            <div class="product-price text-primary fw-bold mt-1">${product.basePrice.toLocaleString()}đ</div>
                        </div>
                    </div>
                `).join('');
    } catch (error) {
        console.error("Lỗi tải danh sách sản phẩm thực tế:", error);
    }
}

// --- 5. LOGIC XỬ LÝ GIỎ HÀNG TẠI QUẦY (POS MODE) ---

async function openProductOptionsModal(productId) {
    try {
        const response = await fetch(`/api/products/${productId}/details`, {
            method: 'GET',
            headers: getAuthHeaders()
        });

        if (!response.ok) throw new Error("Lỗi tải chi tiết sản phẩm");

        const details = await response.json();
        currentProductInModal = details; // Lưu vào biến toàn cục để dùng khi ấn "Thêm vào giỏ"

        document.getElementById('modalProductName').innerText = details.name;
        document.getElementById('modalBasePrice').innerText = `Giá gốc: ${details.basePrice.toLocaleString()}đ`;

        // Render Sizes (Radio Buttons)
        const sizeContainer = document.getElementById('modalSizes');
        if (details.variants && details.variants.length > 0) {
            sizeContainer.innerHTML = details.variants.map((v, index) => `
                <div class="form-check">
                    <input class="form-check-input" type="radio" name="productSize" id="size_${v.id}" value="${v.id}" ${index === 0 ? 'checked' : ''}>
                    <label class="form-check-label" for="size_${v.id}">
                        ${v.sizeName} (+${v.extraPrice.toLocaleString()}đ)
                    </label>
                </div>
            `).join('');
        } else {
            sizeContainer.innerHTML = '<span class="text-muted small">Size tiêu chuẩn</span>';
        }

        // Render Toppings/Options (Checkboxes)
        const optionContainer = document.getElementById('modalOptions');
        if (details.options && details.options.length > 0) {
            optionContainer.innerHTML = details.options.map(o => `
                <div class="form-check mb-1">
                    <input class="form-check-input topping-checkbox" type="checkbox" id="option_${o.id}" value="${o.id}" data-price="${o.additionalPrice}" data-name="${o.optionName}">
                    <label class="form-check-label d-flex justify-content-between w-100" for="option_${o.id}">
                        <span>${o.optionName}</span>
                        <span class="text-success">+${o.additionalPrice.toLocaleString()}đ</span>
                    </label>
                </div>
            `).join('');
        } else {
            optionContainer.innerHTML = '<span class="text-muted small">Không có topping kèm theo</span>';
        }

        // Reset Sugar/Ice về mặc định
        document.getElementById('sugarLevel').value = "100";
        document.getElementById('iceLevel').value = "100";

        // Mở Modal Bootstrap
        const modal = new bootstrap.Modal(document.getElementById('productOptionsModal'));
        modal.show();
    } catch (error) {
        console.error("Lỗi mở Popup:", error);
    }
}

document.getElementById('btnConfirmAddToCart').addEventListener('click', () => {
    if (!currentProductInModal) return;

    // 1. Lấy thông tin Size
    let selectedSizeId = null;
    let sizeName = "Mặc định";
    let extraSizePrice = 0;
    const sizeRadios = document.getElementsByName('productSize');
    if (sizeRadios.length > 0) {
        const checkedSize = Array.from(sizeRadios).find(r => r.checked);
        if (checkedSize) {
            selectedSizeId = parseInt(checkedSize.value);
            const variant = currentProductInModal.variants.find(v => v.id === selectedSizeId);
            sizeName = variant.sizeName;
            extraSizePrice = variant.extraPrice;
        }
    }

    // 2. Lấy thông tin Topping
    const selectedOptions = [];
    let extraOptionPrice = 0;
    document.querySelectorAll('.topping-checkbox:checked').forEach(cb => {
        const p = parseFloat(cb.getAttribute('data-price'));
        selectedOptions.push({ id: cb.value, name: cb.getAttribute('data-name'), price: p });
        extraOptionPrice += p;
    });

    // 3. Lấy thông tin Đường / Đá
    const sugar = document.getElementById('sugarLevel').value;
    const ice = document.getElementById('iceLevel').value;

    // 4. Tính tổng tiền cho 1 ly này
    const finalItemPrice = currentProductInModal.basePrice + extraSizePrice + extraOptionPrice;

    // 5. Tạo Unique ID để gom nhóm trong giỏ hàng (Cùng món, cùng size, cùng topping, đá, đường thì cộng dồn số lượng)
    const optionIdsStr = selectedOptions.map(o => o.id).sort().join(',');
    const cartItemId = `${currentProductInModal.id}_S${selectedSizeId}_O${optionIdsStr}_SU${sugar}_IC${ice}`;

    const existingItem = localCart.find(item => item.cartId === cartItemId);

    if (existingItem) {
        existingItem.quantity += 1;
    } else {
        localCart.push({
            cartId: cartItemId,
            productId: currentProductInModal.id,
            name: currentProductInModal.name,
            price: finalItemPrice,
            quantity: 1,
            sizeName: sizeName,
            sugar: sugar,
            ice: ice,
            options: selectedOptions.map(o => o.name).join(', ')
        });
    }

    renderLocalCart();
    bootstrap.Modal.getInstance(document.getElementById('productOptionsModal')).hide();
});

function renderLocalCart() {
    const cartContainer = document.getElementById('current-cart');

    if (localCart.length === 0) {
        cartContainer.innerHTML = `<div class="text-center text-muted my-5"><i class="fas fa-shopping-basket fa-2x mb-2 d-block"></i>Hóa đơn trống</div>`;
        document.getElementById('final-total').innerText = '0đ';
        return;
    }

    let subTotal = 0;
    cartContainer.innerHTML = localCart.map((item, index) => {
        const itemTotal = item.price * item.quantity;
        subTotal += itemTotal;

        // Cấu trúc mô tả ngắn gọn bên dưới tên món
        let desc = `${item.sizeName} | Đ:${item.sugar}% | ĐK:${item.ice === 'hot' ? 'Nóng' : item.ice+'%'}`;
        if (item.options) desc += ` | ${item.options}`;

        return `
            <div class="d-flex justify-content-between align-items-center border-bottom py-2 animate__animated animate__fadeInFast">
                <div style="max-width: 55%;">
                    <div class="fw-bold text-dark text-truncate">${item.name}</div>
                    <small class="text-muted d-block" style="font-size: 0.65rem;">${desc}</small>
                    <small class="text-primary fw-bold">${item.price.toLocaleString()}đ</small>
                </div>
                <div class="d-flex align-items-center gap-2">
                    <button class="btn btn-xs btn-outline-secondary px-2 py-0" onclick="updateQuantity(${index}, -1)">-</button>
                    <span class="fw-bold fs-6">${item.quantity}</span>
                    <button class="btn btn-xs btn-outline-secondary px-2 py-0" onclick="updateQuantity(${index}, 1)">+</button>
                </div>
                <span class="fw-bold text-end" style="min-width: 65px;">${itemTotal.toLocaleString()}đ</span>
            </div>
        `;
    }).join('');

    document.getElementById('final-total').innerText = `${subTotal.toLocaleString()}đ`;
}

async function createOrderAtCounter() {
    if (localCart.length === 0) {
        alert("Vui lòng chọn món trước!");
        return;
    }

    const orderPayload = {
        paymentMethod: "CASH",
        items: localCart.map(item => ({
            productId: item.productId,
            quantity: item.quantity,
            priceAtBuy: item.price,
            variantName: item.sizeName,
            note: `Đường ${item.sugar}%, Đá ${item.ice}%. Topping: ${item.options || 'Không'}`
        }))
    };

    try {
        const response = await fetch('/api/staff/orders/create-at-counter', {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(orderPayload)
        });

        if (response.ok) {
            alert("Đã xuất Bill thành công!");
            localCart = [];
            renderLocalCart();
        } else {
            alert("Lỗi lưu đơn hàng.");
        }
    } catch (error) {
        console.error(error);
    }
}

function initializeSearchFilters() {
    document.getElementById('orderSearch').addEventListener('input', function (e) {
        const keyword = e.target.value.trim().toLowerCase();
        document.querySelectorAll('#order-grid-inner .col-4').forEach(card => {
            card.style.setProperty('display', card.innerText.toLowerCase().includes(keyword) ? 'block' : 'none', 'important');
        });
    });

    document.getElementById('productSearch').addEventListener('input', function (e) {
        const keyword = e.target.value.trim().toLowerCase();
        document.querySelectorAll('#product-list .col').forEach(card => {
            card.style.setProperty('display', card.innerText.toLowerCase().includes(keyword) ? 'block' : 'none', 'important');
        });
    });
}

function updateQuantity(index, change) {
    localCart[index].quantity += change;
    if (localCart[index].quantity <= 0) {
        localCart.splice(index, 1);
    }
    renderLocalCart();
}

// --- 6. GỬI REQUEST API TẠO ĐƠN TẠI QUẦY XUỐNG DATABASE ---
async function createOrderAtCounter() {
    if (localCart.length === 0) {
        alert("Vui lòng chọn ít nhất một món nước trước khi xuất hóa đơn!");
        return;
    }

    // Đóng gói mảng giỏ hàng khớp định dạng bảng n4_order & n4_orderitem
    const orderPayload = {
        paymentMethod: "CASH",
        items: localCart.map(item => ({
            productId: item.id,
            quantity: item.quantity,
            priceAtBuy: item.price
        }))
    };

    try {
        const response = await fetch('/api/staff/orders/create-at-counter', {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(orderPayload)
        });

        if (response.ok) {
            alert("Tạo hóa đơn tại quầy thành công! Hệ thống tiến hành in Bill cứng.");
            localCart = [];
            renderLocalCart();
        } else {
            alert("Không thể lưu đơn hàng. Vui lòng kiểm tra lại quyền hạn!");
        }
    } catch (error) {
        console.error("Lỗi API tạo hóa đơn tại quầy:", error);
    }
}

// --- 7. BỘ LỌC TÌM KIẾM THỜI GIAN THỰC (REAL-TIME SEARCH FILTER) ---
function initializeSearchFilters() {
    // A. Bộ lọc tìm mã đơn hàng hệ thống (Sidebar trái)
    document.getElementById('orderSearch').addEventListener('input', function (e) {
        const keyword = e.target.value.trim().toLowerCase();
        const orderCards = document.querySelectorAll('#order-grid-inner .col-4');

        orderCards.forEach(card => {
            const codeText = card.querySelector('.order-code').innerText.toLowerCase();
            if (codeText.includes(keyword)) {
                card.style.setProperty('display', 'block', 'important');
            } else {
                card.style.setProperty('display', 'none', 'important');
            }
        });
    });

    // B. Bộ lọc tìm tên sản phẩm nước uống (Khung giữa)
    document.getElementById('productSearch').addEventListener('input', function (e) {
        const keyword = e.target.value.trim().toLowerCase();
        const productCards = document.querySelectorAll('#product-list .col');

        productCards.forEach(card => {
            const nameText = card.querySelector('.product-name').innerText.toLowerCase();
            if (nameText.includes(keyword)) {
                card.style.setProperty('display', 'block', 'important');
            } else {
                card.style.setProperty('display', 'none', 'important');
            }
        });
    });
}

// --- 8. HIỂN THỊ CHI TIẾT ĐƠN HÀNG HỆ THỐNG ---
async function showOrderDetail(orderId) {
    currentSelectedOrderId = orderId;

    document.getElementById('btn-back-to-pos').style.display = 'block';
    document.getElementById('pos-menu-view').setAttribute('style', 'display: none !important');
    document.getElementById('order-detail-view').setAttribute('style', 'display: flex !important');
    document.getElementById('pos-cart-side').setAttribute('style', 'display: none !important');
    document.getElementById('order-action-side').setAttribute('style', 'display: flex !important');

    try {
        const response = await fetch(`/api/staff/orders/${orderId}`, {
            method: 'GET',
            headers: getAuthHeaders()
        });

        if (!response.ok) throw new Error("Lỗi tải dữ liệu API");

        const order = await response.json();

        document.getElementById('viewOrderCode').innerText = `Chi tiết đơn #${order.orderCode}`;
        document.getElementById('viewStatus').innerText = order.orderStatus;
        document.getElementById('viewStatus').className = `badge ${getBadgeColor(order.orderStatus)}`;
        document.getElementById('viewTotal').innerText = `${order.finalAmount.toLocaleString()}đ`;

        const itemsHtml = order.items.map(item => `
            <div class="d-flex justify-content-between border-bottom py-3 fs-5">
                <div>
                    <strong class="text-dark">${item.productName}</strong>
                    <span class="badge bg-secondary ms-2">x${item.quantity}</span>
                    <div class="text-muted small fs-6">${item.variantName || 'Mặc định'}</div>
                </div>
                <span class="fw-bold text-end">${(item.priceAtBuy * item.quantity).toLocaleString()}đ</span>
            </div>
        `).join('');
        document.getElementById('viewOrderItems').innerHTML = itemsHtml;

        const btnConfirm = document.getElementById('btnConfirm');
        const btnComplete = document.getElementById('btnComplete');

        if (order.orderStatus === 'PENDING') {
            btnConfirm.style.display = 'block';
            btnConfirm.innerText = 'XÁC NHẬN ĐƠN (ĐƯA VÀO BẾP)';
            btnConfirm.className = 'btn btn-primary btn-lg fw-bold w-100';
            btnComplete.style.display = 'none';
        } else if (order.orderStatus === 'CONFIRMED') {
            btnConfirm.style.display = 'block';
            btnConfirm.innerText = 'GIAO HÀNG / GỌI LẤY ĐỒ';
            btnConfirm.className = 'btn btn-info text-dark btn-lg fw-bold w-100';
            btnComplete.style.display = 'none';
        } else if (order.orderStatus === 'DELIVERING') {
            btnConfirm.style.display = 'none';
            btnComplete.style.display = 'block';
        }
    } catch (error) {
        console.error("Lỗi tải chi tiết đơn hàng:", error);
    }
}

// --- 9. XỬ LÝ CẬP NHẬT TRẠNG THÁI ĐƠN HÀNG QUA API OAUTH2/JWT ---
async function updateStatus(newStatus) {
    if (!currentSelectedOrderId) return;

    const response = await fetch(`/api/staff/orders/${currentSelectedOrderId}/status`, {
        method: 'PUT',
        headers: getAuthHeaders(),
        body: JSON.stringify({ status: newStatus })
    });

    if (response.ok) {
        switchToPOSMode();
    } else {
        alert("Thao tác thất bại. Phiên đăng nhập có thể đã hết hạn.");
    }
}

// Điều hướng phân phối nút bấm hành động ở cột bên phải
document.getElementById('btnConfirm').onclick = () => {
    const currentStatus = document.getElementById('viewStatus').innerText;
    if (currentStatus === 'PENDING') updateStatus('CONFIRMED');
    else if (currentStatus === 'CONFIRMED') updateStatus('DELIVERING');
};
document.getElementById('btnComplete').onclick = () => updateStatus('COMPLETED');
document.getElementById('btnCancel').onclick = () => {
    if (confirm('Bạn có chắc chắn muốn hủy đơn này?')) updateStatus('CANCELLED');
};

// --- 10. ĐỒNG BỘ GIAO DIỆN REAL-TIME TỪ TÍN HIỆU WEBSOCKET BROKER ---
function updateOrderUI(order) {
    const gridInner = document.getElementById('order-grid-inner');
    let existingCard = document.getElementById(`order-card-${order.id}`);

    if (order.orderStatus === 'COMPLETED' || order.orderStatus === 'CANCELLED') {
        if (existingCard) existingCard.closest('.col-4').remove();
        updateCount();
        return;
    }

    let statusClass = 'status-pending';
    if (order.orderStatus === 'CONFIRMED') statusClass = 'status-confirmed';
    if (order.orderStatus === 'DELIVERING') statusClass = 'status-delivering';

    const cardHtml = `
        <div class="col-4 mb-2 animate__animated animate__fadeIn">
            <div class="card order-card h-100 ${statusClass}"
                 id="order-card-${order.id}" onclick="showOrderDetail(${order.id})">
                <div class="card-body text-center">
                    <div class="order-code mb-1">#${order.orderCode}</div>
                    <span class="badge ${getBadgeColor(order.orderStatus)} p-1">${order.orderStatus}</span>
                </div>
            </div>
        </div>`;

    if (existingCard) {
        existingCard.closest('.col-4').outerHTML = cardHtml;
        if (currentSelectedOrderId === order.id) {
            showOrderDetail(order.id);
        }
    } else {
        gridInner.insertAdjacentHTML('afterbegin', cardHtml);
        const audio = new Audio('https://assets.mixkit.co/active_storage/sfx/2869/2869-500.wav');
        audio.play().catch(e => {});
    }
    updateCount();
}

function getBadgeColor(status) {
    if(status === 'PENDING') return 'bg-warning text-dark';
    if(status === 'CONFIRMED') return 'bg-info text-dark';
    if(status === 'DELIVERING') return 'bg-primary';
    return 'bg-secondary';
}

function updateCount() {
    document.getElementById('order-count').innerText = document.querySelectorAll('.order-card').length;
}

// --- 11. KHỞI CHẠY KIỂM TRA QUYỀN HẠN KHI MỞ TRANG ---
document.addEventListener('DOMContentLoaded', () => {
    const currentRole = localStorage.getItem('role');

    if (currentRole !== 'STAFF' && currentRole !== 'ADMIN') {
        alert("Bạn không có quyền truy cập vào khu vực dành cho nhân viên!");
        window.location.href = '/auth';
        return;
    }

    // Kích hoạt chuỗi tính năng liên đới
    connect();
    switchToPOSMode();
    loadProductsToMenu(); // Nạp sản phẩm thật từ MySQL ra khung POS trung tâm
    initializeSearchFilters(); // Kích hoạt bộ lắng nghe ô tìm kiếm
});