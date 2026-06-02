const modal = document.getElementById("userModal");

function openAddModal() {

    document.getElementById("modalTitle").innerText =
        "Thêm người dùng";

    document.getElementById("userId").value = "";

    document.getElementById("fullName").value = "";
    document.getElementById("email").value = "";
    document.getElementById("phone").value = "";
    document.getElementById("password").value = "";
    document.getElementById("role").value = "CUSTOMER";
    document.getElementById("isActive").value = "true";

    modal.style.display = "flex";
}

function openEditModal(btn) {

    document.getElementById("modalTitle").innerText =
        "Cập nhật người dùng";

    document.getElementById("userId").value =
        btn.dataset.id;

    document.getElementById("fullName").value =
        btn.dataset.fullname;

    document.getElementById("email").value =
        btn.dataset.email;

    document.getElementById("phone").value =
        btn.dataset.phone;

    document.getElementById("role").value =
        btn.dataset.role;

    document.getElementById("isActive").value =
        btn.dataset.active;

    modal.style.display = "flex";
}

function closeModal() {

    modal.style.display = "none";
}

async function saveUser() {

    const id =
        document.getElementById("userId").value;

    const user = {

        fullName:
        document.getElementById("fullName").value,

        email:
        document.getElementById("email").value,

        phone:
        document.getElementById("phone").value,

        password:
        document.getElementById("password").value,

        role:
        document.getElementById("role").value,

        isActive:
            document.getElementById("isActive").value === "true"
    };

    let url = "/api/admin/users";

    let method = "POST";

    if (id) {

        url += "/" + id;

        method = "PUT";
    }

    const response = await fetch(url, {

        method: method,

        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify(user)
    });

    if (response.ok) {

        alert("Lưu thành công!");

        location.reload();

    } else {

        alert("Có lỗi xảy ra!");
    }
}

async function deleteUser(id) {

    if (!confirm("Bạn có chắc muốn xóa user này?")) {
        return;
    }

    const response = await fetch(
        "/api/admin/users/" + id,
        {
            method: "DELETE"
        }
    );

    if (response.ok) {

        alert("Xóa thành công!");

        location.reload();

    } else {

        alert("Xóa thất bại!");
    }
}