<template>
  <div>
    <h2>🍅 Pomodoro Timer</h2>

    <div class="pomodoro-display">
      <div class="mode-indicator" :class="{ 'break-mode': isBreak }">
        {{ isBreak ? '☕ Break Time' : '💪 Focus Time' }}
      </div>

      <div class="pomodoro-timer">
        <div class="time-value">{{ formatTime(timeRemaining) }}</div>
        <div class="time-label">{{ isBreak ? 'break' : 'focus' }}</div>
      </div>

      <div class="pomodoro-controls">
        <button 
          @click="toggleTimer" 
          :class="['btn', isRunning ? 'btn-pause' : 'btn-primary']"
        >
          {{ isRunning ? '⏸️ Pause' : '▶️ Start' }}
        </button>
        <button @click="resetTimer" class="btn btn-secondary">🔄 Reset</button>
        <button @click="skipSession" class="btn btn-secondary">⏭️ Skip</button>
      </div>

      <div class="session-info">
        <div class="stat">
          <span class="stat-label">Sessions</span>
          <span class="stat-value">{{ pomodoroStats.sessionsCompleted }}</span>
        </div>
        <div class="stat">
          <span class="stat-label">Breaks</span>
          <span class="stat-value">{{ pomodoroStats.breaksCompleted }}</span>
        </div>
        <div class="stat">
          <span class="stat-label">Focus Time</span>
          <span class="stat-value">{{ pomodoroStats.totalFocusTime }}m</span>
        </div>
      </div>

      <div class="progress-indicator">
        <div class="progress-ring">
          <svg width="120" height="120" viewBox="0 0 120 120">
            <circle cx="60" cy="60" r="54" class="progress-bg"/>
            <circle 
              cx="60" 
              cy="60" 
              r="54" 
              class="progress-fill" 
              :style="{ 'stroke-dashoffset': strokeDashOffset }"
            />
          </svg>
          <div class="ring-text">{{ Math.floor(progressPercent) }}%</div>
        </div>
      </div>

      <div class="settings">
        <label>
          <span>Focus Duration (min):</span>
          <input v-model.number="focusDuration" type="number" min="1" max="60" @change="resetTimer">
        </label>
        <label>
          <span>Break Duration (min):</span>
          <input v-model.number="breakDuration" type="number" min="1" max="30" @change="resetTimer">
        </label>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, inject, onBeforeUnmount } from 'vue'

export default {
  name: 'PomodoroTimer',
  setup() {
    const pomodoroStats = inject('pomodoroStats', ref({}))
    const completePomodoroSession = inject('completePomodoroSession', () => {})
    const completePomodoroBreak = inject('completePomodoroBreak', () => {})

    const focusDuration = ref(25)
    const breakDuration = ref(5)
    const isBreak = ref(false)
    const isRunning = ref(false)
    const timeRemaining = ref(25 * 60) // in seconds
    let timerInterval = null

    const setInitialTime = () => {
      timeRemaining.value = (isBreak.value ? breakDuration.value : focusDuration.value) * 60
    }

    const toggleTimer = () => {
      isRunning.value = !isRunning.value
      
      if (isRunning.value) {
        timerInterval = setInterval(() => {
          timeRemaining.value--
          
          if (timeRemaining.value <= 0) {
            completeSession()
          }
        }, 1000)
      } else {
        clearInterval(timerInterval)
      }
    }

    const resetTimer = () => {
      isRunning.value = false
      clearInterval(timerInterval)
      setInitialTime()
    }

    const skipSession = () => {
      completeSession()
    }

    const completeSession = () => {
      isRunning.value = false
      clearInterval(timerInterval)

      if (isBreak.value) {
        completePomodoroBreak()
        isBreak.value = false
      } else {
        completePomodoroSession(focusDuration.value)
        isBreak.value = true
      }

      setInitialTime()
      
      // Auto play notification sound
      playNotificationSound()
      
      // Auto start next session
      setTimeout(() => {
        isRunning.value = true
        toggleTimer()
      }, 2000)
    }

    const playNotificationSound = () => {
      const audioContext = new (window.AudioContext || window.webkitAudioContext)()
      const oscillator = audioContext.createOscillator()
      const gainNode = audioContext.createGain()
      
      oscillator.connect(gainNode)
      gainNode.connect(audioContext.destination)
      
      oscillator.frequency.value = 800
      oscillator.type = 'sine'
      
      gainNode.gain.setValueAtTime(0.3, audioContext.currentTime)
      gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.5)
      
      oscillator.start(audioContext.currentTime)
      oscillator.stop(audioContext.currentTime + 0.5)
    }

    const formatTime = (seconds) => {
      const mins = Math.floor(seconds / 60)
      const secs = seconds % 60
      return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`
    }

    const progressPercent = computed(() => {
      const totalSeconds = (isBreak.value ? breakDuration.value : focusDuration.value) * 60
      return ((totalSeconds - timeRemaining.value) / totalSeconds) * 100
    })

    const strokeDashOffset = computed(() => {
      const circumference = 2 * Math.PI * 54
      return circumference - (progressPercent.value / 100) * circumference
    })

    onBeforeUnmount(() => {
      if (timerInterval) clearInterval(timerInterval)
    })

    return {
      focusDuration,
      breakDuration,
      isBreak,
      isRunning,
      timeRemaining,
      pomodoroStats,
      formatTime,
      toggleTimer,
      resetTimer,
      skipSession,
      progressPercent,
      strokeDashOffset
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

.pomodoro-display {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.mode-indicator {
  padding: 12px 16px;
  background: #e8f0fe;
  border-left: 4px solid #1a73e8;
  border-radius: 6px;
  color: #1a73e8;
  font-weight: 600;
  text-align: center;
}

.mode-indicator.break-mode {
  background: #e6f5ea;
  border-left-color: #188038;
  color: #188038;
}

.pomodoro-timer {
  text-align: center;
  padding: 32px 16px;
  background: linear-gradient(135deg, #f5f6f7 0%, #eeeff2 100%);
  border-radius: 8px;
}

.time-value {
  font-size: 56px;
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

.pomodoro-controls {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
}

.btn {
  padding: 10px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-primary {
  background: #1a73e8;
  color: white;
}

.btn-primary:hover {
  background: #1557b0;
  box-shadow: 0 2px 8px rgba(26, 115, 232, 0.25);
}

.btn-pause {
  background: #e8c547;
  color: #202124;
}

.btn-pause:hover {
  background: #d4b43a;
}

.btn-secondary {
  background: #f5f6f7;
  color: #202124;
  border: 1px solid #d0d2d7;
}

.btn-secondary:hover {
  background: #eaeced;
}

.session-info {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px;
  background: #f5f6f7;
  border-radius: 6px;
}

.stat-label {
  font-size: 12px;
  color: #5a5f66;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1a73e8;
  margin-top: 4px;
}

.progress-indicator {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.progress-ring {
  position: relative;
  width: 120px;
  height: 120px;
}

.progress-ring svg {
  transform: rotate(-90deg);
}

.progress-bg {
  fill: none;
  stroke: #d0d2d7;
  stroke-width: 4;
}

.progress-fill {
  fill: none;
  stroke: #1a73e8;
  stroke-width: 4;
  stroke-dasharray: 340;
  transition: stroke-dashoffset 0.3s ease;
}

.ring-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 18px;
  font-weight: 700;
  color: #202124;
}

.settings {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: #f5f6f7;
  border-radius: 6px;
}

.settings label {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.settings span {
  font-size: 12px;
  font-weight: 600;
  color: #202124;
}

.settings input {
  padding: 8px 12px;
  border: 1px solid #d0d2d7;
  border-radius: 4px;
  font-size: 14px;
  color: #202124;
}

.settings input:focus {
  outline: none;
  border-color: #1a73e8;
  box-shadow: 0 0 0 3px rgba(26, 115, 232, 0.1);
}

@media (max-width: 768px) {
  .time-value {
    font-size: 40px;
  }

  .session-info {
    grid-template-columns: 1fr;
  }

  .pomodoro-controls {
    flex-direction: column;
  }

  .btn {
    width: 100%;
  }

  .settings {
    flex-direction: column;
  }
}
</style>
