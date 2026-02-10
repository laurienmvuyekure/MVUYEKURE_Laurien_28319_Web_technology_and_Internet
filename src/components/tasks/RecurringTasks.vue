<template>
  <div class="recurring-tasks">
    <h2>🔄 Recurring Tasks</h2>
    <p class="description">Create and manage tasks that repeat automatically</p>

    <div class="recurring-controls">
      <div class="control-group">
        <label for="recurrence-title">Task Title</label>
        <input 
          v-model="newRecurring.title"
          id="recurrence-title"
          type="text"
          placeholder="e.g., Weekly team meeting"
          class="input-field"
        />
      </div>

      <div class="control-group">
        <label for="recurrence-pattern">Recurrence Pattern</label>
        <select v-model="newRecurring.pattern" id="recurrence-pattern" class="select-field">
          <option value="daily">Daily</option>
          <option value="weekly">Weekly</option>
          <option value="biweekly">Bi-weekly</option>
          <option value="monthly">Monthly</option>
          <option value="quarterly">Quarterly</option>
          <option value="yearly">Yearly</option>
        </select>
      </div>

      <div v-if="newRecurring.pattern === 'weekly'" class="control-group">
        <label>Days of Week</label>
        <div class="days-selector">
          <button 
            v-for="day in daysOfWeek"
            :key="day"
            @click="toggleDay(day)"
            class="day-button"
            :class="{ selected: newRecurring.daysOfWeek.includes(day) }"
          >
            {{ day.substring(0, 3) }}
          </button>
        </div>
      </div>

      <div class="control-row">
        <div class="control-group">
          <label for="recurrence-category">Category</label>
          <select v-model="newRecurring.category" id="recurrence-category" class="select-field">
            <option value="Work">Work</option>
            <option value="Personal">Personal</option>
            <option value="Study">Study</option>
            <option value="Health">Health</option>
            <option value="Finance">Finance</option>
          </select>
        </div>

        <div class="control-group">
          <label for="recurrence-priority">Priority</label>
          <select v-model="newRecurring.priority" id="recurrence-priority" class="select-field">
            <option value="low">Low</option>
            <option value="medium">Medium</option>
            <option value="high">High</option>
          </select>
        </div>

        <div class="control-group">
          <label for="recurrence-duration">Duration (min)</label>
          <input 
            v-model.number="newRecurring.duration"
            id="recurrence-duration"
            type="number"
            min="1"
            max="480"
            class="input-field"
          />
        </div>
      </div>

      <div class="control-group">
        <label for="recurrence-end-date">End Date (optional)</label>
        <input 
          v-model="newRecurring.endDate"
          id="recurrence-end-date"
          type="date"
          class="input-field"
        />
      </div>

      <button @click="createRecurringTask" class="btn-create" :disabled="!newRecurring.title.trim()">
        ➕ Create Recurring Task
      </button>
    </div>

    <div class="recurring-list">
      <h3>Active Recurring Tasks ({{ activeRecurring.length }})</h3>

      <div v-if="activeRecurring.length === 0" class="no-recurring">
        <p>No active recurring tasks. Create one to get started!</p>
      </div>

      <div v-else class="recurring-grid">
        <div 
          v-for="recurring in activeRecurring"
          :key="recurring.id"
          class="recurring-card"
        >
          <div class="recurring-header">
            <h4>{{ recurring.title }}</h4>
            <span class="recurring-badge">{{ formatPattern(recurring.pattern) }}</span>
          </div>

          <div class="recurring-info">
            <div class="info-row">
              <span class="label">Category:</span>
              <span class="value">{{ recurring.category }}</span>
            </div>
            <div class="info-row">
              <span class="label">Priority:</span>
              <span class="value" :class="recurring.priority.toLowerCase()">{{ recurring.priority }}</span>
            </div>
            <div class="info-row">
              <span class="label">Duration:</span>
              <span class="value">{{ recurring.duration }} min</span>
            </div>
            <div v-if="recurring.daysOfWeek && recurring.daysOfWeek.length" class="info-row">
              <span class="label">Days:</span>
              <span class="value">{{ recurring.daysOfWeek.join(', ') }}</span>
            </div>
            <div v-if="recurring.endDate" class="info-row">
              <span class="label">Ends:</span>
              <span class="value">{{ formatDate(recurring.endDate) }}</span>
            </div>
            <div class="info-row">
              <span class="label">Created:</span>
              <span class="value">{{ formatDate(recurring.createdAt) }}</span>
            </div>
          </div>

          <div class="recurring-stats">
            <span class="stat">📊 Generated: {{ recurring.generatedCount || 0 }}</span>
            <span class="stat">✅ Completed: {{ recurring.completedCount || 0 }}</span>
          </div>

          <div class="recurring-actions">
            <button @click="pauseRecurring(recurring.id)" class="btn-pause" title="Pause">
              ⏸️
            </button>
            <button @click="generateNextTask(recurring.id)" class="btn-generate" title="Generate next">
              📋
            </button>
            <button @click="deleteRecurring(recurring.id)" class="btn-delete" title="Delete">
              🗑️
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="paused-recurring" v-if="pausedRecurring.length > 0">
      <h3>Paused ({{ pausedRecurring.length }})</h3>
      <div class="recurring-grid">
        <div 
          v-for="recurring in pausedRecurring"
          :key="recurring.id"
          class="recurring-card paused"
        >
          <h4>{{ recurring.title }}</h4>
          <button @click="resumeRecurring(recurring.id)" class="btn-resume">
            ▶️ Resume
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'

export default {
  name: 'RecurringTasks',
  emits: ['task-generated'],
  setup(props, { emit }) {
    const daysOfWeek = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']

    const newRecurring = ref({
      title: '',
      pattern: 'weekly',
      daysOfWeek: ['Monday', 'Wednesday', 'Friday'],
      category: 'Work',
      priority: 'medium',
      duration: 30,
      endDate: ''
    })

    const recurringTasks = ref([])

    const loadRecurringTasks = () => {
      const saved = localStorage.getItem('recurring-tasks')
      if (saved) {
        try {
          recurringTasks.value = JSON.parse(saved)
        } catch (e) {
          console.error('Failed to load recurring tasks:', e)
        }
      }
    }

    const saveRecurringTasks = () => {
      localStorage.setItem('recurring-tasks', JSON.stringify(recurringTasks.value))
    }

    const toggleDay = (day) => {
      const index = newRecurring.value.daysOfWeek.indexOf(day)
      if (index > -1) {
        newRecurring.value.daysOfWeek.splice(index, 1)
      } else {
        newRecurring.value.daysOfWeek.push(day)
      }
    }

    const createRecurringTask = () => {
      if (!newRecurring.value.title.trim()) return

      recurringTasks.value.push({
        id: Date.now(),
        ...newRecurring.value,
        active: true,
        createdAt: new Date().toISOString(),
        generatedCount: 0,
        completedCount: 0,
        lastGenerated: null
      })

      saveRecurringTasks()
      newRecurring.value = {
        title: '',
        pattern: 'weekly',
        daysOfWeek: ['Monday', 'Wednesday', 'Friday'],
        category: 'Work',
        priority: 'medium',
        duration: 30,
        endDate: ''
      }
    }

    const generateNextTask = (id) => {
      const recurring = recurringTasks.value.find(r => r.id === id)
      if (recurring) {
        const task = {
          id: Date.now(),
          title: recurring.title,
          category: recurring.category,
          priority: recurring.priority,
          duration: recurring.duration,
          date: new Date().toISOString().split('T')[0],
          time: '09:00',
          completed: false,
          createdAt: new Date().toISOString(),
          recurringId: id
        }

        recurring.generatedCount = (recurring.generatedCount || 0) + 1
        recurring.lastGenerated = new Date().toISOString()
        saveRecurringTasks()
        
        emit('task-generated', task)
      }
    }

    const pauseRecurring = (id) => {
      const recurring = recurringTasks.value.find(r => r.id === id)
      if (recurring) {
        recurring.active = false
        saveRecurringTasks()
      }
    }

    const resumeRecurring = (id) => {
      const recurring = recurringTasks.value.find(r => r.id === id)
      if (recurring) {
        recurring.active = true
        saveRecurringTasks()
      }
    }

    const deleteRecurring = (id) => {
      const index = recurringTasks.value.findIndex(r => r.id === id)
      if (index > -1) {
        recurringTasks.value.splice(index, 1)
        saveRecurringTasks()
      }
    }

    const activeRecurring = computed(() => {
      return recurringTasks.value.filter(r => r.active)
    })

    const pausedRecurring = computed(() => {
      return recurringTasks.value.filter(r => !r.active)
    })

    const formatPattern = (pattern) => {
      const map = {
        'daily': 'Daily',
        'weekly': 'Weekly',
        'biweekly': 'Bi-weekly',
        'monthly': 'Monthly',
        'quarterly': 'Quarterly',
        'yearly': 'Yearly'
      }
      return map[pattern] || pattern
    }

    const formatDate = (dateStr) => {
      const date = new Date(dateStr)
      return date.toLocaleDateString('en-GB', { month: 'short', day: 'numeric', year: 'numeric' })
    }

    loadRecurringTasks()

    return {
      newRecurring,
      recurringTasks,
      daysOfWeek,
      activeRecurring,
      pausedRecurring,
      toggleDay,
      createRecurringTask,
      generateNextTask,
      pauseRecurring,
      resumeRecurring,
      deleteRecurring,
      formatPattern,
      formatDate
    }
  }
}
</script>

<style scoped>
.recurring-tasks {
  background: white;
  padding: 24px;
  border-radius: 12px;
  max-width: 1200px;
  margin: 0 auto;
}

.recurring-tasks h2 {
  color: #1a73e8;
  font-size: 28px;
  margin-bottom: 8px;
}

.description {
  color: #5f6368;
  margin-bottom: 24px;
}

.recurring-controls {
  background: #f5f7fa;
  border: 1px solid #e8eaed;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 32px;
}

.control-group {
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.control-group label {
  font-weight: 600;
  color: #202124;
  font-size: 13px;
}

.input-field,
.select-field {
  padding: 8px 12px;
  border: 1px solid #e8eaed;
  border-radius: 6px;
  font-family: inherit;
  font-size: 13px;
}

.input-field:focus,
.select-field:focus {
  outline: none;
  border-color: #1a73e8;
  box-shadow: 0 0 0 2px rgba(26, 115, 232, 0.1);
}

.control-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.days-selector {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(60px, 1fr));
  gap: 6px;
}

.day-button {
  padding: 6px 8px;
  border: 2px solid #e8eaed;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 600;
  transition: all 0.2s;
}

.day-button:hover {
  border-color: #1a73e8;
}

.day-button.selected {
  background: #1a73e8;
  color: white;
  border-color: #1a73e8;
}

.btn-create {
  width: 100%;
  padding: 12px;
  background: #1a73e8;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
}

.btn-create:hover:not(:disabled) {
  background: #1557b0;
}

.btn-create:disabled {
  background: #d0d2d7;
  cursor: not-allowed;
}

.recurring-list {
  margin-bottom: 32px;
}

.recurring-list h3 {
  color: #202124;
  font-size: 18px;
  margin-bottom: 16px;
  margin-top: 0;
}

.no-recurring {
  text-align: center;
  padding: 40px 20px;
  background: #f5f7fa;
  border-radius: 8px;
  color: #5f6368;
}

.recurring-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.recurring-card {
  background: white;
  border: 1px solid #e8eaed;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.2s;
  border-left: 4px solid #1a73e8;
}

.recurring-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border-left-color: #34a853;
}

.recurring-card.paused {
  opacity: 0.6;
  border-left-color: #fbbc06;
}

.recurring-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  gap: 8px;
}

.recurring-header h4 {
  margin: 0;
  color: #1a73e8;
  font-size: 16px;
}

.recurring-badge {
  background: #e8f5e9;
  color: #34a853;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
  white-space: nowrap;
}

.recurring-info {
  background: #f9f9f9;
  padding: 12px;
  border-radius: 6px;
  margin-bottom: 12px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  margin-bottom: 6px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.label {
  color: #5f6368;
  font-weight: 600;
}

.value {
  color: #202124;
  font-weight: 500;
}

.value.low {
  color: #34a853;
}

.value.medium {
  color: #fbbc06;
}

.value.high {
  color: #d32f2f;
}

.recurring-stats {
  display: flex;
  gap: 12px;
  font-size: 12px;
  margin-bottom: 12px;
  padding: 8px 0;
  border-top: 1px solid #e8eaed;
  border-bottom: 1px solid #e8eaed;
}

.stat {
  color: #5f6368;
}

.recurring-actions {
  display: flex;
  gap: 6px;
}

.btn-pause,
.btn-generate,
.btn-delete,
.btn-resume {
  flex: 1;
  padding: 6px;
  border: 1px solid #e8eaed;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s;
}

.btn-pause:hover,
.btn-generate:hover {
  border-color: #1a73e8;
  background: #f0f7ff;
}

.btn-delete:hover {
  border-color: #d32f2f;
  background: #ffebee;
}

.btn-resume {
  width: 100%;
  padding: 8px;
  background: #fbbc06;
  color: white;
  border: none;
  font-weight: 600;
}

.btn-resume:hover {
  background: #f9a825;
}

.paused-recurring {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 2px solid #e8eaed;
}

.paused-recurring h3 {
  color: #5f6368;
  font-size: 16px;
  margin-bottom: 12px;
}

@media (max-width: 768px) {
  .recurring-tasks {
    padding: 16px;
  }

  .control-row {
    grid-template-columns: 1fr;
  }

  .recurring-grid {
    grid-template-columns: 1fr;
  }

  .recurring-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
