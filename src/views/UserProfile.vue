<template>
  <div class="profile-page">
    <div class="profile-container">
      <!-- Profile Header -->
      <div class="profile-header">
        <div class="profile-avatar">
          <img 
            v-if="currentUser?.avatar" 
            :src="currentUser.avatar" 
            :alt="currentUser.name"
            class="avatar-image"
          />
          <div v-else class="avatar-placeholder">
            {{ getInitials(currentUser?.name) }}
          </div>
          <label class="upload-badge" title="Upload new profile picture">
            <input 
              type="file" 
              accept="image/*"
              @change="handleAvatarUpload"
              hidden
            />
            📷
          </label>
        </div>
        
        <div class="profile-info">
          <h1>{{ currentUser?.name }}</h1>
          <p class="email">{{ currentUser?.email }}</p>
          <div class="role-badge" :class="currentUser?.role">
            {{ formatRole(currentUser?.role) }}
          </div>
        </div>
      </div>

      <div class="profile-content">
        <!-- Tabs -->
        <div class="tab-navigation">
          <button 
            v-for="tab in tabs"
            :key="tab"
            @click="activeTab = tab"
            class="tab-button"
            :class="{ active: activeTab === tab }"
          >
            {{ formatTabName(tab) }}
          </button>
        </div>

        <!-- Profile Tab -->
        <div v-if="activeTab === 'profile'" class="tab-content">
          <div class="section">
            <h2>Personal Information</h2>
            <div class="form-group">
              <label>Full Name</label>
              <input 
                v-model="editForm.name"
                type="text"
                class="form-input"
                @blur="validateField('name')"
              />
            </div>

            <div class="form-group">
              <label>Email Address</label>
              <input 
                v-model.prop="currentUser.email"
                type="email"
                class="form-input"
                disabled
              />
            </div>

            <div v-if="saveMessage" class="success-message">
              {{ saveMessage }}
            </div>

            <button @click="saveProfile" class="btn-primary">
              Save Changes
            </button>
          </div>
        </div>

        <!-- Preferences Tab -->
        <div v-if="activeTab === 'preferences'" class="tab-content">
          <div class="section">
            <h2>Display Preferences</h2>
            <div class="form-group">
              <label class="checkbox-label">
                <input 
                  type="checkbox"
                  v-model="editForm.preferences.theme"
                />
                <span>Dark Mode</span>
              </label>
              <p class="help-text">Enable dark theme for easier reading at night</p>
            </div>

            <h2>Notifications</h2>
            <div class="form-group">
              <label class="checkbox-label">
                <input 
                  type="checkbox"
                  v-model="editForm.preferences.notifications"
                />
                <span>In-App Notifications</span>
              </label>
              <p class="help-text">Receive notifications for task updates</p>
            </div>

            <div class="form-group">
              <label class="checkbox-label">
                <input 
                  type="checkbox"
                  v-model="editForm.preferences.emailUpdates"
                />
                <span>Email Updates</span>
              </label>
              <p class="help-text">Receive email summaries of your tasks</p>
            </div>

            <button @click="savePreferences" class="btn-primary">
              Save Preferences
            </button>
          </div>
        </div>

        <!-- Permissions Tab -->
        <div v-if="activeTab === 'permissions'" class="tab-content">
          <div class="section">
            <h2>Your Permissions</h2>
            <p class="role-description">
              Your account role is <strong>{{ formatRole(currentUser?.role) }}</strong>
            </p>

            <div class="permissions-grid">
              <div 
                v-for="(value, permission) in userPermissions"
                :key="permission"
                class="permission-item"
                :class="{ enabled: value, disabled: !value }"
              >
                <span class="permission-icon">{{ value ? '✓' : '✗' }}</span>
                <span class="permission-name">{{ formatPermissionName(permission) }}</span>
              </div>
            </div>

            <div class="role-info">
              <h3>Role Information</h3>
              <table class="roles-table">
                <thead>
                  <tr>
                    <th>Role</th>
                    <th>Description</th>
                  </tr>
                </thead>
                <tbody>
                  <tr class="admin-role">
                    <td><strong>Admin</strong></td>
                    <td>Full system access and user management</td>
                  </tr>
                  <tr class="team-lead-role">
                    <td><strong>Team Lead</strong></td>
                    <td>Team management and task visibility</td>
                  </tr>
                  <tr class="user-role">
                    <td><strong>User</strong></td>
                    <td>Basic task creation and management</td>
                  </tr>
                  <tr class="guest-role">
                    <td><strong>Guest</strong></td>
                    <td>Read-only access to shared tasks</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <!-- Security Tab -->
        <div v-if="activeTab === 'security'" class="tab-content">
          <div class="section">
            <h2>Change Password</h2>
            
            <div v-if="securityError" class="error-message">
              {{ securityError }}
            </div>

            <div v-if="securitySuccess" class="success-message">
              {{ securitySuccess }}
            </div>

            <div class="form-group">
              <label>Current Password</label>
              <input 
                v-model="passwordForm.current"
                type="password"
                class="form-input"
                placeholder="Enter your current password"
              />
            </div>

            <div class="form-group">
              <label>New Password</label>
              <input 
                v-model="passwordForm.new"
                type="password"
                class="form-input"
                placeholder="Enter new password (min 6 characters)"
              />
            </div>

            <div class="form-group">
              <label>Confirm Password</label>
              <input 
                v-model="passwordForm.confirm"
                type="password"
                class="form-input"
                placeholder="Confirm new password"
              />
            </div>

            <button @click="changePassword" class="btn-primary">
              Change Password
            </button>

            <h2>Active Sessions</h2>
            <div class="session-info">
              <p>📱 Current Device</p>
              <p class="session-time">Last active: {{ getCurrentTime() }}</p>
              <button @click="handleLogout" class="btn-danger">
                Logout
              </button>
            </div>
          </div>
        </div>

        <!-- Account Tab -->
        <div v-if="activeTab === 'account'" class="tab-content">
          <div class="section">
            <h2>Account Information</h2>
            <div class="account-info">
              <div class="info-item">
                <span class="label">Account Created</span>
                <span class="value">{{ formatDate(currentUser?.createdAt) }}</span>
              </div>
              <div class="info-item">
                <span class="label">User ID</span>
                <span class="value">{{ currentUser?.id }}</span>
              </div>
              <div class="info-item">
                <span class="label">Account Status</span>
                <span class="value">
                  <span class="status-badge active">✓ Active</span>
                </span>
              </div>
            </div>

            <h2>Danger Zone</h2>
            <div class="danger-section">
              <p>Be careful with these options.</p>
              
              <button @click="exportData" class="btn-secondary">
                📥 Export My Data
              </button>

              <button @click="confirmDeleteAccount" class="btn-danger">
                🗑️ Delete Account
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div v-if="showDeleteConfirm" class="modal-overlay" @click="showDeleteConfirm = false">
      <div class="modal" @click.stop>
        <h2>Delete Account?</h2>
        <p>This action cannot be undone. All your tasks and data will be permanently deleted.</p>
        <div class="modal-actions">
          <button @click="showDeleteConfirm = false" class="btn-secondary">
            Cancel
          </button>
          <button @click="deleteAccount" class="btn-danger">
            Confirm Delete
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import authService, { PERMISSIONS } from '../services/authService'

export default {
  name: 'UserProfile',
  setup() {
    const router = useRouter()
    const currentUser = ref(null)
    const activeTab = ref('profile')
    const tabs = ['profile', 'preferences', 'permissions', 'security', 'account']
    
    const editForm = ref({
      name: '',
      preferences: {
        theme: false,
        notifications: true,
        emailUpdates: true
      }
    })

    const passwordForm = ref({
      current: '',
      new: '',
      confirm: ''
    })

    const saveMessage = ref('')
    const securityError = ref('')
    const securitySuccess = ref('')
    const showDeleteConfirm = ref(false)

    const userPermissions = computed(() => {
      if (!currentUser.value) return {}
      return PERMISSIONS[currentUser.value.role] || PERMISSIONS.USER
    })

    const getInitials = (name) => {
      if (!name) return '?'
      return name.split(' ').map(n => n[0]).join('').toUpperCase()
    }

    const formatRole = (role) => {
      const roleMap = {
        admin: 'Administrator',
        team_lead: 'Team Lead',
        user: 'User',
        guest: 'Guest'
      }
      return roleMap[role] || role
    }

    const formatTabName = (tab) => {
      const names = {
        profile: 'Profile',
        preferences: 'Preferences',
        permissions: 'Permissions',
        security: 'Security',
        account: 'Account'
      }
      return names[tab] || tab
    }

    const formatPermissionName = (permission) => {
      return permission
        .split('_')
        .map(word => word.charAt(0).toUpperCase() + word.slice(1))
        .join(' ')
    }

    const formatDate = (dateString) => {
      if (!dateString) return 'N/A'
      return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    }

    const getCurrentTime = () => {
      return new Date().toLocaleString('en-US', {
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    const validateField = (field) => {
      // Add field validation here if needed
    }

    const saveProfile = () => {
      saveMessage.value = ''
      if (!editForm.value.name || editForm.value.name.length < 2) {
        saveMessage.value = 'Name must be at least 2 characters'
        return
      }

      const result = authService.updateProfile(currentUser.value.id, {
        name: editForm.value.name
      })

      if (result.success) {
        currentUser.value = result.user
        saveMessage.value = 'Profile updated successfully!'
        setTimeout(() => {
          saveMessage.value = ''
        }, 3000)
      }
    }

    const savePreferences = () => {
      const result = authService.updateProfile(currentUser.value.id, {
        preferences: editForm.value.preferences
      })

      if (result.success) {
        currentUser.value = result.user
        saveMessage.value = 'Preferences saved!'
        setTimeout(() => {
          saveMessage.value = ''
        }, 3000)
      }
    }

    const changePassword = () => {
      securityError.value = ''
      securitySuccess.value = ''

      if (!passwordForm.value.current) {
        securityError.value = 'Current password is required'
        return
      }

      if (!passwordForm.value.new) {
        securityError.value = 'New password is required'
        return
      }

      if (passwordForm.value.new.length < 6) {
        securityError.value = 'Password must be at least 6 characters'
        return
      }

      if (passwordForm.value.new !== passwordForm.value.confirm) {
        securityError.value = 'Passwords do not match'
        return
      }

      // In a real app, verify current password with backend
      securitySuccess.value = 'Password changed successfully!'
      passwordForm.value = {
        current: '',
        new: '',
        confirm: ''
      }
      setTimeout(() => {
        securitySuccess.value = ''
      }, 3000)
    }

    const handleAvatarUpload = (event) => {
      const file = event.target.files?.[0]
      if (!file) return

      const reader = new FileReader()
      reader.onload = (e) => {
        const base64 = e.target?.result
        if (base64 && typeof base64 === 'string') {
          currentUser.value.avatar = base64
          localStorage.setItem('userAvatar', base64)
          authService.updateProfile(currentUser.value.id, {
            profilePicture: base64
          })
        }
      }
      reader.readAsDataURL(file)
    }

    const exportData = () => {
      const userData = {
        user: currentUser.value,
        tasks: JSON.parse(localStorage.getItem('tasks') || '[]'),
        exportDate: new Date().toISOString()
      }

      const dataStr = JSON.stringify(userData, null, 2)
      const dataBlob = new Blob([dataStr], { type: 'application/json' })
      const url = URL.createObjectURL(dataBlob)
      const link = document.createElement('a')
      link.href = url
      link.download = `taskbuddy-export-${Date.now()}.json`
      link.click()
      URL.revokeObjectURL(url)
    }

    const confirmDeleteAccount = () => {
      showDeleteConfirm.value = true
    }

    const deleteAccount = () => {
      // In a real app, this would delete from backend
      localStorage.clear()
      authService.logout()
      router.push('/login')
    }

    const handleLogout = async () => {
      authService.logout()
      await router.push('/login')
    }

    onMounted(() => {
      currentUser.value = authService.getCurrentUser()
      if (currentUser.value) {
        editForm.value.name = currentUser.value.name
        editForm.value.preferences = {
          ...currentUser.value.preferences
        }
        // Convert boolean to checkbox (for theme display purposes)
        if (currentUser.value.preferences?.theme === 'dark') {
          editForm.value.preferences.theme = true
        }
      }
    })

    return {
      currentUser,
      activeTab,
      tabs,
      editForm,
      passwordForm,
      saveMessage,
      securityError,
      securitySuccess,
      showDeleteConfirm,
      userPermissions,
      getInitials,
      formatRole,
      formatTabName,
      formatPermissionName,
      formatDate,
      getCurrentTime,
      validateField,
      saveProfile,
      savePreferences,
      changePassword,
      handleAvatarUpload,
      exportData,
      confirmDeleteAccount,
      deleteAccount,
      handleLogout
    }
  }
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 30px 20px;
}

.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.profile-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 40px;
  display: flex;
  align-items: center;
  gap: 30px;
}

.profile-avatar {
  position: relative;
}

.avatar-image,
.avatar-placeholder {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  font-weight: bold;
  border: 3px solid white;
}

.avatar-image {
  object-fit: cover;
}

.upload-badge {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 36px;
  height: 36px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 18px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  transition: transform 0.2s;
}

.upload-badge:hover {
  transform: scale(1.1);
}

.profile-info h1 {
  margin: 0 0 8px 0;
  font-size: 32px;
}

.profile-info .email {
  margin: 0 0 12px 0;
  opacity: 0.9;
}

.role-badge {
  display: inline-block;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.role-badge.admin {
  background: #dc3545;
}

.role-badge.team_lead {
  background: #fd7e14;
}

.role-badge.user {
  background: #0066cc;
}

.profile-content {
  padding: 40px;
}

.tab-navigation {
  display: flex;
  gap: 12px;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #e8eaed;
}

.tab-button {
  padding: 10px 20px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  color: #5f6368;
  font-weight: 500;
  transition: all 0.3s;
  position: relative;
}

.tab-button:hover {
  color: #202124;
}

.tab-button.active {
  color: #0066cc;
}

.tab-button.active::after {
  content: '';
  position: absolute;
  bottom: -20px;
  left: 0;
  right: 0;
  height: 3px;
  background: #0066cc;
}

.tab-content {
  animation: fadeIn 0.3s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.section {
  margin-bottom: 40px;
}

.section h2 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #202124;
  font-size: 20px;
}

.section h3 {
  margin-top: 30px;
  margin-bottom: 15px;
  color: #4a4a4a;
  font-size: 16px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #495057;
  font-size: 14px;
}

.form-input {
  width: 100%;
  padding: 12px;
  border: 2px solid #dee2e6;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #0066cc;
  box-shadow: 0 0 0 3px rgba(0, 102, 204, 0.1);
}

.form-input:disabled {
  background: #f5f5f5;
  cursor: not-allowed;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  font-weight: 400;
}

.checkbox-label input {
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.help-text {
  font-size: 12px;
  color: #6c757d;
  margin: 6px 0 0 0;
}

.permissions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 15px;
  margin: 20px 0;
}

.permission-item {
  padding: 15px;
  border: 2px solid #e8eaed;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.permission-item.enabled {
  background: #e6f4ea;
  border-color: #80c995;
}

.permission-item.disabled {
  background: #f9f9f9;
  opacity: 0.6;
}

.permission-icon {
  font-size: 20px;
  font-weight: bold;
  min-width: 24px;
}

.permission-item.enabled .permission-icon {
  color: #34a853;
}

.permission-item.disabled .permission-icon {
  color: #d32f2f;
}

.permission-name {
  font-size: 14px;
  color: #202124;
}

.role-info {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e8eaed;
}

.roles-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 15px;
}

.roles-table th,
.roles-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #e8eaed;
}

.roles-table th {
  background: #f5f5f5;
  font-weight: 600;
  color: #202124;
}

.roles-table tr:last-child td {
  border-bottom: none;
}

.admin-role td:first-child {
  color: #dc3545;
  font-weight: 600;
}

.team-lead-role td:first-child {
  color: #fd7e14;
  font-weight: 600;
}

.user-role td:first-child {
  color: #0066cc;
  font-weight: 600;
}

.guest-role td:first-child {
  color: #6c757d;
  font-weight: 600;
}

.session-info {
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-top: 15px;
}

.session-info p {
  margin: 8px 0;
  color: #5f6368;
}

.session-time {
  font-size: 13px;
}

.account-info {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 20px;
  margin: 20px 0;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #e8eaed;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  font-weight: 500;
  color: #495057;
}

.info-item .value {
  color: #202124;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge.active {
  background: #d4edda;
  color: #155724;
}

.danger-section {
  background: #fff5f5;
  border: 2px solid #f8d7da;
  border-radius: 8px;
  padding: 20px;
  margin-top: 20px;
}

.danger-section p {
  margin-top: 0;
  color: #721c24;
}

.btn-primary,
.btn-secondary,
.btn-danger {
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  margin-right: 12px;
  margin-bottom: 12px;
}

.btn-primary {
  background: #0066cc;
  color: white;
}

.btn-primary:hover {
  background: #0052a3;
}

.btn-secondary {
  background: #e8eaed;
  color: #202124;
}

.btn-secondary:hover {
  background: #d2d3d7;
}

.btn-danger {
  background: #dc3545;
  color: white;
}

.btn-danger:hover {
  background: #c82333;
}

.success-message {
  background: #d4edda;
  border: 1px solid #c3e6cb;
  color: #155724;
  padding: 12px;
  border-radius: 6px;
  margin-bottom: 20px;
  font-size: 14px;
}

.error-message {
  background: #f8d7da;
  border: 1px solid #f5c6cb;
  color: #721c24;
  padding: 12px;
  border-radius: 6px;
  margin-bottom: 20px;
  font-size: 14px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: white;
  padding: 30px;
  border-radius: 12px;
  max-width: 400px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
}

.modal h2 {
  margin-top: 0;
  color: #202124;
}

.modal p {
  color: #5f6368;
  line-height: 1.6;
}

.modal-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .profile-header {
    flex-direction: column;
    text-align: center;
    padding: 30px 20px;
  }

  .profile-content {
    padding: 20px;
  }

  .tab-navigation {
    flex-wrap: wrap;
  }

  .permissions-grid {
    grid-template-columns: 1fr;
  }

  .roles-table {
    font-size: 14px;
  }

  .roles-table th,
  .roles-table td {
    padding: 8px;
  }
}
</style>
