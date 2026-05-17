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

            functionDiv.innerHTML = newDiv.innerHTML;

            functionDiv.classList.remove('animate-fade-up');
            void functionDiv.offsetWidth;
            functionDiv.classList.add('animate-fade-up');

            window.history.pushState({}, '', targetUrl);

            setActiveLink(targetUrl);

            functionDiv.scrollIntoView({ behavior: 'smooth', block: 'start' });

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

    const token = localStorage.getItem("token");

    const navGuest = document.getElementById("nav-isnot");
    const navUser = document.getElementById("nav-is");

    if (token !== null && token !== "") {
        navGuest.style.display = "none";
        navUser.style.display = "block";

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
        navGuest.style.display = "block";
        navUser.style.display = "none";
    }
});

function handleLogout() {
     localStorage.removeItem("token");
     window.location.href = "/";
}