/**
 * Team Management Service
 * Manages team creation, membership, and collaboration
 */

class TeamService {
  constructor() {
    this.storageKey = 'teams'
    this.membershipKey = 'team-memberships'
  }

  /**
   * Create a new team
   */
  createTeam(name, description, ownerId) {
    const teams = this.getTeams()
    
    if (!name || name.trim().length < 2) {
      return {
        success: false,
        message: 'Team name must be at least 2 characters'
      }
    }

    const newTeam = {
      id: Date.now(),
      name: name.trim(),
      description: description || '',
      ownerId,
      createdAt: new Date().toISOString(),
      members: [{ userId: ownerId, role: 'owner' }],
      isActive: true
    }

    teams.push(newTeam)
    localStorage.setItem(this.storageKey, JSON.stringify(teams))

    return {
      success: true,
      message: 'Team created successfully',
      team: newTeam
    }
  }

  /**
   * Get all teams
   */
  getTeams() {
    const teams = localStorage.getItem(this.storageKey)
    return teams ? JSON.parse(teams) : []
  }

  /**
   * Get teams for a specific user
   */
  getUserTeams(userId) {
    const teams = this.getTeams()
    return teams.filter(team => 
      team.members.some(member => member.userId === userId)
    )
  }

  /**
   * Get team by ID
   */
  getTeamById(teamId) {
    const teams = this.getTeams()
    return teams.find(t => t.id === teamId)
  }

  /**
   * Add member to team
   */
  addTeamMember(teamId, userId, memberRole = 'member') {
    const teams = this.getTeams()
    const teamIndex = teams.findIndex(t => t.id === teamId)

    if (teamIndex === -1) {
      return {
        success: false,
        message: 'Team not found'
      }
    }

    const team = teams[teamIndex]
    if (team.members.some(m => m.userId === userId)) {
      return {
        success: false,
        message: 'User is already a member of this team'
      }
    }

    team.members.push({ userId, role: memberRole })
    localStorage.setItem(this.storageKey, JSON.stringify(teams))

    return {
      success: true,
      message: 'Member added to team',
      team
    }
  }

  /**
   * Remove member from team
   */
  removeTeamMember(teamId, userId) {
    const teams = this.getTeams()
    const teamIndex = teams.findIndex(t => t.id === teamId)

    if (teamIndex === -1) {
      return {
        success: false,
        message: 'Team not found'
      }
    }

    const team = teams[teamIndex]
    team.members = team.members.filter(m => m.userId !== userId)
    
    localStorage.setItem(this.storageKey, JSON.stringify(teams))

    return {
      success: true,
      message: 'Member removed from team'
    }
  }

  /**
   * Update team
   */
  updateTeam(teamId, updates) {
    const teams = this.getTeams()
    const teamIndex = teams.findIndex(t => t.id === teamId)

    if (teamIndex === -1) {
      return {
        success: false,
        message: 'Team not found'
      }
    }

    const allowedUpdates = ['name', 'description', 'isActive']
    allowedUpdates.forEach(field => {
      if (field in updates) {
        teams[teamIndex][field] = updates[field]
      }
    })

    localStorage.setItem(this.storageKey, JSON.stringify(teams))

    return {
      success: true,
      message: 'Team updated',
      team: teams[teamIndex]
    }
  }

  /**
   * Delete team
   */
  deleteTeam(teamId) {
    const teams = this.getTeams()
    const filtered = teams.filter(t => t.id !== teamId)
    localStorage.setItem(this.storageKey, JSON.stringify(filtered))

    return {
      success: true,
      message: 'Team deleted'
    }
  }

  /**
   * Get team members (returns member objects with user details)
   */
  getTeamMembers(teamId) {
    const team = this.getTeamById(teamId)
    if (!team) return []
    return team.members
  }

  /**
   * Change member role
   */
  changeTeamMemberRole(teamId, userId, newRole) {
    const teams = this.getTeams()
    const teamIndex = teams.findIndex(t => t.id === teamId)

    if (teamIndex === -1) {
      return {
        success: false,
        message: 'Team not found'
      }
    }

    const team = teams[teamIndex]
    const member = team.members.find(m => m.userId === userId)

    if (!member) {
      return {
        success: false,
        message: 'Member not found'
      }
    }

    member.role = newRole
    localStorage.setItem(this.storageKey, JSON.stringify(teams))

    return {
      success: true,
      message: 'Member role updated',
      team
    }
  }

  /**
   * Check if user is team owner
   */
  isTeamOwner(teamId, userId) {
    const team = this.getTeamById(teamId)
    return team && team.ownerId === userId
  }

  /**
   * Check if user is team member
   */
  isTeamMember(teamId, userId) {
    const team = this.getTeamById(teamId)
    return team && team.members.some(m => m.userId === userId)
  }

  /**
   * Invite user to team (creates invitation)
   */
  inviteUserToTeam(teamId, userEmail, role = 'member', invitedBy = null) {
    const invites = this.getTeamInvitations()

    const invite = {
      id: Date.now(),
      teamId,
      userEmail,
      role: role || 'member',
      invitedBy: invitedBy || 'system',
      createdAt: new Date().toISOString(),
      status: 'pending'
    }

    invites.push(invite)
    localStorage.setItem('team-invites', JSON.stringify(invites))

    return {
      success: true,
      message: 'Invitation sent',
      invite
    }
  }

  /**
   * Get team invitations
   */
  getTeamInvitations(filterEmail = null) {
    const invites = localStorage.getItem('team-invites')
    const parsed = invites ? JSON.parse(invites) : []
    if (filterEmail) {
      return parsed.filter(i => i.userEmail === filterEmail && i.status === 'pending')
    }
    return parsed
  }

  /**
   * Accept team invitation
   */
  acceptInvitation(inviteId, userId) {
    const invites = this.getTeamInvitations()
    const invite = invites.find(i => i.id === inviteId)

    if (!invite) {
      return {
        success: false,
        message: 'Invitation not found'
      }
    }

    // Add user to team using role from invite if present
    const result = this.addTeamMember(invite.teamId, userId, invite.role || 'member')

    if (result.success) {
      // Mark invitation as accepted
      invite.status = 'accepted'
      localStorage.setItem('team-invites', JSON.stringify(invites))
    }

    return result
  }

  /**
   * Return all teams (alias)
   */
  getAllTeams() {
    return this.getTeams()
  }
}

export default new TeamService()
