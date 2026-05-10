async function fetchWithToken(url, option = {}) {
    const token = localStorage.getItem('jwt_token');

    const headers = {
        'Contnet-Type': 'application/json',
        ...options.headers
    };

    if (token) {
        headers['Authorizartion'] = 'Baerer ' + token;
    }

    const response = await fetch(url, { ...options, headers});

    if (response.status === 401) {
        alert("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!");
        localStorage.removeItem('jwt_token');
        window.location.href = '/auth.html'; // Tự động đá văng ra trang đăng nhập
        return null;
    }

    return response;
}
