/* This is the js file for the shopping cart.
 * Updated to load products from cookies and fetch details from the server.
 * 
 * - Brandon Kue (Last edit: 9/14/24)
 * - Updated with cookie-based cart loading and product fetching
 * - Modified to use the correct endpoint for fetching product details
 */

// Divs needed 
let emptyCartDiv = document.getElementById("cartEmpty");
let emptyTotalDiv = document.getElementById("totalEmpty");
let itemCartDiv = document.getElementById("cartItemCard");
let itemTotalDiv = document.getElementById("totalItemCard");

let myCart = [];
let totalCost = 0.00;
let totalAmountOfItems = 0;
let subtotal = 0.00;
let tax = 0.00;
let totalDeposit = 0.00;
const taxRate = 0.075; // 7.5% tax rate

// Function to parse cookies
function getCookieValue(cookieName) {
    const name = cookieName + "=";
    const decodedCookie = decodeURIComponent(document.cookie);
    const cookieArray = decodedCookie.split(';');
    for(let i = 0; i < cookieArray.length; i++) {
        let cookie = cookieArray[i];
        while (cookie.charAt(0) == ' ') {
            cookie = cookie.substring(1);
        }
        if (cookie.indexOf(name) == 0) {
            return cookie.substring(name.length, cookie.length);
        }
    }
    return "";
}

// Function to fetch product details from the server
async function fetchProductById(productId) {
    const jwtToken = localStorage.getItem('jwtToken');

    const response = await fetch(`/product/getById?id=${productId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwtToken}`
        }
    });

    if (!response.ok) {
        throw new Error('Failed to fetch product');
    }

    return await response.json();
}

// Function to load cart from cookies and fetch product details
async function loadCartFromCookies() {
    const cartCookie = getCookieValue('cart');
    if (cartCookie) {
        const cartItems = JSON.parse(cartCookie);
        for (const item of cartItems) {
            const productDetails = await fetchProductById(item.productId);
            if (productDetails) {
                myCart.push({
                    id: productDetails.id,
                    name: productDetails.name,
                    price: productDetails.price,
                    description: productDetails.description,
                    deposit: productDetails.deposit, 
                    amount: item.quantity
                });
            }
        }
    }
    updateCartDisplay();
}

// Function to update the cart display
function updateCartDisplay() {
    if (myCart.length == 0) {
        emptyCartDiv.classList.remove("d-none");
        emptyTotalDiv.classList.remove("d-none");
    } else {
        emptyCartDiv.classList.add("d-none");
        emptyTotalDiv.classList.add("d-none");
        calculateTotalItems();
        myCart.forEach(item => {
            duplicateCartItem(item);
            duplicateTotalItem(item);
        });
        calculateTotalCost();
    }
}

function duplicateCartItem(item) {
    // Get the div
    let originalCartItem = document.getElementById("cartItemCard")
    // Make a clone
    let clonedCartItem = originalCartItem.cloneNode(true);
    // set the image
    let clonedItemImage = clonedCartItem.querySelector("#itemImage");
    clonedItemImage.src = "/picture/items/" + item.name.toLowerCase() + ".jpg"; 
    // Change the name
    clonedCartItem.id = "cloned"+ item.id +"CartItem"
    let clonedItemName = clonedCartItem.querySelector("#itemName");
    clonedItemName.innerHTML = item.name;

    let clonedItemDescription = clonedCartItem.querySelector("#itemDescription");
    clonedItemDescription.innerHTML = item.description;

    let clonedItemAmount = clonedCartItem.querySelector("#itemAmountInput");
    clonedItemAmount.id = "cloned" + item.id + "CartAmount"
    clonedItemAmount.value = item.amount;

    let clonedItemCost = clonedCartItem.querySelector("#itemCost");
    clonedItemCost.id = "cloned" + item.id + "CartCost"
    clonedItemCost.innerHTML = "$"+item.price;
    
    clonedItemAmount.addEventListener("input", function() {
        if (parseInt(this.value)) {
            // Reflect changes onto the otherside
            let cartItemAmountElement = document.querySelector("#cloned" + item.id + "CartAmount");
            let totalItemAmountElement = document.querySelector("#cloned" + item.id + "TotalAmount"); 
            cartItemAmountElement.value = this.value;
            totalItemAmountElement.innerHTML = "x"+this.value;
            // Recalculate Total
            let itemWanted = myCart.find(cartItem => cartItem.id === item.id);
            if (itemWanted) {
                itemWanted.amount = parseInt(this.value);
                totalAmountOfItems = 0;
                totalCost = 0;
                calculateTotalItems();
                calculateTotalCost(); 
            }
        }
    })

     // Handle delete button
     let deleteButton = clonedCartItem.querySelector(".deleteButton");
     deleteButton.addEventListener("click", function() {
         deleteItem(item.id); // Call deleteItem with the item's ID
     });

    // Append
    clonedCartItem.classList.remove("d-none");
    document.getElementById("shoppingCol").appendChild(clonedCartItem);
}

function deleteItem(productId) {
    // Retrieve the cart from cookies
    const cart = JSON.parse(getCookie('cart')) || [];

    // Filter out the product with the specified productId
    const updatedCart = cart.filter(item => item.productId !== productId);

    // Update the cart in cookies
    setCookie('cart', JSON.stringify(updatedCart), 7);

     // Clear the existing display
     myCart = []; // Clear the myCart array
     document.getElementById("shoppingCol").innerHTML = ""; // Clear the cart items from the display
     document.getElementById("itemCardContainer").innerHTML = ""; // Clear total items display

     loadCartFromCookies();
    // Optionally, update the total cost
    calculateTotalCost();
    calculateTotalItems(); // Optionally update item count display

    
}

function setCookie(name, value, days) {
    const d = new Date();
    d.setTime(d.getTime() + (days * 24 * 60 * 60 * 1000));
    const expires = "expires=" + d.toUTCString();
    document.cookie = name + "=" + value + ";" + expires + ";path=/";
}

function getCookie(name) {
    const nameEQ = name + "=";
    const ca = document.cookie.split(';');
    for(let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) === ' ') c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) === 0) return c.substring(nameEQ.length, c.length);
    }
    return null;
}

function duplicateTotalItem(item) {
    // Get the div
    let originalTotalItem = document.getElementById("totalItemCard")
    // Make a clone
    let clonedTotalItem = originalTotalItem.cloneNode(true);
    // Change the name
    clonedTotalItem.id = "cloned" + item.id + "TotalItem"
    let clonedItemName = clonedTotalItem.querySelector("#itemName");
    clonedItemName.innerHTML = item.name;

    let clonedItemAmount = clonedTotalItem.querySelector("#itemAmount");
    clonedItemAmount.id = "cloned" + item.id + "TotalAmount"
    clonedItemAmount.innerHTML = "x"+item.amount;

    let clonedItemCost = clonedTotalItem.querySelector("#itemCost");
    clonedItemCost.id = "cloned" + item.id + "TotalCost"
    clonedItemCost.innerHTML = "$"+item.price;

    // Append
    clonedTotalItem.classList.remove("d-none");
    document.getElementById("itemCardContainer").appendChild(clonedTotalItem);
}

function calculateTotalItems() {
    totalAmountOfItems = myCart.reduce((total, item) => total + parseInt(item.amount), 0);
    document.getElementById("totalItemCount").innerHTML = "Your Cart (" + totalAmountOfItems + ")";
}

function calculateTotalCost() {
    subtotal = myCart.reduce((total, item) => total + (item.price * item.amount), 0);
    totalDeposit = myCart.reduce((total, item) => total + (item.deposit * item.amount), 0);
    tax = subtotal * taxRate;
    totalCost = subtotal + tax + totalDeposit;

    document.getElementById("subtotalAmount").innerHTML = "$" + subtotal.toFixed(2);
    document.getElementById("taxAmount").innerHTML = "$" + tax.toFixed(2);
    document.getElementById("depositAmount").innerHTML = "$" + totalDeposit.toFixed(2);
    document.getElementById("completeTotal").innerHTML = "$" + totalCost.toFixed(2);


}

// Load cart when the page loads
window.onload = loadCartFromCookies();

function redirectToCheckout() {
    window.location.href = '/checkout'; // Replace with your checkout URL
}

function checkout() {
    const token = localStorage.getItem('jwtToken');

    if (!token) {
        console.error("User is not logged in.");
        return;
    }

    // Fetch the customer ID from the AccountController
    fetch('/account/customerId', {
        method: 'GET',
        headers: {
            'Authorization': "Bearer " + token // Include the token as a Bearer token
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Failed to retrieve customer ID: " + response.statusText);
        }
        return response.text(); // Customer ID will be returned as plain text
    })
    .then(customerId => {
        // Proceed with creating the order
        const creationDate = new Date();
        const localDateString = creationDate.toISOString().split('T')[0]; // Format as YYYY-MM-DD
        calculateTotalCost();
        const orderData = {
            creationDate: localDateString,
            rentalTime: 1, // Set rental time as needed
            paid: 0, // Set to true if the payment is made
            //payment_reference: "UNPAID",
            price: totalCost, // Use the calculated total cost
            orderProducts: myCart.map(item => ({
                product: {
                    id: item.id // Wrap the id in an object
                },
                quantity: item.amount // This remains the same
            }))
        };

        // Create the order
        return fetch(`/order/create?id=${customerId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': "Bearer " + token
            },
            body: JSON.stringify(orderData)
        });
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Network response was not ok: " + response.statusText);
        }
        return response.json();
    })
    .then(data => {
        console.log("Order created successfully:", data);
        redirectToCheckout(); // Redirect to checkout or handle success as needed
    })
    .catch(error => {
        console.error("Error during checkout:", error);
        redirectToCheckout(); // TEMP PLACEHOLDER REMOVE LATER WHEN FIXED
    });
}

document.getElementById("termsCheckbox").addEventListener("change", function() {
    document.getElementById("checkoutButton").disabled = !this.checked;
});
