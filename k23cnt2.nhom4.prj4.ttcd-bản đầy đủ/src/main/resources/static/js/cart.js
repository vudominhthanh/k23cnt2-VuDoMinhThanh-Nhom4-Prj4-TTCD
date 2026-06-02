(function() {
    let currentCartItems = [];
    let totalCartPrice = 0;
    let checkoutModalInstance;
    let mockPaymentModalInstance;
    let activeTransactionId = '';

    function formatVND(amount) {
        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
    }

    async function loadSidebarProfile() {
                try {
                    const checkAuth = window.checkAuthAndGetProfile || (typeof checkAuthAndGetProfile === 'function' ? checkAuthAndGetProfile : null);
                    if (checkAuth) {
                        const user = await checkAuth();
                        if (user) {
                            const titleEl = document.getElementById('db-user-title');
                            if (titleEl) titleEl.innerText = user.fullName || user.username || 'Thành viên';
                        }
                    }
                } catch (e) {
                    console.warn("Lỗi load profile sidebar (không ảnh hưởng giỏ hàng):", e);
                }
            }

    async function loadCartData() {
        const wrapper = document.getElementById('cart-items-wrapper');
        const btnOpenCheckout = document.getElementById('btn-open-checkout');

        try {
            const response = await window.fetchWithToken('/api/cart');
            if (!response.ok) throw new Error("Lỗi xác thực hệ thống: " + response.status);

            currentCartItems = await response.json();

            if (!currentCartItems || currentCartItems.length === 0) {
                wrapper.innerHTML = `
                    <div class="text-center py-5 bg-white rounded-4 shadow-sm border p-4 w-100">
                        <i class="fa-solid fa-basket-shopping fs-1 text-muted opacity-50 mb-3"></i>
                        <p class="text-muted mb-0 fw-bold">Giỏ hàng của bạn trống rỗng.</p>
                    </div>`;
                document.getElementById('summary-subtotal').innerText = '0 đ';
                document.getElementById('summary-total').innerText = '0 đ';
                if(btnOpenCheckout) btnOpenCheckout.disabled = true;
                return;
            }

            totalCartPrice = 0;
            let htmlContent = '';

            currentCartItems.forEach(item => {
                let singleItemPrice = item.basePrice + (item.extraPrice || 0);
                let optionsDetail = `Size: ${item.variantName || 'Mặc định'}`;

                if (item.options && item.options.length > 0) {
                    item.options.forEach(opt => {
                        singleItemPrice += (opt.price || 0);
                        optionsDetail += ` / ${opt.name}`;
                    });
                }

                let itemTotalPrice = singleItemPrice * item.quantity;
                totalCartPrice += itemTotalPrice;

                let imgSource = item.imageUrl ? `/images/${item.imageUrl}` : 'https://images.unsplash.com/photo-1541167760496-1628856ab772?q=80&w=100';

                htmlContent += `
                    <div class="card border-0 shadow-sm rounded-4 p-3 bg-white w-100 mb-3 transition-hover">
                        <div class="d-flex align-items-start gap-3">
                            <img src="${imgSource}" class="rounded-3 shadow-sm border" style="width: 65px; height: 65px; object-fit: cover;">
                            <div class="flex-grow-1">
                                <h6 class="fw-bold mb-1 text-dark" style="font-size: 0.95rem;">${item.productName}</h6>
                                <div class="text-muted" style="font-size: 0.75rem;"><i class="fa-solid fa-mug-hot me-1 text-success"></i>${optionsDetail}</div>
                            </div>
                        </div>
                        <hr class="my-2 border-secondary opacity-10">
                        <div class="d-flex justify-content-between align-items-center px-1">
                            <span class="badge bg-light text-dark border px-2 py-1" style="font-size: 0.75rem;">Số lượng: ${item.quantity}</span>
                            <span class="fw-bold text-danger" style="font-size: 0.95rem;">${formatVND(itemTotalPrice)}</span>
                        </div>
                    </div>
                `;
            });

            wrapper.innerHTML = htmlContent;
            document.getElementById('summary-subtotal').innerText = formatVND(totalCartPrice);
            document.getElementById('summary-total').innerText = formatVND(totalCartPrice);
            if(btnOpenCheckout) btnOpenCheckout.disabled = false;

        } catch (err) {
            wrapper.innerHTML = `<div class="alert alert-danger w-100 rounded-4 shadow-sm border-0">Lỗi kết nối giỏ hàng: ${err.message}</div>`;
        }
    }

    function renderItemsToModal() {
        const modalContainer = document.getElementById('modal-invoice-items');
        let modalHtml = '';

        currentCartItems.forEach(item => {
            let singleItemPrice = item.basePrice + (item.extraPrice || 0);
            let optionsDetail = `Size: ${item.variantName || 'Mặc định'}`;
            if (item.options && item.options.length > 0) {
                item.options.forEach(opt => {
                    singleItemPrice += (opt.price || 0);
                    optionsDetail += ` / ${opt.name}`;
                });
            }
            modalHtml += `
                <div class="d-flex justify-content-between align-items-start border-bottom py-2">
                    <div style="flex: 1; padding-right: 15px;">
                        <span class="fw-bold text-dark small">${item.productName}</span>
                        <span class="badge bg-secondary text-white ms-1" style="font-size:10px;">SL: ${item.quantity}</span>
                        <div class="text-muted" style="font-size: 0.72rem; margin-top: 2px;">${optionsDetail}</div>
                    </div>
                    <span class="fw-bold text-dark small text-end">${formatVND(singleItemPrice * item.quantity)}</span>
                </div>
            `;
        });
        modalContainer.innerHTML = modalHtml;
    }

    async function initCartView() {
        loadSidebarProfile();
        await loadCartData();

        const checkoutModalEl = document.getElementById('checkoutModal');
        if (checkoutModalEl) {
            if (checkoutModalEl.parentNode !== document.body) {
                document.body.appendChild(checkoutModalEl);
            }
            checkoutModalInstance = new bootstrap.Modal(checkoutModalEl);
        }

        const mockPaymentModalEl = document.getElementById('mockPaymentModal');
        if (mockPaymentModalEl) {
            if (mockPaymentModalEl.parentNode !== document.body) {
                document.body.appendChild(mockPaymentModalEl);
            }
            mockPaymentModalInstance = new bootstrap.Modal(mockPaymentModalEl);
        }

        if(checkoutModalEl) checkoutModalInstance = new bootstrap.Modal(checkoutModalEl);
        if(mockPaymentModalEl) mockPaymentModalInstance = new bootstrap.Modal(mockPaymentModalEl);

        const btnOpenCheckout = document.getElementById('btn-open-checkout');
        if(btnOpenCheckout) {
            btnOpenCheckout.addEventListener('click', () => {
                renderItemsToModal();
                document.getElementById('modal-total-amount').innerText = formatVND(totalCartPrice);
                checkoutModalInstance.show();
            });
        }

        // Xử lý Submit Form đặt hàng
        const formCheckout = document.getElementById('form-checkout');
        if(formCheckout) {
            formCheckout.addEventListener('submit', async (e) => {
                e.preventDefault();

                const submitBtn = formCheckout.querySelector('button[type="submit"]');
                const oldBtnText = submitBtn.innerHTML;
                submitBtn.innerHTML = `<span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span> Đang xử lý...`;
                submitBtn.disabled = true;

                const payload = {
                    shippingAddress: document.getElementById('checkout-address').value,
                    paymentMethod: document.getElementById('checkout-payment-method').value
                };

                try {
                    const response = await window.fetchWithToken('/api/order/checkout', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(payload)
                    });

                    if (!response.ok) {
                        alert("Đặt hàng không thành công!");
                        submitBtn.innerHTML = oldBtnText;
                        submitBtn.disabled = false;
                        return;
                    }

                    const orderResult = await response.json();
                    activeTransactionId = orderResult.transactionId;

                    checkoutModalInstance.hide();

                    checkoutModalEl.addEventListener('hidden.bs.modal', function handler() {
                        document.getElementById('mock-payment-amount').innerText = formatVND(totalCartPrice);
                        mockPaymentModalInstance.show();
                        checkoutModalEl.removeEventListener('hidden.bs.modal', handler);
                    });

                } catch (error) {
                    alert("Lỗi đồng bộ hóa đơn.");
                    submitBtn.innerHTML = oldBtnText;
                    submitBtn.disabled = false;
                }
            });
        }

        // Xử lý Callback hoàn tất thanh toán
        const btnSuccess = document.getElementById('btn-mock-success');
        if(btnSuccess) btnSuccess.addEventListener('click', () => executePaymentCallback(activeTransactionId, true));

        const btnFail = document.getElementById('btn-mock-fail');
        if(btnFail) btnFail.addEventListener('click', () => executePaymentCallback(activeTransactionId, false));
    }

    async function executePaymentCallback(transactionId, isSuccess) {
        try {
            const res = await window.fetchWithToken(`/api/payments/mock-callback?transactionId=${transactionId}&isSuccess=${isSuccess}`, {
                method: 'POST'
            });
            if (res.ok) {
                mockPaymentModalInstance.hide();
                cleanupModalArtifacts();
                setTimeout(() => {
                    window.location.href = '/customer/orders';
                }, 300);
            }
        } catch (err) {
            alert("Lỗi kết nối cổng thanh toán.");
        }
    }

    function cleanupModalArtifacts() {
        document.querySelectorAll('.modal-backdrop').forEach(el => el.remove());
        document.body.classList.remove('modal-open');
        document.body.style = '';
    }

    // Chạy thẳng hàm vì JS thường nằm cuối body
    initCartView();
})();