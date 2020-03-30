var app = new Vue({
    el: '#app',
    data: {

        gameView: {},
        gamePlayerId: "null",
        gameRows: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
        gameColumns: ["#", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"],
        mainPlayer: {},
        opponent: {},
        firstSquare: [],
        shipType: "",
        shipLocation: [],
        shipPreSave: [],
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

    },

    methods: {

        findPlayers: function () {
            app.mainPlayer = app.gameView.gamePlayers.filter(x => x.id === app.gameView.id);
            app.opponent = app.gameView.gamePlayers.filter(x => x.id !== app.gameView.id);
        },

        findGamePlayerId: function () {
            const urlParams = new URLSearchParams(window.location.search);
            app.gamePlayerId = urlParams.get('gp');


        },

        findGameView: function () {
            $.get('/api/game_view/' + app.gamePlayerId, function (data) {
                    app.gameView = data;
                    app.findPlayers();
                    app.findShipsToPaint();
                    app.findSalvoes();


                })
                .done(function () {

                })
                .fail(function (error) {
                    alert("You are not authorized to see this info.");
                    console.log(error.responseJSon.error);
                });
        },

        paintShipsLocation: function (a, b) {
            let td = document.getElementById(a);
            td.classList.add(b);

        },

        reversePaintShipsLocation: function (a) {
            let td = document.getElementById(a);
            td.classList = '';

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

        },

        findSalvoes: function () {

            app.gameView.salvoes.forEach(salvoes => {
                salvoes.locations.forEach(salvoLocation => {
                    if (salvoes.player !== app.gameView.id && app.shipLocation.includes(salvoLocation)) {
                        // app.reversePaintShipsLocation(("ship" + salvoLocation));
                        app.paintSalvoes(("ship" + salvoLocation), "bg-danger", salvoes.turn);
                        // findHitBoat(("ship" + salvolocation));
                    } else if (salvoes.player !== app.gameView.id && app.gameView.ships.length != 0 && !app.shipLocation.includes(salvoLocation)) {
                        app.paintSalvoes(("ship" + salvoLocation), "hit", salvoes.turn);
                    } else if (salvoes.player === app.gameView.id) {
                        app.paintSalvoes(("salvo" + salvoLocation), "bg-warning", salvoes.turn);
                        app.thisPlayerSalvoLocations.push(salvoLocation);
                        if (app.thisPlayerSalvoes.length === 0) {
                            app.thisPlayerSalvoes.push(app.gameView.salvoes)
                        }
                    }


                })
            })


        },

        createShips: function () {

            $.post({
                    url: "/api/games/players/" + this.gamePlayerId + "/ships",
                    data: JSON.stringify(this.shipPreSave),
                    dataType: "text",
                    contentType: "application/json"
                })

                .done(function (response) {

                    alert(response);
                    resetShip();
                    app.findGameView();
                    location.reload(true);
                })
                .fail(function (jqXHR, error) {
                    mensajeError = jqXHR.responseText;
                    alert(mensajeError);
                    location.reload(true);
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

        getTurn: function () {

            app.thisPlayerSalvoes = app.gameView.salvoes.filter(salvoes => salvoes.player == app.gameView.id);
            app.salvoTurn = "" + (Math.floor(app.thisPlayerSalvoes.length) + 1);

        },



    },
})


function selectSalvoes(item) {

    app.getTurn();

    let x = item.getAttribute('id')

    x.length == 7 ? app.salvoSquare = ((x).substr(5, 6)) :
        app.salvoSquare = ((x).substr(5, 6, 7));

    if (app.thisPlayerSalvoLocations.includes(app.salvoSquare) || app.salvoLocationsPreSave.includes(app.salvoSquare)) {
        alert("You have already fired at this position!");

    } else {
        app.salvoLocationsPreSave.push(app.salvoSquare);
        app.paintSalvoes(x, "blinking", app.salvoTurn);
    }


}

function createSalvoes() {

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
            resetSalvo();
            app.findGameView();
            location.reload(true);
        })
        .fail(function (response) {
            alert(response);

        });

}

function resetSalvo() {
    app.reversePaintSalvoes("salvo" + app.salvoSquare);
    app.salvoLocationsPreSave = [];

}

function placeShips(item) {


    if (app.shipType == "" || app.shipOrientation == "") {
        alert("Choose your ship type and orientation!")
    } else if (app.firstSquare != "") {
        alert("Save or reset this ship before placing a new bow!")
    } else {
        app.firstSquare.push(item.getAttribute('id').substr(4, 5, 6));
        app.getShipLength(app.shipType);

        newShip = {
            "type": app.shipType,
            "locations": [],

        }

        app.shipPreSave.push(newShip);
        var i = 0;
        var lettersAxe = app.firstSquare[0].charCodeAt(0);
        var numbersAxe = app.firstSquare[0].charAt(1) + app.firstSquare[0].charAt(2) || "";

        if (app.shipOrientation === "v") {


            while (i < (app.shipLength) && 64 < lettersAxe < 75 && 0 < numbersAxe < 11) {

                if (numbersAxe < 1 || numbersAxe > 10 || lettersAxe < 65 || lettersAxe > 74) {
                    alert("Please respect the grid limits!");
                    resetShip();
                    break;
                } else {
                    let toPaint = ((String.fromCharCode(lettersAxe)) + app.firstSquare[0].charAt(1) + app.firstSquare[0].charAt(2) || "");
                    if (app.shipLocation.includes(toPaint)) {
                        resetShip();
                        alert("You already have a ship in this location!");
                        break;

                    } else {

                        app.paintShipsLocation("ship" + toPaint, "blinking");
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
                    alert("Please respect the grid limits!");
                    resetShip();
                    break;
                } else {
                    let toPaint = app.firstSquare[0].charAt(0) + numbersAxe;
                    if (app.shipLocation.includes(toPaint)) {
                        resetShip();
                        alert("You already have a ship in this location!");
                        break;

                    } else {

                        app.paintShipsLocation("ship" + toPaint, "blinking");
                        app.squareToPaint.push(toPaint);
                        i++;
                        numbersAxe++;
                    }
                }
            }
        }

    }

}

function resetShip() {
    app.shipType = "";
    app.shipOrientation = "";

    if (app.squareToPaint.length != 0) {
        let x = app.squareToPaint.map(i => ("ship" + i));
        x.forEach(shipSquare => {
            app.reversePaintShipsLocation(shipSquare);
        })

    }

    app.firstSquare = [];
    app.squareToPaint = [];
    app.shipPreSave.pop();

}

function saveShips() {

    var a = app.shipPreSave.length;
    var b = app.gameView.ships.length;
    if (a == 0 || app.squareToPaint.length == 0) {
        alert("Choose where you are placing your ship before saving it!");
    } else if (a + b > 5) {
        alert("You can have a max of 5 ships only!");
        resetShip();
    } else {
        app.shipPreSave[a - 1].locations = app.squareToPaint;
        app.createShips();
        app.shipType = "";
        app.shipOrientation = "";
        app.firstSquare = [];
        app.squareToPaint = [];
    }

}

// function findHitBoat(td) {
//     var img = document.createElement('img');
//     img.src = "images/hitBoat.gif";
//     img.classList.add("hitBoat");
//     document.getElementById(td).appendChild(img);

// }


app.findGamePlayerId();
app.findGameView();