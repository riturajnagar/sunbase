document.getElementById('addCustomerForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    const customer = {
        firstName: document.getElementById('firstName').value,
        lastName: document.getElementById('lastName').value,
        street: document.getElementById('street').value,
        address: document.getElementById('address').value,
        city: document.getElementById('city').value,
        state: document.getElementById('state').value,
        email: document.getElementById('email').value,
        phone: document.getElementById('phone').value
    };

    const token = localStorage.getItem('token');
    const response = await fetch('http://localhost:7080/customers', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(customer)
    });
    console.log("response: ", response);
    if (response.ok) {
        window.location.href = 'customers.html';
    } else {
        alert('Failed to add customer');
    }
});