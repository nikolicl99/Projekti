<!DOCTYPE html>
<html lang="sr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kulinarski Recepti</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600;700&display=swap" rel="stylesheet">


    <style>
        
body {
    background-color: #faf7f2;
    font-family: 'Poppins', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    color: #333;
    margin: 0;
    padding: 0;
    line-height: 1.6;
}

/* Navigacija */
.navbar {
    background: linear-gradient(90deg, #ff6f61, #ff9068);
    border-radius: 0;
    padding: 15px 0;
}

.navbar-brand {
    font-weight: 700;
    font-size: 2rem;
    color: #fff !important;
    letter-spacing: 1px;
}

/* Dugme za dodavanje */
.btn-success {
    background-color: #ff6f61;
    border: none;
    font-weight: 600;
    font-family: 'Poppins', sans-serif;
    padding: 10px 22px;
    border-radius: 10px;
    transition: all 0.3s ease;
}
.btn-success:hover {
    background-color: #ff6f61;
    transform: scale(1.05);
}

/* Header */
.header-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
}

.header-container h1 {
    font-size: 2.2rem;
    font-weight: 700;
    color: #333;
}

/* Kartice */
.card {
    border-radius: 15px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    transition: transform 0.2s ease, box-shadow 0.2s ease;
    overflow: hidden;
    background: #fff;
    border: none;
    height: 100%;
    padding: 20px;
}

.card:hover {
    transform: translateY(-6px);
    box-shadow: 0 8px 20px rgba(0,0,0,0.2);
}

.card-title {
    color: #ff6f61;
    font-weight: 600;
    font-size: 1.4rem;
    margin-bottom: 12px;
    font-family: 'Poppins', sans-serif;
}

.card-text {
    color: #555;
    font-size: 1rem;
    margin-bottom: 12px;
    font-family: 'Poppins', sans-serif;
}

/* Dugme unutar kartice */
.card .btn {
    font-family: 'Poppins', sans-serif;
    font-weight: 600;
    padding: 8px 18px;
    border-radius: 8px;
    transition: 0.3s ease;
}
.card .btn:hover {
    transform: translateY(-2px);
}

/* Footer */
footer {
    margin-top: 50px;
    padding: 20px 0;
    background-color: #ff6f61;
    color: white;
    text-align: center;
    font-size: 0.95rem;
    font-family: 'Poppins', sans-serif;
    font-weight: 500;
}

/* Grid razmak */
.row {
    margin-top: 20px;
    margin-bottom: 20px;
}

h1 {
    text-align: center;
}

.header-container h1 {
    flex: 1;                
    text-align: center;     
    margin: 0; 
    color: #ff6f61;  
}


</style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark mb-4">
        <div class="container">
            <a class="navbar-brand" href="/">Kulinarski Recepti</a>
        </div>
    </nav>

    <div class="container">
        @yield('content')
    </div>

    <footer>
        &copy; {{ date('Y') }} Kulinarski Recepti. Sva prava zadržana.
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
