<template>
  <div class="home-page">
    <div v-if="!movie" class="suggestion-options">
      <h2>Welcome back! What kind of movie are you in the mood for?</h2>
      <button @click="getRandomSuggestion">Surprise Me!</button>
      <button @click="showGenreSelection">I have a genre in mind</button>
    </div>

    <div v-if="showingGenreSelect" class="genre-selection">
      <h3>Select a genre:</h3>
      <select v-model="selectedGenre">
        <option value="action">Action</option>
        <option value="comedy">Comedy</option>
        <option value="drama">Drama</option>
        <option value="sciFi">Sci-Fi</option>
        <!-- Add more genres as needed -->
      </select>
      <button @click="getSuggestionByGenre">Get Suggestion</button>
    </div>
    <div v-if="movie" class="movie-suggestion">
      <h2>{{ movie.title }}</h2>
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
            <span>{{ movie.release_date }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-title">Production Companies:</span>
            <span>{{
              movie.production_companies != null
                ? movie.production_companies
                : "N/A"
            }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-title">Runtime:</span>
            <span
              >{{ movie.runtime != null ? movie.runtime : "N/A" }} mins</span
            >
          </div>
          <div class="detail-item">
            <span class="detail-title">Genre:</span>
            <span>{{ movie.genre.map((g) => g.name).join(", ") }}</span>
          </div>
          <div class="detail-item" v-if="movie.rating != null">
            <span class="detail-title">Rating:</span>
            <span>{{ movie.rating.toFixed(1) }}/10</span>
          </div>
          <div class="detail-item">
            <span class="detail-title"> Platforms: </span>
            <span>{{
              movie.platforms.map((p) => p.provider_name).join(", ")
            }}</span>
          </div>
        </div>
      </div>
      <div class="trailer">
        <iframe :src="movie.trailer" frameborder="0" allowfullscreen></iframe>
      </div>

      <div class="user-actions">
        <button @click="showRatingPopup = true">Rate this movie</button>
        <button @click="markAsWatched">Mark as Watched</button>
        <button @click="getNextMovie">
          I've already seen this, next please!
        </button>
      </div>
    </div>
    <!-- Star Rating Popup -->
    <div v-if="showRatingPopup" class="rating-popup">
      <div class="rating-content">
        <h3>Rate this movie</h3>
        <div class="stars">
          <span
            v-for="star in 10"
            :key="star"
            @click="rateMovie(star)"
            :class="{
              'star-filled': star <= hoverRating || star <= currentRating,
            }"
            @mouseover="hoverRating = star"
            @mouseleave="hoverRating = 0"
          >
            ★
          </span>
        </div>
        <p>{{ currentRating || hoverRating || 0 }} / 10</p>
        <button @click="closeRatingPopup">Close</button>
      </div>
    </div>
    <div class="mark-pop-up" v-if="showMarkPopUp">
      <div>
        <h2>Movie added to your films.</h2>
        <button @click="showMarkPopUp = false">Close</button>
      </div>
    </div>
  </div>
</template>
<script>
import { ref } from "vue";

export default {
  name: "HomePage",
  props: ["movie"],
  emits: ["get-suggestion", "rate-movie", "mark-watched", "next-movie"],
  setup(props, { emit }) {
    const showingGenreSelect = ref(false);
    const selectedGenre = ref("");
    const showMarkPopUp = ref(false);
    const showRatingPopup = ref(false);
    const currentRating = ref(0);
    const hoverRating = ref(0);

    const getRandomSuggestion = () => {
      emit("get-suggestion");
    };

    const showGenreSelection = () => {
      showingGenreSelect.value = true;
    };

    const getSuggestionByGenre = () => {
      emit("get-suggestion", { genre: selectedGenre.value });
      showingGenreSelect.value = false;
    };

    const openRatingPopup = () => {
      showRatingPopup.value = true;
    };

    const closeRatingPopup = () => {
      showRatingPopup.value = false;
    };

    const rateMovie = (rating) => {
      currentRating.value = rating;
      emit("rate-movie", rating);
    };

    const markAsWatched = () => {
      showMarkPopUp.value = true;
      emit("mark-watched");
    };

    const getNextMovie = () => {
      emit("next-movie");
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
      markAsWatched,
      getNextMovie,
    };
  },
};
</script>
  
  <style scoped>
.details {
  display: grid;
  grid-template-columns: auto 1fr; /* Two columns: one for the title and one for the value */
  gap: 5px; /* Space between columns */
  margin-left: 60px;
}

.detail-item {
  display: contents; /* Makes child elements align to the grid layout */
}

.detail-title {
  font-weight: bold;
  grid-column: 1; /* Align title in the first column */
}

.detail-item span:nth-child(2) {
  grid-column: 2; /* Align value in the second column */
}

.movie-poster {
  max-width: 200px;
  height: auto;
  margin-left: 60px;
}

.home-page {
  max-width: 800px;
  margin: 0 auto;
}

.infos {
  display: flex;
  margin-top: 40px;
  margin-bottom: 40px;
  justify-content: center;
}

.suggestion-options,
.genre-selection {
  margin-bottom: 20px;
}

button {
  margin: 10px;
  padding: 10px 20px;
  font-size: 1em;
  cursor: pointer;
  background-color: #db6109;
  color: white;
  border: none;
  border-radius: 5px;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #a36412;
}

select {
  padding: 10px;
  font-size: 1em;
  margin-right: 10px;
}

.movie-suggestion {
  background-color: #f4f4f4;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
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
}

.user-actions {
  margin-top: 20px;
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

.mark-pop-up button {
  margin-top: 15px;
}

.stars {
  font-size: 24px;
  cursor: pointer;
}

.star-filled {
  color: gold;
}
</style>