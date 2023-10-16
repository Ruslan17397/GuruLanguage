    let irregulars = JSON.parse(Android.getIrregulars());
    let irregular_card = document.querySelector('.card.card-1');
    let irregular_wrap = document.querySelector('.irregular_wrap');
    let irregular_wrap_back = document.querySelector('.irregular_wrap .search_bar .middle i')
    irregular_card.addEventListener('click', ()=>{
        irregular_wrap.classList.add('active');
        parent.postMessage("full", "*");
    })
    irregular_wrap_back.addEventListener('click', ()=>{
        irregular_wrap.classList.remove('active');
        parent.postMessage("min", "*");
    })

 let filter_bar = document.querySelector('.filter_bar');
    let letters = document.querySelectorAll('.letter');
    let table = document.querySelector('table');
    let details = document.querySelector('div.details');
    let details_lvl = details.querySelector('.lvl');
    let details_infinitive = details.querySelector('span');
    let sim_part = details.querySelector('.sim_part');
    let detils_example = details.querySelector('.example .text');
    let details_close = details.querySelector('.close');


details.addEventListener('click', (e)=>{
    if(e.target.classList.contains('details') || e.target.classList.contains('close')){
    details.classList.remove('active');
}
})

    filter_bar.addEventListener('click', (e)=>{
        let tds = document.querySelectorAll('table tbody');
        if(e.target.classList == 'letter'){
            letters.forEach((item)=>{
                item.classList.remove('active');
            });
            e.target.classList.add('active');
            tds.forEach((item)=>{
                item.style.display = "table-row-group";
                if (!item.querySelector('tr td').innerText.startsWith(e.target.innerText.toLowerCase()))item.style.display = "none";
            });
        }else if(e.target.classList == 'letter active'){
            e.target.classList.remove('active');
            tds.forEach((item)=>{
                item.style.display = "table-row-group";
            })
        }
    })
json2html = ()=>{
    irregulars.forEach((item)=>{
        let tbody = document.createElement('tbody');
        tbody.addEventListener('click', (e)=>{
            details.classList.add('active');
            details_lvl.classList.replace(details_lvl.classList[1], item['Level']);
            details_infinitive.innerText = item['Infinitive'];
            sim_part.innerText = `${item['Past Simple']} / ${item["Past Participle"]}`;
            detils_example.innerText = item['Example'];
        })
        tbody.classList = 'irregular_item';
        tbody.innerHTML = `<tr>
                <td><div class="lvl ${item['Level']}"></div>${item['Infinitive']}</td>
                <td>${item['Past Simple']} / ${item["Past Participle"]}</td>
            </tr>
            <tr><td>${item["Translate"]}</td></tr>`;
        table.appendChild(tbody)
    })
}
   json2html();