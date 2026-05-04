document.addEventListener("DOMContentLoaded", function() {

    // --- LỌC TABS ---
    const filterButtons = document.querySelectorAll('#orderStatusTabs .nav-link');
    const orderCards = document.querySelectorAll('.order-item-card');

    if(filterButtons.length > 0) {
        filterButtons.forEach(button => {
            button.addEventListener('click', function(e) {
                e.preventDefault();
                filterButtons.forEach(btn => btn.classList.remove('active'));
                this.classList.add('active');

                const filterValue = this.getAttribute('data-filter');

                orderCards.forEach(card => {
                    const cardStatus = card.getAttribute('data-status');
                    if(filterValue === 'ALL' || cardStatus === filterValue) {
                        card.style.display = 'block';
                        card.style.opacity = '0';
                        setTimeout(() => card.style.opacity = '1', 50);
                    } else {
                        card.style.display = 'none';
                    }
                });
            });
        });
    }

    // --- WEBSOCKET ---
    let stompClient = null;

    function connectWebSocket() {
        if (typeof SockJS === 'undefined' || typeof Stomp === 'undefined') return;

        const socket = new SockJS('/ws-tea-coffee');
        stompClient = Stomp.over(socket);
        stompClient.debug = null;

        stompClient.connect({}, function (frame) {
            stompClient.subscribe('/topic/orders', function (message) {
                const orderData = JSON.parse(message.body);
                updateOrderStatusOnUI(orderData.orderId, orderData.newStatus);
            });
        });
    }

    function updateOrderStatusOnUI(orderId, newStatus) {
        orderCards.forEach(card => {
            const cardOrderId = card.querySelector('.ticket-top h5').innerText.replace('#', '');

            if(cardOrderId === orderId) {
                card.setAttribute('data-status', newStatus);

                const statusFrame = card.querySelector('.ticket-bottom .status-frame');
                statusFrame.className = 'status-frame mb-3';

                if(newStatus === 'COMPLETED') {
                    statusFrame.classList.add('status-completed');
                    statusFrame.innerHTML = '<i class="fas fa-check-circle me-1"></i> Hoàn thành';
                } else if (newStatus === 'DELIVERING') {
                    statusFrame.classList.add('status-delivering');
                    statusFrame.innerHTML = '<i class="fas fa-motorcycle me-1"></i> Đang giao';
                } else if (newStatus === 'CANCELLED') {
                    statusFrame.classList.add('status-cancelled');
                    statusFrame.innerHTML = '<i class="fas fa-times-circle me-1"></i> Đã hủy';
                }

                card.style.transform = 'scale(1.03)';
                setTimeout(() => card.style.transform = '', 300);
            }
        });
    }

    connectWebSocket();
});