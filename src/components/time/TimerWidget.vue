<template>
  <div>
    <h2>⏰ Active Timer</h2>
    
    <div v-if="!activeTimer" class="no-timer">
      <p>No active timer. Select a task to start tracking time.</p>
      <div class="quick-start">
        <select v-model="selectedTaskId" class="task-select" @change="selectTask">
          <option value="">Select a task to track...</option>
          <option v-for="task in tasks" :key="task.id" :value="task.id">
            {{ task.title }}
          </option>
        </select>
        <button @click="startTracking" class="btn btn-primary" :disabled="!selectedTaskId">
          🚀 Start Tracking
        </button>
      </div>
    </div>

    <div v-else class="timer-display">
      <div class="active-task">
        <span class="task-icon">📌</span>
        <span class="task-name">{{ getTaskName(activeTimer.taskId) }}</span>
      </div>

      <div class="time-counter">
        <div class="time-value">{{ formatTime(elapsedTime) }}</div>
        <div class="time-label">elapsed</div>
      </div>

      <div class="timer-controls">
        <button @click="togglePause" :class="['btn', activeTimer.isPaused ? 'btn-resume' : 'btn-pause']">
          {{ activeTimer.isPaused ? '▶️ Resume' : '⏸️ Pause' }}
        </button>
        <button @click="stopTracking" class="btn btn-danger">⏹️ Stop</button>
      </div>

      <div class="timer-progress">
        <div class="progress-bar" :style="{ width: progressPercent + '%' }"></div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, inject, onMounted, onBeforeUnmount } from 'vue'

export default {
  name: 'TimerWidget',
  setup() {
    const tasks = inject('tasks', [])
    const activeTimer = inject('activeTimer', ref(null))
    const startTimer = inject('startTimer', () => {})
    const pauseTimer = inject('pauseTimer', () => {})
    const stopTimer = inject('stopTimer', () => {})

    const selectedTaskId = ref('')
    const elapsedTime = ref(0)
    let timerInterval = null

    onMounted(() => {
      if (activeTimer.value) {
        startTimerInterval()
      }
    })

    onBeforeUnmount(() => {
      if (timerInterval) {
        clearInterval(timerInterval)
      }
    })

    const selectTask = () => {
      // Task selection handled by v-model
    }

    const startTracking = () => {
      if (selectedTaskId.value) {
        startTimer(selectedTaskId.value)
        startTimerInterval()
      }
    }

    const startTimerInterval = () => {
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
      selectedTaskId.value = ''
      elapsedTime.value = 0
    }

    const formatTime = (seconds) => {
      const hours = Math.floor(seconds / 3600)
      const minutes = Math.floor((seconds % 3600) / 60)
      const secs = seconds % 60

      if (hours > 0) {
        return `${hours}h ${minutes}m ${secs}s`
      } else if (minutes > 0) {
        return `${minutes}m ${secs}s`
      } else {
        return `${secs}s`
      }
    }

    const getTaskName = (taskId) => {
      const task = tasks.find(t => t.id === taskId)
      return task ? task.title : 'Unknown Task'
    }

    const progressPercent = computed(() => {
      if (!activeTimer.value) return 0
      // Estimate based on task duration (in minutes converted to seconds)
      const task = tasks.find(t => t.id === activeTimer.value.taskId)
      if (!task) return 0
      const estimatedSeconds = task.duration * 60
      return Math.min((elapsedTime.value / estimatedSeconds) * 100, 100)
    })

    return {
      tasks,
      activeTimer,
      selectedTaskId,
      elapsedTime,
      formatTime,
      getTaskName,
      selectTask,
      startTracking,
      togglePause,
      stopTracking,
      progressPercent
    }
  }
}
</script>

<style scoped>
h2 {
  margin: 0 0 20px 0;
  color: #202124;
  font-size: 20px;
  font-weight: 600;
}

.no-timer {
  text-align: center;
  padding: 32px 16px;
}

.no-timer p {
  color: #5a5f66;
  margin: 0 0 20px 0;
}

.quick-start {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: center;
}

.task-select {
  flex: 1;
  min-width: 200px;
  padding: 10px 12px;
  border: 1px solid #d0d2d7;
  border-radius: 6px;
  font-size: 14px;
  color: #202124;
}

.task-select:focus {
  outline: none;
  border-color: #1a73e8;
  box-shadow: 0 0 0 3px rgba(26, 115, 232, 0.1);
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-primary {
  background: #1a73e8;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #1557b0;
  box-shadow: 0 2px 8px rgba(26, 115, 232, 0.25);
}

.timer-display {
  text-align: center;
}

.active-task {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 24px;
  padding: 12px;
  background: #e8f0fe;
  border-radius: 8px;
}

.task-icon {
  font-size: 24px;
}

.task-name {
  font-size: 16px;
  font-weight: 600;
  color: #202124;
}

.time-counter {
  margin-bottom: 32px;
}

.time-value {
  font-size: 48px;
  font-weight: 700;
  color: #1a73e8;
  font-family: 'Courier New', monospace;
  letter-spacing: 2px;
}

.time-label {
  font-size: 12px;
  color: #5a5f66;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-top: 8px;
}

.timer-controls {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-bottom: 24px;
}

.btn-pause,
.btn-resume {
  background: #e8c547;
  color: #202124;
}

.btn-pause:hover,
.btn-resume:hover {
  background: #d4b43a;
}

.btn-danger {
  background: #d23636;
  color: white;
}

.btn-danger:hover {
  background: #b92626;
  box-shadow: 0 2px 8px rgba(210, 54, 54, 0.25);
}

.timer-progress {
  height: 8px;
  background: #d0d2d7;
  border-radius: 4px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #1a73e8, #0d47a1);
  transition: width 0.3s ease;
}
</style>
