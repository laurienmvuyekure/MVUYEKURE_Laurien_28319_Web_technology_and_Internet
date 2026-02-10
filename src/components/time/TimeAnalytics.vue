<template>
  <div>
    <h2>📊 Time Analytics</h2>

    <div class="analytics-grid">
      <!-- Daily Summary -->
      <div class="analytics-card">
        <div class="card-header">
          <h3>Today's Summary</h3>
          <input v-model="selectedDate" type="date" class="date-input">
        </div>
        
        <div class="summary-stats">
          <div class="summary-item">
            <span class="label">Focus Time</span>
            <span class="value">{{ getTodayFocusTime() }}m</span>
          </div>
          <div class="summary-item">
            <span class="label">Break Time</span>
            <span class="value">{{ getTodayBreakTime() }}m</span>
          </div>
          <div class="summary-item">
            <span class="label">Total Tasks</span>
            <span class="value">{{ getTodayTaskCount() }}</span>
          </div>
        </div>
      </div>

      <!-- Category Breakdown -->
      <div class="analytics-card">
        <div class="card-header">
          <h3>Time by Category</h3>
        </div>
        
        <div class="category-breakdown">
          <div v-for="(category, idx) in categoryTimes" :key="idx" class="category-item">
            <div class="category-header">
              <span class="category-name">{{ category.name }}</span>
              <span class="category-time">{{ category.time }}m</span>
            </div>
            <div class="category-bar">
              <div 
                class="category-fill" 
                :style="{ 
                  width: getTotalMinutes() > 0 ? (category.time / getTotalMinutes()) * 100 + '%' : '0%',
                  backgroundColor: getCategoryColor(category.name)
                }"
              ></div>
            </div>
          </div>
        </div>
      </div>

      <!-- Weekly Stats -->
      <div class="analytics-card">
        <div class="card-header">
          <h3>Weekly Activity</h3>
        </div>

        <div class="weekly-chart">
          <div v-for="(day, idx) in weeklyData" :key="idx" class="day-bar">
            <div 
              class="bar" 
              :style="{ height: (day.minutes / maxDailyMinutes) * 100 + '%' }"
            ></div>
            <span class="day-label">{{ day.day }}</span>
            <span class="day-value">{{ day.minutes }}m</span>
          </div>
        </div>
      </div>

      <!-- Productivity Score -->
      <div class="analytics-card">
        <div class="card-header">
          <h3>Productivity Score</h3>
        </div>

        <div class="score-display">
          <svg viewBox="0 0 100 100" class="score-gauge">
            <circle cx="50" cy="50" r="45" class="gauge-bg"/>
            <circle 
              cx="50" 
              cy="50" 
              r="45" 
              class="gauge-fill" 
              :style="{ 'stroke-dasharray': `${productivityScore * 2.83} 283` }"
            />
            <text x="50" y="55" text-anchor="middle" class="gauge-text">{{ productivityScore }}%</text>
          </svg>
          
          <div class="score-details">
            <p class="score-rating">{{ getProductivityRating() }}</p>
            <p class="score-message">{{ getProductivityMessage() }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, inject } from 'vue'

export default {
  name: 'TimeAnalytics',
  setup() {
    const tasks = inject('tasks', [])
    const timeLog = inject('timeLog', [])

    const selectedDate = ref(new Date().toISOString().split('T')[0])

    const getTodayFocusTime = () => {
      return timeLog
        .filter(entry => entry.date === selectedDate.value && entry.type === 'work')
        .reduce((sum, entry) => sum + entry.duration, 0)
    }

    const getTodayBreakTime = () => {
      return timeLog
        .filter(entry => entry.date === selectedDate.value && entry.type === 'break')
        .reduce((sum, entry) => sum + entry.duration, 0)
    }

    const getTodayTaskCount = () => {
      return new Set(
        timeLog
          .filter(entry => entry.date === selectedDate.value && entry.type === 'work')
          .map(entry => entry.taskId)
      ).size
    }

    const getTotalMinutes = () => {
      return timeLog
        .filter(entry => entry.date === selectedDate.value && entry.type === 'work')
        .reduce((sum, entry) => sum + entry.duration, 0)
    }

    const categoryTimes = computed(() => {
      const categories = {}
      
      timeLog
        .filter(entry => entry.date === selectedDate.value && entry.type === 'work')
        .forEach(entry => {
          const task = tasks.find(t => t.id === entry.taskId)
          if (task) {
            categories[task.category] = (categories[task.category] || 0) + entry.duration
          }
        })

      return Object.entries(categories).map(([name, time]) => ({ name, time }))
    })

    const weeklyData = computed(() => {
      const days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
      const data = []
      
      for (let i = 6; i >= 0; i--) {
        const date = new Date()
        date.setDate(date.getDate() - i)
        const dateStr = date.toISOString().split('T')[0]
        
        const minutes = timeLog
          .filter(entry => entry.date === dateStr && entry.type === 'work')
          .reduce((sum, entry) => sum + entry.duration, 0)
        
        data.push({
          day: days[date.getDay()],
          minutes,
          date: dateStr
        })
      }
      
      return data
    })

    const maxDailyMinutes = computed(() => {
      return Math.max(...weeklyData.value.map(d => d.minutes), 60)
    })

    const getCategoryColor = (category) => {
      const colors = {
        'Work': '#1a73e8',
        'Personal': '#d23636',
        'Study': '#188038',
        'Health': '#e8c547',
        'default': '#5a5f66'
      }
      return colors[category] || colors['default']
    }

    const productivityScore = computed(() => {
      const todayFocus = getTodayFocusTime()
      const averageDaily = weeklyData.value.reduce((sum, d) => sum + d.minutes, 0) / 7 || 60
      return Math.min(Math.round((todayFocus / averageDaily) * 100), 100)
    })

    const getProductivityRating = () => {
      const score = productivityScore.value
      if (score >= 90) return '🌟 Excellent'
      if (score >= 75) return '⭐ Great'
      if (score >= 50) return '👍 Good'
      if (score >= 25) return '📈 Fair'
      return '⏱️ Getting Started'
    }

    const getProductivityMessage = () => {
      const score = productivityScore.value
      if (score >= 90) return 'You\'re crushing it today!'
      if (score >= 75) return 'Great focus and productivity!'
      if (score >= 50) return 'You\'re on track'
      if (score >= 25) return 'Keep pushing forward'
      return 'Time to get focused'
    }

    return {
      selectedDate,
      getTodayFocusTime,
      getTodayBreakTime,
      getTodayTaskCount,
      getTotalMinutes,
      categoryTimes,
      weeklyData,
      maxDailyMinutes,
      getCategoryColor,
      productivityScore,
      getProductivityRating,
      getProductivityMessage
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

.analytics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.analytics-card {
  background: #f5f6f7;
  border-radius: 8px;
  padding: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-header h3 {
  margin: 0;
  color: #202124;
  font-size: 16px;
  font-weight: 600;
}

.date-input {
  padding: 6px 10px;
  border: 1px solid #d0d2d7;
  border-radius: 4px;
  font-size: 13px;
}

.summary-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.summary-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px;
  background: white;
  border-radius: 6px;
  border: 1px solid #d0d2d7;
}

.label {
  font-size: 12px;
  color: #5a5f66;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: 500;
}

.value {
  font-size: 20px;
  font-weight: 700;
  color: #1a73e8;
  margin-top: 4px;
}

.category-breakdown {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.category-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.category-header {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
}

.category-name {
  font-weight: 600;
  color: #202124;
}

.category-time {
  font-weight: 700;
  color: #1a73e8;
}

.category-bar {
  height: 8px;
  background: white;
  border-radius: 4px;
  overflow: hidden;
}

.category-fill {
  height: 100%;
  transition: width 0.3s ease;
}

.weekly-chart {
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  height: 200px;
  gap: 8px;
}

.day-bar {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  flex: 1;
}

.bar {
  width: 100%;
  background: linear-gradient(180deg, #1a73e8 0%, #0d47a1 100%);
  border-radius: 4px 4px 0 0;
  min-height: 4px;
  transition: height 0.3s ease;
}

.day-label {
  font-size: 11px;
  font-weight: 600;
  color: #5a5f66;
}

.day-value {
  font-size: 10px;
  color: #202124;
}

.score-display {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 16px 0;
}

.score-gauge {
  width: 120px;
  height: 120px;
}

.gauge-bg {
  fill: none;
  stroke: #d0d2d7;
  stroke-width: 4;
}

.gauge-fill {
  fill: none;
  stroke: #1a73e8;
  stroke-width: 4;
  stroke-dashoffset: 0;
  transition: stroke-dasharray 0.5s ease;
  transform-origin: 50% 50%;
}

.gauge-text {
  font-size: 24px;
  font-weight: 700;
  fill: #202124;
}

.score-details {
  flex: 1;
}

.score-rating {
  font-size: 18px;
  font-weight: 700;
  color: #202124;
  margin: 0;
}

.score-message {
  font-size: 13px;
  color: #5a5f66;
  margin: 4px 0 0 0;
}

@media (max-width: 768px) {
  .analytics-grid {
    grid-template-columns: 1fr;
  }

  .summary-stats {
    grid-template-columns: 1fr;
  }

  .weekly-chart {
    height: 150px;
  }

  .score-display {
    flex-direction: column;
    gap: 12px;
  }
}
</style>
