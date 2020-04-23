var app = new Vue({
	el: '#app',
	data: {
		games: [],
		leaderBoard: [],
		email: "",
		pwd: "",
		players: [],


	},
	updated: function () {
		filterGameTable();


	},
	methods: {
		getLeaderboard: function () {

			app.leaderBoard = [];

			app.games.games.forEach(game => {
				game.gamePlayers.forEach(gp => {
					var playerIndex = app.leaderBoard.findIndex(p => p.username == gp.player.username);

					if (playerIndex == -1) {
						player = {
							username: gp.player.username,
							totalPoints: 0,
							wins: 0,
							losses: 0,
							ties: 0,
						}

						if (gp.scores != null) {
							if (gp.scores.score == 1) {
								player.wins += 1;
								player.totalPoints += 1;
							} else if (gp.scores.score == 0.5) {
								player.ties += 1;
								player.totalPoints += 0.5;
							} else if (gp.scores.score == 0.0) {
								player.losses += 1;
							}
						}

						app.leaderBoard.push(player);


					} else {
						if (gp.scores != null) {
							if (gp.scores.score == 1) {
								app.leaderBoard[playerIndex].wins += 1;
								app.leaderBoard[playerIndex].totalPoints += 1;
							} else if (gp.scores.score == 0.5) {
								app.leaderBoard[playerIndex].ties += 1;
								app.leaderBoard[playerIndex].totalPoints += 0.5;
							} else if (gp.scores.score == 0.0) {
								app.leaderBoard[playerIndex].losses += 1;
							}
						}
					}

					app.leaderBoard.sort(function (a, b) {
						if (a.totalPoints === b.totalPoints) {
							return a.username - b.username
						} else {
							return b.totalPoints - a.totalPoints
						}

					});


				})
			});
		},

		findData: function () {
			$.get("/api/games")
				.done(function (data) {
					app.games = data;
					findPlayers();
					app.getLeaderboard();

					if (app.games.player != null) {
						document.body.style.backgroundSize = "100% 100%";
						document.getElementById("main-title").style.border = "outset";

					}
				})
		},

		loginNow: function () {
			$.post("/api/login", {
					email: this.email,
					password: this.pwd,

				})
				.done(function () {

					alert("Welcome to Salvo! You are now online!");
					location.reload(true);


				})
				.fail(function (error) {
					alert('Check your email/password or register if you are a new user.')
					console.log(error);
				});


		},

		signUp: function () {
			$.post("/api/players", {

					email: this.email,
					password: this.pwd,
				})
				.done(function () {
					alert("You are now signed up!");
					app.loginNow();
				})
				.fail(function (error) {
					alert("Please, enter a valid email/password.");
					console.log(error.responseJSON);

				})

		},

		logout: function () {
			app.email = null;
			app.pwd = null;

			$.post("/api/logout")
				.done(function () {

					console.log("You are now signed out!");
					location.reload(true);

				})
				.fail(function (error) {
					console.log(error.responseJSON.error);
				})
		},

		createGame: function () {
			$.post("/api/games")
				.done(function (data) {
					location.assign("game.html?gp=" + data.gpid);

				})
				.fail(function (error) {
					console.log(error.responseJSON.error);
				});

		},

		joinGame: function (gameId) {
			$.post("/api/game/" + gameId + "/players")
				.done(function (data) {
					location.assign("game.html?gp=" + data.gpid);
				})
				.fail(function (error) {
					console.log(error.responseJSON.error);
				});

		},


	},

})

function hide(button) {
	var x = document.getElementById("button");
	x.style.display = "none";

}

function alternateDisplays(a, b) {
	let x = document.getElementById(a);
	x.style.display = "none";
	let y = document.getElementById(b);
	y.style.display = "block";
}

function filterGameTable() {

	if (app.games.player != null) {

		[...document.querySelectorAll(".my-games-table")]
		.filter(a => !a.textContent.includes(app.games.player.username) && a.cells.length == 4)
			.forEach(a => a.style.display = "none");
	}
}

function findPlayers() {
	app.games.games.forEach(game => game.gamePlayers.forEach(gamePlayer => {
		app.players.push(gamePlayer.player);

	}))
}


app.findData();
setInterval(function () {
	app.findData();
}, 5000);