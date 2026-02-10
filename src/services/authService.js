/**
 * Authentication Service
 * Manages user registration, login, password reset, and permission management
 */

// Simple password hashing simulation
const hashPassword = (password) => {
  // Simple hash - in production use bcrypt or similar
  let hash = 0
  for (let i = 0; i < password.length; i++) {
    const char = password.charCodeAt(i)
    hash = ((hash << 5) - hash) + char
    hash = hash & hash // Convert to 32-bit integer
  }
  return 'hash_' + Math.abs(hash).toString(16)
}

// User Roles and Permissions
export const USER_ROLES = {
  ADMIN: 'admin',
  TEAM_LEAD: 'team_lead',
  USER: 'user',
  GUEST: 'guest'
}

export const PERMISSIONS = {
  ADMIN: {
    create_user: true,
    delete_user: true,
    manage_permissions: true,
    view_all_tasks: true,
    manage_teams: true,
    access_reports: true,
    create_task: true,
    edit_task: true,
    delete_task: true,
    share_task: true,
    reset_password: true
  },
  TEAM_LEAD: {
    create_user: false,
    delete_user: false,
    manage_permissions: false,
    view_all_tasks: true,
    manage_teams: true,
    access_reports: true,
    create_task: true,
    edit_task: true,
    delete_task: true,
    share_task: true,
    reset_password: false
  },
  USER: {
    create_user: false,
    delete_user: false,
    manage_permissions: false,
    view_all_tasks: false,
    manage_teams: false,
    access_reports: false,
    create_task: true,
    edit_task: true,
    delete_task: true,
    share_task: true,
    reset_password: false
  },
  GUEST: {
    create_user: false,
    delete_user: false,
    manage_permissions: false,
    view_all_tasks: false,
    manage_teams: false,
    access_reports: false,
    create_task: false,
    edit_task: false,
    delete_task: false,
    share_task: false,
    reset_password: false
  }
}

class AuthService {
  constructor() {
    this.storageKey = 'users'
    this.sessionKey = 'currentUser'
    this.resetTokensKey = 'passwordResetTokens'
    this.initializeDefaultUsers()
  }

  // Initialize with a default admin user for demo
  initializeDefaultUsers() {
    const existingUsers = this.getAllUsers()
    if (existingUsers.length === 0) {
      const defaultAdmin = {
        id: Date.now(),
        email: 'admin@taskbuddy.com',
        name: 'Admin User',
        password: hashPassword('admin123'),
        role: USER_ROLES.ADMIN,
        createdAt: new Date().toISOString(),
        verified: true,
        active: true,
        profilePicture: null,
        preferences: {
          theme: 'light',
          notifications: true,
          emailUpdates: true
        }
      }

      const demoUser = {
        id: Date.now() + 1,
        email: 'demo@taskbuddy.com',
        name: 'Demo User',
        password: hashPassword('demo123'),
        role: USER_ROLES.USER,
        createdAt: new Date().toISOString(),
        verified: true,
        active: true,
        profilePicture: null,
        preferences: {
          theme: 'light',
          notifications: true,
          emailUpdates: true
        }
      }

      localStorage.setItem(this.storageKey, JSON.stringify([defaultAdmin, demoUser]))
    }
  }

  /**
   * Register a new user
   */
  register(userData) {
    const { email, password, name, passwordConfirm } = userData

    // Validation
    if (!email || !password || !name || !passwordConfirm) {
      return {
        success: false,
        message: 'All fields are required'
      }
    }

    if (password !== passwordConfirm) {
      return {
        success: false,
        message: 'Passwords do not match'
      }
    }

    if (password.length < 6) {
      return {
        success: false,
        message: 'Password must be at least 6 characters'
      }
    }

    // Check if email already exists
    const users = this.getAllUsers()
    if (users.some(u => u.email.toLowerCase() === email.toLowerCase())) {
      return {
        success: false,
        message: 'Email already registered'
      }
    }

    // Create new user
    const newUser = {
      id: Date.now(),
      email: email.toLowerCase(),
      password: hashPassword(password),
      name,
      role: USER_ROLES.USER, // New users are regular users
      createdAt: new Date().toISOString(),
      verified: false, // Requires email verification in production
      active: true,
      profilePicture: null,
      preferences: {
        theme: 'light',
        notifications: true,
        emailUpdates: true
      }
    }

    users.push(newUser)
    localStorage.setItem(this.storageKey, JSON.stringify(users))

    return {
      success: true,
      message: 'Registration successful! Please log in.',
      user: this.sanitizeUser(newUser)
    }
  }

  /**
   * Login user
   */
  login(email, password) {
    if (!email || !password) {
      return {
        success: false,
        message: 'Email and password are required'
      }
    }

    const users = this.getAllUsers()
    const user = users.find(u => u.email.toLowerCase() === email.toLowerCase())

    if (!user) {
      return {
        success: false,
        message: 'Invalid email or password'
      }
    }

    if (!user.active) {
      return {
        success: false,
        message: 'Account is disabled'
      }
    }

    // Verify password
    if (user.password !== hashPassword(password)) {
      return {
        success: false,
        message: 'Invalid email or password'
      }
    }

    // Set current session
    const sessionUser = this.sanitizeUser(user)
    localStorage.setItem(this.sessionKey, JSON.stringify(sessionUser))

    return {
      success: true,
      message: 'Login successful',
      user: sessionUser
    }
  }

  /**
   * Request password reset
   */
  requestPasswordReset(email) {
    const users = this.getAllUsers()
    const user = users.find(u => u.email.toLowerCase() === email.toLowerCase())

    if (!user) {
      // Don't reveal if email exists (security)
      return {
        success: true,
        message: 'If an account exists, a reset link will be sent'
      }
    }

    // Generate reset token
    const resetToken = {
      userId: user.id,
      token: Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15),
      createdAt: Date.now(),
      expiresAt: Date.now() + 3600000 // 1 hour expiry
    }

    // Store reset token
    const tokens = this.getResetTokens()
    tokens.push(resetToken)
    localStorage.setItem(this.resetTokensKey, JSON.stringify(tokens))

    return {
      success: true,
      message: 'If an account exists, a reset link will be sent',
      token: resetToken.token // In production, send via email
    }
  }

  /**
   * Verify reset token
   */
  verifyResetToken(token) {
    const tokens = this.getResetTokens()
    const resetToken = tokens.find(t => t.token === token)

    if (!resetToken) {
      return {
        success: false,
        message: 'Invalid reset link'
      }
    }

    if (resetToken.expiresAt < Date.now()) {
      return {
        success: false,
        message: 'Reset link has expired'
      }
    }

    return {
      success: true,
      message: 'Token is valid',
      userId: resetToken.userId
    }
  }

  /**
   * Reset password with token
   */
  resetPassword(token, newPassword, confirmPassword) {
    const tokenVerify = this.verifyResetToken(token)
    if (!tokenVerify.success) {
      return tokenVerify
    }

    if (newPassword !== confirmPassword) {
      return {
        success: false,
        message: 'Passwords do not match'
      }
    }

    if (newPassword.length < 6) {
      return {
        success: false,
        message: 'Password must be at least 6 characters'
      }
    }

    // Update user password
    const users = this.getAllUsers()
    const userIndex = users.findIndex(u => u.id === tokenVerify.userId)

    if (userIndex === -1) {
      return {
        success: false,
        message: 'User not found'
      }
    }

    users[userIndex].password = hashPassword(newPassword)
    localStorage.setItem(this.storageKey, JSON.stringify(users))

    // Remove used reset token
    const tokens = this.getResetTokens()
    const filteredTokens = tokens.filter(t => t.token !== token)
    localStorage.setItem(this.resetTokensKey, JSON.stringify(filteredTokens))

    return {
      success: true,
      message: 'Password reset successful. Please log in with your new password.'
    }
  }

  /**
   * Logout current user
   */
  logout() {
    localStorage.removeItem(this.sessionKey)
    return {
      success: true,
      message: 'Logged out successfully'
    }
  }

  /**
   * Get current logged-in user
   */
  getCurrentUser() {
    const user = localStorage.getItem(this.sessionKey)
    return user ? JSON.parse(user) : null
  }

  /**
   * Check if user is authenticated
   */
  isAuthenticated() {
    return !!this.getCurrentUser()
  }

  /**
   * Check user permission
   */
  hasPermission(permission) {
    const user = this.getCurrentUser()
    if (!user) return false

    const rolePermissions = PERMISSIONS[user.role]
    return rolePermissions && rolePermissions[permission] === true
  }

  /**
   * Get all users (admin only)
   */
  getAllUsers() {
    const users = localStorage.getItem(this.storageKey)
    return users ? JSON.parse(users) : []
  }

  /**
   * Get specific user by ID (admin only)
   */
  getUserById(userId) {
    const users = this.getAllUsers()
    return users.find(u => u.id === userId)
  }

  /**
   * Change user role (admin only)
   */
  changeUserRole(userId, newRole) {
    const currentUser = this.getCurrentUser()
    
    if (!currentUser || currentUser.role !== USER_ROLES.ADMIN) {
      return {
        success: false,
        message: 'Only admins can change user roles'
      }
    }

    if (!Object.values(USER_ROLES).includes(newRole)) {
      return {
        success: false,
        message: 'Invalid role'
      }
    }

    const users = this.getAllUsers()
    const userIndex = users.findIndex(u => u.id === userId)

    if (userIndex === -1) {
      return {
        success: false,
        message: 'User not found'
      }
    }

    users[userIndex].role = newRole
    localStorage.setItem(this.storageKey, JSON.stringify(users))

    return {
      success: true,
      message: `User role changed to ${newRole}`
    }
  }

  /**
   * Disable/Enable user account (admin only)
   */
  toggleUserAccount(userId, active) {
    const currentUser = this.getCurrentUser()
    
    if (!currentUser || currentUser.role !== USER_ROLES.ADMIN) {
      return {
        success: false,
        message: 'Only admins can disable accounts'
      }
    }

    const users = this.getAllUsers()
    const userIndex = users.findIndex(u => u.id === userId)

    if (userIndex === -1) {
      return {
        success: false,
        message: 'User not found'
      }
    }

    users[userIndex].active = active
    localStorage.setItem(this.storageKey, JSON.stringify(users))

    return {
      success: true,
      message: `User account ${active ? 'enabled' : 'disabled'}`
    }
  }

  /**
   * Delete user (admin only)
   */
  deleteUser(userId) {
    const currentUser = this.getCurrentUser()
    
    if (!currentUser || currentUser.role !== USER_ROLES.ADMIN) {
      return {
        success: false,
        message: 'Only admins can delete users'
      }
    }

    if (userId === currentUser.id) {
      return {
        success: false,
        message: 'Cannot delete your own account'
      }
    }

    const users = this.getAllUsers()
    const filteredUsers = users.filter(u => u.id !== userId)
    localStorage.setItem(this.storageKey, JSON.stringify(filteredUsers))

    return {
      success: true,
      message: 'User deleted'
    }
  }

  /**
   * Update user profile
   */
  updateProfile(userId, updates) {
    const users = this.getAllUsers()
    const userIndex = users.findIndex(u => u.id === userId)

    if (userIndex === -1) {
      return {
        success: false,
        message: 'User not found'
      }
    }

    // Only allow certain fields to be updated
    const allowedUpdates = ['name', 'profilePicture', 'preferences']
    allowedUpdates.forEach(field => {
      if (field in updates) {
        users[userIndex][field] = updates[field]
      }
    })

    localStorage.setItem(this.storageKey, JSON.stringify(users))

    // Update session if it's the current user
    const currentUser = this.getCurrentUser()
    if (currentUser && currentUser.id === userId) {
      const updated = users[userIndex]
      localStorage.setItem(this.sessionKey, JSON.stringify(this.sanitizeUser(updated)))
    }

    return {
      success: true,
      message: 'Profile updated',
      user: this.sanitizeUser(users[userIndex])
    }
  }

  /**
   * Get user permissions
   */
  getPermissions(role = null) {
    if (role) {
      return PERMISSIONS[role] || PERMISSIONS[USER_ROLES.GUEST]
    }
    const user = this.getCurrentUser()
    return PERMISSIONS[user?.role] || PERMISSIONS[USER_ROLES.GUEST]
  }

  /**
   * Remove sensitive data from user object
   */
  sanitizeUser(user) {
    const { password, ...sanitized } = user
    return sanitized
  }

  /**
   * Get reset tokens
   */
  getResetTokens() {
    const tokens = localStorage.getItem(this.resetTokensKey)
    if (!tokens) return []
    
    const parsed = JSON.parse(tokens)
    // Clean up expired tokens
    return parsed.filter(t => t.expiresAt > Date.now())
  }
}

export default new AuthService()
