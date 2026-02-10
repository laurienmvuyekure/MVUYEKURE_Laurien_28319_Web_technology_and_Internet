<template>
  <div class="time-blocker">
    <h1>🕒 Time Blocker</h1>
    <p class="description">Block out your day with focused time slots for maximum productivity</p>

    <div class="time-blocker-controls">
      <div class="control-group">
        <label for="block-date">Select Date</label>
        <input
          id="block-date"
          v-model="selectedDate"
          type="date"
          class="date-input"
        />
      </div>

      <div class="control-group">
        <label for="block-title">Block Title</label>
        <input
          id="block-title"
          v-model="blockTitle"
          type="text"
          placeholder="e.g., Deep Work Session"
          class="title-input"
        />
      </div>

      <div class="control-group">
        <label for="block-start">Start Time</label>
        <input
          id="block-start"
          v-model="blockStart"
          type="time"
          class="time-input"
        />
      </div>

      <div class="control-group">
        <label for="block-end">End Time</label>
        <input
          id="block-end"
          v-model="blockEnd"
          type="time"
          class="time-input"
        />
      </div>

      <div class="control-group">
        <label for="block-category">Category</label>
        <select v-model="blockCategory" id="block-category" class="category-select">
          <option value="">Select category</option>
          <option value="work">Work</option>
          <option value="personal">Personal</option>
          <option value="learning">Learning</option>
          <option value="health">Health</option>
          <option value="meetings">Meetings</option>
        </select>
      </div>

      <button @click="addTimeBlock" class="add-block-btn" :disabled="!isFormValid">
        ➕ Add Time Block
      </button>
    </div>

    <div class="time-blocks-list">
      <h2>Today's Time Blocks</h2>

      <div v-if="filteredBlocks.length === 0" class="no-blocks">
        <p>No time blocks for this date. Create your first one above!</p>
      </div>

      <div v-else class="blocks-container">
        <div
          v-for="block in filteredBlocks"
          :key="block.id"
          :class="['time-block-item', block.category]"
        >
          <div class="block-header">
            <h3 class="block-title">{{ block.title }}</h3>
            <span class="block-category">{{ block.category }}</span>
          </div>

          <div class="block-time">
            <span class="time-range">{{ formatTime(block.start) }} - {{ formatTime(block.end) }}</span>
            <span class="duration">({{ calculateDuration(block.start, block.end) }} min)</span>
          </div>

          <div class="block-actions">
            <button @click="editBlock(block)" class="edit-btn">✏️ Edit</button>
            <button @click="deleteBlock(block.id)" class="delete-btn">🗑️ Delete</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Edit Modal -->
    <div v-if="editingBlock" class="modal-overlay" @click="closeEditModal">
      <div class="modal-content" @click.stop>
        <h3>Edit Time Block</h3>

        <div class="modal-form">
          <div class="form-group">
            <label>Title</label>
            <input v-model="editForm.title" type="text" class="modal-input">
          </div>

          <div class="form-group">
            <label>Start Time</label>
            <input v-model="editForm.start" type="time" class="modal-input">
          </div>

          <div class="form-group">
            <label>End Time</label>
            <input v-model="editForm.end" type="time" class="modal-input">
          </div>

          <div class="form-group">
            <label>Category</label>
            <select v-model="editForm.category" class="modal-select">
              <option value="work">Work</option>
              <option value="personal">Personal</option>
              <option value="learning">Learning</option>
              <option value="health">Health</option>
              <option value="meetings">Meetings</option>
            </select>
          </div>
        </div>

        <div class="modal-actions">
          <button @click="saveEdit" class="save-btn">💾 Save</button>
          <button @click="closeEditModal" class="cancel-btn">❌ Cancel</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'

export default {
  name: 'TimeBlocker',
  setup() {
    const selectedDate = ref(new Date().toISOString().split('T')[0])
    const blockTitle = ref('')
    const blockStart = ref('')
    const blockEnd = ref('')
    const blockCategory = ref('')

    const timeBlocks = ref([])
    const editingBlock = ref(null)
    const editForm = ref({
      title: '',
      start: '',
      end: '',
      category: ''
    })

    // Load time blocks from localStorage
    onMounted(() => {
      const saved = localStorage.getItem('timeBlocks')
      if (saved) {
        timeBlocks.value = JSON.parse(saved)
      }
    })

    const filteredBlocks = computed(() => {
      return timeBlocks.value
        .filter(block => block.date === selectedDate.value)
        .sort((a, b) => a.start.localeCompare(b.start))
    })

    const isFormValid = computed(() => {
      return blockTitle.value.trim() &&
             blockStart.value &&
             blockEnd.value &&
             blockCategory.value &&
             blockStart.value < blockEnd.value
    })

    const addTimeBlock = () => {
      if (!isFormValid.value) return

      const newBlock = {
        id: Date.now(),
        date: selectedDate.value,
        title: blockTitle.value.trim(),
        start: blockStart.value,
        end: blockEnd.value,
        category: blockCategory.value
      }

      timeBlocks.value.push(newBlock)
      saveTimeBlocks()

      // Reset form
      blockTitle.value = ''
      blockStart.value = ''
      blockEnd.value = ''
      blockCategory.value = ''
    }

    const editBlock = (block) => {
      editingBlock.value = block
      editForm.value = {
        title: block.title,
        start: block.start,
        end: block.end,
        category: block.category
      }
    }

    const saveEdit = () => {
      if (!editingBlock.value) return

      const index = timeBlocks.value.findIndex(b => b.id === editingBlock.value.id)
      if (index > -1) {
        timeBlocks.value[index] = {
          ...timeBlocks.value[index],
          ...editForm.value
        }
        saveTimeBlocks()
      }

      closeEditModal()
    }

    const closeEditModal = () => {
      editingBlock.value = null
      editForm.value = { title: '', start: '', end: '', category: '' }
    }

    const deleteBlock = (id) => {
      if (confirm('Are you sure you want to delete this time block?')) {
        timeBlocks.value = timeBlocks.value.filter(block => block.id !== id)
        saveTimeBlocks()
      }
    }

    const saveTimeBlocks = () => {
      localStorage.setItem('timeBlocks', JSON.stringify(timeBlocks.value))
    }

    const formatTime = (timeStr) => {
      const [hours, minutes] = timeStr.split(':')
      const hour = parseInt(hours)
      const ampm = hour >= 12 ? 'PM' : 'AM'
      const displayHour = hour % 12 || 12
      return `${displayHour}:${minutes} ${ampm}`
    }

    const calculateDuration = (start, end) => {
      const startDate = new Date(`2000-01-01T${start}`)
      const endDate = new Date(`2000-01-01T${end}`)
      return Math.round((endDate - startDate) / (1000 * 60))
    }

    return {
      selectedDate,
      blockTitle,
      blockStart,
      blockEnd,
      blockCategory,
      timeBlocks,
      filteredBlocks,
      editingBlock,
      editForm,
      isFormValid,
      addTimeBlock,
      editBlock,
      saveEdit,
      closeEditModal,
      deleteBlock,
      formatTime,
      calculateDuration
    }
  }
}
</script>

<style scoped>
.time-blocker {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.time-blocker h1 {
  color: #1a73e8;
  font-size: 32px;
  margin-bottom: 8px;
}

.description {
  color: #5a5f66;
  margin-bottom: 30px;
  font-size: 16px;
}

.time-blocker-controls {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 40px;
  padding: 24px;
  background: white;
  border-radius: 8px;
  border: 1px solid #d0d2d7;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.control-group {
  display: flex;
  flex-direction: column;
}

.control-group label {
  font-weight: 500;
  color: #202124;
  margin-bottom: 6px;
  font-size: 14px;
}

.date-input,
.title-input,
.time-input,
.category-select {
  padding: 10px 12px;
  border: 1px solid #d0d2d7;
  border-radius: 6px;
  font-size: 14px;
  font-family: inherit;
}

.date-input:focus,
.title-input:focus,
.time-input:focus,
.category-select:focus {
  border-color: #1a73e8;
  outline: none;
  box-shadow: 0 0 0 3px rgba(26, 115, 232, 0.1);
}

.add-block-btn {
  padding: 12px 20px;
  background: #1a73e8;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  align-self: end;
}

.add-block-btn:hover:not(:disabled) {
  background: #1557b0;
  transform: translateY(-1px);
}

.add-block-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.time-blocks-list h2 {
  color: #202124;
  font-size: 24px;
  margin-bottom: 20px;
}

.no-blocks {
  text-align: center;
  padding: 40px 20px;
  color: #5a5f66;
  background: white;
  border-radius: 8px;
  border: 1px solid #d0d2d7;
}

.blocks-container {
  display: grid;
  gap: 16px;
}

.time-block-item {
  background: white;
  border: 1px solid #d0d2d7;
  border-radius: 8px;
  padding: 20px;
  transition: all 0.2s;
}

.time-block-item:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  border-color: #70777f;
}

.time-block-item.work {
  border-left: 4px solid #1a73e8;
}

.time-block-item.personal {
  border-left: 4px solid #188038;
}

.time-block-item.learning {
  border-left: 4px solid #e8c547;
}

.time-block-item.health {
  border-left: 4px solid #ea4335;
}

.time-block-item.meetings {
  border-left: 4px solid #9334e6;
}

.block-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.block-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #202124;
}

.block-category {
  padding: 4px 12px;
  background: #f5f6f7;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  text-transform: capitalize;
  color: #5a5f66;
}

.block-time {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  font-size: 14px;
  color: #5a5f66;
}

.time-range {
  font-weight: 500;
}

.duration {
  color: #1a73e8;
}

.block-actions {
  display: flex;
  gap: 8px;
}

.edit-btn,
.delete-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.edit-btn {
  background: #fef8e3;
  color: #e8c547;
}

.edit-btn:hover {
  background: #e8c547;
  color: white;
}

.delete-btn {
  background: #fce4e3;
  color: #ea4335;
}

.delete-btn:hover {
  background: #ea4335;
  color: white;
}

/* Modal Styles */
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
  padding: 24px;
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.modal-content h3 {
  margin: 0 0 20px 0;
  color: #202124;
}

.modal-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  font-weight: 500;
  margin-bottom: 4px;
  color: #202124;
}

.modal-input,
.modal-select {
  padding: 10px;
  border: 1px solid #d0d2d7;
  border-radius: 4px;
  font-size: 14px;
}

.modal-actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
  justify-content: flex-end;
}

.save-btn,
.cancel-btn {
  padding: 10px 16px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.save-btn {
  background: #1a73e8;
  color: white;
}

.save-btn:hover {
  background: #1557b0;
}

.cancel-btn {
  background: #f5f6f7;
  color: #5a5f66;
}

.cancel-btn:hover {
  background: #eaeced;
}

@media (max-width: 768px) {
  .time-blocker-controls {
    grid-template-columns: 1fr;
  }

  .block-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .block-actions {
    justify-content: flex-end;
  }

  .modal-content {
    margin: 20px;
    width: calc(100% - 40px);
  }
}
</style>
