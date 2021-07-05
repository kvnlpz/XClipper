const express = require('express');
const { check } = require('express-validator');

const authenticateController = require('../controller/authenticateController');

const router = express.Router();

router.post(
    '/signup',
    check('username').notEmpty(),
    check('email').notEmpty(),
    check('password').notEmpty(),
    check('email').isEmail(),
    authenticateController.postSignUp
);

router.post(
    '/login',
    check('username').notEmpty(),
    check('password').notEmpty(),
    authenticateController.postLogin
);

router.post('/logout', authenticateController.postLogout);

// Route for checking if the client's session is valid
router.get('/checkSession', authenticateController.getCheckSession);

module.exports = router;