const express = require('express');
const cors = require('cors');
const jwt = require('jsonwebtoken');
const path = require('node:path');
const { getDb, seedDatabase, hashPassword } = require('./db');

const app = express();
const PORT = process.env.PORT || 3000;
const JWT_SECRET = process.env.JWT_SECRET || 'autogate-secret-key-qa-regression';

app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(express.static(path.join(__dirname, 'public')));

// JWT Authentication Middleware
function authenticateToken(req, res, next) {
  const authHeader = req.headers['authorization'];
  if (!authHeader) {
    return res.status(401).json({
      error: 'Unauthorized',
      message: 'Authorization header is missing'
    });
  }

  const parts = authHeader.split(' ');
  if (parts.length !== 2 || parts[0] !== 'Bearer') {
    return res.status(401).json({
      error: 'Unauthorized',
      message: 'Invalid authorization format. Expected: Bearer <token>'
    });
  }

  jwt.verify(parts[1], JWT_SECRET, (err, user) => {
    if (err) {
      return res.status(401).json({
        error: 'Unauthorized',
        message: 'Invalid or expired authentication token'
      });
    }
    req.user = user;
    next();
  });
}

// Health Check Endpoint
app.get('/health', (req, res) => {
  res.status(200).json({
    status: 'UP',
    service: 'AutoGate Mini Shop',
    timestamp: new Date().toISOString()
  });
});

// Database Reset Endpoint (useful for test runs)
app.post('/api/reset', (req, res) => {
  try {
    seedDatabase();
    res.status(200).json({
      message: 'Database reset to initial seed state',
      timestamp: new Date().toISOString()
    });
  } catch (error) {
    res.status(500).json({
      error: 'Internal Server Error',
      message: error.message
    });
  }
});

// Auth Login Endpoint
app.post('/api/auth/login', (req, res) => {
  const { email, password } = req.body || {};

  if (!email || !password) {
    return res.status(400).json({
      error: 'Bad Request',
      message: 'Email and password are required'
    });
  }

  try {
    const db = getDb();
    const user = db.prepare('SELECT id, email, password_hash, name, role FROM users WHERE email = ?').get(email.trim());

    if (!user) {
      return res.status(401).json({
        error: 'Unauthorized',
        message: 'Invalid email or password'
      });
    }

    const hashed = hashPassword(password);
    if (user.password_hash !== hashed) {
      return res.status(401).json({
        error: 'Unauthorized',
        message: 'Invalid email or password'
      });
    }

    const token = jwt.sign(
      {
        id: user.id,
        email: user.email,
        name: user.name,
        role: user.role
      },
      JWT_SECRET,
      { expiresIn: '24h' }
    );

    res.status(200).json({
      token,
      user: {
        id: user.id,
        email: user.email,
        name: user.name,
        role: user.role
      }
    });
  } catch (error) {
    res.status(500).json({
      error: 'Internal Server Error',
      message: error.message
    });
  }
});

// Current User Endpoint
app.get('/api/auth/me', authenticateToken, (req, res) => {
  res.status(200).json({ user: req.user });
});

// Product Listing Endpoint
app.get('/api/products', (req, res) => {
  try {
    const db = getDb();
    const { category, search } = req.query;

    let query = 'SELECT id, name, description, price, category, stock, image_url, created_at FROM products WHERE 1=1';
    const params = [];

    if (category) {
      query += ' AND category = ?';
      params.push(category);
    }
    if (search) {
      query += ' AND (name LIKE ? OR description LIKE ?)';
      params.push(`%${search}%`, `%${search}%`);
    }

    query += ' ORDER BY id ASC';
    const products = db.prepare(query).all(...params);
    res.status(200).json(products);
  } catch (error) {
    res.status(500).json({
      error: 'Internal Server Error',
      message: error.message
    });
  }
});

// Single Product Endpoint
app.get('/api/products/:id', (req, res) => {
  const productId = parseInt(req.params.id, 10);
  if (isNaN(productId) || productId <= 0) {
    return res.status(400).json({
      error: 'Bad Request',
      message: 'Product ID must be a positive integer'
    });
  }

  try {
    const db = getDb();
    const product = db.prepare('SELECT id, name, description, price, category, stock, image_url, created_at FROM products WHERE id = ?').get(productId);

    if (!product) {
      return res.status(404).json({
        error: 'Not Found',
        message: `Product not found with id: ${productId}`
      });
    }

    res.status(200).json(product);
  } catch (error) {
    res.status(500).json({
      error: 'Internal Server Error',
      message: error.message
    });
  }
});

// Get Cart Endpoint
app.get('/api/cart', authenticateToken, (req, res) => {
  try {
    const db = getDb();
    const items = db.prepare(`
      SELECT c.id, c.product_id AS productId, p.name, p.price, c.quantity,
             ROUND(p.price * c.quantity, 2) AS subtotal, p.image_url AS imageUrl
      FROM cart_items c
      JOIN products p ON c.product_id = p.id
      WHERE c.user_id = ?
      ORDER BY c.id ASC
    `).all(req.user.id);

    const totalAmount = items.reduce((sum, item) => sum + item.subtotal, 0);
    const itemCount = items.reduce((sum, item) => sum + item.quantity, 0);

    res.status(200).json({
      items,
      totalAmount: Math.round(totalAmount * 100) / 100,
      itemCount
    });
  } catch (error) {
    res.status(500).json({
      error: 'Internal Server Error',
      message: error.message
    });
  }
});

// Add / Update Cart Endpoint
app.post('/api/cart', authenticateToken, (req, res) => {
  const { productId, quantity } = req.body || {};

  const pId = parseInt(productId, 10);
  const qty = parseInt(quantity, 10);

  if (isNaN(pId) || pId <= 0) {
    return res.status(400).json({
      error: 'Validation Error',
      message: 'Valid productId is required'
    });
  }

  if (isNaN(qty) || qty < 1 || qty > 10) {
    return res.status(400).json({
      error: 'Validation Error',
      message: 'Quantity must be an integer between 1 and 10'
    });
  }

  try {
    const db = getDb();
    const product = db.prepare('SELECT id, name, price, stock FROM products WHERE id = ?').get(pId);
    if (!product) {
      return res.status(404).json({
        error: 'Not Found',
        message: `Product not found with id: ${pId}`
      });
    }

    // Upsert into cart_items
    const existing = db.prepare('SELECT id, quantity FROM cart_items WHERE user_id = ? AND product_id = ?').get(req.user.id, pId);
    if (existing) {
      db.prepare('UPDATE cart_items SET quantity = ? WHERE id = ?').run(qty, existing.id);
    } else {
      db.prepare('INSERT INTO cart_items (user_id, product_id, quantity) VALUES (?, ?, ?)').run(req.user.id, pId, qty);
    }

    const items = db.prepare(`
      SELECT c.id, c.product_id AS productId, p.name, p.price, c.quantity,
             ROUND(p.price * c.quantity, 2) AS subtotal, p.image_url AS imageUrl
      FROM cart_items c
      JOIN products p ON c.product_id = p.id
      WHERE c.user_id = ?
      ORDER BY c.id ASC
    `).all(req.user.id);

    const totalAmount = items.reduce((sum, item) => sum + item.subtotal, 0);
    const itemCount = items.reduce((sum, item) => sum + item.quantity, 0);

    res.status(200).json({
      message: 'Item added to cart',
      items,
      totalAmount: Math.round(totalAmount * 100) / 100,
      itemCount
    });
  } catch (error) {
    res.status(500).json({
      error: 'Internal Server Error',
      message: error.message
    });
  }
});

// Remove Item from Cart Endpoint
app.delete('/api/cart/:productId', authenticateToken, (req, res) => {
  const pId = parseInt(req.params.productId, 10);
  if (isNaN(pId) || pId <= 0) {
    return res.status(400).json({
      error: 'Bad Request',
      message: 'Product ID must be a positive integer'
    });
  }

  try {
    const db = getDb();
    db.prepare('DELETE FROM cart_items WHERE user_id = ? AND product_id = ?').run(req.user.id, pId);

    const items = db.prepare(`
      SELECT c.id, c.product_id AS productId, p.name, p.price, c.quantity,
             ROUND(p.price * c.quantity, 2) AS subtotal, p.image_url AS imageUrl
      FROM cart_items c
      JOIN products p ON c.product_id = p.id
      WHERE c.user_id = ?
      ORDER BY c.id ASC
    `).all(req.user.id);

    const totalAmount = items.reduce((sum, item) => sum + item.subtotal, 0);
    const itemCount = items.reduce((sum, item) => sum + item.quantity, 0);

    res.status(200).json({
      message: 'Item removed from cart',
      items,
      totalAmount: Math.round(totalAmount * 100) / 100,
      itemCount
    });
  } catch (error) {
    res.status(500).json({
      error: 'Internal Server Error',
      message: error.message
    });
  }
});

// Clear Cart Endpoint
app.delete('/api/cart', authenticateToken, (req, res) => {
  try {
    const db = getDb();
    db.prepare('DELETE FROM cart_items WHERE user_id = ?').run(req.user.id);
    res.status(200).json({
      message: 'Cart cleared',
      items: [],
      totalAmount: 0,
      itemCount: 0
    });
  } catch (error) {
    res.status(500).json({
      error: 'Internal Server Error',
      message: error.message
    });
  }
});

// Create Order / Checkout Endpoint
app.post('/api/orders', authenticateToken, (req, res) => {
  const { shippingAddress, paymentMethod, items } = req.body || {};

  // Validate shippingAddress
  if (!shippingAddress || typeof shippingAddress !== 'object') {
    return res.status(422).json({
      error: 'Validation Error',
      field: 'shippingAddress',
      message: 'Shipping address is required'
    });
  }

  const fullName = (shippingAddress.fullName || '').trim();
  if (!fullName || fullName.length < 2) {
    return res.status(422).json({
      error: 'Validation Error',
      field: 'fullName',
      message: 'Full name is required and must be at least 2 characters'
    });
  }

  const addressLine = (shippingAddress.addressLine || '').trim();
  if (!addressLine || addressLine.length < 5) {
    return res.status(422).json({
      error: 'Validation Error',
      field: 'addressLine',
      message: 'Address is required and must be at least 5 characters'
    });
  }

  const city = (shippingAddress.city || '').trim();
  if (!city || city.length < 2) {
    return res.status(422).json({
      error: 'Validation Error',
      field: 'city',
      message: 'City is required and must be at least 2 characters'
    });
  }

  const pincode = String(shippingAddress.pincode || '').trim();
  if (!/^\d{6}$/.test(pincode)) {
    return res.status(422).json({
      error: 'Validation Error',
      field: 'pincode',
      message: 'Pincode must be exactly 6 numeric digits'
    });
  }

  // Validate paymentMethod
  const validPaymentMethods = ['CARD', 'UPI', 'COD'];
  const method = (paymentMethod || '').trim().toUpperCase();
  if (!validPaymentMethods.includes(method)) {
    return res.status(422).json({
      error: 'Validation Error',
      field: 'paymentMethod',
      message: 'Payment method must be CARD, UPI, or COD'
    });
  }

  // Validate items
  let orderItems = items;
  const db = getDb();

  // If items not provided, load items from user's cart
  if (!orderItems || !Array.isArray(orderItems) || orderItems.length === 0) {
    const cartRows = db.prepare(`
      SELECT c.product_id AS productId, c.quantity
      FROM cart_items c
      WHERE c.user_id = ?
    `).all(req.user.id);
    if (!cartRows || cartRows.length === 0) {
      return res.status(400).json({
        error: 'Validation Error',
        message: 'Cart or order items must not be empty'
      });
    }
    orderItems = cartRows;
  }

  // Verify each item
  const validatedItems = [];
  let totalAmount = 0;

  for (const item of orderItems) {
    const pId = parseInt(item.productId, 10);
    const qty = parseInt(item.quantity, 10);

    if (isNaN(pId) || pId <= 0) {
      return res.status(400).json({
        error: 'Validation Error',
        message: 'Invalid productId in items list'
      });
    }

    if (isNaN(qty) || qty < 1 || qty > 10) {
      return res.status(400).json({
        error: 'Validation Error',
        message: `Quantity for product ID ${pId} must be between 1 and 10`
      });
    }

    const product = db.prepare('SELECT id, name, price, stock FROM products WHERE id = ?').get(pId);
    if (!product) {
      return res.status(404).json({
        error: 'Not Found',
        message: `Product not found with id: ${pId}`
      });
    }

    const subtotal = Math.round(product.price * qty * 100) / 100;
    totalAmount += subtotal;

    validatedItems.push({
      productId: product.id,
      productName: product.name,
      unitPrice: product.price,
      quantity: qty,
      subtotal
    });
  }

  totalAmount = Math.round(totalAmount * 100) / 100;
  const orderNumber = `ORD-${Date.now()}-${Math.floor(Math.random() * 9000 + 1000)}`;

  try {
    // Insert order
    const orderStmt = db.prepare(`
      INSERT INTO orders (
        order_number, user_id, customer_name, customer_email,
        shipping_address, shipping_city, shipping_pincode,
        payment_method, total_amount, status
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    `);

    const orderResult = orderStmt.run(
      orderNumber,
      req.user.id,
      fullName,
      req.user.email,
      addressLine,
      city,
      pincode,
      method,
      totalAmount,
      'CONFIRMED'
    );

    const orderId = Number(orderResult.lastInsertRowid);

    // Insert order items
    const itemStmt = db.prepare(`
      INSERT INTO order_items (order_id, product_id, product_name, unit_price, quantity, subtotal)
      VALUES (?, ?, ?, ?, ?, ?)
    `);

    for (const item of validatedItems) {
      itemStmt.run(orderId, item.productId, item.productName, item.unitPrice, item.quantity, item.subtotal);
    }

    // Clear user cart
    db.prepare('DELETE FROM cart_items WHERE user_id = ?').run(req.user.id);

    res.status(201).json({
      id: orderId,
      orderNumber,
      customerName: fullName,
      customerEmail: req.user.email,
      shippingAddress: addressLine,
      shippingCity: city,
      shippingPincode: pincode,
      paymentMethod: method,
      totalAmount,
      status: 'CONFIRMED',
      items: validatedItems,
      createdAt: new Date().toISOString()
    });
  } catch (error) {
    res.status(500).json({
      error: 'Internal Server Error',
      message: error.message
    });
  }
});

// Get Order By ID Endpoint
app.get('/api/orders/:id', authenticateToken, (req, res) => {
  const orderId = parseInt(req.params.id, 10);
  if (isNaN(orderId) || orderId <= 0) {
    return res.status(400).json({
      error: 'Bad Request',
      message: 'Order ID must be a positive integer'
    });
  }

  try {
    const db = getDb();
    const order = db.prepare(`
      SELECT id, order_number AS orderNumber, user_id AS userId,
             customer_name AS customerName, customer_email AS customerEmail,
             shipping_address AS shippingAddress, shipping_city AS shippingCity,
             shipping_pincode AS shippingPincode, payment_method AS paymentMethod,
             total_amount AS totalAmount, status, created_at AS createdAt
      FROM orders
      WHERE id = ?
    `).get(orderId);

    if (!order) {
      return res.status(404).json({
        error: 'Not Found',
        message: `Order not found with id: ${orderId}`
      });
    }

    // Non-admins can only see their own orders
    if (req.user.role !== 'admin' && order.userId !== req.user.id) {
      return res.status(403).json({
        error: 'Forbidden',
        message: 'You do not have permission to view this order'
      });
    }

    const items = db.prepare(`
      SELECT product_id AS productId, product_name AS productName,
             unit_price AS unitPrice, quantity, subtotal
      FROM order_items
      WHERE order_id = ?
      ORDER BY id ASC
    `).all(orderId);

    order.items = items;
    res.status(200).json(order);
  } catch (error) {
    res.status(500).json({
      error: 'Internal Server Error',
      message: error.message
    });
  }
});

// Serve Single Page / Fallback
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

// Start Server
if (require.main === module) {
  // Ensure DB initialized
  getDb();
  app.listen(PORT, () => {
    console.log(`AutoGate Demo Shop running on http://localhost:${PORT}`);
  });
}

module.exports = app;
