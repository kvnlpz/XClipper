const mongoose = require('mongoose');

const Schema = mongoose.Schema;

const clipSchema = new Schema({
    username: {
        type: String,
        required: true
    },

    clip: {
        type: String,
        required: true
    },
    createdAt: { type: Date, expires: '5m', default: Date.now }
});

module.exports = mongoose.model('Clip', clipSchema);