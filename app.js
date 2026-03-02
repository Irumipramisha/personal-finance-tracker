const descriptionInput = document.getElementById('description');
const amountInput = document.getElementById('amount');
const typeInput = document.getElementById('type');
const addBtn = document.getElementById('add-btn');
const transactionList = document.getElementById('transaction-list');

const totalIncomeDisplay = document.getElementById('total-income');
const totalExpenseDisplay = document.getElementById('total-expense');
const balanceDisplay = document.getElementById('balance');

let transactions = [];

// Load when page loads
window.onload = function () {
    loadTransactions();
};

// Auto comma formatting
amountInput.addEventListener('input', function (e) {
    let value = e.target.value.replace(/,/g, '');
    if (!isNaN(value) && value !== '') {
        e.target.value = Number(value).toLocaleString('en-US');
    } else {
        e.target.value = '';
    }
});

// Add transaction
addBtn.addEventListener('click', function () {

    const description = descriptionInput.value.trim();
    const amount = parseFloat(amountInput.value.replace(/,/g, ''));
    const type = typeInput.value;

    if (description === '' || isNaN(amount) || amount <= 0) {
        alert("Enter valid details!");
        return;
    }

    fetch("http://localhost:8080/add", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `description=${description}&amount=${amount}&type=${type}`
    })
    .then(res => res.text())
    .then(data => {
        descriptionInput.value = '';
        amountInput.value = '';
        loadTransactions();
    })
    .catch(err => console.log(err));
});

// Load from database
function loadTransactions() {

    fetch("http://localhost:8080/transactions")
    .then(response => response.json())
    .then(data => {
        transactions = data;
        displayTransactions();
    })
    .catch(error => {
        console.log("Error loading:", error);
    });
}

// Show in table
function displayTransactions() {

    transactionList.innerHTML = "";

    let totalIncome = 0;
    let totalExpense = 0;

    transactions.forEach(t => {

        const row = document.createElement("tr");

        const formattedAmount = Number(t.amount).toLocaleString("en-US");

        row.innerHTML = `
            <td>${t.description}</td>
            <td class="${t.type === 'income' ? 'type-income' : 'type-expense'}">
                ${t.type === 'income' ? '+Rs.' : '-Rs.'} ${formattedAmount}
            </td>
            <td>${t.type.toUpperCase()}</td>
            <td>
                <button class="delete-btn" onclick="deleteTransaction(${t.id})">
                    Delete
                </button>
            </td>
        `;

        transactionList.appendChild(row);

        if (t.type === "income") totalIncome += t.amount;
        else totalExpense += t.amount;
    });

    const balance = totalIncome - totalExpense;

    totalIncomeDisplay.innerText = "Rs. " + totalIncome.toLocaleString("en-US");
    totalExpenseDisplay.innerText = "Rs. " + totalExpense.toLocaleString("en-US");
    balanceDisplay.innerText = "Rs. " + balance.toLocaleString("en-US");
}

// Delete
function deleteTransaction(id) {

    fetch(`http://localhost:8080/delete?id=${id}`)
    .then(res => res.text())
    .then(data => {
        loadTransactions();
    })
    .catch(err => console.log(err));
}