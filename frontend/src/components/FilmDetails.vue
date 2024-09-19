<template>
  <div class="popup-overlay" @click.self="closePopup">
    <div class="popup-content">
      <button class="close-button" @click="closePopup">×</button>
      <div class="title">
        <h2>{{ movie.title }}</h2>
      </div>
      <p>{{ movie.overview }}</p>
      <div class="infos">
        <img
          v-if="movie.poster_path"
          :src="movie.poster_path"
          :alt="movie.title"
          class="movie-poster"
        />
        <div class="details">
          <div class="detail-item">
            <span class="detail-title">Release Date:</span>
            <span class="detail-value">{{ movie.release_date }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-title">Production Companies:</span>
            <span class="detail-value">{{
              movie.production_companies ? movie.production_companies : "N/A"
            }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-title">Runtime:</span>
            <span class="detail-value"
              >{{ movie.runtime ? movie.runtime : "N/A" }} mins</span
            >
          </div>
          <div class="detail-item">
            <span class="detail-title">Genre:</span>
            <span class="detail-value">{{
              movie.genres ? movie.genres.map((g) => g.name).join(", ") : "N/A"
            }}</span>
          </div>
          <div class="detail-item" v-if="movie.rating != null">
            <span class="detail-title">Rating:</span>
            <span class="detail-value">{{ movie.rating.toFixed(1) }}/10</span>
          </div>
          <div class="detail-item">
            <span class="detail-title">Platforms:</span>
            <span class="detail-value">{{
              movie.platforms
                ? movie.platforms.map((p) => p.provider_name).join(", ")
                : "N/A"
            }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
  
  <script>
export default {
  name: "FilmDetails",
  props: {
    movie: {
      type: Object,
      required: true,
    },
    // isVisible: {
    //   type: Boolean,
    //   default: false,
    // },
  },
  methods: {
    closePopup() {
      this.$emit("close");
    },
  },
};
</script>
  
  <style scoped>
.title {
  display: flex;
  justify-content: center;
}
.popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.popup-content {
  background: rgba(255, 255, 255, 0.907);
  padding: 20px;
  border-radius: 10px;
  max-width: 800px;
  width: 100%;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: relative;
}

.close-button {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
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
}

.details {
  flex: 1;
  display: grid;
  grid-template-columns: max-content 1fr;
  gap: 5px 20px;
  align-items: baseline;
  margin-left: 100px;
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
}
</style>