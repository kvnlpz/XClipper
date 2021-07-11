
const Clip = require('../model/clipModel');

module.exports = (io, sessionMiddleware) => {
    // Lets the socket to use express session if the cookie is passed through the header
    io.use((socket, next) => {
        sessionMiddleware(socket.request, {}, next);
        // sessionMiddleware(socket.request, socket.request.res, next); will not work with websocket-only
        // connections, as 'socket.request.res' will be undefined in that case
    });

    io.on('connection', async (socket) => {
        // Checks if the session is valid
        const session = socket.request.session;
        if (!session.isLoggedIn) {
            console.log('Invalid cookie');
            socket.disconnect(0);
            return;
        }

        const username = session.user['username'];
        const id = session.user['id'];

        // Place user into room based on their username
        socket.join(username);

        // Emit event that gives all the clips assosciated with the username every 1 min
        const userClips = await Clip.find({username: username});
        socket.emit('refresh', userClips);
        
        setInterval(async () => {
            const userClips = await Clip.find({username: username});
            socket.emit('refresh', userClips);
        }, 10000);

        // Event when the client requests a refresh
        socket.on('requestRefresh', async () => {
            const userClips = await Clip.find({username: username});
            socket.emit('refresh', userClips);
        });

        // Event when a socket client sends new clip
        socket.on('sendNewClip', async (clip) => {
            console.log(clip);
            // Emit to others in the room that a new clip has arrived
            socket.to(username).emit('recieveNewClip', clip);

            // Add clip to the database
            const newClip = new Clip({username: username, clip: clip});
            await newClip.save();

            // Emit to sender that the clip was sent successfully
            socket.emit('clipSavedStatus', 'success');
        });
    });
};