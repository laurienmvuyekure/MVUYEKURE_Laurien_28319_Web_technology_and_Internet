<template>
  <div id="app" :class="appClasses">
    <!-- Accessibility live region -->
    <div 
      id="a11y-live-region" 
      class="sr-only" 
      aria-live="polite" 
      aria-atomic="true"
    >
      {{ liveMessage }}
    </div>
    
    <!-- Skip navigation -->
    <a href="#main-content" class="skip-link" @click.prevent="skipToContent">
      Skip to main content
    </a>
    
    <!-- Header -->
    <layout-header 
      v-if="showHeader"
      :user="currentUser"
      @toggle-sidebar="toggleSidebar"
      @update-avatar="handleAvatarUpdate"
      @logout="handleLogout"
    />
    
    <div class="app-container">
      <!-- Sidebar -->
      <layout-sidebar 
        v-if="showSidebar && showHeader"
        :categories="categories"
        @select-category="setActiveCategory"
      />
            <!-- Main content -->
      <main id="main-content" class="main-content" tabindex="-1">
        <router-view />
      </main>
    </div>
    
    <!-- Notifications -->
    <div class="notifications-container">
      <transition-group name="notification" tag="div">
        <helpers-notification
          v-for="notification in notifications"
          :key="notification.id"
          :notification="notification"
          @dismiss="removeNotification"
        />
      </transition-group>
    </div>
    
    <!-- Accessibility Controls -->
    <helpers-accessibility-controls v-if="showHeader" />
  </div>
</template>

<script>
import { ref, computed, onMounted, provide } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import LayoutHeader from './components/layout/Header.vue'
import LayoutSidebar from './components/layout/Sidebar.vue'
import HelpersAccessibilityControls from './components/helpers/AccessibilityControls.vue'
import HelpersNotification from './components/helpers/Notification.vue'

export default {
  name: 'App',
  components: {
    LayoutHeader,
    LayoutSidebar,
    HelpersAccessibilityControls,
    HelpersNotification
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const liveMessage = ref('')
    const showSidebar = ref(true)
    const notifications = ref([])
    const categories = ref(['All', 'Work', 'Personal', 'Study', 'Health'])
    const currentUser = ref(null)
    const tasks = ref([])
    const selectedCategory = ref('All')
    
    // Time manager state
    const activeTimer = ref(null) // { taskId, startTime, elapsedSeconds, isPaused }
    const timeLog = ref([]) // Array of { taskId, date, duration, type (work/break) }
    const pomodoroStats = ref({
      sessionsCompleted: 0,
      breaksCompleted: 0,
      totalFocusTime: 0
    })
    
    const showHeader = computed(() => {
      return !['/login', '/register'].includes(route.path)
    })
    
    const appClasses = computed(() => {
      return {
        'reduced-motion': localStorage.getItem('reducedMotion') === 'true',
        'high-contrast': localStorage.getItem('highContrast') === 'true'
      }
    })
    
    const skipToContent = () => {
      const mainContent = document.getElementById('main-content')
      if (mainContent) {
        mainContent.focus()
        mainContent.scrollIntoView({ behavior: 'smooth' })
      }
    }
    
    const toggleSidebar = () => {
      showSidebar.value = !showSidebar.value
      announce(showSidebar.value ? 'Sidebar expanded' : 'Sidebar collapsed')
    }
    
    const setActiveCategory = (category) => {
      selectedCategory.value = category
      announce(`Filtering tasks by ${category} category`)
    }
    
    const announce = (message) => {
      liveMessage.value = message
      setTimeout(() => {
        liveMessage.value = ''
      }, 1000)
    }
    
    const addNotification = (type, message) => {
      const id = Date.now()
      notifications.value.push({
        id,
        type,
        message,
        timestamp: new Date()
      })
      
      // Auto-remove after 5 seconds
      setTimeout(() => {
        removeNotification(id)
      }, 5000)
    }
    
    const removeNotification = (id) => {
      notifications.value = notifications.value.filter(n => n.id !== id)
    }
    
    // Simulate user login
    onMounted(() => {
      currentUser.value = {
        name: 'Laurien MVUYEKURE',
        email: 'laurien@mvuyekure.com',
        role: 'Admin'
      }
      // load persisted avatar if available
      const saved = localStorage.getItem('userAvatar')
      if (saved) {
        currentUser.value.avatar = saved
      }
      
      // Load persisted tasks from localStorage
      const savedTasks = localStorage.getItem('tasks')
      if (savedTasks) {
        try {
          tasks.value = JSON.parse(savedTasks)
        } catch (e) {
          console.error('Failed to parse saved tasks:', e)
          tasks.value = []
        }
      } else {
        // Initialize with sample tasks if none exist
        const tomorrow = new Date()
        tomorrow.setDate(tomorrow.getDate() + 1)
        const tomorrowStr = tomorrow.toISOString().split('T')[0]
        
        const sampleTasks = [
          {
            id: 1707292800000,
            title: 'Complete Project Report',
            description: 'Finish the quarterly report and submit to management',
            category: 'Work',
            priority: 'high',
            date: tomorrowStr,
            time: '14:00',
            duration: 45,
            completed: false,
            createdAt: new Date().toISOString()
          },
          {
            id: 1707292800001,
            title: 'Morning Exercise',
            description: 'Run for 30 minutes at the park',
            category: 'Health',
            priority: 'medium',
            date: new Date().toISOString().split('T')[0],
            time: '06:00',
            duration: 30,
            completed: false,
            createdAt: new Date().toISOString()
          },
          {
            id: 1707292800002,
            title: 'Review Vue Documentation',
            description: 'Study Vue 3 composition API and best practices',
            category: 'Study',
            priority: 'medium',
            date: new Date().toISOString().split('T')[0],
            time: '16:00',
            duration: 60,
            completed: false,
            createdAt: new Date().toISOString()
          }
        ]
        tasks.value = sampleTasks
        localStorage.setItem('tasks', JSON.stringify(sampleTasks))
      }
      
      // Load time tracking data from localStorage
      const savedTimeLog = localStorage.getItem('timeLog')
      if (savedTimeLog) {
        try {
          timeLog.value = JSON.parse(savedTimeLog)
        } catch (e) {
          // Failed to load time log
          timeLog.value = []
        }
      }
      
      const savedPomodoroStats = localStorage.getItem('pomodoroStats')
      if (savedPomodoroStats) {
        try {
          pomodoroStats.value = JSON.parse(savedPomodoroStats)
        } catch (e) {
          console.error('Failed to load pomodoro stats:', e)
        }
      }
      
      // Listen for custom events from child components
      const handleTaskCreated = (e) => {
        addTask(e.detail)
        addNotification('success', `✅ Task "${e.detail.title}" created successfully!`)
        announce(`Task ${e.detail.title} has been created`)
      }
      
      const handleTaskUpdated = (e) => {
        editTask(e.detail.id, e.detail)
        addNotification('info', `ℹ️ Task "${e.detail.title}" updated`)
        announce(`Task ${e.detail.title} has been updated`)
      }
      
      const handleTaskDeleted = (e) => {
        deleteTask(e.detail.id)
        addNotification('warning', `⚠️ Task "${e.detail.title}" deleted`)
        announce(`Task ${e.detail.title} has been deleted`)
      }
      
      window.addEventListener('task-created', handleTaskCreated)
      window.addEventListener('task-updated', handleTaskUpdated)
      window.addEventListener('task-deleted', handleTaskDeleted)
    })
    
    const saveTasks = () => {
      localStorage.setItem('tasks', JSON.stringify(tasks.value))
    }
    
    const addTask = (taskData) => {
      const newTask = {
        id: Date.now(),
        title: taskData.title,
        description: taskData.description,
        category: taskData.category,
        priority: taskData.priority,
        date: taskData.date,
        time: taskData.time,
        duration: taskData.duration,
        completed: false,
        createdAt: new Date().toISOString()
      }
      tasks.value.push(newTask)
      saveTasks()
    }
    
    const toggleTask = (id) => {
      const task = tasks.value.find(t => t.id === id)
      if (task) {
        task.completed = !task.completed
        saveTasks()
      }
    }
    
    const editTask = (id, updates) => {
      const task = tasks.value.find(t => t.id === id)
      if (task) {
        Object.assign(task, updates)
        saveTasks()
      }
    }
    
    const deleteTask = (id) => {
      const index = tasks.value.findIndex(t => t.id === id)
      if (index > -1) {
        tasks.value.splice(index, 1)
        saveTasks()
      }
    }
    
    // Time tracking methods
    const startTimer = (taskId) => {
      activeTimer.value = {
        taskId,
        startTime: Date.now(),
        elapsedSeconds: 0,
        isPaused: false
      }
      announce(`Timer started for task`)
    }
    
    const pauseTimer = () => {
      if (activeTimer.value) {
        activeTimer.value.isPaused = !activeTimer.value.isPaused
        announce(activeTimer.value.isPaused ? 'Timer paused' : 'Timer resumed')
      }
    }
    
    const stopTimer = () => {
      if (activeTimer.value) {
        const duration = Math.floor((Date.now() - activeTimer.value.startTime + 
                        activeTimer.value.elapsedSeconds * 1000) / 1000 / 60) // in minutes
        
        timeLog.value.push({
          taskId: activeTimer.value.taskId,
          date: new Date().toISOString().split('T')[0],
          duration,
          type: 'work',
          startTime: activeTimer.value.startTime,
          endTime: Date.now()
        })
        
        saveTimeLog()
        activeTimer.value = null
        announce(`Task time logged: ${duration} minutes`)
      }
    }
    
    const logBreak = (duration) => {
      timeLog.value.push({
        date: new Date().toISOString().split('T')[0],
        duration,
        type: 'break',
        timestamp: Date.now()
      })
      saveTimeLog()
    }
    
    const completePomodoroSession = (focusMinutes) => {
      pomodoroStats.value.sessionsCompleted++
      pomodoroStats.value.totalFocusTime += focusMinutes
      savePomodoroStats()
      announce(`Pomodoro session completed! Total focus time: ${pomodoroStats.value.totalFocusTime} minutes`)
    }
    
    const completePomodoroBreak = () => {
      pomodoroStats.value.breaksCompleted++
      savePomodoroStats()
    }
    
    const saveTimeLog = () => {
      localStorage.setItem('timeLog', JSON.stringify(timeLog.value))
    }
    
    const savePomodoroStats = () => {
      localStorage.setItem('pomodoroStats', JSON.stringify(pomodoroStats.value))
    }
    
    // Provide tasks and methods to child components via injection
    provide('tasks', tasks)
    provide('selectedCategory', selectedCategory)
    provide('toggleTask', toggleTask)
    provide('editTask', editTask)
    provide('deleteTask', deleteTask)
    
    // Provide time tracking methods
    provide('activeTimer', activeTimer)
    provide('timeLog', timeLog)
    provide('pomodoroStats', pomodoroStats)
    provide('startTimer', startTimer)
    provide('pauseTimer', pauseTimer)
    provide('stopTimer', stopTimer)
    provide('logBreak', logBreak)
    provide('completePomodoroSession', completePomodoroSession)
    provide('completePomodoroBreak', completePomodoroBreak)
    
    const handleAvatarUpdate = (dataUrl) => {
      if (!currentUser.value) return
      if (dataUrl) {
        currentUser.value.avatar = dataUrl
        localStorage.setItem('userAvatar', dataUrl)
      } else {
        delete currentUser.value.avatar
        localStorage.removeItem('userAvatar')
      }
    }

    const handleLogout = async () => {
      currentUser.value = null
      localStorage.removeItem('userAvatar')
      await router.push('/login')
    }
    
    return {
      liveMessage,
      showSidebar,
      notifications,
      categories,
      currentUser,
      showHeader,
      appClasses,
      skipToContent,
      toggleSidebar,
      setActiveCategory,
      removeNotification,
      handleAvatarUpdate,
      handleLogout,
      tasks,
      selectedCategory,
      addTask,
      toggleTask,
      editTask,
      deleteTask,
      activeTimer,
      timeLog,
      pomodoroStats,
      startTimer,
      pauseTimer,
      stopTimer,
      logBreak,
      completePomodoroSession,
      completePomodoroBreak
    }
  }
}
</script>

<style scoped>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.skip-link {
  position: absolute;
  top: -40px;
  left: 10px;
  background: #0066cc;
  color: white;
  padding: 8px 16px;
  text-decoration: none;
  border-radius: 4px;
  z-index: 9999;
}

.skip-link:focus {
  top: 10px;
}

.app-container {
  display: flex;
  flex: 1;
}

.main-content {
  flex: 1;
  padding: 20px;
  min-height: calc(100vh - 60px);
}

.notification-enter-active,
.notification-leave-active {
  transition: all 0.3s ease;
}

.notification-enter-from,
.notification-leave-to {
  opacity: 0;
  transform: translateX(100%);
}

.notifications-container {
  position: fixed;
  top: 80px;
  right: 20px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 12px;
  pointer-events: none;
}

.notifications-container > * {
  pointer-events: auto;
}

.reduced-motion .notification-enter-active,
.reduced-motion .notification-leave-active {
  transition: none;
}

@media (max-width: 768px) {
  .app-container {
    flex-direction: column;
  }
  
  .main-content {
    padding: 15px;
  }
}
</style>