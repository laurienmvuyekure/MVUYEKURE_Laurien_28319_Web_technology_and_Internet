<template>
  <transition name="modal-fade">
    <div v-if="isOpen" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>Edit Task</h2>
          <button class="close-btn" @click="closeModal" aria-label="Close modal">✕</button>
        </div>
        
        <form @submit.prevent="saveTask" class="modal-form">
          <div class="form-group">
            <label for="edit-title">Task Title</label>
            <input 
              id="edit-title"
              v-model="editForm.title" 
              type="text" 
              class="form-input"
              required
            />
          </div>
          
          <div class="form-group">
            <label for="edit-description">Description</label>
            <textarea 
              id="edit-description"
              v-model="editForm.description" 
              class="form-textarea"
              rows="3"
            ></textarea>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label for="edit-category">Category</label>
              <select v-model="editForm.category" id="edit-category" class="form-select">
                <option value="">Select a category</option>
                <option value="Work">Work</option>
                <option value="Personal">Personal</option>
                <option value="Study">Study</option>
                <option value="Health">Health</option>
              </select>
            </div>
            
            <div class="form-group">
              <label for="edit-priority">Priority</label>
              <select v-model="editForm.priority" id="edit-priority" class="form-select">
                <option value="low">Low</option>
                <option value="medium">Medium</option>
                <option value="high">High</option>
              </select>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label for="edit-date">Due Date</label>
              <input 
                id="edit-date"
                v-model="editForm.date" 
                type="date" 
                class="form-input"
              />
            </div>
            
            <div class="form-group">
              <label for="edit-time">Due Time</label>
              <input 
                id="edit-time"
                v-model="editForm.time" 
                type="time" 
                class="form-input"
              />
            </div>
          </div>
          
          <div class="form-group">
            <label for="edit-duration">Duration (minutes)</label>
            <input 
              id="edit-duration"
              v-model.number="editForm.duration" 
              type="number" 
              min="1"
              class="form-input"
            />
          </div>
          
          <div class="form-group checkbox">
            <label for="edit-completed">
              <input 
                id="edit-completed"
                v-model="editForm.completed" 
                type="checkbox"
                class="form-checkbox"
              />
              <span>Mark as Completed</span>
            </label>
          </div>
          
          <div class="modal-footer">
            <button type="button" @click="closeModal" class="btn-cancel">Cancel</button>
            <button type="submit" class="btn-save">Save Changes</button>
          </div>
        </form>
      </div>
    </div>
  </transition>
</template>

<script>
import { ref, watch } from 'vue'

export default {
  name: 'EditTaskModal',
  props: {
    isOpen: {
      type: Boolean,
      default: false
    },
    task: {
      type: Object,
      default: null
    }
  },
  emits: ['close', 'save'],
  setup(props, { emit }) {
    const editForm = ref({
      title: '',
      description: '',
      category: '',
      priority: 'medium',
      date: '',
      time: '',
      duration: 30,
      completed: false
    })

    watch(() => props.task, (newTask) => {
      if (newTask) {
        editForm.value = { ...newTask }
      }
    }, { deep: true })

    const closeModal = () => {
      emit('close')
    }

    const saveTask = () => {
      emit('save', editForm.value)
      closeModal()
    }

    return {
      editForm,
      closeModal,
      saveTask
    }
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease;
}

.modal-content {
  background: white;
  border-radius: 12px;
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  animation: slideUp 0.3s ease;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #d0d2d7;
}

.modal-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #202124;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #5a5f66;
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s;
}

.close-btn:hover {
  color: #1a73e8;
}

.modal-form {
  padding: 20px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-size: 13px;
  font-weight: 500;
  color: #202124;
}

.form-input,
.form-textarea,
.form-select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d0d2d7;
  border-radius: 6px;
  font-size: 14px;
  font-family: inherit;
  color: #202124;
  transition: border-color 0.2s;
}

.form-input:focus,
.form-textarea:focus,
.form-select:focus {
  outline: none;
  border-color: #1a73e8;
  box-shadow: 0 0 0 3px rgba(26, 115, 232, 0.1);
}

.form-textarea {
  resize: vertical;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.form-group.checkbox {
  margin-top: 16px;
}

.form-group.checkbox label {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  font-weight: 400;
}

.form-checkbox {
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.modal-footer {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding: 20px;
  border-top: 1px solid #d0d2d7;
  background: #f5f6f7;
}

.btn-cancel,
.btn-save {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel {
  background: #f5f6f7;
  color: #202124;
  border: 1px solid #d0d2d7;
}

.btn-cancel:hover {
  background: #e8eaed;
}

.btn-save {
  background: #1a73e8;
  color: white;
}

.btn-save:hover {
  background: #1557b0;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.2s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .form-row {
    grid-template-columns: 1fr;
  }

  .modal-footer {
    flex-direction: column;
  }

  .btn-cancel,
  .btn-save {
    width: 100%;
  }
}
</style>
