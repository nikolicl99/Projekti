document.addEventListener('DOMContentLoaded', function () {
    let currentSelectedProductID = null;

    // Hvata se url fajla i na osnovu njega se gleda id proizvoda
    const urlParams = new URLSearchParams(window.location.search);

    // ID sa kraja url-a se smesta u promenjivu
    const urlProductID = urlParams.get('id');

    // Proverava se da li je ID null ili nije broj; ako jeste smesta se u promenjivu, ako nije ispisuje poruku u konzoli
    if (urlProductID !== null && !isNaN(urlProductID)) {
        currentSelectedProductID = parseInt(urlProductID);
        console.log('Selektovani proizvod:', currentSelectedProductID);
    } else {
        console.log('ID proizvoda nije pronađen.');
        return;
    }

    fetch('./Database.json')
        .then(response => response.json())
        .then(data => {
            // Na osnovu ID-ja se prolazi kroz bazu i trazi se odgovarajuci proizovd
            const selectedProduct = data.Proizvodi.find(proizvod => proizvod.IDProizvoda === currentSelectedProductID);

            if (selectedProduct) {
                // Ako postoji taj proizvod onda se informacije o njemu ispisuju
                const nazivElement = document.getElementById('nazivProizvoda');
                const cenaElement = document.getElementById('cenaProizvoda');
                const opisElement = document.getElementById('opisProizvoda');
                const imgElement = document.getElementById('slikaProizvoda');
                const korpaButton = document.getElementById("korpa");

                nazivElement.textContent = selectedProduct.Naziv;
                cenaElement.textContent = `Price: ${selectedProduct['Cena']} RSD`;
                opisElement.textContent = `${selectedProduct.Opis || 'Proizvod nema opis'}`;

                // Na osnovu ID-ja proizvoda se gleda naziv slike
                if (selectedProduct.IDProizvoda) {
                    imgElement.src = `img/${selectedProduct.IDProizvoda}.png`;
                } else {
                    imgElement.src = 'img/Null.jpg';
                }

                // Provera da li se proizvod nalazi u korpi
                const isProductInCart = isInCart(selectedProduct.IDProizvoda);
                updateCartButton(isProductInCart);

                korpaButton.addEventListener("click", function () {
                    if (!isProductInCart) {
                        // Ako nije onda se dodaje
                        addToCart(selectedProduct);
                        updateCartButton(true);
                        console.log(`Proizvod ${selectedProduct.IDProizvoda} je dodat u korpu.`);
                    } else {
                        console.log('Proizvod je već u korpi.');
                    }
                });
                // Ako ne postoji proizvod sa tim ID-jem onda se ispisuje poruka
            } else {
                console.log('Proizvod nije pronađen.');
            }
        })
        .catch(error => console.error('Error fetching data:', error));

    // Učitaj podatke iz localStorage-a
    const cart = JSON.parse(localStorage.getItem('cart')) || [];

    // Ažuriraj prikaz korpe nakon promene podataka
    updateCartButton(cart.includes(currentSelectedProductID));

    function isInCart(productID) {
        // Provera da li se nalazi u korpi
        return cart.includes(productID);
    }

    function addToCart(product) {
        // Dodaj proizvod u korpu
        cart.push(product.IDProizvoda);

        // Ažuriraj vrednost "Korpa" u localStorage-u
        localStorage.setItem('cart', JSON.stringify(cart));
    }

    // Azurira se dugme u odnosu na to da li se proizvod nalazi u korpi
    function updateCartButton(isProductInCart) {
        const korpaButton = document.getElementById("korpa");

        if (isProductInCart) {
            korpaButton.textContent = "Proizvod je već dodat";
            korpaButton.disabled = true;
        } else {
            korpaButton.textContent = "Dodaj u korpu";
            korpaButton.disabled = false;
        }
    }
});
