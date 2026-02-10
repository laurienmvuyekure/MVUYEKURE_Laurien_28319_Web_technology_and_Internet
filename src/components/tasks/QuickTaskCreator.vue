<template>
  <div class="quick-task-creator">
    <div class="quick-task-header">
      <h3>⚡ Quick Task Creator</h3>
      <button 
        @click="toggleExpanded"
        class="btn-toggle"
        :aria-expanded="isExpanded"
      >
        {{ isExpanded ? '−' : '+' }}
      </button>
    </div>

    <div v-if="isExpanded" class="quick-task-form">
      <div class="form-row">
        <input 
          v-model="quickTask.title"
          type="text"
          placeholder="What needs to be done?"
          class="task-title-input"
          @keyup.enter="createQuickTask"
        />
      </div>

      <div class="form-row">
        <select v-model="quickTask.category" class="form-select">
          <option value="">Select category</option>
          <option value="Work">Work</option>
          <option value="Personal">Personal</option>
          <option value="Study">Study</option>
          <option value="Health">Health</option>
          <option value="Finance">Finance</option>
        </select>

        <select v-model="quickTask.priority" class="form-select">
          <option value="low">Low</option>
          <option value="medium">Medium</option>
          <option value="high">High</option>
        </select>

        <input 
          v-model.number="quickTask.duration"
          type="number"
          min="1"
          placeholder="Minutes"
          class="duration-input"
        />
      </div>

      <div class="form-row">
        <input 
          v-model="quickTask.dueDate"
          type="date"
          class="form-input"
        />
        <input 
          v-model="quickTask.dueTime"
          type="time"
          class="form-input"
        />
      </div>

      <div class="form-actions">
        <button @click="createQuickTask" class="btn-create" :disabled="!quickTask.title.trim()">
          ✓ Add Task
        </button>
        <button @click="toggleExpanded" class="btn-close">Cancel</button>
      </div>

      <div class="quick-tags">
        <span class="tag-label">Quick tags:</span>
        <button 
          v-for="tag in tags"
          :key="tag"
          @click="addTag(tag)"
          class="quick-tag"
        >
          {{ tag }}
        </button>
      </div>
    </div>

    <div v-if="!isExpanded && recentTasks.length > 0" class="recent-preview">
      <div class="preview-title">Recent:</div>
      <div class="recent-tasks">
        <button 
          v-for="task in recentTasks.slice(0, 3)"
          :key="task.id"
          @click="duplicateTask(task)"
          class="recent-task-btn"
          :title="`Create from: ${task.title}`"
        >
          {{ task.title.substring(0, 20) }}{{ task.title.length > 20 ? '...' : '' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, inject } from 'vue'

export default {
  name: 'QuickTaskCreator',
  emits: ['task-created'],
  setup(props, { emit }) {
    const isExpanded = ref(false)
    const tasks = inject('tasks', ref([]))
    
    const quickTask = ref({
      title: '',
      category: '',
      priority: 'medium',
      duration: 30,
      dueDate: '',
      dueTime: ''
    })

    const tags = ['Urgent', 'Review', 'Meeting', 'Follow-up', 'Brainstorm', 'Research']

    const recentTasks = computed(() => {
      return tasks.value.slice(-5).reverse()
    })

    const toggleExpanded = () => {
      isExpanded.value = !isExpanded.value
      if (!isExpanded.value) {
        resetForm()
      }
    }

    const resetForm = () => {
      quickTask.value = {
        title: '',
        category: '',
        priority: 'medium',
        duration: 30,
        dueDate: '',
        dueTime: ''
      }
    }

    const addTag = (tag) => {
      if (!quickTask.value.title.includes(tag)) {
        quickTask.value.title += ` #${tag}`
      }
    }

    const createQuickTask = () => {
      if (!quickTask.value.title.trim()) return

      const newTask = {
        id: Date.now(),
        title: quickTask.value.title,
        description: '',
        category: quickTask.value.category,
        priority: quickTask.value.priority,
        date: quickTask.value.dueDate || new Date().toISOString().split('T')[0],
        time: quickTask.value.dueTime || '09:00',
        duration: quickTask.value.duration,
        completed: false,
        createdAt: new Date().toISOString()
      }

      emit('task-created', newTask)
      resetForm()
      isExpanded.value = false
    }

    const duplicateTask = (task) => {
      quickTask.value.title = task.title
      quickTask.value.category = task.category
      quickTask.value.priority = task.priority
      quickTask.value.duration = task.duration
      isExpanded.value = true
    }

    return {
      isExpanded,
      quickTask,
      tags,
      recentTasks,
      toggleExpanded,
      resetForm,
      addTag,
      createQuickTask,
      duplicateTask
    }
  }
}
</script>

<style scoped>
.quick-task-creator {
  background: linear-gradient(135deg, #1a73e8 0%, #1557b0 100%);
  border-radius: 8px;
  padding: 12px;
  color: white;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(26, 115, 232, 0.2);
}

.quick-task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
}

.quick-task-header h3 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
}

.btn-toggle {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  width: 28px;
  height: 28px;
  border-radius: 4px;
  font-size: 18px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-toggle:hover {
  background: rgba(255, 255, 255, 0.3);
}

.quick-task-form {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  animation: slideDown 0.3s ease;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.form-row {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.task-title-input,
.form-select,
.duration-input,
.form-input {
  padding: 8px 12px;
  border: none;
  border-radius: 4px;
  font-size: 13px;
  font-family: inherit;
}

.task-title-input {
  flex: 1;
  min-width: 200px;
}

.form-select {
  flex: 0 1 120px;
}

.duration-input {
  flex: 0 1 80px;
}

.form-input {
  flex: 0 1 140px;
}

.task-title-input:focus,
.form-select:focus,
.duration-input:focus,
.form-input:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.5);
}

.form-actions {
  display: flex;
  gap: 8px;
}

.btn-create,
.btn-close {
  flex: 1;
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 13px;
}

.btn-create {
  background: white;
  color: #1a73e8;
}

.btn-create:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.btn-create:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-close {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

.btn-close:hover {
  background: rgba(255, 255, 255, 0.3);
}

.quick-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  align-items: center;
  padding: 8px 0;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
}

.tag-label {
  font-size: 11px;
  opacity: 0.8;
}

.quick-tag {
  padding: 4px 8px;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: 4px;
  color: white;
  font-size: 11px;
  cursor: pointer;
  transition: all 0.2s;
}

.quick-tag:hover {
  background: rgba(255, 255, 255, 0.3);
}

.recent-preview {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  padding: 8px 0;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
}

.preview-title {
  opacity: 0.8;
  font-size: 11px;
}

.recent-tasks {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.recent-task-btn {
  padding: 4px 8px;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: 4px;
  color: white;
  font-size: 11px;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.recent-task-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

@media (max-width: 768px) {
  .quick-task-form {
    gap: 10px;
  }

  .form-row {
    flex-direction: column;
  }

  .task-title-input,
  .form-select,
  .duration-input,
  .form-input {
    width: 100%;
  }

  .form-actions {
    flex-direction: column;
  }
}
</style>
