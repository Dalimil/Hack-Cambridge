var video, cam;
var cam_video_id = "camsource";

var camera = (function(p_vid_id, p_inter, p_scale) {

    if (p_vid_id == undefined) {
        console.log("ERROR: You need to specify the id of the <video> element with the camera data stream.");
        return;
    }

    var vid_id   = p_vid_id;
    var interval = p_inter != undefined ? p_inter : 1000;
    var scale    = p_scale != undefined ? p_scale : 0.5;

    var video    = document.getElementById(vid_id);
    var int_id   = null;

    function start() {
        int_id = setInterval(function(video, scale) { capture() }, interval);
    }

    function stop() {
        console.log("Clearing interval with id "+int_id);
        clearInterval(int_id);
    }

    function capture() {
        // console.time('capture');
        var w = video.videoWidth * scale;
        var h = video.videoHeight * scale;
        var qr_can = document.getElementById('qr-canvas').getContext('2d');
            qr_can.drawImage(video, 0, 0, w, h);
        try        { qrcode.decode();  }
        catch(err) {}
        // console.timeEnd('capture');
    } 

    return {
        interval:interval,
        scale:scale,
        start:start,
        stop:stop,
        capture:capture
    }

})

function read(a)
{
    console.log(a);
    $("#qr-value").removeClass('warning');
    $("#qr-value").text("Authenticating");

    $.post( "https://hackcambridge-3368.appspot.com/authenticate", {'hash': a.toString()})
        .done(function( data ) {
        $("#qr-value").html('Welcome, ' + JSON.parse(data).username + '!');
        cam.stop();
        $("video").each(function () { this.pause() });
        //$("video").slideUp(2000, function() {
        //    this.remove();
        //});
        $("#camcontainer").animate({ height: 'toggle', opacity: 'toggle' }, 600,function(){
                $("#qr-value").fadeIn(2500);
            });

        })
        .fail(function(data){
            $("#qr-value").text("Authentication failed");
            $("#qr-value").addClass('warning');
        })


}
    
qrcode.callback = read;

navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia;

window.addEventListener('DOMContentLoaded', function() {
    // Assign the <video> element to a variable
    var video = document.getElementById(cam_video_id);
    var options = {
        "audio": false,
        "video": true
    };
    // Replace the source of the video element with the stream from the camera
    if (navigator.getUserMedia) {
        navigator.getUserMedia(options, function(stream) {
            video.src = (window.URL && window.URL.createObjectURL(stream)) || stream;
        }, function(error) {
            console.log(error)
        });
        // Below is the latest syntax. Using the old syntax for the time being for backwards compatibility.
        // navigator.getUserMedia({video: true}, successCallback, errorCallback);
    } else {
        $("#qr-value").text('Sorry, native web camera streaming (getUserMedia) is not supported by this browser...')
        return;
    }
}, false);

$(document).ready(function() {
    if (!navigator.getUserMedia) return;
    cam = camera(cam_video_id);
    cam.start()
})