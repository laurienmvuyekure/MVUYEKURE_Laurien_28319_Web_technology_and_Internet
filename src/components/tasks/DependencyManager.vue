<template>
  <div class="dependency-manager">
    <div class="manager-header">
      <h1>🔗 Task Dependencies Manager</h1>
      <p class="subtitle">Visualize and manage task relationships and blocking dependencies</p>
    </div>

    <div class="manager-controls">
      <div class="control-group">
        <label for="filter-type">Filter by Type</label>
        <select v-model="filterType" id="filter-type" class="select-input">
          <option value="">All Dependencies</option>
          <option value="blockedBy">Blocked By</option>
          <option value="blocks">Blocks</option>
          <option value="relatedTo">Related To</option>
        </select>
      </div>

      <div class="control-group">
        <label for="filter-status">Filter by Status</label>
        <select v-model="filterStatus" id="filter-status" class="select-input">
          <option value="">All</option>
          <option value="blocked">Blocked (incomplete prerequisites)</option>
          <option value="unblocked">Unblocked (ready to work)</option>
        </select>
      </div>

      <button @click="refreshDependencies" class="btn-refresh">
        🔄 Refresh
      </button>
    </div>

    <div class="manager-content">
      <div class="section blocking-tasks">
        <h2>🚫 Blocking Tasks</h2>
        <p class="section-description">Tasks that are blocking other tasks from being started</p>
        
        <div v-if="blockingTasks.length === 0" class="no-items">
          <p>No blocking tasks</p>
        </div>

        <div v-else class="tasks-grid">
          <div 
            v-for="task in blockingTasks"
            :key="task.id"
            class="task-card blocking"
          >
            <div class="card-header">
              <h3>{{ task.title }}</h3>
              <span class="blocked-count">⛔ {{ task.blockedCount }}</span>
            </div>
            <p v-if="task.description" class="card-description">{{ task.description }}</p>
            <div class="card-meta">
              <span class="status" :class="{ completed: task.completed }">
                {{ task.completed ? '✓ Complete' : '⏳ In Progress' }}
              </span>
              <span class="category">{{ task.category }}</span>
            </div>
            <div class="depends-on">
              <strong>Blocks {{ task.blockedCount }} task{{ task.blockedCount !== 1 ? 's' : '' }}:</strong>
              <ul class="depends-list">
                <li v-for="depId in getBlockedTaskIds(task.id)" :key="depId">
                  • {{ getTaskTitle(depId) }}
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      <div class="section blocked-tasks">
        <h2>⏸️ Blocked Tasks</h2>
        <p class="section-description">Tasks that are waiting for prerequisites to be completed</p>
        
        <div v-if="blockedTasks.length === 0" class="no-items">
          <p>No blocked tasks. All tasks are ready to work on!</p>
        </div>

        <div v-else class="tasks-grid">
          <div 
            v-for="task in blockedTasks"
            :key="task.id"
            class="task-card blocked"
          >
            <div class="card-header">
              <h3>{{ task.title }}</h3>
              <span class="blocker-count">🚫 {{ task.blockerCount }}</span>
            </div>
            <p v-if="task.description" class="card-description">{{ task.description }}</p>
            <div class="card-meta">
              <span class="category">{{ task.category }}</span>
              <span class="priority" :class="task.priority">{{ task.priority }}</span>
            </div>
            <div class="blocked-by">
              <strong>Blocked by {{ task.blockerCount }} task{{ task.blockerCount !== 1 ? 's' : '' }}:</strong>
              <ul class="blockers-list">
                <li v-for="depId in getBlockedByTaskIds(task.id)" :key="depId">
                  <span class="blocker-status">
                    {{ isTaskCompleted(depId) ? '✓' : '✗' }}
                  </span>
                  {{ getTaskTitle(depId) }}
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      <div class="section related-tasks">
        <h2>🔗 Related Tasks</h2>
        <p class="section-description">Tasks that are related but don't have strict blocking dependencies</p>
        
        <div v-if="relatedTasks.length === 0" class="no-items">
          <p>No related tasks</p>
        </div>

        <div v-else class="tasks-grid">
          <div 
            v-for="task in relatedTasks"
            :key="task.id"
            class="task-card related"
          >
            <div class="card-header">
              <h3>{{ task.title }}</h3>
            </div>
            <p v-if="task.description" class="card-description">{{ task.description }}</p>
            <div class="card-meta">
              <span class="category">{{ task.category }}</span>
              <span class="status" :class="{ completed: task.completed }">
                {{ task.completed ? '✓' : '○' }}
              </span>
            </div>
            <div class="related-list">
              <strong>Related to:</strong>
              <ul>
                <li v-for="depId in getRelatedTaskIds(task.id)" :key="depId">
                  • {{ getTaskTitle(depId) }}
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="dependency-stats">
      <div class="stat">
        <span class="stat-icon">🔗</span>
        <span class="stat-label">Total Dependencies</span>
        <span class="stat-value">{{ totalDependencies }}</span>
      </div>
      <div class="stat">
        <span class="stat-icon">🚫</span>
        <span class="stat-label">Blocked Tasks</span>
        <span class="stat-value">{{ blockedTasks.length }}</span>
      </div>
      <div class="stat">
        <span class="stat-icon">✓</span>
        <span class="stat-label">Ready to Work</span>
        <span class="stat-value">{{ readyToWork }}</span>
      </div>
      <div class="stat">
        <span class="stat-icon">⏸️</span>
        <span class="stat-label">In Progress</span>
        <span class="stat-value">{{ inProgress }}</span>
      </div>
    </div>

    <div class="critical-path">
      <h2>🎯 Critical Path</h2>
      <p class="section-description">Longest chain of dependent tasks that determines minimum completion time</p>
      
      <div v-if="criticalPath.length === 0" class="no-items">
        <p>No dependent task chains</p>
      </div>

      <div v-else class="path-visualization">
        <div class="path-chain">
          <div 
            v-for="(taskId, index) in criticalPath"
            :key="taskId"
            class="path-step"
          >
            <div class="step-number">{{ index + 1 }}</div>
            <div class="step-task">{{ getTaskTitle(taskId) }}</div>
            <div v-if="index < criticalPath.length - 1" class="step-arrow">→</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, inject } from 'vue'

export default {
  name: 'DependencyManager',
  setup() {
    const tasks = inject('tasks', ref([]))
    const filterType = ref('')
    const filterStatus = ref('')
    const allDependencies = ref([])

    const loadAllDependencies = () => {
      allDependencies.value = []
      tasks.value.forEach(task => {
        const saved = localStorage.getItem(`dependencies-${task.id}`)
        if (saved) {
          try {
            const deps = JSON.parse(saved)
            deps.forEach(dep => {
              allDependencies.value.push({
                ...dep,
                taskId: task.id,
                relatedTaskId: dep.taskId
              })
            })
          } catch (e) {
            console.error(`Failed to load dependencies for task ${task.id}:`, e)
          }
        }
      })
    }

    const getTaskTitle = (taskId) => {
      const task = tasks.value.find(t => t.id === taskId)
      return task ? task.title : 'Unknown Task'
    }

    const isTaskCompleted = (taskId) => {
      const task = tasks.value.find(t => t.id === taskId)
      return task ? task.completed : false
    }

    const getBlockedTaskIds = (taskId) => {
      return allDependencies.value
        .filter(d => d.relatedTaskId === taskId && d.type === 'blocks')
        .map(d => d.taskId)
    }

    const getBlockedByTaskIds = (taskId) => {
      return allDependencies.value
        .filter(d => d.taskId === taskId && d.type === 'blockedBy')
        .map(d => d.relatedTaskId)
    }

    const getRelatedTaskIds = (taskId) => {
      return allDependencies.value
        .filter(d => (d.taskId === taskId || d.relatedTaskId === taskId) && d.type === 'relatedTo')
        .map(d => d.taskId === taskId ? d.relatedTaskId : d.taskId)
    }

    const blockingTasks = computed(() => {
      return tasks.value
        .filter(task => {
          const blockedCount = getBlockedTaskIds(task.id).length
          return blockedCount > 0
        })
        .map(task => ({
          ...task,
          blockedCount: getBlockedTaskIds(task.id).length
        }))
    })

    const blockedTasks = computed(() => {
      return tasks.value
        .filter(task => {
          const blockerIds = getBlockedByTaskIds(task.id)
          return blockerIds.length > 0
        })
        .map(task => {
          const blockerIds = getBlockedByTaskIds(task.id)
          return {
            ...task,
            blockerCount: blockerIds.length,
            isBlocked: blockerIds.some(bid => !isTaskCompleted(bid))
          }
        })
        .filter(task => !filterStatus.value || (filterStatus.value === 'blocked' ? task.isBlocked : !task.isBlocked))
    })

    const relatedTasks = computed(() => {
      return tasks.value.filter(task => getRelatedTaskIds(task.id).length > 0)
    })

    const totalDependencies = computed(() => {
      return allDependencies.value.length
    })

    const readyToWork = computed(() => {
      return tasks.value.filter(task => {
        const blockerIds = getBlockedByTaskIds(task.id)
        return blockerIds.length === 0 && !task.completed
      }).length
    })

    const inProgress = computed(() => {
      return tasks.value.filter(task => !task.completed && task.id).length
    })

    const criticalPath = computed(() => {
      const paths = []
      const visited = new Set()

      const findPath = (taskId, path = []) => {
        if (visited.has(taskId)) return
        visited.add(taskId)

        path.push(taskId)
        const blockerIds = getBlockedByTaskIds(taskId)

        if (blockerIds.length === 0) {
          paths.push([...path])
        } else {
          blockerIds.forEach(blockerId => {
            findPath(blockerId, [...path])
          })
        }
      }

      tasks.value.forEach(task => {
        findPath(task.id)
      })

      return paths.length > 0 ? paths.reduce((longest, current) => 
        current.length > longest.length ? current : longest
      ) : []
    })

    const refreshDependencies = () => {
      loadAllDependencies()
    }

    loadAllDependencies()

    return {
      filterType,
      filterStatus,
      blockingTasks,
      blockedTasks,
      relatedTasks,
      totalDependencies,
      readyToWork,
      inProgress,
      criticalPath,
      getTaskTitle,
      isTaskCompleted,
      getBlockedTaskIds,
      getBlockedByTaskIds,
      getRelatedTaskIds,
      refreshDependencies
    }
  }
}
</script>

<style scoped>
.dependency-manager {
  background: white;
  border-radius: 12px;
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.manager-header {
  margin-bottom: 24px;
}

.manager-header h1 {
  color: #1a73e8;
  font-size: 32px;
  margin-bottom: 8px;
}

.subtitle {
  color: #5f6368;
  margin: 0;
}

.manager-controls {
  display: flex;
  gap: 12px;
  margin-bottom: 32px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}

.control-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.control-group label {
  font-size: 12px;
  font-weight: 600;
  color: #5f6368;
}

.select-input {
  padding: 8px 12px;
  border: 1px solid #e8eaed;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  font-family: inherit;
}

.select-input:focus {
  outline: none;
  border-color: #1a73e8;
}

.btn-refresh {
  padding: 8px 16px;
  background: #1a73e8;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  align-self: flex-end;
  transition: all 0.2s;
}

.btn-refresh:hover {
  background: #1557b0;
}

.manager-content {
  display: grid;
  gap: 32px;
  margin-bottom: 32px;
}

.section {
  border: 1px solid #e8eaed;
  border-radius: 8px;
  padding: 20px;
  background: #f9f9f9;
}

.section h2 {
  margin-top: 0;
  margin-bottom: 8px;
  color: #202124;
  font-size: 20px;
}

.section-description {
  color: #5f6368;
  font-size: 13px;
  margin-bottom: 16px;
}

.no-items {
  text-align: center;
  padding: 40px 20px;
  color: #5f6368;
  background: white;
  border-radius: 6px;
}

.tasks-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.task-card {
  background: white;
  border: 1px solid #e8eaed;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.2s;
  border-left: 4px solid;
}

.task-card.blocking {
  border-left-color: #d32f2f;
}

.task-card.blocking:hover {
  border-left-color: #c62828;
  box-shadow: 0 4px 12px rgba(211, 47, 47, 0.2);
}

.task-card.blocked {
  border-left-color: #fbbc06;
}

.task-card.blocked:hover {
  border-left-color: #f9a825;
  box-shadow: 0 4px 12px rgba(251, 188, 6, 0.2);
}

.task-card.related {
  border-left-color: #34a853;
}

.task-card.related:hover {
  border-left-color: #2d7a4a;
  box-shadow: 0 4px 12px rgba(52, 168, 83, 0.2);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 8px;
}

.card-header h3 {
  margin: 0;
  color: #202124;
  font-size: 16px;
  flex: 1;
}

.blocked-count,
.blocker-count {
  font-size: 12px;
  font-weight: 700;
  white-space: nowrap;
  padding: 4px 8px;
  background: #ffebee;
  color: #d32f2f;
  border-radius: 4px;
}

.blocker-count {
  background: #fff3e0;
  color: #fbbc06;
}

.card-description {
  color: #5f6368;
  font-size: 13px;
  margin: 8px 0;
}

.card-meta {
  display: flex;
  gap: 6px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.status,
.category,
.priority {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 4px;
  background: #e8f0fe;
  color: #1a73e8;
}

.status.completed {
  background: #e6f4ea;
  color: #34a853;
}

.category {
  background: #f3e5f5;
  color: #6a1b9a;
}

.priority {
  background: #fff3e0;
  color: #fbbc06;
}

.priority.high {
  background: #ffebee;
  color: #d32f2f;
}

.depends-on,
.blocked-by,
.related-list {
  font-size: 12px;
  padding: 8px;
  background: #f5f5f5;
  border-radius: 4px;
  margin-top: 8px;
}

.depends-on strong,
.blocked-by strong,
.related-list strong {
  display: block;
  margin-bottom: 4px;
  color: #202124;
}

.depends-list,
.blockers-list {
  margin: 0;
  padding-left: 16px;
  color: #5f6368;
}

.depends-list li,
.blockers-list li {
  margin: 4px 0;
}

.blocker-status {
  margin-right: 4px;
  font-weight: 700;
}

.dependency-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 12px;
  margin-bottom: 32px;
}

.stat {
  background: #f5f7fa;
  border: 1px solid #e8eaed;
  padding: 16px;
  border-radius: 8px;
  text-align: center;
}

.stat-icon {
  display: block;
  font-size: 24px;
  margin-bottom: 8px;
}

.stat-label {
  display: block;
  font-size: 12px;
  color: #5f6368;
  margin-bottom: 4px;
}

.stat-value {
  display: block;
  font-size: 28px;
  font-weight: 700;
  color: #1a73e8;
}

.critical-path {
  border: 2px solid #fbbc06;
  border-radius: 8px;
  padding: 20px;
  background: #fffbf0;
}

.critical-path h2 {
  margin-top: 0;
  margin-bottom: 8px;
  color: #f57c00;
}

.path-visualization {
  background: white;
  border-radius: 6px;
  padding: 16px;
}

.path-chain {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.path-step {
  display: flex;
  align-items: center;
  gap: 8px;
}

.step-number {
  width: 30px;
  height: 30px;
  background: #fbbc06;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 12px;
  flex-shrink: 0;
}

.step-task {
  min-width: 150px;
  padding: 8px 12px;
  background: #e8f5e9;
  color: #34a853;
  border-radius: 4px;
  font-weight: 600;
  font-size: 12px;
}

.step-arrow {
  font-size: 20px;
  color: #fbbc06;
  font-weight: 700;
}

@media (max-width: 768px) {
  .dependency-manager {
    padding: 12px;
  }

  .manager-controls {
    flex-direction: column;
  }

  .tasks-grid {
    grid-template-columns: 1fr;
  }

  .path-chain {
    flex-direction: column;
  }

  .step-arrow {
    transform: rotate(90deg);
  }
}
</style>
