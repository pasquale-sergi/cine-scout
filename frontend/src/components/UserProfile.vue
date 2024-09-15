<template>
  <div v-if="showProfile" class="user-profile">
    <h2>User Profile</h2>
    <form @submit.prevent="updateProfile">
      <div class="form-group">
        <label for="email">Email:</label>
        <input type="email" id="email" v-model="email" required />
      </div>
      <div class="form-group">
        <label for="username">Username:</label>
        <input type="text" id="username" v-model="username" required />
      </div>
      <div class="form-group">
        <label for="password">New Password:</label>
        <input type="password" id="password" v-model="password" />
      </div>
      <button type="submit">Update Profile</button>
    </form>
  </div>
</template>
  
  <script>
import { ref, onMounted } from "vue";

export default {
  name: "UserProfile",
  props: {
    showProfile: Boolean,
  },
  setup(props, { emit }) {
    const email = ref("");
    const username = ref("");
    const password = ref("");

    onMounted(() => {
      // Fetch user data from your backend or local storage
      const storedUsername = localStorage.getItem("username");
      if (storedUsername) {
        username.value = storedUsername;
      }
      // You would typically fetch the email from your backend here
    });

    const updateProfile = async () => {
      // Here you would send the updated profile data to your backend
      // For now, we'll just update the local storage
      localStorage.setItem("username", username.value);

      // Clear the password field after update
      password.value = "";

      // Emit an event to inform the parent component that the profile was updated
      emit("profile-updated", { username: username.value, email: email.value });
    };

    return {
      email,
      username,
      password,
      updateProfile,
    };
  },
};
</script>
  
  <style scoped>
.user-profile {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
}

input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  background-color: #4caf50;
  color: white;
  padding: 10px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #45a049;
}
</style>