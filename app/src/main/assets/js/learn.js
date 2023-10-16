let instance;
let stageId = 0;
let slide_id_num;
let isRepeat = false;
let stagesHTML = [
    [],
    [],
    [],
    []
];
let slide_id = document.querySelector('.slide-id');
let CarouseItem = document.querySelectorAll('.carousel-item');
let learn_wrap = document.querySelector('.learn_wrap');
let congratuation_wrap =document.querySelector('.congratuation_wrap');
let continue_button =document.querySelector('.congratuation_wrap button');
let words;
setWords = (json) => {
    stageId = 0;
    stagesHTML = [
        [],
        [],
        [],
        []
    ];
    parent.postMessage("full", "*");
    instance.set(1);
    instance.set(0);
    words = Object.fromEntries(
        Object.entries(json).slice(0, 5)
    );

    nextStage();
    learn_wrap.classList.add('active');
}
setupLearn = () => {
    if(parseInt(learn_button.innerText) < 5){
        more_word_alert.classList.add('active');
        blur_bg.classList.add('active');
        return;
    }
    setWords(wordJson.learn);
    isRepeat = false;
}
setupRepeat = () => {
if(parseInt(repeat_button.innerText) < 5){
        more_word_alert.classList.add('active');
        blur_bg.classList.add('active');
        return;
    }
    setWords(wordJson.repeat);
    isRepeat = true;
}
const shuffleArray = array => {
    for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        const temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
    return array;
}
Congratuation = () => {
    let count = 200;
    let defaults = {
        origin: {
            y: 0.7
        }
    };
    congratuation_wrap.classList.add('active');

    function fire(particleRatio, opts) {
        confetti(Object.assign({}, defaults, opts, {
            particleCount: Math.floor(count * particleRatio)
        }));
    }

    fire(0.25, {
        spread: 26,
        startVelocity: 55,
    });
    fire(0.2, {
        spread: 60,
    });
    fire(0.35, {
        spread: 100,
        decay: 0.91,
        scalar: 0.8
    });
    fire(0.1, {
        spread: 120,
        startVelocity: 25,
        decay: 0.92,
        scalar: 1.2
    });
    fire(0.1, {
        spread: 120,
        startVelocity: 45,
    });
    Android.xpUp(100);
    let originalWords = Object.keys(words);
    if(isRepeat)for (let i = 0; i < originalWords.length; ++i) Android.removeWordFromRepeat(originalWords[i])
    else for (let i = 0; i < originalWords.length; ++i) Android.moveWordToRepeat(originalWords[i])

    update()
}

generateCard = (property, wordDataStr) => {
    let wordData = JSON.parse(wordDataStr);
    stagesHTML[0].push(`<div class="flip-card"><div class="flip-card-inner" onclick="this.classList.toggle('active');"><div class="flip-card-front"><div class="card-inf"><div class="card-word">${property}</div><div class="card-transciption">${wordData[1]}</div></div></div><div class="flip-card-back"><div class="translate">${wordData[0]}</div></div></div><div class="rotate-ico center"><img src="image/switch.png" alt=""></div></div>`);
}
generateSymbol = (property, word) => {
    let full = `<div class="sw"><div class="symbol-item" data-id="0" data-len="${property.length-1}" onclick="setSymbol(event,this);">`;
    let btn = '<div class="symbol-buttons-wrap">';
    let input = '<div class="symbol-input-wrap">';
    shuffleArray(Array.from(property)).forEach((item) => {
        btn += `<div class="symbol-button">${item}</div>`;
        input += '<div class="symbol-input"></div>';
    });
    btn += "</div>";
    input += "</div>";
    full += input;
    full += btn;
    full += "</div></div>";
    stagesHTML[1].push(full);
}

generateOriginalTable = (word) => {
    let origHTML = `<div class="original-item" onclick="verifyOriginalTable(event,this);">
     		<div class="original-word">${word}</div>`;
    let shuffleValues = shuffleArray(Object.values(words));
    shuffleValues.forEach(item => {
        origHTML += `<div class="answer-word">${JSON.parse(item)[0]}</div>`;
    });
    origHTML += '</div>';
    stagesHTML[2].push(origHTML);
}
generateTranslatedTable = (word) => {
    let origHTML = `<div class="original-item" onclick="verifyTranslateTable(event,this);">
     		<div class="original-word">${JSON.parse(word)[0]}</div>`;
    let shuffleKeys = shuffleArray(Object.keys(words));
    shuffleKeys.forEach(item => {
        origHTML += `<div class="answer-word">${item}</div>`;
    });
    origHTML += '</div>';
    stagesHTML[3].push(origHTML);
}
generateConformity = () => {
    document.querySelector('.stage div').innerText = (stageId + 1) + "/5"
    let shuffleValues = shuffleArray(Object.values(words));
    let shuffleKeys = shuffleArray(Object.keys(words));
    let confHTML = '<div class="conformity-item" onclick="verifyConformity(event,this);" data-id="0">';
    let orig = '<div class="orig">';
    let transl = '<div class="transl">';
    shuffleKeys.forEach((item, i) => {
        orig += `<div class="orig-item">${item}</div>`;
        transl += `<div class="transl-item">${JSON.parse(shuffleValues[i])[0]}</div>`;
    });
    orig += '</div>';
    transl += '</div>';
    confHTML += orig;
    confHTML += transl;
    confHTML += '</div>';
    CarouseItem[0].innerHTML = confHTML;
}

let isOrigin = false;
let isTransl = false;

let originEl;
let translEl;
verifyConformity = (e, el) => {
    if (e.target.classList == "orig-item") {
        if (isOrigin) originEl.classList.remove('active')
        isOrigin = true;
        originEl = e.target;
        originEl.classList.add('active');
    }
    if (e.target.classList == "transl-item") {
        if (isTransl) translEl.classList.remove('active')
        isTransl = true;
        translEl = e.target;
        translEl.classList.add('active');
    }
    if (isOrigin && isTransl) {
        isOrigin = false;
        isTransl = false;
        originEl.classList.remove('active');
        translEl.classList.remove('active');
        if (JSON.parse(words[originEl.innerText])[0] == translEl.innerText) {
            el.dataset.id++;
            if (el.dataset.id == 5) {
                Congratuation();
            }
            originEl.classList.add('success');
            translEl.classList.add('success');
        } else {
            originEl.classList.add('error');
            translEl.classList.add('error');
            setTimeout(() => {
                originEl.classList.remove('error');
                translEl.classList.remove('error');
            }, 200);
        }
    }
}
verifyOriginalTable = (e, el) => {
    if (e.target.classList == "answer-word") {
        let origEl = el.querySelector('.original-word');
        if (e.target.innerText != JSON.parse(words[origEl.innerText])[0]) {} else if (slide_id_num == 5) eventStage();
        else instance.next();
    }
}
verifyTranslateTable = (e, el) => {
    if (e.target.classList == "answer-word") {
        let origEl = el.querySelector('.original-word');
        if (JSON.parse(words[e.target.innerText])[0] != origEl.innerText) {} else if (slide_id_num == 5) eventStage();
        else instance.next();
    }
}
setSymbol = (e, el) => {
    if (e.target.classList == "symbol-button") {
        e.target.classList.add('active');
        let input_child = el.querySelector('.symbol-input-wrap').children;
        input_child[el.dataset.id].innerText = e.target.innerText;
        if (el.dataset.id == el.dataset.len) {
            let user_word = el.querySelector('.symbol-input-wrap').innerText.replace(/\r?\n/g, "");
            if (!words.hasOwnProperty(user_word)) {
                Array.from(el.querySelector('.symbol-buttons-wrap').children).forEach((item, i) => {
                    input_child[i].innerText = "";
                    item.classList.remove('active');
                    el.dataset.id = 0;
                })

            } else if (slide_id_num == 5) eventStage();
            else {
                instance.next();
            }
        } else {

            el.dataset.id++;
        }
    }

}
nextStage = () => {
    document.querySelector('.stage div').innerText = (stageId + 1) + "/5"

    for (let property in words) {
        switch (stageId) {
            case 0:
                draggAllow = true;
                generateCard(property, words[property]);
                break;
            case 1:
                draggAllow = false;
                generateSymbol(property, words[property]);
                break;
            case 2:
                generateOriginalTable(property);
                break;
            case 3:
                generateTranslatedTable(words[property]);
                break;
        }
    }

    stagesHTML[stageId].forEach((item, i) => {
        let wrap = document.createElement('div');
        wrap.innerHTML = item;
        CarouseItem[i].innerHTML = wrap.outerHTML;
        // CarouseItem[i].style="";
    })

    stageId++;
}
eventStage = () => {
    let count = 200;
    slide_id.removeEventListener('click', eventStage);
    instance.set(0);
    if (stageId != 4) {
        nextStage();
    } else generateConformity();
}

continue_button.addEventListener("click", ()=>{
    congratuation_wrap.classList.remove("active");
    closeLearnElement();
})


let elem = document.querySelector('.carousel');
document.addEventListener('DOMContentLoaded', function() {
    instance = M.Carousel.init(elem, {
        fullWidth: true,
        noWrap: true,
        onCycleTo: (el) => {
            if (el.dataset.id == 5 && stageId == 1) {
                slide_id.innerText = "NEXT";
                slide_id.addEventListener('click', eventStage);
            } else if (stageId != 5) {
                slide_id_num = el.dataset.id;
                slide_id.innerText = slide_id_num + "/5";
            } else {
                slide_id.innerText = "1/1";
            }
        }
    });

});