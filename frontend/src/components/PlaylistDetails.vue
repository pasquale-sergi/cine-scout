<template>
  <div class="playlist-details">
    <div class="playlist-details-content">
      <div class="title-div">
        <h2>{{ playlist.name }}</h2>
      </div>
      <div class="playlist-options">
        <button class="action-playlist-button" @click="handleRename">
          Rename Playlist
        </button>
        <button class="action-playlist-button" @click="handleAddMovie">
          Add Movie
        </button>
        <button class="action-playlist-button" @click="handleDeletePlaylist">
          Delete Playlist
        </button>
      </div>
      <div v-if="playlist.movies" class="film-grid">
        <div
          v-for="movie in moviesInPlaylist"
          :key="movie.id"
          class="film-card"
        >
          <img
            v-if="movie.poster_path"
            :src="movie.poster_path"
            :alt="movie.title"
            class="movie-poster"
          />
          <h3>{{ movie.title }}</h3>
          <div class="button-container">
            <button class="action-button" @click="showMovieDetails(movie)">
              Show Details
            </button>
            <button
              class="action-button"
              @click="handleRemoveMovie((this.selectedMovie = movie))"
            >
              Remove
            </button>
          </div>
        </div>
      </div>
      <div v-else class="message">
        <p>This playlist has no movies.</p>
      </div>
      <button class="close-button" @click="closeDetails">&times;</button>

      <!-- Ensure the Close Button is Inside the Playlist Content -->
    </div>

    <!-- Movie Details Popup -->
    <film-details
      v-if="selectedMovie"
      :movie="selectedMovie"
      :isVisible="isMovieDetailsVisible"
      @close="closeMovieDetails"
    ></film-details>
  </div>
</template>
  
  
  
  <script>
import FilmDetails from "./FilmDetails.vue";

export default {
  name: "PlaylistDetails",
  components: {
    FilmDetails,
  },
  props: {
    playlist: {
      type: Object,
      required: true,
    },
    isVisible: {
      type: Boolean,
      required: true,
    },
    moviesInPlaylist: {
      type: Array,
      required: true,
    },
  },
  emits: [
    "close",
    "rename-playlist",
    "add-movie",
    "remove-movie",
    "delete-playlist",
  ],
  data() {
    return {
      selectedMovie: null,
      isMovieDetailsVisible: false,
    };
  },
  methods: {
    closeDetails() {
      this.$emit("close");
    },
    handleRename() {
      const newName = prompt("Enter new playlist name:", this.playlist.name);
      if (newName && newName !== this.playlist.name) {
        this.$emit("rename-playlist", {
          playlistName: this.playlist.name,
          newName,
        });
      }
    },
    handleAddMovie() {
      this.$emit("add-movie", this.playlist);
    },
    handleRemoveMovie(movie) {
      this.$emit("remove-movie", {
        playlistName: this.playlist.name,
        movieId: movie.id,
      });
    },
    handleDeletePlaylist() {
      if (
        confirm(
          `Are you sure you want to delete the playlist "${this.playlist.name}"?`
        )
      ) {
        this.$emit("delete-playlist", this.playlist.id);
      }
    },
    showMovieDetails(movie) {
      this.selectedMovie = movie;
      this.isMovieDetailsVisible = true;
    },
    closeMovieDetails() {
      this.selectedMovie = null;
      this.isMovieDetailsVisible = false;
    },
  },
};
</script>
  
  <style scoped>
.playlist-details {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.playlist-details-content {
  position: relative;
  background-color: #ffffffe0;
  padding: 20px;
  border-radius: 8px;
  max-width: 80%;
  max-height: 80%;
  overflow-y: auto;
}

.playlist-options {
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
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

.movie-poster {
  width: 100%;
  border-radius: 4px;
  margin-bottom: 10px;
}
.close-button {
  position: absolute;
  background: none;
  top: -15px;
  right: 2px;
  color: #002b66;
  font-size: 1.5rem;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 20px; /* Space from the top */
}

.close-button:hover {
  color: #05012ca1;
}

.action-button {
  background-color: #407c94;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 8px;
  cursor: pointer;
  margin-right: 7px;
}

.action-button:hover {
  background-color: #13444f;
}

.action-playlist-button {
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

.action-playlist-button::after {
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

.action-playlist-button:hover::after {
  transform: scaleX(1);
  transform-origin: bottom left;
}
.message {
  text-align: center;
  margin-top: 20px;
}

.title-div {
  display: flex;
  justify-content: center;
}
</style>