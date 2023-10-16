
var iscon = false;
Offline.options = {checks: {xhr: {url: 'http://english.ho.ua/check_internet.php'}}};

var run = function(){
  var req = new XMLHttpRequest();
  req.timeout = 5000;
  req.open('GET', 'http://english.ho.ua/check_internet.php', true);
  req.send();


if(Offline.state=="up"){
clearInterval(connect);
if(iscon){
window.location.href="index.html";
}
}else if(Offline.state=="down"){
iscon=true;
document.querySelector(".wrap_int").style.display = "block";
//	document.querySelector("white").style.display = "block";
}
}

var connect = setInterval(run, 3000);


Offline.on("up",function(){
if(iscon){
window.location.href="index.html";
}
	
})

Offline.on("down",function(){
iscon=true;
document.querySelector(".wrap_int").style.display = "block";
	//document.querySelector("white").style.display = "block";
})

