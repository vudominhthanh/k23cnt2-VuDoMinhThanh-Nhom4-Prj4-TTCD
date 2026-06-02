function toggleAdminMenu() {

    document
        .getElementById("adminDropdownMenu")
        .classList
        .toggle("show");
}


function handleLogout() {

    localStorage.removeItem("token");
    localStorage.removeItem("user");

    window.location.href = "/auth";
}

function toggleProductMenu() {

    document
        .getElementById("productSubmenu")
        .classList.toggle("show");
}


window.onclick = function (e) {

    if (!e.target.closest(".admin-dropdown")) {

        document
            .getElementById("adminDropdownMenu")
            .classList
            .remove("show");
    }
    async function showOrderDetail(orderId){

        try{

            const response =
                await fetch(`/api/admin/orders/${orderId}`);

            const order =
                await response.json();

            document.getElementById(
                "orderItemsContainer"
            ).innerHTML = `
            <p><b>Mã đơn:</b> ${order.orderCode}</p>
            <p><b>Khách hàng:</b> ${order.user.fullName}</p>
            <p><b>Địa chỉ:</b> ${order.shippingAddress}</p>
            <p><b>Tổng tiền:</b> ${Number(order.finalAmount).toLocaleString()} đ</p>
            <p><b>Trạng thái:</b> ${order.orderStatus}</p>
        `;

            document.getElementById(
                "orderDetailModal"
            ).style.display = "flex";

        }catch(error){

            console.error(error);

            alert("Không lấy được chi tiết đơn hàng");
        }
    }

    function closeOrderDetailModal(){

        document.getElementById(
            "orderDetailModal"
        ).style.display = "none";
    }
}