let lastIndent = 0;
let bookInfo;
let allTab;


const tabs = document.querySelector('.tabs');

const insertBefore = (el) => tabs.insertAdjacentHTML('afterbegin', el);
const insertAfter = (el) => tabs.insertAdjacentHTML('beforeend', el);

const startRead = (type) => {
  bookInfo = JSON.parse(Android.getBook(book_id));
  tabs.innerHTML = "";

  if(type == "listen"){
      bookInfo.lastTab = 0;
      Android.startReader(book_id, 0);
  }
  const tab = Android.getTab(book_id, bookInfo.lastTab);

  insertAfter(tab);

  if (bookInfo.lastTab == 0) {
    insertBefore(`<div class="book_info"><h2 class="title">${bookInfo.title}</h2><h4 class="author">${bookInfo.author}</h4></div>`);
  }else{
    tabs.scrollTop+=30;
  }

  tabs.addEventListener('scroll', () => {
  let heightMax = 0;
  let mainEl;

  allTab = Array.from(tabs.querySelectorAll(".tab"));
  allTab.forEach((item,i) => {
    const rect = item.getBoundingClientRect();
    if (rect.bottom > 0 && rect.top < tabs.clientHeight) {
        let height;
        if (0 > rect.top) {
            height = rect.bottom;
        } else {
            height = tabs.clientHeight - rect.top;
        }

        if (heightMax < height) {
            mainEl = i;
            heightMax = height;
        }
    }
})

if (tabs.scrollTop + tabs.clientHeight > tabs.scrollHeight - 300) {
    const newIdTab = Number(allTab[mainEl].dataset.id) + 1;
    Android.setLastReadingBookTab(book_id, newIdTab);
    const tab = Android.getTab(book_id,newIdTab);
    insertAfter(tab);
    } else if (tabs.scrollTop < 300) {
      const newIdTab = Number(allTab[mainEl].dataset.id) - 1;
      if (newIdTab >= 0) {
        const tab = Android.getTab(book_id,newIdTab);
        insertBefore(tab);
        if (newIdTab == 0) {
          insertBefore(`<div class="book_info"><h2 class="title">${bookInfo.title}</h2><h4 class="author">${bookInfo.author}</h4></div>`);
        }
      }
    }

    if (mainEl != 0) {
      allTab.splice(mainEl-1,3);
      const book_info_el = document.querySelector('.book_info');
      if(book_info_el && mainEl-1 > 0) book_info_el.remove();
    } else {
      allTab.splice(mainEl,2);
    }
    allTab.forEach(item=>item.remove());
  })
const map = (x, in_min, in_max, out_min, out_max)=>
{
  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
}
    const slider = document.querySelector(".slider-container .slider");
    const slider_progress = document.querySelector(".slider-container .fill");
    slider.addEventListener("input", ()=>{
        tabs.style.fontSize = slider.value + 'px';
        slider_progress.style.width = `${map(slider.value, 14, 22, 0, 100) + 0.3}%`;
        updateTooltip(activeTooltip);
    })
}
let lastSentence;
const listener = (event) => {
  const json = JSON.parse(event.data);
  if (json.type == "highlight") {

    const currentSentence = document.querySelector(`.tab[data-id="${json.data[0]}"] div.indent[data-id="${json.data[1]}"]`);
    if(lastSentence != currentSentence){
        currentSentence.style.background = "rgba(96, 183, 241, 0.2)";
        currentSentence.scrollIntoView({ behavior: "smooth", block: "center" });
        if(lastSentence) lastSentence.style = ""
        lastSentence = currentSentence;
    }
  }else{
       book_id = json.id;
       startRead(json.type);
     }
}

if (window.addEventListener) {
  window.addEventListener("message", listener);
} else {
  window.attachEvent("onmessage", listener);
}
