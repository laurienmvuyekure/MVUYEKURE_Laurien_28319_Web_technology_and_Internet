<template>
  <div class="auth-container">
    <div class="auth-card" role="main" aria-labelledby="forgot-title">
      <div class="auth-header">
        <h1 id="forgot-title">Reset Your Password</h1>
        <p class="auth-subtitle">Don't worry, we'll help you get back in</p>
      </div>
      
      <!-- Step 1: Request Reset -->
      <form v-if="currentStep === 'request'" @submit.prevent="handleRequestReset" class="auth-form" novalidate>
        <div v-if="generalError" class="general-error" role="alert">
          {{ generalError }}
        </div>

        <div v-if="successMessage" class="success-message" role="status">
          {{ successMessage }}
        </div>
        
        <div class="form-group">
          <label for="email" class="form-label">Email Address</label>
          <input
            type="email"
            id="email"
            v-model="email"
            @input="clearError"
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
            We'll send a reset link to this email
          </div>
        </div>
        
        <button 
          type="submit" 
          class="auth-button"
          :disabled="loading"
          :aria-busy="loading"
        >
          <span v-if="loading">Sending...</span>
          <span v-else>Send Reset Link</span>
        </button>
        
        <div class="auth-footer">
          <p>
            Remember your password?
            <router-link to="/login" class="auth-link">
              Back to login
            </router-link>
          </p>
        </div>
      </form>

      <!-- Step 2: Enter Token (optional if using actual email) -->
      <form v-if="currentStep === 'token'" @submit.prevent="handleVerifyToken" class="auth-form" novalidate>
        <div v-if="generalError" class="general-error" role="alert">
          {{ generalError }}
        </div>
        
        <div class="info-box">
          <p>We've sent a reset link to your email. Enter the reset code below:</p>
        </div>
        
        <div class="form-group">
          <label for="token" class="form-label">Reset Code</label>
          <input
            type="text"
            id="token"
            v-model="resetToken"
            @input="clearError"
            required
            :aria-describedby="tokenError ? 'token-error' : null"
            :class="{ 'error': tokenError }"
            placeholder="Enter the code from the email"
          />
          <div v-if="tokenError" id="token-error" class="error-message" role="alert">
            {{ tokenError }}
          </div>
        </div>
        
        <button 
          type="submit" 
          class="auth-button"
          :disabled="loading"
          :aria-busy="loading"
        >
          <span v-if="loading">Verifying...</span>
          <span v-else>Verify Code</span>
        </button>
      </form>

      <!-- Step 3: Reset Password -->
      <form v-if="currentStep === 'reset'" @submit.prevent="handleResetPassword" class="auth-form" novalidate>
        <div v-if="generalError" class="general-error" role="alert">
          {{ generalError }}
        </div>
        
        <div class="info-box success">
          <p>✓ Reset code verified. Enter your new password:</p>
        </div>
        
        <div class="form-group">
          <label for="newPassword" class="form-label">New Password</label>
          <div class="password-input">
            <input
              :type="showPassword ? 'text' : 'password'"
              id="newPassword"
              v-model="newPassword"
              @input="clearError"
              required
              autocomplete="new-password"
              :aria-describedby="passwordError ? 'password-error' : 'password-help'"
              :class="{ 'error': passwordError }"
              placeholder="Enter new password"
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
          <label for="confirmPassword" class="form-label">Confirm Password</label>
          <div class="password-input">
            <input
              :type="showPasswordConfirm ? 'text' : 'password'"
              id="confirmPassword"
              v-model="confirmPassword"
              @input="clearError"
              required
              autocomplete="new-password"
              :aria-describedby="confirmError ? 'confirm-error' : null"
              :class="{ 'error': confirmError }"
              placeholder="Confirm new password"
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
          <div v-if="confirmError" id="confirm-error" class="error-message" role="alert">
            {{ confirmError }}
          </div>
        </div>
        
        <button 
          type="submit" 
          class="auth-button"
          :disabled="loading"
          :aria-busy="loading"
        >
          <span v-if="loading">Resetting...</span>
          <span v-else>Reset Password</span>
        </button>
      </form>

      <!-- Step 4: Success -->
      <div v-if="currentStep === 'success'" class="success-box">
        <div class="success-icon">✓</div>
        <h2>Password Reset Successfully!</h2>
        <p>Your password has been reset. You can now log in with your new password.</p>
        <router-link to="/login" class="auth-button button-full">
          Return to Login
        </router-link>
      </div>
    </div>
    
    <div class="auth-features">
      <h2>Secure Password Reset</h2>
      <ul class="feature-list" role="list">
        <li>
          <span class="feature-icon" aria-hidden="true">🔐</span>
          Secure reset process
        </li>
        <li>
          <span class="feature-icon" aria-hidden="true">⏱️</span>
          Time-limited reset links
        </li>
        <li>
          <span class="feature-icon" aria-hidden="true">📧</span>
          Email verification
        </li>
        <li>
          <span class="feature-icon" aria-hidden="true">🛡️</span>
          Protected account
        </li>
        <li>
          <span class="feature-icon" aria-hidden="true">✨</span>
          Quick and easy process
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
  name: 'ForgotPassword',
  setup() {
    const router = useRouter()
    
    const currentStep = ref('request')
    const email = ref('')
    const resetToken = ref('')
    const newPassword = ref('')
    const confirmPassword = ref('')
    const showPassword = ref(false)
    const showPasswordConfirm = ref(false)
    const loading = ref(false)
    
    const emailError = ref('')
    const tokenError = ref('')
    const passwordError = ref('')
    const confirmError = ref('')
    const generalError = ref('')
    const successMessage = ref('')
    
    const clearError = () => {
      emailError.value = ''
      tokenError.value = ''
      passwordError.value = ''
      confirmError.value = ''
      generalError.value = ''
      successMessage.value = ''
    }
    
    const togglePasswordVisibility = () => {
      showPassword.value = !showPassword.value
    }

    const togglePasswordConfirmVisibility = () => {
      showPasswordConfirm.value = !showPasswordConfirm.value
    }
    
    const handleRequestReset = async () => {
      clearError()
      
      if (!email.value) {
        emailError.value = 'Email is required'
        return
      }
      
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)) {
        emailError.value = 'Please enter a valid email address'
        return
      }
      
      loading.value = true
      
      try {
        // Simulate network delay
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        // Request password reset
        const result = authService.requestPasswordReset(email.value)
        
        if (result.success) {
          successMessage.value = 'Reset link sent! Check your email for instructions.'
          
          // For demo - show token directly (in production it would be emailed)
          resetToken.value = result.token
          
          setTimeout(() => {
            currentStep.value = 'token'
            successMessage.value = ''
          }, 1500)
        } else {
          generalError.value = result.message
        }
        
      } catch (error) {
        generalError.value = error.message || 'Failed to request password reset'
      } finally {
        loading.value = false
      }
    }

    const handleVerifyToken = async () => {
      clearError()
      
      if (!resetToken.value) {
        tokenError.value = 'Reset code is required'
        return
      }
      
      loading.value = true
      
      try {
        // Simulate network delay
        await new Promise(resolve => setTimeout(resolve, 800))
        
        // Verify token
        const result = authService.verifyResetToken(resetToken.value)
        
        if (result.success) {
          // Move to password reset step
          currentStep.value = 'reset'
        } else {
          tokenError.value = result.message
        }
        
      } catch (error) {
        tokenError.value = error.message || 'Token verification failed'
      } finally {
        loading.value = false
      }
    }
    
    const handleResetPassword = async () => {
      clearError()
      
      if (!newPassword.value) {
        passwordError.value = 'New password is required'
        return
      }
      
      if (newPassword.value.length < 6) {
        passwordError.value = 'Password must be at least 6 characters'
        return
      }
      
      if (!confirmPassword.value) {
        confirmError.value = 'Please confirm your password'
        return
      }
      
      if (newPassword.value !== confirmPassword.value) {
        confirmError.value = 'Passwords do not match'
        return
      }
      
      loading.value = true
      
      try {
        // Simulate network delay
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        // Reset password
        const result = authService.resetPassword(
          resetToken.value,
          newPassword.value,
          confirmPassword.value
        )
        
        if (result.success) {
          // Move to success step
          currentStep.value = 'success'
          
          // Redirect to login after 3 seconds
          setTimeout(() => {
            router.push('/login')
          }, 3000)
        } else {
          generalError.value = result.message
        }
        
      } catch (error) {
        generalError.value = error.message || 'Password reset failed'
      } finally {
        loading.value = false
      }
    }
    
    return {
      currentStep,
      email,
      resetToken,
      newPassword,
      confirmPassword,
      showPassword,
      showPasswordConfirm,
      loading,
      emailError,
      tokenError,
      passwordError,
      confirmError,
      generalError,
      successMessage,
      clearError,
      togglePasswordVisibility,
      togglePasswordConfirmVisibility,
      handleRequestReset,
      handleVerifyToken,
      handleResetPassword
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

#forgot-title {
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
  margin-bottom: 20px;
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

.info-box {
  background-color: #e2e3e5;
  border: 1px solid #d6d8db;
  color: #383d41;
  padding: 12px;
  border-radius: 6px;
  margin-bottom: 20px;
  font-size: 14px;
}

.info-box.success {
  background-color: #d4edda;
  border-color: #c3e6cb;
  color: #155724;
}

.info-box p {
  margin: 0;
}

.help-text {
  color: #6c757d;
  font-size: 12px;
  margin-top: 4px;
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
  text-decoration: none;
  display: inline-block;
  text-align: center;
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
  margin-top: 20px;
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

.success-box {
  text-align: center;
  padding: 40px 20px;
}

.success-icon {
  font-size: 60px;
  color: #28a745;
  margin-bottom: 20px;
}

.success-box h2 {
  color: #212529;
  margin-bottom: 15px;
}

.success-box p {
  color: #6c757d;
  margin-bottom: 30px;
}

.button-full {
  display: block;
  margin: 0 auto;
  max-width: 300px;
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

  #forgot-title {
    font-size: 24px;
  }

  .auth-features h2 {
    font-size: 24px;
  }
}
</style>
