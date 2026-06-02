function openAddModal() {
    document.getElementById("modalTitle").innerText = "Thêm sản phẩm";
    document.getElementById("productId").value = "";
    document.getElementById("name").value = "";
    document.getElementById("slug").value = "";
    document.getElementById("price").value = "";
    document.getElementById("description").value = "";
    document.getElementById("categoryId").value = "";
    document.getElementById("imageFile").value = "";

    // Bỏ tích tất cả checkbox kích cỡ khi mở modal thêm mới
    document.querySelectorAll("input[name='variantIds']").forEach(cb => cb.checked = false);

    document.getElementById("productModal").style.display = "flex";
}

function closeModal() {
    document.getElementById("productModal").style.display = "none";
}

function openEditModal(button) {
    document.getElementById("modalTitle").innerText = "Sửa sản phẩm";
    document.getElementById("productId").value = button.dataset.id;
    document.getElementById("name").value = button.dataset.name;
    document.getElementById("slug").value = button.dataset.slug;
    document.getElementById("price").value = button.dataset.price;
    document.getElementById("description").value = button.dataset.description;
    document.getElementById("categoryId").value = button.dataset.category;
    document.getElementById("productModal").style.display = "flex";
}

async function saveProduct() {
    const id = document.getElementById("productId").value;
    const formData = new FormData();

    formData.append("name", document.getElementById("name").value);
    formData.append("slug", document.getElementById("slug").value);
    formData.append("basePrice", document.getElementById("price").value);
    formData.append("description", document.getElementById("description").value);
    formData.append("categoryId", document.getElementById("categoryId").value);

    // 🌟 QUÉT CHECKBOX: Gom tất cả ID kích cỡ được tích chọn
    const selectedVariants = [];
    document.querySelectorAll("input[name='variantIds']:checked").forEach((checkbox) => {
        selectedVariants.push(checkbox.value);
    });

    formData.append("selectedVariantIds", selectedVariants.join(","));

    const imageFile = document.getElementById("imageFile").files[0];
    if (imageFile) {
        formData.append("imageFile", imageFile);
    }

    let url = "/api/admin/products";
    let method = "POST";

    if (id) {
        url += "/" + id;
        method = "PUT";
    }

    try {
        const response = await fetch(url, {
            method: method,
            body: formData
        });

        if (response.ok) {
            alert("Lưu sản phẩm và đồng bộ kích cỡ thành công!");
            location.reload();
        } else {
            const errText = await response.text();
            alert("Có lỗi xảy ra khi lưu sản phẩm: " + errText);
        }
    } catch (error) {
        console.error("Lỗi kết nối:", error);
    }
}


async function toggleStatus(id, currentStatus) {
    const actionText = currentStatus ? "tạm ẩn (dừng bán)" : "kích hoạt lại (mở bán)";
    const confirmAction = confirm(`Bạn có chắc muốn ${actionText} sản phẩm này không?`);
    if (!confirmAction) return;

    try {
        console.log(`===> ĐANG GỬI YÊU CẦU ĐẢO TRẠNG THÁI CHO ID: ${id}`);


        const response = await fetch("/api/admin/products/toggle-status/" + id, {
            method: "POST"
        });

        if (response.ok) {
            alert(`Đã ${actionText} sản phẩm thành công!`);
            location.reload();
        } else {
            const errorText = await response.text();
            alert("Thao tác thất bại! Chi tiết lỗi: " + errorText);
        }
    } catch (error) {
        console.error("Lỗi hệ thống:", error);
        alert("Không thể kết nối đến máy chủ!");
    }
}