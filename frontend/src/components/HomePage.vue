<template>
  <div class="home-page">
    <div v-if="!movie" class="suggestion-options">
      <h2>Welcome! What kind of movie are you in the mood for?</h2>
      <div class="button-group">
        <button @click="getRandomSuggestion" class="action-button">
          Surprise me!
        </button>
        <button @click="showGenreSelection" class="action-button">
          I have a genre in mind
        </button>
      </div>
    </div>

    <div v-if="showingGenreSelect" class="genre-selection">
      <h3>Select a genre:</h3>
      <div class="genre-selection-group">
        <select v-model="selectedGenre" class="genre-menu">
          <option v-for="genre of genres" :key="genre" :value="genre">
            {{ genre }}
          </option>
        </select>
        <button @click="getSuggestionByGenre" class="action-button">
          Get Suggestion
        </button>
      </div>
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
          <!-- <div class="detail-item">
            <span class="detail-title">Production Companies:</span>
            <span class="detail-value">{{
              movie.production_companies != null
                ? movie.production_companies
                : "N/A"
            }}</span>
          </div> -->
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
        <button @click="addToCurrentlyWatching" class="action-button">
          Add to currently watching
        </button>

        <button @click="openRatingPopup" class="action-button">
          Mark as Watched
        </button>
        <button @click="getNextMovie" class="action-button">
          Next please!
        </button>
      </div>
    </div>
    <div v-if="!isMovie" class="no-movie-found">
      <p>{{ message }}</p>
    </div>
    <!-- Star Rating Popup -->
    <div v-if="showRatingPopup" class="rating-popup">
      <rating-system
        @rate-movie="rateMovie"
        @mark-watched="saveMovie(movie)"
        @my-films="toMyFilms"
      ></rating-system>
    </div>
    <!--start add to currently watching pop up-->
    <div class="mark-pop-up" v-if="showCWPopUp">
      <div>
        <h2>Movie added to the list.</h2>
        <div class="pop-up-buttons">
          <button @click="showCWPopUp = false" class="action-button">
            Close
          </button>
          <button @click="toMyCurrentlyWatching" class="action-button">
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
      }
    });

    const showGenreSelection = () => {
      showingGenreSelect.value = true;
    };

    const getSuggestionByGenre = () => {
      apiCallInProgress.value = true;

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
  padding: 7px 10px;
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

.spinner-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100px;
}

.spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-left-color: #db6109;
  border-radius: 50%;
  width: 40px;
  height: 40px;
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