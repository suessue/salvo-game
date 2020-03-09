var app = new Vue({
    el: '#app',
    data: {

        gameView: [],
        gamePlayerId: "null",
        gameRows: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
        gameColumns: ["#", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"],
        mainPlayer: {},
        opponent: {},
        shipLocation: [],

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
        },

        paintShipsLocation: function (a, b) {
            let td = document.getElementById(a);
            td.classList.add(b);

        },

        findShipsToPaint: function () {
            app.gameView.ships.forEach(ships => {
                ships.locations.forEach(location => {
                    app.paintShipsLocation(("ship" + location), "bg-success");
                    app.shipLocation.push(location);

                })
            });
        },


        paintSalvoes: function (a, b, c) {
            let td = document.getElementById(a);
            td.classList.add(b);
            td.innerHTML = c;
        },

        findSalvoes: function () {

            app.gameView.salvoes.forEach(salvoes => {
                salvoes.locations.forEach(salvolocation => {
                    if (salvoes.player !== app.gameView.id && app.shipLocation.includes(salvolocation)) {
                        app.paintSalvoes(("ship" + salvolocation), "bg-danger", salvoes.turn);
                    } else if (salvoes.player !== app.gameView.id && !app.shipLocation.includes(salvolocation)) {
                        app.paintSalvoes(("ship" + salvolocation), "bg-warning", salvoes.turn);
                    } else if (salvoes.player === app.gameView.id) {
                        app.paintSalvoes(("salvo" + salvolocation), "bg-primary", salvoes.turn);
                    }
                })
            })
        },
    },
})



app.findGamePlayerId();
app.findGameView();