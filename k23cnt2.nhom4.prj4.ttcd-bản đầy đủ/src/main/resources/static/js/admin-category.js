function openAddModal() {
    document.getElementById("modalTitle").innerText = "Thêm danh mục";
    document.getElementById("categoryId").value = "";
    document.getElementById("name").value = "";
    document.getElementById("slug").value = "";

    document.getElementById("imageFile").value = "";

    document.getElementById("categoryModal").style.display = "flex";
}

function closeModal() {
    document.getElementById("categoryModal").style.display = "none";
}

function openEditModal(button) {
    document.getElementById("modalTitle").innerText = "Sửa danh mục";
    document.getElementById("categoryId").value = button.dataset.id;
    document.getElementById("name").value = button.dataset.name;
    document.getElementById("slug").value = button.dataset.slug;


    document.getElementById("imageFile").value = "";

    document.getElementById("categoryModal").style.display = "flex";
}

async function saveCategory() {
    const id = document.getElementById("categoryId").value;
    const formData = new FormData();

    formData.append("name", document.getElementById("name").value);
    formData.append("slug", document.getElementById("slug").value);


    const imageFile = document.getElementById("imageFile").files[0];
    if (imageFile) {
        formData.append("imageFile", imageFile);
    }

    let url = "/api/admin/categories";
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
            alert("Lưu danh mục thành công!");
            location.reload();
        } else {
            const errText = await response.text();
            alert("Có lỗi xảy ra khi lưu danh mục: " + errText);
        }
    } catch (error) {
        console.error("Lỗi hệ thống kết nối:", error);
    }
}

async function deleteCategory(id) {
    if (!confirm("Bạn có chắc muốn xóa danh mục này không?")) return;

    try {
        const response = await fetch("/api/admin/categories/" + id, {
            method: "DELETE"
        });

        if (response.ok) {
            alert("Xóa danh mục thành công!");
            location.reload();
        } else {
            alert("Xóa thất bại! Vui lòng kiểm tra ràng buộc dữ liệu món ăn bên trong.");
        }
    } catch (error) {
        console.error("Lỗi hệ thống:", error);
    }
}