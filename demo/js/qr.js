function read(a)
{
    $("#qr-value").text(a);
    cam.stop();
    console.log(cam);
}
    
qrcode.callback = read;