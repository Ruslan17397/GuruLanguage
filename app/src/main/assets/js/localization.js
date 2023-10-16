const localization_json = {
   "uk":{
        "profile":"Профіль",
        "library":"Бібліотека",
        "genres":"Жанри",
        "newbook":"Новинки",
        "game":"Ігри",
        "word":"Слова",
        "brain":"Навчання",
         "books":"Книги",
         "more":"Ще",
         "progressbar":"До нового рівня залишилося ...",
         "repeat":"Повторити",
         "learn":"Вчити",
         "recomendate":"Для кращого результату вчи по 10 слів на день",
         "goaloftheday":"Мета дня",
         "change":"Змінити",
         "congratuation":"Успіх!",
         "listen": "Слухати",
         "read": "Читати",
         "Pets":"Тварини",
         "Birds":"Птиці",
         "continuereading": "Продовжити читати",
         "days": ["Понеділок", "Вівторок", "Середа", "Четвер", "П'ятниця"],
         "learnTeleg":"Більше в нашому телеграм боті!\r@GuruBot",
         "lvl1":"Хто я?",
         "lvl2":"Невже я дійшов до наступного рівня?",
         "lvl3":"Я так швидко збираю знання, як Губка Боб воду",
         "lvl4":"Все, готую валізу для світового подорожування",
         "lvl5":"\"Guru Language\", я досяг твого рівня, дякую!",
   }
};
let country_code = Android.getCountryCode();
let language_pack = localization_json[country_code];
//let country_code = "es";
document.querySelectorAll('[data-localization]').forEach(item=>{
   item.innerText = language_pack[item.dataset.localization];
});