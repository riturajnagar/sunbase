document.addEventListener('DOMContentLoaded', async function() {
    const token = localStorage.getItem('token');
    console.log(token);
    const response = await fetch('http://localhost:7080/customers/get', {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (response.ok) {
        const data = await response.json();
        const table = document.getElementById('customerTable').getElementsByTagName('tbody')[0];
        data.content.forEach(customer => {
            const row = table.insertRow();
            row.insertCell(0).innerText = customer.id;
            row.insertCell(1).innerText = customer.firstName;
            row.insertCell(2).innerText = customer.lastName;
            row.insertCell(3).innerText = customer.street;
            row.insertCell(4).innerText = customer.address;
            row.insertCell(5).innerText = customer.city;
            row.insertCell(6).innerText = customer.state;
            row.insertCell(7).innerText = customer.email;
            row.insertCell(8).innerText = customer.phone;
            const actionsCell = row.insertCell(9);
            const editButton = document.createElement('button');
            editButton.innerText = 'Edit';
            editButton.onclick = () => editCustomer(customer.id);
            actionsCell.appendChild(editButton);
            const deleteButton = document.createElement('button');
            deleteButton.innerText = 'Delete';
            deleteButton.onclick = () => deleteCustomer(customer.id);
            actionsCell.appendChild(deleteButton);
        });
    } else {
        alert('Failed to load customers');
    }

    // if (!token) {   
    //     console.error('No token found. Please log in first.');
    //     return;
    // }

    // try {
    //     const response = await fetch('http://localhost:7080/api/verify-sync', {
    //         method: 'GET',
    //         headers: {
    //             'Authorization': 'Bearer ' + token
    //         }
    //     });

    //     if (response.ok) {
    //         // User is allowed to access sync functionality
    //         document.getElementById('syncButton').disabled = false;
    //     } else {
    //         // User is not allowed to access sync functionality
    //         document.getElementById('syncButton').disabled = true;
    //     }
    // } catch (error) {
    //     console.error('Error:', error);
    // }

    //fetchCustomers(); // Fetch and display customers when the page loads
});

async function syncCustomers() {
    const token = localStorage.getItem('jwtToken');

    if (!token) {
        console.error('No token found. Please log in first.');
        return;
    }

    try {
        const response = await fetch('http://localhost:7080/api/sync', {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        });

        if (response.ok) {
            console.log('Customer list synchronized successfully');
            fetchCustomers(); // Refresh the customer list
        } else {
            console.error('Failed to synchronize customers');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

async function fetchCustomers() {
    const token = localStorage.getItem('jwtToken');
   

    try {
        const response = await fetch('http://localhost:7080/api/customers/get', {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        });

        if (response.ok) {
            const customers = await response.json();
            const customerTable = document.getElementById('customerTable').getElementsByTagName('tbody')[0];
            customerTable.innerHTML = ''; // Clear existing table content

            // Populate the table with fetched customers
            customers.forEach(customer => {
                const row = customerTable.insertRow();
                row.insertCell(0).innerText = customer.firstName;
                row.insertCell(1).innerText = customer.lastName;
                row.insertCell(2).innerText = customer.street;
                row.insertCell(3).innerText = customer.address;
                row.insertCell(4).innerText = customer.city;
                row.insertCell(5).innerText = customer.state;
                row.insertCell(6).innerText = customer.email;
                row.insertCell(7).innerText = customer.phone;
                const actionsCell = row.insertCell(9);
                const editButton = document.createElement('button');
                editButton.innerText = 'Edit';
                editButton.onclick = () => editCustomer(customer.id);
                actionsCell.appendChild(editButton);
                const deleteButton = document.createElement('button');
                deleteButton.innerText = 'Delete';
                deleteButton.onclick = () => deleteCustomer(customer.id);
                actionsCell.appendChild(deleteButton);
            });
        } else {
            console.error('Failed to fetch customers');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

// async function syncCustomers() {
//     const token = localStorage.getItem('token');
//     const response = await fetch('https://qa.sunbasedata.com/sunbase/portal/api/assignment_sync.jsp', {
//         method: 'POST',
//         headers: {
//             'Authorization': `Bearer ${token}`
//         }
//     });

//     if (response.ok) {
//         location.reload();
//     } else {
//         alert('Sync failed');
//     }
// }

function editCustomer(id) {
    // Redirect to edit form
    window.location.href = `edit_customer.html?id=${id}`;
}

async function deleteCustomer(id) {
    const token = localStorage.getItem('token');
    const response = await fetch(`http://localhost:7080/customers/${id}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (response.ok) {
        location.reload();
    } else {
        alert('Failed to delete customer');
    }
}