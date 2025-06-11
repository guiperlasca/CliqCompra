function showCartPopup(message) {
    const popup = document.getElementById('cart-popup');
    if (!popup) {
        console.error('Cart popup element not found.');
        return;
    }

    if (message) {
        popup.textContent = message;
    } else {
        popup.textContent = 'Produto adicionado ao carrinho!'; // Default message
    }

    popup.classList.add('show');

    setTimeout(() => {
        popup.classList.remove('show');
    }, 3000); // Hide after 3 seconds
}
