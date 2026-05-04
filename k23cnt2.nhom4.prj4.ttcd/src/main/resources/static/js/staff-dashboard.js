let stompClient = null;
let currentSelectedOrderId = null;

// Khởi tạo kết nối WebSocket
function connect() {
    const socket = new SockJS('/ws'); // Endpoint trong Spring Boot
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        // Lắng nghe thông báo đơn hàng mới/cập nhật
        stompClient.subscribe('/topic/orders', function (message) {
            const orderUpdate = JSON.parse(message.body);
            updateOrderUI(orderUpdate);
        });
    });
}

// Cập nhật giao diện khi có thay đổi từ WebSocket
function updateOrderUI(order) {
    const gridInner = document.getElementById('order-grid-inner');
    let existingCard = document.getElementById(`order-card-${order.id}`);

    // Nếu đơn đã hoàn thành hoặc hủy, xóa khỏi danh sách bên trái
    if (order.orderStatus === 'COMPLETED' || order.orderStatus === 'CANCELLED') {
        if (existingCard) existingCard.closest('.col-4').remove();
        updateCount();
        return;
    }

    const cardHtml = `
        <div class="col-4 mb-2">
            <div class="card order-card h-100 status-${order.orderStatus.toLowerCase()}"
                 id="order-card-${order.id}" onclick="showOrderDetail(${order.id})">
                <div class="card-body text-center">
                    <div class="order-code mb-1">#${order.orderCode}</div>
                    <span class="badge bg-secondary p-1">${order.orderStatus}</span>
                </div>
            </div>
        </div>`;

    if (existingCard) {
        existingCard.closest('.col-4').outerHTML = cardHtml;
    } else {
        gridInner.insertAdjacentHTML('afterbegin', cardHtml);
    }
    updateCount();
}

// Hiển thị Popup chi tiết
async function showOrderDetail(orderId) {
    currentSelectedOrderId = orderId;
    const modal = new bootstrap.Modal(document.getElementById('orderDetailModal'));

    // Giả lập gọi API lấy chi tiết order_items
    const response = await fetch(`/api/staff/orders/${orderId}`);
    const order = await response.json();

    document.getElementById('modalOrderCode').innerText = `Chi tiết đơn #${order.orderCode}`;
    document.getElementById('modalStatus').innerText = order.orderStatus;
    document.getElementById('modalTotal').innerText = `${order.finalAmount.toLocaleString()}đ`;

    // Render danh sách món
    const itemsHtml = order.items.map(item => `
        <div class="d-flex justify-content-between border-bottom py-2">
            <div>
                <strong>${item.productName}</strong> <small class="text-muted">x${item.quantity}</small>
                <div class="text-muted small">${item.variantName || ''}</div>
            </div>
            <span>${item.priceAtBuy.toLocaleString()}đ</span>
        </div>
    `).join('');

    document.getElementById('modalOrderItems').innerHTML = itemsHtml;

    // Hiển thị/Ẩn nút dựa trên trạng thái hiện tại
    document.getElementById('btnConfirm').style.display = order.orderStatus === 'PENDING' ? 'block' : 'none';
    document.getElementById('btnComplete').style.display = order.orderStatus === 'CONFIRMED' ? 'block' : 'none';

    modal.show();
}

// Xử lý cập nhật trạng thái đơn hàng
async function updateStatus(newStatus) {
    if (!currentSelectedOrderId) return;

    // Gửi yêu cầu cập nhật lên Server
    const response = await fetch(`/api/staff/orders/${currentSelectedOrderId}/status`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status: newStatus })
    });

    if (response.ok) {
        bootstrap.Modal.getInstance(document.getElementById('orderDetailModal')).hide();
        // UI sẽ tự cập nhật thông qua WebSocket khi Server push lại
    }
}

function updateCount() {
    const count = document.querySelectorAll('.order-card').length;
    document.getElementById('order-count').innerText = count;
}

// Gán sự kiện cho các nút trong Modal
document.getElementById('btnConfirm').onclick = () => updateStatus('CONFIRMED');
document.getElementById('btnComplete').onclick = () => updateStatus('COMPLETED');
document.getElementById('btnCancel').onclick = () => {
    if(confirm('Bạn có chắc chắn muốn hủy đơn này?')) updateStatus('CANCELLED');
};

// Khởi chạy
document.addEventListener('DOMContentLoaded', () => {
    connect();
    updateCount();
});