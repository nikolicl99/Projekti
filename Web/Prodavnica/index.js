const container = document.getElementById('akcija');
// Element iz HTML se smesta u promenjivu
let data = JSON.parse(localStorage.getItem('DatabaseData')) || null
// Smesta se u promenjivu data local storage

if(data) {
    // Ako postoji data onda se na osnovu nje kreiraju kartice
    renderCards(data.Proizvodi);   
}
else{
fetch('./Database.json')
    .then(response => response.json())
    .then(initialData => {
        // Ako ne postoji data onda se data kreira na osnovu JSON baze
        data = initialData
        renderCards(data.Proizvodi);
    });
}

function renderCards(proizvodi) {
    // U promenjivu se smestaju samo oni proizvodi iz baze kojima je vrednost kolone "Akcija" = 1
    const akcijskiProizvodi = proizvodi.filter(proizvod => proizvod.Akcija === 1);

    akcijskiProizvodi.forEach((proizvod, index) => {
        // Za svaki proizvod se kreira kartica
        const card = createCard(proizvod);
        if (index === 0) {
            // Prvi proizvod u listi se postavlja kao aktivan i on se prvi prikazuje
            card.classList.add('active');
        }
        // Kreirana kartica se smesta u HTML element
        container.appendChild(card);
    });
}

function createCard(proizvod) {
    // Kreiraju se elementi u HTML i dodaju se klase 
    const card = document.createElement('div');
    card.classList.add('carousel-item', 'col-lg-3', 'col-md-5', 'col-sm-12');
    card.setAttribute('data-id', proizvod.IDProizvoda);
    // setAttribute se koristi za dodavanje drugih elemenata unutar tagova

    const link = document.createElement('a');
    link.setAttribute('style', 'text-decoration: none; color: #001F3F;');
    link.href = `proizvod.html?id=${proizvod.IDProizvoda}`;
    link.classList.add('stretched-link');

    const naziv = document.createElement('p');
    naziv.setAttribute('style', 'text-align: center;')
    naziv.textContent = proizvod.Naziv;
    // Postavlja se tekst za tag <p>

    const img = document.createElement('img');
    img.classList.add('d-block', 'w-100');
    img.alt = proizvod.Naziv;

    if (proizvod.IDProizvoda) {
        img.src = `img/${proizvod.IDProizvoda}.png`;
    } else {
        img.src = 'img/Null.jpg';
    }

    card.appendChild(img);
    card.appendChild(naziv);
    card.appendChild(link);
    // Ubacuju se svi kreirani elementi u karticu
    return card;
}


