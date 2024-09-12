<template>
  <div id="app">
    <header class="header">
      <h1 class="app-title">🎬 Movie of the Day</h1>
      <nav v-if="isLoggedIn" class="navbar">
        <div class="nav-links">
          <button @click="showHome" class="nav-button">Home</button>
          <button @click="showMyFilms" class="nav-button">My Films</button>
          <button @click="showCurrentlyWatching" class="nav-button">
            Currently Watching
          </button>
          <button class="nav-button" @click="showMyPlaylists">
            My Playlists
          </button>
        </div>
        <div class="auth-links">
          <button @click="logout" class="nav-button logout-button">
            Logout
          </button>
        </div>
      </nav>
    </header>

    <main class="main-content">
      <template v-if="!isLoggedIn">
        <login-form @login="handleLogin" />
      </template>

      <template v-else>
        <home-page
          v-if="currentPage === 'home'"
          @get-suggestion="getSuggestion"
          :movie="currentMovie"
          @rate-movie="rateMovie"
          @save-movie="saveMovieFromCw"
          @next-movie="getNextMovie"
          @get-suggestion-by-genre="getSuggestionByGenre"
          @my-films="showMyFilms"
          @add-to-currently-watching="addToCurrentlyWatching"
          @to-my-currently-watching="showCurrentlyWatching"
        />
        <my-films
          v-else-if="currentPage === 'myFilms'"
          :savedMovies="savedMovies"
          @delete-movie="deleteMovie"
        />
        <currently-watching
          v-else-if="currentPage === 'CurrentlyWatching'"
          :currentlyWatching="currentlyWatching"
          @delete-movie-from-cw="deleteMovieFromCW"
          @save-movie="saveMovieFromCw"
          @my-films="showMyFilms"
        />
        <playlist-movie
          v-else-if="currentPage === 'MyPlaylists'"
          :playlists="allPlaylists"
          :savedMovies="savedMovies"
          @delete-movie-from-playlist="deleteMovieFromPlaylist"
          :moviesInPlaylist="moviesInPlaylist"
          @add-movie-to-playlist="addMovieToPlaylist"
          @get-movies-playlist="getMoviesInPlaylist"
          @get-playlist="getPlaylists"
        ></playlist-movie>
      </template>
    </main>

    <footer class="footer">
      <p>&copy; 2024 Movie of the Day. All rights reserved.</p>
    </footer>
  </div>
</template>
<script>
import { ref, onMounted } from "vue";
import LoginForm from "./components/LoginForm.vue";
import HomePage from "./components/HomePage.vue";
import MyFilms from "./components/MyFilms.vue";
import CurrentlyWatching from "./components/CurrentlyWatching.vue";
import PlaylistMovie from "./components/PlaylistMovie.vue";
import axios from "axios";

export default {
  name: "App",
  components: {
    LoginForm,
    HomePage,
    MyFilms,
    CurrentlyWatching,
    PlaylistMovie,
  },
  setup() {
    const isLoggedIn = ref(false);
    const currentPage = ref("home");
    const currentMovie = ref(null);
    const watchedMovies = ref([]);
    const userJWT = ref(null);
    const sessionUserData = ref(null);
    const savedMovies = ref([]);
    const currentlyWatching = ref([]);
    const allPlaylists = ref([]);
    const moviesInPlaylist = ref([]);

    const handleLogin = (userData) => {
      // In a real app, you'd verify the login with your backend
      isLoggedIn.value = true;
      userJWT.value = userData.token;
      sessionUserData.value = userData.username;

      // Save username and JWT to local storage
      localStorage.setItem("userToken", userData.token);
      localStorage.setItem("username", userData.username);
    };
    const showCurrentlyWatching = () => {
      currentPage.value = "CurrentlyWatching";
      getCurrentWatching();
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

    const showMyPlaylists = () => {
      currentPage.value = "MyPlaylists";
      getPlaylists();
    };

    const getSuggestion = async () => {
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

    const addToCurrentlyWatching = async () => {
      if (
        currentMovie.value &&
        !currentlyWatching.value.find((m) => m.id === currentMovie.value.id)
      ) {
        currentlyWatching.value.push({ ...currentMovie.value });

        const bodyRequest = {
          username: sessionUserData.value,
          movie: {
            movieId: currentMovie.value.id,
            title: currentMovie.value.title,
            overview: currentMovie.value.description,
            release_date: currentMovie.value.release_date,
            runtime: currentMovie.value.runtime,
            vote_average: currentMovie.value.rating,
            poster_path: currentMovie.value.image,
            genres: currentMovie.value.genre,
            original_language: currentMovie.value.original_language,
          },
        };
        try {
          const response = await fetch(
            "http://localhost:8080/movie/currently-watching",
            {
              method: "POST",
              headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${userJWT.value}`,
              },
              body: JSON.stringify(bodyRequest),
            }
          );

          const data = await response.json();
          return data;
        } catch (error) {
          console.error("Error with the add currently watching request");
        }
      }
    };

    const getCurrentWatching = async () => {
      try {
        const response = await fetch(
          `http://localhost:8080/movie/currently-watching?username=${sessionUserData.value}`,
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${userJWT.value}`,
            },
          }
        );

        const data = await response.json();
        currentlyWatching.value = data;
      } catch (error) {
        console.error("Error retrieving currently watching movies");
      }
    };

    const rateMovie = async (rating) => {
      if (currentMovie.value) {
        currentMovie.value.userRating = rating;
      }
    };

    const markAsWatched = async () => {
      if (
        currentMovie.value &&
        !watchedMovies.value.find((m) => m.id === currentMovie.value.id)
      ) {
        watchedMovies.value.push({ ...currentMovie.value });

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
          return data;
        } catch (error) {
          console.error("Error saving the movie:", error);
        }
      }
    };

    const saveMovieFromCw = async (movie, rating) => {
      currentMovie.value = movie;
      await rateMovie(rating);

      markAsWatched();
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

    const deleteMovieFromCW = async (movie) => {
      try {
        if (!movie || !movie.id) {
          console.error("Movie object or movie ID is not provided.");
          return;
        }
        const response = await fetch(
          `http://localhost:8080/movie/currently-watching/delete?username=${sessionUserData.value}&movieId=${movie.id}`,
          {
            method: "DELETE",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${userJWT.value}`,
            },
            body: JSON.stringify(movie),
          }
        );

        if (response.status === 200) {
          console.log("Delete successful: No content returned.");
        } else if (response.ok) {
          const data = await response.json();
          console.log("Delete successful:", data);
        } else {
          throw new Error(`Failed to delete movie. Status: ${response.status}`);
        }
        //update UI
        currentlyWatching.value = currentlyWatching.value.filter(
          (currently) => currently.id !== movie.id
        );
      } catch (error) {
        console.error("Error deleting movie from currently watching");
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

    //playlist section

    const getPlaylists = async () => {
      try {
        const response = await fetch(
          `http://localhost:8080/playlist/all?username=${sessionUserData.value}`,
          {
            method: "GET",
            headers: {
              Authorization: `Bearer ${userJWT.value}`,
              "Content-Type": "application/json",
            },
          }
        );
        const data = await response.json();
        allPlaylists.value = data;
      } catch (error) {
        console.error("Error retrieving playlist");
      }
    };

    const deleteMovieFromPlaylist = async (playlistName, movieId) => {
      try {
        const response = await fetch(
          `http://localhost:8080/playlist/delete-movie?username=${sessionUserData.value}&name=${playlistName}&movieId=${movieId}`,
          {
            method: "DELETE",
            headers: {
              Authorization: `Bearer ${userJWT.value}`,
              "Content-Type": "application/json",
            },
          }
        );
        if (response.status === 200) {
          //update the movies inside the playlist
          moviesInPlaylist.value = moviesInPlaylist.value.filter(
            (m) => m.id !== movieId
          );
          console.log("movie deleted");
        }
      } catch (error) {
        console.error("Error deleting the movie from the playlist");
      }
    };

    const getMoviesInPlaylist = async (playlistName) => {
      try {
        const response = await fetch(
          `http://localhost:8080/playlist/movies?username=${sessionUserData.value}&name=${playlistName.name}`,
          {
            method: "GET",
            headers: {
              Authorization: `Bearer ${userJWT.value}`,
              "Content-Type": "application/json",
            },
          }
        );
        const data = await response.json();
        moviesInPlaylist.value = data;
      } catch (error) {
        console.error("Error retrieving the movies in the current playlist");
      }
    };

    const addMovieToPlaylist = async (parameters) => {
      console.log(typeof parameters, parameters);
      try {
        const response = await fetch(
          `http://localhost:8080/playlist/add-movie?name=${parameters.playlistName}&username=${sessionUserData.value}&movieId=${parameters.movieId}`,
          {
            method: "POST",
            headers: {
              Authorization: `Bearer ${userJWT.value}`,
              "Content-Type": "application/json",
            },
          }
        );

        await response.json();
        await getPlaylists();
      } catch (error) {
        console.error("error adding the movie to the playlist");
      }
    };

    onMounted(() => {
      const token = localStorage.getItem("userToken");
      const username = localStorage.getItem("username");

      if (token && username) {
        isLoggedIn.value = true;
        sessionUserData.value = username;
        userJWT.value = token;
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
      addToCurrentlyWatching,
      currentlyWatching,
      showCurrentlyWatching,
      getCurrentWatching,
      deleteMovieFromCW,
      saveMovieFromCw,
      showMyPlaylists,
      getPlaylists,
      allPlaylists,
      deleteMovieFromPlaylist,
      getMoviesInPlaylist,
      moviesInPlaylist,
      addMovieToPlaylist,
    };
  },
};
</script>

<style scoped>
:root {
  --primary-color: #1a1a1a;
  --secondary-color: #f0f0f0;
  --accent-color: #008ebdd3;
  --text-color: #333;
  --button-hover: #e0e0e0;
}

body {
  font-family: "Arial", sans-serif;
  line-height: 1.6;
  color: var(--text-color);
  background-color: var(--secondary-color);
  margin: 0;
  padding: 0;
}

#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.header {
  background-color: var(--primary-color);
  padding: 1rem;
  color: var(--secondary-color);
}

.app-title {
  font-size: 1.8rem;
  margin: 0 0 1rem 0;
  text-align: center;
}

.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0;
}
/* 
.nav-links,
.auth-links {
  display: flex;
  gap: 1rem;
} */

.nav-button {
  background-color: transparent;
  border: none;
  color: var(--secondary-color);
  cursor: pointer;
  font-size: 1rem;
  padding: 0.5rem 1rem;
  transition: all 0.3s ease;
  border-radius: 4px;
  position: relative;
}

.nav-button::after {
  content: "";
  position: absolute;
  width: 100%;
  transform: scaleX(0);
  height: 2px;
  bottom: 0;
  left: 0;
  background: #141718;
  transform-origin: bottom right;
  transition: transform 0.25s ease-out;
}

.logout-button:hover::after,
.nav-button:hover::after {
  transform: scaleX(1);
  transform-origin: bottom left;
}

.logout-button {
  background-color: var(--accent-color);
  color: var(--primary-color);
  border-radius: 20px;
}

.main-content {
  flex: 1;
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  box-sizing: border-box;
}

.footer {
  background-color: var(--primary-color);
  color: var(--secondary-color);
  text-align: center;
  padding: 1rem;
  margin-top: auto;
}

@media (max-width: 768px) {
  .navbar,
  .nav-links,
  .auth-links {
    flex-direction: column;
    align-items: stretch;
  }

  .nav-button {
    width: 100%;
    margin-bottom: 0.5rem;
  }

  .main-content {
    padding: 1rem;
  }
}
</style>
