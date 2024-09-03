<template>
  <div class="my-films">
    <h2>My Watched Films</h2>
    <div class="film-grid" v-if="watchedMovies.length != 0">
      <div v-for="movie in watchedMovies" :key="movie.id" class="film-card">
        <img :src="movie.image" :alt="movie.title" />
        <h3>{{ movie.title }}</h3>
        <p>Your Rating: {{ movie.userRating || "Not rated" }}</p>
      </div>
    </div>
    <div v-else>
      <p>You have no saved movies.</p>
    </div>
  </div>
</template>
  
  <script>
export default {
  name: "MyFilms",
  props: ["watchedMovies"],
};
</script>
  
  <style scoped>
.my-films {
  max-width: 1000px;
  margin: 0 auto;
}

.film-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.film-card {
  background-color: #f4f4f4;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
}

.film-card:hover {
  transform: translateY(-5px);
}

.film-card img {
  width: 100%;
  height: auto;
  border-radius: 4px;
}

.film-card h3 {
  margin: 10px 0 5px;
  font-size: 1.1em;
}

.film-card p {
  margin: 5px 0;
  font-size: 0.9em;
  color: #666;
}
</style>