document.addEventListener("DOMContentLoaded", function() {
    // Chức năng 1: Đóng/Mở Sidebar
    const toggleBtn = document.getElementById("toggle-btn");
    const sidebar = document.getElementById("sidebar");

    toggleBtn.addEventListener("click", function() {
        sidebar.classList.toggle("collapsed");
    });

    // Chức năng 2: Hiệu ứng active cho menu
    const menuItems = document.querySelectorAll(".sidebar-menu li a");
    menuItems.forEach(item => {
        item.addEventListener("click", function() {
            // Xóa class active ở tất cả các thẻ
            menuItems.forEach(i => i.classList.remove("active"));
            // Thêm class active vào thẻ vừa click
            this.classList.add("active");
        });
    });
});

// Chức năng 3: Xử lý Đăng xuất
function handleLogout() {
    if(confirm("Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?")) {
        // Chuyển hướng về trang đăng nhập hoặc gọi API logout của Spring Security
        window.location.href = "/auth";
    }
}