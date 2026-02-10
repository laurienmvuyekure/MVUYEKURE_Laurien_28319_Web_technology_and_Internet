<template>
  <div class="productivity-goals">
    <h2>🎯 Productivity Goals</h2>
    <p class="description">Set and track your productivity targets</p>

    <div class="goals-setup">
      <div class="control-group">
        <label>Goal Name</label>
        <input v-model="newGoal.name" type="text" placeholder="e.g., Complete 5 tasks daily" class="input-field" />
      </div>

      <div class="control-group">
        <label>Target</label>
        <input v-model.number="newGoal.target" type="number" placeholder="e.g., 5" class="input-field" />
      </div>

      <div class="control-group">
        <label>Period</label>
        <select v-model="newGoal.period" class="select-field">
          <option value="daily">Daily</option>
          <option value="weekly">Weekly</option>
        </select>
      </div>

      <button @click="addGoal" class="btn-add" :disabled="!newGoal.name || !newGoal.target">
        ➕ Add Goal
      </button>
    </div>

    <div class="goals-list">
      <h3>Active Goals</h3>

      <div v-if="goals.length === 0" class="no-goals">
        <p>No goals yet. Create one!</p>
      </div>

      <div v-else class="goals-grid">
        <div v-for="goal in goals" :key="goal.id" class="goal-card">
          <div class="goal-header">
            <div class="goal-title">{{ goal.name }}</div>
            <span class="goal-period">{{ goal.period }}</span>
          </div>

          <div class="goal-progress">
            <div class="progress-bar">
              <div 
                class="progress-fill" 
                :style="{ width: getProgressPercent(goal) + '%' }"
              ></div>
            </div>
            <div class="progress-text">
              {{ getProgressPercent(goal) }}%
            </div>
          </div>

          <button @click="removeGoal(goal.id)" class="btn-remove">🗑️ Remove</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, inject } from 'vue'

export default {
  name: 'ProductivityGoals',
  setup() {
    const tasks = inject('tasks', ref([]))
    const goals = ref([])
    const newGoal = ref({
      name: '',
      target: 5,
      period: 'daily'
    })

    const loadGoals = () => {
      const saved = localStorage.getItem('goals')
      if (saved) {
        try {
          goals.value = JSON.parse(saved)
        } catch (e) {
          console.error('Failed to load goals:', e)
        }
      }
    }

    const saveGoals = () => {
      localStorage.setItem('goals', JSON.stringify(goals.value))
    }

    const addGoal = () => {
      goals.value.push({
        id: Date.now(),
        ...newGoal.value,
        createdAt: new Date()
      })
      saveGoals()
      newGoal.value = { name: '', target: 5, period: 'daily' }
    }

    const removeGoal = (id) => {
      goals.value = goals.value.filter(g => g.id !== id)
      saveGoals()
    }

    const getCurrentProgress = (goal) => {
      const today = new Date()
      today.setHours(0, 0, 0, 0)

      if (goal.period === 'daily') {
        return tasks.value.filter(t => {
          const taskDate = new Date(t.date || new Date())
          taskDate.setHours(0, 0, 0, 0)
          return t.completed && taskDate.getTime() === today.getTime()
        }).length
      } else if (goal.period === 'weekly') {
        const weekStart = new Date(today)
        weekStart.setDate(today.getDate() - today.getDay())
        return tasks.value.filter(t => {
          const taskDate = new Date(t.date || new Date())
          taskDate.setHours(0, 0, 0, 0)
          return t.completed && taskDate >= weekStart
        }).length
      }
      return 0
    }

    const getProgressPercent = (goal) => {
      const current = getCurrentProgress(goal)
      return Math.min(Math.round((current / goal.target) * 100), 100)
    }

    loadGoals()

    return {
      goals,
      newGoal,
      addGoal,
      removeGoal,
      getProgressPercent
    }
  }
}
</script>

<style scoped>
.productivity-goals {
  padding: 20px;
  background: white;
  border-radius: 12px;
}

h2 {
  color: #1a73e8;
  margin-bottom: 8px;
}

.description {
  color: #5f6368;
  margin-bottom: 16px;
  font-size: 14px;
}

.goals-setup {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 12px;
  margin-bottom: 20px;
  padding: 16px;
  background: #f5f5f5;
  border-radius: 8px;
}

.control-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.control-group label {
  font-size: 12px;
  font-weight: 600;
  color: #202124;
}

.input-field,
.select-field {
  padding: 8px;
  border: 1px solid #e8eaed;
  border-radius: 4px;
  font-size: 13px;
}

.btn-add {
  padding: 8px 16px;
  background: #1a73e8;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  align-self: flex-end;
  font-weight: 600;

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

.goals-list h3 {
  margin-bottom: 12px;
  color: #202124;
}

.no-goals {
  text-align: center;
  color: #5f6368;
  padding: 20px;
  background: #f5f5f5;
  border-radius: 8px;
}

.goals-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
}

.goal-card {
  background: green;
  border-radius: 8px;
  padding: 12px;
  border: 1px solid #e8eaed;
}

.goal-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.goal-title {
  font-weight: 600;
  color: #202124;
  font-size: 13px;
}

.goal-period {
  background: #e3f2fd;
  color: #1a73e8;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 10px;
}

.goal-progress {
  margin-bottom: 12px;
}

.progress-bar {
  height: 6px;
  background: #e8eaed;
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 4px;
}

.progress-fill {
  height: 100%;
  background: #1a73e8;
}

.progress-text {
  font-size: 11px;
  color: #1a73e8;
  font-weight: 600;
}

.btn-remove {
  width: 100%;
  padding: 6px;
  background: #f5f5f5;
  border: 1px solid #e8eaed;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;

  &:hover {
    background: #ffebee;
    border-color: #d32f2f;
    color: #d32f2f;
  }
}
</style>
