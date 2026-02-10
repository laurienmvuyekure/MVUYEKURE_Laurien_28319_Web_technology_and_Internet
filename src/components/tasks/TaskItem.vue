<template>
  <article 
    class="task-item" 
    :class="[task.priority, { completed: task.completed }]"
    role="article"
    :aria-labelledby="`task-title-${task.id}`"
  >
    <div class="task-header">
      <input
        type="checkbox"
        :id="`task-checkbox-${task.id}`"
        :checked="task.completed"
        @change="$emit('toggle', task.id)"
        class="task-checkbox"
        :aria-label="`Mark ${task.title} as ${task.completed ? 'incomplete' : 'complete'}`"
      />
      
      <div class="task-title-group">
        <h3 
          :id="`task-title-${task.id}`"
          class="task-title"
          :class="{ 'completed-text': task.completed }"
        >
          {{ task.title }}
        </h3>
        
        <div class="task-meta">
          <span class="task-category" :style="{ backgroundColor: getCategoryColor(task.category) }">
            {{ task.category }}
          </span>
          <span class="task-priority" :class="task.priority">
            {{ task.priority }}
          </span>
        </div>
      </div>
      
      <div class="task-actions">
        <button 
          @click="$emit('edit', task)"
          class="action-button edit"
          aria-label="Edit task"
        >
          <span aria-hidden="true">✏️</span>
        </button>
        
        <button 
          @click="$emit('share', task)"
          class="action-button share"
          aria-label="Share task"
        >
          <span aria-hidden="true">🔗</span>
        </button>
        
        <button 
          @click="$emit('delete', task.id)"
          class="action-button delete"
          aria-label="Delete task"
        >
          <span aria-hidden="true">🗑️</span>
        </button>
      </div>
    </div>
    
    <div class="task-body" v-if="task.description">
      <p class="task-description">{{ task.description }}</p>
    </div>
    
    <div class="task-footer">
      <div class="task-dates">
        <span class="due-date" v-if="task.dueDate">
          <span aria-hidden="true">📅</span>
          Due: {{ formatDate(task.dueDate) }}
          <span v-if="isOverdue(task.dueDate)" class="overdue">(Overdue)</span>
        </span>
        <span class="created-date" v-if="task.createdAt">
          Created: {{ formatDate(task.createdAt) }}
        </span>
      </div>
      
      <div class="task-tags" v-if="task.tags && task.tags.length">
        <span 
          v-for="tag in task.tags" 
          :key="tag"
          class="tag"
        >
          {{ tag }}
        </span>
      </div>
    </div>
    
    <div v-if="task.sharedWith && task.sharedWith.length" class="shared-with">
      <span class="shared-label">Shared with:</span>
      <div class="shared-users">
        <span 
          v-for="user in task.sharedWith" 
          :key="user.email"
          class="shared-user"
          :title="`${user.name} (${user.permission})`"
        >
          {{ getUserInitials(user.name) }}
        </span>
      </div>
    </div>
  </article>
</template>

<script>
export default {
  name: 'ReusableTaskItem',
  props: {
    task: {
      type: Object,
      required: true
    }
  },
  emits: ['toggle', 'edit', 'share', 'delete'],
  methods: {
    getCategoryColor(category) {
      const colors = {
        'Work': '#e3f2fd',
        'Personal': '#f3e5f5',
        'Study': '#e8f5e9',
        'Health': '#ffebee',
        'Finance': '#fff3e0'
      }
      return colors[category] || '#f5f5f5'
    },
    
    formatDate(dateString) {
      const date = new Date(dateString)
      return date.toLocaleDateString('en-GB', {
        day: 'numeric',
        month: 'short',
        year: 'numeric'
      })
    },
    
    isOverdue(dueDate) {
      if (!dueDate) return false
      return new Date(dueDate) < new Date() && !this.task.completed
    },
    
    getUserInitials(name) {
      return name.split(' ')
        .map(word => word[0])
        .join('')
        .toUpperCase()
        .substring(0, 2)
    }
  }
}
</script>

<style scoped>
.task-item {
  background: white;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  transition: all 0.2s ease;
  position: relative;
}

.task-item:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transform: translateY(-2px);
}

.task-item.completed {
  opacity: 0.7;
  background: #f8f9fa;
}

.task-item.completed::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 10px;
  right: 10px;
  height: 1px;
  background: #6c757d;
}

.task-header {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 12px;
}

.task-checkbox {
  width: 20px;
  height: 20px;
  margin-top: 4px;
  flex-shrink: 0;
}

.task-title-group {
  flex: 1;
  min-width: 0;
}

.task-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #212529;
  word-break: break-word;
}

.completed-text {
  text-decoration: line-through;
  color: #6c757d;
}

.task-meta {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.task-category {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  color: #495057;
}

.task-priority {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  color: white;
}

.task-priority.high {
  background: #dc3545;
}

.task-priority.medium {
  background: #ffc107;
  color: #212529;
}

.task-priority.low {
  background: #28a745;
}

.task-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.action-button {
  width: 32px;
  height: 32px;
  border: 1px solid #dee2e6;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.action-button:hover,
.action-button:focus {
  background: #f8f9fa;
  outline: 2px solid #0066cc;
}

.action-button.edit:hover {
  border-color: #0066cc;
}

.action-button.share:hover {
  border-color: #17a2b8;
}

.action-button.delete:hover {
  border-color: #dc3545;
}

.task-body {
  margin-bottom: 12px;
}

.task-description {
  margin: 0;
  color: #6c757d;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}

.task-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #e9ecef;
}

.task-dates {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 12px;
  color: #6c757d;
}

.due-date {
  display: flex;
  align-items: center;
  gap: 4px;
}

.overdue {
  color: #dc3545;
  font-weight: bold;
}

.task-tags {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

.tag {
  padding: 2px 6px;
  background: #e9ecef;
  border-radius: 4px;
  font-size: 11px;
  color: #495057;
}

.shared-with {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e9ecef;
  font-size: 12px;
}

.shared-label {
  color: #6c757d;
}

.shared-users {
  display: flex;
  gap: 4px;
}

.shared-user {
  width: 24px;
  height: 24px;
  background: #0066cc;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: bold;
}

@media (max-width: 768px) {
  .task-header {
    flex-wrap: wrap;
  }
  
  .task-actions {
    order: 3;
    width: 100%;
    justify-content: flex-end;
    margin-top: 12px;
  }
  
  .task-footer {
    flex-direction: column;
    align-items: flex-start;
  }
}

@media (prefers-reduced-motion: reduce) {
  .task-item {
    transition: none;
  }
}
</style>