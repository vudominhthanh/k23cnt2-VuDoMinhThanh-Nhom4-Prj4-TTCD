loadHomeProducts();

function loadHomeProducts() {
    fetch('/api/products/home')
        .then(response => {
            if(!response.ok) {
                throw new Error('Loi tai du lieu tu sever');
            }

            if(response.status === 204) {
                return [];
            }
            return response.json();
        })
        .then(products =>{
            renderProducts(products);
        })
        .catch(error => {
            console.error('Err', error)
            document.getElementById('product-list').innerHTML =
                '<div class="alert alert-danger">Không thể tải danh sách sản phẩm lúc này.</div>';
        })
}

function renderProducts(products) {
    const productListContainer = document.getElementById('product-list');

    productListContainer.innerHTML = '';

    if(products.length === 0) {
        productListContainer.innerHTML = '<p class="text-muted text-center w-100">Hiện tại quán chưa có sản phẩm nào.</p>';
        return;
    }

    products.forEach(product => {
        const formattedPrice = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(product.startingPrice);

        const productCardHtml = `
            <div class="col-12 col-md-6 col-lg-3">
                <a href="/product-detail?id=${product.productId}" class="card h-100 product-card cus-link text-decoration-none">

                    <div class="product-img-wrapper">
                        <span class="badge-new">Mới</span>

                        <img src="${product.imageUrl}" class="card-img-top" alt="${product.name}" style="width: 100%; height: 100%; object-fit: cover; border-radius: 15px 15px 0 0;">
                    </div>

                    <div class="card-body d-flex flex-column mt-2 text-center" style="color: inherit;">
                        <h5 class="card-title fw-bold" style="color: var(--coffee-dark);">${product.name}</h5>
                        <p class="mb-2 rating-text text-warning small">
                            ⭐ ${product.averageRating} <span class="text-muted fs-6" style="color: #6c757d !important;">(${product.totalReviews} đánh giá)</span>
                        </p>
                        <p class="card-text price-text mt-auto mb-0 fw-bold" style="color: var(--tea-green); font-size: 1.15rem;">
                            ${formattedPrice}
                        </p>
                    </div>
                </a>
            </div>
        `;

        productListContainer.innerHTML += productCardHtml;
    });
}

function goToDetail(productId) {
    window.location.assign("/product-detail?id=" + productId + "&t=" + new Date().getTime());
}
