document.addEventListener("DOMContentLoaded", function () {
    const productGrid = document.getElementById('product-grid');

    async function executeFilter() {
        const selectedCategories = Array.from(document.querySelectorAll('.category-filter:checked')).map(cb => cb.getAttribute('data-id'));
        const selectedSizes = Array.from(document.querySelectorAll('.size-filter:checked')).map(cb => cb.value);
        const minPrice = document.getElementById('minPrice').value;
        const maxPrice = document.getElementById('maxPrice').value;
        const keyword = document.querySelector('.search-bar input').value;

        let params = new URLSearchParams();
        if (selectedCategories.length > 0) {
            params.append('categoryIds', selectedCategories.join(','));
        }
        if (selectedSizes.length > 0) {
            params.append('sizes', selectedSizes.join(','));
        }
        if (minPrice) params.append('minPrice', minPrice);
        if (maxPrice) params.append('maxPrice', maxPrice);
        if (keyword) params.append('keyword', keyword);

        try {
            const res = await fetch(`/api/products/filter?${params.toString()}`);

            if (res.ok) {
                const products = await res.json();
                renderMenu(products);
            } else {
                console.error("Lỗi tải thực đơn: ", res.status);
            }
        } catch (err) {
            console.error("Lỗi khi kết nối đến API lọc: ", err);
        }
    }

    function renderMenu(products) {
        if (products.length === 0) {
            productGrid.innerHTML = '<div class="col-12 text-center mt-5"><h5 class="text-muted"><i class="fa-solid fa-mug-hot me-2"></i>Không tìm thấy đồ uống nào phù hợp!</h5></div>';
            return;
        }

        productGrid.innerHTML = products.map(p => `
            <div class="col animate-fade-up">
                <div class="product-card card">
                    <div class="product-img-wrapper">
                        <!-- p.imageUrl từ DTO -->
                        <img src="${p.imageUrl}" alt="${p.name}">
                    </div>
                    <div class="card-body d-flex flex-column">
                        <!-- p.name từ DTO -->
                        <a href="#" class="product-title text-truncate" title="${p.name}"><i class="fa-solid fa-fire-flame-curved me-2" style="color: #e25822; font-size: 0.9rem;"></i>${p.name}</a>

                        <!-- p.averageRating và p.totalReviews từ DTO -->
                        <div class="rating-stars mb-2">
                            <span style="color: #ffc107;"><i class="fa-solid fa-star"></i> ${p.averageRating}</span>
                            <span class="text-muted ms-1" style="font-size: 0.8rem;">(${p.totalReviews})</span>
                        </div>

                        <!-- p.startingPrice từ DTO (Định dạng tiền Việt) -->
                        <p class="product-price mb-3"><i class="fa-solid fa-tag me-2" style="font-size: 0.9rem; color: var(--coffee-light);"></i>${p.startingPrice.toLocaleString('vi-VN')} đ</p>

                        <!-- Nút gọi hàm thêm vào giỏ hàng -->
                        <button class="btn btn-outline-theme w-100 mt-auto" onclick="openProductPopup(${p.productId})">
                            <i class="fa-solid fa-cart-plus me-2"></i>Thêm vào giỏ
                        </button>
                    </div>
                </div>
            </div>
        `).join('');
    }

    const inputs = [
        document.getElementById('minPrice'),
        document.getElementById('maxPrice'),
        document.getElementById('searchInput')
    ];

    inputs.forEach(input => {
        if(input) input.addEventListener('input', debounce(executeFilter, 400));
    })

    document.querySelectorAll('.size-filter').forEach(checkbox => {
        checkbox.addEventListener('change', executeFilter);
    });

    document.querySelectorAll('.category-filter').forEach(radio => {
        radio.addEventListener('change', executeFilter);
    });

    function debounce(func, wait) {
        let timeout;
        return function(...args) {
            clearTimeout(timeout);
            timeout = setTimeout(() => func.apply(this, args), wait);
        };
    }

    const clearCatBtn = document.getElementById('clearCategory');
    if (clearCatBtn) {
        clearCatBtn.addEventListener('click', function(e) {
            e.preventDefault();
            document.querySelectorAll('.category-filter').forEach(radio => radio.checked = false);
            executeFilter();
        });
    }

    const btnPlus = document.getElementById('btnPlus');
    if (btnPlus) {
        btnPlus.addEventListener('click', () => {
            selectedQuantity++;
            document.getElementById('popupQuantity').value = selectedQuantity;
        });
    }

    const btnMinus = document.getElementById('btnMinus');
    if (btnMinus) {
        btnMinus.addEventListener('click', () => {
            if (selectedQuantity > 1) {
                selectedQuantity--;
                document.getElementById('popupQuantity').value = selectedQuantity;
            }
        });
    }

    const btnConfirm = document.getElementById('btnConfirmAddToCart');
    if (btnConfirm) {
        btnConfirm.addEventListener('click', async () => {

            const token = localStorage.getItem("token");

            if (!token || token === "null") {
                alert("Vui lòng đăng nhập để thực hiện mua hàng!");
                window.location.href = "/auth";
                return;
            }

            const selectedSizeRadio = document.querySelector('.popup-size-radio:checked');

            if (!selectedSizeRadio) {
                alert("Vui lòng chọn một kích cỡ size!");
                return;
            }

            const variantId = parseInt(selectedSizeRadio.value);

            const optionIds = Array.from(
                document.querySelectorAll('.popup-option-checkbox:checked')
            ).map(cb => parseInt(cb.value));

            const payload = {
                variantId: variantId,
                quantity: selectedQuantity,
                optionIds: optionIds
            };

            try {

                const response = await fetch('/api/cart/add', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + token
                    },
                    body: JSON.stringify(payload)
                });

                if (response.ok) {

                    alert("🎉 Tuyệt vời! Đã thêm món ăn vào giỏ hàng thành công.");

                    const modalEl = document.getElementById('productModal');

                    const modalInstance =
                        bootstrap.Modal.getInstance(modalEl);

                    if (modalInstance) {
                        modalInstance.hide();
                    }

                } else {
                    alert("Có lỗi xảy ra khi thêm vào giỏ!");
                }

            } catch (error) {
                console.error("Lỗi gửi giỏ hàng:", error);
            }
        });
    }

    executeFilter();
});

async function addToCart(variantId) {
    const token = localStorage.getItem("token");

    if(!token || token === "null" || token.trim() === "") {
        alert("Vui lòng đăng nhập để thực hiện chức năng thêm vào giỏ hàng!");
        window.location.href = "/auth";
        return;
    }

    const cartPayload = {
        variantId: variantId,
        quantity: 1,
        optionIds: []
    };

    try {
        const response = await fetch('/api/cart/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify(cartPayload)
        });

        if (response.ok) {
            alert("Thêm vào giỏ hàng thành công!");
        }
    } catch (error) {
        console.error("Lỗi thêm nhanh vào giỏ:", error);
    }
}

let currentProductDetails = null;
let selectedQuantity = 1;

async function openProductPopup(productId) {
    selectedQuantity = 1;
    document.getElementById('popupQuantity').value = selectedQuantity;

    try {
        const res = await fetch(`/api/products/${productId}/details`);
        if (!res.ok) return alert("Không thể tải thông tin sản phẩm!");

        currentProductDetails = await res.json();

        document.getElementById('popupProductName').innerText = currentProductDetails.name;
        document.getElementById('popupProductImg').src = currentProductDetails.imageUrl;
        document.getElementById('popupProductPrice').innerText = currentProductDetails.basePrice.toLocaleString('vi-VN') + " đ";

        const sizeContainer = document.getElementById('popupSizeContainer');
        sizeContainer.innerHTML = currentProductDetails.productVariants.map((v, index) => `
            <input type="radio" class="btn-check popup-size-radio" name="popupSize" id="popupSize_${v.id}" value="${v.id}" ${index === 0 ? 'checked' : ''}>
            <label class="btn btn-outline-dark" for="popupSize_${v.id}">${v.sizeName} (+${v.extraPrice.toLocaleString('vi-VN')}đ)</label>
        `).join('');

        const optionContainer = document.getElementById('popupOptionContainer');
        if (currentProductDetails.productOptions && currentProductDetails.productOptions.length > 0) {
            optionContainer.innerHTML = currentProductDetails.productOptions.map(o => `
                <div class="form-check mb-2">
                    <input class="form-check-input popup-option-checkbox" type="checkbox" value="${o.id}" id="popupOpt_${o.id}">
                    <label class="form-check-label d-flex justify-content-between" for="popupOpt_${o.id}">
                        <span>${o.optionName}</span>
                        <span class="text-muted">+${o.additionalPrice.toLocaleString('vi-VN')} đ</span>
                    </label>
                </div>
            `).join('');
        } else {
            optionContainer.innerHTML = '<p class="text-muted" style="font-size: 0.85rem;">Món này không có topping đi kèm.</p>';
        }

        const modalEl = document.getElementById('productModal');

        modalEl.addEventListener('hidden.bs.modal', () => {
            document.body.classList.remove('modal-open');
            document.querySelectorAll('.modal-backdrop')
                .forEach(e => e.remove());
        });

        const myModal = new bootstrap.Modal(modalEl);
        myModal.show();
    } catch (err) {
        console.error("Lỗi mở pop-up: ", err);
    }
}
