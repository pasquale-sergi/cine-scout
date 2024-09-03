<template>
  <div class="login-form">
    <h2>Welcome to Movie of the Day!</h2>
    <div v-if="showLogin">
      <form @submit.prevent="login">
        <input v-model="username" type="text" placeholder="Username" required />
        <input
          v-model="password"
          type="password"
          placeholder="Password"
          required
        />
        <button type="submit">Login</button>
      </form>
      <p v-if="message" :class="{ 'error-message': authFailed }">
        {{ message }}
      </p>
      <p>Not a user? <a href="#" @click.prevent="toggleForm">Sign up</a></p>
    </div>
    <div v-else>
      <form @submit.prevent="signUp">
        <input v-model="email" type="text" placeholder="Email" required />
        <input v-model="username" type="text" placeholder="Username" required />
        <input
          v-model="password"
          type="password"
          placeholder="Password"
          required
        />
        <input
          v-model="confirmPassword"
          type="password"
          placeholder="Confirm Password"
          required
        />
        <button type="submit">Register</button>
      </form>
      <p v-if="message" :class="{ 'error-message': authFailed }">
        {{ message }}
      </p>
      <p>Already a user? <a href="#" @click.prevent="toggleForm">Login</a></p>
    </div>
  </div>
</template>
<script>
import { ref, reactive, toRefs } from "vue";

export default {
  name: "LoginForm",
  setup(props, { emit }) {
    const confirmPassword = ref("");
    const email = ref("");
    const username = ref("");
    const password = ref("");
    const state = reactive({
      message: "",
      authFailed: false,
      showLogin: true,
    });

    const login = async () => {
      try {
        const response = await fetch("http://localhost:8080/auth/login", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            username: username.value,
            password: password.value,
          }),
        });

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log("DATA", data);
        if (data.user == null) {
          console.error("This Login Failed");
          state.authFailed = true;
          state.message = "Incorrect username or password.";
        } else {
          console.log("Login successful:", data);
          state.authFailed = false;
          emit("login", { username: username.value, token: data.jwt }); // Assuming the backend returns a token
        }
      } catch (error) {
        console.error("Login failed:", error);
        state.authFailed = true;
        state.message = "An error occurred. Please try again.";

        // Here you might want to show an error message to the user
      }
    };

    const signUp = async () => {
      console.log(password.value);
      console.log(confirmPassword.value);
      if (password.value !== confirmPassword.value) {
        state.signingFailed = true;
        state.message = "Passwords do not match.";
        return;
      }
      try {
        const response = await fetch("http://localhost:8080/auth/sign-up", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            email: email.value,
            username: username.value,
            password: password.value,
          }),
        });

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log("Registration successful:", data);
        state.authFailed = false;
        state.message = "Registration successful! Please log in.";
        state.showLogin = true;
      } catch (error) {
        console.error("Registration failed:", error);
        state.signingFailed = true;
        state.message = "An error occurred. Please try again.";

        // Here you might want to show an error message to the user
      }
    };
    const toggleForm = () => {
      state.showLogin = !state.showLogin;
      state.message = "";
      state.authFailed = false;
      username.value = "";
      password.value = "";
      confirmPassword.value = "";
      email.value = "";
    };

    return {
      email,
      signUp,
      toggleForm,
      username,
      password,
      confirmPassword,
      login,
      ...toRefs(state),
    };
  },
};
</script>
  
  <style scoped>
.login-form {
  max-width: 300px;
  margin: 0 auto;
}

input {
  width: 100%;
  padding: 10px;
  margin: 10px 0;
  border: 1px solid #ddd;
  border-radius: 4px;
}

button {
  width: 100%;
  padding: 10px;
  background-color: #db6109;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #8f5b06e0;
}
</style>