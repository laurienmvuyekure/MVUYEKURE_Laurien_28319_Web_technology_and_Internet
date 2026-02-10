<template>
  <div class="task-sharing-dialog" v-if="isOpen" @click="closeOnBackdrop">
    <div class="dialog-content" @click.stop>
      <div class="dialog-header">
        <h2>Share Task</h2>
        <button @click="close" class="close-button">✕</button>
      </div>

      <div class="dialog-body">
        <!-- Task Info -->
        <div class="task-info">
          <h3>{{ task.title }}</h3>
          <p>{{ task.description || 'No description' }}</p>
        </div>

        <!-- Share Form -->
        <div class="share-section">
          <h4>Share With</h4>
          
          <div class="share-type-selector">
            <button 
              @click="shareType = 'user'"
              class="type-btn"
              :class="{ active: shareType === 'user' }"
            >
              👤 User
            </button>
            <button 
              @click="shareType = 'team'"
              class="type-btn"
              :class="{ active: shareType === 'team' }"
            >
              👥 Team
            </button>
          </div>

          <div class="form-group">
            <label v-if="shareType === 'user'">Select User</label>
            <label v-else>Select Team</label>
            
            <select v-model="selectedRecipient" class="form-select">
              <option value="">-- Choose --</option>
              <option 
                v-for="option in recipientOptions"
                :key="option.id"
                :value="option.id"
              >
                {{ option.name }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>Permission Level</label>
            <div class="permission-options">
              <label class="radio-label">
                <input 
                  v-model="sharePermission"
                  type="radio"
                  value="view"
                />
                <span class="radio-text">
                  <strong>View Only</strong>
                  <small>Can view the task</small>
                </span>
              </label>
              <label class="radio-label">
                <input 
                  v-model="sharePermission"
                  type="radio"
                  value="edit"
                />
                <span class="radio-text">
                  <strong>Edit</strong>
                  <small>Can view and edit the task</small>
                </span>
              </label>
              <label class="radio-label">
                <input 
                  v-model="sharePermission"
                  type="radio"
                  value="admin"
                />
                <span class="radio-text">
                  <strong>Full Access</strong>
                  <small>Can manage task and sharing</small>
                </span>
              </label>
            </div>
          </div>

          <button 
            @click="shareTask"
            :disabled="!selectedRecipient"
            class="btn-share"
          >
            Share Task
          </button>
        </div>

        <!-- Current Shares -->
        <div class="shares-section">
          <h4>Current Shares ({{ currentShares.length }})</h4>
          
          <div v-if="currentShares.length === 0" class="empty-shares">
            <p>This task hasn't been shared yet</p>
          </div>

          <div v-else class="shares-list">
            <div 
              v-for="share in currentShares"
              :key="share.id"
              class="share-item"
            >
              <div class="share-info">
                <div class="share-recipient">
                  <span class="recipient-icon">{{ share.type === 'user' ? '👤' : '👥' }}</span>
                  <span class="recipient-name">{{ share.recipientName }}</span>
                </div>
                <span class="permission-badge" :class="share.permission">
                  {{ formatPermission(share.permission) }}
                </span>
              </div>
              <div class="share-actions">
                <select 
                  v-if="canManageShare(share)"
                  :value="share.permission"
                  @change="updatePermission(share.id, $event.target.value)"
                  class="permission-select"
                >
                  <option value="view">View</option>
                  <option value="edit">Edit</option>
                  <option value="admin">Admin</option>
                </select>
                <button 
                  v-if="canManageShare(share)"
                  @click="revokeAccess(share.id)"
                  class="btn-revoke"
                >
                  Revoke
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Collaborators -->
        <div class="collaborators-section">
          <h4>Collaborators</h4>
          <div class="collaborators-list">
            <div class="collaborator-item">
              <span class="collaborator-name">You</span>
              <span class="collaborator-role">Owner</span>
            </div>
            <div 
              v-for="collab in collaborators"
              :key="collab.id"
              class="collaborator-item"
            >
              <span class="collaborator-name">{{ collab.name }}</span>
              <span class="collaborator-role">{{ collab.role }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="dialog-footer">
        <button @click="close" class="btn-secondary">Close</button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, watch } from 'vue'
import authService from '../services/authService'
import teamService from '../services/teamService'
import taskSharingService from '../services/taskSharingService'

export default {
  name: 'TaskSharingDialog',
  props: {
    isOpen: {
      type: Boolean,
      required: true
    },
    task: {
      type: Object,
      required: true
    }
  },
  emits: ['close', 'share'],
  setup(props, { emit }) {
    const shareType = ref('user')
    const selectedRecipient = ref('')
    const sharePermission = ref('edit')
    const currentUser = ref(null)
    const allUsers = ref([])
    const allTeams = ref([])
    const currentShares = ref([])

    const recipientOptions = computed(() => {
      if (shareType.value === 'user') {
        // Filter out current user
        return allUsers.value.filter(u => u.id !== currentUser.value.id)
      } else {
        // Get user's teams
        return allTeams.value.filter(t => 
          t.members.some(m => m.userId === currentUser.value.id)
        )
      }
    })

    const collaborators = computed(() => {
      const tasks = JSON.parse(localStorage.getItem('tasks') || '[]')
      const task = tasks.find(t => t.id === props.task.id)
      if (!task || !task.collaborators) return []

      return task.collaborators.filter(c => c.id !== currentUser.value.id)
    })

    const formatPermission = (permission) => {
      const names = {
        'view': '👁️ View Only',
        'edit': '✏️ Edit',
        'admin': '🔐 Full Access'
      }
      return names[permission] || permission
    }

    const canManageShare = (share) => {
      // Can manage shares if user is task owner or has admin permission
      return props.task.userId === currentUser.value.id || 
             taskSharingService.canDelete(props.task.id, currentUser.value.id)
    }

    const shareTask = () => {
      if (!selectedRecipient.value) {
        alert('Please select a recipient')
        return
      }

      const result = taskSharingService.shareTask(
        props.task.id,
        selectedRecipient.value,
        shareType.value,
        sharePermission.value
      )

      if (result.success) {
        loadCurrentShares()
        selectedRecipient.value = ''
        sharePermission.value = 'edit'
        emit('share', result.share)
        alert('Task shared successfully!')
      } else {
        alert('Error: ' + result.message)
      }
    }

    const loadCurrentShares = () => {
      currentShares.value = taskSharingService.getTaskShares(props.task.id)
    }

    const updatePermission = (shareId, newPermission) => {
      const result = taskSharingService.updateSharePermission(shareId, newPermission)
      if (result.success) {
        loadCurrentShares()
      }
    }

    const revokeAccess = (shareId) => {
      if (confirm('Revoke access to this user/team?')) {
        const result = taskSharingService.revokeAccess(shareId)
        if (result.success) {
          loadCurrentShares()
        }
      }
    }

    const close = () => {
      emit('close')
    }

    const closeOnBackdrop = (e) => {
      if (e.target === e.currentTarget) {
        close()
      }
    }

    watch(() => props.isOpen, (newVal) => {
      if (newVal) {
        currentUser.value = authService.getCurrentUser()
        allUsers.value = authService.getAllUsers()
        allTeams.value = teamService.getAllTeams()
        loadCurrentShares()
      }
    })

    return {
      shareType,
      selectedRecipient,
      sharePermission,
      currentShares,
      collaborators,
      recipientOptions,
      formatPermission,
      canManageShare,
      shareTask,
      updatePermission,
      revokeAccess,
      close,
      closeOnBackdrop
    }
  }
}
</script>

<style scoped>
.task-sharing-dialog {
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
  padding: 20px;
}

.dialog-content {
  background: white;
  border-radius: 12px;
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 2px solid #e8eaed;
}

.dialog-header h2 {
  margin: 0;
  color: #202124;
  font-size: 20px;
}

.close-button {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #5f6368;
  transition: color 0.2s;
}

.close-button:hover {
  color: #202124;
}

.dialog-body {
  padding: 24px;
}

.task-info {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 24px;
}

.task-info h3 {
  margin: 0 0 8px 0;
  color: #202124;
  font-size: 16px;
}

.task-info p {
  margin: 0;
  color: #5f6368;
  font-size: 13px;
  line-height: 1.4;
}

.share-section,
.shares-section,
.collaborators-section {
  margin-bottom: 24px;
}

.share-section h4,
.shares-section h4,
.collaborators-section h4 {
  margin: 0 0 16px 0;
  color: #202124;
  font-size: 14px;
  font-weight: 600;
}

.share-type-selector {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.type-btn {
  flex: 1;
  padding: 10px;
  background: #f5f5f5;
  border: 2px solid #e8eaed;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.2s;
}

.type-btn:hover {
  border-color: #0066cc;
  background: #f0f7ff;
}

.type-btn.active {
  background: #0066cc;
  color: white;
  border-color: #0066cc;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: #495057;
  font-size: 13px;
}

.form-select {
  width: 100%;
  padding: 10px;
  border: 2px solid #e8eaed;
  border-radius: 6px;
  font-size: 13px;
  box-sizing: border-box;
}

.form-select:focus {
  outline: none;
  border-color: #0066cc;
}

.permission-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.radio-label {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 10px;
  background: #f9f9f9;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s;
}

.radio-label:hover {
  background: #f5f5f5;
}

.radio-label input[type="radio"] {
  margin-top: 2px;
  cursor: pointer;
}

.radio-text {
  flex: 1;
}

.radio-text strong {
  display: block;
  color: #202124;
  margin-bottom: 2px;
}

.radio-text small {
  display: block;
  color: #5f6368;
  font-size: 11px;
}

.btn-share {
  width: 100%;
  padding: 12px;
  background: #0066cc;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
  margin-top: 16px;
}

.btn-share:hover:not(:disabled) {
  background: #0052a3;
}

.btn-share:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.empty-shares {
  text-align: center;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 6px;
  color: #5f6368;
  font-size: 13px;
}

.shares-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.share-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 6px;
  border: 1px solid #e8eaed;
}

.share-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
}

.share-recipient {
  display: flex;
  align-items: center;
  gap: 6px;
}

.recipient-icon {
  font-size: 16px;
}

.recipient-name {
  color: #202124;
  font-weight: 500;
  font-size: 13px;
}

.permission-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
}

.permission-badge.view {
  background: #e3f2fd;
  color: #1976d2;
}

.permission-badge.edit {
  background: #f3e5f5;
  color: #7b1fa2;
}

.permission-badge.admin {
  background: #fce4ec;
  color: #c2185b;
}

.share-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.permission-select {
  padding: 6px;
  border: 1px solid #dee2e6;
  border-radius: 4px;
  font-size: 12px;
}

.btn-revoke {
  padding: 6px 12px;
  background: #dc3545;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-revoke:hover {
  background: #c82333;
}

.collaborators-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.collaborator-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background: #f9f9f9;
  border-radius: 6px;
}

.collaborator-name {
  color: #202124;
  font-weight: 500;
  font-size: 13px;
}

.collaborator-role {
  display: inline-block;
  background: #e8f0fe;
  color: #0066cc;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 11px;
  font-weight: 600;
}

.dialog-footer {
  padding: 16px 24px;
  border-top: 2px solid #e8eaed;
  display: flex;
  justify-content: flex-end;
}

.btn-secondary {
  padding: 10px 20px;
  background: #e8eaed;
  color: #202124;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-secondary:hover {
  background: #d2d3d7;
}

@media (max-width: 640px) {
  .dialog-content {
    max-width: 100%;
    max-height: 100%;
    border-radius: 0;
  }

  .share-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .share-actions {
    width: 100%;
    margin-top: 8px;
  }

  .share-actions select,
  .share-actions button {
    flex: 1;
  }
}
</style>
