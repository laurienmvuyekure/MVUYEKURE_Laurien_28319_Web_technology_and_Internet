<template>
  <div class="search-container">
    <div class="search-box">
      <span class="search-icon">🔍</span>
      <input 
        v-model="searchQuery"
        type="text"
        placeholder="Search tasks by title or description..."
        class="search-input"
        aria-label="Search tasks"
      />
      <button 
        v-if="searchQuery"
        @click="clearSearch"
        class="clear-btn"
        aria-label="Clear search"
      >
        ✕
      </button>
    </div>
    
    <div class="filter-group">
      <label class="filter-label">
        <input 
          v-model="filterCompleted"
          type="checkbox"
          class="filter-checkbox"
        />
        <span>Show Completed</span>
      </label>
      
      <select v-model="sortBy" class="sort-select">
        <option value="newest">Newest First</option>
        <option value="oldest">Oldest First</option>
        <option value="priority-high">High Priority</option>
        <option value="priority-low">Low Priority</option>
        <option value="due-soon">Due Soon</option>
        <option value="title">Title (A-Z)</option>
      </select>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'

export default {
  name: 'TaskSearch',
  emits: ['update:search', 'update:filter', 'update:sort'],
  setup(props, { emit }) {
    const searchQuery = ref('')
    const filterCompleted = ref(true)
    const sortBy = ref('newest')

    const clearSearch = () => {
      searchQuery.value = ''
    }

    const updateSearch = (value) => {
      emit('update:search', value)
    }

    const updateFilter = (value) => {
      emit('update:filter', value)
    }

    const updateSort = (value) => {
      emit('update:sort', value)
    }

    return {
      searchQuery,
      filterCompleted,
      sortBy,
      clearSearch,
      updateSearch,
      updateFilter,
      updateSort
    }
  },
  watch: {
    searchQuery(newVal) {
      this.updateSearch(newVal)
    },
    filterCompleted(newVal) {
      this.updateFilter(newVal)
    },
    sortBy(newVal) {
      this.updateSort(newVal)
    }
  }
}
</script>

<style scoped>
.search-container {
  margin-bottom: 24px;
  padding: 16px;
  background: #f5f6f7;
  border-radius: 8px;
  border: 1px solid #d0d2d7;
}

.search-box {
  display: flex;
  align-items: center;
  background: white;
  border: 1px solid #d0d2d7;
  border-radius: 6px;
  padding: 10px 12px;
  margin-bottom: 12px;
  gap: 8px;
}

.search-icon {
  font-size: 16px;
  color: #5a5f66;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 14px;
  color: #202124;
  background: transparent;
}

.search-input::placeholder {
  color: #9aa0a6;
}

.clear-btn {
  background: none;
  border: none;
  color: #5a5f66;
  cursor: pointer;
  font-size: 16px;
  padding: 4px;
  transition: color 0.2s;
}

.clear-btn:hover {
  color: #1a73e8;
}

.filter-group {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  align-items: center;
}

.filter-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #202124;
  user-select: none;
}

.filter-checkbox {
  cursor: pointer;
  width: 18px;
  height: 18px;
}

.sort-select {
  padding: 8px 12px;
  border: 1px solid #d0d2d7;
  border-radius: 6px;
  background: white;
  color: #202124;
  font-size: 13px;
  cursor: pointer;
  transition: border-color 0.2s;
}

.sort-select:hover,
.sort-select:focus {
  border-color: #1a73e8;
  outline: none;
}

@media (max-width: 768px) {
  .search-container {
    padding: 12px;
  }

  .filter-group {
    gap: 12px;
  }

  .sort-select {
    flex: 1;
  }
}
</style>
