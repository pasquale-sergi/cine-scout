<template>
  <div class="rating-popup">
    <div class="rating-content" v-if="showRatingPopup">
      <h3>Do you want to give this movie a rating?</h3>
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
      <button
        @click.stop="
          closeRatingPopup();
          markAsWatched();
          showMarkPopUp = true;
        "
        class="action-button"
      >
        Rate this movie
      </button>
      <button
        @click.stop="
          closeRatingPopup();
          markAsWatched();
          showMarkPopUp = true;
        "
        class="action-button"
      >
        Save without rating
      </button>
      <button class="close-btn" @click.stop="closeRatingPopup, popUpOff">
        &times;
      </button>
    </div>
  </div>
  <div class="mark-pop-up" v-if="showMarkPopUp">
    <div>
      <div>
        <p>Movie added to your list.</p>
      </div>
      <div class="buttons">
        <div class="action-button" @click="showMarkPopUp = false">Close</div>
        <div class="action-button" @click="toMyFilms">My Movies</div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from "vue";
export default {
  name: "RatingSystem",
  emits: ["mark-watched", "rate-movie", "my-films", "close-rating-pop-up"],
  setup(props, { emit }) {
    const showRatingPopup = ref(true);
    const currentRating = ref(0);
    const hoverRating = ref(0);
    const showMarkPopUp = ref(false);

    const openRatingPopup = () => {
      showRatingPopup.value = true;
    };
    const closeRatingPopup = () => {
      showRatingPopup.value = false;
    };

    const popUpOff = () => {
      emit("close-rating-pop-up");
    };
    const rateMovie = (rating) => {
      currentRating.value = rating;

      emit("rate-movie", rating);
    };

    const markAsWatched = () => {
      showMarkPopUp.value = false;

      emit("mark-watched");
    };

    const toMyFilms = () => {
      emit("my-films");
    };

    return {
      hoverRating,
      showRatingPopup,
      rateMovie,
      closeRatingPopup,
      openRatingPopup,
      markAsWatched,
      currentRating,
      toMyFilms,
      showMarkPopUp,
      popUpOff,
    };
  },
};
</script>

<style scoped>
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

.action-button:hover {
  background-color: #13444f;
}

.close-btn {
  position: absolute;
  top: 1px;
  right: 1px;
  color: #13444f4c;
  background: none;
  border: none;
  font-size: 1.5em;
  cursor: pointer;
  padding: 10px 12px;
  border-radius: 20px;
}

.close-btn:hover {
  color: black;
}

.action-button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
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
  position: relative;
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
</style>