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
    }
});

module.exports = mongoose.model('Clip', clipSchema);