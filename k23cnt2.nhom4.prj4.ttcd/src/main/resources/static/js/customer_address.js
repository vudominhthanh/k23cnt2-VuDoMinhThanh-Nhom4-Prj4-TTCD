// Dùng Object Pattern để đóng gói các hàm liên quan đến Address, tránh conflict toàn cục
const AddressApp = {
    modalInstance: null,

    init: function() {
        const modalEl = document.getElementById('addressModal');
        if(modalEl) {
            this.modalInstance = new bootstrap.Modal(modalEl);
        }
    },

    openModal: function(mode, addressId = null) {
        const title = document.getElementById('addressModalTitle');
        const form = document.getElementById('addressForm');

        if (mode === 'ADD') {
            title.innerText = 'Thêm Địa Chỉ Mới';
            form.reset(); // Xóa trắng form
        } else if (mode === 'EDIT') {
            title.innerText = 'Cập Nhật Địa Chỉ';
            // Logic call API lấy data cũ dựa vào addressId và đổ vào form
            // Demo:
            console.log("Đang lấy data của địa chỉ ID:", addressId);
        }

        if(this.modalInstance) this.modalInstance.show();
    },

    deleteAddress: function(addressId) {
        if(confirm("Bạn có chắc chắn muốn xóa địa chỉ này? Hành động này không thể hoàn tác.")) {
            // Call API DELETE
            alert("Đã xóa địa chỉ thành công!");
            // Reload div hoặc dòng đó
        }
    }
};

// Khởi tạo khi file JS được load
AddressApp.init();