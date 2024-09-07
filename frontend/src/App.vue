<template>
  <div id="app">
    <header>
      <h1 class="app-title">🎬 Movie of the Day</h1>
      <nav v-if="isLoggedIn">
        <button @click="showHome">Home</button>
        <button @click="showMyFilms">My Films</button>
        <button @click="logout">Logout</button>
      </nav>
    </header>

    <main>
      <template v-if="!isLoggedIn">
        <login-form @login="handleLogin" />
      </template>

      <template v-else>
        <home-page
          v-if="currentPage === 'home'"
          @get-suggestion="getSuggestion"
          :movie="currentMovie"
          :username="sessionUserData"
          @rate-movie="rateMovie"
          @mark-watched="markAsWatched"
          @next-movie="getNextMovie"
          @get-suggestion-by-genre="getSuggestionByGenre"
          @my-films="showMyFilms"
        />
        <my-films
          v-else-if="currentPage === 'myFilms'"
          :savedMovies="savedMovies"
          :username="sessionUserData"
          @delete-movie="deleteMovie"
        />
      </template>
    </main>

    <footer>
      <p>&copy; 2024 Movie of the Day. All rights reserved.</p>
    </footer>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import LoginForm from "./components/LoginForm.vue";
import HomePage from "./components/HomePage.vue";
import MyFilms from "./components/MyFilms.vue";
import axios from "axios";

export default {
  name: "App",
  components: {
    LoginForm,
    HomePage,
    MyFilms,
  },
  setup() {
    const isLoggedIn = ref(false);
    const currentPage = ref("home");
    const currentMovie = ref(null);
    const watchedMovies = ref([]);
    const userJWT = ref(null);
    const sessionUserData = ref(null);
    const savedMovies = ref([]);

    const handleLogin = (userData) => {
      // In a real app, you'd verify the login with your backend
      isLoggedIn.value = true;
      userJWT.value = userData.token;
      sessionUserData.value = userData.username;

      // Save username and JWT to local storage
      localStorage.setItem("userToken", userData.token);
      localStorage.setItem("username", userData.username);
      console.log("Username: ", sessionUserData.value);
    };

    const logout = () => {
      isLoggedIn.value = false;
      currentMovie.value = null;
      watchedMovies.value = [];
      savedMovies.value = [];
      userJWT.value = null;
      sessionUserData.value = null;

      // clear local storage
      localStorage.removeItem("userToken");
      localStorage.removeItem("username");

      //redirect to home or login page
      currentPage.value = "home";
    };

    const showHome = () => {
      currentPage.value = "home";
      currentMovie.value = null;
    };

    const showMyFilms = () => {
      currentPage.value = "myFilms";
      getSavedMovies();
    };

    //put criteria = {} inside func parameter
    const getSuggestion = async () => {
      // In a real app, you'd call your backend API here
      try {
        if (!userJWT.value) {
          console.error("No JWT available. User might not be logged in.");
          return;
        }

        const response = await fetch("http://localhost:8080/movie/random", {
          method: "GET",
          headers: {
            Authorization: `Bearer ${userJWT.value}`,
            "Content-Type": "application/json",
          },
        });

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        const movieData = await response.json();
        const [trailerUrl, platforms] = await Promise.all([
          fetchTrailer(movieData.title),
          fetchStreamingPlatforms(movieData.id, userJWT),
        ]);

        currentMovie.value = formatMovieData(movieData, trailerUrl, platforms);
      } catch (error) {
        console.error("Error fetching movie suggestion:", error);
      }
    };

    const getSuggestionByGenre = async (genre) => {
      try {
        console.log("Passing parameter genre:", genre.genre);
        const response = await fetch(
          `http://localhost:8080/movie/random/genre?genre=${genre.genre}`,
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${userJWT.value}`,
            },
          }
        );

        const movieData = await response.json();
        const [trailerUrl, platforms] = await Promise.all([
          fetchTrailer(movieData.title),
          fetchStreamingPlatforms(movieData.id, userJWT),
        ]);

        currentMovie.value = formatMovieData(movieData, trailerUrl, platforms);
      } catch (error) {
        console.error("Error retrieving movie with genre selection");
      }
    };

    const getNextMovie = () => {
      getSuggestion();
    };

    const rateMovie = (rating) => {
      if (currentMovie.value) {
        currentMovie.value.userRating = rating;
        // In a real app, you'd send this rating to your backend
      }
    };

    const markAsWatched = async () => {
      if (
        currentMovie.value &&
        !watchedMovies.value.find((m) => m.id === currentMovie.value.id)
      ) {
        watchedMovies.value.push({ ...currentMovie.value });

        console.log("currentmovie: ", currentMovie);
        // Prepare the request body
        const requestBody = {
          username: sessionUserData.value,
          movie: {
            movieId: currentMovie.value.id,
            title: currentMovie.value.title,
            overview: currentMovie.value.description,
            release_date: currentMovie.value.release_date,
            runtime: currentMovie.value.runtime,
            vote_average: currentMovie.value.rating,
            poster_path: currentMovie.value.image,
            rating: currentMovie.value.userRating,
            genres: currentMovie.value.genre,
            original_language: currentMovie.value.original_language,
            // production_companies: currentMovie.value.production_companies,
            // production_countries: currentMovie.value.production_countries,
          },
        };

        console.log("bodyrequest: ", requestBody);

        try {
          const response = await fetch("http://localhost:8080/movie/save", {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${userJWT.value}`,
            },
            body: JSON.stringify(requestBody),
          });

          if (!response.ok) {
            throw new Error("Network response was not ok");
          }

          const data = await response.json();
          console.log("Saved Successfully. Data: ", data);
        } catch (error) {
          console.error("Error saving the movie:", error);
        }
      }
    };

    const getSavedMovies = async () => {
      try {
        const response = await fetch(
          `http://localhost:8080/movie/saved-movies?username=${sessionUserData.value}`,
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${userJWT.value}`,
            },
          }
        );
        const data = await response.json();
        savedMovies.value = data;
        console.log("Saved Movie Data ", savedMovies.value);
      } catch (error) {
        console.error("Error retrieving the saved movies");
      }
    };

    const deleteMovie = async (movie) => {
      // Accepting the movie object as a parameter
      try {
        if (!movie || !movie.id) {
          console.error("Movie object or movie ID is not provided.");
          return;
        }

        const response = await fetch(
          `http://localhost:8080/movie/delete?movieId=${movie.movieId}&username=${sessionUserData.value}`,
          {
            method: "DELETE",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${userJWT.value}`,
            },
            body: JSON.stringify(movie),
          }
        );

        if (response.status === 204) {
          console.log("Delete successful: No content returned.");
        } else if (response.ok) {
          // If it's not 204, it might be another status that still returns content
          const data = await response.json();
          console.log("Delete successful:", data);
        } else {
          throw new Error(`Failed to delete movie. Status: ${response.status}`);
        }

        // Update the savedMovies list by removing the deleted movie
        savedMovies.value = savedMovies.value.filter(
          (savedMovie) => savedMovie.id !== movie.id
        );
      } catch (error) {
        console.error("Error deleting movie:", error);
        alert("There was an error deleting the movie.");
      }
    };

    const fetchTrailer = async (movieTitle) => {
      const response = await axios.get(
        "https://www.googleapis.com/youtube/v3/search",
        {
          params: {
            part: "snippet",
            q: `${movieTitle} official trailer`,
            key: process.env.VUE_APP_YOUTUBE_API_KEY,
            type: "video",
            maxResults: 1,
          },
        }
      );
      const trailer = response.data.items[0];
      return `https://www.youtube.com/embed/${trailer.id.videoId}`;
    };

    const fetchStreamingPlatforms = async (movieId, userJWT) => {
      const response = await fetch(
        `http://localhost:8080/movie/${movieId}/streaming-platforms`,
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${userJWT.value}`,
            "Content-Type": "application/json",
          },
        }
      );
      return await response.json();
    };

    const formatMovieData = (movieData, trailerUrl, platforms) => {
      const baseUrl = "https://image.tmdb.org/t/p/w500";
      return {
        id: movieData.id,
        title: movieData.title,
        description: movieData.overview,
        image: `${baseUrl}${movieData.poster_path}`,
        genre: movieData.genres,
        rating: movieData.vote_average,
        release_date: movieData.release_date,
        runtime: movieData.runtime,
        original_language: movieData.original_language,
        genres: movieData.genres,
        trailer: trailerUrl,
        platforms: Array.isArray(platforms) ? platforms : [],
      };
    };

    onMounted(() => {
      // Check if user is already logged in (e.g., from localStorage)
      const token = localStorage.getItem("userToken");
      const username = localStorage.getItem("username");

      if (token && username) {
        isLoggedIn.value = true;
        sessionUserData.value = username;
        userJWT.value = token;
        getSuggestion();
      }
    });

    return {
      isLoggedIn,
      currentPage,
      currentMovie,
      watchedMovies,
      handleLogin,
      logout,
      showHome,
      showMyFilms,
      getSuggestion,
      getNextMovie,
      rateMovie,
      markAsWatched,
      getSavedMovies,
      savedMovies,
      deleteMovie,
      getSuggestionByGenre,
    };
  },
};
</script>

<style>
#app {
  font-family: "Arial", sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.app-title {
  font-size: 2.5em;
  color: #a85d12f4;
  margin-bottom: 20px;
}

header {
  margin-bottom: 30px;
}

nav button {
  margin: 0 10px;
  padding: 10px 20px;
  font-size: 1em;
  cursor: pointer;
  background-color: #db6109;
  color: white;
  border: none;
  border-radius: 5px;
  transition: background-color 0.3s;
}

nav button:hover {
  background-color: #a36412;
}

footer {
  margin-top: 50px;
  font-size: 0.9em;
  color: #666;
}
</style>