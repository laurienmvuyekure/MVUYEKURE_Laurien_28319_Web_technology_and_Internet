<template>
  <div class="auth-container">
    <div class="auth-card" role="main" aria-labelledby="register-title">
      <div class="auth-header">
        <h1 id="register-title">Create Your Account</h1>
        <p class="auth-subtitle">Join TaskBuddy and organize your life</p>
      </div>
      
      <form @submit.prevent="handleRegister" class="auth-form" novalidate>
        <div v-if="generalError" class="general-error" role="alert">
          {{ generalError }}
        </div>

        <div v-if="successMessage" class="success-message" role="status">
          {{ successMessage }}
        </div>
        
        <div class="form-group">
          <label for="name" class="form-label">Full Name</label>
          <input
            type="text"
            id="name"
            v-model="name"
            @input="clearError('name')"
            required
            :aria-describedby="nameError ? 'name-error' : 'name-help'"
            :class="{ 'error': nameError }"
            placeholder="John Doe"
          />
          <div v-if="nameError" id="name-error" class="error-message" role="alert">
            {{ nameError }}
          </div>
          <div v-else id="name-help" class="help-text">
            Your full name
          </div>
        </div>
        
        <div class="form-group">
          <label for="email" class="form-label">Email Address</label>
          <input
            type="email"
            id="email"
            v-model="email"
            @input="clearError('email')"
            required
            autocomplete="email"
            :aria-describedby="emailError ? 'email-error' : 'email-help'"
            :class="{ 'error': emailError }"
            placeholder="you@example.com"
          />
          <div v-if="emailError" id="email-error" class="error-message" role="alert">
            {{ emailError }}
          </div>
          <div v-else id="email-help" class="help-text">
            We'll never share your email
          </div>
        </div>
        
        <div class="form-group">
          <label for="password" class="form-label">Password</label>
          <div class="password-input">
            <input
              :type="showPassword ? 'text' : 'password'"
              id="password"
              v-model="password"
              @input="clearError('password')"
              required
              autocomplete="new-password"
              :aria-describedby="passwordError ? 'password-error' : 'password-help'"
              :class="{ 'error': passwordError }"
              placeholder="Enter a strong password"
            />
            <button
              type="button"
              @click="togglePasswordVisibility"
              class="password-toggle"
              :aria-label="showPassword ? 'Hide password' : 'Show password'"
            >
              <span aria-hidden="true">{{ showPassword ? '👁️' : '👁️‍🗨️' }}</span>
            </button>
          </div>
          <div v-if="passwordError" id="password-error" class="error-message" role="alert">
            {{ passwordError }}
          </div>
          <div v-else id="password-help" class="help-text">
            Minimum 6 characters
          </div>
        </div>

        <div class="form-group">
          <label for="passwordconfirm" class="form-label">Confirm Password</label>
          <div class="password-input">
            <input
              :type="showPasswordConfirm ? 'text' : 'password'"
              id="passwordconfirm"
              v-model="passwordConfirm"
              @input="clearError('passwordConfirm')"
              required
              autocomplete="new-password"
              :aria-describedby="passwordConfirmError ? 'password-confirm-error' : null"
              :class="{ 'error': passwordConfirmError }"
              placeholder="Confirm your password"
            />
            <button
              type="button"
              @click="togglePasswordConfirmVisibility"
              class="password-toggle"
              :aria-label="showPasswordConfirm ? 'Hide password' : 'Show password'"
            >
              <span aria-hidden="true">{{ showPasswordConfirm ? '👁️' : '👁️‍🗨️' }}</span>
            </button>
          </div>
          <div v-if="passwordConfirmError" id="password-confirm-error" class="error-message" role="alert">
            {{ passwordConfirmError }}
          </div>
        </div>
        
        <div class="form-options">
          <label class="checkbox-label">
            <input
              type="checkbox"
              v-model="agreeToTerms"
              aria-describedby="terms-help"
            />
            <span>I agree to the 
              <router-link to="#" class="terms-link">Terms of Service</router-link> 
              and 
              <router-link to="#" class="terms-link">Privacy Policy</router-link>
            </span>
          </label>
        </div>
        
        <button 
          type="submit" 
          class="auth-button"
          :disabled="loading || !agreeToTerms"
          :aria-busy="loading"
        >
          <span v-if="loading">Creating Account...</span>
          <span v-else>Create Account</span>
        </button>
      </form>
      
      <div class="auth-footer">
        <p>
          Already have an account?
          <router-link to="/login" class="auth-link">
            Sign in here
          </router-link>
        </p>
      </div>
    </div>
    
    <div class="auth-features">
      <h2>Get Started Today</h2>
      <ul class="feature-list" role="list">
        <li>
          <span class="feature-icon" aria-hidden="true">✨</span>
          Organize your tasks efficiently
        </li>
        <li>
          <span class="feature-icon" aria-hidden="true">🎯</span>
          Track your progress
        </li>
        <li>
          <span class="feature-icon" aria-hidden="true">🔔</span>
          Get smart reminders
        </li>
        <li>
          <span class="feature-icon" aria-hidden="true">🤝</span>
          Collaborate with your team
        </li>
        <li>
          <span class="feature-icon" aria-hidden="true">📊</span>
          View detailed analytics
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import authService from '../services/authService'

export default {
  name: 'AuthRegister',
  setup() {
    const router = useRouter()
    
    const name = ref('')
    const email = ref('')
    const password = ref('')
    const passwordConfirm = ref('')
    const showPassword = ref(false)
    const showPasswordConfirm = ref(false)
    const agreeToTerms = ref(false)
    const loading = ref(false)
    
    const nameError = ref('')
    const emailError = ref('')
    const passwordError = ref('')
    const passwordConfirmError = ref('')
    const generalError = ref('')
    const successMessage = ref('')
    
    const validateForm = () => {
      let valid = true
      
      if (!name.value || name.value.trim().length < 2) {
        nameError.value = 'Name must be at least 2 characters'
        valid = false
      }
      
      if (!email.value) {
        emailError.value = 'Email is required'
        valid = false
      } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)) {
        emailError.value = 'Please enter a valid email address'
        valid = false
      }
      
      if (!password.value) {
        passwordError.value = 'Password is required'
        valid = false
      } else if (password.value.length < 6) {
        passwordError.value = 'Password must be at least 6 characters'
        valid = false
      }

      if (!passwordConfirm.value) {
        passwordConfirmError.value = 'Please confirm your password'
        valid = false
      } else if (password.value !== passwordConfirm.value) {
        passwordConfirmError.value = 'Passwords do not match'
        valid = false
      }
      
      if (!agreeToTerms.value) {
        generalError.value = 'You must agree to the Terms of Service'
        valid = false
      }
      
      return valid
    }
    
    const clearError = (field) => {
      if (field === 'name') nameError.value = ''
      if (field === 'email') emailError.value = ''
      if (field === 'password') passwordError.value = ''
      if (field === 'passwordConfirm') passwordConfirmError.value = ''
      generalError.value = ''
      successMessage.value = ''
    }
    
    const togglePasswordVisibility = () => {
      showPassword.value = !showPassword.value
    }

    const togglePasswordConfirmVisibility = () => {
      showPasswordConfirm.value = !showPasswordConfirm.value
    }
    
    const handleRegister = async () => {
      generalError.value = ''
      successMessage.value = ''
      
      if (!validateForm()) return
      
      loading.value = true
      
      try {
        // Simulate network delay
        await new Promise(resolve => setTimeout(resolve, 1200))
        
        // Use auth service
        const result = authService.register({
          name: name.value,
          email: email.value,
          password: password.value,
          passwordConfirm: passwordConfirm.value
        })
        
        if (result.success) {
          successMessage.value = result.message
          // Clear form
          name.value = ''
          email.value = ''
          password.value = ''
          passwordConfirm.value = ''
          agreeToTerms.value = false
          
          // Redirect to login
          setTimeout(() => {
            router.push('/login')
          }, 2000)
        } else {
          generalError.value = result.message
        }
        
      } catch (error) {
        generalError.value = error.message || 'Registration failed. Please try again.'
      } finally {
        loading.value = false
      }
    }
    
    return {
      name,
      email,
      password,
      passwordConfirm,
      showPassword,
      showPasswordConfirm,
      agreeToTerms,
      loading,
      nameError,
      emailError,
      passwordError,
      passwordConfirmError,
      generalError,
      successMessage,
      clearError,
      togglePasswordVisibility,
      togglePasswordConfirmVisibility,
      handleRegister
    }
  }
}
</script>

<style scoped>
.auth-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.auth-card {
  background: white;
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  max-width: 450px;
  margin: 0 auto;
  width: 100%;
}

.auth-header {
  text-align: center;
  margin-bottom: 30px;
}

#register-title {
  margin: 0 0 10px 0;
  color: #212529;
  font-size: 28px;
}

.auth-subtitle {
  margin: 0;
  color: #6c757d;
  font-size: 14px;
}

.auth-form {
  margin-bottom: 30px;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #495057;
}

input[type="email"],
input[type="password"],
input[type="text"] {
  width: 100%;
  padding: 12px;
  border: 2px solid #dee2e6;
  border-radius: 6px;
  font-size: 16px;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

input:focus {
  border-color: #0066cc;
  outline: none;
  box-shadow: 0 0 0 3px rgba(0,102,204,0.1);
}

input.error {
  border-color: #dc3545;
}

.password-input {
  position: relative;
}

.password-toggle {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  font-size: 20px;
  color: #6c757d;
}

.password-toggle:hover,
.password-toggle:focus {
  color: #0066cc;
  outline: none;
}

.error-message {
  color: #dc3545;
  font-size: 14px;
  margin-top: 4px;
}

.general-error {
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
  color: #721c24;
  padding: 12px;
  border-radius: 6px;
  margin-bottom: 20px;
  font-size: 14px;
}

.success-message {
  background-color: #d4edda;
  border: 1px solid #c3e6cb;
  color: #155724;
  padding: 12px;
  border-radius: 6px;
  margin-bottom: 20px;
  font-size: 14px;
}

.help-text {
  color: #6c757d;
  font-size: 12px;
  margin-top: 4px;
}

.form-options {
  margin-bottom: 20px;
}

.checkbox-label {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
}

.checkbox-label input {
  width: 18px;
  height: 18px;
  margin-top: 2px;
  cursor: pointer;
}

.terms-link {
  color: #0066cc;
  text-decoration: none;
}

.terms-link:hover {
  text-decoration: underline;
}

.auth-button {
  width: 100%;
  padding: 14px;
  background: #0066cc;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.auth-button:hover:not(:disabled) {
  background: #0052a3;
}

.auth-button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.auth-footer {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #dee2e6;
}

.auth-footer p {
  margin: 0;
  color: #6c757d;
  font-size: 14px;
}

.auth-link {
  color: #0066cc;
  text-decoration: none;
  font-weight: 500;
}

.auth-link:hover {
  text-decoration: underline;
}

.auth-features {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.auth-features h2 {
  margin-top: 0;
  margin-bottom: 30px;
  font-size: 28px;
}

.feature-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.feature-list li {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.feature-icon {
  font-size: 24px;
  min-width: 30px;
}

@media (max-width: 768px) {
  .auth-container {
    grid-template-columns: 1fr;
  }

  .auth-card {
    max-width: 100%;
    padding: 24px;
  }

  .auth-features {
    padding: 40px 24px;
  }

  #register-title {
    font-size: 24px;
  }

  .auth-features h2 {
    font-size: 24px;
  }
}
</style>
