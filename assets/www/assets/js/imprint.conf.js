/*!
 * IMPRINT Gocin 0.02.15.1
 * Copyright 2015 Nur Maulidiyah.
 * Nur.Maulidiyah20@yahoo.co.id
 */
 
 /* API SERVICES */
 var ws_host    = "http://192.168.1.101/API/app_api.php";
 var ws_timeout	= 10000;
 
 /* GCM SERVICES */
 var SenderID			= "526266735061";
 var RegID;
 var pushNotification;
 var isreg;
 var isunreg;
 var islogin;
 var unread_notif = 0;
 
 if (localStorage) {
	if(localStorage.unread){
		unread_notif = localStorage.unread;
	}
 }
 
 /* CAMERA AND FEAT ACCESS */
  var pictureSource;
  var destinationType;
  
 /* GEOLOCATION*/
 var isgeoloc;
 var longitude;
 var latitude;

 