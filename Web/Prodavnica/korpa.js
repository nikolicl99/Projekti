document.addEventListener('DOMContentLoaded', function () {
    const tableBody = document.getElementById('korpaTableBody');

    fetch('./Database.json')
        .then(response => response.json())
        .then(initialData => {
            let cart = JSON.parse(localStorage.getItem('cart')) || [];
            // Cart je niz koji sadrzi sve proizvode koji se nalaze u korpi

            renderCards(initialData.Proizvodi);

            function createCard(proizvod) {
                // Kreiranje HTML elemenata koji su potrebni za kreiranje redova tabele
                const row = document.createElement('tr');
                row.dataset.id = proizvod.IDProizvoda;

                const nazivCell = document.createElement('td');
                nazivCell.textContent = proizvod.Naziv;

                const cenaCell = document.createElement('td');
                cenaCell.textContent = `${proizvod.Cena} RSD`;

                const slikaCell = document.createElement('td');
                const img = document.createElement('img');
                img.setAttribute('width', '150px;');
                img.alt = proizvod.Naziv;
                img.src = proizvod.IDProizvoda ? `img/${proizvod.IDProizvoda}.png` : 'img/Null.jpg';
                slikaCell.appendChild(img);

                const akcijaCell = document.createElement('td');
                const removeButton = document.createElement('button');
                removeButton.classList.add('btn', 'btn-light');
                removeButton.textContent = `Ukloni iz korpe`;

                removeButton.addEventListener('click', function () {
                    // Pronađite indeks proizvoda u korpi
                    const index = cart.indexOf(proizvod.IDProizvoda);

                    if (index !== -1) {
                        // Ako proizvod pronađen, ukloni ga iz korpe
                        cart.splice(index, 1);

                        // Ažurirajte podatke u localStorage
                        localStorage.setItem('cart', JSON.stringify(cart));

                        // Ažurirajte UI
                        renderCards(initialData.Proizvodi);
                    }
                });

                akcijaCell.appendChild(removeButton);

                row.appendChild(nazivCell);
                row.appendChild(cenaCell);
                row.appendChild(slikaCell);
                row.appendChild(akcijaCell);

                // Dodaju se kreirani elementi redu tabele

                return row;
            }

            function renderCards(proizvodi) {
                // Čisti tabelu pre nego što se dodaju nove kartice
                tableBody.innerHTML = '';
                
                // Promenjiva koja pamti ukupnu cenu proizvoda
                let suma = 0;

                proizvodi.forEach(proizvod => {
                    if (cart.includes(proizvod.IDProizvoda)) {
                        // Za svaki proizvod u korpi se dodaje njegova cena u sumu i kreira se red u tabeli
                        suma += proizvod.Cena;
                        const row = createCard(proizvod);
                        tableBody.appendChild(row);
                    }
                });

                // Ažuriranje ukupnu cenu na stranici
                updateTotal(suma);
            }

            function updateTotal(suma) {
                // Ažuriranje ukupne cene na stranici
                document.getElementById('ukupnaCena').textContent = `Ukupna cena: ${suma} RSD`;
            }
        })
        .catch(error => console.error('Greška pri dohvatu podataka:', error));
});
