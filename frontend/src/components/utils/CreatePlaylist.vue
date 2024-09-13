<template>
  <div class="popup-overlay" @click.self="close">
    <div class="popup-window">
      <button @click="close" class="close-button">&times;</button>
      <h2>Create New Playlist</h2>
      <div class="input-group">
        <label for="playlist-title">Title:</label>
        <input
          id="playlist-title"
          v-model="title"
          placeholder="Enter playlist title (max 50 characters)"
          maxlength="50"
        />
      </div>
      <div class="input-group">
        <label for="playlist-description">Description:</label>
        <textarea
          id="playlist-description"
          v-model="description"
          placeholder="Enter playlist description (max 200 characters)"
          maxlength="200"
          rows="3"
        ></textarea>
      </div>
      <div class="checkbox-group">
        <input type="checkbox" id="add-movies" v-model="addMovies" />
        <label for="add-movies">Add movies after creating</label>
      </div>
      <div class="button-create">
        <button @click="createPlaylist" class="create-button">
          Create Playlist
        </button>
      </div>
    </div>
  </div>
</template>
  
  <script>
export default {
  name: "CreatePlaylistPopup",
  data() {
    return {
      title: "",
      description: "",
      addMovies: false,
    };
  },
  methods: {
    createPlaylist() {
      if (this.title.trim()) {
        // We still check if the title is not just whitespace
        this.$emit("create-playlist", {
          name: this.title,
          description: this.description,
          addMovies: this.addMovies,
        });
        this.close();
      } else {
        // Optionally, show an error message if the title is empty or just whitespace
        alert("Please enter a playlist title");
      }
    },
    close() {
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
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.popup-window {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  width: 500px;
  height: 60%;
  position: relative;
}

.close-button {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
}

.input-group {
  margin-bottom: 15px;
}

.input-group label {
  display: block;
  margin-bottom: 5px;
}

.input-group input,
.input-group textarea {
  width: 100%;
  padding: 5px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.checkbox-group {
  margin-bottom: 15px;
}

.create-button {
  background-color: #4caf50;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 4px;
  cursor: pointer;
  width: 70%;
  position: absolute;
  bottom: 10px;
}

.button-create {
  display: flex;
  justify-content: center;
}

.create-button:hover {
  background-color: #45a049;
}
</style>