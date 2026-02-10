<template>
  <div class="advanced-dashboard">
    <div class="dashboard-header">
      <h1>📊 Advanced Dashboard</h1>
      <p class="subtitle">Complete overview of your tasks and productivity</p>
    </div>

    <!-- Quick Stats -->
    <div class="quick-stats">
      <div class="stat-box">
        <div class="stat-number">{{ totalTasks }}</div>
        <div class="stat-label">Total Tasks</div>
      </div>
      <div class="stat-box completed">
        <div class="stat-number">{{ completedTasks }}</div>
        <div class="stat-label">Completed</div>
      </div>
      <div class="stat-box pending">
        <div class="stat-number">{{ pendingTasks }}</div>
        <div class="stat-label">Pending</div>
      </div>
      <div class="stat-box">
        <div class="stat-number">{{ completionPercentage }}%</div>
        <div class="stat-label">Completion</div>
      </div>
    </div>

    <!-- Deadline Notifications -->
    <div class="dashboard-section">
      <DeadlineNotifications />
    </div>

    <!-- Priority Manager -->
    <div class="dashboard-section">
      <PriorityManager />
    </div>

    <!-- Productivity Goals -->
    <div class="dashboard-section">
      <ProductivityGoals />
    </div>

    <!-- Calendar View -->
    <div class="dashboard-section full-width">
      <CalendarView />
    </div>

    <!-- Task Templates -->
    <div class="dashboard-section">
      <TaskTemplates />
    </div>

    <!-- Report Generator -->
    <div class="dashboard-section full-width">
      <ReportGenerator />
    </div>
  </div>
</template>

<script>
import { computed, inject, ref } from 'vue'
import DeadlineNotifications from '../components/helpers/DeadlineNotifications.vue'
import PriorityManager from '../components/tasks/PriorityManager.vue'
import ProductivityGoals from '../components/tasks/ProductivityGoals.vue'
import ReportGenerator from '../components/reports/ReportGenerator.vue'
import TaskTemplates from '../components/tasks/TaskTemplates.vue'
import CalendarView from '../components/tasks/CalendarView.vue'

export default {
  name: 'AdvancedDashboard',
  components: {
    DeadlineNotifications,
    PriorityManager,
    ProductivityGoals,
    ReportGenerator,
    TaskTemplates,
    CalendarView
  },
  setup() {
    const tasks = inject('tasks', ref([]))

    const totalTasks = computed(() => tasks.value.length)
    const completedTasks = computed(() => tasks.value.filter(t => t.completed).length)
    const pendingTasks = computed(() => tasks.value.filter(t => !t.completed).length)
    const completionPercentage = computed(() => {
      if (totalTasks.value === 0) return 0
      return Math.round((completedTasks.value / totalTasks.value) * 100)
    })

    return {
      totalTasks,
      completedTasks,
      pendingTasks,
      completionPercentage
    }
  }
}
</script>

<style scoped>
.advanced-dashboard {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

.dashboard-header {
  margin-bottom: 30px;
}

.dashboard-header h1 {
  color: #1a73e8;
  font-size: 32px;
  margin-bottom: 8px;
}

.subtitle {
  color: #5f6368;
  font-size: 16px;
}

.quick-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 16px;
  margin-bottom: 30px;
}

.stat-box {
  background: white;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  &.completed {
    border-top: 4px solid #34a853;
  }

  &.pending {
    border-top: 4px solid #f57c00;
  }
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #1a73e8;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 12px;
  color: #5f6368;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.dashboard-section {
  margin-bottom: 30px;
}

.dashboard-section.full-width {
  grid-column: 1 / -1;
}

@media (max-width: 768px) {
  .advanced-dashboard {
    padding: 12px;
  }

  .dashboard-header h1 {
    font-size: 24px;
  }

  .quick-stats {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
