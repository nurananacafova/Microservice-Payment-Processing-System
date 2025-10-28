db = db.getSiblingDB('risk_db');
db.createCollection('risk_checks');

db = db.getSiblingDB('payment_db');
db.createCollection('payments');
