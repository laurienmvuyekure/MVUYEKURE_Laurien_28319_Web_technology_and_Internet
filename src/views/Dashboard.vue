<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <div class="header-top">
        <h1>📊 Task Manager</h1>
        <div class="header-actions">
          <router-link to="/new-task" class="btn btn-primary">
            ➕ Create New Task
          </router-link>
          <DashboardTimer />
        </div>
      </div>
      
      <div class="category-badge">
        {{ getCategoryLabel(selectedCategory) }}
      </div>
    </div>
    
    <!-- Task Statistics -->
    <TaskStats :tasks="filteredTasks" />
    
    <!-- Search & Filter -->
    <TaskSearch 
      v-model:search="searchQuery"
      v-model:filter="showCompleted"
      v-model:sort="sortBy"
    />
    
    <!-- Task List -->
    <TaskList 
      :tasks="sortedAndFilteredTasks"
      @toggle="toggleTask"
      @edit="openEditModal"
      @delete="deleteTask"
    />
    
    <!-- Edit Task Modal -->
    <EditTaskModal 
      :isOpen="editModalOpen"
      :task="editingTask"
      @close="editModalOpen = false"
      @save="saveEditedTask"
    />
  </div>
</template>

<script>
import { inject, ref, computed } from 'vue'
import TaskList from '../components/tasks/TaskList.vue'
import TaskStats from '../components/tasks/TaskStats.vue'
import TaskSearch from '../components/tasks/TaskSearch.vue'
import EditTaskModal from '../components/tasks/EditTaskModal.vue'
import DashboardTimer from '../components/time/DashboardTimer.vue'

export default {
  name: 'Dashboard',
  components: {
    TaskList,
    TaskStats,
    TaskSearch,
    EditTaskModal,
    DashboardTimer
  },
  setup() {
    const tasks = inject('tasks', ref([]))
    const selectedCategory = inject('selectedCategory', ref('All'))
    const toggleTask = inject('toggleTask', () => {})
    const editTask = inject('editTask', () => {})
    const deleteTask = inject('deleteTask', () => {})
    
    const searchQuery = ref('')
    const showCompleted = ref(true)
    const sortBy = ref('newest')
    const editModalOpen = ref(false)
    const editingTask = ref(null)
    
    const getCategoryLabel = (category) => {
      const labels = {
        'All': '📋 All Tasks',
        'Today': '📅 Today\'s Tasks',
        'Completed': '✅ Completed Tasks',
        'Work': '💼 Work',
        'Personal': '🙍‍♂️ Personal',
        'Study': '📚 Study',
        'Health': '🏥 Health'
      }
      return labels[category] || category
    }

    const getToday = () => {
      return new Date().toISOString().split('T')[0]
    }

    const filteredTasks = computed(() => {
      const taskList = tasks.value || []
      
      if (selectedCategory.value === 'All') {
        return taskList
      } else if (selectedCategory.value === 'Today') {
        const today = getToday()
        return taskList.filter(task => task.date === today)
      } else if (selectedCategory.value === 'Completed') {
        return taskList.filter(task => task.completed === true)
      } else {
        return taskList.filter(task => task.category === selectedCategory.value)
      }
    })

    const searchedTasks = computed(() => {
      const taskList = filteredTasks.value
      
      if (!searchQuery.value.trim()) {
        return taskList
      }
      
      const query = searchQuery.value.toLowerCase()
      return taskList.filter(task => 
        task.title.toLowerCase().includes(query) ||
        task.description.toLowerCase().includes(query)
      )
    })

    const sortedAndFilteredTasks = computed(() => {
      let result = [...searchedTasks.value]
      
      // Filter by completion
      if (!showCompleted.value) {
        result = result.filter(task => !task.completed)
      }
      
      // Sort
      switch (sortBy.value) {
        case 'oldest':
          result.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
          break
        case 'priority-high':
          const priorityOrder = { high: 3, medium: 2, low: 1 }
          result.sort((a, b) => priorityOrder[b.priority] - priorityOrder[a.priority])
          break
        case 'priority-low':
          result.sort((a, b) => priorityOrder[a.priority] - priorityOrder[b.priority])
          break
        case 'due-soon':
          result.sort((a, b) => {
            const dateA = new Date(`${a.date}T${a.time}`)
            const dateB = new Date(`${b.date}T${b.time}`)
            return dateA - dateB
          })
          break
        case 'title':
          result.sort((a, b) => a.title.localeCompare(b.title))
          break
        case 'newest':
        default:
          result.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
      }
      
      return result
    })

    const openEditModal = (taskId) => {
      editingTask.value = tasks.value.find(t => t.id === taskId)
      editModalOpen.value = true
    }

    const saveEditedTask = (updatedTask) => {
      editTask(updatedTask.id, updatedTask)
    }
    
    return {
      tasks,
      selectedCategory,
      filteredTasks,
      sortedAndFilteredTasks,
      searchQuery,
      showCompleted,
      sortBy,
      toggleTask,
      editTask,
      deleteTask,
      getCategoryLabel,
      editModalOpen,
      editingTask,
      openEditModal,
      saveEditedTask
    }
  }
}
</script>

<style scoped>
.dashboard {
  padding: 24px;
  background: linear-gradient(135deg, #fafbfc 0%, #f3f4f6 100%);
  min-height: 100vh;
}

.dashboard-header {
  margin-bottom: 32px;
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.dashboard-header h1 {
  margin: 0;
  color: #202124;
  font-size: 32px;
  font-weight: 600;
}

.category-badge {
  display: inline-block;
  padding: 12px 18px;
  background: white;
  border: 1px solid #d0d2d7;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #1a73e8;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
  font-weight: 500;
}

.btn-primary {
  background-color: #1a73e8;
  color: white;
}

.btn-primary:hover {
  background-color: #1557b0;
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(26, 115, 232, 0.25);
}

.btn-primary:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(26, 115, 232, 0.2);
}

@media (max-width: 768px) {
  .dashboard {
    padding: 16px;
  }

  .header-top {
    flex-direction: column;
    gap: 16px;
  }

  .dashboard-header h1 {
    font-size: 24px;
  }

  .btn-primary {
    width: 100%;
    justify-content: center;
  }
}
</style>
