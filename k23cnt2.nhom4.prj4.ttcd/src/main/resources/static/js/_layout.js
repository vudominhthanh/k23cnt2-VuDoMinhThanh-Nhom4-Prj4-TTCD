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
            const res = await fetch(targetUrl);
            const html = await res.text();

            const doc = new DOMParser().parseFromString(html, 'text/html');
            const newDiv = doc.getElementById('function-div');

            if (!newDiv) throw new Error("Không có function-div");

            functionDiv.innerHTML = newDiv.innerHTML;

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

    // 🔥 NAVBAR SCROLL
    window.addEventListener("scroll", () => {
        const navbar = document.querySelector(".custom-navbar");
        if (!navbar) return;

        if (window.scrollY > 50) {
            navbar.classList.add("scrolled");
        } else {
            navbar.classList.remove("scrolled");
        }
    });
});