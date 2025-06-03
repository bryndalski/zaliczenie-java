// MongoDB initialization script for users database
print('Starting MongoDB initialization for users database...');

// Switch to users database
db = db.getSiblingDB(process.env.MONGO_INITDB_DATABASE || 'users');

// Create user collection with validation
db.createCollection('users', {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["id", "name", "surname", "email"],
      properties: {
        id: { bsonType: "string" },
        name: { bsonType: "string" },
        surname: { bsonType: "string" },
        email: { bsonType: "string" },
        dateOfBirth: { bsonType: "date" },
        role: { enum: ["ADMIN", "USER", "MODERATOR"] },
        createdAt: { bsonType: "date" },
        updatedAt: { bsonType: "date" }
      }
    }
  }
});

// Create unique index on email
db.users.createIndex({ "email": 1 }, { unique: true });

// Create index on id for better performance
db.users.createIndex({ "id": 1 }, { unique: true });

print('Users database initialization completed successfully.');
