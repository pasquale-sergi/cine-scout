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

    const validateEmail = (email) => {
      const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      return re.test(email);
    };

    const validatePassword = (password) => {
      // At least 8 characters, 1 uppercase, 1 lowercase, 1 number, 1 special character
      const re =
        /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
      return re.test(password);
    };

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
        if (data.username == null || data.jwt == null) {
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
      // Reset error message
      state.message = "";
      state.authFailed = false;

      if (!validateEmail(email.value)) {
        state.authFailed = true;
        state.message = "Please enter a valid email address.";
        return;
      }

      if (!validatePassword(password.value)) {
        state.authFailed = true;
        state.message =
          "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character.";
        return;
      }

      if (password.value !== confirmPassword.value) {
        state.authFailed = true;
        state.message = "Passwords do not match.";
        return;
      }

      console.log(
        "data sending to the backend: " +
          "username: " +
          username.value +
          "email: " +
          email.value
      );

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
        state.authFailed = true;
        state.message = "An error occurred. Please try again.";
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
  max-width: 400px;
  margin: 5% auto;
  padding: 30px;
  background-color: #f8f9fa;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  text-align: center;
}

h2 {
  color: #343a40;
  margin-bottom: 20px;
  font-size: 1.5rem;
}

form {
  display: flex;
  flex-direction: column;
}

input {
  padding: 10px;
  margin: 10px 0;
  border: 1px solid #ced4da;
  border-radius: 4px;
  font-size: 1rem;
  background-color: #fff;
  transition: all 0.3s ease;
}

input:focus {
  border-color: #007bff;
  outline: none;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.5);
}

button {
  background-color: #045fc0;
  color: white;
  padding: 10px;
  margin-top: 10px;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

button:hover {
  background-color: #0056b3;
}

p {
  margin-top: 10px;
  font-size: 0.9rem;
}

p a {
  color: #007bff;
  text-decoration: none;
}

p a:hover {
  text-decoration: underline;
}

.error-message {
  color: #dc3545;
  font-weight: bold;
  margin-top: 10px;
}
</style>