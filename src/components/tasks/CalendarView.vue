<template>
  <div class="calendar-view">
    <h2>📅 Calendar View</h2>
    <p class="description">Visualize your tasks on a calendar</p>

    <div class="calendar-controls">
      <button @click="previousMonth" class="btn-nav">◀ Previous</button>
      <div class="month-year">
        <select v-model="selectedMonth" class="select-month">
          <option v-for="(name, idx) in monthNames" :key="idx" :value="idx">
            {{ name }}
          </option>
        </select>
        <select v-model.number="selectedYear" class="select-year">
          <option v-for="year in yearRange" :key="year" :value="year">
            {{ year }}
          </option>
        </select>
      </div>
      <button @click="nextMonth" class="btn-nav">Next ▶</button>
      <button @click="goToToday" class="btn-today">📍 Today</button>
    </div>

    <div class="calendar-container">
      <div class="calendar">
        <!-- Day headers -->
        <div class="calendar-header">
          <div v-for="day in dayNames" :key="day" class="day-header">
            {{ day }}
          </div>
        </div>

        <!-- Calendar days -->
        <div class="calendar-grid">
          <div 
            v-for="date in calendarDays" 
            :key="date.dateStr"
            :class="['calendar-day', { 
              'other-month': !date.isCurrentMonth,
              'today': date.isToday,
              'has-tasks': date.taskCount > 0
            }]"
            @click="selectDate(date.dateStr)"
          >
            <div class="day-number">{{ date.day }}</div>
            <div class="day-tasks">
              <div v-for="task in date.tasks.slice(0, 2)" :key="task.id" class="task-dot" :class="task.priority">
                {{ task.title[0] }}
              </div>
              <div v-if="date.taskCount > 2" class="task-more">+{{ date.taskCount - 2 }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Day details panel -->
      <div class="day-details">
        <h3 v-if="selectedDateObj">{{ formatDateTitle(selectedDateObj) }}</h3>
        <p v-else class="select-prompt">Select a date to view tasks</p>

        <div v-if="selectedDateObj && selectedDateObj.taskCount > 0" class="tasks-list">
          <div v-for="task in selectedDateObj.tasks" :key="task.id" class="task-item">
            <div class="task-checkbox">
              <input 
                type="checkbox" 
                :checked="task.completed"
                @change="toggleTask(task.id)"
              />
            </div>
            <div class="task-content">
              <div class="task-title" :class="{ completed: task.completed }">
                {{ task.title }}
              </div>
              <div class="task-meta">
                <span class="priority-badge" :class="task.priority">{{ task.priority }}</span>
                <span class="category">{{ task.category }}</span>
                <span v-if="task.time" class="time">{{ task.time }}</span>
              </div>
              <div v-if="task.description" class="task-desc">{{ task.description }}</div>
            </div>
          </div>
        </div>

        <div v-else-if="selectedDateObj" class="no-tasks-message">
          <p>No tasks scheduled for this date</p>
        </div>
      </div>
    </div>

    <!-- Legend -->
    <div class="calendar-legend">
      <div class="legend-item">
        <span class="legend-dot critical"></span>
        <span>Critical</span>
      </div>
      <div class="legend-item">
        <span class="legend-dot high"></span>
        <span>High</span>
      </div>
      <div class="legend-item">
        <span class="legend-dot medium"></span>
        <span>Medium</span>
      </div>
      <div class="legend-item">
        <span class="legend-dot low"></span>
        <span>Low</span>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, inject, reactive } from 'vue'

export default {
  name: 'CalendarView',
  setup() {
    const tasks = inject('tasks', ref([]))
    const toggleTask = inject('toggleTask', () => {})

    const monthNames = [
      'January', 'February', 'March', 'April', 'May', 'June',
      'July', 'August', 'September', 'October', 'November', 'December'
    ]
    
    const dayNames = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']

    const today = new Date()
    const selectedMonth = ref(today.getMonth())
    const selectedYear = ref(today.getFullYear())
    const selectedDateStr = ref(null)

    const yearRange = computed(() => {
      const current = new Date().getFullYear()
      const years = []
      for (let i = current - 2; i <= current + 2; i++) {
        years.push(i)
      }
      return years
    })

    const calendarDays = computed(() => {
      const firstDay = new Date(selectedYear.value, selectedMonth.value, 1)
      const lastDay = new Date(selectedYear.value, selectedMonth.value + 1, 0)
      const startDate = new Date(firstDay)
      startDate.setDate(startDate.getDate() - firstDay.getDay())

      const days = []
      let currentDate = new Date(startDate)

      // Generate 42 days (6 weeks * 7 days)
      for (let i = 0; i < 42; i++) {
        const dateStr = currentDate.toISOString().split('T')[0]
        const isCurrentMonth = currentDate.getMonth() === selectedMonth.value
        const isToday = dateStr === new Date().toISOString().split('T')[0]

        const dayTasks = tasks.value.filter(t => t.date === dateStr)

        days.push({
          dateStr,
          day: currentDate.getDate(),
          isCurrentMonth,
          isToday,
          tasks: dayTasks,
          taskCount: dayTasks.length
        })

        currentDate.setDate(currentDate.getDate() + 1)
      }

      return days
    })

    const selectedDateObj = computed(() => {
      if (!selectedDateStr.value) return null
      return calendarDays.value.find(d => d.dateStr === selectedDateStr.value)
    })

    const previousMonth = () => {
      if (selectedMonth.value === 0) {
        selectedMonth.value = 11
        selectedYear.value--
      } else {
        selectedMonth.value--
      }
    }

    const nextMonth = () => {
      if (selectedMonth.value === 11) {
        selectedMonth.value = 0
        selectedYear.value++
      } else {
        selectedMonth.value++
      }
    }

    const goToToday = () => {
      const today = new Date()
      selectedMonth.value = today.getMonth()
      selectedYear.value = today.getFullYear()
      selectedDateStr.value = today.toISOString().split('T')[0]
    }

    const selectDate = (dateStr) => {
      selectedDateStr.value = selectedDateStr.value === dateStr ? null : dateStr
    }

    const formatDateTitle = (dateObj) => {
      const date = new Date(dateObj.dateStr + 'T00:00:00')
      return date.toLocaleDateString('en-GB', { 
        weekday: 'long', 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric' 
      })
    }

    // Select today by default
    goToToday()

    return {
      monthNames,
      dayNames,
      selectedMonth,
      selectedYear,
      selectedDateStr,
      yearRange,
      calendarDays,
      selectedDateObj,
      previousMonth,
      nextMonth,
      goToToday,
      selectDate,
      formatDateTitle,
      toggleTask
    }
  }
}
</script>

<style scoped>
.calendar-view {
  padding: 20px;
  background: white;
  border-radius: 12px;
}

h2 {
  color: #1a73e8;
  margin-bottom: 8px;
  font-size: 24px;
}

.description {
  color: #5f6368;
  margin-bottom: 20px;
  font-size: 14px;
}

.calendar-controls {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.btn-nav,
.btn-today {
  padding: 8px 16px;
  background: #f5f5f5;
  border: 1px solid #e8eaed;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.2s ease;

  &:hover {
    background: #e8eaed;
    border-color: #1a73e8;
  }
}

.btn-today {
  background: #1a73e8;
  color: white;
  border-color: #1a73e8;

  &:hover {
    background: #1665d8;
  }
}

.month-year {
  display: flex;
  gap: 8px;
}

.select-month,
.select-year {
  padding: 8px;
  border: 1px solid #e8eaed;
  border-radius: 4px;
  font-weight: 600;
  cursor: pointer;

  &:focus {
    outline: none;
    border-color: #1a73e8;
  }
}

.select-month {
  min-width: 120px;
}

.select-year {
  min-width: 80px;
}

.calendar-container {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 20px;
  margin-bottom: 20px;
}

.calendar {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
}

.calendar-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
  margin-bottom: 8px;
}

.day-header {
  text-align: center;
  font-weight: 600;
  color: #1a73e8;
  padding: 8px;
  font-size: 12px;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
}

.calendar-day {
  background: white;
  border: 1px solid #e8eaed;
  border-radius: 6px;
  padding: 8px;
  min-height: 80px;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;

  &:hover {
    border-color: #1a73e8;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }

  &.other-month {
    background: #f5f5f5;
    color: #ccc;
  }

  &.today {
    background: #e3f2fd;
    border-color: #1a73e8;
    font-weight: 600;
  }

  &.has-tasks {
    border-color: #1a73e8;
  }
}

.day-number {
  font-weight: 600;
  color: #202124;
  margin-bottom: 6px;
  font-size: 14px;
}

.day-tasks {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

.task-dot {
  width: 20px;
  height: 20px;
  border-radius: 3px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 9px;
  font-weight: 600;
  color: white;

  &.critical { background: #d32f2f; }
  &.high { background: #f57c00; }
  &.medium { background: #fbc02d; }
  &.low { background: #388e3c; }
}

.task-more {
  font-size: 9px;
  color: #5f6368;
  padding: 2px 4px;
}

.day-details {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
  max-height: 500px;
  overflow-y: auto;
}

.day-details h3 {
  color: #202124;
  margin-bottom: 12px;
  font-size: 16px;
}

.select-prompt {
  color: #5f6368;
  text-align: center;
  padding: 20px;
  font-size: 13px;
}

.no-tasks-message {
  color: #5f6368;
  text-align: center;
  padding: 20px;
  font-size: 13px;
}

.tasks-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.task-item {
  background: white;
  border-radius: 6px;
  padding: 10px;
  display: flex;
  gap: 10px;
  border-left: 3px solid #e8eaed;
}

.task-checkbox {
  display: flex;
  align-items: flex-start;
  margin-top: 2px;
}

.task-checkbox input {
  cursor: pointer;
}

.task-content {
  flex: 1;
  min-width: 0;
}

.task-title {
  font-weight: 600;
  color: #202124;
  font-size: 13px;
  margin-bottom: 4px;

  &.completed {
    text-decoration: line-through;
    color: #9aa0a6;
  }
}

.task-meta {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  margin-bottom: 4px;
}

.priority-badge {
  padding: 1px 6px;
  border-radius: 3px;
  font-size: 10px;
  font-weight: 600;

  &.critical { background: #ffebee; color: #d32f2f; }
  &.high { background: #fff3e0; color: #f57c00; }
  &.medium { background: #fffde7; color: #f9a825; }
  &.low { background: #e8f5e9; color: #388e3c; }
}

.category {
  font-size: 10px;
  color: #5f6368;
  background: #f5f5f5;
  padding: 1px 6px;
  border-radius: 3px;
}

.time {
  font-size: 10px;
  color: #5f6368;
}

.task-desc {
  font-size: 11px;
  color: #5f6368;
  margin-top: 4px;
  line-height: 1.3;
}

.calendar-legend {
  display: flex;
  gap: 16px;
  justify-content: center;
  padding: 16px;
  background: #f5f5f5;
  border-radius: 8px;
  flex-wrap: wrap;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #5f6368;
}

.legend-dot {
  width: 12px;
  height: 12px;
  border-radius: 2px;

  &.critical { background: #d32f2f; }
  &.high { background: #f57c00; }
  &.medium { background: #fbc02d; }
  &.low { background: #388e3c; }
}

@media (max-width: 1024px) {
  .calendar-container {
    grid-template-columns: 1fr;
  }

  .day-details {
    max-height: 300px;
  }
}

@media (max-width: 768px) {
  .calendar-controls {
    flex-direction: column;
    width: 100%;
  }

  .select-month,
  .select-year {
    max-width: 100%;
  }

  .calendar-grid {
    gap: 6px;
  }

  .calendar-day {
    min-height: 60px;
    padding: 6px;
    font-size: 12px;
  }

  .day-number {
    font-size: 12px;
  }

  .task-dot {
    width: 16px;
    height: 16px;
    font-size: 8px;
  }
}
</style>
