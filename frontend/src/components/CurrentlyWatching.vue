<template>
  <div class="home-page">
    <div class="title">
      <h2>My Currently Watching</h2>
    </div>
    <div v-if="currentlyWatching.length > 0" class="film-grid">
      <div
        v-for="movie in currentlyWatching"
        :key="movie.id"
        class="movie-suggestion"
        @click="showMovieDetails(movie)"
      >
        <img
          v-if="movie.poster_path"
          :src="movie.poster_path"
          :alt="movie.title"
          class="movie-poster"
        />
        <h3>{{ movie.title }}</h3>
        <div class="button-group">
          <button @click.stop="handleDeleteMovie(movie)" class="action-button">
            Remove
          </button>
          <button
            class="action-button"
            @click.stop="(savedMoviePopUp = true), (this.selectedMovie = movie)"
          >
            Save Movie
          </button>
        </div>
      </div>
    </div>

    <div v-else class="message-no-movies">
      <p>No movies in your watchlist yet. Find something to start watching!</p>
    </div>
    <div class="saved-content-popup" v-if="savedMoviePopUp">
      <rating-system
        @rate-movie="rateMovie"
        @mark-watched="saveMovie"
        @my-films="toMyFilms"
        @close-rating-pop-up="savedMoviePopUp = false"
      ></rating-system>
    </div>

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
import RatingSystem from "./utils/RatingSystem.vue";

export default {
  name: "CurrentlyWatching",
  components: {
    FilmDetails,
    RatingSystem,
  },
  props: {
    currentlyWatching: {
      type: Array,
      required: true,
    },
  },
  emits: ["delete-movie-from-cw", "save-movie", "my-films"],
  data() {
    return {
      selectedMovie: null,
      isPopupVisible: false,
      savedMoviePopUp: false,
      rating: null,
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
      this.$emit("delete-movie-from-cw", movie);
    },
    saveMovie() {
      this.$emit("save-movie", this.selectedMovie, this.rating);
      this.handleDeleteMovie(this.selectedMovie);
    },
    rateMovie(rating) {
      this.rating = rating;
    },
    toMyFilms() {
      this.$emit("my-films");
    },
  },
};
</script>
  
  <style scoped>
.home-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
.message-no-movies {
  display: flex;
  justify-content: center;
}

.home-tag {
  margin-top: 60px;
}

.film-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center;
}

.movie-suggestion {
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
.movie-suggestion:hover {
  transform: translateY(-5px);
  cursor: pointer;
}

.movie-poster {
  max-width: 100%;
  height: auto;
  border-radius: 5px;
  margin-bottom: 10px;
}

.button-group {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 10px;
}

.action-button {
  padding: 7px 12px;
  cursor: pointer;
  background-color: #1968acf3;
  color: rgba(255, 255, 255, 0.93);
  border: none;
  border-radius: 12px;
  transition: background-color 0.3s;
}

.action-button:hover {
  background-color: #013775cf;
}

.delete-button {
  position: absolute;
  top: 5px;
  right: -12px;
  border-radius: 14px;
  padding: 8px 10px;
  background-color: #c6413a63;
}

.delete-button:hover {
  background-color: #d0342b;
}

@media (max-width: 768px) {
  .film-grid {
    flex-direction: column;
    align-items: center;
  }

  .movie-suggestion {
    width: 100%;
  }

  .button-group {
    flex-direction: column;
  }

  .action-button {
    width: 100%;
    margin: 5px 0;
  }
}
.title {
  display: flex;
  justify-content: center;
}
</style>