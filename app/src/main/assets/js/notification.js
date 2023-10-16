let notify = (text, mode)=>{
let toast_type = ".toast";
if(mode) toast_type+=".success";
else toast_type+=".error";
document.querySelector(`${toast_type} .toast-text`).innerText = text;
document.querySelector(toast_type).classList.add('active');
setTimeout(()=>{
document.querySelector(toast_type).classList.remove('active');
},2000);
}