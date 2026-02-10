<template>
  <div class="document-manager">
    <div class="manager-header">
      <h2>📁 Documents & Files</h2>
      <p class="subtitle">Upload and manage task-related documents</p>
    </div>

    <div class="upload-section">
      <div class="upload-area" @dragover="dragOver = true" @dragleave="dragOver = false" @drop="handleDrop" :class="{ 'drag-active': dragOver }">
        <input 
          ref="fileInput" 
          type="file" 
          multiple 
          @change="handleFileSelect"
          class="file-input"
          accept=".pdf,.doc,.docx,.txt,.xls,.xlsx,.ppt,.pptx,.jpg,.png,.gif"
        />
        <div class="upload-content">
          <span class="upload-icon">📤</span>
          <p class="upload-text">Drop files here or click to upload</p>
          <p class="upload-hint">Supports: PDF, Doc, Excel, PPT, Images</p>
          <button @click="triggerFileInput" class="btn-select-files">
            📁 Select Files
          </button>
        </div>
      </div>
    </div>

    <div class="documents-section">
      <div class="section-header">
        <h3>📚 Uploaded Documents</h3>
        <div class="filter-controls">
          <select v-model="filterType" class="filter-select">
            <option value="">All Files</option>
            <option value="pdf">PDF</option>
            <option value="doc">Documents</option>
            <option value="image">Images</option>
            <option value="sheet">Spreadsheets</option>
          </select>
        </div>
      </div>

      <div v-if="filteredDocuments.length === 0" class="no-documents">
        <p>📭 No documents uploaded yet</p>
      </div>

      <div v-else class="documents-grid">
        <div 
          v-for="doc in filteredDocuments" 
          :key="doc.id"
          class="document-card"
        >
          <div class="doc-icon">{{ getFileIcon(doc.type) }}</div>
          
          <div class="doc-info">
            <div class="doc-name" :title="doc.name">{{ doc.name }}</div>
            <div class="doc-meta">
              <span class="doc-size">{{ formatFileSize(doc.size) }}</span>
              <span class="doc-date">{{ formatDate(doc.uploadedAt) }}</span>
            </div>
          </div>

          <div class="doc-type-badge">{{ doc.type.toUpperCase() }}</div>

          <div class="doc-actions">
            <button @click="downloadDocument(doc)" class="btn-action" title="Download">
              ⬇️
            </button>
            <button @click="linkToTask(doc)" class="btn-action" title="Link to task">
              🔗
            </button>
            <button @click="deleteDocument(doc.id)" class="btn-action delete" title="Delete">
              🗑️
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="documents-stats">
      <div class="stat">
        <span class="stat-label">Total Files</span>
        <span class="stat-value">{{ documents.length }}</span>
      </div>
      <div class="stat">
        <span class="stat-label">Total Size</span>
        <span class="stat-value">{{ formatFileSize(totalSize) }}</span>
      </div>
      <div class="stat">
        <span class="stat-label">Linked Tasks</span>
        <span class="stat-value">{{ linkedTaskCount }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, inject } from 'vue'

export default {
  name: 'DocumentManager',
  setup() {
    const fileInput = ref(null)
    const dragOver = ref(false)
    const filterType = ref('')
    const tasks = inject('tasks', ref([]))
    
    const documents = ref([])
    
    const loadDocuments = () => {
      const saved = localStorage.getItem('documents')
      if (saved) {
        try {
          documents.value = JSON.parse(saved)
        } catch (e) {
          console.error('Failed to load documents:', e)
        }
      }
    }

    const saveDocuments = () => {
      localStorage.setItem('documents', JSON.stringify(documents.value))
    }

    const triggerFileInput = () => {
      if (fileInput.value) fileInput.value.click()
    }

    const getFileType = (filename) => {
      const ext = filename.split('.').pop().toLowerCase()
      if (['pdf'].includes(ext)) return 'pdf'
      if (['doc', 'docx', 'txt'].includes(ext)) return 'doc'
      if (['jpg', 'jpeg', 'png', 'gif', 'webp'].includes(ext)) return 'image'
      if (['xls', 'xlsx', 'csv'].includes(ext)) return 'sheet'
      if (['ppt', 'pptx'].includes(ext)) return 'presentation'
      return 'file'
    }

    const getFileIcon = (type) => {
      const icons = {
        'pdf': '📄',
        'doc': '📝',
        'image': '🖼️',
        'sheet': '📊',
        'presentation': '🎯',
        'file': '📎'
      }
      return icons[type] || '📎'
    }

    const formatFileSize = (bytes) => {
      if (!bytes) return '0 B'
      const k = 1024
      const sizes = ['B', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
    }

    const formatDate = (dateStr) => {
      const date = new Date(dateStr)
      return date.toLocaleDateString('en-GB', { month: 'short', day: 'numeric', year: 'numeric' })
    }

    const handleFileSelect = (event) => {
      const files = event.target.files
      if (files) {
        Array.from(files).forEach(file => {
          addDocument(file)
        })
      }
      if (fileInput.value) fileInput.value.value = ''
    }

    const handleDrop = (event) => {
      event.preventDefault()
      dragOver.value = false
      const files = event.dataTransfer.files
      if (files) {
        Array.from(files).forEach(file => {
          addDocument(file)
        })
      }
    }

    const addDocument = (file) => {
      // Limit file size to 10MB
      if (file.size > 10 * 1024 * 1024) {
        alert(`File "${file.name}" is too large (max 10MB)`)
        return
      }

      const reader = new FileReader()
      reader.onload = (e) => {
        documents.value.push({
          id: Date.now(),
          name: file.name,
          type: getFileType(file.name),
          size: file.size,
          data: e.target.result,
          uploadedAt: new Date().toISOString(),
          linkedTasks: []
        })
        saveDocuments()
      }
      reader.readAsDataURL(file)
    }

    const deleteDocument = (id) => {
      if (confirm('Delete this document?')) {
        documents.value = documents.value.filter(d => d.id !== id)
        saveDocuments()
      }
    }

    const downloadDocument = (doc) => {
      const link = document.createElement('a')
      link.href = doc.data
      link.download = doc.name
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
    }

    const linkToTask = (doc) => {
      const taskId = prompt('Enter task ID to link this document:')
      if (taskId) {
        if (!doc.linkedTasks.includes(taskId)) {
          doc.linkedTasks.push(taskId)
          saveDocuments()
          alert('✅ Document linked to task')
        } else {
          alert('⚠️ Already linked to this task')
        }
      }
    }

    const filteredDocuments = computed(() => {
      if (!filterType.value) return documents.value
      return documents.value.filter(d => d.type === filterType.value)
    })

    const totalSize = computed(() => {
      return documents.value.reduce((sum, d) => sum + d.size, 0)
    })

    const linkedTaskCount = computed(() => {
      return new Set(documents.value.flatMap(d => d.linkedTasks)).size
    })

    loadDocuments()

    return {
      fileInput,
      dragOver,
      filterType,
      documents,
      filteredDocuments,
      totalSize,
      linkedTaskCount,
      triggerFileInput,
      handleFileSelect,
      handleDrop,
      getFileIcon,
      formatFileSize,
      formatDate,
      deleteDocument,
      downloadDocument,
      linkToTask
    }
  }
}
</script>

<style scoped>
.document-manager {
  padding: 20px;
  background: white;
  border-radius: 12px;
  max-width: 1000px;
  margin: 0 auto;
}

.manager-header {
  margin-bottom: 24px;
}

.manager-header h2 {
  color: #1a73e8;
  font-size: 28px;
  margin-bottom: 4px;
}

.subtitle {
  color: #5f6368;
  font-size: 14px;
}

.upload-section {
  margin-bottom: 32px;
}

.upload-area {
  border: 2px dashed #e8eaed;
  border-radius: 8px;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #f9f9f9;

  &:hover {
    border-color: #1a73e8;
    background: #f0f7ff;
  }

  &.drag-active {
    border-color: #1a73e8;
    background: #e3f2fd;
    transform: scale(1.02);
  }
}

.file-input {
  display: none;
}

.upload-icon {
  display: block;
  font-size: 48px;
  margin-bottom: 16px;
}

.upload-text {
  color: #202124;
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
}

.upload-hint {
  color: #5f6368;
  font-size: 13px;
  margin-bottom: 16px;
}

.btn-select-files {
  padding: 10px 24px;
  background: #1a73e8;
  color: white;
  border: none;
  border-radius: 4px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    background: #1665d8;
    box-shadow: 0 2px 8px rgba(26, 115, 232, 0.3);
  }
}

.documents-section {
  margin-bottom: 32px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h3 {
  color: #202124;
  font-size: 18px;
  margin: 0;
}

.filter-controls {
  display: flex;
  gap: 8px;
}

.filter-select {
  padding: 6px 12px;
  border: 1px solid #e8eaed;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;

  &:focus {
    outline: none;
    border-color: #1a73e8;
  }
}

.no-documents {
  text-align: center;
  color: #5f6368;
  padding: 40px 20px;
  background: #f5f5f5;
  border-radius: 8px;
}

.documents-grid {
  display: grid;
  gap: 12px;
}

.document-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f9f9f9;
  border: 1px solid #e8eaed;
  border-radius: 6px;
  transition: all 0.2s ease;

  &:hover {
    background: #f5f5f5;
    border-color: #1a73e8;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }
}

.doc-icon {
  font-size: 28px;
  flex-shrink: 0;
}

.doc-info {
  flex: 1;
  min-width: 0;
}

.doc-name {
  font-weight: 600;
  color: #202124;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.doc-meta {
  display: flex;
  gap: 12px;
  font-size: 11px;
  color: #5f6368;
  margin-top: 4px;
}

.doc-size,
.doc-date {
  display: flex;
  align-items: center;
}

.doc-type-badge {
  background: #e3f2fd;
  color: #1a73e8;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 600;
  white-space: nowrap;
}

.doc-actions {
  display: flex;
  gap: 4px;
}

.btn-action {
  width: 32px;
  height: 32px;
  border: 1px solid #e8eaed;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  transition: all 0.2s ease;

  &:hover {
    background: #f0f7ff;
    border-color: #1a73e8;
  }

  &.delete:hover {
    background: #ffebee;
    border-color: #d32f2f;
  }
}

.documents-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 12px;
  padding: 16px;
  background: #f5f5f5;
  border-radius: 8px;
}

.stat {
  text-align: center;
}

.stat-label {
  display: block;
  font-size: 11px;
  color: #5f6368;
  text-transform: uppercase;
  margin-bottom: 4px;
}

.stat-value {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #1a73e8;
}

@media (max-width: 768px) {
  .document-manager {
    padding: 12px;
  }

  .upload-area {
    padding: 24px 12px;
  }

  .upload-icon {
    font-size: 36px;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .documents-grid {
    grid-template-columns: 1fr;
  }

  .document-card {
    flex-wrap: wrap;
  }

  .doc-info {
    width: 100%;
  }
}
</style>
