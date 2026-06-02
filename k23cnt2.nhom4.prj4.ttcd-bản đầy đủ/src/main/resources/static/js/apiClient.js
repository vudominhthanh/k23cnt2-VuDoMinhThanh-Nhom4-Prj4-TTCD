async function fetchWithToken(url, options = {}) {
    const token = localStorage.getItem('token');

    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };

    if (token && token !== "null" && token.trim() !== "") {
        headers['Authorization'] = 'Bearer ' + token;
    }

    try {
        const response = await fetch(url, { ...options, headers });

        if (response.status === 401) {
            alert("🔒 Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!");
            localStorage.removeItem('token');
            window.location.href = '/auth';
            return null;
        }
        return response;
    } catch (error) {
        console.error("Lỗi kết nối mạng:", error);
        return null;
    }
}

async function checkAuthAndGetProfile() {
    const response = await fetchWithToken('/api/customer/profile');
    if (!response || !response.ok) return null;
    return await response.json();
}

window.fetchWithToken = fetchWithToken;
window.checkAuthAndGetProfile = checkAuthAndGetProfile;