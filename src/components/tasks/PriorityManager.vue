<template>
  <div class="priority-manager">
    <h2>⚡ Priority Manager</h2>
    <p class="description">View and manage tasks by priority level</p>

    <div class="priority-filters">
      <button 
        v-for="level in priorityLevels" 
        :key="level.value"
        @click="selectedPriority = selectedPriority === level.value ? null : level.value"
        :class="['priority-btn', level.value, { active: selectedPriority === level.value }]"
      >
        {{ level.icon }} {{ level.label }}
        <span class="count">({{ countByPriority(level.value) }})</span>
      </button>
      <button 
        @click="selectedPriority = null"
        class="priority-btn all"
        :class="{ active: selectedPriority === null }"
      >
        📊 All Tasks ({{ tasks.length }})
      </button>
    </div>

    <div class="priority-stats">
      <div class="stat-card critical" v-if="countByPriority('critical') > 0">
        <div class="stat-icon">🔴</div>
        <div class="stat-content">
          <div class="stat-label">Critical Tasks</div>
          <div class="stat-value">{{ countByPriority('critical') }}</div>
        </div>
      </div>

      <div class="stat-card high" v-if="countByPriority('high') > 0">
        <div class="stat-icon">🟠</div>
        <div class="stat-content">
          <div class="stat-label">High Priority</div>
          <div class="stat-value">{{ countByPriority('high') }}</div>
        </div>
      </div>

      <div class="stat-card medium" v-if="countByPriority('medium') > 0">
        <div class="stat-icon">🟡</div>
        <div class="stat-content">
          <div class="stat-label">Medium Priority</div>
          <div class="stat-value">{{ countByPriority('medium') }}</div>
        </div>
      </div>

      <div class="stat-card low" v-if="countByPriority('low') > 0">
        <div class="stat-icon">🟢</div>
        <div class="stat-content">
          <div class="stat-label">Low Priority</div>
          <div class="stat-value">{{ countByPriority('low') }}</div>
        </div>
      </div>
    </div>

    <div class="priority-tasks">
      <h3 v-if="selectedPriority">{{ getPriorityLabel(selectedPriority) }} Tasks</h3>
      <h3 v-else>All Tasks by Priority</h3>

      <div v-if="filteredTasks.length === 0" class="no-tasks">
        <p>No tasks at this priority level.</p>
      </div>

      <div v-else class="tasks-grid">
        <div 
          v-for="task in filteredTasks" 
          :key="task.id"
          class="task-card"
        >
          <div class="task-header">
            <div class="task-title">{{ task.title }}</div>
            <div class="priority-badge" :class="getPriorityValue(task.priority)">
              {{ getPriorityIcon(task.priority) }}
            </div>
          </div>
          <div class="task-meta">
            <span v-if="task.date" class="meta-item">📅 {{ formatDate(task.date) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, inject } from 'vue'

export default {
  name: 'PriorityManager',
  setup() {
    const tasks = inject('tasks', ref([]))
    const selectedPriority = ref(null)

    const priorityLevels = [
      { value: 'critical', label: 'Critical', icon: '🔴' },
      { value: 'high', label: 'High', icon: '🟠' },
      { value: 'medium', label: 'Medium', icon: '🟡' },
      { value: 'low', label: 'Low', icon: '🟢' }
    ]

    const countByPriority = (priority) => {
      return tasks.value.filter(t => t.priority === priority).length
    }

    const getPriorityLabel = (priority) => {
      const level = priorityLevels.find(l => l.value === priority)
      return level ? level.label : 'Unknown'
    }

    const getPriorityIcon = (priority) => {
      const level = priorityLevels.find(l => l.value === priority)
      return level ? level.icon : '❓'
    }

    const getPriorityValue = (priority) => {
      return priority || 'medium'
    }

    const formatDate = (dateStr) => {
      if (!dateStr) return 'N/A'
      const date = new Date(dateStr + 'T00:00:00')
      return date.toLocaleDateString('en-GB', { weekday: 'short', day: 'numeric', month: 'short' })
    }

    const filteredTasks = computed(() => {
      if (!selectedPriority.value) {
        return tasks.value.sort((a, b) => {
          const priorityOrder = { critical: 0, high: 1, medium: 2, low: 3 }
          return priorityOrder[a.priority || 'medium'] - priorityOrder[b.priority || 'medium']
        })
      }
      return tasks.value.filter(t => (t.priority || 'medium') === selectedPriority.value)
    })

    return {
      tasks,
      selectedPriority,
      priorityLevels,
      countByPriority,
      getPriorityLabel,
      getPriorityIcon,
      getPriorityValue,
      filteredTasks,
      formatDate
    }
  }
}
</script>

<style scoped>
.priority-manager {
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

.priority-filters {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.priority-btn {
  padding: 8px 12px;
  border: 1px solid #e8eaed;
  border-radius: 20px;
  background: white;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s;

  &.active {
    background: #1a73e8;
    color: white;
    border-color: #1a73e8;
  }
}

.priority-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 12px;
  margin-bottom: 20px;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 12px;
  display: flex;
  gap: 8px;
  border-left: 4px solid #e8eaed;

  &.critical { border-left-color: #d32f2f; }
  &.high { border-left-color: #f57c00; }
  &.medium { border-left-color: #fbc02d; }
  &.low { border-left-color: #388e3c; }
}

.stat-icon {
  font-size: 20px;
}

.stat-label {
  font-size: 12px;
  color: #5f6368;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #1a73e8;
}

.priority-tasks {
  background: #f5f5f5;
  border-radius: 8px;
  padding: 16px;
}

.priority-tasks h3 {
  margin-bottom: 12px;
  color: #202124;
}

.no-tasks {
  text-align: center;
  color: #5f6368;
  padding: 20px;
}

.tasks-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 12px;
}

.task-card {
  background: white;
  border-radius: 6px;
  padding: 12px;
  border-left: 4px solid #e8eaed;
}

.task-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.task-title {
  font-weight: 600;
  font-size: 13px;
  color: #202124;
}

.priority-badge {
  font-size: 14px;
}

.task-meta {
  font-size: 11px;
  color: #5f6368;
}
</style>
