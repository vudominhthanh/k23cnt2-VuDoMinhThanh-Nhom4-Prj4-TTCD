let customerStompClient = null;

function connectCustomerWS(token) {
    const socket = new SockJS('/ws-order');
    customerStompClient = Stomp.over(socket);
    customerStompClient.debug = null;

    customerStompClient.connect({'Authorization': 'Bearer ' + token}, function (frame) {

        customerStompClient.subscribe('/topic/orders', function (message) {

            const badge = document.getElementById('order-badge');
            if(badge) badge.style.display = 'block';
        });
    }, function (error) {
        console.log("Mất kết nối WebSocket, thử lại sau 5s...");
        setTimeout(() => connectCustomerWS(token), 5000);
    });
}

function hideOrderBadge() {
    const badge = document.getElementById('order-badge');
    if(badge) badge.style.display = 'none';
}

function updateNavbarStatus() {
    const token = localStorage.getItem("token");
    const navGuest = document.getElementById("nav-isnot");
    const navUser = document.getElementById("nav-is");
    const navOrders = document.getElementById("nav-orders");
    const navCart = document.getElementById("nav-cart");

    if (token !== null && token !== "") {
        if (navGuest) navGuest.style.display = "none";
        if (navUser) navUser.style.display = "block";
        if (navOrders) navOrders.style.display = "block";
        if (navCart) navCart.style.display = "block";
        try{
           const userNameSpan = document.querySelector("#nav-is span");

           const base64Url = token.split('.')[1];
           const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
           const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
               return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
           }).join(''));

           const decodedToken = JSON.parse(jsonPayload);

           if (userNameSpan && decodedToken.sub) {
                userNameSpan.innerText = decodedToken.sub;
           }
        } catch (error) {
           console.error("Lỗi giải token !", error);
        }
    } else {
        if (navGuest) navGuest.style.display = "block";
        if (navUser) navUser.style.display = "none";
        if (navOrders) navOrders.style.display = "none";
        if (navCart) navCart.style.display = "none";
    }
}

document.addEventListener("DOMContentLoaded", function () {

    const functionDiv = document.getElementById('function-div');

    function getLinks() {
        return document.querySelectorAll('.cus-link');
    }

    function setActiveLink(activeUrl) {
        const cleanPath = activeUrl.replace(/\/$/, "");

        getLinks().forEach(link => {
            link.classList.remove('active');

            const href = link.getAttribute('href')?.replace(/\/$/, "");
            if (!href) return;

            if (cleanPath === href) {
                link.classList.add('active');
            }
        });
    }

    setActiveLink(window.location.pathname);

    document.addEventListener('click', async function (e) {
        const link = e.target.closest('.cus-link');
        if (!link) return;

        e.preventDefault();

        const targetUrl = link.getAttribute('href');
        if (!targetUrl) return;

        setActiveLink(targetUrl);
        functionDiv.classList.add('is-loading');

        try {

            const token = localStorage.getItem("token");

            const fetchOptions = {
                headers: {}
            };

            if (token !== null && token !== "") {
                fetchOptions.headers['Authorization'] = 'Bearer ' + token;
            }

            const res = await fetch(targetUrl, fetchOptions);

            const html = await res.text();

            const doc = new DOMParser().parseFromString(html, 'text/html');
            const newDiv = doc.getElementById('function-div');

            if (!newDiv) throw new Error("Không có function-div");

            document.querySelectorAll('.modal-backdrop')
                .forEach(el => el.remove());

            document.body.classList.remove('modal-open');
            document.body.style = '';

            functionDiv.innerHTML = newDiv.innerHTML;
            const scripts = functionDiv.querySelectorAll("script");

            scripts.forEach(oldScript => {

                const newScript = document.createElement("script");

                if (oldScript.src) {
                    newScript.src = oldScript.src;
                } else {
                    newScript.textContent = oldScript.textContent;
                }

                document.body.appendChild(newScript);

                oldScript.remove();
            });

            functionDiv.classList.remove('animate-fade-up');
            void functionDiv.offsetWidth;
            functionDiv.classList.add('animate-fade-up');

            window.history.pushState({}, '', targetUrl);

            setActiveLink(targetUrl);

            window.scrollTo({ top: 0, behavior: 'smooth' });

        } catch (err) {
            console.error(err);
            functionDiv.innerHTML = `<div class="alert alert-danger">Lỗi tải dữ liệu</div>`;
        } finally {
            functionDiv.classList.remove('is-loading');
        }
    });

    window.addEventListener('popstate', () => {
        window.location.reload();
    });

    window.addEventListener("scroll", () => {
        const navbar = document.querySelector(".custom-navbar");
        if (!navbar) return;

        if (window.scrollY > 50) {
            navbar.classList.add("scrolled");
        } else {
            navbar.classList.remove("scrolled");
        }
    });

//    const token = localStorage.getItem("token");
//
//    const navGuest = document.getElementById("nav-isnot");
//    const navUser = document.getElementById("nav-is");
//    const navOrders = document.getElementById("nav-orders");
//    const navCart = document.getElementById("nav-cart");
//
//    if (token !== null && token !== "") {
//        navGuest.style.display = "none";
//        navUser.style.display = "block";
//
//        if (navOrders) navOrders.style.display = "block";
//                connectCustomerWS(token);
//
//        if (navCart) navCart.style.display = "block";
//
//        try{
//            const userNameSpan = document.querySelector("#nav-is span");
//
//            const base64Url = token.split('.')[1];
//            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
//            const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
//                return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
//            }).join(''));
//
//            const decodedToken = JSON.parse(jsonPayload);
//
//            if (userNameSpan && decodedToken.sub) {
//                userNameSpan.innerText = decodedToken.sub;
//            }
//        } catch (error) {
//            console.error("Lỗi giải token !", error);
//        }
//
//    } else {
//        navGuest.style.display = "block";
//        navUser.style.display = "none";
//
//        if (navOrders) navOrders.style.display = "none";
//        if (navCart) navCart.style.display = "none";
//    }

      updateNavbarStatus();
});

function handleLogout() {
     localStorage.removeItem("token");
     localStorage.removeItem("role");
     window.location.href = "/";
}

