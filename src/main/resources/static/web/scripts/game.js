var app = new Vue({
    el: '#app',
    data: {

        gameView: {},
        gamePlayerId: "null",
        gameRows: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
        gameColumns: ["#", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"],
        mainPlayer: {},
        opponent: {},
        hitsOnMe: [],
        hitsMp: [],
        sinksMp: [],
        hitsOp: [],
        sinkOp: [],
        firstSquare: [],
        shipType: "",
        shipLocation: [],
        shipPreSave: [],
        myPreShipLocations: [],
        myPreShipTypes: [],
        squareToPaint: [],
        shipLength: 0,
        shipOrientation: "",
        email: {},
        pwd: {},
        thisPlayerShips: [],
        thisPlayerShipLocations: [],
        thisPlayerSalvoes: [],
        thisPlayerSalvoLocations: [],
        salvoSquare: "",
        salvoLocationsPreSave: [],
        salvoTurn: 0,
        shots: 0,

    },

    methods: {

        getViewVariables: function () {



            app.mainPlayer = app.gameView.gamePlayers.filter(x => x.id === app.gameView.id);
            app.opponent = app.gameView.gamePlayers.filter(x => x.id !== app.gameView.id);

            //getting hits and sinks info
            app.hitsMp = app.gameView.history.hits
            app.hitsOp = app.gameView.history.hitsOpponent;
            app.sinksMp = app.gameView.history.sinks;
            app.sinksOp = app.gameView.history.sinksOpponent;
            app.thisPlayerSalvoes = app.gameView.salvoes.filter(salvoes => salvoes.player == app.gameView.id);

            //hiding controls and adjusting display
            if (app.gameView.ships.length >= 5 || app.gameView.state.includes("OVER")) {

                document.getElementById("row-controllers").style.maxHeight = "10px";
                document.body.style.backgroundSize = "100% 130%";
                document.getElementById("shipControl").style.visibility = "hidden";
                document.getElementById("salvoControl").style.visibility = "hidden";
                document.getElementById("game-player-state").classList.add("blink_me");


            }

            if (app.gameView.state.includes("WAITING")) {
                document.getElementById("salvoControl").style.visibility = "hidden";
                document.getElementById("shipControl").style.visibility = "hidden";
            }

            if (app.gameView.state == "PLACE YOUR SHIPS") {
                document.getElementById("salvoControl").style.visibility = "hidden";
                document.getElementById("ships-grid").classList.add("selectedShip");
                document.getElementById("ships-grid-title").classList.add("blink_me");
                document.getElementById("shipControl").style.visibility = "visible";
            }

            if (app.gameView.state.includes("FIRE")) {
                document.getElementById("salvoes-grid-title").classList.add("blink_me");
                document.getElementById("salvoControl").style.visibility = "visible";
            }





        },

        findGamePlayerId: function () {
            const urlParams = new URLSearchParams(window.location.search);
            app.gamePlayerId = urlParams.get('gp');


        },

        toUpper(str) {
            return str.toUpperCase()
        },


        findGameView: function () {
            $.get('/api/game_view/' + app.gamePlayerId, function (data) {
                    app.gameView = data;

                })
                .done(function () {
                    app.getViewVariables();
                    app.findShipsToPaint();
                    app.findSalvoesToPaint();
                    app.getTurn();
                    app.getShotsforThisTurn();


                })
                .fail(function (error) {
                    alert(error);
                    console.log(error.responseJSON);
                    location.reload(true);

                });
        },

        paintShipsLocation: function (a, b) {
            let td = document.getElementById(a);
            td.classList.add(b);
            td.style.cursor = "not-allowed";

        },

        reversePaintShipsLocation: function (a) {
            let td = document.getElementById(a);
            td.classList = '';
            td.style.cursor = "cell";

        },

        updateShipControlDisplay() {
            let currentSelection = document.getElementsByClassName("carousel-item active")[0];

            function getSlide(shipType) {
                let slideId = ""

                if (shipType == "Aircraft Carrier") {
                    slideId = "carrier";
                } else if (shipType == "Battleship") {
                    slideId = "battleship";
                } else if (shipType == "Submarine") {
                    slideId = "submarine";
                } else if (shipType == "Destroyer") {
                    slideId = "destroyer";
                } else if (shipType == "Patrol Boat") {
                    slideId = "patrol";
                }
                return slideId;
            };


            if (app.shipType == "") {
                currentSelection.classList.remove("active");
                document.getElementById("slide0").classList.add("active");

            } else {

                let slide = ""

                if (app.shipType == "Aircraft Carrier") {
                    slide = "carrier";
                } else if (app.shipType == "Battleship") {
                    slide = "battleship";
                } else if (app.shipType == "Submarine") {
                    slide = "submarine";
                } else if (app.shipType == "Destroyer") {
                    slide = "destroyer";
                } else if (app.shipType == "Patrol Boat") {
                    slide = "patrol";
                }


                currentSelection.classList.remove("active");
                document.getElementById(slide).classList.add("active");
            }

        },

        selectShipType(value) {
            if (app.shipType == value) {
                app.shipType = "";


            } else {
                app.shipType = value

            }


        },

        findShipsToPaint: function () {
            app.gameView.ships.forEach(ship => {
                ship.locations.forEach(location => {

                    let orientationHorizontal = (ship.locations[0].charAt(0) == ship.locations[1].charAt(0));

                    if (ship.type == "Aircraft Carrier" || ship.type == "Aircraft") {
                        if (orientationHorizontal) {
                            if (location == ship.locations[0]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "ah1");
                            } else if (location == ship.locations[1]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "ah2");
                            } else if (location == ship.locations[2]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "ah3");
                            } else if (location == ship.locations[3]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "ah4");
                            } else if (location == ship.locations[4]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "ah5");
                            }
                        } else {
                            if (location == ship.locations[0]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "av1");
                            } else if (location == ship.locations[1]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "av2");
                            } else if (location == ship.locations[2]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "av3");
                            } else if (location == ship.locations[3]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "av4");
                            } else if (location == ship.locations[4]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "av5");
                            }
                        }
                    } else if (ship.type == "Battleship") {

                        if (orientationHorizontal) {
                            if (location == ship.locations[0]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "bh1");
                            } else if (location == ship.locations[1]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "bh2");
                            } else if (location == ship.locations[2]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "bh3");
                            } else if (location == ship.locations[3]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "bh4");
                            }
                        } else {
                            if (location == ship.locations[0]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "bv1");
                            } else if (location == ship.locations[1]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "bv2");
                            } else if (location == ship.locations[2]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "bv3");
                            } else if (location == ship.locations[3]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "bv4");
                            }
                        }


                    } else if (ship.type == "Submarine") {
                        if (orientationHorizontal) {
                            if (location == ship.locations[0]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "sh1");
                            } else if (location == ship.locations[1]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "sh2");
                            } else if (location == ship.locations[2]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "sh3");
                            }
                        } else {
                            if (location == ship.locations[0]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "sv1");
                            } else if (location == ship.locations[1]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "sv2");
                            } else if (location == ship.locations[2]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "sv3");
                            }
                        }

                    } else if (ship.type == "Destroyer") {
                        if (orientationHorizontal) {
                            if (location == ship.locations[0]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "dh1");
                            } else if (location == ship.locations[1]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "dh2");
                            } else if (location == ship.locations[2]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "dh3");
                            }
                        } else {
                            if (location == ship.locations[0]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "dv1");
                            } else if (location == ship.locations[1]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "dv2");
                            } else if (location == ship.locations[2]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "dv3");
                            }
                        }

                    } else if (ship.type == "Patrol Boat" || ship.type == "Patrol") {
                        if (orientationHorizontal) {
                            if (location == ship.locations[0]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "ph1");
                            } else if (location == ship.locations[1]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "ph2");
                            }
                        } else {
                            if (location == ship.locations[0]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "pv1");
                            } else if (location == ship.locations[1]) {
                                app.paintShipsLocation(
                                    ("ship" + location), "pv2");
                            }
                        }

                    }


                    app.shipLocation.push(location);

                })
            });
        },

        paintSalvoes: function (a, b, c) {
            let td = document.getElementById(a);
            td.classList.add(b);
            td.innerHTML = "<span>" + c + "</span>";
        },

        reversePaintSalvoes: function (a) {
            let td = document.getElementById(a);
            td.classList = "";
            td.innerHTML = "";
            td.style.cursor = "cell";

        },

        findSalvoesToPaint: function () {

            app.gameView.salvoes.forEach(salvoes => {
                salvoes.locations.forEach(salvoLocation => {
                    if (salvoes.player !== app.gameView.id && app.shipLocation.includes(salvoLocation) && salvoes.turn <= app.thisPlayerSalvoes.length) {

                        app.hitsOnMe.push(location);
                        app.paintSalvoes(("ship" + salvoLocation), "bg-danger", salvoes.turn);

                    } else if (salvoes.player != app.gameView.id && !app.shipLocation.includes(salvoLocation) && salvoes.turn <= app.thisPlayerSalvoes.length) {
                        app.paintSalvoes(("ship" + salvoLocation), "hit", salvoes.turn);

                    } else if (salvoes.player === app.gameView.id) {

                        app.paintSalvoes(("salvo" + salvoLocation), "bg-warning", salvoes.turn);
                        app.thisPlayerSalvoLocations.push(salvoLocation);

                        if (app.gameView.history.hits[salvoes.turn - 1] != "null" &&
                            app.gameView.history.hits[salvoes.turn - 1].allHits.includes(salvoLocation)) {
                            app.paintSalvoes(("salvo" + salvoLocation), "hit", salvoes.turn);
                        }

                    }


                })
            })

            if (app.gameView.history.hitsOpponent == null || app.hitsOnMe.length == 0 || app.gameView.history.hits.length == 0) {
                document.getElementById("my-ships-danger").style.display = "none";
            }

            if (app.gameView.history.hits == null || app.gameView.history.hits.length == 0 || app.hitsMp.length == 0) {
                document.getElementById("crashing-enemy").style.display = "none";

            }


        },

        createShips: function () {

            $.post({
                    url: "/api/games/players/" + app.gamePlayerId + "/ships",
                    data: JSON.stringify(app.shipPreSave),
                    dataType: "text",
                    contentType: "application/json"
                })


                .done(function (response, status, jqXHR) {

                    alert(response)
                    location.assign("game.html?gp=" + app.gamePlayerId);
                    app.findGameView();
                    app.updateShipControlDisplay();

                })


                .fail(function (jqXHR, status, httpError) {
                    alert("Failed to add ship: " + textStatus + " " + httpError);
                    console.log(status);

                });

        },

        getShipLength: function (type) {

            if (type == "Aircraft Carrier") {
                app.shipLength = 5;
            } else if (type == "Battleship") {
                app.shipLength = 4;
            } else if (type == "Submarine") {
                app.shipLength = 3
            } else if (type == "Destroyer") {
                app.shipLength = 3
            } else if (type == "Patrol Boat") {
                app.shipLength = 2
            }

        },

        getShipInitials: function () {

            if (app.shipType == "Aircraft Carrier") {
                shipInitials = "AC";
            } else if (app.shipType == "Battleship") {
                shipInitials = "B";
            } else if (app.shipType == "Submarine") {
                shipInitials = "S";
            } else if (app.shipType == "Destroyer") {
                shipInitials = "D";
            } else if (app.shipType == "Patrol Boat") {
                shipInitials = "PB";
            }



        },

        getTurn: function () {

            if (app.gameView.salvoes.length == 0) {
                app.salvoTurn = 1;
            }

            app.salvoTurn = app.thisPlayerSalvoes.length + 1;

        },

        getShotsforThisTurn: function () {

            app.getTurn();

            if (app.salvoTurn == 1) {
                app.shots = app.gameView.ships.length;
            } else {
                app.shots = app.gameView.ships.length - app.sinksOp[app.salvoTurn - 2].sinks.length;
            }

        },

        goToHomePage: function () {
            location.assign("games.html");
        }



    },


})





function selectSalvoes(item) {

    app.findGameView();


    if (app.gameView.state.includes("OVER")) {
        alert("Khalas, Habib! Game over!");
    } else if (app.gameView.state.includes("WAITING FOR YOUR OPPONENT")) {
        alert("Wait until your opponent joins the game!")
    } else if (app.gameView.ships.length < 5) {
        alert("Place all your ships first!");
    } else if (app.thisPlayerSalvoes.length > (app.gameView.salvoes.length / 2)) {
        alert("Wait for your turn! ");
    } else if (app.salvoLocationsPreSave.length >= 5) {
        alert("Ops... Don't forget the number of shots in your salvo is the number of ships afloat you have!")
    } else if (app.salvoLocationsPreSave.length >= app.shots) {
        alert("You only have " + app.shots + " shots for this turn!");
    } else {
        item.style.cursor = "not-allowed";

        let x = item.getAttribute('id')
        //  this 7 below refers to salvoA10 (salvo + letter + 2 digits number), for example. whereas the rest refers to salvoA+ only one digit number
        x.length == 7 ? app.salvoSquare = ((x).substr(5, 6)) :
            app.salvoSquare = ((x).substr(5, 6, 7));

        if (app.thisPlayerSalvoLocations.includes(app.salvoSquare) || app.salvoLocationsPreSave.includes(app.salvoSquare)) {
            alert("You have already fired at this position!");


        } else {
            app.salvoLocationsPreSave.push(app.salvoSquare);
            app.paintSalvoes(x, "blinking", app.salvoTurn);

        }

    }
}

function createSalvoes() {

    app.findGameView();


    newSalvo = {
        "turn": app.salvoTurn,
        "locations": app.salvoLocationsPreSave,

    }


    $.post({
            url: "/api/games/players/" + app.gamePlayerId + "/salvoes",
            data: JSON.stringify(this.newSalvo),
            dataType: "text",
            contentType: "application/json"
        })

        .done(function (response) {

            alert(response);
            location.assign("game.html?gp=" + app.gamePlayerId);
            app.findGameView();
        })
        .fail(function (error) {
            alert(error);
            console.log(error.responseJSON);
            app.findGameView();

        });

}

function resetSalvo() {
    let i = app.salvoLocationsPreSave.length - 1;
    app.reversePaintSalvoes("salvo" + app.salvoLocationsPreSave[i]);
    app.salvoSquare = "";
    app.salvoLocationsPreSave.pop();
}

function placeShips(item) {

    app.findGameView();

    if (app.gameView.state.includes("OVER")) {
        alert("This game has already finished!")
        app.findGameView();
    } else if (app.gameView.state.includes("WAITING FOR YOUR OPPONENT")) {
        alert("Wait until your opponent joins the game!")
        app.findGameView();
    } else if (!app.gameView.state.includes("PLACE")) {
        alert("You can't send ships now!")
        app.findGameView();

    } else if (app.shipPreSave.length > 5) {
        alert("You have chosen all your ships!")
    } else {

        if (app.shipLocation.includes(item.getAttribute('id').substr(4, 5, 6)) ||
            app.squareToPaint.includes(item.getAttribute('id').substr(4, 5, 6)) ||
            app.myPreShipLocations.includes(item.getAttribute('id').substr(4, 5, 6))) {
            alert("You already have a ship here!")

        } else if (app.myPreShipTypes.includes(app.shipType)) {
            alert("You have already sent your " + app.shipType + "!")

        } else if (app.shipType == "" || app.shipOrientation == "") {
            alert("Choose your ship type and orientation!")

            // } else if (app.firstSquare != "") {
            //     alert("Save or reset this ship before placing a new bow!")

        } else {
            app.firstSquare.push(item.getAttribute('id').substr(4, 5, 6));
            app.getShipLength(app.shipType);

            newShip = {
                "type": app.shipType,
                "locations": app.squareToPaint,
            }




            var i = 0;
            var lettersAxe = app.firstSquare[0].charCodeAt(0);
            var numbersAxe = app.firstSquare[0].charAt(1) + app.firstSquare[0].charAt(2) || "";

            if (app.shipOrientation === "v") {


                while (i < (app.shipLength) && 64 < lettersAxe < 75 && 0 < numbersAxe < 11) {

                    if (numbersAxe < 1 || numbersAxe > 10 || lettersAxe < 65 || lettersAxe > 74) {
                        if (app.squareToPaint.length > 0) {
                            app.squareToPaint.forEach(loc => app.reversePaintSalvoes("ship" + loc));
                        }
                        alert("Please respect the grid limits!");

                        break;
                    } else {
                        let toPaint = ((String.fromCharCode(lettersAxe)) + app.firstSquare[0].charAt(1) + app.firstSquare[0].charAt(2) || "");
                        if (app.myPreShipLocations.includes(toPaint)) {
                            if (app.squareToPaint.length > 0) {
                                app.squareToPaint.forEach(loc => app.reversePaintSalvoes("ship" + loc));
                            }

                            alert("You already have a ship in this location!");
                            break;

                        } else {
                            app.getShipInitials();
                            app.paintSalvoes("ship" + toPaint, "blinking", shipInitials);
                            app.squareToPaint.push(toPaint);
                            i++;
                            lettersAxe++;
                        }
                    }
                }
            }

            if (app.shipOrientation === "h") {


                while (i < (app.shipLength) && 0 < numbersAxe < 11 && 64 < lettersAxe < 75) {

                    if (numbersAxe < 1 || numbersAxe > 10 || lettersAxe < 65 || lettersAxe > 74) {

                        if (app.squareToPaint.length > 0) {
                            app.squareToPaint.forEach(loc => app.reversePaintSalvoes("ship" + loc));
                        }
                        alert("Please respect the grid limits!");

                        break;
                    } else {
                        let toPaint = app.firstSquare[0].charAt(0) + numbersAxe;
                        if (app.myPreShipLocations.includes(toPaint)) {

                            if (app.squareToPaint.length > 0) {
                                app.squareToPaint.forEach(loc => app.reversePaintSalvoes("ship" + loc));
                            }

                            alert("You already have a ship in this location!");
                            break;

                        } else {
                            app.getShipInitials();
                            app.paintSalvoes("ship" + toPaint, "blinking", shipInitials);
                            app.squareToPaint.push(toPaint);
                            i++;
                            numbersAxe++;
                        }
                    }
                }
            }

            app.shipPreSave.push(newShip);
            app.myPreShipLocations = app.myPreShipLocations.concat(app.squareToPaint);
            app.myPreShipTypes.push(app.shipType);

            app.firstSquare = [];
            app.squareToPaint = [];
            app.shipType = "";
            app.shipOrientation = "";



        }
    }

}

function resetShip() {
    app.shipType = "";
    app.shipOrientation = "";
    app.myPreShipLocations.forEach(loc => app.reversePaintSalvoes("ship" + loc));
    app.squareToPaint.forEach(loc => app.reversePaintSalvoes("ship" + loc));
    app.shipPreSave = [];
    app.findGameView();
    app.updateShipControlDisplay();
    app.firstSquare = [];
    app.squareToPaint = [];
    app.myPreShipLocations = [];
    app.myPreShipTypes = [];



}

function saveShips() {

    var a = app.shipPreSave.length;
    var b = app.gameView.ships.length;
    if (a < 5) {
        alert("Choose all your 5 ships before sending them!");
    } else if (a + b == 5) {

        app.createShips();
        app.firstSquare = [];
        app.squareToPaint = [];
        app.shipOrientation = "";
        app.shipType = "";


    } else if (a + b > 5) {
        alert("You can have a max of 5 ships only!");

        resetShip();

    } else {
        app.shipPreSave[a - 1].locations = app.squareToPaint;
        app.firstSquare = [];
        app.squareToPaint = [];
        app.shipType = "";
        app.shipOrientation = "";
    }


}


app.findGamePlayerId();
app.findGameView();




// setInterval(function () {

//     app.findGameView();



// }, 8000);