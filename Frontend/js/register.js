document.getElementById('registerForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    console.log(username,password);
    const response = await fetch('http://localhost:8088/api/register', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ username, password })
    });
    console.log(response);
    if (response.ok) {
        console.log('response ok');
        alert('Registration successful');
        window.location.href = 'login.html';
    } else {
        console.log('response not ok');
        alert('Registration failed');
    }
});
