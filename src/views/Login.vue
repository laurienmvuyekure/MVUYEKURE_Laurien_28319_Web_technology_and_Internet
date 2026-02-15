<template>
  <div class="auth-container">
    <div class="auth-card" role="main" aria-labelledby="login-title">
      <div class="auth-header">
        <h1 id="login-title">Login to TaskBuddy</h1>
        <p class="auth-subtitle">Access your tasks and collaborate with others</p>
      </div>
      
      <form @submit.prevent="handleLogin" class="auth-form" novalidate>
        <div v-if="generalError" class="general-error" role="alert">
          {{ generalError }}
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
            Enter your registered email address
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
              autocomplete="current-password"
              :aria-describedby="passwordError ? 'password-error' : 'password-help'"
              :class="{ 'error': passwordError }"
              placeholder="Enter your password"
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
            Enter your password
          </div>
        </div>
        
        <div class="form-options">
          <label class="checkbox-label">
            <input
              type="checkbox"
              v-model="rememberMe"
              aria-describedby="remember-help"
            />
            <span>Remember me</span>
          </label>
          <router-link to="/forgot-password" class="forgot-link">
            Forgot password?
          </router-link>
        </div>
        
        <button 
          type="submit" 
          class="auth-button"
          :disabled="loading"
          :aria-busy="loading"
        >
          <span v-if="loading">Logging in...</span>
          <span v-else>Login</span>
        </button>
        
        <div class="auth-divider">
          <span>or</span>
        </div>
        
        <button 
          type="button" 
          @click="signInWithGoogle"
          class="google-button"
          :disabled="loading"
        >
          <span class="google-icon" aria-hidden="true">G</span>
          Continue with Google
        </button>
      </form>
      
      <div class="auth-footer">
        <p>
          Don't have an account?
          <router-link to="/register" class="auth-link">
            Sign up here
          </router-link>
        </p>
        
        <router-link to="/demo" class="demo-link">
          Try demo account
        </router-link>
      </div>
    </div>
    
    <div class="auth-features">
      <h2>Accessible Task Management</h2>
      <ul class="feature-list" role="list">
        <li>
          <span class="feature-icon" aria-hidden="true">👁️</span>
          Screen reader optimized
        </li>
        <li>
          <span class="feature-icon" aria-hidden="true">🎨</span>
          Customizable accessibility settings
        </li>
        <li>
          <span class="feature-icon" aria-hidden="true">🔒</span>
          Secure and private
        </li>
        <li>
          <span class="feature-icon" aria-hidden="true">🤝</span>
          Collaborate with others
        </li>
        <li>
          <span class="feature-icon" aria-hidden="true">🔔</span>
          Smart reminders
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
  name: 'AuthLogin',
  setup() {
    const router = useRouter()
    
    const email = ref('')
    const password = ref('')
    const rememberMe = ref(false)
    const showPassword = ref(false)
    const loading = ref(false)
    
    const emailError = ref('')
    const passwordError = ref('')
    const generalError = ref('')
    
    const validateForm = () => {
      let valid = true
      
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
      }
      
      return valid
    }
    
    const clearError = (field) => {
      if (field === 'email') emailError.value = ''
      if (field === 'password') passwordError.value = ''
      generalError.value = ''
    }
    
    const togglePasswordVisibility = () => {
      showPassword.value = !showPassword.value
    }
    
    const handleLogin = async () => {
      generalError.value = ''
      
      if (!validateForm()) {
        generalError.value = 'Please fill in all required fields correctly'
        return
      }
      
      loading.value = true
      
      try {
        // Simulate network delay
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        // Use auth service
        const result = authService.login(email.value, password.value)
        
        if (result.success) {
          // Remember email if checkbox is checked
          if (rememberMe.value) {
            localStorage.setItem('rememberEmail', email.value)
          } else {
            localStorage.removeItem('rememberEmail')
          }
          
          // Navigate to dashboard
          setTimeout(() => {
            router.push('/dashboard')
          }, 500)
        } else {
          generalError.value = result.message
        }
        
      } catch (error) {
        generalError.value = error.message || 'Login failed. Please try again.'
      } finally {
        loading.value = false
      }
    }
    
    const signInWithGoogle = () => {
      generalError.value = 'Google authentication coming soon'
    }
    
    // Load remembered email if available
    const rememberEmail = localStorage.getItem('rememberEmail')
    if (rememberEmail) {
      email.value = rememberEmail
      rememberMe.value = true
    }
    
    return {
      email,
      password,
      rememberMe,
      showPassword,
      loading,
      emailError,
      passwordError,
      generalError,
      clearError,
      togglePasswordVisibility,
      handleLogin,
      signInWithGoogle
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
  max-width: 400px;
  margin: 0 auto;
  width: 100%;
}

.auth-header {
  text-align: center;
  margin-bottom: 30px;
}

#login-title {
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

.general-error [role="alert"] {
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
  color: #721c24;
  padding: 12px;
  border-radius: 6px;
  margin-bottom: 20px;
  font-size: 14px;
  /*role: alert;*/
}

.help-text {
  color: #6c757d;
  font-size: 12px;
  margin-top: 4px;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
}

.checkbox-label input {
  width: 16px;
  height: 16px;
}

.forgot-link {
  color: #0066cc;
  text-decoration: none;
  font-size: 14px;
}

.forgot-link:hover {
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
  transition: background-color 0.2s;
}

.auth-button:hover:not(:disabled),
.auth-button:focus:not(:disabled) {
  background: #0055aa;
  outline: 3px solid #ff9900;
}

.auth-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.auth-divider {
  display: flex;
  align-items: center;
  margin: 20px 0;
  color: #6c757d;
}

.auth-divider::before,
.auth-divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #dee2e6;
}

.auth-divider span {
  padding: 0 15px;
}

.google-button {
  width: 100%;
  padding: 14px;
  background: white;
  color: #212529;
  border: 2px solid #dee2e6;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  transition: all 0.2s;
}

.google-button:hover:not(:disabled),
.google-button:focus:not(:disabled) {
  border-color: #0066cc;
  background: #f8f9fa;
  outline: none;
}

.google-icon {
  width: 24px;
  height: 24px;
  background: #4285f4;
  color: white;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.auth-footer {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #dee2e6;
}

.auth-footer p {
  margin: 0 0 15px 0;
  color: #6c757d;
}

.auth-link {
  color: #0066cc;
  text-decoration: none;
  font-weight: 500;
}

.auth-link:hover {
  text-decoration: underline;
}

.demo-link {
  display: inline-block;
  color: #6c757d;
  text-decoration: none;
  font-size: 14px;
}

.demo-link:hover {
  text-decoration: underline;
  color: #0066cc;
}

.auth-features {
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: white;
  background: linear-gradient(135deg, #0066cc 0%, #004d99 100%);
}

.auth-features h2 {
  margin: 0 0 30px 0;
  font-size: 24px;
}

.feature-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.feature-list li {
  margin-bottom: 20px;
  font-size: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.feature-icon {
  font-size: 20px;
  width: 40px;
  height: 40px;
  background: rgba(255,255,255,0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

@media (max-width: 992px) {
  .auth-container {
    grid-template-columns: 1fr;
  }
  
  .auth-features {
    display: none;
  }
  
  .auth-card {
    max-width: none;
    padding: 20px;
  }
}

@media (max-width: 768px) {
  .form-options {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
}
</style>