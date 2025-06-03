// MongoDB initialization script for auth database
print('Starting MongoDB initialization for auth database...');

// Switch to auth database
db = db.getSiblingDB(process.env.MONGO_INITDB_DATABASE || 'auth');

// Create auth collections
db.createCollection('passwords');
db.createCollection('tokens');
db.createCollection('sessions');

// Create indexes for better performance
db.passwords.createIndex({ "userId": 1 }, { unique: true });
db.tokens.createIndex({ "token": 1 }, { unique: true });
db.sessions.createIndex({ "sessionId": 1 }, { unique: true });

print('Auth database initialization completed successfully.');
