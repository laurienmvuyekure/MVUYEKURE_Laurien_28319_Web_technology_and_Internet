<template>
  <div class="task-dependencies">
    <div class="dependencies-header">
      <h3>🔗 Task Dependencies</h3>
      <button 
        @click="toggleAddDependency"
        class="btn-add-dependency"
        :aria-label="showAddForm ? 'Cancel' : 'Add dependency'"
      >
        {{ showAddForm ? '✕' : '➕' }}
      </button>
    </div>

    <div v-if="showAddForm" class="add-dependency-form">
      <div class="form-group">
        <label for="dependency-type">Dependency Type</label>
        <select v-model="newDependency.type" id="dependency-type" class="form-select">
          <option value="blockedBy">This task is blocked by...</option>
          <option value="blocks">This task blocks...</option>
          <option value="relatedTo">Related to...</option>
        </select>
      </div>

      <div class="form-group">
        <label for="dependency-task">Select Task</label>
        <select 
          v-model="newDependency.taskId"
          id="dependency-task"
          class="form-select"
        >
          <option value="">Choose a task...</option>
          <option 
            v-for="task in availableTasks"
            :key="task.id"
            :value="task.id"
          >
            {{ task.title }}
          </option>
        </select>
      </div>

      <div class="form-actions">
        <button 
          @click="addDependency"
          class="btn-add"
          :disabled="!newDependency.taskId"
        >
          Link Task
        </button>
        <button @click="toggleAddDependency" class="btn-cancel">Cancel</button>
      </div>
    </div>

    <div v-if="dependencies.length === 0 && !showAddForm" class="no-dependencies">
      <p>No task dependencies. Click ➕ to create task relationships.</p>
    </div>

    <div v-else class="dependencies-list">
      <div 
        v-for="dep in dependencies"
        :key="dep.id"
        class="dependency-item"
        :class="[dep.type, { blocked: isBlocked(dep) }]"
      >
        <div class="dependency-icon">{{ getDependencyIcon(dep.type) }}</div>
        
        <div class="dependency-info">
          <div class="dependency-label">
            {{ formatDependencyLabel(dep.type) }}
          </div>
          <div class="dependency-task">
            {{ getTaskTitle(dep.taskId) }}
          </div>
          <div v-if="isBlocked(dep)" class="blocking-status">
            ⛔ Task not completed
          </div>
          <div v-else class="unblocked-status">
            ✅ Requirements met
          </div>
        </div>

        <button
          @click="removeDependency(dep.id)"
          class="btn-remove"
          title="Remove dependency"
        >
          🗑️
        </button>
      </div>
    </div>

    <div v-if="blockedByCount > 0" class="blocking-warning">
      <div class="warning-content">
        <span class="warning-icon">⚠️</span>
        <div class="warning-text">
          <strong>{{ blockedByCount }} task{{ blockedByCount !== 1 ? 's' : '' }} blocking this one</strong>
          <p>Complete the prerequisite task{{ blockedByCount !== 1 ? 's' : '' }} first</p>
        </div>
      </div>
    </div>

    <div v-if="blockingCount > 0" class="dependent-warning">
      <div class="warning-content">
        <span class="warning-icon">📋</span>
        <div class="warning-text">
          <strong>{{ blockingCount }} task{{ blockingCount !== 1 ? 's' : '' }} depends on this</strong>
          <p>Other tasks are waiting for this to complete</p>
        </div>
      </div>
    </div>

    <div v-if="dependencies.length > 0" class="dependency-chain">
      <h4>📊 Dependency Chain</h4>
      <div class="chain-visualization">
        <div v-for="(dep, index) in dependencies" :key="dep.id" class="chain-item">
          <span class="chain-number">{{ index + 1 }}</span>
          <span class="chain-type">{{ formatDependencyLabel(dep.type) }}</span>
          <span class="chain-task">{{ getTaskTitle(dep.taskId) }}</span>
          <span :class="['chain-status', { completed: !isBlocked(dep) }]">
            {{ !isBlocked(dep) ? '✓' : '✗' }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, inject } from 'vue'

export default {
  name: 'TaskDependencies',
  props: {
    taskId: {
      type: [String, Number],
      required: true
    }
  },
  emits: ['update-dependencies'],
  setup(props, { emit }) {
    const tasks = inject('tasks', ref([]))
    const showAddForm = ref(false)
    const dependencies = ref([])

    const newDependency = ref({
      type: 'blockedBy',
      taskId: ''
    })

    const loadDependencies = () => {
      const saved = localStorage.getItem(`dependencies-${props.taskId}`)
      if (saved) {
        try {
          dependencies.value = JSON.parse(saved)
        } catch (e) {
          console.error('Failed to load dependencies:', e)
        }
      }
    }

    const saveDependencies = () => {
      localStorage.setItem(`dependencies-${props.taskId}`, JSON.stringify(dependencies.value))
      emit('update-dependencies', dependencies.value)
    }

    const availableTasks = computed(() => {
      return tasks.value.filter(t => t.id !== props.taskId)
    })

    const toggleAddDependency = () => {
      showAddForm.value = !showAddForm.value
      if (!showAddForm.value) {
        newDependency.value = { type: 'blockedBy', taskId: '' }
      }
    }

    const addDependency = () => {
      if (!newDependency.value.taskId) return

      dependencies.value.push({
        id: Date.now(),
        type: newDependency.value.type,
        taskId: newDependency.value.taskId,
        createdAt: new Date().toISOString()
      })

      saveDependencies()
      newDependency.value = { type: 'blockedBy', taskId: '' }
      showAddForm.value = false
    }

    const removeDependency = (id) => {
      const index = dependencies.value.findIndex(d => d.id === id)
      if (index > -1) {
        dependencies.value.splice(index, 1)
        saveDependencies()
      }
    }

    const getTaskTitle = (taskId) => {
      const task = tasks.value.find(t => t.id === taskId)
      return task ? task.title : 'Unknown Task'
    }

    const isBlocked = (dependency) => {
      if (dependency.type !== 'blockedBy') return false
      const blockerTask = tasks.value.find(t => t.id === dependency.taskId)
      return blockerTask && !blockerTask.completed
    }

    const getDependencyIcon = (type) => {
      const icons = {
        'blockedBy': '🚫',
        'blocks': '🔒',
        'relatedTo': '🔗'
      }
      return icons[type] || '🔗'
    }

    const formatDependencyLabel = (type) => {
      const labels = {
        'blockedBy': 'Blocked by',
        'blocks': 'Blocks',
        'relatedTo': 'Related to'
      }
      return labels[type] || type
    }

    const blockedByCount = computed(() => {
      return dependencies.value.filter(d => d.type === 'blockedBy' && isBlocked(d)).length
    })

    const blockingCount = computed(() => {
      return dependencies.value.filter(d => d.type === 'blocks').length
    })

    loadDependencies()

    return {
      showAddForm,
      newDependency,
      dependencies,
      availableTasks,
      blockedByCount,
      blockingCount,
      toggleAddDependency,
      addDependency,
      removeDependency,
      getTaskTitle,
      isBlocked,
      getDependencyIcon,
      formatDependencyLabel
    }
  }
}
</script>

<style scoped>
.task-dependencies {
  background: #fafafa;
  border-left: 4px solid #1a73e8;
  padding: 12px;
  border-radius: 4px;
  margin-top: 12px;
}

.dependencies-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.dependencies-header h3 {
  margin: 0;
  font-size: 14px;
  color: #202124;
}

.btn-add-dependency {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  padding: 4px;
  transition: transform 0.2s;
}

.btn-add-dependency:hover {
  transform: scale(1.2);
}

.add-dependency-form {
  background: white;
  border: 1px solid #e8eaed;
  border-radius: 4px;
  padding: 12px;
  margin-bottom: 12px;
}

.form-group {
  margin-bottom: 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.form-group label {
  font-size: 12px;
  font-weight: 600;
  color: #5f6368;
}

.form-select {
  padding: 8px 12px;
  border: 1px solid #e8eaed;
  border-radius: 4px;
  font-size: 12px;
  font-family: inherit;
  cursor: pointer;
}

.form-select:focus {
  outline: none;
  border-color: #1a73e8;
  box-shadow: 0 0 0 2px rgba(26, 115, 232, 0.1);
}

.form-actions {
  display: flex;
  gap: 8px;
}

.btn-add,
.btn-cancel {
  flex: 1;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-add {
  background: #1a73e8;
  color: white;
}

.btn-add:hover:not(:disabled) {
  background: #1557b0;
}

.btn-add:disabled {
  background: #d0d2d7;
  cursor: not-allowed;
}

.btn-cancel {
  background: white;
  border: 1px solid #e8eaed;
  color: #5f6368;
}

.btn-cancel:hover {
  background: #f5f5f5;
}

.no-dependencies {
  padding: 12px;
  text-align: center;
  color: #5f6368;
  font-size: 12px;
  font-style: italic;
}

.dependencies-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.dependency-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 8px;
  background: white;
  border-radius: 4px;
  border: 1px solid #e8eaed;
  border-left: 3px solid #1a73e8;
  transition: all 0.2s;
}

.dependency-item.blocks {
  border-left-color: #fbbc06;
}

.dependency-item.relatedTo {
  border-left-color: #34a853;
}

.dependency-item.blocked {
  background: #ffebee;
  border-color: #d32f2f;
}

.dependency-item:hover {
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.dependency-icon {
  font-size: 16px;
  flex-shrink: 0;
  margin-top: 2px;
}

.dependency-info {
  flex: 1;
  min-width: 0;
}

.dependency-label {
  font-size: 11px;
  font-weight: 600;
  color: #5f6368;
  text-transform: uppercase;
}

.dependency-task {
  font-size: 12px;
  font-weight: 600;
  color: #202124;
  word-break: break-word;
  margin: 2px 0;
}

.blocking-status {
  font-size: 11px;
  color: #d32f2f;
  font-weight: 600;
}

.unblocked-status {
  font-size: 11px;
  color: #34a853;
  font-weight: 600;
}

.btn-remove {
  background: none;
  border: none;
  font-size: 14px;
  cursor: pointer;
  padding: 2px;
  opacity: 0.6;
  transition: opacity 0.2s;
  flex-shrink: 0;
}

.btn-remove:hover {
  opacity: 1;
}

.blocking-warning,
.dependent-warning {
  margin-top: 12px;
  padding: 8px 12px;
  border-radius: 4px;
  border-left: 3px solid;
  background: #fff3e0;
  border-left-color: #fbbc06;
}

.dependent-warning {
  background: #e3f2fd;
  border-left-color: #1a73e8;
}

.warning-content {
  display: flex;
  gap: 8px;
  align-items: flex-start;
}

.warning-icon {
  font-size: 14px;
  flex-shrink: 0;
}

.warning-text {
  font-size: 11px;
  color: #5f6368;
}

.warning-text strong {
  display: block;
  color: #202124;
  margin-bottom: 2px;
}

.warning-text p {
  margin: 0;
}

.dependency-chain {
  margin-top: 12px;
  padding: 8px;
  background: white;
  border-radius: 4px;
  border: 1px solid #e8eaed;
}

.dependency-chain h4 {
  margin: 0 0 8px 0;
  font-size: 12px;
  color: #202124;
}

.chain-visualization {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.chain-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  padding: 4px;
  background: #f9f9f9;
  border-radius: 3px;
}

.chain-number {
  background: #1a73e8;
  color: white;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 10px;
  flex-shrink: 0;
}

.chain-type {
  font-weight: 600;
  color: #5f6368;
  min-width: 60px;
}

.chain-task {
  flex: 1;
  color: #202124;
  word-break: break-word;
}

.chain-status {
  flex-shrink: 0;
  font-weight: 700;
  color: #d32f2f;
}

.chain-status.completed {
  color: #34a853;
}

@media (max-width: 768px) {
  .dependency-item {
    flex-wrap: wrap;
  }

  .dependency-info {
    width: 100%;
  }
}
</style>
