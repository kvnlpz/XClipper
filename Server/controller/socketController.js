const xss = require('xss');

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
        try {
            const userClips = await Clip.find({username: username});
            socket.emit('refresh', userClips);
        } catch (error) {
            socket.emit('refresh', null);
        }
        
        setInterval(async () => {
            try {
                const userClips = await Clip.find({username: username});
                socket.emit('refresh', userClips);
            } catch (error) {
                socket.emit('refresh', null);
            }
        }, 10000);

        // Event when the client requests a refresh
        socket.on('requestRefresh', async () => {
            try {
                const userClips = await Clip.find({username: username});
                socket.emit('refresh', userClips);
            } catch (error) {
                socket.emit('refresh', null);
            }
        });

        // Event when a socket client sends new clip
        socket.on('sendNewClip', async (clip) => {
            try {
                const sanitizeClip = xss(clip);

                console.log(sanitizeClip);
                
                // Emit to others in the room that a new clip has arrived
                socket.to(username).emit('receiveNewClip', sanitizeClip);

                // Add clip to the database
                const newClip = new Clip({username: username, clip: sanitizeClip});

                await newClip.save();

                // Emit to sender that the clip was sent successfully
                socket.emit('clipSavedStatus', newClip._id);

            } catch (error) {
                console.log(error);
                socket.emit('clipSaved Status', null);
            }
        });

        socket.on('deleteOneClip', async (clip_id) => {
            try {
                await Clip.deleteOne({username: username, _id: clip_id});
                socket.emit('deleteOneClipStatus', clip_id);

            } catch (error) {
                console.log(error);
                socket.emit('deleteOneClipStatus', null);
            }
        });

        socket.on('deleteAllClips', async () => {
            try {
                await Clip.deleteMany({username: username});
                socket.emit('deleteAllClipsStatus', 'success');

            } catch (error) {
                console.log(error);
                socket.emit('deleteAllClipsStatus', null);
            }
        });
    });
};