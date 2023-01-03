async function loadReviews(HotelId, hotelName, username, isNext){
    let response = await fetch('/getReviews?hotelId='+ HotelId+'&isNext='+ isNext, {method :'get'});
    let output = await response.json();

    let body = "";
    if(output == null){
        body = "<tr>"+
            "<td>-----No reviews Found----</td>"+
        "</tr>";
    }
    else{
        let head = "<tr>"+
            "<th scope=\"col\">Title</th>"+
            "<th scope=\"col\">Text</th>"+
            "<th scope=\"col\">Submission time</th>"+
            "<th scope=\"col\">Overall rating</th>"+
            "<th scope=\"col\">Username</th>"+
        "</tr>";
        document.getElementById("head1").innerHTML = head;

        for(let review in output.reviews){
            body = body + "<tr>"+
            "<td>"+output.reviews[review].title+"</td>"+
            "<td>"+output.reviews[review].reviewText+"</td>"+
            "<td>"+output.reviews[review].date+"</td>"+
            "<td>"+output.reviews[review].overallRating+"</td>"+
            "<td>"+output.reviews[review].user+"</td>"+
            "</tr>";
        }
    }
    document.getElementById("body").innerHTML = body;

    let fetchResponse = await fetch('/getFavoriteHotel?hotelId='+ HotelId, {method :'get'});
    let isHotelFavorite = await fetchResponse.text();
    isHotelFavorite = isHotelFavorite.trim();
    if(isHotelFavorite == "false"){
        document.getElementById("favoriteButton").innerHTML = "<button type=\"button\" class=\"btn btn-outline-danger\""+
                "onclick=\"addHotelToFavorites(\'"+username+"\',"+HotelId+",\'"+hotelName+"\');\">"+
                "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\""+
                "class=\"bi bi-heart-fill red-color\" viewBox=\"0 0 16 16\">"+
                "<path fill-rule=\"evenodd\""+
                "d=\"M8 1.314C12.438-3.248 23.534 4.735 8 15-7.534 4.736 3.562-3.248 8 1.314z\"></path>"+
                "</svg>"+
                "Add to Favorites"+
                "</button>";
    }
    else if(isHotelFavorite == "true"){
        document.getElementById("favoriteIcon").innerHTML = "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"30\" height=\"30\" fill=\"currentColor\" class=\"bi bi-heart-fill red-color\" viewBox=\"0 0 16 16\">"+
           "<path fill-rule=\"evenodd\" d=\"M8 1.314C12.438-3.248 23.534 4.735 8 15-7.534 4.736 3.562-3.248 8 1.314z\"/>"+
           "</svg>";
    }
}

async function showWeather(latitude,longitude){
let response = await fetch("https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&current_weather=true", {method :'get'})
let output = await response.json();
let weatherbody = "<b>Temperature:</b>"+output.current_weather.temperature+"</br>"+
"<b>Time:</b>"+output.current_weather.time+"</br>"+
"<b>Wind Direction:</b>"+output.current_weather.winddirection+"</br>"+
"<b>Wind Speed:</b>"+output.current_weather.windspeed+"</br>";
document.getElementById("weather").innerHTML = weatherbody;
}

async function addHotelToFavorites(username,hotelId,hotelName){
//favoriteButton
console.log("IN addHotelToFavorite");
document.getElementById("favoriteIcon").innerHTML = "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"30\" height=\"30\" fill=\"currentColor\" class=\"bi bi-heart-fill red-color\" viewBox=\"0 0 16 16\">"+
   "<path fill-rule=\"evenodd\" d=\"M8 1.314C12.438-3.248 23.534 4.735 8 15-7.534 4.736 3.562-3.248 8 1.314z\"/>"+
   "</svg>";
document.getElementById("favoriteButton").innerHTML = "";
let response = await fetch('/addHotelToFavorites?hotelId='+hotelId+'&hotelName='+hotelName,{method :'get'})
}

async function addToHistoryTable(username, hotelId, expedialink){
    let response = await fetch('/insertIntoHistoryTable?hotelId='+ hotelId+'&username='+ username +'&expedialink='+expedialink, {method :'get'});
}