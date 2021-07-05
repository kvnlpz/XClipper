const bcrypt = require('bcrypt');
const config = require('config');

const { validationResult } = require('express-validator');
const { usernameOrEmailExists, getUser, createUser, deleteUser} = require('./usersController');

// Checks if the username and password is correct
const checkLogin = async (username, password) => {
    try {
        // Find the user in the database
        const userInfo = await getUser(username);

        // Check if the password matches the user
        const isPasswordCorrect = await bcrypt.compare(password, userInfo.password);

        return isPasswordCorrect;

    } catch (error) {
        throw new Error(`An error occured when trying to find a user the database and checking if the passwords match: ${error}`); 
    }
};

// Creates error response json 
const createErrorJson = (param, msg) => {
    return {
        errors: [{'param': param, 'msg': msg}]
    };
};

const setAuthCookies = async (req, username, id) => {
    // Set session isLoggedIn to be true
    req.session.isLoggedIn = true;
    req.session.user = {username: username, id: id};
    try {
        await req.session.save();
    } catch (error) {
        throw new Error(`An error occured when trying to save the session: ${error}`);
    }
    return req;
};

exports.postSignUp = async (req, res, next) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
        return res.status(400).json({ errors: errors.array() });
    }

    const { username, password, email } = req.body;

    try {
        // Check if user exits in the database
        if (await usernameOrEmailExists(username, email)) return res.status(409).json(createErrorJson('username', 'existed'));

    } catch (error) {
        console.log(error);
        return res.status(500).json(createErrorJson(null, 'server error'));
    }

    try {
        // Hash Password
        const hashedPassword = await bcrypt.hash(password, config.get('SALT_ROUNDS'));
        
        // Insert into DB
        const newUser = await createUser(username, email, hashedPassword);

        req = await setAuthCookies(req, newUser.username, newUser._id);
        
        return res.status(200).json(req.session.user);

    } catch (error) {
        console.log(error);
        return res.status(500).json(createErrorJson(null, 'server error'));
    }
};

exports.postLogin = async (req, res, next) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
        return res.status(400).json({ errors: errors.array() });
    }

    // Get the username, email, and password from the req body
    const { username, password } = req.body;

    try {
        // Check if user exits in the database
        if (!await usernameOrEmailExists(username)) return res.status(401).json(createErrorJson(null, 'unauthorized'));

    } catch (error) {
        console.log(error);
        return res.status(500).json(createErrorJson(null, 'server error'));
    }

    try {
        if (await checkLogin(username, password)) {
            const user = await getUser(username);

            req = await setAuthCookies(req, user.username, user._id);
            
            return res.status(200).json(req.session.user);
    
        } else {
            return res.status(401).json(createErrorJson(null, 'unauthorized'));
        }

    } catch (error) {
        console.log(error);
        return res.status(500).json(createErrorJson(null, 'server error'));
    }

};

exports.postLogout = async (req, res, next) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
        return res.status(400).json({ errors: errors.array() });
    }

    if (req.session) {
        try {
            await req.session.destroy();
            return res.sendStatus(200);

        } catch (error) {
            console.log(error);
            return res.status(500).json(createErrorJson(null, 'server error'));
        }
        
    } else {
        return req.status(400).json(createErrorJson('session', 'no session header provided'));
    }
};

exports.getCheckSession = async (req, res, next) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
        return res.status(400).json({ errors: errors.array() });
    }

    if (req.session.isLoggedIn) {
        // returns true if a user already logged in.
        res.status(200).json({isLoggedIn: true, user: req.session.user});
    } else {
        res.status(401).json({isLoggedIn: false});
    }
};