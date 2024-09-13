<template>
  <div class="my-playlists">
    <div class="title-myplaylists">
      <h2>My Playlists</h2>
      <button class="create-playlist" @click="showCreatePlaylistPopup">
        Create a Playlist
      </button>
    </div>
    <div v-if="playlists.length > 0" class="playlist-grid">
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

    <!-- Create Playlist Pop-up -->
    <create-playlist
      v-if="isCreatePlaylistPopupVisible"
      @close="closeCreatePlaylistPopup"
      @create-playlist="handleCreatePlaylist"
    />

    <!-- Popup for adding movies -->
    <div
      v-if="isAddMoviesPopupVisible"
      class="playlist-details add-movies-popup"
    >
      <div class="playlist-details-content">
        <div class="title-div">
          <h2>Select Movies to Add to {{ selectedPlaylist.name }}</h2>
        </div>
        <div class="film-grid">
          <div v-for="movie in savedMovies" :key="movie.id" class="film-card">
            <img
              v-if="movie.poster_path"
              :src="movie.poster_path"
              :alt="movie.title"
              class="movie-poster"
            />
            <h3>{{ movie.title }}</h3>
            <button class="action-button" @click="addMovieToPlaylist(movie.id)">
              Add
            </button>
          </div>
        </div>
        <button class="close-button" @click="closeAddMoviesPopup">
          &times;
        </button>
        <transition name="fade">
          <div v-if="showPopUpMovieAdded" class="notification-popup">
            <p>Movie added to the playlist</p>
          </div>
        </transition>
      </div>
    </div>

    <!--pop up for playlist details-->
    <div v-if="showPlaylistInfo">
      <playlist-details
        v-if="selectedPlaylist"
        :playlist="selectedPlaylist"
        @close="closePlaylistDetails"
        @rename-playlist="handleRenamePlaylist"
        @add-movie="handleAddMovies"
        @remove-movie="handleRemoveMovie"
        @delete-playlist="handleDeletePlaylist"
        :moviesInPlaylist="moviesInPlaylist"
      ></playlist-details>
    </div>
  </div>
</template>
  
  <script>
import PlaylistDetails from "./PlaylistDetails.vue";
import CreatePlaylist from "./utils/CreatePlaylist.vue";
export default {
  name: "MyPlaylists",
  components: {
    PlaylistDetails,
    CreatePlaylist,
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
    moviesInPlaylist: {
      type: Array,
      required: true,
    },
  },
  emits: [
    "delete-movie-from-playlist",
    "add-movie-to-playlist",
    "get-movies-playlist",
    "get-playlist",
    "rename-the-playlist",
    "delete-the-playlist",
  ],
  data() {
    return {
      activePlaylist: null,
      isAddMoviesPopupVisible: false,
      selectedPlaylist: null,
      showPlaylistInfo: false,
      showPopUpMovieAdded: false,
      notificationTimeout: null,
      showCreatePlaylistModal: false,
      isCreatePlaylistPopupVisible: false,
    };
  },
  methods: {
    getRandomMovies(movies) {
      // Return a random selection of 4 movies
      return movies.length > 2 ? movies.slice(0, 2) : movies;
    },
    showPlaylistDetails(playlist) {
      this.selectedPlaylist = playlist;
      this.$emit("get-movies-playlist", playlist);
      this.showPlaylistInfo = true;
    },
    toggleOptionsMenu(playlist) {
      this.activePlaylist = this.activePlaylist === playlist ? null : playlist;
    },
    handleAddMovies(playlist) {
      this.$emit("get-movies-playlist", playlist);
      this.showCreatePlaylistModal = false;
      this.showPlaylistInfo = false;
      this.selectedPlaylist = playlist;
      this.isAddMoviesPopupVisible = true;
    },
    handleRenamePlaylist(playlist) {
      console.log("emitting renaming event");
      // Logic to update the playlist name
      this.$emit("rename-the-playlist", playlist);
      console.log(playlist);
    },
    handleDeletePlaylist(playlist) {
      this.showPlaylistInfo = false;
      this.$emit("delete-the-playlist", playlist);
    },
    addMovieToPlaylist(movieId) {
      // Emit event to add movie to the selected playlist
      this.$emit("add-movie-to-playlist", {
        playlistName: this.selectedPlaylist.name,
        movieId,
      });
      this.showNotification();
    },

    showCreatePlaylistPopup() {
      this.isCreatePlaylistPopupVisible = true;
    },

    closeCreatePlaylistPopup() {
      this.isCreatePlaylistPopupVisible = false;
    },

    handleCreatePlaylist(playlistData) {
      // Emit event to create new playlist
      this.$emit("create-the-playlist", playlistData);

      if (playlistData.addMovies) {
        // Show add movies popup for the newly created playlist
        this.handleAddMovies({ name: playlistData.name });
      }
    },

    showNotification() {
      this.showPopUpMovieAdded = true;
      clearTimeout(this.notificationTimeout);
      this.notificationTimeout = setTimeout(() => {
        this.showPopUpMovieAdded = false;
      }, 1000);
    },
    closeAddMoviesPopup() {
      this.isAddMoviesPopupVisible = false;
      this.selectedPlaylist = null;
    },
    closePlaylistDetails() {
      console.log("emitting get playtlist");
      this.$emit("get-playlist");
      this.showPlaylistInfo = false;
    },
    handleRemoveMovie({ playlistName, movieId }) {
      this.$emit("delete-movie-from-playlist", playlistName, movieId);
    },
    beforeUnmount() {
      clearTimeout(this.notificationTimeout);
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
.title-myplaylists {
  display: flex;
  justify-content: center;
}
.add-movies-popup {
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

.notification-popup {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: rgba(76, 175, 80, 0.9); /* Slightly transparent green */
  color: white;
  padding: 10px 14px;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  text-align: center;
  min-width: 200px;
}

/* Ensure the parent container allows for absolute positioning */
.playlist-details-content {
  position: relative;
}

/* Existing transition styles */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.title-div {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.film-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  justify-content: center;
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

.action-button {
  background-color: #407c94;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 8px;
  cursor: pointer;
  margin-top: 10px;
}

.action-button:hover {
  background-color: #13444f;
}

.close-button {
  position: absolute;
  background: none;
  color: #05012ca1;
  top: 15px;
  right: 2px;
  font-size: 1.5rem;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
}

.close-button:hover {
  color: #05012c2b;
}

.message {
  display: flex;
  justify-content: center;
}
.title-myfilms {
  display: flex;
  justify-content: center;
}
</style>
  