<template>
  <div class="home-page">
    <div v-if="!movie && !isLoading" class="suggestion-options">
      <h2>Welcome! What kind of movie are you in the mood for?</h2>
      <div class="button-group">
        <button
          @click="getRandomSuggestion"
          class="action-button action-button-secondary"
        >
          Surprise me!
        </button>
        <button
          @click="showGenreSelection"
          class="action-button action-button-secondary"
        >
          I have a genre in mind
        </button>
      </div>
    </div>

    <div
      v-if="showingGenreSelect && !isLoading && !movie"
      class="genre-selection"
    >
      <h3>Select a genre:</h3>
      <div class="genre-selection-group">
        <select v-model="selectedGenre" class="genre-menu">
          <option v-for="genre of genres" :key="genre" :value="genre">
            {{ genre }}
          </option>
        </select>
        <button
          @click="getSuggestionByGenre"
          class="action-button action-button-secondary"
        >
          Get Suggestion
        </button>
      </div>
    </div>
    <div v-if="isLoading" class="spinner-overlay">
      <div class="spinner"></div>
    </div>

    <div v-if="isMovie && movie" class="movie-suggestion">
      <div class="infos-title">
        <h2>{{ movie.title }}</h2>
      </div>
      <p>{{ movie.description }}</p>
      <div class="infos">
        <img
          v-if="movie.image"
          :src="movie.image"
          :alt="movie.title"
          class="movie-poster"
        />
        <div class="details">
          <div class="detail-item">
            <span class="detail-title">Release Date:</span>
            <span class="detail-value">{{ movie.release_date }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-title">Runtime:</span>
            <span class="detail-value"
              >{{ movie.runtime != null ? movie.runtime : "N/A" }} mins</span
            >
          </div>
          <div class="detail-item">
            <span class="detail-title">Genre:</span>
            <span class="detail-value">{{
              movie.genre && movie.genre.length
                ? movie.genre.map((g) => g.name).join(", ")
                : "N/A"
            }}</span>
          </div>
          <div class="detail-item" v-if="movie.rating != null">
            <span class="detail-title">Rating:</span>
            <span class="detail-value">{{ movie.rating.toFixed(1) }}/10</span>
          </div>
          <div class="detail-item">
            <span class="detail-title">Platforms:</span>
            <span class="detail-value">{{
              movie.platforms && movie.platforms.length
                ? movie.platforms.map((p) => p.provider_name).join(", ")
                : "N/A"
            }}</span>
          </div>
        </div>
      </div>
      <div class="trailer">
        <iframe :src="movie.trailer" frameborder="0" allowfullscreen></iframe>
      </div>

      <div class="user-actions">
        <button
          @click="addToCurrentlyWatching"
          class="action-button action-button-secondary"
        >
          Add to currently watching
        </button>

        <button
          @click="openRatingPopup"
          class="action-button action-button-secondary"
        >
          Mark as Watched
        </button>
        <button
          @click="getNextMovie"
          class="action-button action-button-secondary"
        >
          Next please!
        </button>
      </div>
    </div>
    <div v-if="!isMovie && !isLoading" class="no-movie-found">
      <p>{{ message }}</p>
    </div>

    <!-- Star Rating and Saving Popup -->
    <div v-if="showRatingPopup" class="rating-popup">
      <rating-system
        @rate-movie="rateMovie"
        @mark-watched="saveMovie(movie)"
        @my-films="toMyFilms"
        @close-mark-pop-up="showRatingPopup = false"
      ></rating-system>
    </div>

    <!--start add to currently watching pop up-->
    <div class="mark-pop-up" v-if="showCWPopUp">
      <div>
        <h2>Movie added to the list.</h2>
        <div class="pop-up-buttons">
          <button
            @click="showCWPopUp = false"
            class="action-button action-button-secondary"
          >
            Close
          </button>
          <button
            @click="toMyCurrentlyWatching"
            class="action-button action-button-secondary"
          >
            My Currently Watching
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { ref, toRef, watch } from "vue";
import RatingSystem from "./utils/RatingSystem.vue";

export default {
  name: "HomePage",
  components: {
    RatingSystem,
  },
  props: {
    movie: {
      type: Object,
      default: () => ({}),
    },
    username: {
      type: String,
      required: true,
    },
  },
  emits: [
    "get-suggestion",
    "rate-movie",
    "save-movie",
    "next-movie",
    "get-suggestion-by-genre",
    "my-films",
    "add-to-currently-watching",
    "to-my-currently-watching",
  ],
  setup(props, { emit }) {
    const currentMovie = toRef(props, "movie");

    const showingGenreSelect = ref(false);
    const selectedGenre = ref("");
    const showMarkPopUp = ref(false);
    const showRatingPopup = ref(false);
    const currentRating = ref(0);
    const hoverRating = ref(0);
    const isLoading = ref(false);
    const message = ref(null);
    const isMovie = ref(null);
    const apiCallInProgress = ref(false);
    const showCWPopUp = ref(false);

    const genres = [
      "Action",
      "Adventure",
      "Comedy",
      "Crime",
      "Documentary",
      "Drama",
      "Family",
      "Fantasy",
      "History",
      "Horror",
      "Music",
      "Mistery",
      "Romance",
      "Science Fiction",
      "TV movie",
      "Thriller",
      "War",
      "Western",
    ];

    const getRandomSuggestion = () => {
      apiCallInProgress.value = true;
      isLoading.value = true;
      emit("get-suggestion");
    };
    watch(currentMovie, () => {
      if (apiCallInProgress.value) {
        if (!currentMovie.value || currentMovie.value.id === undefined) {
          isMovie.value = false;
          message.value =
            "Sorry but we haven't found any movie. Please try again.";
        } else {
          isMovie.value = true;
        }
        apiCallInProgress.value = false;
        isLoading.value = false;
      }
    });

    const showGenreSelection = () => {
      showingGenreSelect.value = true;
    };

    const getSuggestionByGenre = () => {
      apiCallInProgress.value = true;
      isLoading.value = true;
      emit("get-suggestion-by-genre", { genre: selectedGenre.value });
      showingGenreSelect.value = false;
    };

    const openRatingPopup = () => {
      showRatingPopup.value = true;
    };

    const addToCurrentlyWatching = () => {
      showCWPopUp.value = true;
      emit("add-to-currently-watching");
    };

    const closeRatingPopup = () => {
      showRatingPopup.value = false;
    };

    const rateMovie = (rating) => {
      currentRating.value = rating;

      emit("rate-movie", rating);
    };

    const saveMovie = (movie) => {
      showMarkPopUp.value = false;
      emit("save-movie", movie, currentRating.value);
    };

    const getNextMovie = () => {
      emit("next-movie");
    };

    const toMyFilms = () => {
      emit("my-films");
    };

    const toMyCurrentlyWatching = () => {
      emit("to-my-currently-watching");
    };

    return {
      showingGenreSelect,
      selectedGenre,
      showMarkPopUp,
      showRatingPopup,
      currentRating,
      hoverRating,
      getRandomSuggestion,
      showGenreSelection,
      getSuggestionByGenre,
      openRatingPopup,
      closeRatingPopup,
      rateMovie,
      saveMovie,
      getNextMovie,
      genres,
      toMyFilms,
      isLoading,
      currentMovie,
      message,
      isMovie,
      addToCurrentlyWatching,
      showCWPopUp,
      toMyCurrentlyWatching,
    };
  },
};
</script>
  
<style scoped>
.pop-up-buttons {
  display: flex;
}
.infos-title {
  display: flex;
  justify-content: center;
}
.home-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.no-movie-found {
  display: flex;
  justify-content: center;
}

.genre-menu {
  margin-top: 11px;
  width: 150px;
  height: 40px;
  text-align: center;
}
.suggestion-options,
.genre-selection {
  text-align: center;
  margin-bottom: 20px;
}

.button-group,
.genre-selection-group,
.user-actions {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 10px;
}

/* Styles for main action buttons on the home page */
.home-page .action-button {
  padding: 10px 20px;
  font-size: 1em;
  cursor: pointer;
  border: none;
  border-radius: 8px;
  transition: all 0.3s ease;
  font-weight: 500;
  position: relative;
  overflow: hidden;
}

.home-page .action-button-primary {
  background-color: #3089ac;
  color: rgba(255, 255, 255, 0.93);
}

.home-page .action-button-primary:hover {
  background-color: #13444f;
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.home-page .action-button-secondary {
  background-color: transparent;
  color: #3089ac;
  border: 2px solid #3089ac;
}

.home-page .action-button-secondary:hover {
  background-color: rgba(48, 137, 172, 0.1);
}

/* Underline effect on hover */
.home-page .action-button::after {
  content: "";
  position: absolute;
  width: 0;
  height: 2px;
  bottom: 0;
  left: 50%;
  background-color: currentColor;
  transition: all 0.3s ease;
}

.home-page .action-button:hover::after {
  width: 100%;
  left: 0;
}

/* Button group styling */
.home-page .button-group {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 50px;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .home-page .button-group {
    flex-direction: column;
    align-items: center;
  }

  .home-page .action-button {
    width: 100%;
    max-width: 250px;
  }
}

.action-button {
  margin: 10px;
  padding: 10px 20px;
  font-size: 1em;
  cursor: pointer;
  background-color: #407c94;
  color: rgba(255, 255, 255, 0.93);
  border: none;
  border-radius: 10px;
  transition: background-color 0.3s;
}

.close-btn {
  position: absolute;
  top: 10px;
  right: -9px;
}
.action-button:hover {
  background-color: #13444f;
}

.action-button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

select {
  padding: 10px;
  font-size: 1em;
  margin-right: 10px;
  border-radius: 5px;
  border: 1px solid #ccc;
}

.movie-suggestion {
  background-color: #f4f4f4;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  margin-top: 20px;
}

.infos {
  display: flex;
  margin-top: 50px;
}

.movie-poster {
  max-width: 200px;
  height: auto;
  margin-right: 40px;
  margin-left: 40px;
  border-radius: 5px;
}

.details {
  flex: 1;
  display: grid;
  grid-template-columns: max-content 1fr;
  gap: 5px 20px;
  align-items: baseline;
  margin-left: 60px;
  margin-top: 30px;
}

.detail-item {
  display: contents;
}

.loading-message {
  text-align: center;
  font-size: 1.2em;
  margin-top: 20px;
  color: #3089ac;
}

.detail-title {
  font-weight: bold;
  text-align: left;
  padding-right: 10px;
}

.detail-value {
  text-align: left;
  grid-column: 2;
}

.trailer {
  position: relative;
  padding-bottom: 56.25%; /* 16:9 aspect ratio */
  height: 0;
  overflow: hidden;
  margin: 20px 0;
}

.trailer iframe {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: 5px;
}

.rating-popup,
.mark-pop-up {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.rating-content,
.mark-pop-up > div {
  background-color: white;
  padding: 20px;
  border-radius: 5px;
  text-align: center;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  max-width: 400px;
  width: 90%;
}

.mark-pop-up {
  display: flex;
  justify-content: center;
  align-items: center;
}

.mark-pop-up > div {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.mark-pop-up h2 {
  margin-bottom: 20px;
  color: #333;
}

.stars {
  font-size: 24px;
  cursor: pointer;
  margin-bottom: 10px;
}

.star-filled {
  color: #ffd700;
}

.buttons {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.spinner-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 5px solid #f3f3f3;
  border-top: 5px solid #072b39;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@media (max-width: 768px) {
  .infos {
    flex-direction: column;
    align-items: center;
  }

  .movie-poster {
    margin: 0 0 20px 0;
  }

  .details {
    margin-left: 0;
  }

  .button-group,
  .genre-selection-group,
  .user-actions {
    flex-direction: column;
  }

  .action-button {
    width: 100%;
    margin: 5px 0;
  }
}
</style>