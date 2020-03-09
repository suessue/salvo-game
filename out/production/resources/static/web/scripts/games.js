var app = new Vue({
	el: '#app',
	data: {
		games: [],

	},
	methods: {
		findData: function () {
			$.get("/api/games", function (data) {
				app.games = data;
			})
		}
	}
})

app.findData();
