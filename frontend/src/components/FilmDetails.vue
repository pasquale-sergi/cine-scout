<template>
  <div v-if="isVisible" class="popup-overlay" @click.self="closePopup">
    <div class="popup-content">
      <button class="close-button" @click="closePopup">×</button>
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
              movie.production_companies ? movie.production_companies : "N/A"
            }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-title">Runtime:</span>
            <span>{{ movie.runtime ? movie.runtime : "N/A" }} mins</span>
          </div>
          <div class="detail-item">
            <span class="detail-title">Genre:</span>
            <span>{{
              movie.genres ? movie.genres.map((g) => g.name).join(", ") : "N/A"
            }}</span>
          </div>
          <div class="detail-item" v-if="movie.rating != null">
            <span class="detail-title">Rating:</span>
            <span>{{ movie.rating.toFixed(1) }}/10</span>
          </div>
          <div class="detail-item">
            <span class="detail-title">Platforms:</span>
            <span>{{
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
    isVisible: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    closePopup() {
      this.$emit("close");
    },
  },
};
</script>
  
  <style scoped>
.popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.popup-content {
  background: white;
  padding: 20px;
  border-radius: 8px;
  max-width: 600px;
  width: 100%;
  position: relative;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.movie-poster {
  max-width: 100%;
  height: auto;
  margin-bottom: 20px;
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
  flex-direction: column;
}

.detail-item {
  margin-bottom: 10px;
}

.detail-title {
  font-weight: bold;
}
</style>
  