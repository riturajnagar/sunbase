document.getElementById('registerForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    console.log(username,password);
    const response = await fetch('http://localhost:7080/api/register', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ username, password })
    });
    console.log(response);
    if (response.ok) {
        alert('Registration successful');
        window.location.href = 'index.html';
    } else {
        alert('Registration failed');
    }
});