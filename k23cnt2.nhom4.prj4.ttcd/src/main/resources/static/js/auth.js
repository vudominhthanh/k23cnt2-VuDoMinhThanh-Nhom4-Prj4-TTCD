const signUpButton = document.getElementById('signUp');
const signInButton = document.getElementById('signIn');
const container = document.getElementById('container');

// Khi click nút "Đăng ký ngay" ở khung Overlay
signUpButton.addEventListener('click', () => {
    container.classList.add("right-panel-active");
});

// Khi click nút "Đăng nhập" ở khung Overlay
signInButton.addEventListener('click', () => {
    container.classList.remove("right-panel-active");
});