<template>
  <div class="deadline-notifications">
    <h2>🔔 Notifications</h2>
    <p class="description">Track upcoming deadlines and task alerts</p>

    <div v-if="upcomingDeadlines.length > 0" class="deadline-timeline">
      <div class="deadline-items">
        <div 
          v-for="deadline in upcomingDeadlines" 
          :key="deadline.id"
          class="deadline-item"
        >
          <div class="deadline-dot" :class="deadline.urgency"></div>
          <div class="deadline-info">
            <span class="deadline-title">{{ deadline.title }}</span>
            <span class="deadline-due">{{ deadline.daysUntil }} days remaining</span>
          </div>
          <span class="deadline-date">{{ formatDate(deadline.date) }}</span>
        </div>
      </div>
    </div>

    <div v-else class="no-deadlines">
      <p>✅ No upcoming deadlines!</p>
    </div>
  </div>
</template>

<script>
import { ref, computed, inject, onMounted, onBeforeUnmount } from 'vue'

export default {
  name: 'DeadlineNotifications',
  setup() {
    const tasks = inject('tasks', ref([]))

    const formatDate = (dateStr) => {
      if (!dateStr) return 'N/A'
      const date = new Date(dateStr + 'T00:00:00')
      return date.toLocaleDateString('en-GB', { month: 'short', day: 'numeric' })
    }

    const upcomingDeadlines = computed(() => {
      const today = new Date()
      today.setHours(0, 0, 0, 0)

      return tasks.value
        .filter(t => !t.completed && t.date)
        .map(task => {
          const taskDate = new Date(task.date + 'T00:00:00')
          const daysUntil = Math.floor((taskDate - today) / (1000 * 60 * 60 * 24))
          return { ...task, daysUntil, urgency: getUrgency(daysUntil) }
        })
        .filter(t => t.daysUntil >= 0 && t.daysUntil <= 7)
        .sort((a, b) => a.daysUntil - b.daysUntil)
        .slice(0, 5)
    })

    const getUrgency = (daysUntil) => {
      if (daysUntil === 0) return 'critical'
      if (daysUntil === 1) return 'high'
      if (daysUntil <= 3) return 'medium'
      return 'low'
    }

    return {
      upcomingDeadlines,
      formatDate
    }
  }
}
</script>

<style scoped>
.deadline-notifications {
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

.deadline-timeline {
  margin-bottom: 16px;
}

.deadline-items {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.deadline-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: #f5f5f5;
  border-radius: 6px;
  align-items: center;
}

.deadline-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;

  &.critical { background: #d32f2f; }
  &.high { background: #f57c00; }
  &.medium { background: #fbc02d; }
  &.low { background: #388e3c; }
}

.deadline-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.deadline-title {
  font-size: 13px;
  color: #202124;
  font-weight: 600;
}

.deadline-due {
  font-size: 11px;
  color: #5f6368;
}

.deadline-date {
  font-size: 11px;
  color: #5f6368;
}

.no-deadlines {
  text-align: center;
  color: #388e3c;
  padding: 20px;
  background: #e8f5e9;
  border-radius: 8px;
}
</style>
