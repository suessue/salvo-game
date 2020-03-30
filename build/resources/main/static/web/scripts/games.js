var app = new Vue({
	el: '#app',
	data: {
		games: [],
		leaderBoard: [],
		email: "",
		pwd: "",
		players: [],
		// playersJson: [],
		// playersSet: [],
		// ties: [],
		// score: [],
		// wins: [],
		// losses: [],


	},

	methods: {
		getLeaderboard: function () {
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
			$.get("/api/games", function (data) {
				app.games = data;
				app.getLeaderboard();
				findPlayers();
				// findSets();
				// findTiesLossesWins(1);
				// findTiesLossesWins(0.5);
				// findTiesLossesWins(0);
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
					console.log(error.responseJSON);
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

		}

	},

})

function hide(button) {
	var x = document.getElementById("button");
	x.style.display = "none";

}

app.findData();



function findPlayers() {
	app.games.games.forEach(game => game.gamePlayers.forEach(gamePlayer => {
		app.players.push(gamePlayer.player);

	}))
}

// function findSets() {
// 	app.playersJson = (app.players.map(x => x.player));
// 	app.playersSet = (app.playersJson.map(x => {
// 			return {

// 				username: x.username,
// 				totalPoints: x.totalPoints,
// 				wins: x.wins,
// 				losses: x.losses,
// 				ties: x.ties,
// 			}
// 		}

// 	))

// 	app.playersSetSorted = [...new Set(app.playersSet)]

// }

// function findSets() {
// 	app.playersJson = [...new Set(app.players.map(x => {

// 		return {
// 			user: x.player.username,
// 			score: x.scores.score,
// 			ties: 0,
// 			wins: 0,
// 			losses: 0,

// 		}
// 	}))]

// 	console.log(app.playersJson);

// }


// score: 0, app.players.map(x => x.scores),
// 			ties: 0, app.players.count(x => x.scores.score == 0, 5 && x.player.username == this.user),
// 			wins: 0, app.players.count(x => x.scores.score == 1 && x.player.username == this.user),
// 			losses: 0,app.players.count(x => x.scores.score == 0 && x.player.username == this.user),

// function findTiesLossesWins(x, email, y) {
// 	var result = "";
// 	result = app.players.filter(b => (b.scores.score == x && b.player.username == email));
// 	app.y.push(result);
// }

// function findTiesLossesWins(x) {

// 	let result = app.players.score.find(b => (b.scores.score === x));

// 	if (x = 1) {
// 		console.log(result);
// 	} else if (x = 0.5) {
// 		console.log(result);
// 	} else if (x = 0) {
// 		console.log(result);
// 	}
// }

// function filterTiesLosseWins() {

// }

// function countTiesLossesWins(td, result) {
// 	td.includes(player.username && result) ?
// 		app.result.filter(sub => sub.player.username == player document.getElementById(td);
// 			var total = a.filter(player)
// 		}