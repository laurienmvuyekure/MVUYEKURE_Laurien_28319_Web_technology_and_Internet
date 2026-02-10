<template>
  <header class="app-header" role="banner">
    <div class="header-container">
      <button 
        class="sidebar-toggle" 
        @click="$emit('toggle-sidebar')"
        aria-label="Toggle sidebar"
        aria-expanded="sidebarVisible"
      >
        <span class="hamburger" aria-hidden="true">☰</span>
      </button>
      
      <div class="logo">
        <router-link to="/" class="logo-link">
          <span aria-hidden="true">📝</span>
          <h1>TaskBuddy</h1>
        </router-link>
      </div>

      <div class="timer-indicator" v-if="activeTimer">
        <router-link to="/time-manager" class="timer-link" title="View timer details">
          <span class="timer-time">{{ formatTime(timerElapsed) }}</span>
          <span class="timer-task">{{ getTaskName(activeTimer.taskId) }}</span>
        </router-link>
      </div>
            
      <div class="user-actions">
        <div class="user-info" v-if="user">
          <span class="user-name">{{ user.name }}</span>
          <span class="user-role">{{ user.role }}</span>
        </div>
        
        <button 
          class="notification-bell"
          aria-label="View notifications"
          @click="showNotifications"
        >
          <span aria-hidden="true">🔔</span>
          <span class="badge" v-if="notificationCount > 0">
            {{ notificationCount }}
          </span>
        </button>
        
        <div class="avatar-wrapper">
          <router-link 
            to="/profile"
            class="avatar-button"
            type="button"
            :aria-label="user && user.name ? `View profile for ${user.name}` : 'View profile'"
           
          >
            <img
              v-if="user && user.avatar"
              :src="user.avatar"
              :alt="`Profile photo of ${user.name}`"
              class="avatar-img"
            />
            <img
              v-else
              src="/Passphoto.png"
              alt="Default profile photo"
              class="avatar-img"
            />
          </router-link>
        </div>

        <button
          class="logout-button"
          @click="handleLogout"
          aria-label="Log out"
          title="Log out"
        >
          <span aria-hidden="true">🚪</span>
        </button>
      </div>
    </div>
  </header>
</template>

<script>
import { ref, computed, inject, onMounted, onBeforeUnmount } from 'vue'

export default {
  name: 'LayoutHeader',
  props: {
    user: {
      type: Object,
      default: null
    }
  },
  emits: ['toggle-sidebar', 'update-avatar', 'logout'],
  setup(props, { emit }) {
    const sidebarVisible = ref(true)
    const notificationCount = ref(3)
    const avatarInput = ref(null)
    const activeTimer = inject('activeTimer', ref(null))
    const timerElapsed = ref(0)
    let timerInterval = null

    onMounted(() => {
      if (activeTimer.value) {
        startTimerDisplay()
      }
    })

    onBeforeUnmount(() => {
      if (timerInterval) clearInterval(timerInterval)
    })

    const startTimerDisplay = () => {
      if (timerInterval) clearInterval(timerInterval)
      timerInterval = setInterval(() => {
        if (activeTimer.value && !activeTimer.value.isPaused) {
          timerElapsed.value = Math.floor((Date.now() - activeTimer.value.startTime) / 1000)
        }
      }, 1000)
    }

    const showNotifications = () => {
      // Show notifications
    }

    const triggerAvatarInput = () => {
      if (avatarInput.value) avatarInput.value.click()
    }

    const resizeImage = (file, size = 128) => {
      return new Promise((resolve, reject) => {
        const img = new Image()
        const reader = new FileReader()
        reader.onload = (e) => {
          img.onload = () => {
            const canvas = document.createElement('canvas')
            canvas.width = size
            canvas.height = size
            const ctx = canvas.getContext('2d')
            // crop to square center
            const minSide = Math.min(img.width, img.height)
            const sx = (img.width - minSide) / 2
            const sy = (img.height - minSide) / 2
            ctx.drawImage(img, sx, sy, minSide, minSide, 0, 0, size, size)
            const dataUrl = canvas.toDataURL('image/png')
            resolve(dataUrl)
          }
          img.onerror = reject
          img.src = e.target.result
        }
        reader.onerror = reject
        reader.readAsDataURL(file)
      })
    }

    const onFileChange = async (e) => {
      const file = e.target.files && e.target.files[0]
      if (!file) return
      if (!file.type.startsWith('image/')) return
      try {
        const resized = await resizeImage(file, 128)
        emit('update-avatar', resized)
      } catch (err) {
        console.error('Failed to resize image', err)
      } finally {
        e.target.value = ''
      }
    }

    const userInitials = computed(() => {
      if (!props.user || !props.user.name) return ''
      return props.user.name.split(' ').map(n => n[0]).join('').substring(0,2).toUpperCase()
    })

    const handleLogout = () => {
      emit('logout')
    }

    const formatTime = (seconds) => {
      const hours = Math.floor(seconds / 3600)
      const minutes = Math.floor((seconds % 3600) / 60)
      const secs = seconds % 60

      if (hours > 0) {
        return `${hours}h ${minutes}m`
      } else if (minutes > 0) {
        return `${minutes}m ${secs}s`
      } else {
        return `${secs}s`
      }
    }

    const getTaskName = (taskId) => {
      return taskId ? 'Task Running' : 'No Task'
    }

    return {
      sidebarVisible,
      notificationCount,
      showNotifications,
      avatarInput,
      triggerAvatarInput,
      onFileChange,
      userInitials,
      handleLogout,
      activeTimer,
      timerElapsed,
      formatTime,
      getTaskName
    }
  }
}
</script>

<style scoped>
.app-header {
  background: linear-gradient(135deg, #1a73e8 0%, #1557b0 100%);
  color: white;
  padding: 0 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  position: relative;
  z-index: 100;
}

.header-container {
  display: flex;
  align-items: center;
  height: 60px;
  max-width: 1200px;
  margin: 0 auto;
  gap: 20px;
}

.sidebar-toggle {
  background: none;
  border: none;
  color: white;
  font-size: 24px;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.sidebar-toggle:hover,
.sidebar-toggle:focus {
  background: rgba(255,255,255,0.1);
  outline: 2px solid white;
}

.logo {
  flex-shrink: 0;
}

.logo-link {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  color: white;
}

.logo-link h1 {
  margin: 0;
  font-size: 1.5em;
  font-weight: 600;
}

.timer-indicator {
  flex: 1;
  display: flex;
  align-items: center;
  min-width: 200px;
}

.timer-link {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: rgba(255,255,255,0.15);
  border-radius: 6px;
  text-decoration: none;
  color: white;
  font-size: 13px;
  transition: all 0.2s;
  flex: 1;
}

.timer-link:hover {
  background: rgba(255,255,255,0.25);
}

.timer-time {
  font-weight: 700;
  font-family: 'Courier New', monospace;
  min-width: 50px;
}

.timer-task {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-actions {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.user-name {
  font-weight: 500;
}

.user-role {
  font-size: 0.8em;
  opacity: 0.8;
}

.notification-bell,
.profile-link {
  background: none;
  border: none;
  color: white;
  font-size: 20px;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  position: relative;
  text-decoration: none;
}

.avatar-wrapper {
  display: inline-flex;
  align-items: center;
}

.avatar-button {
  background: none;
  border: none;
  padding: 0;
  margin: 0 6px;
  cursor: pointer;
  border-radius: 50%;
  overflow: hidden;
  width: 40px;
  height: 40px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.avatar-img {
  width: 40px;
  height: 40px;
  object-fit: cover;
  border-radius: 50%;
  display: block;
}

.avatar-initials {
  width: 40px;
  height: 40px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(255,255,255,0.15);
  color: white;
  font-weight: 600;
}

.notification-bell:hover,
.notification-bell:focus,
.profile-link:hover,
.profile-link:focus {
  background: rgba(255,255,255,0.1);
  outline: 2px solid white;
}

.badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background: #ff4757;
  color: white;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
}

.logout-button {
  background: none;
  border: none;
  color: white;
  font-size: 20px;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  position: relative;
  text-decoration: none;
  transition: background-color 0.2s;
}

.logout-button:hover,
.logout-button:focus {
  background: rgba(255,255,255,0.1);
  outline: 2px solid white;
}

@media (max-width: 768px) {
  .user-name {
    display: none;
  }
  
  .user-role {
    display: none;
  }
}
</style>