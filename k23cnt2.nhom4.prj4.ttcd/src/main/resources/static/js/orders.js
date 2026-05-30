(function() {
    let stompClient = null;
    let globalOrders = [];
    let currentViewingOrderCode = null;
    let orderDetailModalInstance = null;

    async function initOrdersView() {
        const user = await window.checkAuthAndGetProfile();
        if(!user) return;

        document.querySelectorAll('.user-placeholder-name').forEach(el => el.innerText = user.fullName || user.username);

        const jwtToken = localStorage.getItem('token');
        await fetchAndRenderOrders(jwtToken);
        connectWebSocket(user.id, jwtToken);

        const modalEl = document.getElementById('orderDetailModal');
        if(modalEl) {
            if(modalEl.parentNode !== document.body) {
                document.body.appendChild(modalEl);
            }
            if (!orderDetailModalInstance) {
                orderDetailModalInstance = new bootstrap.Modal(modalEl);
            }
        }
    }

    async function fetchAndRenderOrders(token) {
        try {
            const response = await fetch('/api/order/my-orders', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                globalOrders = await response.json();
                renderOrders(globalOrders);
            } else {
                document.getElementById('orders-container').innerHTML = '<div class="text-center text-danger">Không thể tải đơn hàng. Vui lòng thử lại sau.</div>';
            }
        } catch (error) {
            console.error("Lỗi fetch đơn hàng:", error);
        }
    }

    function renderOrders(orders) {
        const container = document.getElementById('orders-container');

        if (!orders || orders.length === 0) {
            container.innerHTML = '<div class="text-center text-muted py-4">Bạn chưa có đơn hàng nào.</div>';
            return;
        }

        let html = '';
        orders.forEach(order => {
            let statusText = getStatusDisplayName(order.orderStatus);
            let badgeClass = getBadgeClassForStatus(order.orderStatus);

            // Gom tên các món lại thành chuỗi ngắn gọn
            let itemsText = order.items && order.items.length > 0
                ? order.items.map(i => `${i.quantity}x ${i.productName}`).join(', ')
                : 'Đang tải thông tin món...';

            html += `
                <div class="border-bottom pb-3 order-item">
                    <div class="d-flex justify-content-between align-items-center flex-wrap mb-2">
                        <span class="text-muted small">Mã đơn: <strong class="text-dark">#${order.orderCode}</strong></span>
                        <span id="status-${order.id}" class="badge ${badgeClass} text-dark rounded-pill transition-all" style="font-size:0.75rem;">
                            ${statusText}
                        </span>
                    </div>
                    <p class="mb-2 small text-dark fw-medium text-truncate" style="max-width: 90%;">${itemsText}</p>
                    <div class="d-flex justify-content-between align-items-center pt-1">
                        <span class="fw-bold text-danger">${formatCurrency(order.finalAmount)}</span>
                        <button class="btn btn-sm btn-outline-secondary" style="font-size:0.8rem;" onclick="openOrderDetailModal('${order.orderCode}')">
                            Xem chi tiết
                        </button>
                    </div>
                </div>
            `;
        });

        container.innerHTML = html;
    }

    function openOrderDetailModal(orderCode) {
        const order = globalOrders.find(o => o.orderCode === orderCode);
        if (!order) return;

        currentViewingOrderCode = orderCode;

        document.getElementById('modalOrderCode').innerText = `#${order.orderCode}`;
        document.getElementById('modalFinalAmount').innerText = formatCurrency(order.finalAmount);

        const itemsContainer = document.getElementById('modalOrderItems');
        if (order.items && order.items.length > 0) {
            itemsContainer.innerHTML = order.items.map(item => `
                <div class="d-flex justify-content-between align-items-center border-bottom pb-2">
                    <div>
                        <p class="mb-0 fw-medium">${item.productName}</p>
                        <small class="text-muted">Size/Đá/Đường: ${item.variantName || 'Mặc định'}</small>
                    </div>
                    <div class="text-end">
                        <p class="mb-0 fw-bold">${formatCurrency(item.priceAtBuy)}</p>
                        <small class="text-muted">x${item.quantity}</small>
                    </div>
                </div>
            `).join('');
        } else {
            itemsContainer.innerHTML = '<p class="text-muted small">Chưa có thông tin món.</p>';
        }

        const btnCancel = document.getElementById('btnCancelOrder');
        if (order.orderStatus === 'PENDING') {
            btnCancel.classList.remove('d-none');
        } else {
            btnCancel.classList.add('d-none');
        }

        if(orderDetailModalInstance) orderDetailModalInstance.show();
    }

    // --- HÀM HỦY ĐƠN (Đã được khôi phục) ---
    async function cancelCurrentOrder() {
        if (!currentViewingOrderCode) return;

        const isConfirm = confirm("Bạn có chắc chắn muốn hủy đơn hàng này không?");
        if (!isConfirm) return;

        try {
            const token = localStorage.getItem('token');
            const response = await fetch(`/api/order/${currentViewingOrderCode}/status?newStatus=CANCELLED`, {
                method: 'PUT',
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.ok) {
                alert("Hủy đơn hàng thành công!");
                if(orderDetailModalInstance) orderDetailModalInstance.hide();
                fetchAndRenderOrders(token);
            } else {
                alert("Không thể hủy đơn hàng lúc này. Vui lòng thử lại!");
            }
        } catch (error) {
            console.error("Lỗi khi hủy đơn:", error);
            alert("Lỗi hệ thống khi hủy đơn hàng.");
        }
    }

    // WEBSOCKET: Cập nhật UI Realtime
    function connectWebSocket(userId, token) {
        const socket = new SockJS('/ws-order');
        stompClient = Stomp.over(socket);
        stompClient.debug = null;

        stompClient.connect({ 'Authorization': 'Bearer ' + token }, function(frame) {
            stompClient.subscribe('/topic/orders/' + userId, function(response) {
                const updateData = JSON.parse(response.body);

                fetchAndRenderOrders(token);
            });
        });
    }

    function getStatusDisplayName(statusEnum) {
                switch(statusEnum) {
                    case 'PENDING': return 'Chờ xác nhận';
                    case 'CONFIRMED': return 'Đã xác nhận';
                    case 'DELIVERING': return 'Đang giao hàng';
                    case 'COMPLETED': return 'Hoàn thành';
                    case 'CANCELLED': return 'Đã hủy';
                    default: return statusEnum;
                }
            }

    function getBadgeClassForStatus(statusEnum) {
        switch(statusEnum) {
            case 'PENDING': return 'bg-warning';
            case 'CONFIRMED': return 'bg-info';
            case 'DELIVERING': return 'bg-primary';
            case 'COMPLETED': return 'bg-success';
            case 'CANCELLED': return 'bg-danger';
            default: return 'bg-secondary';
        }
    }

    function formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount || 0);
    }

    window.openOrderDetailModal = openOrderDetailModal;
    window.cancelCurrentOrder = cancelCurrentOrder;

    initOrdersView();
})();

