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
    const productListContainer = document.getElementById('product-list')

    productListContainer.innerHTML = '';

    if(products.length === 0) {
        productListContainer.innerHTML = '<p class="text-muted">Hiện tại quán chưa có sản phẩm nào.</p>'
        return;
    }

    products.forEach(product => {
        const formattedPrice = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(product.startingPrice);

        const productCardHtml = `
            <div class="col-12 col-md-6 col-lg-3">
                            <div class="card h-100 shadow-sm product-card" onclick="goToDetail(${product.productId})">
                                <img src="${product.imageUrl}" class="card-img-top" alt="${product.name}" style="height: 200px; object-fit: cover;">
                                <div class="card-body d-flex flex-column">
                                    <h5 class="card-title">${product.name}</h5>
                                    <p class="mb-1 rating-text">
                                        ⭐ ${product.averageRating} <span class="text-muted fs-6">(${product.totalReviews} đánh giá)</span>
                                    </p>
                                    <p class="card-text price-text mt-auto mb-0">Từ: ${formattedPrice}</p>
                                </div>
                            </div>
                        </div>
        `;

        productListContainer.innerHTML += productCardHtml;
    })
}

function goToDetail(productId) {
    window.location.href = `/product-detail.html?id=${productId}`
}
