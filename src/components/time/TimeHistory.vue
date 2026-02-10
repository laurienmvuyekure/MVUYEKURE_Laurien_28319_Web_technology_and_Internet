<template>
  <div>
    <h2>📋 Time History</h2>

    <div class="history-controls">
      <input 
        v-model="filterDate" 
        type="date" 
        class="date-filter"
        @change="updateFilter"
      >
      <select v-model="filterType" class="type-filter" @change="updateFilter">
        <option value="">All Activities</option>
        <option value="work">Work Sessions</option>
        <option value="break">Break Sessions</option>
      </select>
      <button @click="exportHistory" class="btn btn-secondary">📥 Export</button>
    </div>

    <div v-if="filteredHistory.length === 0" class="no-history">
      <p>No time log entries yet. Start tracking to see history.</p>
    </div>

    <div v-else class="history-list">
      <div v-for="(entry, idx) in filteredHistory" :key="idx" :class="['history-item', entry.type]">
        <div class="item-header">
          <div class="item-title">
            <span class="item-icon">{{ entry.type === 'work' ? '💼' : '☕' }}</span>
            <span class="item-name">{{ getTaskName(entry.taskId) }}</span>
          </div>
          <span class="item-time">{{ entry.duration }}m</span>
        </div>
        
        <div class="item-info">
          <span class="info-date">📅 {{ formatDate(entry.date) }}</span>
          <span class="info-type" :class="entry.type">{{ entry.type }}</span>
        </div>
        
        <div class="item-progress">
          <div class="progress-bar">
            <div 
              class="progress-fill" 
              :style="{ width: getProgressPercent(entry) + '%' }"
            ></div>
          </div>
          <span class="progress-label">{{ getProgressLabel(entry) }}</span>
        </div>
      </div>

      <div class="history-summary">
        <div class="summary-card">
          <span class="summary-label">Total Logged Time</span>
          <span class="summary-value">{{ getTotalTime() }}m</span>
        </div>
        <div class="summary-card">
          <span class="summary-label">Sessions</span>
          <span class="summary-value">{{ filteredHistory.length }}</span>
        </div>
        <div class="summary-card">
          <span class="summary-label">Average Session</span>
          <span class="summary-value">{{ getAverageSession() }}m</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, inject } from 'vue'

export default {
  name: 'TimeHistory',
  setup() {
    const tasks = inject('tasks', [])
    const timeLog = inject('timeLog', [])

    const filterDate = ref('')
    const filterType = ref('')

    const filteredHistory = computed(() => {
      let result = [...timeLog]

      if (filterDate.value) {
        result = result.filter(entry => entry.date === filterDate.value)
      }

      if (filterType.value) {
        result = result.filter(entry => entry.type === filterType.value)
      }

      return result.sort((a, b) => {
        const dateA = new Date(a.date)
        const dateB = new Date(b.date)
        return dateB - dateA
      })
    })

    const getTaskName = (taskId) => {
      if (!taskId) return 'Break'
      const task = tasks.find(t => t.id === taskId)
      return task ? task.title : 'Unknown Task'
    }

    const formatDate = (dateStr) => {
      const date = new Date(dateStr + 'T00:00:00')
      return date.toLocaleDateString('en-GB', {
        weekday: 'short',
        day: 'numeric',
        month: 'short'
      })
    }

    const getProgressPercent = (entry) => {
      const standardDuration = entry.type === 'work' ? 60 : 5
      return Math.min((entry.duration / standardDuration) * 100, 100)
    }

    const getProgressLabel = (entry) => {
      const standard = entry.type === 'work' ? 60 : 5
      if (entry.duration >= standard) {
        return `+${entry.duration - standard}m over`
      } else {
        return `${entry.duration}/${standard}m`
      }
    }

    const getTotalTime = () => {
      return filteredHistory.value.reduce((sum, entry) => sum + entry.duration, 0)
    }

    const getAverageSession = () => {
      if (filteredHistory.value.length === 0) return 0
      return Math.round(getTotalTime() / filteredHistory.value.length)
    }

    const updateFilter = () => {
      // Filters are reactive via computed property
    }

    const exportHistory = () => {
      const csvContent = [
        ['Date', 'Task', 'Type', 'Duration (min)'].join(','),
        ...filteredHistory.value.map(entry =>
          [
            entry.date,
            getTaskName(entry.taskId),
            entry.type,
            entry.duration
          ].join(',')
        )
      ].join('\n')

      const blob = new Blob([csvContent], { type: 'text/csv' })
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `time-history-${new Date().toISOString().split('T')[0]}.csv`
      a.click()
      window.URL.revokeObjectURL(url)
    }

    return {
      filterDate,
      filterType,
      filteredHistory,
      getTaskName,
      formatDate,
      getProgressPercent,
      getProgressLabel,
      getTotalTime,
      getAverageSession,
      updateFilter,
      exportHistory
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

.history-controls {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.date-filter,
.type-filter {
  padding: 10px 12px;
  border: 1px solid #d0d2d7;
  border-radius: 6px;
  font-size: 14px;
  color: #202124;
}

.date-filter:focus,
.type-filter:focus {
  outline: none;
  border-color: #1a73e8;
  box-shadow: 0 0 0 3px rgba(26, 115, 232, 0.1);
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

.btn-secondary {
  background: #f5f6f7;
  color: #202124;
  border: 1px solid #d0d2d7;
}

.btn-secondary:hover {
  background: #eaeced;
}

.no-history {
  text-align: center;
  padding: 40px 20px;
  color: #5a5f66;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.history-item {
  background: white;
  border: 1px solid #d0d2d7;
  border-left: 4px solid #1a73e8;
  border-radius: 6px;
  padding: 16px;
  transition: all 0.2s;
}

.history-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border-left-color: #1557b0;
}

.history-item.break {
  border-left-color: #188038;
}

.history-item.break:hover {
  border-left-color: #0d652d;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.item-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.item-icon {
  font-size: 20px;
}

.item-name {
  font-size: 15px;
  font-weight: 600;
  color: #202124;
}

.item-time {
  font-size: 18px;
  font-weight: 700;
  color: #1a73e8;
}

.item-info {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
  font-size: 12px;
}

.info-date {
  color: #5a5f66;
}

.info-type {
  padding: 2px 8px;
  background: #e8f0fe;
  color: #1a73e8;
  border-radius: 3px;
  text-transform: capitalize;
  font-weight: 500;
}

.info-type.break {
  background: #e6f5ea;
  color: #188038;
}

.item-progress {
  display: flex;
  gap: 8px;
  align-items: center;
}

.progress-bar {
  flex: 1;
  height: 6px;
  background: #d0d2d7;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #1a73e8, #0d47a1);
  transition: width 0.3s ease;
}

.progress-label {
  font-size: 11px;
  color: #5a5f66;
  font-weight: 500;
  min-width: 60px;
  text-align: right;
}

.history-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 12px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #d0d2d7;
}

.summary-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px;
  background: #f5f6f7;
  border-radius: 6px;
  border: 1px solid #d0d2d7;
}

.summary-label {
  font-size: 12px;
  color: #5a5f66;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: 500;
}

.summary-value {
  font-size: 24px;
  font-weight: 700;
  color: #1a73e8;
  margin-top: 4px;
}

@media (max-width: 768px) {
  .history-controls {
    flex-direction: column;
  }

  .date-filter,
  .type-filter,
  .btn {
    width: 100%;
  }

  .item-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .history-summary {
    grid-template-columns: 1fr;
  }
}
</style>
