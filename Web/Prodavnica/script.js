const container = document.getElementById('kartice');
const dropdownMenu = document.getElementById('meni');
const pretragaForma = document.getElementById('pretragaForma');
const unosPretrageInput = document.getElementById('unosPretrage');
// Elementi iz HTML se smestaju u promenjivu

// Uzimaju se podaci iz Local Storage
let data = JSON.parse(localStorage.getItem('DatabaseData')) || null;

// Funkcija za prikaz proizvoda
function renderCards(proizvodi) {
    container.innerHTML = ''; 
    // Očisti kontejner pre dodavanja novih kartica

    proizvodi.forEach(proizvod => {
        const card = createCard(proizvod);
        container.appendChild(card);
    });
}

// Funkcija za kreiranje kartice proizvoda
function createCard(proizvod) {
    const card = document.createElement('div');
    card.classList.add('card', 'col-lg-3', 'col-md-5', 'col-sm-12', 'karte');

    const link = document.createElement('a');
    link.setAttribute('style', 'text-decoration: none; color: #001F3F;');
    link.href = `proizvod.html?id=${proizvod.IDProizvoda}`;
    link.classList.add('stretched-link');

    const cardHeader = document.createElement('div');
    cardHeader.classList.add('card-header');
    cardHeader.textContent = proizvod.Naziv;

    const cardBody = document.createElement('div');
    cardBody.classList.add('card-body');

    const cardText = document.createElement('p');
    cardText.classList.add('card-text');
    cardText.textContent = `Cena: ${proizvod['Cena']} RSD`;

    const img = document.createElement('img');
    img.classList.add('img-fluid');
    img.alt = proizvod.Naziv;

    if (proizvod.IDProizvoda) {
        img.src = `img/${proizvod.IDProizvoda}.png`;
    } else {
        img.src = 'img/Null.jpg';
    }

    cardBody.appendChild(cardText);
    cardBody.appendChild(img);

    link.appendChild(cardHeader);
    link.appendChild(cardBody);
    card.appendChild(link);

    return card;
}

// Proverava Local Storage
if (data) {
    // Ako postoje podaci u Local Storage, koristi ih
    renderCards(data.Proizvodi);
} else {
    // Inače, izvrši fetch (ovo će se dogoditi samo prvi put ili ako se podaci obrišu iz Local Storage)
    fetch('./Database.json')
        .then(response => response.json())
        .then(initialData => {
            data = initialData;
            localStorage.setItem('DatabaseData', JSON.stringify(data));
            renderCards(data.Proizvodi);
        })
        .catch(error => console.error('Greška pri dohvatu podataka:', error));
}

// Event listener za pretragu pri svakom unosu
unosPretrageInput.addEventListener('input', function () {
    const unosPretrage = unosPretrageInput.value.toLowerCase();
    const filtriraniProizvodi = data.Proizvodi.filter(proizvod => proizvod.Naziv.toLowerCase().includes(unosPretrage));
    renderCards(filtriraniProizvodi);
});

// Poziva se metoda za svaki clan u tabeli kategorija
data.Kategorija.forEach(kategorija => {
    const menuItem = document.createElement('a');
    menuItem.classList.add('dropdown-item');
    menuItem.textContent = kategorija.Naziv_Kategorije;

    // Klikom na neku od kategorija se kreiraju samo oni proizvodi sa tom kategorijom (kategorija je kolona u JSON fajlu)
    menuItem.addEventListener('click', () => {
        const filtriraniProizvodi = data.Proizvodi.filter(proizvod => proizvod.Kategorija === kategorija.Naziv_Kategorije);
        renderCards(filtriraniProizvodi);
    });

    dropdownMenu.appendChild(menuItem);
});

const linija = document.createElement('div');
linija.classList.add('dropdown-divider');
dropdownMenu.appendChild(linija);

// Dodaj opciju "Prikaži sve" na kraj dropdown menija
const prikaziSveOption = document.createElement('a');
prikaziSveOption.classList.add('dropdown-item');
prikaziSveOption.textContent = 'Prikaži sve';

prikaziSveOption.addEventListener('click', () => {
    // Resetuj stranicu i prikaži sve proizvode
    renderCards(data.Proizvodi);
});

dropdownMenu.appendChild(prikaziSveOption);


