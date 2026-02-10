<template>
  <aside 
    class="app-sidebar" 
    :class="{ collapsed: isCollapsed }"
    role="navigation"
    aria-label="Main navigation"
  >
    <nav class="sidebar-nav">
      <ul class="nav-list" role="menubar">
        <li v-for="category in categories" :key="category" role="none">
          <button 
            @click="selectCategory(category)"
            :class="['nav-item', { active: activeCategory === category }]"
            role="menuitem"
            :aria-current="activeCategory === category ? 'page' : null"
          >
            <span class="nav-icon" aria-hidden="true">
              {{ getCategoryIcon(category) }}
            </span>
            <span class="nav-text">{{ category }}</span>
            <span class="task-count" v-if="getTaskCount(category) > 0">
              {{ getTaskCount(category) }}
            </span>
          </button>
        </li>
      </ul>
      
      <div class="sidebar-section">
        <h3 class="section-title">Quick Actions</h3>
        <ul class="action-list" role="menu">
          <li role="none">
            <button 
              @click="createTask"
              class="action-button"
              role="menuitem"
            >
              <span aria-hidden="true">＋</span>
              <span>New Task</span>
            </button>
          </li>
          <li role="none">
            <button 
              @click="showTodayTasks"
              class="action-button"
              role="menuitem"
            >
              <span aria-hidden="true">📅</span>
              <span>Today</span>
            </button>
          </li>
          <li role="none">
            <button 
              @click="showCompletedTasks"
              class="action-button"
              role="menuitem"
            >
              <span aria-hidden="true">✅</span>
              <span>Completed</span>
            </button>
          </li>
        </ul>
      </div>
      
      <div class="sidebar-section">
        <h3 class="section-title">Tools</h3>
        <ul class="action-list" role="menu">
          <li role="none">
            <router-link 
              to="/time-manager"
              class="action-button"
              role="menuitem"
            >
              <span aria-hidden="true">⏱️</span>
              <span>Time Manager</span>
            </router-link>
          </li>
          <li role="none">
            <router-link 
              to="/time-blocker"
              class="action-button"
              role="menuitem"
            >
              <span aria-hidden="true">🎯</span>
              <span>Time Blocker</span>
            </router-link>
          </li>
          <li role="none">
            <router-link 
              to="/recurring-tasks"
              class="action-button"
              role="menuitem"
            >
              <span aria-hidden="true">🔄</span>
              <span>Recurring Tasks</span>
            </router-link>
          </li>
          <li role="none">
            <router-link 
              to="/task-dependencies"
              class="action-button"
              role="menuitem"
            >
              <span aria-hidden="true">🔗</span>
              <span>Task Dependencies</span>
            </router-link>
          </li>
          <li role="none">
            <router-link 
              to="/documents"
              class="action-button"
              role="menuitem"
            >
              <span aria-hidden="true">📁</span>
              <span>Documents</span>
            </router-link>
          </li>
        </ul>
      </div>

      <div class="sidebar-section">
        <h3 class="section-title">Settings & Help</h3>
        <ul class="action-list" role="menu">
          <li role="none">
            <router-link 
              to="/reminders"
              class="action-button"
              role="menuitem"
            >
              <span aria-hidden="true">🔔</span>
              <span>Reminders</span>
            </router-link>
          </li>
          <li role="none">
            <router-link 
              to="/settings"
              class="action-button"
              role="menuitem"
            >
              <span aria-hidden="true">⚙️</span>
              <span>Settings</span>
            </router-link>
          </li>
        </ul>
      </div>
      
      <div class="sidebar-section">
        <h3 class="section-title">Shared With Me</h3>
        <ul class="shared-list" role="list">
          <li v-for="shared in sharedTasks" :key="shared.id" role="listitem">
            <div class="shared-task">
              <span class="task-title">{{ shared.title }}</span>
              <span class="shared-by">from {{ shared.owner }}</span>
            </div>
          </li>
        </ul>
      </div>
    </nav>
  </aside>
</template>

<script>
import { ref, inject } from 'vue'

export default {
  name: 'LayoutSidebar',
  props: {
    categories: {
      type: Array,
      default: () => []
    }
  },
  emits: ['select-category'],
  setup(props, { emit }) {
    const isCollapsed = ref(false)
    const activeCategory = ref('All')
    const tasks = inject('tasks', ref([]))
    const sharedTasks = ref([
      { id: 1, title: 'Project Review', owner: 'Alice' },
      { id: 2, title: 'Team Meeting', owner: 'Bob' }
    ])
    
    const selectCategory = (category) => {
      activeCategory.value = category
      emit('select-category', category)
    }
    
    const getCategoryIcon = (category) => {
      const icons = {
        'All': '📋',
        'Work': '💼',
        'Personal': '🙍‍♂️',
        'Study': '📚',
        'Health': '🏥'
      }
      return icons[category] || '📌'
    }
    
    const getTaskCount = (category) => {
      const taskList = tasks.value || []
      if (category === 'All') {
        return taskList.length
      }
      return taskList.filter(task => task.category === category).length
    }
    
    const createTask = () => {
      window.dispatchEvent(new CustomEvent('open-task-form'))
    }
    
    const showTodayTasks = () => {
      selectCategory('Today')
    }
    
    const showCompletedTasks = () => {
      selectCategory('Completed')
    }
    
    return {
      isCollapsed,
      activeCategory,
      sharedTasks,
      tasks,
      selectCategory,
      getCategoryIcon,
      getTaskCount,
      createTask,
      showTodayTasks,
      showCompletedTasks
    }
  }
}
</script>

<style scoped>
.app-sidebar {
  width: 250px;
  background: #f5f6f7;
  border-right: 1px solid #d0d2d7;
  transition: width 0.3s ease;
  overflow-y: auto;
  height: calc(100vh - 60px);
}

.app-sidebar.collapsed {
  width: 60px;
}

.nav-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.nav-item {
  width: 100%;
  padding: 12px 20px;
  background: none;
  border: none;
  text-align: left;
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  color: #5a5f66;
  transition: all 0.2s;
  border-radius: 0 8px 8px 0;
  margin: 2px 0;
}

.nav-item:hover,
.nav-item:focus {
  background: #eaeced;
  color: #1a73e8;
  outline: none;
}

.nav-item.active {
  background: #1a73e8;
  color: white;
  font-weight: 500;
}

.nav-icon {
  font-size: 18px;
  min-width: 24px;
}

.nav-text {
  flex: 1;
}

.task-count {
  background: #d0d2d7;
  color: #202124;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  min-width: 20px;
  text-align: center;
}

.nav-item.active .task-count {
  background: rgba(255,255,255,0.2);
  color: white;
}

.sidebar-section {
  padding: 20px;
  border-top: 1px solid #d0d2d7;
}

.section-title {
  font-size: 0.9em;
  text-transform: uppercase;
  color: #5a5f66;
  margin: 0 0 12px 0;
  font-weight: 600;
}

.action-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.action-button {
  width: 100%;
  padding: 10px 15px;
  background: white;
  border: 1px solid #d0d2d7;
  border-radius: 6px;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: all 0.2s;
  color: #5a5f66;
  text-decoration: none;
}

.action-button:hover,
.action-button:focus {
  background: #1a73e8;
  color: white;
  border-color: #1a73e8;
  outline: none;
}

.shared-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.shared-task {
  padding: 10px;
  background: white;
  border: 1px solid #d0d2d7;
  border-radius: 6px;
  margin-bottom: 8px;
  display: flex;
  flex-direction: column;
}

.task-title {
  font-size: 0.9em;
  font-weight: 500;
  margin-bottom: 4px;
  color: #202124;
}

.shared-by {
  font-size: 0.8em;
  color: #5a5f66;
}

@media (max-width: 768px) {
  .app-sidebar {
    width: 100%;
    height: auto;
    border-right: none;
    border-bottom: 1px solid #d0d2d7;
  }
  
  .nav-list {
    display: flex;
    overflow-x: auto;
    padding: 10px;
  }
  
  .nav-item {
    flex-direction: column;
    min-width: 80px;
    padding: 10px;
    border-radius: 8px;
    margin: 0 5px;
  }
  
  .nav-text {
    font-size: 0.8em;
    margin-top: 4px;
  }
  
  .task-count {
    position: absolute;
    top: 5px;
    right: 5px;
  }
}
</style>