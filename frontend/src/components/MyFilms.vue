<template>
  <div class="my-films">
    <h2>My Watched Films</h2>
    <div v-if="savedMovies.length > 0" class="film-grid">
      <div v-for="movie in savedMovies" :key="movie.id" class="film-card">
        <img
          v-if="movie.poster_path"
          :src="movie.poster_path"
          :alt="movie.title"
          class="movie-poster"
        />
        <h3>{{ movie.title }}</h3>
        <p>Your Rating: {{ movie.rating || "Not rated" }}</p>
        <div class="button-container">
          <button @click="showMovieDetails(movie)">Show Details</button>
          <button @click="handleDeleteMovie(movie)">Remove</button>
        </div>
      </div>
    </div>
    <div v-else>
      <p>You have no saved movies.</p>
    </div>

    <!-- FilmDetails Popup -->
    <film-details
      v-if="selectedMovie"
      :movie="selectedMovie"
      :isVisible="isPopupVisible"
      @close="closeMovieDetails"
    ></film-details>
  </div>
</template>

<script>
import FilmDetails from "./FilmDetails.vue";

export default {
  name: "MyFilms",
  components: {
    FilmDetails,
  },
  props: {
    savedMovies: {
      type: Array,
      required: true,
    },
    deleteMovie: {
      type: Function,
      required: true,
    },
  },
  emits: ["delete-movie"],
  data() {
    return {
      selectedMovie: null,
      isPopupVisible: false,
    };
  },
  methods: {
    showMovieDetails(movie) {
      this.selectedMovie = movie;
      this.isPopupVisible = true;
    },
    closeMovieDetails() {
      this.selectedMovie = null;
      this.isPopupVisible = false;
    },
    handleDeleteMovie(movie) {
      this.$emit("delete-movie", movie);
    },
  },
};
</script>

  
  <style scoped>
.my-films {
  max-width: 1000px;
  margin: 0 auto;
}

.film-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.film-card {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  width: 200px;
  text-align: center;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 10px;
  position: relative;
}

.film-card:hover {
  transform: translateY(-5px);
}

.film-card img {
  width: 100%;
  height: auto;
  border-radius: 4px;
}

.film-card h3 {
  margin: 10px 0 5px;
  font-size: 1.1em;
}

.film-card p {
  margin: 5px 0;
  font-size: 0.9em;
  color: #666;
}

.movie-poster {
  width: 100%;
  border-radius: 4px;
  margin-bottom: 10px;
}

button {
  background-color: #948540;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #524f18;
}
</style>