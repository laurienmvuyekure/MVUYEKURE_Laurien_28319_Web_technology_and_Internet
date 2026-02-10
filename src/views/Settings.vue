<template>
  <div class="settings-page">
    <div class="settings-header">
      <h1>⚙️ Settings</h1>
      <p class="subtitle">Manage your preferences and app configuration</p>
    </div>

    <div class="settings-container">
      <!-- Display Settings -->
      <div class="settings-section">
        <h2>🎨 Display Settings</h2>
        <div class="setting-item">
          <label>Theme</label>
          <select v-model="settings.theme" @change="applySetting" class="select-input">
            <option value="light">Light</option>
            <option value="dark">Dark</option>
            <option value="auto">Auto (System)</option>
          </select>
        </div>

        <div class="setting-item">
          <label>Task View</label>
          <select v-model="settings.taskView" @change="applySetting" class="select-input">
            <option value="list">List View</option>
            <option value="grid">Grid View</option>
            <option value="kanban">Kanban Board</option>
          </select>
        </div>

        <div class="setting-item">
          <label>
            <input v-model="settings.compactMode" type="checkbox" class="checkbox-input" @change="applySetting" />
            <span>Compact mode</span>
          </label>
        </div>

        <div class="setting-item">
          <label>Items per page</label>
          <input v-model.number="settings.itemsPerPage" type="number" min="5" max="100" class="input-field" @change="applySetting" />
        </div>
      </div>

      <!-- Notification Settings -->
      <div class="settings-section">
        <h2>🔔 Notifications</h2>
        <div class="setting-item">
          <label>
            <input v-model="settings.enableNotifications" type="checkbox" class="checkbox-input" @change="applySetting" />
            <span>Enable notifications</span>
          </label>
        </div>

        <div class="setting-item">
          <label>
            <input v-model="settings.enableSoundAlerts" type="checkbox" class="checkbox-input" @change="applySetting" />
            <span>Enable sound alerts</span>
          </label>
        </div>

        <div class="setting-item">
          <label>Default reminder time</label>
          <select v-model.number="settings.defaultReminderMinutes" @change="applySetting" class="select-input">
            <option value="15">15 minutes</option>
            <option value="30">30 minutes</option>
            <option value="60">1 hour</option>
            <option value="1440">1 day</option>
          </select>
        </div>

        <div class="setting-item">
          <label>
            <input v-model="settings.notifyDueToday" type="checkbox" class="checkbox-input" @change="applySetting" />
            <span>Notify for tasks due today</span>
          </label>
        </div>
      </div>

      <!-- Task Settings -->
      <div class="settings-section">
        <h2>✅ Task Management</h2>
        <div class="setting-item">
          <label>Default task category</label>
          <select v-model="settings.defaultCategory" @change="applySetting" class="select-input">
            <option value="Personal">Personal</option>
            <option value="Work">Work</option>
            <option value="Study">Study</option>
            <option value="Health">Health</option>
            <option value="Finance">Finance</option>
          </select>
        </div>

        <div class="setting-item">
          <label>Default task priority</label>
          <select v-model="settings.defaultPriority" @change="applySetting" class="select-input">
            <option value="low">Low</option>
            <option value="medium">Medium</option>
            <option value="high">High</option>
          </select>
        </div>

        <div class="setting-item">
          <label>Default task duration (minutes)</label>
          <input v-model.number="settings.defaultDuration" type="number" min="1" max="480" class="input-field" @change="applySetting" />
        </div>

        <div class="setting-item">
          <label>
            <input v-model="settings.showCompletedTasks" type="checkbox" class="checkbox-input" @change="applySetting" />
            <span>Show completed tasks by default</span>
          </label>
        </div>
      </div>

      <!-- Time Settings -->
      <div class="settings-section">
        <h2>⏱️ Time Management</h2>
        <div class="setting-item">
          <label>Pomodoro focus duration (minutes)</label>
          <input v-model.number="settings.pomodoroFocusTime" type="number" min="1" max="120" class="input-field" @change="applySetting" />
        </div>

        <div class="setting-item">
          <label>Pomodoro break duration (minutes)</label>
          <input v-model.number="settings.pomodoroBreakTime" type="number" min="1" max="60" class="input-field" @change="applySetting" />
        </div>

        <div class="setting-item">
          <label>
            <input v-model="settings.enableAutoTimerStart" type="checkbox" class="checkbox-input" @change="applySetting" />
            <span>Auto-start timer when task begins</span>
          </label>
        </div>

        <div class="setting-item">
          <label>
            <input v-model="settings.enableTimeWarnings" type="checkbox" class="checkbox-input" @change="applySetting" />
            <span>Show warnings when time is running out</span>
          </label>
        </div>
      </div>

      <!-- Data & Privacy -->
      <div class="settings-section danger-zone">
        <h2>🔐 Data & Privacy</h2>
        <div class="setting-item">
          <label>
            <input v-model="settings.privateMode" type="checkbox" class="checkbox-input" @change="applySetting" />
            <span>Private mode (don't track time automatically)</span>
          </label>
        </div>

        <div class="setting-item">
          <button @click="exportData" class="btn-export">
            📥 Export Data (JSON)
          </button>
          <p class="setting-help">Download all your tasks and data</p>
        </div>

        <div class="setting-item">
          <button @click="importData" class="btn-import">
            📤 Import Data
          </button>
          <input 
            ref="importInput"
            type="file"
            accept=".json"
            style="display: none"
            @change="handleImport"
          />
          <p class="setting-help">Load previously exported data</p>
        </div>

        <div class="setting-item danger">
          <button @click="clearAllData" class="btn-danger">
            🗑️ Clear All Data
          </button>
          <p class="setting-help">⚠️ This will permanently delete all your tasks and data. This cannot be undone.</p>
        </div>
      </div>

      <!-- About -->
      <div class="settings-section">
        <h2>ℹ️ About</h2>
        <div class="setting-item">
          <p>
            <strong>Task Buddy v1.0</strong><br/>
            A comprehensive task and time management application<br/>
            <small>Built with Vue 3 | Vite | Local Storage</small>
          </p>
        </div>
      </div>
    </div>

    <div class="settings-footer">
      <button @click="resetToDefaults" class="btn-reset">Reset to Defaults</button>
      <button @click="saveAndClose" class="btn-save">✓ Save Settings</button>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'

export default {
  name: 'Settings',
  setup() {
    const importInput = ref(null)

    const defaultSettings = {
      theme: 'light',
      taskView: 'list',
      compactMode: false,
      itemsPerPage: 10,
      enableNotifications: true,
      enableSoundAlerts: false,
      defaultReminderMinutes: 30,
      notifyDueToday: true,
      defaultCategory: 'Personal',
      defaultPriority: 'medium',
      defaultDuration: 30,
      showCompletedTasks: false,
      pomodoroFocusTime: 25,
      pomodoroBreakTime: 5,
      enableAutoTimerStart: false,
      enableTimeWarnings: true,
      privateMode: false
    }

    const settings = ref({ ...defaultSettings })

    const loadSettings = () => {
      const saved = localStorage.getItem('app-settings')
      if (saved) {
        try {
          settings.value = { ...defaultSettings, ...JSON.parse(saved) }
        } catch (e) {
          console.error('Failed to load settings:', e)
        }
      }
    }

    const applySetting = () => {
      saveSettings()
    }

    const saveSettings = () => {
      localStorage.setItem('app-settings', JSON.stringify(settings.value))
    }

    const saveAndClose = () => {
      saveSettings()
      window.history.back()
    }

    const resetToDefaults = () => {
      if (confirm('Reset all settings to defaults?')) {
        settings.value = { ...defaultSettings }
        saveSettings()
      }
    }

    const exportData = () => {
      const data = {
        tasks: JSON.parse(localStorage.getItem('tasks') || '[]'),
        goals: JSON.parse(localStorage.getItem('goals') || '[]'),
        timeLog: JSON.parse(localStorage.getItem('timeLog') || '[]'),
        pomodoroStats: JSON.parse(localStorage.getItem('pomodoroStats') || '[]'),
        settings: settings.value,
        exportDate: new Date().toISOString()
      }

      const dataStr = JSON.stringify(data, null, 2)
      const dataBlob = new Blob([dataStr], { type: 'application/json' })
      const url = URL.createObjectURL(dataBlob)
      const link = document.createElement('a')
      link.href = url
      link.download = `task-buddy-export-${Date.now()}.json`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      URL.revokeObjectURL(url)
    }

    const importData = () => {
      if (importInput.value) {
        importInput.value.click()
      }
    }

    const handleImport = (event) => {
      const file = event.target.files?.[0]
      if (!file) return

      const reader = new FileReader()
      reader.onload = (e) => {
        try {
          const data = JSON.parse(e.target?.result || '{}')
          if (data.tasks) localStorage.setItem('tasks', JSON.stringify(data.tasks))
          if (data.goals) localStorage.setItem('goals', JSON.stringify(data.goals))
          if (data.timeLog) localStorage.setItem('timeLog', JSON.stringify(data.timeLog))
          if (data.pomodoroStats) localStorage.setItem('pomodoroStats', JSON.stringify(data.pomodoroStats))
          if (data.settings) {
            settings.value = { ...defaultSettings, ...data.settings }
            saveSettings()
          }
          alert('✅ Data imported successfully!')
          window.location.reload()
        } catch (err) {
          alert('❌ Failed to import data. Please check the file format.')
        }
      }
      reader.readAsText(file)
    }

    const clearAllData = () => {
      if (
        confirm('⚠️ Are you sure? This will permanently delete ALL your data including tasks, goals, and time logs.')
      ) {
        if (confirm('⚠️ This action CANNOT be undone. Delete everything?')) {
          localStorage.clear()
          alert('✅ All data cleared')
          window.location.href = '/'
        }
      }
    }

    loadSettings()

    return {
      settings,
      importInput,
      applySetting,
      saveAndClose,
      resetToDefaults,
      exportData,
      importData,
      handleImport,
      clearAllData
    }
  }
}
</script>

<style scoped>
.settings-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

.settings-header {
  max-width: 900px;
  margin: 0 auto 30px;
}

.settings-header h1 {
  color: #1a73e8;
  font-size: 32px;
  margin-bottom: 8px;
}

.subtitle {
  color: #5f6368;
  margin: 0;
}

.settings-container {
  max-width: 900px;
  margin: 0 auto 30px;
  display: grid;
  gap: 24px;
}

.settings-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.settings-section h2 {
  margin-top: 0;
  margin-bottom: 16px;
  color: #202124;
  font-size: 18px;
  border-bottom: 2px solid #f0f7ff;
  padding-bottom: 8px;
}

.settings-section.danger-zone {
  border-left: 4px solid #d32f2f;
}

.setting-item {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.setting-item label {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #202124;
  font-weight: 500;
  cursor: pointer;
}

.checkbox-input {
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.select-input,
.input-field {
  padding: 8px 12px;
  border: 1px solid #e8eaed;
  border-radius: 6px;
  font-family: inherit;
  font-size: 13px;
  min-width: 150px;
}

.select-input:focus,
.input-field:focus {
  outline: none;
  border-color: #1a73e8;
  box-shadow: 0 0 0 2px rgba(26, 115, 232, 0.1);
}

.btn-export,
.btn-import,
.btn-danger {
  padding: 10px 16px;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 13px;
}

.btn-export,
.btn-import {
  background: #1a73e8;
  color: white;
}

.btn-export:hover,
.btn-import:hover {
  background: #1557b0;
}

.btn-danger {
  background: #ffebee;
  color: #d32f2f;
  border: 1px solid #d32f2f;
}

.btn-danger:hover {
  background: #ffcdd2;
}

.setting-item.danger {
  flex-direction: column;
  align-items: flex-start;
}

.setting-help {
  margin: 0;
  font-size: 12px;
  color: #5f6368;
  margin-top: 4px;
}

.settings-footer {
  max-width: 900px;
  margin: 0 auto;
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.btn-reset,
.btn-save {
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
}

.btn-reset {
  background: white;
  color: #5f6368;
  border: 1px solid #e8eaed;
}

.btn-reset:hover {
  background: #f5f5f5;
}

.btn-save {
  background: #1a73e8;
  color: white;
}

.btn-save:hover {
  background: #1557b0;
}

@media (max-width: 768px) {
  .settings-page {
    padding: 12px;
  }

  .settings-section {
    padding: 16px;
  }

  .setting-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .select-input,
  .input-field {
    width: 100%;
  }

  .settings-footer {
    flex-direction: column;
    justify-content: flex-start;
  }

  .btn-reset,
  .btn-save {
    width: 100%;
  }
}
</style>
