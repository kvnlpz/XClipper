
const http = require('http');
const app = require('express')();
const server = require('http').createServer(app);
const bodyParser = require('body-parser');
const session = require('express-session');
const mongoSession = require('connect-mongodb-session')(session);
const config = require('config');
const io = require('socket.io')(server);
const socketController = require('./controller/socketController');

// MongoDB connection utility function
const { databaseConnection } = require('./utils/database');

// Initalizes express-session
const sessionStore = new mongoSession({
    uri: config.get('DB_URL'),
    collection: 'sessions'
});

// Express-session config
const sessionMiddleware = session({
    secret: config.get('SESSION_SECRET'),
    resave: false, 
    saveUninitialized: false, 
    store: sessionStore,
    unset: 'destroy',
    cookie: {
        maxAge: 600000,
        secure: false
    } 
});

// Use express-session in
app.use(sessionMiddleware);

// Parse body requests as JSON
app.use(bodyParser.json());

const authRoutes = require('./routes/authenticateRoutes');

// Routes
app.use(authRoutes);

// Run socket logic
socketController(io, sessionMiddleware);

databaseConnection(() => {
    server.listen(config.get('devlopment_port'));
});