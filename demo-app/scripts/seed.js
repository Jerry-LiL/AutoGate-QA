const { seedDatabase, resolveDbPath } = require('../db');

const targetPath = resolveDbPath();
console.log(`Seeding database at: ${targetPath}`);
seedDatabase(targetPath);
console.log('Database successfully initialized and seeded with test data.');
