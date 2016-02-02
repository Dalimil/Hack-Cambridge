# Hack-Cambridge - Hackathon (Cambridge, 30-31st Jan 2016)
We built a security service called 'Clear Pass'. It consists of several parts:

There is a web server managing a database of users and their biometric data.  
It also provides a website explaining how to use/integrate the product. 

Than there is an Android app which is used for scanning of fingerprints, measuring heart rate, etc.  
The mobile app is then used to generate QR-code-based one-time passwords.

Client company with our integrated JS plugin provides a way for the users to scan their QR codes (via a webcam) and then communicates with our server to verify user's authenticity.

All parts of this system are implemented and saved in corresponding folders.  
We used Google App Engine but they deleted the provided account after the hackathon.

**Team:** [Dalimil Hajek](https://github.com/dalimil), [Miguel Jaques](https://github.com/seuqaj114), [Andre Melo](https://github.com/andrenmelo), [Nicholas Boucher](https://github.com/nickboucher32)

## Structure

Server in /Server

Android app in /Android-app

Client-Company-Website-Demo in /Demo-company

Google App Engine files included for convenience

## Screenshots

![01](https://github.com/Dalimil/Hack-Cambridge/blob/master/Screenshots/Screenshot1.png)
![02](https://github.com/Dalimil/Hack-Cambridge/blob/master/Screenshots/Screenshot2.png)
![03](https://github.com/Dalimil/Hack-Cambridge/blob/master/Screenshots/Screenshot3.png)
![04](https://github.com/Dalimil/Hack-Cambridge/blob/master/Screenshots/Screenshot4.png)
![05](https://github.com/Dalimil/Hack-Cambridge/blob/master/Screenshots/Screenshot5.png)