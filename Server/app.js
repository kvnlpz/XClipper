
const http = require('http');
const express = require('express');
const bodyParser = require('body-parser');
const session = require('express-session');
const mongoSession = require('connect-mongodb-session')(session);
const config = require('config');

// MongoDB connection utility function
const { databaseConnection } = require('./utils/database');

const app = express();

// Initalizes express-session
const sessionStore = new mongoSession({
    uri: config.get('DB_URL'),
    collection: 'sessions'
});

// Express-session config
app.use(session({
    secret: config.get('SESSION_SECRET'),
    resave: false, 
    saveUninitialized: false, 
    store: sessionStore,
    unset: 'destroy',
    cookie: {
        maxAge: 600000,
        secure: true
    } 
}));

// Parse body requests as JSON
app.use(bodyParser.json());

const authRoutes = require('./routes/authenticateRoutes');

// Routes
app.use(authRoutes);

databaseConnection(() => {
    http.createServer(app).listen(config.get('devlopment_port'));
});