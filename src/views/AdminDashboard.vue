<template>
  <div class="admin-dashboard">
    <div class="admin-header">
      <h1>🔧 Admin Dashboard</h1>
      <p>Manage users, permissions, and system settings</p>
    </div>

    <div class="admin-container">
      <!-- Navigation Tabs -->
      <div class="admin-tabs">
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

      <!-- Overview Tab -->
      <div v-if="activeTab === 'overview'" class="tab-panel">
        <h2>System Overview</h2>
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-icon">👥</div>
            <div class="stat-info">
              <div class="stat-value">{{ totalUsers }}</div>
              <div class="stat-label">Total Users</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">✅</div>
            <div class="stat-info">
              <div class="stat-value">{{ activeUsers }}</div>
              <div class="stat-label">Active Users</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">📋</div>
            <div class="stat-info">
              <div class="stat-value">{{ totalTasks }}</div>
              <div class="stat-label">Total Tasks</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">🎯</div>
            <div class="stat-info">
              <div class="stat-value">{{ completedTasks }}</div>
              <div class="stat-label">Completed Tasks</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">👨‍💼</div>
            <div class="stat-info">
              <div class="stat-value">{{ adminCount }}</div>
              <div class="stat-label">Administrators</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">🔐</div>
            <div class="stat-info">
              <div class="stat-value">{{ systemStatus }}</div>
              <div class="stat-label">System Status</div>
            </div>
          </div>
        </div>

        <div class="section">
          <h3>Role Distribution</h3>
          <div class="role-distribution">
            <div 
              v-for="(count, role) in roleDistribution"
              :key="role"
              class="role-item"
              :class="role"
            >
              <span class="role-name">{{ formatRole(role) }}</span>
              <span class="role-count">{{ count }}</span>
              <div class="role-bar">
                <div class="role-fill" :style="{ width: (count / totalUsers * 100) + '%' }"></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Users Tab -->
      <div v-if="activeTab === 'users'" class="tab-panel">
        <h2>User Management</h2>
        
        <div class="section-controls">
          <input 
            v-model="searchQuery"
            type="text"
            placeholder="Search users by name or email..."
            class="search-input"
          />
          <button @click="showAddUserForm = !showAddUserForm" class="btn-primary">
            + Add New User
          </button>
        </div>

        <!-- Add User Form -->
        <div v-if="showAddUserForm" class="form-panel">
          <h3>Create New User</h3>
          <div class="form-group">
            <label>Email</label>
            <input v-model="newUserForm.email" type="email" class="form-input" />
          </div>
          <div class="form-group">
            <label>Name</label>
            <input v-model="newUserForm.name" type="text" class="form-input" />
          </div>
          <div class="form-group">
            <label>Role</label>
            <select v-model="newUserForm.role" class="form-input">
              <option value="user">User</option>
              <option value="team_lead">Team Lead</option>
              <option value="admin">Administrator</option>
            </select>
          </div>
          <div class="form-actions">
            <button @click="createAdminUser" class="btn-primary">Create User</button>
            <button @click="showAddUserForm = false" class="btn-secondary">Cancel</button>
          </div>
        </div>

        <!-- Users Table -->
        <div class="table-container">
          <table class="users-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Role</th>
                <th>Status</th>
                <th>Created</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in filteredUsers" :key="user.id" class="user-row">
                <td>{{ user.name }}</td>
                <td>{{ user.email }}</td>
                <td>
                  <select 
                    :value="user.role"
                    @change="changeUserRole(user.id, $event.target.value)"
                    class="role-select"
                  >
                    <option value="guest">Guest</option>
                    <option value="user">User</option>
                    <option value="team_lead">Team Lead</option>
                    <option value="admin">Admin</option>
                  </select>
                </td>
                <td>
                  <span class="status-badge" :class="user.active ? 'active' : 'inactive'">
                    {{ user.active ? 'Active' : 'Inactive' }}
                  </span>
                </td>
                <td>{{ formatDate(user.createdAt) }}</td>
                <td>
                  <div class="action-buttons">
                    <button 
                      @click="toggleUserStatus(user.id, !user.active)"
                      class="btn-small"
                      :class="user.active ? 'btn-danger' : 'btn-success'"
                    >
                      {{ user.active ? 'Disable' : 'Enable' }}
                    </button>
                    <button 
                      @click="deleteUser(user.id)"
                      class="btn-small btn-danger"
                    >
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Roles & Permissions Tab -->
      <div v-if="activeTab === 'permissions'" class="tab-panel">
        <h2>Roles & Permissions Management</h2>
        
        <div class="roles-grid">
          <div 
            v-for="(permissions, role) in rolePermissions"
            :key="role"
            class="role-card"
          >
            <h3>{{ formatRole(role) }}</h3>
            <div class="permissions-list">
              <div 
                v-for="(value, permission) in permissions"
                :key="permission"
                class="permission-item"
              >
                <span class="permission-checkbox">
                  {{ value ? '✓' : '✗' }}
                </span>
                <span class="permission-text">{{ formatPermissionName(permission) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- System Settings Tab -->
      <div v-if="activeTab === 'settings'" class="tab-panel">
        <h2>System Settings</h2>
        
        <div class="section">
          <h3>Application Settings</h3>
          <div class="form-group">
            <label class="checkbox-label">
              <input 
                v-model="systemSettings.maintenanceMode"
                type="checkbox"
              />
              <span>Maintenance Mode (disables user access)</span>
            </label>
          </div>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input 
                v-model="systemSettings.allowRegistration"
                type="checkbox"
              />
              <span>Allow New User Registration</span>
            </label>
          </div>

          <div class="form-group">
            <label class="checkbox-label">
              <input 
                v-model="systemSettings.requireEmailVerification"
                type="checkbox"
              />
              <span>Require Email Verification</span>
            </label>
          </div>

          <button @click="saveSystemSettings" class="btn-primary">
            Save Settings
          </button>
        </div>

        <div class="section">
          <h3>Backup & Export</h3>
          <p>Backup and restore system data</p>
          <div class="button-group">
            <button @click="backupData" class="btn-secondary">
              📥 Backup System Data
            </button>
            <button @click="clearAllData" class="btn-danger">
              🗑️ Clear All Data
            </button>
          </div>
        </div>
      </div>

      <!-- Audit Log Tab -->
      <div v-if="activeTab === 'audit'" class="tab-panel">
        <h2>System Audit Log</h2>
        
        <div class="audit-log">
          <div class="log-entry">
            <span class="log-time">{{ getCurrentTime() }}</span>
            <span class="log-action">Admin Dashboard accessed</span>
          </div>
          <div class="log-entry">
            <span class="log-time">11:30 AM</span>
            <span class="log-action">User 'John Doe' created new task</span>
          </div>
          <div class="log-entry">
            <span class="log-time">11:15 AM</span>
            <span class="log-action">User 'Jane Smith' logged in</span>
          </div>
          <div class="log-entry">
            <span class="log-time">10:45 AM</span>
            <span class="log-action">Admin changed user role</span>
          </div>
          <div class="log-entry">
            <span class="log-time">10:30 AM</span>
            <span class="log-action">System backup completed</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import authService, { PERMISSIONS, USER_ROLES } from '../services/authService'

export default {
  name: 'AdminDashboard',
  setup() {
    const activeTab = ref('overview')
    const tabs = ['overview', 'users', 'permissions', 'settings', 'audit']
    const searchQuery = ref('')
    const showAddUserForm = ref(false)
    const allUsers = ref([])
    
    const newUserForm = ref({
      email: '',
      name: '',
      role: USER_ROLES.USER
    })

    const systemSettings = ref({
      maintenanceMode: false,
      allowRegistration: true,
      requireEmailVerification: false
    })

    const formatTabName = (tab) => {
      const names = {
        overview: '📊 Overview',
        users: '👥 Users',
        permissions: '🔐 Permissions',
        settings: '⚙️ Settings',
        audit: '📋 Audit Log'
      }
      return names[tab] || tab
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
        month: 'short',
        day: 'numeric'
      })
    }

    const getCurrentTime = () => {
      return new Date().toLocaleTimeString('en-US', {
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    const totalUsers = computed(() => allUsers.value.length)
    const activeUsers = computed(() => allUsers.value.filter(u => u.active).length)
    const adminCount = computed(() => allUsers.value.filter(u => u.role === USER_ROLES.ADMIN).length)
    const systemStatus = computed(() => systemSettings.value.maintenanceMode ? 'Maintenance' : 'Online')

    const totalTasks = computed(() => {
      const tasks = localStorage.getItem('tasks')
      return tasks ? JSON.parse(tasks).length : 0
    })

    const completedTasks = computed(() => {
      const tasks = localStorage.getItem('tasks')
      if (!tasks) return 0
      return JSON.parse(tasks).filter(t => t.completed).length
    })

    const roleDistribution = computed(() => {
      const dist = {
        admin: 0,
        team_lead: 0,
        user: 0,
        guest: 0
      }
      allUsers.value.forEach(user => {
        if (user.role in dist) {
          dist[user.role]++
        }
      })
      return dist
    })

    const rolePermissions = computed(() => PERMISSIONS)

    const filteredUsers = computed(() => {
      const query = searchQuery.value.toLowerCase()
      return allUsers.value.filter(user =>
        user.name.toLowerCase().includes(query) ||
        user.email.toLowerCase().includes(query)
      )
    })

    const changeUserRole = (userId, newRole) => {
      const result = authService.changeUserRole(userId, newRole)
      if (result.success) {
        const userIndex = allUsers.value.findIndex(u => u.id === userId)
        if (userIndex !== -1) {
          allUsers.value[userIndex].role = newRole
        }
      }
    }

    const toggleUserStatus = (userId, active) => {
      const result = authService.toggleUserAccount(userId, active)
      if (result.success) {
        const userIndex = allUsers.value.findIndex(u => u.id === userId)
        if (userIndex !== -1) {
          allUsers.value[userIndex].active = active
        }
      }
    }

    const deleteUser = (userId) => {
      if (confirm('Are you sure you want to delete this user?')) {
        const result = authService.deleteUser(userId)
        if (result.success) {
          allUsers.value = allUsers.value.filter(u => u.id !== userId)
        }
      }
    }

    const createAdminUser = () => {
      if (!newUserForm.value.email || !newUserForm.value.name) {
        alert('Please fill in all fields')
        return
      }

      const result = authService.register({
        email: newUserForm.value.email,
        password: 'TempPass123', // Temporary password
        name: newUserForm.value.name,
        passwordConfirm: 'TempPass123'
      })

      if (result.success) {
        // Change role to selected one
        authService.changeUserRole(result.user.id, newUserForm.value.role)
        
        // Reload users
        const users = authService.getAllUsers()
        allUsers.value = users
        
        // Reset form
        newUserForm.value = { email: '', name: '', role: USER_ROLES.USER }
        showAddUserForm.value = false
        alert('User created successfully')
      } else {
        alert('Error: ' + result.message)
      }
    }

    const saveSystemSettings = () => {
      localStorage.setItem('system-settings', JSON.stringify(systemSettings.value))
      alert('System settings saved')
    }

    const backupData = () => {
      const backup = {
        users: authService.getAllUsers(),
        tasks: JSON.parse(localStorage.getItem('tasks') || '[]'),
        settings: systemSettings.value,
        backupDate: new Date().toISOString()
      }

      const dataStr = JSON.stringify(backup, null, 2)
      const dataBlob = new Blob([dataStr], { type: 'application/json' })
      const url = URL.createObjectURL(dataBlob)
      const link = document.createElement('a')
      link.href = url
      link.download = `taskbuddy-backup-${Date.now()}.json`
      link.click()
      URL.revokeObjectURL(url)
    }

    const clearAllData = () => {
      if (confirm('This will permanently delete all data! Are you sure?')) {
        localStorage.clear()
        location.reload()
      }
    }

    onMounted(() => {
      const currentUser = authService.getCurrentUser()
      if (!currentUser || currentUser.role !== USER_ROLES.ADMIN) {
        alert('Admin access required')
        window.history.back()
        return
      }

      allUsers.value = authService.getAllUsers()

      const saved = localStorage.getItem('system-settings')
      if (saved) {
        systemSettings.value = JSON.parse(saved)
      }
    })

    return {
      activeTab,
      tabs,
      searchQuery,
      showAddUserForm,
      newUserForm,
      systemSettings,
      allUsers,
      totalUsers,
      activeUsers,
      adminCount,
      systemStatus,
      totalTasks,
      completedTasks,
      roleDistribution,
      rolePermissions,
      filteredUsers,
      formatTabName,
      formatRole,
      formatPermissionName,
      formatDate,
      getCurrentTime,
      changeUserRole,
      toggleUserStatus,
      deleteUser,
      createAdminUser,
      saveSystemSettings,
      backupData,
      clearAllData
    }
  }
}
</script>

<style scoped>
.admin-dashboard {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 30px 20px;
}

.admin-header {
  max-width: 1400px;
  margin: 0 auto 30px;
  padding: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
}

.admin-header h1 {
  margin: 0 0 8px 0;
  font-size: 32px;
}

.admin-header p {
  margin: 0;
  opacity: 0.9;
}

.admin-container {
  max-width: 1400px;
  margin: 0 auto;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.admin-tabs {
  display: flex;
  background: #f5f5f5;
  border-bottom: 2px solid #e8eaed;
  overflow-x: auto;
}

.tab-button {
  padding: 16px 24px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  color: #5f6368;
  white-space: nowrap;
  transition: all 0.3s;
  border-bottom: 3px solid transparent;
}

.tab-button:hover {
  color: #202124;
  background: rgba(0, 0, 0, 0.05);
}

.tab-button.active {
  color: #0066cc;
  border-bottom-color: #0066cc;
}

.tab-panel {
  padding: 40px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.stat-card {
  background: #f5f7fa;
  border: 2px solid #e8eaed;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  font-size: 32px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #0066cc;
}

.stat-label {
  font-size: 12px;
  color: #5f6368;
  margin-top: 4px;
}

.section {
  margin-bottom: 40px;
}

.section h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #202124;
}

.role-distribution {
  gap: 12px;
}

.role-item {
  margin-bottom: 16px;
  padding: 12px;
  background: #f5f5f5;
  border-radius: 6px;
}

.role-name {
  display: block;
  font-weight: 500;
  margin-bottom: 6px;
}

.role-count {
  display: inline-block;
  background: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 700;
  margin-left: 8px;
}

.role-bar {
  width: 100%;
  height: 8px;
  background: #e8eaed;
  border-radius: 4px;
  margin-top: 8px;
  overflow: hidden;
}

.role-fill {
  height: 100%;
  background: #0066cc;
}

.section-controls {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.search-input {
  flex: 1;
  padding: 12px;
  border: 2px solid #e8eaed;
  border-radius: 6px;
  font-size: 14px;
}

.search-input:focus {
  outline: none;
  border-color: #0066cc;
}

.form-panel {
  background: #f5f7fa;
  border: 2px solid #e8eaed;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: #495057;
  font-size: 14px;
}

.form-input {
  width: 100%;
  padding: 10px;
  border: 2px solid #dee2e6;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #0066cc;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.table-container {
  overflow-x: auto;
  margin-top: 20px;
}

.users-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.users-table thead {
  background: #f5f5f5;
}

.users-table th {
  padding: 12px;
  text-align: left;
  font-weight: 600;
  color: #202124;
  border-bottom: 2px solid #e8eaed;
}

.users-table td {
  padding: 12px;
  border-bottom: 1px solid #e8eaed;
}

.user-row:hover {
  background: #f9f9f9;
}

.role-select {
  padding: 6px;
  border: 1px solid #e8eaed;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
}

.status-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge.active {
  background: #d4edda;
  color: #155724;
}

.status-badge.inactive {
  background: #f8d7da;
  color: #721c24;
}

.action-buttons {
  display: flex;
  gap: 6px;
}

.btn-small {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  background: #e8eaed;
  color: #202124;
  transition: all 0.2s;
}

.btn-small.btn-danger {
  background: #dc3545;
  color: white;
}

.btn-small.btn-success {
  background: #28a745;
  color: white;
}

.roles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.role-card {
  background: #f5f7fa;
  border: 2px solid #e8eaed;
  border-radius: 8px;
  padding: 20px;
}

.role-card h3 {
  margin-top: 0;
  margin-bottom: 16px;
  color: #202124;
}

.permissions-list {
  gap: 8px;
}

.permission-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  background: white;
  border-radius: 4px;
  font-size: 13px;
}

.permission-checkbox {
  display: inline-block;
  width: 20px;
  text-align: center;
  font-weight: 700;
  color: #28a745;
}

.permission-item:has(.permission-checkbox:contains('✗')) .permission-checkbox {
  color: #dc3545;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
  margin-bottom: 12px;
}

.checkbox-label input {
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.button-group {
  display: flex;
  gap: 12px;
}

.audit-log {
  background: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;
  max-height: 400px;
  overflow-y: auto;
}

.log-entry {
  display: flex;
  padding: 12px 16px;
  border-bottom: 1px solid #e8eaed;
  font-size: 13px;
}

.log-entry:last-child {
  border-bottom: none;
}

.log-time {
  color: #5f6368;
  font-weight: 600;
  min-width: 80px;
}

.log-action {
  flex: 1;
  color: #202124;
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

@media (max-width: 768px) {
  .admin-header {
    padding: 20px;
  }

  .admin-header h1 {
    font-size: 24px;
  }

  .tab-panel {
    padding: 20px;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .roles-grid {
    grid-template-columns: 1fr;
  }

  .section-controls {
    flex-direction: column;
  }

  .users-table {
    font-size: 12px;
  }

  .users-table th,
  .users-table td {
    padding: 8px;
  }
}
</style>
