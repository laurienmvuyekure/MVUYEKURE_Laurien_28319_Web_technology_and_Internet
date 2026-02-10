<template>
  <div class="time-manager">
    <div class="time-manager-header">
      <h1>⏱️ Time Manager</h1>
      <p class="subtitle">Track your focus, optimize your productivity</p>
    </div>

    <div class="tm-grid">
      <!-- Active Timer Widget -->
      <div class="tm-section timer-section">
        <TimerWidget />
      </div>

      <!-- Pomodoro Timer -->
      <div class="tm-section pomodoro-section">
        <PomodoroTimer />
      </div>

      <!-- Time Analytics -->
      <div class="tm-section analytics-section">
        <TimeAnalytics />
      </div>

      <!-- Time History -->
      <div class="tm-section history-section">
        <TimeHistory />
      </div>
    </div>
  </div>
</template>

<script>
import { inject } from 'vue'
import TimerWidget from '../components/time/TimerWidget.vue'
import PomodoroTimer from '../components/time/PomodoroTimer.vue'
import TimeAnalytics from '../components/time/TimeAnalytics.vue'
import TimeHistory from '../components/time/TimeHistory.vue'

export default {
  name: 'TimeManager',
  components: {
    TimerWidget,
    PomodoroTimer,
    TimeAnalytics,
    TimeHistory
  },
  setup() {
    const tasks = inject('tasks', [])
    const timeLog = inject('timeLog', [])

    return {
      tasks,
      timeLog
    }
  }
}
</script>

<style scoped>
.time-manager {
  padding: 24px;
  background: linear-gradient(135deg, #fafbfc 0%, #f3f4f6 100%);
  min-height: 100vh;
}

.time-manager-header {
  margin-bottom: 32px;
}

.time-manager-header h1 {
  margin: 0 0 8px 0;
  color: #202124;
  font-size: 32px;
  font-weight: 600;
}

.subtitle {
  color: #5a5f66;
  font-size: 16px;
  margin: 0;
}

.tm-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 24px;
}

.tm-section {
  background: white;
  border: 1px solid #d0d2d7;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.tm-section h2 {
  margin: 0 0 20px 0;
  color: #202124;
  font-size: 20px;
  font-weight: 600;
}

.timer-section {
  grid-column: 1 / -1;
}

.analytics-section {
  grid-column: 1 / -1;
}

.history-section {
  grid-column: 1 / -1;
}

@media (max-width: 768px) {
  .time-manager {
    padding: 16px;
  }

  .tm-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .tm-section {
    padding: 16px;
  }

  .time-manager-header h1 {
    font-size: 24px;
  }
}
</style>
