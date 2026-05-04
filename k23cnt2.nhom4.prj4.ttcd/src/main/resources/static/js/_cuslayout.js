document.addEventListener("DOMContentLoaded", function () {
    const functionDiv = document.getElementById('function-div');
    const appMain = document.querySelector('.app-main'); // Cột cuộn bên phải
    const appNav = document.querySelector('.app-nav');

    if (!functionDiv || !appNav || !appMain) return;

    appNav.addEventListener('click', async function (e) {
        const link = e.target.closest('.cus-link');
        if (!link || link.classList.contains('active')) return;

        e.preventDefault();
        const targetUrl = link.getAttribute('href');

        try {
            const res = await fetch(targetUrl);
            const html = await res.text();
            const doc = new DOMParser().parseFromString(html, 'text/html');
            const newContent = doc.getElementById('function-div').innerHTML;

            // Thay đổi nội dung bên phải
            functionDiv.innerHTML = newContent;
            window.history.pushState({}, '', targetUrl);

            // Cập nhật trạng thái menu
            document.querySelectorAll('.cus-link').forEach(l => {
                l.classList.toggle('active', l.getAttribute('href') === targetUrl);
            });

            // CHỐT HẠ: Cuộn khung bên phải về đầu (không liên quan gì đến Navbar nữa)
            appMain.scrollTo({ top: 0, behavior: 'smooth' });

        } catch (err) {
            window.location.href = targetUrl;
        }
    });

    window.addEventListener('popstate', () => window.location.reload());
});