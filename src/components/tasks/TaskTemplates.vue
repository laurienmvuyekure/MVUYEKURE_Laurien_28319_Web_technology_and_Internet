<template>
  <div class="task-templates">
    <h2>📋 Task Templates</h2>
    <p class="description">Create and manage reusable task templates for faster task creation</p>

    <div class="template-controls">
      <div class="control-group">
        <label for="template-name">Template Name</label>
        <input 
          v-model="newTemplate.name" 
          id="template-name" 
          type="text" 
          placeholder="e.g., Weekly Review"
          class="input-field"
        />
      </div>

      <div class="control-group">
        <label for="template-desc">Description</label>
        <textarea 
          v-model="newTemplate.description" 
          id="template-desc" 
          placeholder="What is this template for?"
          class="textarea-field"
        ></textarea>
      </div>

      <div class="control-group">
        <label for="template-category">Category</label>
        <select v-model="newTemplate.category" id="template-category" class="select-field">
          <option value="Work">Work</option>
          <option value="Personal">Personal</option>
          <option value="Study">Study</option>
          <option value="Health">Health</option>
        </select>
      </div>

      <div class="control-group">
        <label for="template-priority">Priority</label>
        <select v-model="newTemplate.priority" id="template-priority" class="select-field">
          <option value="low">Low</option>
          <option value="medium">Medium</option>
          <option value="high">High</option>
          <option value="critical">Critical</option>
        </select>
      </div>

      <div class="control-group">
        <label for="template-duration">Duration (minutes)</label>
        <input 
          v-model.number="newTemplate.duration" 
          id="template-duration" 
          type="number" 
          placeholder="e.g., 30"
          class="input-field"
        />
      </div>

      <div class="control-group">
        <label for="template-tags">Tags (comma-separated)</label>
        <input 
          v-model="newTemplate.tags" 
          id="template-tags" 
          type="text" 
          placeholder="e.g., recurring, review, important"
          class="input-field"
        />
      </div>

      <button @click="addTemplate" class="btn-add-template" :disabled="!newTemplate.name">
        ➕ Create Template
      </button>
    </div>

    <div class="templates-section">
      <h3>Available Templates ({{ templates.length }})</h3>

      <div v-if="templates.length === 0" class="no-templates">
        <p>No templates yet. Create one to get started!</p>
      </div>

      <div v-else class="templates-grid">
        <div 
          v-for="template in templates" 
          :key="template.id"
          class="template-card"
        >
          <div class="template-header">
            <h4 class="template-name">{{ template.name }}</h4>
            <span class="template-category">{{ template.category }}</span>
          </div>

          <p class="template-desc">{{ template.description }}</p>

          <div class="template-meta">
            <span class="meta-item priority-badge" :class="template.priority">
              {{ template.priority }}
            </span>
            <span class="meta-item">⏱️ {{ template.duration }}m</span>
            <span v-if="template.tags" class="meta-item">🏷️ {{ template.tags }}</span>
          </div>

          <div class="template-actions">
            <button @click="createTaskFromTemplate(template)" class="btn-use">
              ✨ Use Template
            </button>
            <button @click="editTemplate(template)" class="btn-edit">
              ✏️ Edit
            </button>
            <button @click="deleteTemplate(template.id)" class="btn-delete">
              🗑️ Delete
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Edit Modal -->
    <div v-if="editingTemplate" class="modal-overlay" @click="closeEditModal">
      <div class="modal-content" @click.stop>
        <h3>Edit Template: {{ editingTemplate.name }}</h3>
        
        <div class="modal-form">
          <div class="form-group">
            <label>Name</label>
            <input v-model="editingTemplate.name" type="text" class="input-field" />
          </div>

          <div class="form-group">
            <label>Description</label>
            <textarea v-model="editingTemplate.description" class="textarea-field"></textarea>
          </div>

          <div class="form-group">
            <label>Category</label>
            <select v-model="editingTemplate.category" class="select-field">
              <option value="Work">Work</option>
              <option value="Personal">Personal</option>
              <option value="Study">Study</option>
              <option value="Health">Health</option>
            </select>
          </div>

          <div class="form-group">
            <label>Priority</label>
            <select v-model="editingTemplate.priority" class="select-field">
              <option value="low">Low</option>
              <option value="medium">Medium</option>
              <option value="high">High</option>
              <option value="critical">Critical</option>
            </select>
          </div>

          <div class="form-group">
            <label>Duration (minutes)</label>
            <input v-model.number="editingTemplate.duration" type="number" class="input-field" />
          </div>

          <div class="form-group">
            <label>Tags</label>
            <input v-model="editingTemplate.tags" type="text" class="input-field" />
          </div>
        </div>

        <div class="modal-actions">
          <button @click="saveEdit" class="btn-save">💾 Save Changes</button>
          <button @click="closeEditModal" class="btn-cancel">✕ Cancel</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, inject } from 'vue'

export default {
  name: 'TaskTemplates',
  setup() {
    const addTask = inject('addTask', () => {})
    
    const templates = ref([])
    const editingTemplate = ref(null)
    const newTemplate = ref({
      name: '',
      description: '',
      category: 'Work',
      priority: 'medium',
      duration: 30,
      tags: ''
    })

    const loadTemplates = () => {
      const saved = localStorage.getItem('taskTemplates')
      if (saved) {
        try {
          templates.value = JSON.parse(saved)
        } catch (e) {
          console.error('Failed to load templates:', e)
        }
      }
    }

    const saveTemplates = () => {
      localStorage.setItem('taskTemplates', JSON.stringify(templates.value))
    }

    const addTemplate = () => {
      templates.value.push({
        id: Date.now(),
        ...newTemplate.value,
        createdAt: new Date().toISOString()
      })
      saveTemplates()
      newTemplate.value = {
        name: '',
        description: '',
        category: 'Work',
        priority: 'medium',
        duration: 30,
        tags: ''
      }
    }

    const editTemplate = (template) => {
      editingTemplate.value = { ...template }
    }

    const saveEdit = () => {
      const index = templates.value.findIndex(t => t.id === editingTemplate.value.id)
      if (index > -1) {
        templates.value[index] = editingTemplate.value
        saveTemplates()
      }
      editingTemplate.value = null
    }

    const closeEditModal = () => {
      editingTemplate.value = null
    }

    const deleteTemplate = (id) => {
      templates.value = templates.value.filter(t => t.id !== id)
      saveTemplates()
    }

    const createTaskFromTemplate = (template) => {
      const today = new Date().toISOString().split('T')[0]
      
      // Emit event or call injected function to create task
      window.dispatchEvent(new CustomEvent('create-task-from-template', {
        detail: {
          title: template.name,
          description: template.description,
          category: template.category,
          priority: template.priority,
          duration: template.duration,
          date: today,
          time: new Date().toTimeString().slice(0, 5)
        }
      }))
      
      alert(`✨ Task created from template: "${template.name}"`)
    }

    loadTemplates()

    return {
      templates,
      editingTemplate,
      newTemplate,
      addTemplate,
      editTemplate,
      saveEdit,
      closeEditModal,
      deleteTemplate,
      createTaskFromTemplate
    }
  }
}
</script>

<style scoped>
.task-templates {
  padding: 20px;
  background: white;
  border-radius: 12px;
}

h2 {
  color: #1a73e8;
  margin-bottom: 8px;
  font-size: 24px;
}

.description {
  color: #5f6368;
  margin-bottom: 20px;
  font-size: 14px;
}

.template-controls {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
  margin-bottom: 30px;
  padding: 16px;
  background: #f5f5f5;
  border-radius: 8px;
}

.control-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.control-group label {
  font-size: 12px;
  font-weight: 600;
  color: #202124;
}

.input-field,
.textarea-field,
.select-field {
  padding: 8px;
  border: 1px solid #e8eaed;
  border-radius: 4px;
  font-size: 14px;
  font-family: inherit;

  &:focus {
    outline: none;
    border-color: #1a73e8;
    box-shadow: 0 0 0 2px rgba(26, 115, 232, 0.1);
  }
}

.textarea-field {
  resize: vertical;
  min-height: 70px;
}

.btn-add-template {
  padding: 8px 16px;
  background: #1a73e8;
  color: white;
  border: none;
  border-radius: 4px;
  font-weight: 600;
  cursor: pointer;
  align-self: flex-end;
  transition: all 0.2s ease;

  &:hover:not(:disabled) {
    background: #1665d8;
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

.templates-section {
  margin-bottom: 20px;
}

.templates-section h3 {
  color: #202124;
  margin-bottom: 16px;
  font-size: 16px;
}

.no-templates {
  text-align: center;
  color: #5f6368;
  padding: 40px 20px;
  background: #f5f5f5;
  border-radius: 8px;
}

.templates-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.template-card {
  border: 1px solid #e8eaed;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.3s ease;

  &:hover {
    border-color: #1a73e8;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

.template-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.template-name {
  font-weight: 600;
  color: #202124;
  font-size: 16px;
  margin: 0;
}

.template-category {
  background: #e3f2fd;
  color: #1a73e8;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
}

.template-desc {
  color: #5f6368;
  font-size: 13px;
  margin: 8px 0;
  line-height: 1.4;
}

.template-meta {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.meta-item {
  font-size: 12px;
  color: #5f6368;
  background: #f5f5f5;
  padding: 4px 8px;
  border-radius: 4px;
}

.priority-badge {
  font-weight: 600;

  &.low { background: #e8f5e9; color: #388e3c; }
  &.medium { background: #fffde7; color: #f9a825; }
  &.high { background: #fff3e0; color: #f57c00; }
  &.critical { background: #ffebee; color: #d32f2f; }
}

.template-actions {
  display: flex;
  gap: 6px;
}

.btn-use,
.btn-edit,
.btn-delete {
  flex: 1;
  padding: 6px;
  border: 1px solid #e8eaed;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 600;
  transition: all 0.2s ease;

  &:hover {
    border-color: #1a73e8;
    color: #1a73e8;
  }
}

.btn-delete:hover {
  border-color: #d32f2f;
  color: #d32f2f;
}

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
}

.modal-content {
  background: white;
  border-radius: 8px;
  padding: 24px;
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-content h3 {
  color: #202124;
  margin-bottom: 16px;
  font-size: 18px;
}

.modal-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group label {
  font-weight: 600;
  color: #202124;
  font-size: 13px;
}

.form-group input,
.form-group textarea,
.form-group select {
  padding: 8px;
  border: 1px solid #e8eaed;
  border-radius: 4px;
  font-size: 13px;
  font-family: inherit;

  &:focus {
    outline: none;
    border-color: #1a73e8;
    box-shadow: 0 0 0 2px rgba(26, 115, 232, 0.1);
  }
}

.form-group textarea {
  resize: vertical;
  min-height: 80px;
}

.modal-actions {
  display: flex;
  gap: 8px;
}

.btn-save,
.btn-cancel {
  flex: 1;
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-save {
  background: #1a73e8;
  color: white;

  &:hover {
    background: #1665d8;
  }
}

.btn-cancel {
  background: #f5f5f5;
  color: #5f6368;

  &:hover {
    background: #e8eaed;
  }
}
</style>
