<template>
  <div class="team-management">
    <div class="team-header">
      <h1>👥 Team Management</h1>
      <p>Create and manage your teams</p>
    </div>

    <div class="team-container">
      <!-- Create Team Section -->
      <div v-if="!selectedTeam" class="teams-list-view">
        <div class="controls-bar">
          <div class="search-box">
            <input 
              v-model="searchQuery"
              type="text"
              placeholder="Search teams..."
              class="search-input"
            />
          </div>
          <button @click="openCreateTeamForm" class="btn-create">
            + Create Team
          </button>
        </div>

        <!-- Create Team Form -->
        <div v-if="showCreateForm" class="form-card">
          <h2>Create New Team</h2>
          <div class="form-group">
            <label>Team Name</label>
            <input 
              v-model="newTeam.name"
              type="text"
              placeholder="Enter team name"
              class="form-input"
            />
          </div>
          <div class="form-group">
            <label>Description</label>
            <textarea 
              v-model="newTeam.description"
              placeholder="Describe this team..."
              class="form-textarea"
            ></textarea>
          </div>
          <div class="form-actions">
            <button @click="createTeam" class="btn-primary">Create Team</button>
            <button @click="cancelCreateTeam" class="btn-secondary">Cancel</button>
          </div>
        </div>

        <!-- Teams Grid -->
        <div class="teams-grid">
          <div 
            v-for="team in filteredTeams"
            :key="team.id"
            class="team-card"
            @click="selectTeam(team)"
          >
            <div class="team-card-header">
              <h3>{{ team.name }}</h3>
              <span class="member-badge">{{ team.members.length }} members</span>
            </div>
            <p class="team-description">{{ team.description || 'No description' }}</p>
            <div class="team-card-footer">
              <span class="team-role" v-if="isTeamOwner(team)">🔑 Owner</span>
              <span class="team-role" v-else>👤 Member</span>
              <span class="created-date">{{ formatDate(team.createdAt) }}</span>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-if="filteredTeams.length === 0 && !showCreateForm" class="empty-state">
          <div class="empty-icon">👥</div>
          <h3>No Teams Yet</h3>
          <p>Create your first team to collaborate with others</p>
          <button @click="openCreateTeamForm" class="btn-primary">Create First Team</button>
        </div>

        <!-- Pending Invitations -->
        <div v-if="pendingInvitations.length > 0" class="invitations-section">
          <h2>Pending Team Invitations</h2>
          <div class="invitations-list">
            <div 
              v-for="invite in pendingInvitations"
              :key="invite.id"
              class="invitation-card"
            >
              <div>
                <h4>{{ invite.teamName }}</h4>
                <p>Invited by {{ invite.invitedBy }}</p>
              </div>
              <div class="invitation-actions">
                <button @click="acceptInvitation(invite)" class="btn-success">
                  Accept
                </button>
                <button @click="declineInvitation(invite)" class="btn-danger">
                  Decline
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Team Details View -->
      <div v-else class="team-details-view">
        <div class="details-header">
          <button @click="selectTeam(null)" class="btn-back">← Back to Teams</button>
          <h2>{{ selectedTeam.name }}</h2>
          <div class="header-actions">
            <button 
              v-if="isTeamOwner(selectedTeam)"
              @click="openInviteForm"
              class="btn-secondary"
            >
              + Invite Member
            </button>
            <button 
              v-if="isTeamOwner(selectedTeam)"
              @click="deleteTeam(selectedTeam.id)"
              class="btn-danger"
            >
              Delete Team
            </button>
          </div>
        </div>

        <!-- Team Info Card -->
        <div class="info-card">
          <h3>Team Information</h3>
          <div class="info-grid">
            <div class="info-item">
              <label>Name</label>
              <p>{{ selectedTeam.name }}</p>
            </div>
            <div class="info-item">
              <label>Owner</label>
              <p>{{ getTeamOwnerName(selectedTeam) }}</p>
            </div>
            <div class="info-item">
              <label>Members</label>
              <p>{{ selectedTeam.members.length }}</p>
            </div>
            <div class="info-item">
              <label>Created</label>
              <p>{{ formatDate(selectedTeam.createdAt) }}</p>
            </div>
          </div>
          <div class="info-item full-width">
            <label>Description</label>
            <p>{{ selectedTeam.description || 'No description' }}</p>
          </div>
        </div>

        <!-- Invite Form -->
        <div v-if="showInviteForm" class="form-card">
          <h3>Invite Team Member</h3>
          <div class="form-group">
            <label>User Email</label>
            <input 
              v-model="inviteForm.email"
              type="email"
              placeholder="Enter user email"
              class="form-input"
            />
          </div>
          <div class="form-group">
            <label>Role</label>
            <select v-model="inviteForm.role" class="form-input">
              <option value="member">Member</option>
              <option value="admin">Team Admin</option>
            </select>
          </div>
          <div class="form-actions">
            <button @click="sendInvitation" class="btn-primary">Send Invitation</button>
            <button @click="cancelInvite" class="btn-secondary">Cancel</button>
          </div>
        </div>

        <!-- Members List -->
        <div class="members-section">
          <h3>Team Members ({{ selectedTeam.members.length }})</h3>
          <div class="members-table-container">
            <table class="members-table">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Role</th>
                  <th>Joined</th>
                  <th v-if="isTeamOwner(selectedTeam)">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="member in selectedTeam.members" :key="member.userId">
                  <td>{{ member.name }}</td>
                  <td>{{ member.email }}</td>
                  <td>
                    <span v-if="isTeamOwner(selectedTeam) && member.userId !== selectedTeam.ownerId">
                      <select 
                        :value="member.role"
                        @change="changeMemberRole(member.userId, $event.target.value)"
                        class="role-select"
                      >
                        <option value="member">Member</option>
                        <option value="admin">Admin</option>
                      </select>
                    </span>
                    <span v-else>
                      {{ formatRole(member.role) }}
                      <span v-if="member.userId === selectedTeam.ownerId" class="owner-badge">🔑 Owner</span>
                    </span>
                  </td>
                  <td>{{ formatDate(member.joinedAt) }}</td>
                  <td v-if="isTeamOwner(selectedTeam)">
                    <button 
                      v-if="member.userId !== selectedTeam.ownerId"
                      @click="removeMember(member.userId)"
                      class="btn-small btn-danger"
                    >
                      Remove
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import authService from '../services/authService'
import teamService from '../services/teamService'

export default {
  name: 'TeamManagement',
  setup() {
    const selectedTeam = ref(null)
    const showCreateForm = ref(false)
    const showInviteForm = ref(false)
    const searchQuery = ref('')
    const allTeams = ref([])
    const currentUser = ref(null)
    const pendingInvitations = ref([])

    const newTeam = ref({
      name: '',
      description: ''
    })

    const inviteForm = ref({
      email: '',
      role: 'member'
    })

    const filteredTeams = computed(() => {
      const query = searchQuery.value.toLowerCase()
      return allTeams.value.filter(team =>
        team.name.toLowerCase().includes(query) ||
        team.description.toLowerCase().includes(query)
      )
    })

    const openCreateTeamForm = () => {
      showCreateForm.value = true
    }

    const cancelCreateTeam = () => {
      showCreateForm.value = false
      newTeam.value = { name: '', description: '' }
    }

    const createTeam = () => {
      if (!newTeam.value.name.trim()) {
        alert('Team name is required')
        return
      }

      const result = teamService.createTeam(
        newTeam.value.name,
        newTeam.value.description,
        currentUser.value.id
      )

      if (result.success) {
        allTeams.value.push(result.team)
        cancelCreateTeam()
        alert('Team created successfully!')
      } else {
        alert('Error creating team: ' + result.message)
      }
    }

    const selectTeam = (team) => {
      selectedTeam.value = team
    }

    const isTeamOwner = (team) => {
      return team.ownerId === currentUser.value.id
    }

    const getTeamOwnerName = (team) => {
      const owner = team.members.find(m => m.userId === team.ownerId)
      return owner ? owner.name : 'Unknown'
    }

    const openInviteForm = () => {
      showInviteForm.value = true
    }

    const cancelInvite = () => {
      showInviteForm.value = false
      inviteForm.value = { email: '', role: 'member' }
    }

    const sendInvitation = () => {
      if (!inviteForm.value.email.trim()) {
        alert('Email is required')
        return
      }

      const result = teamService.inviteUserToTeam(
        selectedTeam.value.id,
        inviteForm.value.email,
        inviteForm.value.role,
        currentUser.value.email
      )

      if (result.success) {
        cancelInvite()
        alert('Invitation sent successfully!')
      } else {
        alert('Error: ' + result.message)
      }
    }

    const changeMemberRole = (userId, newRole) => {
      const result = teamService.changeTeamMemberRole(
        selectedTeam.value.id,
        userId,
        newRole
      )

      if (result.success) {
        const member = selectedTeam.value.members.find(m => m.userId === userId)
        if (member) {
          member.role = newRole
        }
      }
    }

    const removeMember = (userId) => {
      if (confirm('Are you sure you want to remove this member?')) {
        const result = teamService.removeTeamMember(selectedTeam.value.id, userId)

        if (result.success) {
          selectedTeam.value.members = selectedTeam.value.members.filter(
            m => m.userId !== userId
          )
        }
      }
    }

    const deleteTeam = (teamId) => {
      if (confirm('Are you sure you want to delete this team? This action cannot be undone.')) {
        const result = teamService.deleteTeam(teamId)

        if (result.success) {
          allTeams.value = allTeams.value.filter(t => t.id !== teamId)
          selectedTeam.value = null
          alert('Team deleted successfully')
        }
      }
    }

    const acceptInvitation = (invitation) => {
      const result = teamService.acceptInvitation(invitation.id, currentUser.value.id)

      if (result.success) {
        // Reload teams
        const userTeams = teamService.getUserTeams(currentUser.value.id)
        allTeams.value = userTeams
        
        // Remove from pending
        pendingInvitations.value = pendingInvitations.value.filter(
          i => i.id !== invitation.id
        )
        alert('Team invitation accepted!')
      }
    }

    const declineInvitation = (invitation) => {
      pendingInvitations.value = pendingInvitations.value.filter(
        i => i.id !== invitation.id
      )
    }

    const formatDate = (dateString) => {
      if (!dateString) return 'N/A'
      return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      })
    }

    const formatRole = (role) => {
      const roleMap = {
        'owner': 'Owner',
        'admin': 'Admin',
        'member': 'Member'
      }
      return roleMap[role] || role
    }

    onMounted(() => {
      currentUser.value = authService.getCurrentUser()

      if (!currentUser.value) {
        alert('Please log in first')
        window.history.back()
        return
      }

      allTeams.value = teamService.getUserTeams(currentUser.value.id)

      // Load pending invitations
      const invitations = teamService.getTeamInvitations(currentUser.value.email)
      pendingInvitations.value = invitations
    })

    return {
      selectedTeam,
      showCreateForm,
      showInviteForm,
      searchQuery,
      allTeams,
      currentUser,
      newTeam,
      inviteForm,
      pendingInvitations,
      filteredTeams,
      openCreateTeamForm,
      cancelCreateTeam,
      createTeam,
      selectTeam,
      isTeamOwner,
      getTeamOwnerName,
      openInviteForm,
      cancelInvite,
      sendInvitation,
      changeMemberRole,
      removeMember,
      deleteTeam,
      acceptInvitation,
      declineInvitation,
      formatDate,
      formatRole
    }
  }
}
</script>

<style scoped>
.team-management {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 30px 20px;
}

.team-header {
  max-width: 1400px;
  margin: 0 auto 30px;
  padding: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
}

.team-header h1 {
  margin: 0 0 8px 0;
  font-size: 32px;
}

.team-header p {
  margin: 0;
  opacity: 0.9;
}

.team-container {
  max-width: 1400px;
  margin: 0 auto;
}

.teams-list-view {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.controls-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 30px;
}

.search-box {
  flex: 1;
}

.search-input {
  width: 100%;
  padding: 12px;
  border: 2px solid #e8eaed;
  border-radius: 6px;
  font-size: 14px;
}

.search-input:focus {
  outline: none;
  border-color: #0066cc;
}

.btn-create {
  padding: 12px 24px;
  background: #0066cc;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.btn-create:hover {
  background: #0052a3;
}

.form-card {
  background: #f5f7fa;
  border: 2px solid #e8eaed;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 30px;
}

.form-card h2,
.form-card h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #202124;
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

.form-input,
.form-textarea {
  width: 100%;
  padding: 10px;
  border: 2px solid #dee2e6;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
  font-family: inherit;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #0066cc;
}

.form-textarea {
  resize: vertical;
  min-height: 100px;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.teams-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.team-card {
  background: white;
  border: 2px solid #e8eaed;
  border-radius: 8px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.team-card:hover {
  border-color: #0066cc;
  box-shadow: 0 4px 12px rgba(0, 102, 204, 0.15);
  transform: translateY(-2px);
}

.team-card-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 12px;
}

.team-card-header h3 {
  margin: 0;
  color: #202124;
  font-size: 18px;
}

.member-badge {
  display: inline-block;
  background: #e8f0fe;
  color: #0066cc;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
}

.team-description {
  margin: 12px 0;
  color: #5f6368;
  font-size: 13px;
  line-height: 1.5;
}

.team-card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #e8eaed;
}

.team-role {
  font-size: 12px;
  font-weight: 600;
  color: #1f73e6;
}

.created-date {
  font-size: 12px;
  color: #5f6368;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.empty-state h3 {
  margin: 0 0 8px 0;
  color: #202124;
  font-size: 20px;
}

.empty-state p {
  margin: 0 0 24px 0;
  color: #5f6368;
}

.invitations-section {
  margin-top: 40px;
  padding-top: 30px;
  border-top: 2px solid #e8eaed;
}

.invitations-section h2 {
  margin-top: 0;
  margin-bottom: 20px;
}

.invitations-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.invitation-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f5f7fa;
  border: 2px solid #e8eaed;
  border-radius: 8px;
  padding: 16px;
}

.invitation-card h4 {
  margin: 0 0 4px 0;
  color: #202124;
}

.invitation-card p {
  margin: 0;
  color: #5f6368;
  font-size: 13px;
}

.invitation-actions {
  display: flex;
  gap: 8px;
}

.team-details-view {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.details-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #e8eaed;
}

.btn-back {
  padding: 10px 16px;
  background: #f5f5f5;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
}

.btn-back:hover {
  background: #e8eaed;
}

.details-header h2 {
  flex: 1;
  margin: 0;
  color: #202124;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.info-card {
  background: #f5f7fa;
  border: 2px solid #e8eaed;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 30px;
}

.info-card h3 {
  margin-top: 0;
  margin-bottom: 20px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.info-item {
  background: white;
  padding: 12px;
  border-radius: 6px;
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.info-item label {
  display: block;
  font-size: 12px;
  font-weight: 600;
  color: #5f6368;
  margin-bottom: 4px;
}

.info-item p {
  margin: 0;
  color: #202124;
}

.members-section {
  margin-top: 30px;
}

.members-section h3 {
  margin-top: 0;
  margin-bottom: 16px;
}

.members-table-container {
  overflow-x: auto;
}

.members-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.members-table thead {
  background: #f5f5f5;
}

.members-table th {
  padding: 12px;
  text-align: left;
  font-weight: 600;
  border-bottom: 2px solid #e8eaed;
}

.members-table td {
  padding: 12px;
  border-bottom: 1px solid #e8eaed;
}

.members-table tbody tr:hover {
  background: #f9f9f9;
}

.role-select {
  padding: 6px;
  border: 1px solid #dee2e6;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
}

.owner-badge {
  display: inline-block;
  background: #fce5cd;
  color: #d97706;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 11px;
  font-weight: 600;
  margin-left: 6px;
}

.btn-primary,
.btn-secondary,
.btn-danger,
.btn-success {
  padding: 10px 16px;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  font-size: 14px;
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

.btn-success {
  background: #28a745;
  color: white;
}

.btn-success:hover {
  background: #218838;
}

.btn-small {
  padding: 6px 12px;
  font-size: 12px;
}

@media (max-width: 768px) {
  .team-header {
    padding: 20px;
  }

  .team-header h1 {
    font-size: 24px;
  }

  .teams-list-view,
  .team-details-view {
    padding: 20px;
  }

  .controls-bar {
    flex-direction: column;
  }

  .teams-grid {
    grid-template-columns: 1fr;
  }

  .details-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-actions {
    width: 100%;
  }

  .header-actions button {
    flex: 1;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .members-table {
    font-size: 12px;
  }

  .members-table th,
  .members-table td {
    padding: 8px;
  }
}
</style>
