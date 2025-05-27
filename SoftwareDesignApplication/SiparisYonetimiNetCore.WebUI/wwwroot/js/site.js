// Please see documentation at https://docs.microsoft.com/aspnet/core/client-side/bundling-and-minification
// for details on configuring this project to bundle and minify static web assets.

// Write your JavaScript code.

function addToCart(productId) {
    if (!isUserLoggedIn()) {
        alert('Lütfen giriş yapın');
        window.location.href = '/Account/Login';
        return;
    }

    const quantity = parseInt(document.getElementById('inputQuantity')?.value) || 1;
    
    $.post('/Cart/AddToCart', { productId: productId, quantity: quantity })
        .done(function (data) {
            if (data.success) {
                $('#cart-count').text(data.count);
                alert(data.productName + ' sepete eklendi!');
            }
        })
        .fail(function () {
            alert('Ürün sepete eklenirken bir hata oluştu.');
        });
}

// Check if user is logged in
function isUserLoggedIn() {
    return document.body.classList.contains('user-authenticated');
}

// Update cart count on page load
$(document).ready(function () {
    $.get('/Cart/GetCartCount', function (data) {
        $('#cart-count').text(data.count);
    });
});
