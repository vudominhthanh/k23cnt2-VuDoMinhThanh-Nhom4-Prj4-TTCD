(function() {
            const qtyInput = document.getElementById('quantity');
            const basePriceInput = document.getElementById('basePrice');
            const priceDisplay = document.getElementById('totalPriceDisplay');
            const modifiers = document.querySelectorAll('.price-modifier');
            const form = document.getElementById('addToCartForm');
            const btnMinus = document.getElementById('btnMinus');
            const btnPlus = document.getElementById('btnPlus');

            // Hàm tính tiền
            const calculatePrice = () => {
                if(!basePriceInput) return;
                const basePrice = parseFloat(basePriceInput.value);
                let extraPrice = 0;

                document.querySelectorAll('.price-modifier:checked').forEach(el => {
                    extraPrice += parseFloat(el.getAttribute('data-price')) || 0;
                });

                const quantity = parseInt(qtyInput.value) || 1;
                const total = (basePrice + extraPrice) * quantity;
                priceDisplay.innerHTML = `<span>${total.toLocaleString('vi-VN')}</span> VNĐ`;
            };

            // Sự kiện tăng giảm số lượng
            if (btnMinus && btnPlus && qtyInput) {
                btnMinus.addEventListener('click', () => {
                    let qty = parseInt(qtyInput.value) || 1;
                    if (qty > 1) { qtyInput.value = qty - 1; calculatePrice(); }
                });

                btnPlus.addEventListener('click', () => {
                    let qty = parseInt(qtyInput.value) || 1;
                    qtyInput.value = qty + 1; calculatePrice();
                });

                qtyInput.addEventListener('change', () => {
                    if(qtyInput.value < 1) qtyInput.value = 1;
                    calculatePrice();
                });
            }

            modifiers.forEach(mod => mod.addEventListener('change', calculatePrice));

            calculatePrice();

            if (form) {
                form.addEventListener('submit', function(e) {
                    e.preventDefault();

                    const productId = document.getElementById('productId').value;
                    const quantity = qtyInput.value;
                    const checkedVariant = document.querySelector('input[name="variantId"]:checked');

                    if (!checkedVariant) {
                        alert('Vui lòng chọn Kích cỡ!');
                        return;
                    }

                    const selectedOptions = Array.from(document.querySelectorAll('.option-item-val:checked'))
                                                 .map(item => parseInt(item.value));

                    const payload = {
                        productId: parseInt(productId),
                        variantId: parseInt(checkedVariant.value),
                        quantity: parseInt(quantity),
                        optionIds: selectedOptions
                    };

                    const btnSubmit = document.getElementById('btnSubmit');
                    const originalText = btnSubmit.innerText;
                    btnSubmit.innerText = "Đang xử lý...";
                    btnSubmit.disabled = true;

                    // Lấy token từ localStorage (Kế thừa từ code SPA của bạn)
                    const token = localStorage.getItem("token");
                    const headers = { 'Content-Type': 'application/json' };
                    if (token) headers['Authorization'] = 'Bearer ' + token;

                    fetch('/api/cart/add', {
                        method: 'POST',
                        headers: headers,
                        body: JSON.stringify(payload)
                    })
                    .then(async response => {
                        const text = await response.text();
                        if (response.ok) {
                            alert("✅ " + text);
                        } else if (response.status === 401) {
                            alert("Vui lòng đăng nhập để thêm vào giỏ hàng!");
                            window.location.href = '/auth';
                        } else {
                            alert("❌ Lỗi: " + text);
                        }
                    })
                    .catch(err => {
                        console.error('Error:', err);
                        alert('Lỗi kết nối máy chủ!');
                    })
                    .finally(() => {
                        btnSubmit.innerText = originalText;
                        btnSubmit.disabled = false;
                    });
                });
            }
        })();