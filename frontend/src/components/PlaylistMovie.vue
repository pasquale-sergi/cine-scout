<template>
  <div class="my-playlists">
    <div class="title-myplaylists">
      <h2>My Playlists</h2>
    </div>
    <div v-if="playlists" class="playlist-grid">
      <div
        v-for="playlist in playlists"
        :key="playlist.id"
        class="playlist-card"
      >
        <!-- Collage of 4 random movies -->
        <div class="collage">
          <div
            v-for="(movie, index) in getRandomMovies(playlist.movies)"
            :key="index"
            class="collage-item"
          >
            <img
              v-if="movie.poster_path"
              :src="movie.poster_path"
              :alt="movie.title"
              class="collage-poster"
            />
          </div>
        </div>
        <h3>{{ playlist.name }}</h3>

        <div class="button-container">
          <button @click="showPlaylistDetails(playlist)">Show Details</button>
          <button @click="toggleOptionsMenu(playlist)">Options</button>

          <div
            v-if="activePlaylist && activePlaylist.id === playlist.id"
            class="options-menu"
          >
            <button @click="handleAddMovies(playlist)">Add Movies</button>
            <button @click="handleUpdateName(playlist)">Update Name</button>
            <button @click="handleRemovePlaylist(playlist)">Remove</button>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="message">
      <p>You have no playlists.</p>
    </div>

    <!-- Popup for adding movies -->
    <div v-if="isAddMoviesPopupVisible" class="add-movies-popup">
      <h2>Select Movies to Add to {{ selectedPlaylist.name }}</h2>
      <div class="film-grid">
        <div v-for="movie in savedMovies" :key="movie.id" class="film-card">
          <img
            v-if="movie.poster_path"
            :src="movie.poster_path"
            :alt="movie.title"
            class="movie-poster"
          />
          <h3>{{ movie.title }}</h3>
          <button @click="addMovieToPlaylist(movie)">Add</button>
        </div>
      </div>
      <button @click="closeAddMoviesPopup">Close</button>
    </div>

    <!--pop up for playlist details-->
    <div v-if="showPlaylistInfo">
      <playlist-details
        v-if="selectedPlaylist"
        :playlist="selectedPlaylist"
        @close="closePlaylistDetails"
        @rename-playlist="handleRenamePlaylist"
        @add-movie="handleAddMovie"
        @remove-movie="handleRemoveMovie"
        @delete-playlist="handleDeletePlaylist"
      ></playlist-details>
    </div>
  </div>
</template>
  
  <script>
import PlaylistDetails from "./PlaylistDetails.vue";
export default {
  name: "MyPlaylists",
  components: {
    PlaylistDetails,
  },
  props: {
    playlists: {
      type: Object,
      required: true,
    },
    savedMovies: {
      type: Array,
      required: true,
    },
  },
  emits: ["delete-movie-from-playlist"],
  data() {
    return {
      activePlaylist: null,
      isAddMoviesPopupVisible: false,
      selectedPlaylist: null,
      showPlaylistInfo: false,
    };
  },
  methods: {
    getRandomMovies(movies) {
      // Return a random selection of 4 movies
      return movies.length > 4 ? movies.slice(0, 4) : movies;
    },
    showPlaylistDetails(playlist) {
      console.log(playlist);
      this.selectedPlaylist = playlist;
      // Logic to show details for the playlist
      this.showPlaylistInfo = true;
    },
    toggleOptionsMenu(playlist) {
      this.activePlaylist = this.activePlaylist === playlist ? null : playlist;
    },
    handleAddMovies(playlist) {
      this.selectedPlaylist = playlist;
      this.isAddMoviesPopupVisible = true;
    },
    handleUpdateName(playlist) {
      // Logic to update the playlist name
      console.log(playlist);
    },
    handleRemovePlaylist(playlist) {
      // Emit event to remove playlist
      this.$emit("remove-playlist", playlist);
    },
    addMovieToPlaylist(movie) {
      // Emit event to add movie to the selected playlist
      this.$emit("add-movie-to-playlist", {
        playlist: this.selectedPlaylist,
        movie,
      });
    },
    closeAddMoviesPopup() {
      this.isAddMoviesPopupVisible = false;
      this.selectedPlaylist = null;
    },
    closePlaylistDetails() {
      this.showPlaylistInfo = false;
    },
    handleRemoveMovie(playlistName, movieId) {
      this.$emit("delete-movie-from-playlist", playlistName, movieId);
    },
  },
};
</script>
  
  <style scoped>
.my-playlists {
  max-width: 1000px;
  margin: 0 auto;
}

.playlist-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.playlist-card {
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

.playlist-card:hover {
  transform: translateY(-5px);
}

.collage {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-bottom: 10px;
}

.collage-item {
  width: 48%;
  height: 48%;
}

.collage-item img {
  width: 100%;
  height: auto;
  border-radius: 4px;
}

button {
  background-color: #407c94;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 8px;
  cursor: pointer;
  margin-right: 7px;
}

button:hover {
  background-color: #13444f;
}

.options-menu {
  position: absolute;
  top: 100%;
  left: 0;
  background-color: #f9f9f9;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  padding: 10px;
  z-index: 1;
}

.add-movies-popup {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  z-index: 1000;
}

.add-movies-popup .film-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 20px;
}
</style>
  