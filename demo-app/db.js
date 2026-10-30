const { DatabaseSync } = require('node:sqlite');
const fs = require('node:fs');
const path = require('node:path');
const crypto = require('node:crypto');

const DEFAULT_DB_PATH = path.join(__dirname, 'data', 'autogate.db');

function resolveDbPath(customPath) {
  const p = customPath || process.env.DB_PATH || DEFAULT_DB_PATH;
  return path.resolve(p);
}

function hashPassword(password) {
  return crypto.createHash('sha256').update(password).digest('hex');
}

function getDb(customPath) {
  const dbFile = resolveDbPath(customPath);
  const dir = path.dirname(dbFile);
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }

  const db = new DatabaseSync(dbFile);
  initSchema(db);
  return db;
}

function initSchema(db) {
  db.exec(`
    CREATE TABLE IF NOT EXISTS users (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      email TEXT UNIQUE NOT NULL,
      password_hash TEXT NOT NULL,
      name TEXT NOT NULL,
      role TEXT NOT NULL DEFAULT 'customer',
      created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

    CREATE TABLE IF NOT EXISTS products (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      name TEXT NOT NULL,
      description TEXT NOT NULL,
      price REAL NOT NULL,
      category TEXT NOT NULL,
      stock INTEGER NOT NULL DEFAULT 50,
      image_url TEXT NOT NULL,
      created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

    CREATE TABLE IF NOT EXISTS cart_items (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      user_id INTEGER NOT NULL,
      product_id INTEGER NOT NULL,
      quantity INTEGER NOT NULL,
      created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
      UNIQUE(user_id, product_id),
      FOREIGN KEY(user_id) REFERENCES users(id),
      FOREIGN KEY(product_id) REFERENCES products(id)
    );

    CREATE TABLE IF NOT EXISTS orders (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      order_number TEXT UNIQUE NOT NULL,
      user_id INTEGER NOT NULL,
      customer_name TEXT NOT NULL,
      customer_email TEXT NOT NULL,
      shipping_address TEXT NOT NULL,
      shipping_city TEXT NOT NULL,
      shipping_pincode TEXT NOT NULL,
      payment_method TEXT NOT NULL,
      total_amount REAL NOT NULL,
      status TEXT NOT NULL DEFAULT 'CONFIRMED',
      created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
      FOREIGN KEY(user_id) REFERENCES users(id)
    );

    CREATE TABLE IF NOT EXISTS order_items (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      order_id INTEGER NOT NULL,
      product_id INTEGER NOT NULL,
      product_name TEXT NOT NULL,
      unit_price REAL NOT NULL,
      quantity INTEGER NOT NULL,
      subtotal REAL NOT NULL,
      FOREIGN KEY(order_id) REFERENCES orders(id),
      FOREIGN KEY(product_id) REFERENCES products(id)
    );
  `);
}

const SEED_USERS = [
  {
    email: 'admin@autogate.qa',
    password: 'Admin@123',
    name: 'Admin User',
    role: 'admin'
  },
  {
    email: 'tester@autogate.qa',
    password: 'Tester@123',
    name: 'Lead QA Engineer',
    role: 'customer'
  },
  {
    email: 'shopper@autogate.qa',
    password: 'Shopper@123',
    name: 'Alice Walker',
    role: 'customer'
  }
];

const SEED_PRODUCTS = [
  {
    id: 1,
    name: 'UltraSync Smartwatch',
    description: 'Fitness tracker with 1.4-inch AMOLED display and 14-day battery life',
    price: 199.99,
    category: 'Wearables',
    stock: 45,
    image_url: '/images/smartwatch.png'
  },
  {
    id: 2,
    name: 'ProSound ANC Headphones',
    description: 'Over-ear wireless headphones with active noise cancellation and 40h playtime',
    price: 249.50,
    category: 'Audio',
    stock: 30,
    image_url: '/images/headphones.png'
  },
  {
    id: 3,
    name: 'MechKeys Wireless Keyboard',
    description: 'Hot-swappable mechanical switches, RGB backlight and multi-device Bluetooth',
    price: 119.00,
    category: 'Accessories',
    stock: 60,
    image_url: '/images/keyboard.png'
  },
  {
    id: 4,
    name: 'ErgoGlide Precision Mouse',
    description: 'Ergonomic multi-device optical mouse with silent switches and 4000 DPI sensor',
    price: 69.99,
    category: 'Accessories',
    stock: 75,
    image_url: '/images/mouse.png'
  },
  {
    id: 5,
    name: 'HyperHub 8-in-1 USB-C Hub',
    description: 'Dual 4K HDMI, Gigabit Ethernet, 100W Power Delivery and SD card reader',
    price: 49.99,
    category: 'Accessories',
    stock: 90,
    image_url: '/images/hub.png'
  },
  {
    id: 6,
    name: 'ViewMaster 27-inch 4K Monitor',
    description: 'IPS color-accurate panel with ultra-thin bezels, HDR400 and USB-C connectivity',
    price: 389.00,
    category: 'Displays',
    stock: 20,
    image_url: '/images/monitor.png'
  }
];

function seedDatabase(customPath) {
  const dbFile = resolveDbPath(customPath);
  const dir = path.dirname(dbFile);
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }

  const db = new DatabaseSync(dbFile);

  // Drop tables in reverse foreign key order for clean reset
  db.exec(`
    DROP TABLE IF EXISTS order_items;
    DROP TABLE IF EXISTS orders;
    DROP TABLE IF EXISTS cart_items;
    DROP TABLE IF EXISTS products;
    DROP TABLE IF EXISTS users;
  `);

  initSchema(db);

  // Insert seed users
  const userStmt = db.prepare(`
    INSERT INTO users (email, password_hash, name, role)
    VALUES (?, ?, ?, ?)
  `);
  for (const user of SEED_USERS) {
    userStmt.run(user.email, hashPassword(user.password), user.name, user.role);
  }

  // Insert seed products
  const productStmt = db.prepare(`
    INSERT INTO products (id, name, description, price, category, stock, image_url)
    VALUES (?, ?, ?, ?, ?, ?, ?)
  `);
  for (const prod of SEED_PRODUCTS) {
    productStmt.run(prod.id, prod.name, prod.description, prod.price, prod.category, prod.stock, prod.image_url);
  }

  return db;
}

module.exports = {
  getDb,
  seedDatabase,
  hashPassword,
  resolveDbPath,
  DEFAULT_DB_PATH
};
