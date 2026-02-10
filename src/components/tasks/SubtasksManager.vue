<template>
  <div class="subtasks-manager">
    <div class="subtasks-header">
      <h3>📋 Subtasks</h3>
      <button 
        @click="toggleAddForm" 
        class="btn-add-subtask"
        :aria-label="showAddForm ? 'Cancel' : 'Add subtask'"
      >
        {{ showAddForm ? '✕' : '➕' }}
      </button>
    </div>

    <div v-if="showAddForm" class="add-subtask-form">
      <input 
        v-model="newSubtask.title"
        type="text"
        placeholder="Enter subtask title..."
        class="subtask-input"
        @keyup.enter="addSubtask"
      />
      <div class="form-actions">
        <button @click="addSubtask" class="btn-create" :disabled="!newSubtask.title.trim()">
          Add
        </button>
        <button @click="toggleAddForm" class="btn-cancel">Cancel</button>
      </div>
    </div>

    <div v-if="subtasks.length === 0" class="no-subtasks">
      <p>No subtasks yet. Break down your task into smaller steps!</p>
    </div>

    <div v-else class="subtasks-list">
      <div 
        v-for="subtask in subtasks"
        :key="subtask.id"
        class="subtask-item"
        :class="{ completed: subtask.completed }"
      >
        <input
          type="checkbox"
          :id="`subtask-${subtask.id}`"
          :checked="subtask.completed"
          @change="toggleSubtask(subtask.id)"
          class="subtask-checkbox"
        />
        <label :for="`subtask-${subtask.id}`" class="subtask-label">
          <span class="subtask-title">{{ subtask.title }}</span>
          <span v-if="subtask.dueDate" class="subtask-due">{{ formatDate(subtask.dueDate) }}</span>
        </label>
        <button 
          @click="deleteSubtask(subtask.id)"
          class="btn-delete-subtask"
          aria-label="Delete subtask"
        >
          🗑️
        </button>
      </div>
    </div>

    <div v-if="subtasks.length > 0" class="subtasks-progress">
      <div class="progress-label">
        Progress: {{ completedCount }}/{{ subtasks.length }}
      </div>
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: progressPercentage + '%' }"></div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'

export default {
  name: 'SubtasksManager',
  props: {
    taskId: {
      type: [String, Number],
      required: true
    }
  },
  emits: ['update-subtasks'],
  setup(props, { emit }) {
    const showAddForm = ref(false)
    const newSubtask = ref({ title: '' })
    const subtasks = ref([])

    const loadSubtasks = () => {
      const saved = localStorage.getItem(`subtasks-${props.taskId}`)
      if (saved) {
        try {
          subtasks.value = JSON.parse(saved)
        } catch (e) {
          console.error('Failed to load subtasks:', e)
        }
      }
    }

    const saveSubtasks = () => {
      localStorage.setItem(`subtasks-${props.taskId}`, JSON.stringify(subtasks.value))
      emit('update-subtasks', subtasks.value)
    }

    const toggleAddForm = () => {
      showAddForm.value = !showAddForm.value
      if (!showAddForm.value) {
        newSubtask.value = { title: '' }
      }
    }

    const addSubtask = () => {
      if (!newSubtask.value.title.trim()) return

      subtasks.value.push({
        id: Date.now(),
        title: newSubtask.value.title,
        completed: false,
        createdAt: new Date().toISOString()
      })

      saveSubtasks()
      newSubtask.value = { title: '' }
      showAddForm.value = false
    }

    const toggleSubtask = (id) => {
      const subtask = subtasks.value.find(s => s.id === id)
      if (subtask) {
        subtask.completed = !subtask.completed
        saveSubtasks()
      }
    }

    const deleteSubtask = (id) => {
      const index = subtasks.value.findIndex(s => s.id === id)
      if (index > -1) {
        subtasks.value.splice(index, 1)
        saveSubtasks()
      }
    }

    const completedCount = computed(() => {
      return subtasks.value.filter(s => s.completed).length
    })

    const progressPercentage = computed(() => {
      if (subtasks.value.length === 0) return 0
      return Math.round((completedCount.value / subtasks.value.length) * 100)
    })

    const formatDate = (dateStr) => {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return date.toLocaleDateString('en-GB', { month: 'short', day: 'numeric' })
    }

    loadSubtasks()

    return {
      showAddForm,
      newSubtask,
      subtasks,
      completedCount,
      progressPercentage,
      toggleAddForm,
      addSubtask,
      toggleSubtask,
      deleteSubtask,
      formatDate
    }
  }
}
</script>

<style scoped>
.subtasks-manager {
  background: #fafafa;
  border-left: 4px solid #1a73e8;
  padding: 12px;
  border-radius: 4px;
  margin-top: 12px;
}

.subtasks-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.subtasks-header h3 {
  margin: 0;
  font-size: 14px;
  color: #202124;
}

.btn-add-subtask {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  padding: 4px;
  transition: transform 0.2s;
}

.btn-add-subtask:hover {
  transform: scale(1.2);
}

.add-subtask-form {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
  padding: 8px;
  background: white;
  border-radius: 4px;
}

.subtask-input {
  padding: 8px 12px;
  border: 1px solid #e8eaed;
  border-radius: 4px;
  font-size: 13px;
  font-family: inherit;
}

.subtask-input:focus {
  outline: none;
  border-color: #1a73e8;
  box-shadow: 0 0 0 2px rgba(26, 115, 232, 0.1);
}

.form-actions {
  display: flex;
  gap: 8px;
}

.btn-create,
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

.btn-create {
  background: #1a73e8;
  color: white;
}

.btn-create:hover:not(:disabled) {
  background: #1557b0;
}

.btn-create:disabled {
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

.no-subtasks {
  padding: 12px;
  text-align: center;
  color: #5f6368;
  font-size: 12px;
  font-style: italic;
}

.subtasks-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.subtask-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  background: white;
  border-radius: 4px;
  border: 1px solid #e8eaed;
  transition: all 0.2s;
}

.subtask-item:hover {
  border-color: #1a73e8;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
}

.subtask-item.completed {
  opacity: 0.6;
  background: #f5f5f5;
}

.subtask-checkbox {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
  cursor: pointer;
}

.subtask-label {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 12px;
  min-width: 0;
}

.subtask-title {
  flex: 1;
  word-break: break-word;
}

.subtask-item.completed .subtask-title {
  text-decoration: line-through;
  color: #5f6368;
}

.subtask-due {
  white-space: nowrap;
  background: #f0f7ff;
  color: #1a73e8;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 11px;
}

.btn-delete-subtask {
  background: none;
  border: none;
  font-size: 14px;
  cursor: pointer;
  padding: 2px;
  opacity: 0.7;
  transition: opacity 0.2s;
}

.btn-delete-subtask:hover {
  opacity: 1;
}

.subtasks-progress {
  margin-top: 12px;
  padding: 8px;
  background: white;
  border-radius: 4px;
}

.progress-label {
  font-size: 11px;
  color: #5f6368;
  margin-bottom: 4px;
  font-weight: 600;
}

.progress-bar {
  width: 100%;
  height: 6px;
  background: #e8eaed;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #1a73e8, #34a853);
  border-radius: 3px;
  transition: width 0.3s ease;
}
</style>
