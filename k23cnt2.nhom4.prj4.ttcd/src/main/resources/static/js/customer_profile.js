// Scope riêng cho trang Profile bằng cách dùng IIFE (Immediately Invoked Function Expression)
// để tránh trùng lặp biến với các file JS khác.
(function() {
    const btnVerify = document.getElementById('btnVerifyOldPwd');
    const btnSubmit = document.getElementById('btnSubmitNewPwd');
    const step1 = document.getElementById('pwd-step-1');
    const step2 = document.getElementById('pwd-step-2');
    const oldPwdInput = document.getElementById('oldPassword');

    // Xử lý Bước 1
    if(btnVerify) {
        btnVerify.addEventListener('click', function() {
            // Logic call API kiểm tra mật khẩu ở đây
            if(oldPwdInput.value === "123") { // Demo pass
                oldPwdInput.classList.remove('is-invalid');
                step1.classList.add('d-none');
                step2.classList.remove('d-none');
            } else {
                oldPwdInput.classList.add('is-invalid');
            }
        });
    }

    // Xử lý Bước 2
    if(btnSubmit) {
        btnSubmit.addEventListener('click', function() {
            const newPwd = document.getElementById('newPassword');
            const confirmPwd = document.getElementById('confirmPassword');

            if(newPwd.value !== confirmPwd.value || newPwd.value === "") {
                confirmPwd.classList.add('is-invalid');
            } else {
                confirmPwd.classList.remove('is-invalid');
                alert("Cập nhật mật khẩu thành công!");

                // Tắt Modal và Reset Form
                const modal = bootstrap.Modal.getInstance(document.getElementById('passwordModal'));
                modal.hide();
                resetPasswordModal();
            }
        });
    }

    // Reset modal khi người dùng bấm nút X tắt đi
    const pwdModalEl = document.getElementById('passwordModal');
    if(pwdModalEl) {
        pwdModalEl.addEventListener('hidden.bs.modal', resetPasswordModal);
    }

    function resetPasswordModal() {
        step1.classList.remove('d-none');
        step2.classList.add('d-none');
        document.getElementById('oldPassword').value = '';
        document.getElementById('newPassword').value = '';
        document.getElementById('confirmPassword').value = '';
        document.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
    }
})();