document.getElementById('loginForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    let response ;
    if(username==='test@sunbasedata.com' && password==='Test@123') {
        console.log("user is test");
        response = await fetch('http://localhost:7080/api/proxy-auth', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ username, password })
    });

    }else{
        response = await fetch('http://localhost:7080/api/auth', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });  
    }
    console.log(response);
    if (response.ok) {
        const data = await response.json();
        console.log('Authentication successful:', data);
        localStorage.setItem('token', data.token);
        window.location.href = 'customers.html';
    } else {
        alert('Login failed');
    }
});