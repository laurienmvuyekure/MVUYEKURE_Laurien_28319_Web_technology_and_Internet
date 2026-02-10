<template>
  <div class="task-notes">
    <div class="notes-header">
      <h3>📝 Notes & Comments</h3>
      <button 
        @click="toggleAddNote"
        class="btn-add-note"
        :aria-label="showAddNote ? 'Cancel' : 'Add note'"
      >
        {{ showAddNote ? '✕' : '💬' }}
      </button>
    </div>

    <div v-if="showAddNote" class="add-note-form">
      <textarea 
        v-model="newNote.text"
        placeholder="Add a note or comment..."
        class="note-textarea"
        rows="4"
      ></textarea>
      <div class="form-actions">
        <button @click="addNote" class="btn-save" :disabled="!newNote.text.trim()">
          💾 Save Note
        </button>
        <button @click="toggleAddNote" class="btn-cancel">Cancel</button>
      </div>
    </div>

    <div v-if="notes.length === 0 && !showAddNote" class="no-notes">
      <p>No notes yet. Click the 💬 button to add one.</p>
    </div>

    <div v-else class="notes-list">
      <div 
        v-for="note in sortedNotes"
        :key="note.id"
        class="note-item"
        :class="{ pinned: note.pinned }"
      >
        <div class="note-header">
          <div class="note-info">
            <span v-if="note.pinned" class="pin-indicator" title="Pinned">📌</span>
            <span class="note-date">{{ formatDate(note.createdAt) }}</span>
          </div>
          <div class="note-actions">
            <button 
              @click="togglePin(note.id)"
              class="btn-action"
              :title="note.pinned ? 'Unpin' : 'Pin'"
            >
              📌
            </button>
            <button 
              @click="editNote(note.id)"
              class="btn-action"
              title="Edit note"
            >
              ✏️
            </button>
            <button 
              @click="deleteNote(note.id)"
              class="btn-action delete"
              title="Delete note"
            >
              🗑️
            </button>
          </div>
        </div>

        <div v-if="editingId !== note.id" class="note-content">
          <p>{{ note.text }}</p>
        </div>

        <div v-if="editingId === note.id" class="edit-note-form">
          <textarea 
            v-model="editingText"
            class="edit-textarea"
            rows="3"
          ></textarea>
          <div class="form-actions">
            <button @click="saveEdit(note.id)" class="btn-save">Update</button>
            <button @click="cancelEdit" class="btn-cancel">Cancel</button>
          </div>
        </div>

        <div class="note-tags">
          <input 
            v-model="newTag"
            type="text"
            placeholder="Add tag..."
            class="tag-input"
            @keyup.enter="addTagToNote(note.id)"
          />
          <div class="tags-display">
            <span 
              v-for="tag in note.tags"
              :key="tag"
              class="note-tag"
            >
              {{ tag }}
              <button 
                @click="removeTag(note.id, tag)"
                class="remove-tag"
              >
                ✕
              </button>
            </span>
          </div>
        </div>
      </div>
    </div>

    <div v-if="notes.length > 0" class="notes-stats">
      <span>📊 {{ notes.length }} note{{ notes.length !== 1 ? 's' : '' }}</span>
      <span>📌 {{ pinnedCount }} pinned</span>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'

export default {
  name: 'TaskNotesPanel',
  props: {
    taskId: {
      type: [String, Number],
      required: true
    }
  },
  emits: ['update-notes'],
  setup(props, { emit }) {
    const showAddNote = ref(false)
    const newNote = ref({ text: '' })
    const notes = ref([])
    const editingId = ref(null)
    const editingText = ref('')
    const newTag = ref('')

    const loadNotes = () => {
      const saved = localStorage.getItem(`notes-${props.taskId}`)
      if (saved) {
        try {
          notes.value = JSON.parse(saved)
        } catch (e) {
          console.error('Failed to load notes:', e)
        }
      }
    }

    const saveNotes = () => {
      localStorage.setItem(`notes-${props.taskId}`, JSON.stringify(notes.value))
      emit('update-notes', notes.value)
    }

    const toggleAddNote = () => {
      showAddNote.value = !showAddNote.value
      if (!showAddNote.value) {
        newNote.value = { text: '' }
      }
    }

    const addNote = () => {
      if (!newNote.value.text.trim()) return

      notes.value.push({
        id: Date.now(),
        text: newNote.value.text,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
        pinned: false,
        tags: []
      })

      saveNotes()
      newNote.value = { text: '' }
      showAddNote.value = false
    }

    const editNote = (id) => {
      const note = notes.value.find(n => n.id === id)
      if (note) {
        editingId.value = id
        editingText.value = note.text
      }
    }

    const saveEdit = (id) => {
      const note = notes.value.find(n => n.id === id)
      if (note) {
        note.text = editingText.value
        note.updatedAt = new Date().toISOString()
        saveNotes()
        editingId.value = null
        editingText.value = ''
      }
    }

    const cancelEdit = () => {
      editingId.value = null
      editingText.value = ''
    }

    const deleteNote = (id) => {
      const index = notes.value.findIndex(n => n.id === id)
      if (index > -1) {
        notes.value.splice(index, 1)
        saveNotes()
      }
    }

    const togglePin = (id) => {
      const note = notes.value.find(n => n.id === id)
      if (note) {
        note.pinned = !note.pinned
        saveNotes()
      }
    }

    const addTagToNote = (id) => {
      if (!newTag.value.trim()) return
      const note = notes.value.find(n => n.id === id)
      if (note && !note.tags.includes(newTag.value)) {
        note.tags.push(newTag.value)
        saveNotes()
        newTag.value = ''
      }
    }

    const removeTag = (id, tag) => {
      const note = notes.value.find(n => n.id === id)
      if (note) {
        note.tags = note.tags.filter(t => t !== tag)
        saveNotes()
      }
    }

    const sortedNotes = computed(() => {
      const sorted = [...notes.value]
      return sorted.sort((a, b) => {
        if (a.pinned && !b.pinned) return -1
        if (!a.pinned && b.pinned) return 1
        return new Date(b.createdAt) - new Date(a.createdAt)
      })
    })

    const pinnedCount = computed(() => {
      return notes.value.filter(n => n.pinned).length
    })

    const formatDate = (dateStr) => {
      const date = new Date(dateStr)
      const now = new Date()
      const diffMs = now - date
      const diffMins = Math.floor(diffMs / 60000)

      if (diffMins < 1) return 'just now'
      if (diffMins < 60) return `${diffMins}m ago`
      if (diffMins < 1440) return `${Math.floor(diffMins / 60)}h ago`

      return date.toLocaleDateString('en-GB', { month: 'short', day: 'numeric' })
    }

    loadNotes()

    return {
      showAddNote,
      newNote,
      notes,
      editingId,
      editingText,
      newTag,
      sortedNotes,
      pinnedCount,
      toggleAddNote,
      addNote,
      editNote,
      saveEdit,
      cancelEdit,
      deleteNote,
      togglePin,
      addTagToNote,
      removeTag,
      formatDate
    }
  }
}
</script>

<style scoped>
.task-notes {
  background: #fafafa;
  border: 1px solid #e8eaed;
  border-radius: 8px;
  padding: 16px;
  margin-top: 16px;
}

.notes-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.notes-header h3 {
  margin: 0;
  color: #202124;
  font-size: 14px;
}

.btn-add-note {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  padding: 4px;
  transition: transform 0.2s;
}

.btn-add-note:hover {
  transform: scale(1.2);
}

.add-note-form,
.edit-note-form {
  background: white;
  border: 1px solid #e8eaed;
  border-radius: 6px;
  padding: 12px;
  margin-bottom: 12px;
}

.note-textarea,
.edit-textarea {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #e8eaed;
  border-radius: 4px;
  font-family: inherit;
  font-size: 13px;
  resize: vertical;
  margin-bottom: 8px;
}

.note-textarea:focus,
.edit-textarea:focus {
  outline: none;
  border-color: #1a73e8;
  box-shadow: 0 0 0 2px rgba(26, 115, 232, 0.1);
}

.form-actions {
  display: flex;
  gap: 8px;
}

.btn-save,
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

.btn-save {
  background: #1a73e8;
  color: white;
}

.btn-save:hover:not(:disabled) {
  background: #1557b0;
}

.btn-save:disabled {
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

.no-notes {
  text-align: center;
  padding: 20px;
  color: #5f6368;
  font-size: 13px;
}

.notes-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.note-item {
  background: white;
  border: 1px solid #e8eaed;
  border-radius: 6px;
  padding: 12px;
  transition: all 0.2s;
}

.note-item:hover {
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.note-item.pinned {
  border-left: 3px solid #1a73e8;
  background: #f0f7ff;
}

.note-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.note-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 11px;
  color: #5f6368;
}

.pin-indicator {
  font-size: 12px;
}

.note-actions {
  display: flex;
  gap: 4px;
}

.btn-action {
  background: none;
  border: none;
  font-size: 14px;
  cursor: pointer;
  padding: 2px;
  opacity: 0.6;
  transition: opacity 0.2s;
}

.btn-action:hover {
  opacity: 1;
}

.btn-action.delete:hover {
  color: #d32f2f;
}

.note-content {
  margin-bottom: 12px;
  padding: 8px 0;
}

.note-content p {
  margin: 0;
  color: #202124;
  font-size: 13px;
  line-height: 1.5;
  word-break: break-word;
}

.note-tags {
  padding-top: 8px;
  border-top: 1px solid #e8eaed;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.tag-input {
  padding: 4px 8px;
  border: 1px solid #e8eaed;
  border-radius: 4px;
  font-size: 12px;
}

.tag-input:focus {
  outline: none;
  border-color: #1a73e8;
}

.tags-display {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.note-tag {
  background: #e8f5e9;
  color: #34a853;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 11px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.remove-tag {
  background: none;
  border: none;
  color: inherit;
  cursor: pointer;
  padding: 0;
  font-size: 10px;
}

.remove-tag:hover {
  opacity: 0.7;
}

.notes-stats {
  display: flex;
  gap: 16px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e8eaed;
  font-size: 12px;
  color: #5f6368;
}

@media (max-width: 768px) {
  .note-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .note-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
