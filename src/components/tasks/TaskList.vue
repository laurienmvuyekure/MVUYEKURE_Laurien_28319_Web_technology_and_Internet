allx allx<template>
  <div class="tasks-container">
    <h2>My Tasks</h2>
    <div v-if="tasks && tasks.length === 0" class="no-tasks">
      <p>No tasks yet. <router-link to="/new-task">Create your first task</router-link></p>
    </div>
    <div v-else class="tasks-list">
      <article 
        v-for="task in tasks" 
        :key="task.id"
        class="task-card"
        :class="[task.priority, { completed: task.completed, 'time-finished': isTimeFinished(task) }]"
      >
        <div class="task-header">
          <input
            type="checkbox"
            :checked="task.completed"
            @change="toggleTask(task.id)"
            class="task-checkbox"
            :aria-label="`Mark ${task.title} as ${task.completed ? 'incomplete' : 'complete'}`"
          />
          <h3 class="task-title" :class="{ 'completed-text': task.completed }">
            {{ task.title }}
          </h3>
          <span v-if="task.category" class="task-category">{{ task.category }}</span>
          <span class="task-priority" :class="task.priority">{{ task.priority }}</span>
        </div>
        
        <div class="task-datetime">
          <span class="datetime-item">
            📅 {{ formatDate(task.date) }} @ {{ task.time }}
          </span>
          <span class="datetime-item duration">
            ⏱️ {{ task.duration }} min
          </span>
        </div>

        <div class="task-timer" :class="{ 'time-expired': isTimeFinished(task), 'time-warning': isTimeWarning(task) }">
          <span class="timer-label">Time remaining:</span>
          <span class="timer-display">{{ getTimeRemaining(task) }}</span>
          <span v-if="isTimeFinished(task)" class="timer-alert">⏰ TIME FINISHED - SHIFT TO OTHER ACTIVITY!</span>
        </div>
        
        <p v-if="task.description" class="task-description">{{ task.description }}</p>
        
        <div class="task-actions">
          <button @click="editTask(task.id)" class="btn-edit" aria-label="Edit task">Edit</button>
          <button @click="deleteTask(task.id)" class="btn-delete" aria-label="Delete task">Delete</button>
        </div>
      </article>
    </div>

    <router-link to="/new-task" class="fab-button" aria-label="Create new task">
      <span aria-hidden="true">+</span>
    </router-link>
  </div>
</template>

<script>
import { ref, onMounted, onBeforeUnmount } from 'vue'

export default {
  name: 'TaskList',
  props: {
    tasks: {
      type: Array,
      default: () => []
    }
  },
  emits: ['toggle', 'edit', 'delete'],
  setup(props, { emit }) {
    const currentTime = ref(new Date())
    let timerInterval = null

    onMounted(() => {
      // Update current time every second
      timerInterval = setInterval(() => {
        currentTime.value = new Date()
      }, 1000)
    })

    onBeforeUnmount(() => {
      if (timerInterval) {
        clearInterval(timerInterval)
      }
    })

    const formatDate = (dateStr) => {
      const date = new Date(dateStr + 'T00:00:00')
      return date.toLocaleDateString('en-GB', { weekday: 'short', day: 'numeric', month: 'short' })
    }
    const getTaskDateTime = (task) => {
      const [year, month, day] = task.date.split('-')
      const [hours, minutes] = task.time.split(':')
      return new Date(year, month - 1, day, hours, minutes, 0)
    }

    const getTimeRemaining = (task) => {
      const taskEndTime = getTaskDateTime(task)
      const endTime = new Date(taskEndTime.getTime() + task.duration * 60000)
      const now = currentTime.value
      
      if (now >= endTime) {
        return '⏰ Time Finished!'
      }

      const diffMs = endTime - now
      const hours = Math.floor(diffMs / (1000 * 60 * 60))
      const minutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60))
      const seconds = Math.floor((diffMs % (1000 * 60)) / 1000)

      if (hours > 0) {
        return `${hours}h ${minutes}m ${seconds}s`
      } else if (minutes > 0) {
        return `${minutes}m ${seconds}s`
      } else {
        return `${seconds}s`
      }
    }

    const isTimeFinished = (task) => {
      const taskEndTime = getTaskDateTime(task)
      const endTime = new Date(taskEndTime.getTime() + task.duration * 60000)
      return currentTime.value >= endTime
    }

    const isTimeWarning = (task) => {
      const taskEndTime = getTaskDateTime(task)
      const endTime = new Date(taskEndTime.getTime() + task.duration * 60000)
      const now = currentTime.value
      const diffMs = endTime - now
      // Warning when less than 5 minutes remaining
      return diffMs > 0 && diffMs <= 5 * 60 * 1000
    }

    const toggleTask = (id) => {
      emit('toggle', id)
    }

    const editTask = (id) => {
      emit('edit', id)
    }

    const deleteTask = (id) => {
      if (confirm('Are you sure you want to delete this task?')) {
        emit('delete', id)
      }
    }

    return {
      toggleTask,
      editTask,
      deleteTask,
      formatDate,
      getTimeRemaining,
      isTimeFinished,
      isTimeWarning,
      currentTime
    }
  }
}
</script>

<style scoped>
.tasks-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  position: relative;
}

.tasks-container h2 {
  margin-bottom: 30px;
  color: #1a73e8;
  font-size: 28px;
}

.no-tasks {
  text-align: center;
  padding: 60px 20px;
  color: #5a5f66;
}

.no-tasks p {
  font-size: 16px;
  margin: 0;
}

.no-tasks a {
  color: #1a73e8;
  text-decoration: none;
  font-weight: 500;
}

.no-tasks a:hover {
  text-decoration: underline;
}

.tasks-list {
  display: grid;
  gap: 16px;
}

.task-card {
  background: white;
  border: 1px solid #d0d2d7;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.2s ease;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}

.task-card:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  border-color: #70777f;
}

.task-card.completed {
  opacity: 0.7;
  background: #f5f6f7;
}

.task-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.task-checkbox {
  width: 20px;
  height: 20px;
  cursor: pointer;
  flex-shrink: 0;
}

.task-title {
  flex: 1;
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  word-break: break-word;
  color: #202124;
}

.completed-text {
  text-decoration: line-through;
  color: #5a5f66;
}

.task-category {
  display: inline-block;
  padding: 4px 12px;
  background: #e8f0fe;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  color: #1a73e8;
}

.task-priority {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.task-priority.high {
  background: #fce4e3;
  color: #d23636;
}

.task-priority.medium {
  background: #fef8e3;
  color: #e8c547;
}

.task-priority.low {
  background: #e6f5ea;
  color: #188038;
}

.task-datetime {
  display: flex;
  gap: 16px;
  margin: 12px 0;
  padding: 10px;
  background: #f5f6f7;
  border-radius: 6px;
  font-size: 14px;
  color: #5a5f66;
  flex-wrap: wrap;
}

.datetime-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.datetime-item.duration {
  color: #1a73e8;
  font-weight: 500;
}

.task-timer {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #e8f0fe;
  border-left: 4px solid #1a73e8;
  border-radius: 4px;
  margin: 12px 0;
  font-size: 14px;
}

.timer-label {
  font-weight: 500;
  color: #5a5f66;
  min-width: 110px;
}

.timer-display {
  font-family: 'Courier New', monospace;
  font-weight: 700;
  color: #1a73e8;
  font-size: 16px;
  min-width: 100px;
}

.task-timer.time-warning {
  background: #fef8e3;
  border-left-color: #e8c547;
}

.task-timer.time-warning .timer-display {
  color: #e8c547;
  animation: pulse-warning 1s infinite;
}

.task-timer.time-expired {
  background: #fce4e3;
  border-left-color: #d23636;
}

.task-timer.time-expired .timer-display {
  color: #d23636;
}

.timer-alert {
  font-weight: 700;
  color: #d23636;
  animation: pulse-alert 0.8s infinite;
  padding: 4px 8px;
  background: #fff;
  border-radius: 4px;
}

@keyframes pulse-warning {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.6; }
}

@keyframes pulse-alert {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.7; transform: scale(1.05); }
}

.task-card.time-finished {
  border: 2px solid #d23636;
  background: #fce4e3;
}

.task-description {
  color: #5a5f66;
  font-size: 14px;
  margin: 12px 0;
  line-height: 1.5;
  word-break: break-word;
}

.task-actions {
  display: flex;
  gap: 8px;
}

.btn-edit,
.btn-delete {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-edit {
  background: #1a73e8;
  color: white;
}

.btn-edit:hover {
  background: #1557b0;
  box-shadow: 0 2px 4px rgba(26, 115, 232, 0.2);
}

.btn-delete {
  background: #d23636;
  color: white;
}

.btn-delete:hover {
  background: #b92626;
  box-shadow: 0 2px 4px rgba(210, 54, 54, 0.2);
}

.fab-button {
  position: fixed;
  bottom: 30px;
  right: 30px;
  width: 56px;
  height: 56px;
  background: #1a73e8;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  text-decoration: none;
  box-shadow: 0 2px 8px rgba(26, 115, 232, 0.25);
  transition: all 0.2s;
  z-index: 50;
}

.fab-button:hover {
  background: #1557b0;
  box-shadow: 0 4px 12px rgba(26, 115, 232, 0.3);
  transform: scale(1.1);
}

@media (max-width: 768px) {
  .task-header {
    flex-wrap: wrap;
  }

  .fab-button {
    bottom: 20px;
    right: 20px;
    width: 48px;
    height: 48px;
    font-size: 28px;
  }
}
</style>
