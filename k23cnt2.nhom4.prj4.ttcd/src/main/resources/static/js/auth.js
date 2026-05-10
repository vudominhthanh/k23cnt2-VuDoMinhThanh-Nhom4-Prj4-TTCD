const signUpButton = document.getElementById('signUp');
const signInButton = document.getElementById('signIn');
const container = document.getElementById('container');
const msgBox = document.getElementById('alert-msg');

signUpButton.addEventListener('click', () => {
    container.classList.add("right-panel-active");
    msgBox.innerHTML = '';
});

signInButton.addEventListener('click', () => {
    container.classList.remove("right-panel-active");
    msgBox.innerHTML = '';
});

const API_BASE_URL = "http://localhost:8080/api/auth";

document.getElementById('registerForm').addEventListener('submit', async function(event) {
    event.preventDefault();

    const fullName = document.getElementById('fullname').value;
    const email = document.getElementById('email-re').value;
    const phone = document.getElementById('phone').value;
    const password = document.getElementById('password-re').value;

//    if(!email || !password || !fullName || !phone) {
//        msgBox.innerHTML = "<span style='color: red;'>Vui lòng điền đủ thông tin!</span>";
//        return;
//    }

    try {
        const response = await fetch(`${API_BASE_URL}/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json'},
            body: JSON.stringify({ fullName, email, phone, password })
        });

        const data = await response.json();

        if(response.ok) {

            alert(data.message);

            document.getElementById('registerForm').reset();

            setTimeout(() => {
                container.classList.remove("right-panel-active");
                msgBox.innerHTML = '';
            }, 1000);

        } else {
            alert(data.message);
        }

    } catch (error) {
        console.error("Err : ", error);
    }
});


document.getElementById('loginForm').addEventListener('submit', async function(event) {
    event.preventDefault();

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch(`${API_BASE_URL}/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json'},
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();

        if(response.ok) {

            localStorage.setItem("token", data.token);

            alert(data.message);

            window.location.href = '/';

        } else {
            alert(data.message || "Dang nhap that bai")
        }
    } catch (error) {
        console.error("Lỗi thực sự là: ", error);
    }
});