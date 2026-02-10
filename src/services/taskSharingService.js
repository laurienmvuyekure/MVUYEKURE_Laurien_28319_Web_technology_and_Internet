/**
 * Task Sharing Service
 * Manages task sharing and collaboration
 */

class TaskSharingService {
  constructor() {
    this.sharingKey = 'task-sharing'
    this.permissionsKey = 'task-permissions'
  }

  /**
   * Share task with user or team
   */
  shareTask(taskId, recipientId, recipientType = 'user', permission = 'view') {
    const sharings = this.getSharedTasks()
    
    // Check for duplicate share
    if (sharings.some(s => 
      s.taskId === taskId && 
      s.recipientId === recipientId && 
      s.recipientType === recipientType
    )) {
      return {
        success: false,
        message: 'Task already shared with this recipient'
      }
    }

    const sharing = {
      id: Date.now(),
      taskId,
      recipientId,
      recipientType, // 'user' or 'team'
      permission, // 'view', 'edit', 'admin'
      sharedAt: new Date().toISOString(),
      isActive: true
    }

    sharings.push(sharing)
    localStorage.setItem(this.sharingKey, JSON.stringify(sharings))

    return {
      success: true,
      message: 'Task shared successfully',
      sharing
    }
  }

  /**
   * Get all tasks shared with a user
   */
  getUserSharedTasks(userId) {
    const sharings = this.getSharedTasks()
    return sharings.filter(s => 
      s.recipientId === userId && 
      s.recipientType === 'user' &&
      s.isActive
    )
  }

  /**
   * Get all tasks shared in a team
   */
  getTeamSharedTasks(teamId) {
    const sharings = this.getSharedTasks()
    return sharings.filter(s => 
      s.recipientId === teamId && 
      s.recipientType === 'team' &&
      s.isActive
    )
  }

  /**
   * Get all shares for a specific task
   */
  getTaskShares(taskId) {
    const sharings = this.getSharedTasks()
    return sharings.filter(s => s.taskId === taskId && s.isActive)
  }

  /**
   * Get all shared tasks
   */
  getSharedTasks() {
    const sharings = localStorage.getItem(this.sharingKey)
    return sharings ? JSON.parse(sharings) : []
  }

  /**
   * Check user permission on shared task
   */
  getUserTaskPermission(taskId, userId) {
    const sharings = this.getSharedTasks()
    const sharing = sharings.find(s => 
      s.taskId === taskId && 
      s.recipientId === userId &&
      s.recipientType === 'user' &&
      s.isActive
    )
    return sharing ? sharing.permission : 'none'
  }

  /**
   * Update share permission
   */
  updateSharePermission(shareId, newPermission) {
    const sharings = this.getSharedTasks()
    const sharing = sharings.find(s => s.id === shareId)

    if (!sharing) {
      return {
        success: false,
        message: 'Share not found'
      }
    }

    sharing.permission = newPermission
    localStorage.setItem(this.sharingKey, JSON.stringify(sharings))

    return {
      success: true,
      message: 'Permission updated',
      sharing
    }
  }

  /**
   * Revoke task access
   */
  revokeAccess(shareId) {
    const sharings = this.getSharedTasks()
    const sharing = sharings.find(s => s.id === shareId)

    if (!sharing) {
      return {
        success: false,
        message: 'Share not found'
      }
    }

    sharing.isActive = false
    localStorage.setItem(this.sharingKey, JSON.stringify(sharings))

    return {
      success: true,
      message: 'Access revoked'
    }
  }

  /**
   * Can user edit task
   */
  canEdit(taskId, userId) {
    const permission = this.getUserTaskPermission(taskId, userId)
    return permission === 'edit' || permission === 'admin'
  }

  /**
   * Can user delete task
   */
  canDelete(taskId, userId) {
    const permission = this.getUserTaskPermission(taskId, userId)
    return permission === 'admin'
  }

  /**
   * Get collaborators on a task
   */
  getTaskCollaborators(taskId) {
    return this.getTaskShares(taskId)
  }
}

export default new TaskSharingService()
