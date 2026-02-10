<template>
  <div class="dashboard-timer">
    <div v-if="!activeTimer" class="timer-empty">
      <span class="timer-icon">⏰</span>
      <p>No active timer</p>
      <router-link to="/time-manager" class="link">Start tracking →</router-link>
    </div>

    <div v-else class="timer-active">
      <div class="timer-header">
        <span class="timer-label">⏱️ Active Timer</span>
        <span class="timer-badge">Running</span>
      </div>
      
      <div class="timer-task">
        <span class="task-title">{{ getTaskName(activeTimer.taskId) }}</span>
      </div>
      
      <div class="timer-display">{{ formatTime(elapsedTime) }}</div>
      
      <div class="timer-buttons">
        <button @click="togglePause" class="btn-mini" :title="activeTimer.isPaused ? 'Resume' : 'Pause'">
          {{ activeTimer.isPaused ? '▶️' : '⏸️' }}
        </button>
        <button @click="stopTracking" class="btn-mini btn-stop" title="Stop">⏹️</button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, inject, onMounted, onBeforeUnmount } from 'vue'

export default {
  name: 'DashboardTimer',
  setup() {
    const tasks = inject('tasks', [])
    const activeTimer = inject('activeTimer', ref(null))
    const pauseTimer = inject('pauseTimer', () => {})
    const stopTimer = inject('stopTimer', () => {})

    const elapsedTime = ref(0)
    let timerInterval = null

    onMounted(() => {
      if (activeTimer.value) {
        startInterval()
      }
    })

    onBeforeUnmount(() => {
      if (timerInterval) clearInterval(timerInterval)
    })

    const startInterval = () => {
      if (timerInterval) clearInterval(timerInterval)
      timerInterval = setInterval(() => {
        if (activeTimer.value && !activeTimer.value.isPaused) {
          elapsedTime.value = Math.floor((Date.now() - activeTimer.value.startTime) / 1000)
        }
      }, 100)
    }

    const togglePause = () => {
      pauseTimer()
    }

    const stopTracking = () => {
      stopTimer()
      elapsedTime.value = 0
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
      const task = tasks.find(t => t.id === taskId)
      return task ? task.title : 'Unknown'
    }

    return {
      activeTimer,
      elapsedTime,
      formatTime,
      getTaskName,
      togglePause,
      stopTracking
    }
  }
}
</script>

<style scoped>
.dashboard-timer {
  background: linear-gradient(135deg, #e8f0fe 0%, #f0f4ff 100%);
  border: 1px solid #1a73e8;
  border-radius: 8px;
  padding: 16px;
  min-width: 250px;
}

.timer-empty {
  text-align: center;
  padding: 20px 16px;
}

.timer-icon {
  font-size: 32px;
  display: block;
  margin-bottom: 8px;
}

.timer-empty p {
  color: #1a73e8;
  font-size: 14px;
  margin: 0 0 12px 0;
  font-weight: 500;
}

.link {
  color: #1a73e8;
  text-decoration: none;
  font-size: 13px;
  font-weight: 500;
}

.link:hover {
  text-decoration: underline;
}

.timer-active {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.timer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.timer-label {
  font-weight: 600;
  color: #1a73e8;
  font-size: 13px;
}

.timer-badge {
  display: inline-block;
  background: #188038;
  color: white;
  padding: 2px 8px;
  border-radius: 3px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
}

.timer-task {
  background: white;
  padding: 10px 12px;
  border-radius: 6px;
  border-left: 3px solid #1a73e8;
}

.task-title {
  font-size: 13px;
  font-weight: 600;
  color: #202124;
  word-break: break-word;
}

.timer-display {
  text-align: center;
  font-size: 28px;
  font-weight: 700;
  color: #1a73e8;
  font-family: 'Courier New', monospace;
  padding: 8px;
}

.timer-buttons {
  display: flex;
  gap: 8px;
}

.btn-mini {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #1a73e8;
  background: white;
  color: #1a73e8;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.2s;
}

.btn-mini:hover {
  background: #1a73e8;
  color: white;
}

.btn-mini.btn-stop {
  border-color: #d23636;
  color: #d23636;
}

.btn-mini.btn-stop:hover {
  background: #d23636;
  color: white;
}

@media (max-width: 768px) {
  .dashboard-timer {
    min-width: auto;
  }
}
</style>
