<template>
  <div class="stats-container">
    <div class="stat-card">
      <div class="stat-icon">📊</div>
      <div class="stat-content">
        <div class="stat-label">Total Tasks</div>
        <div class="stat-value">{{ totalTasks }}</div>
      </div>
    </div>
    
    <div class="stat-card">
      <div class="stat-icon">✅</div>
      <div class="stat-content">
        <div class="stat-label">Completed</div>
        <div class="stat-value">{{ completedTasks }}</div>
      </div>
    </div>
    
    <div class="stat-card">
      <div class="stat-icon">🔄</div>
      <div class="stat-content">
        <div class="stat-label">In Progress</div>
        <div class="stat-value">{{ inProgressTasks }}</div>
      </div>
    </div>
    
    <div class="stat-card">
      <div class="stat-icon">🎯</div>
      <div class="stat-content">
        <div class="stat-label">Completion %</div>
        <div class="stat-value">{{ completionPercentage }}%</div>
        <div class="stat-bar">
          <div class="stat-progress" :style="{ width: completionPercentage + '%' }"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'

export default {
  name: 'TaskStats',
  props: {
    tasks: {
      type: Array,
      default: () => []
    }
  },
  setup(props) {
    const totalTasks = computed(() => props.tasks.length)
    
    const completedTasks = computed(() => 
      props.tasks.filter(task => task.completed).length
    )
    
    const inProgressTasks = computed(() => 
      props.tasks.filter(task => !task.completed).length
    )
    
    const completionPercentage = computed(() => {
      if (totalTasks.value === 0) return 0
      return Math.round((completedTasks.value / totalTasks.value) * 100)
    })
    
    return {
      totalTasks,
      completedTasks,
      inProgressTasks,
      completionPercentage
    }
  }
}
</script>

<style scoped>
.stats-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 30px;
  padding: 20px;
  background: linear-gradient(135deg, #f5f6f7 0%, #e8eaed 100%);
  border-radius: 12px;
}

.stat-card {
  background: white;
  border: 1px solid #d0d2d7;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  align-items: flex-start;
  gap: 12px;
  transition: all 0.3s ease;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.stat-card:hover {
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.stat-icon {
  font-size: 28px;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 12px;
  color: #5a5f66;
  font-weight: 500;
  text-transform: uppercase;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1a73e8;
}

.stat-bar {
  height: 4px;
  background: #e0e0e0;
  border-radius: 2px;
  margin-top: 8px;
  overflow: hidden;
}

.stat-progress {
  height: 100%;
  background: linear-gradient(90deg, #4285f4, #34a853);
  border-radius: 2px;
  transition: width 0.3s ease;
}

@media (max-width: 768px) {
  .stats-container {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
    padding: 15px;
  }

  .stat-value {
    font-size: 20px;
  }
}
</style>
