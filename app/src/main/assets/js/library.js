const genre_filter_wrap = document.querySelector('.genre_filter ul');

const blur_bg = document.querySelector(".blur_background");

const description_book_read = document.querySelector(".description_book_read");
const description_book_listen = document.querySelector(".description_book_listen");
const description_poster = document.querySelector(".book_image_description img");
const author_description = document.querySelector(".author_description");
const title_description = document.querySelector(".title_description");
const description_book_text = document.querySelector(".description_book_text");

let genresItems;
getInfo = (item) => {
     parent.postMessage("full", "*");
     description_book_read.dataset.id = item.id;
     description_poster.src = item.poster;
     author_description.innerText = item.author;
     title_description.innerText = item.title;
     description_book_text.innerText = Android.getDescription(item.id, `http://english.ho.ua/admin/books/${item.path}/description_${country_code}.txt`);

     blur_bg.classList.add("active");
     document.querySelector(".book_description").classList.add("active");
 }

const loadLastReadingBook = () => {
  const continueReading = Android.continueReading();

  if (continueReading != "empty") {
    const continueReadingData = JSON.parse(continueReading);
    if(document.querySelector(".continue-read-wrap").classList.contains("hide")){
         document.querySelector(".continue-read-wrap").classList.remove("hide");
    }
    document.querySelector(".read_progress div").style.width =`${continueReadingData.lastTab / continueReadingData.tabs * 100}%`;
    document.querySelector(".continue-read").onclick = () => getInfo(continueReadingData);
    document.querySelector(".continue-read .continue-read-image img").src = continueReadingData.poster;
    document.querySelector(".continue-read .continue-read-description .continue-read-title").innerText = continueReadingData.title;
    document.querySelector(".continue-read .continue-read-description .continue-read-author").innerText = continueReadingData.author;
    document.querySelector(".continue-read .continue-read-description .continue-read-short").innerText = Android.getDescription(continueReadingData.id, `http://english.ho.ua/admin/books/${continueReadingData.path}/description.txt`).split(" ").slice(0, 20).join(" ")+"...";
  }else{
     document.querySelector(".continue-read-wrap").classList.add("hide");
  }
};
const genreFilter = (genre_card, genre_id) =>{
    genresItems.forEach((item)=>{
        if (item.dataset.id == genre_id) {
            item.classList.remove('hide');
        }
        else {
            item.classList.add('hide');
        }
    })

    document.querySelector('.genre_filter .category_head .category_poster img').src = genre_card.querySelector('.genre-card-image img').src;
    document.querySelector('.genre_filter .category_head .category_name').innerText = genre_card.querySelector('.genre-card-title').innerText;
    document.querySelector('.genre_filter').classList.add('active');
}

const loadGenres = () =>{
    const genres = JSON.parse(Android.getData(`http://english.ho.ua/get_genres.php?lang=${country_code}`));
    const genres_el = document.querySelector('.genres_data');
    genres.forEach((item)=>{
        genres_el.insertAdjacentHTML('beforeend', `<div class="genre-card" onclick="genreFilter(this, ${item.id})"><div class="genre-card-image"><img src="http://english.ho.ua/genre/${item.image}" alt=""></div><div class="genre-card-title">${item[country_code]}</div></div>`);
    })
    }

const startRead = (id, type) => {
  Android.setLastReadingBook(id);
  document.querySelector(".read iframe").contentWindow.postMessage(JSON.stringify({"type": type, "id": id}), "*");
  document.querySelector(".read").classList.add("active");
  document.querySelector(".book_description").classList.remove("active");
  blur_bg.classList.remove("active");
  document.querySelector(".loadBookProgress").classList.remove("active");
  loadLastReadingBook();
};

description_book_read.addEventListener('click', e => {
  document.querySelector(".loadBookProgress").classList.add("active");

  if (Android.downloadBook(description_book_read.dataset.id, country_code)) {
    startRead(description_book_read.dataset.id, 'load');
  }

});
description_book_listen.addEventListener('click', e => {
  document.querySelector(".loadBookProgress").classList.add("active");

  if (Android.downloadBook(description_book_read.dataset.id, country_code)) {
    startRead(description_book_read.dataset.id, 'listen');
  }

});



loadLastReadingBook();
loadGenres();