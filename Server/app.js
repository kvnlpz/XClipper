const fs = require('fs');
const app = require('express')();

const server = require('https').createServer({
    key: fs.readFileSync('./security/key.pem'),
    cert: fs.readFileSync('./security/cert.pem')
}, app);

const bodyParser = require('body-parser');
const cors = require('cors');
const session = require('express-session');
const mongoSession = require('connect-mongodb-session')(session);
const config = require('config');

const io = require('socket.io')(server, { 
    cors: {
        origin: config.get('CORS_ORIGINS'),
        allowedHeaders: config.get('CORS_EXPOSE_HEADERS'),
        methods: config.get('CORS_ALLOWED_METHODS'),
        credentials: config.get('CORS_ALLOW_CREDENTIALS')
    }
});

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
        secure: true
    }
});

// Enables cors for all requests
app.use(cors({ 
    origin: config.get('CORS_ORIGINS'),
    methods: config.get('CORS_ALLOWED_METHODS'),
    credentials: config.get('CORS_ALLOW_CREDENTIALS'),
    exposedHeaders: config.get('CORS_EXPOSE_HEADERS') 
}));

// Use express-session in
app.use(sessionMiddleware);

// Parse body requests as JSON
app.use(bodyParser.json());

const authRoutes = require('./routes/authenticateRoutes');

// Routes
app.use(authRoutes);

// Run socket logic
const socketController = require('./controller/socketController');
socketController(io, sessionMiddleware);

databaseConnection(() => {
    server.listen(config.get('DEV_PORT'));
});