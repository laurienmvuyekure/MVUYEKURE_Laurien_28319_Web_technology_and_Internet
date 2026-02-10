<template>
  <div class="task-reminders">
    <h2>🔔 Task Reminders</h2>
    <p class="description">Manage reminders and notifications for your tasks</p>

    <div class="reminders-grid">
      <div class="reminder-card upcoming">
        <h3>📌 Upcoming Reminders</h3>
        <div v-if="upcomingReminders.length === 0" class="no-items">
          <p>No upcoming reminders</p>
        </div>
        <div v-else class="reminders-list">
          <div 
            v-for="reminder in upcomingReminders"
            :key="reminder.id"
            class="reminder-item"
            :class="{ urgent: isUrgent(reminder) }"
          >
            <div class="reminder-title">{{ reminder.title }}</div>
            <div class="reminder-meta">
              <span class="reminder-time">{{ formatReminder(reminder.dueDate) }}</span>
              <span class="reminder-type">{{ reminder.type }}</span>
            </div>
            <button 
              @click="dismissReminder(reminder.id)"
              class="btn-dismiss"
              title="Dismiss reminder"
            >
              ✓
            </button>
          </div>
        </div>
      </div>

      <div class="reminder-card completed">
        <h3>✅ Completed Reminders</h3>
        <div v-if="completedReminders.length === 0" class="no-items">
          <p>No completed reminders</p>
        </div>
        <div v-else class="reminders-list">
          <div 
            v-for="reminder in completedReminders"
            :key="reminder.id"
            class="reminder-item completed-item"
          >
            <div class="reminder-title">{{ reminder.title }}</div>
            <div class="reminder-completed-at">Completed: {{ formatDate(reminder.completedAt) }}</div>
            <button 
              @click="deleteReminder(reminder.id)"
              class="btn-delete"
              title="Delete reminder"
            >
              🗑️
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="reminders-settings">
      <h3>⚙️ Reminder Settings</h3>
      <div class="setting-group">
        <label>
          <input 
            v-model="enableNotifications" 
            type="checkbox"
            class="checkbox-input"
          />
          <span>Enable browser notifications</span>
        </label>
      </div>
      <div class="setting-group">
        <label>
          <input 
            v-model="enableSoundAlerts"
            type="checkbox"
            class="checkbox-input"
          />
          <span>Enable sound alerts</span>
        </label>
      </div>
      <div class="setting-group">
        <label>Default reminder time before task:</label>
        <select v-model.number="defaultReminderMinutes" class="select-input">
          <option :value="15">15 minutes</option>
          <option :value="30">30 minutes</option>
          <option :value="60">1 hour</option>
          <option :value="1440">1 day</option>
        </select>
      </div>
    </div>

    <div class="reminders-stats">
      <div class="stat">
        <span class="stat-number">{{ totalReminders }}</span>
        <span class="stat-label">Total Reminders</span>
      </div>
      <div class="stat">
        <span class="stat-number">{{ upcomingReminders.length }}</span>
        <span class="stat-label">Upcoming</span>
      </div>
      <div class="stat">
        <span class="stat-number">{{ completedReminders.length }}</span>
        <span class="stat-label">Completed</span>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'

export default {
  name: 'TaskReminders',
  setup() {
    const reminders = ref([])
    const enableNotifications = ref(true)
    const enableSoundAlerts = ref(false)
    const defaultReminderMinutes = ref(30)

    const loadReminders = () => {
      const saved = localStorage.getItem('reminders')
      if (saved) {
        try {
          reminders.value = JSON.parse(saved)
        } catch (e) {
          console.error('Failed to load reminders:', e)
        }
      }

      const settings = localStorage.getItem('reminder-settings')
      if (settings) {
        try {
          const parsed = JSON.parse(settings)
          enableNotifications.value = parsed.enableNotifications ?? true
          enableSoundAlerts.value = parsed.enableSoundAlerts ?? false
          defaultReminderMinutes.value = parsed.defaultReminderMinutes ?? 30
        } catch (e) {
          console.error('Failed to load reminder settings:', e)
        }
      }
    }

    const saveReminders = () => {
      localStorage.setItem('reminders', JSON.stringify(reminders.value))
    }

    const saveSettings = () => {
      localStorage.setItem('reminder-settings', JSON.stringify({
        enableNotifications: enableNotifications.value,
        enableSoundAlerts: enableSoundAlerts.value,
        defaultReminderMinutes: defaultReminderMinutes.value
      }))
    }

    const upcomingReminders = computed(() => {
      return reminders.value
        .filter(r => !r.completed)
        .sort((a, b) => new Date(a.dueDate) - new Date(b.dueDate))
    })

    const completedReminders = computed(() => {
      return reminders.value.filter(r => r.completed)
    })

    const totalReminders = computed(() => reminders.value.length)

    const isUrgent = (reminder) => {
      const now = new Date()
      const dueDate = new Date(reminder.dueDate)
      const minutesUntil = (dueDate - now) / (1000 * 60)
      return minutesUntil <= 60 && minutesUntil > 0
    }

    const formatReminder = (dateStr) => {
      const date = new Date(dateStr)
      const now = new Date()
      const diffMs = date - now

      if (diffMs < 0) {
        return '⏰ Overdue!'
      }

      const hours = Math.floor(diffMs / (1000 * 60 * 60))
      const minutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60))

      if (hours > 24) {
        return date.toLocaleDateString('en-GB', { month: 'short', day: 'numeric' })
      } else if (hours > 0) {
        return `${hours}h ${minutes}m`
      } else {
        return `${minutes}m`
      }
    }

    const formatDate = (dateStr) => {
      const date = new Date(dateStr)
      return date.toLocaleDateString('en-GB', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
    }

    const dismissReminder = (id) => {
      const reminder = reminders.value.find(r => r.id === id)
      if (reminder) {
        reminder.completed = true
        reminder.completedAt = new Date().toISOString()
        saveReminders()

        if (enableNotifications.value) {
          showNotification(reminder.title, 'Task reminder completed')
        }
      }
    }

    const deleteReminder = (id) => {
      const index = reminders.value.findIndex(r => r.id === id)
      if (index > -1) {
        reminders.value.splice(index, 1)
        saveReminders()
      }
    }

    const showNotification = (title, message) => {
      if ('Notification' in window && Notification.permission === 'granted') {
        new Notification(title, {
          body: message,
          icon: '🔔'
        })
      }
    }

    // Request notification permission
    onMounted(() => {
      loadReminders()

      if (enableNotifications.value && 'Notification' in window && Notification.permission === 'default') {
        Notification.requestPermission()
      }
    })

    // Save settings when they change
    const watchSettings = () => {
      saveSettings()
    }

    return {
      reminders,
      enableNotifications,
      enableSoundAlerts,
      defaultReminderMinutes,
      upcomingReminders,
      completedReminders,
      totalReminders,
      isUrgent,
      formatReminder,
      formatDate,
      dismissReminder,
      deleteReminder,
      watchSettings
    }
  }
}
</script>

<style scoped>
.task-reminders {
  background: white;
  padding: 24px;
  border-radius: 12px;
  max-width: 1200px;
  margin: 0 auto;
}

.task-reminders h2 {
  color: #1a73e8;
  font-size: 28px;
  margin-bottom: 8px;
}

.description {
  color: #5f6368;
  margin-bottom: 24px;
}

.reminders-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.reminder-card {
  background: #f5f7fa;
  border: 1px solid #e8eaed;
  border-radius: 8px;
  padding: 16px;
  border-left: 4px solid #1a73e8;
}

.reminder-card.completed {
  border-left-color: #34a853;
}

.reminder-card h3 {
  margin-top: 0;
  color: #202124;
  font-size: 16px;
}

.no-items {
  text-align: center;
  padding: 20px;
  color: #5f6368;
  font-size: 14px;
}

.reminders-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.reminder-item {
  background: white;
  padding: 12px;
  border-radius: 6px;
  border: 1px solid #e8eaed;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: all 0.2s;
}

.reminder-item:hover {
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  border-color: #1a73e8;
}

.reminder-item.urgent {
  border-color: #d32f2f;
  background: #ffebee;
}

.reminder-title {
  flex: 1;
  font-weight: 600;
  color: #202124;
  font-size: 13px;
}

.reminder-meta {
  display: flex;
  gap: 8px;
  margin-right: 8px;
}

.reminder-time {
  background: #e3f2fd;
  color: #1a73e8;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
  white-space: nowrap;
}

.reminder-type {
  background: #e8f5e9;
  color: #34a853;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  white-space: nowrap;
}

.reminder-item.completed-item {
  opacity: 0.7;
}

.reminder-completed-at {
  font-size: 11px;
  color: #5f6368;
}

.btn-dismiss,
.btn-delete {
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
  padding: 2px;
  transition: transform 0.2s;
}

.btn-dismiss:hover,
.btn-delete:hover {
  transform: scale(1.3);
}

.reminders-settings {
  background: #f5f7fa;
  border: 1px solid #e8eaed;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 24px;
}

.reminders-settings h3 {
  margin-top: 0;
  margin-bottom: 16px;
  color: #202124;
  font-size: 14px;
}

.setting-group {
  margin-bottom: 12px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.setting-group label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #202124;
  cursor: pointer;
}

.checkbox-input {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.select-input {
  padding: 6px 8px;
  border: 1px solid #e8eaed;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
}

.select-input:focus {
  outline: none;
  border-color: #1a73e8;
}

.reminders-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 12px;
}

.stat {
  text-align: center;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 6px;
}

.stat-number {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #1a73e8;
}

.stat-label {
  display: block;
  font-size: 11px;
  color: #5f6368;
  text-transform: uppercase;
  margin-top: 4px;
}

@media (max-width: 768px) {
  .task-reminders {
    padding: 16px;
  }

  .reminders-grid {
    grid-template-columns: 1fr;
  }

  .reminder-item {
    flex-wrap: wrap;
  }

  .reminder-meta {
    width: 100%;
    margin-right: 0;
  }
}
</style>
