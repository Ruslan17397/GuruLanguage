let buttonenabled = true, scroll = 0;

document.querySelector('.darkmode').addEventListener("click", function(){
	if(!buttonenabled) return;
	buttonenabled = false;
	document.querySelector(".clip").insertAdjacentHTML('afterbegin', document.querySelector("main").outerHTML);
	scrollbind(document.querySelector(".clip main .tabs"));
	document.querySelector(".clip main").classList.toggle("dark");
//	document.querySelector(".clip main .slider").value = document.querySelector("main .slider").value;
	document.querySelector(".darkmode").classList.toggle("dark");
	document.querySelector(".clip main .tabs").scrollTo({top:scroll});
	document.querySelector(".clip").classList.add("anim");

	setTimeout(function(){
		document.querySelector("main").classList = document.querySelector(".clip main").classList;
		scrollbind(document.querySelector("main .tabs"));
		document.querySelector("main .tabs").scrollTo({top:scroll});
		document.querySelector(".clip").innerHTML = "";
		document.querySelector(".clip").classList.remove("anim");
		buttonenabled = true;
	}, 1000);
});
 
const scrollbind = el => el.addEventListener("scroll", function(){
	scroll = this.scrollTop;
	if(document.querySelector("main").length > 1)
		document.querySelector("main .tabs").scrollTo({top:scroll}); 
		
});
scrollbind(document.querySelector("main .tabs"));