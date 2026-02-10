<template>
  <div class="new-task-container">
    <h1>Create New Task</h1>
    <form @submit.prevent="handleAddTask" class="new-task-form">
      <div class="form-group">
        <label for="task-title" class="form-label">Task Title</label>
        <input 
          id="task-title"
          v-model="taskTitle" 
          placeholder="Enter new task" 
          type="text" 
          class="task-input"
          required
        />
      </div>
      
      <div class="form-group">
        <label for="task-description" class="form-label">Description</label>
        <textarea 
          id="task-description"
          v-model="taskDescription" 
          placeholder="Add task description (optional)"
          class="task-textarea"
          rows="4"
        ></textarea>
      </div>
      
      <div class="form-group">
        <label for="task-category" class="form-label">Category</label>
        <select v-model="taskCategory" id="task-category" class="task-select">
          <option value="">Select a category</option>
          <option value="Work">Work</option>
          <option value="Personal">Personal</option>
          <option value="Study">Study</option>
          <option value="Health">Health</option>
        </select>
      </div>
      
      <div class="form-group">
        <label for="task-priority" class="form-label">Priority</label>
        <select v-model="taskPriority" id="task-priority" class="task-select">
          <option value="low">Low</option>
          <option value="medium" selected>Medium</option>
          <option value="high">High</option>
        </select>
      </div>

      <div class="form-group">
        <label for="task-date" class="form-label">Due Date</label>
        <input 
          id="task-date"
          v-model="taskDate" 
          type="date" 
          class="task-input"
          required
        />
      </div>

      <div class="form-group">
        <label for="task-time" class="form-label">Due Time</label>
        <input 
          id="task-time"
          v-model="taskTime" 
          type="time" 
          class="task-input"
          required
        />
      </div>

      <div class="form-group">
        <label for="task-duration" class="form-label">Estimated Duration (minutes)</label>
        <input 
          id="task-duration"
          v-model.number="taskDuration" 
          type="number" 
          min="1"
          placeholder="e.g., 30"
          class="task-input"
          required
        />
      </div>
      
      <button type="submit" class="add-task-button">Add Task</button>
    </form>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'NewTask',
  setup() {
    const router = useRouter()
    const taskTitle = ref('')
    const taskDescription = ref('')
    const taskCategory = ref('')
    const taskPriority = ref('medium')
    const taskDate = ref('')
    const taskTime = ref('')
    const taskDuration = ref(30)
    
    // Set default date to today
    onMounted(() => {
      const today = new Date().toISOString().split('T')[0]
      taskDate.value = today
    })
    
    const handleAddTask = () => {
      if (!taskTitle.value.trim()) {
        alert('Task title is required')
        return
      }
      if (!taskDate.value || !taskTime.value) {
        alert('Date and time are required')
        return
      }
      if (!taskDuration.value || taskDuration.value < 1) {
        alert('Duration must be at least 1 minute')
        return
      }
      
      // Dispatch custom event
      const event = new CustomEvent('task-created', {
        detail: {
          title: taskTitle.value,
          description: taskDescription.value,
          category: taskCategory.value,
          priority: taskPriority.value,
          date: taskDate.value,
          time: taskTime.value,
          duration: taskDuration.value
        }
      })
      window.dispatchEvent(event)
      
      // Reset form
      taskTitle.value = ''
      taskDescription.value = ''
      taskCategory.value = ''
      taskPriority.value = 'medium'
      taskTime.value = ''
      taskDuration.value = 30
      const today = new Date().toISOString().split('T')[0]
      taskDate.value = today
      
      // Navigate back
      router.push('/dashboard')
    }
    
    return {
      taskTitle,
      taskDescription,
      taskCategory,
      taskPriority,
      taskDate,
      taskTime,
      taskDuration,
      handleAddTask
    }
  }
}
</script>

<style scoped>
.new-task-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.new-task-container h1 {
  margin-bottom: 30px;
  color: #202124;
  font-size: 28px;
  font-weight: 600;
}

.new-task-form {
  background: white;
  padding: 24px;
  border-radius: 8px;
  border: 1px solid #d0d2d7;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #202124;
  font-size: 14px;
}

.task-input,
.task-textarea,
.task-select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d0d2d7;
  border-radius: 6px;
  font-size: 14px;
  font-family: inherit;
  color: #202124;
  background: white;
  transition: all 0.2s;
}

.task-input:focus,
.task-textarea:focus,
.task-select:focus {
  border-color: #1a73e8;
  outline: none;
  box-shadow: 0 0 0 3px rgba(26, 115, 232, 0.1);
}

.task-textarea {
  resize: vertical;
}

.add-task-button {
  width: 100%;
  padding: 12px;
  background: #1a73e8;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.add-task-button:hover {
  background: #1557b0;
}

.add-task-button:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(26, 115, 232, 0.2);
}

@media (max-width: 768px) {
  .new-task-container {
    padding: 15px;
  }

  .new-task-form {
    padding: 16px;
  }
}
</style>