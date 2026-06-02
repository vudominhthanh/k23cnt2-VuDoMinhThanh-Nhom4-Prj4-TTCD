function toggleAdminMenu() {
    const menu = document.getElementById("adminDropdownMenu");
    if (menu) menu.classList.toggle("show");
}

function toggleProductMenu() {
    const submenu = document.getElementById("productSubmenu");
    if (submenu) {
        submenu.classList.toggle("show");
    }
}

function handleLogout() {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    window.location.href = "/auth";
}

window.onclick = function (e) {
    if (!e.target.closest(".admin-dropdown")) {
        const adminMenu = document.getElementById("adminDropdownMenu");
        if (adminMenu) adminMenu.classList.remove("show");
    }
};

async function showOrderDetail(orderId) {
    try {
        const response = await fetch(`/api/admin/orders/${orderId}`);
        const order = await response.json();

        const container = document.getElementById("orderItemsContainer");
        if (container) {
            container.innerHTML = `
                <p><b>Mã đơn:</b> ${order.orderCode}</p>
                <p><b>Khách hàng:</b> ${order.user.fullName}</p>
                <p><b>Địa chỉ:</b> ${order.shippingAddress}</p>
                <p><b>Tổng tiền:</b> ${Number(order.finalAmount).toLocaleString()} đ</p>
                <p><b>Trạng thái:</b> ${order.orderStatus}</p>
            `;
        }

        const modal = document.getElementById("orderDetailModal");
        if (modal) modal.style.display = "flex";

    } catch (error) {
        console.error(error);
        alert("Không lấy được chi tiết đơn hàng");
    }
}

function closeOrderDetailModal() {
    const modal = document.getElementById("orderDetailModal");
    if (modal) modal.style.display = "none";
}