const app = {
  token: localStorage.getItem('autogate_token') || null,
  user: JSON.parse(localStorage.getItem('autogate_user') || 'null'),
  products: [],
  cart: { items: [], totalAmount: 0, itemCount: 0 },
  checkoutData: {
    shipping: null,
    payment: { method: 'CARD' }
  },

  init() {
    this.updateAuthUI();
    this.fetchProducts();
    if (this.token) {
      this.fetchCart();
    }
  },

  navigate(viewName) {
    document.querySelectorAll('.view-section').forEach(el => el.classList.remove('active'));
    const target = document.getElementById(`${viewName}-view`);
    if (target) {
      target.classList.add('active');
    }

    if (viewName === 'cart') {
      this.fetchCart();
    } else if (viewName === 'products') {
      this.fetchProducts();
    }
  },

  updateAuthUI() {
    const greeting = document.getElementById('user-greeting');
    const loginLink = document.querySelector('[data-testid="nav-login"]');
    const logoutBtn = document.querySelector('[data-testid="nav-logout"]');

    if (this.user) {
      greeting.textContent = `Hi, ${this.user.name.split(' ')[0]}`;
      greeting.style.display = 'inline';
      loginLink.style.display = 'none';
      logoutBtn.style.display = 'inline';
    } else {
      greeting.style.display = 'none';
      loginLink.style.display = 'inline';
      logoutBtn.style.display = 'none';
    }
  },

  async handleLogin(event) {
    event.preventDefault();
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;
    const errorBox = document.getElementById('login-error');
    errorBox.style.display = 'none';

    try {
      const res = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
      });

      const data = await res.json();
      if (!res.ok) {
        errorBox.textContent = data.message || 'Login failed';
        errorBox.style.display = 'block';
        return;
      }

      this.token = data.token;
      this.user = data.user;
      localStorage.setItem('autogate_token', this.token);
      localStorage.setItem('autogate_user', JSON.stringify(this.user));

      this.updateAuthUI();
      await this.fetchCart();
      this.navigate('products');
    } catch (err) {
      errorBox.textContent = 'Network or server error during login';
      errorBox.style.display = 'block';
    }
  },

  logout() {
    this.token = null;
    this.user = null;
    localStorage.removeItem('autogate_token');
    localStorage.removeItem('autogate_user');
    this.cart = { items: [], totalAmount: 0, itemCount: 0 };
    this.updateCartBadge();
    this.updateAuthUI();
    this.navigate('login');
  },

  async fetchProducts() {
    try {
      const res = await fetch('/api/products');
      this.products = await res.json();
      this.renderProducts(this.products);
    } catch (err) {
      console.error('Failed to fetch products', err);
    }
  },

  handleFilter() {
    const search = document.getElementById('search-input').value.toLowerCase();
    const category = document.getElementById('category-filter').value;

    const filtered = this.products.filter(p => {
      const matchesSearch = p.name.toLowerCase().includes(search) || p.description.toLowerCase().includes(search);
      const matchesCat = !category || p.category === category;
      return matchesSearch && matchesCat;
    });

    this.renderProducts(filtered);
  },

  renderProducts(list) {
    const grid = document.getElementById('product-grid');
    grid.innerHTML = '';

    list.forEach(p => {
      const card = document.createElement('div');
      card.className = 'card product-card';
      card.setAttribute('data-testid', `product-card-${p.id}`);
      card.innerHTML = `
        <div>
          <span style="font-size: 0.75rem; text-transform: uppercase; font-weight: 700; color: var(--text-muted);">${p.category}</span>
          <h3 data-testid="product-title-${p.id}">${p.name}</h3>
          <p class="product-desc" data-testid="product-desc-${p.id}">${p.description}</p>
          <div class="product-price" data-testid="product-price-${p.id}">$${p.price.toFixed(2)}</div>
          <div style="font-size: 0.8rem; color: var(--text-muted); margin-bottom: 0.75rem;">
            In Stock: <span data-testid="product-stock-${p.id}">${p.stock}</span>
          </div>
        </div>
        <div>
          <div class="product-actions">
            <input type="number" id="qty-${p.id}" data-testid="product-qty-${p.id}" value="1" min="1" max="10">
            <button class="btn" style="flex: 1;" data-testid="add-to-cart-${p.id}" onclick="app.addToCart(${p.id})">Add to Cart</button>
          </div>
          <div id="qty-error-${p.id}" class="form-error" data-testid="qty-error-${p.id}">Quantity must be between 1 and 10</div>
        </div>
      `;
      grid.appendChild(card);
    });
  },

  async addToCart(productId) {
    const qtyInput = document.getElementById(`qty-${productId}`);
    const qtyErr = document.getElementById(`qty-error-${productId}`);
    const qty = parseInt(qtyInput.value, 10);

    if (isNaN(qty) || qty < 1 || qty > 10) {
      qtyErr.classList.add('visible');
      return;
    }
    qtyErr.classList.remove('visible');

    if (!this.token) {
      alert('Please log in first to add items to your cart.');
      this.navigate('login');
      return;
    }

    try {
      const res = await fetch('/api/cart', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.token}`
        },
        body: JSON.stringify({ productId, quantity: qty })
      });

      if (!res.ok) {
        const data = await res.json();
        alert(data.message || 'Could not add item to cart');
        return;
      }

      const data = await res.json();
      this.cart = data;
      this.updateCartBadge();
      this.showToast('Item added to cart!');
    } catch (err) {
      console.error('Cart add error', err);
    }
  },

  async fetchCart() {
    if (!this.token) {
      this.renderCart();
      return;
    }

    try {
      const res = await fetch('/api/cart', {
        headers: { 'Authorization': `Bearer ${this.token}` }
      });
      if (res.ok) {
        this.cart = await res.json();
        this.updateCartBadge();
        this.renderCart();
      }
    } catch (err) {
      console.error('Fetch cart error', err);
    }
  },

  updateCartBadge() {
    const badge = document.querySelector('[data-testid="cart-badge"]');
    badge.textContent = this.cart.itemCount || 0;
  },

  renderCart() {
    const tbody = document.getElementById('cart-table-body');
    const emptyMsg = document.getElementById('empty-cart-msg');
    const content = document.getElementById('cart-content');
    const totalSpan = document.getElementById('cart-total-amount');

    if (!this.cart.items || this.cart.items.length === 0) {
      emptyMsg.style.display = 'block';
      content.style.display = 'none';
      return;
    }

    emptyMsg.style.display = 'none';
    content.style.display = 'block';
    tbody.innerHTML = '';

    this.cart.items.forEach(item => {
      const tr = document.createElement('tr');
      tr.setAttribute('data-testid', `cart-row-${item.productId}`);
      tr.innerHTML = `
        <td data-testid="cart-item-title-${item.productId}"><strong>${item.name}</strong></td>
        <td data-testid="cart-item-price-${item.productId}">$${item.price.toFixed(2)}</td>
        <td data-testid="cart-item-qty-${item.productId}">${item.quantity}</td>
        <td data-testid="cart-item-subtotal-${item.productId}">$${item.subtotal.toFixed(2)}</td>
        <td>
          <button class="btn btn-outline" style="padding: 0.25rem 0.6rem; font-size: 0.8rem;"
                  data-testid="cart-item-remove-${item.productId}"
                  onclick="app.removeFromCart(${item.productId})">
            Remove
          </button>
        </td>
      `;
      tbody.appendChild(tr);
    });

    totalSpan.textContent = this.cart.totalAmount.toFixed(2);
  },

  async removeFromCart(productId) {
    if (!this.token) return;
    try {
      const res = await fetch(`/api/cart/${productId}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${this.token}` }
      });
      if (res.ok) {
        this.cart = await res.json();
        this.updateCartBadge();
        this.renderCart();
      }
    } catch (err) {
      console.error('Delete cart item error', err);
    }
  },

  async clearCart() {
    if (!this.token) return;
    try {
      const res = await fetch('/api/cart', {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${this.token}` }
      });
      if (res.ok) {
        this.cart = await res.json();
        this.updateCartBadge();
        this.renderCart();
      }
    } catch (err) {
      console.error('Clear cart error', err);
    }
  },

  startCheckout() {
    if (!this.token) {
      alert('Please log in to proceed to checkout.');
      this.navigate('login');
      return;
    }
    if (!this.cart.items || this.cart.items.length === 0) {
      alert('Your cart is empty.');
      return;
    }
    this.goToStep(1);
    this.navigate('checkout');
  },

  goToStep(stepNumber) {
    for (let i = 1; i <= 4; i++) {
      const tab = document.getElementById(`step-indicator-${i}`);
      const content = document.getElementById(`checkout-step-${i}`);
      if (tab) tab.classList.remove('active');
      if (content) content.classList.remove('active');
    }
    const targetTab = document.getElementById(`step-indicator-${stepNumber}`);
    const targetContent = document.getElementById(`checkout-step-${stepNumber}`);
    if (targetTab) targetTab.classList.add('active');
    if (targetContent) targetContent.classList.add('active');
  },

  validateStep1() {
    let isValid = true;
    const nameInput = document.getElementById('shipping-name');
    const nameErr = document.getElementById('error-shipping-name');
    if (!nameInput.value || nameInput.value.trim().length < 2) {
      nameErr.classList.add('visible');
      isValid = false;
    } else {
      nameErr.classList.remove('visible');
    }

    const addrInput = document.getElementById('shipping-address');
    const addrErr = document.getElementById('error-shipping-address');
    if (!addrInput.value || addrInput.value.trim().length < 5) {
      addrErr.classList.add('visible');
      isValid = false;
    } else {
      addrErr.classList.remove('visible');
    }

    const cityInput = document.getElementById('shipping-city');
    const cityErr = document.getElementById('error-shipping-city');
    if (!cityInput.value || cityInput.value.trim().length < 2) {
      cityErr.classList.add('visible');
      isValid = false;
    } else {
      cityErr.classList.remove('visible');
    }

    const pincodeInput = document.getElementById('shipping-pincode');
    const pincodeErr = document.getElementById('error-shipping-pincode');
    const pincodeVal = pincodeInput.value.trim();
    if (!/^\d{6}$/.test(pincodeVal)) {
      pincodeErr.classList.add('visible');
      isValid = false;
    } else {
      pincodeErr.classList.remove('visible');
    }

    if (!isValid) return;

    this.checkoutData.shipping = {
      fullName: nameInput.value.trim(),
      addressLine: addrInput.value.trim(),
      city: cityInput.value.trim(),
      pincode: pincodeVal
    };

    this.goToStep(2);
  },

  handlePaymentMethodChange() {
    const method = document.getElementById('payment-method-select').value;
    const cardFields = document.getElementById('card-fields');
    if (method === 'CARD') {
      cardFields.style.display = 'block';
    } else {
      cardFields.style.display = 'none';
    }
  },

  validateStep2() {
    const method = document.getElementById('payment-method-select').value;
    let isValid = true;

    if (method === 'CARD') {
      const cardNum = document.getElementById('card-number').value.trim();
      const cardNumErr = document.getElementById('error-card-number');
      if (!/^\d{16}$/.test(cardNum)) {
        cardNumErr.classList.add('visible');
        isValid = false;
      } else {
        cardNumErr.classList.remove('visible');
      }

      const cardExp = document.getElementById('card-expiry').value.trim();
      const cardExpErr = document.getElementById('error-card-expiry');
      if (!/^(0[1-9]|1[0-2])\/\d{2}$/.test(cardExp)) {
        cardExpErr.classList.add('visible');
        isValid = false;
      } else {
        cardExpErr.classList.remove('visible');
      }

      const cardCvv = document.getElementById('card-cvv').value.trim();
      const cardCvvErr = document.getElementById('error-card-cvv');
      if (!/^\d{3}$/.test(cardCvv)) {
        cardCvvErr.classList.add('visible');
        isValid = false;
      } else {
        cardCvvErr.classList.remove('visible');
      }
    }

    if (!isValid) return;

    this.checkoutData.payment = { method };
    this.populateStep3();
    this.goToStep(3);
  },

  populateStep3() {
    const s = this.checkoutData.shipping;
    document.getElementById('review-shipping-details').innerHTML = `
      <strong>${s.fullName}</strong><br>
      ${s.addressLine}<br>
      ${s.city} - ${s.pincode}
    `;

    document.getElementById('review-payment-details').innerHTML = `
      <strong>Method:</strong> ${this.checkoutData.payment.method}
    `;

    let itemsHtml = '<ul style="list-style: none; padding: 0;">';
    this.cart.items.forEach(item => {
      itemsHtml += `
        <li style="display: flex; justify-content: space-between; margin-bottom: 0.35rem;">
          <span>${item.name} &times; ${item.quantity}</span>
          <strong>$${item.subtotal.toFixed(2)}</strong>
        </li>
      `;
    });
    itemsHtml += '</ul>';
    document.getElementById('review-items-list').innerHTML = itemsHtml;
    document.getElementById('review-total-amount').textContent = this.cart.totalAmount.toFixed(2);
  },

  async submitOrder() {
    const errorBox = document.getElementById('checkout-error-msg');
    errorBox.style.display = 'none';

    try {
      const res = await fetch('/api/orders', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.token}`
        },
        body: JSON.stringify({
          shippingAddress: this.checkoutData.shipping,
          paymentMethod: this.checkoutData.payment.method,
          items: this.cart.items.map(i => ({ productId: i.productId, quantity: i.quantity }))
        })
      });

      const data = await res.json();
      if (!res.ok) {
        errorBox.textContent = data.message || 'Order placement failed';
        errorBox.style.display = 'block';
        return;
      }

      // Order success!
      document.getElementById('order-confirmation-number').textContent = data.orderNumber;
      document.getElementById('order-confirmation-status').textContent = data.status;
      document.getElementById('order-confirmation-total').textContent = data.totalAmount.toFixed(2);
      document.getElementById('order-confirmation-shipping').textContent = `${data.customerName}, ${data.shippingAddress}, ${data.shippingCity} - ${data.shippingPincode}`;

      this.cart = { items: [], totalAmount: 0, itemCount: 0 };
      this.updateCartBadge();
      this.goToStep(4);
    } catch (err) {
      errorBox.textContent = 'Network or server error during order submission';
      errorBox.style.display = 'block';
    }
  },

  showToast(msg) {
    const toast = document.getElementById('cart-toast');
    toast.textContent = msg;
    toast.classList.add('show');
    setTimeout(() => {
      toast.classList.remove('show');
    }, 2500);
  }
};

window.addEventListener('DOMContentLoaded', () => {
  app.init();
});
