<template>
  <div class="user-profile">
    <h2>User Profile</h2>
    <div class="user-info">
      <p><strong>Email:</strong> {{ email }}</p>
      <p><strong>Username:</strong> {{ username }}</p>
    </div>
    <div class="password-section">
      <div v-if="!showPasswordSection">
        <button @click="togglePasswordSection">Change Password</button>
      </div>
      <div v-else>
        <form @submit.prevent="checkPassword">
          <div class="form-group password-input">
            <label for="currentPassword">Current Password:</label>
            <input
              type="password"
              id="currentPassword"
              v-model="currentPassword"
              required
            />
          </div>
          <button type="submit">Verify</button>
        </form>
      </div>
    </div>
    <div v-if="passwordCorrect" class="change-password-section">
      <h3>Change Password</h3>
      <form @submit.prevent="updatePassword">
        <div class="form-group">
          <label for="newPassword">New Password:</label>
          <input
            type="password"
            id="newPassword"
            v-model="newPassword"
            required
          />
        </div>
        <div class="form-group">
          <label for="confirmPassword">Confirm New Password:</label>
          <input
            type="password"
            id="confirmPassword"
            v-model="confirmPassword"
            required
          />
        </div>
        <button type="submit">Update Password</button>

        <div v-if="!updatePasswordFailed && updatePasswordAttempted">
          <p>Password updated</p>
        </div>
        <div v-else>
          <p>
            Password must be at least 8 characters long and contain at least one
            uppercase letter, one lowercase letter, one number, and one special
            character.
          </p>
        </div>
      </form>
    </div>
    <div v-else-if="updatePasswordAttempted && !passwordCorrect">
      <p>Password Incorrect. Try again</p>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, watch } from "vue";

export default {
  name: "UserProfile",
  props: {
    showProfile: Boolean,
    passwordCorrect: Boolean,
    updatePasswordFailed: Boolean,
  },
  emits: ["check-password", "update-password"],
  setup(props, { emit }) {
    const email = ref("");
    const username = ref("");
    const currentPassword = ref("");
    const newPassword = ref("");
    const confirmPassword = ref("");
    const showPasswordSection = ref(false);
    const passwordCheckAttempted = ref(false);
    const updatePasswordAttempted = ref(false);

    onMounted(() => {
      // Fetch user data from your backend or local storage
      const storedUsername = localStorage.getItem("username");
      const storedEmail = localStorage.getItem("email");
      if (storedUsername) username.value = storedUsername;
      if (storedEmail) email.value = storedEmail;
    });

    const togglePasswordSection = () => {
      showPasswordSection.value = !showPasswordSection.value;
      currentPassword.value = "";
      passwordCheckAttempted.value = false;
    };

    const checkPassword = () => {
      emit("check-password", currentPassword.value);
      passwordCheckAttempted.value = true;
    };

    const updatePassword = () => {
      if (newPassword.value !== confirmPassword.value) {
        alert("New passwords don't match!");
        return;
      }
      // Here you would update the password in your backend
      console.log("sending new password: ", newPassword.value);
      emit("update-password", newPassword.value);
      // For demo purposes, we'll just show an alert
      newPassword.value = "";
      confirmPassword.value = "";
      // showPasswordSection.value = false;
      updatePasswordAttempted.value = true;
    };

    // Reset passwordCheckAttempted when passwordCorrect changes
    watch(
      () => props.passwordCorrect,
      (newValue) => {
        if (newValue) {
          passwordCheckAttempted.value = false;
          updatePasswordAttempted.value = false;  
        }
      }
    );

    return {
      email,
      username,
      currentPassword,
      newPassword,
      confirmPassword,
      showPasswordSection,
      passwordCheckAttempted,
      togglePasswordSection,
      updatePassword,
      checkPassword,
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

.user-info {
  margin-bottom: 20px;
}

.password-section,
.change-password-section {
  margin-top: 20px;
}

.form-group {
  margin-bottom: 15px;
}

.password-input {
  position: relative;
}

.toggle-password {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
}

.eye-icon {
  color: #333;
  background: none;
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
  background-color: #378aff;
  color: white;
  padding: 10px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #4588a0;
}
</style>