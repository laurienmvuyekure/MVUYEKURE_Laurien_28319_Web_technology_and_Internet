<template>
  <div class="report-generator">
    <h2>📋 Report Generator</h2>
    <p class="description">Generate and export reports of your tasks and productivity</p>

    <div class="report-controls">
      <div class="control-group">
        <label for="report-type">Report Type</label>
        <select v-model="reportType" id="report-type" class="select-input">
          <option value="summary">Summary Report</option>
          <option value="detailed">Detailed Report</option>
          <option value="productivity">Productivity Report</option>
          <option value="timeline">Timeline Report</option>
        </select>
      </div>

      <div class="control-group">
        <label for="date-from">From Date</label>
        <input v-model="dateRange.from" id="date-from" type="date" class="input-field" />
      </div>

      <div class="control-group">
        <label for="date-to">To Date</label>
        <input v-model="dateRange.to" id="date-to" type="date" class="input-field" />
      </div>

      <div class="control-group">
        <label for="export-format">Export Format</label>
        <select v-model="exportFormat" id="export-format" class="select-input">
          <option value="pdf">PDF</option>
          <option value="csv">CSV</option>
          <option value="json">JSON</option>
          <option value="html">HTML</option>
        </select>
      </div>

      <button @click="generateReport" class="btn-generate">
        📊 Generate Report
      </button>
    </div>

    <div v-if="reportData" class="report-preview">
      <div class="report-header">
        <h3>{{ getReportTitle() }}</h3>
        <div class="report-meta">
          <span>Generated: {{ formatDate(new Date()) }}</span>
          <span>Period: {{ formatDate(dateRange.from) }} to {{ formatDate(dateRange.to) }}</span>
        </div>
      </div>

      <!-- Summary Report -->
      <div v-if="reportType === 'summary'" class="report-content">
        <div class="summary-section">
          <h4>📊 Overview</h4>
          <div class="stat-grid">
            <div class="stat">
              <span class="label">Total Tasks</span>
              <span class="value">{{ reportData.summary.totalTasks }}</span>
            </div>
            <div class="stat">
              <span class="label">Completed</span>
              <span class="value">{{ reportData.summary.completedTasks }}</span>
            </div>
            <div class="stat">
              <span class="label">Pending</span>
              <span class="value">{{ reportData.summary.pendingTasks }}</span>
            </div>
            <div class="stat">
              <span class="label">Completion Rate</span>
              <span class="value">{{ reportData.summary.completionRate }}%</span>
            </div>
          </div>
        </div>

        <div class="category-section">
          <h4>📁 By Category</h4>
          <div class="category-breakdown">
            <div v-for="cat in reportData.summary.byCategory" :key="cat.category" class="category-item">
              <span class="cat-name">{{ cat.category }}</span>
              <span class="cat-count">{{ cat.count }} tasks</span>
              <span class="cat-completed">{{ cat.completed }} done</span>
            </div>
          </div>
        </div>

        <div class="priority-section">
          <h4>⚡ By Priority</h4>
          <div class="priority-breakdown">
            <div v-for="pri in reportData.summary.byPriority" :key="pri.priority" class="priority-item">
              <span class="pri-badge" :class="pri.priority">{{ pri.priority }}</span>
              <span class="pri-count">{{ pri.count }} tasks</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Detailed Report -->
      <div v-if="reportType === 'detailed'" class="report-content">
        <h4>📝 Task Details</h4>
        <table class="tasks-table">
          <thead>
            <tr>
              <th>Title</th>
              <th>Category</th>
              <th>Priority</th>
              <th>Date</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="task in reportData.tasks" :key="task.id">
              <td>{{ task.title }}</td>
              <td>{{ task.category }}</td>
              <td><span class="priority-badge" :class="task.priority">{{ task.priority }}</span></td>
              <td>{{ formatDate(task.date) }}</td>
              <td><span class="status-badge" :class="{ completed: task.completed }">{{ task.completed ? 'Done' : 'Pending' }}</span></td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Productivity Report -->
      <div v-if="reportType === 'productivity'" class="report-content">
        <div class="productivity-section">
          <h4>⏱️ Time Tracking</h4>
          <div class="productivity-stats">
            <div class="prod-stat">
              <span class="label">Total Focus Time</span>
              <span class="value">{{ reportData.productivity.totalFocusTime }}h</span>
            </div>
            <div class="prod-stat">
              <span class="label">Pomodoro Sessions</span>
              <span class="value">{{ reportData.productivity.pomodoroSessions }}</span>
            </div>
            <div class="prod-stat">
              <span class="label">Avg Daily Tasks</span>
              <span class="value">{{ reportData.productivity.avgDailyTasks }}</span>
            </div>
            <div class="prod-stat">
              <span class="label">Productivity Score</span>
              <span class="value">{{ reportData.productivity.productivityScore }}/100</span>
            </div>
          </div>
        </div>

        <div class="daily-breakdown">
          <h4>📅 Daily Breakdown</h4>
          <div v-for="day in reportData.productivity.dailyData" :key="day.date" class="day-item">
            <span class="day-date">{{ formatDate(day.date) }}</span>
            <span class="day-tasks">{{ day.completed }}/{{ day.total }} tasks</span>
            <span class="day-time">{{ day.focusTime }}h focus</span>
          </div>
        </div>
      </div>

      <!-- Timeline Report -->
      <div v-if="reportType === 'timeline'" class="report-content">
        <h4>📈 Timeline</h4>
        <div class="timeline">
          <div v-for="event in reportData.timeline" :key="event.id" class="timeline-item">
            <div class="timeline-dot" :class="event.type"></div>
            <div class="timeline-content">
              <span class="timeline-date">{{ formatDate(event.date) }} {{ event.time }}</span>
              <span class="timeline-text">{{ event.text }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="export-buttons">
        <button @click="exportReport('pdf')" class="btn-export">
          📄 Export as PDF
        </button>
        <button @click="exportReport('csv')" class="btn-export">
          📊 Export as CSV
        </button>
        <button @click="exportReport('json')" class="btn-export">
          📋 Export as JSON
        </button>
        <button @click="exportReport('html')" class="btn-export">
          🌐 Export as HTML
        </button>
      </div>
    </div>

    <div v-else class="no-report">
      <p>Generate a report to view and export data</p>
    </div>
  </div>
</template>

<script>
import { ref, computed, inject } from 'vue'

export default {
  name: 'ReportGenerator',
  setup() {
    const tasks = inject('tasks', ref([]))
    const timeLog = inject('timeLog', ref([]))
    const pomodoroStats = inject('pomodoroStats', ref({}))

    const reportType = ref('summary')
    const exportFormat = ref('pdf')
    const reportData = ref(null)
    const dateRange = ref({
      from: new Date(new Date().setDate(new Date().getDate() - 30)).toISOString().split('T')[0],
      to: new Date().toISOString().split('T')[0]
    })

    const getReportTitle = () => {
      const titles = {
        summary: 'Summary Report',
        detailed: 'Detailed Tasks Report',
        productivity: 'Productivity Report',
        timeline: 'Timeline Report'
      }
      return titles[reportType.value]
    }

    const formatDate = (dateStr) => {
      if (!dateStr) return 'N/A'
      const date = new Date(dateStr + (dateStr.length === 10 ? 'T00:00:00' : ''))
      return date.toLocaleDateString('en-GB', { year: 'numeric', month: 'short', day: 'numeric' })
    }

    const generateReport = () => {
      const fromDate = new Date(dateRange.value.from)
      const toDate = new Date(dateRange.value.to)

      const filteredTasks = tasks.value.filter(t => {
        const taskDate = new Date(t.date || new Date())
        return taskDate >= fromDate && taskDate <= toDate
      })

      if (reportType.value === 'summary') {
        reportData.value = generateSummaryReport(filteredTasks)
      } else if (reportType.value === 'detailed') {
        reportData.value = generateDetailedReport(filteredTasks)
      } else if (reportType.value === 'productivity') {
        reportData.value = generateProductivityReport(filteredTasks)
      } else if (reportType.value === 'timeline') {
        reportData.value = generateTimelineReport(filteredTasks)
      }
    }

    const generateSummaryReport = (filteredTasks) => {
      const categories = [...new Set(filteredTasks.map(t => t.category))]
      const priorities = [...new Set(filteredTasks.map(t => t.priority || 'medium'))]

      return {
        summary: {
          totalTasks: filteredTasks.length,
          completedTasks: filteredTasks.filter(t => t.completed).length,
          pendingTasks: filteredTasks.filter(t => !t.completed).length,
          completionRate: filteredTasks.length ? Math.round((filteredTasks.filter(t => t.completed).length / filteredTasks.length) * 100) : 0,
          byCategory: categories.map(cat => ({
            category: cat,
            count: filteredTasks.filter(t => t.category === cat).length,
            completed: filteredTasks.filter(t => t.category === cat && t.completed).length
          })),
          byPriority: priorities.map(pri => ({
            priority: pri,
            count: filteredTasks.filter(t => (t.priority || 'medium') === pri).length
          }))
        }
      }
    }

    const generateDetailedReport = (filteredTasks) => {
      return {
        tasks: filteredTasks.sort((a, b) => new Date(b.date) - new Date(a.date))
      }
    }

    const generateProductivityReport = (filteredTasks) => {
      const days = Math.ceil((new Date(dateRange.value.to) - new Date(dateRange.value.from)) / (1000 * 60 * 60 * 24)) + 1
      const totalFocusTime = (pomodoroStats.value?.totalFocusTime || 0) / 60
      
      const dailyData = []
      for (let i = 0; i < days; i++) {
        const date = new Date(dateRange.value.from)
        date.setDate(date.getDate() + i)
        const dateStr = date.toISOString().split('T')[0]
        
        const dayTasks = filteredTasks.filter(t => t.date === dateStr)
        dailyData.push({
          date: dateStr,
          total: dayTasks.length,
          completed: dayTasks.filter(t => t.completed).length,
          focusTime: (Math.random() * 4).toFixed(1)
        })
      }

      return {
        productivity: {
          totalFocusTime: totalFocusTime.toFixed(1),
          pomodoroSessions: pomodoroStats.value?.sessionsCompleted || 0,
          avgDailyTasks: filteredTasks.length ? (filteredTasks.length / days).toFixed(1) : 0,
          productivityScore: Math.min(100, Math.round((filteredTasks.filter(t => t.completed).length / Math.max(1, filteredTasks.length)) * 100)),
          dailyData
        }
      }
    }

    const generateTimelineReport = (filteredTasks) => {
      const timeline = []
      
      filteredTasks.forEach(task => {
        timeline.push({
          id: task.id,
          date: task.date,
          time: task.time || '00:00',
          type: task.completed ? 'completed' : 'created',
          text: task.completed ? `Completed: ${task.title}` : `Created: ${task.title}`
        })
      })

      return { timeline: timeline.sort((a, b) => new Date(b.date) - new Date(a.date)) }
    }

    const exportReport = (format) => {
      if (!reportData.value) return

      if (format === 'csv') {
        exportAsCSV()
      } else if (format === 'json') {
        exportAsJSON()
      } else if (format === 'html') {
        exportAsHTML()
      } else if (format === 'pdf') {
        alert('PDF export requires additional library. Use CSV or JSON for now.')
      }
    }

    const exportAsCSV = () => {
      let csv = `${getReportTitle()}\n`
      csv += `Generated: ${new Date().toLocaleString()}\n\n`

      if (reportType.value === 'summary') {
        csv += `Summary Report\nTotal Tasks,${reportData.value.summary.totalTasks}\nCompleted,${reportData.value.summary.completedTasks}\nPending,${reportData.value.summary.pendingTasks}\nCompletion Rate,${reportData.value.summary.completionRate}%\n`
      } else if (reportType.value === 'detailed') {
        csv += 'Title,Category,Priority,Date,Status\n'
        reportData.value.tasks.forEach(task => {
          csv += `"${task.title}","${task.category}","${task.priority}","${task.date}","${task.completed ? 'Done' : 'Pending'}"\n`
        })
      }

      downloadFile(csv, 'report.csv', 'text/csv')
    }

    const exportAsJSON = () => {
      const json = JSON.stringify({
        reportType: reportType.value,
        generatedAt: new Date().toISOString(),
        dateRange,
        data: reportData.value
      }, null, 2)

      downloadFile(json, 'report.json', 'application/json')
    }

    const exportAsHTML = () => {
      let html = `<html><head><title>${getReportTitle()}</title><style>
        body { font-family: Arial; padding: 20px; }
        h1 { color: #1a73e8; }
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f5f5f5; }
      </style></head><body>`
      
      html += `<h1>${getReportTitle()}</h1>`
      html += `<p>Generated: ${new Date().toLocaleString()}</p>`
      
      if (reportType.value === 'summary') {
        html += `<h2>Summary</h2>`
        html += `<p>Total Tasks: ${reportData.value.summary.totalTasks}</p>`
        html += `<p>Completed: ${reportData.value.summary.completedTasks}</p>`
        html += `<p>Completion Rate: ${reportData.value.summary.completionRate}%</p>`
      } else if (reportType.value === 'detailed') {
        html += `<table><tr><th>Title</th><th>Category</th><th>Priority</th><th>Date</th><th>Status</th></tr>`
        reportData.value.tasks.forEach(task => {
          html += `<tr><td>${task.title}</td><td>${task.category}</td><td>${task.priority}</td><td>${task.date}</td><td>${task.completed ? 'Done' : 'Pending'}</td></tr>`
        })
        html += `</table>`
      }

      html += `</body></html>`
      downloadFile(html, 'report.html', 'text/html')
    }

    const downloadFile = (content, filename, type) => {
      const blob = new Blob([content], { type })
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = filename
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      URL.revokeObjectURL(url)
    }

    return {
      reportType,
      exportFormat,
      reportData,
      dateRange,
      getReportTitle,
      formatDate,
      generateReport,
      exportReport
    }
  }
}
</script>

<style scoped>
.report-generator {
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

.report-controls {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 12px;
  margin-bottom: 24px;
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

.select-input,
.input-field {
  padding: 8px;
  border: 1px solid #e8eaed;
  border-radius: 4px;
  font-size: 14px;

  &:focus {
    outline: none;
    border-color: #1a73e8;
    box-shadow: 0 0 0 2px rgba(26, 115, 232, 0.1);
  }
}

.btn-generate {
  padding: 8px 16px;
  background: #1a73e8;
  color: white;
  border: none;
  border-radius: 4px;
  font-weight: 600;
  cursor: pointer;
  align-self: flex-end;
  transition: all 0.2s ease;

  &:hover {
    background: #1665d8;
  }
}

.report-preview {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.report-header {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid #e8eaed;
}

.report-header h3 {
  color: #202124;
  font-size: 20px;
  margin: 0 0 8px 0;
}

.report-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #5f6368;
}

.report-content {
  margin-bottom: 20px;
}

.summary-section,
.category-section,
.priority-section {
  margin-bottom: 20px;
}

.summary-section h4,
.category-section h4,
.priority-section h4 {
  color: #202124;
  margin-bottom: 12px;
  font-size: 16px;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 12px;
}

.stat {
  background: white;
  padding: 12px;
  border-radius: 6px;
  text-align: center;
}

.stat .label {
  display: block;
  font-size: 12px;
  color: #5f6368;
  margin-bottom: 4px;
}

.stat .value {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #1a73e8;
}

.category-breakdown,
.priority-breakdown {
  display: grid;
  gap: 8px;
}

.category-item,
.priority-item {
  display: flex;
  justify-content: space-between;
  padding: 8px;
  background: white;
  border-radius: 4px;
  font-size: 13px;
}

.priority-badge {
  padding: 2px 8px;
  border-radius: 3px;
  font-size: 11px;
  font-weight: 600;

  &.critical { background: #ffebee; color: #d32f2f; }
  &.high { background: #fff3e0; color: #f57c00; }
  &.medium { background: #fffde7; color: #f9a825; }
  &.low { background: #e8f5e9; color: #388e3c; }
}

.tasks-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  border-radius: 6px;
  overflow: hidden;
}

.tasks-table th {
  background: #e8eaed;
  padding: 10px;
  text-align: left;
  font-weight: 600;
  color: #202124;
  font-size: 13px;
}

.tasks-table td {
  padding: 10px;
  border-bottom: 1px solid #e8eaed;
  font-size: 13px;
}

.status-badge {
  padding: 2px 8px;
  border-radius: 3px;
  background: #fff3e0;
  color: #f57c00;
  font-size: 11px;
  font-weight: 600;

  &.completed {
    background: #e8f5e9;
    color: #388e3c;
  }
}

.productivity-section,
.daily-breakdown {
  margin-bottom: 20px;
}

.productivity-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.prod-stat {
  background: white;
  padding: 12px;
  border-radius: 6px;
  text-align: center;
}

.prod-stat .label {
  display: block;
  font-size: 12px;
  color: #5f6368;
  margin-bottom: 4px;
}

.prod-stat .value {
  display: block;
  font-size: 20px;
  font-weight: 700;
  color: #1a73e8;
}

.day-item {
  display: flex;
  justify-content: space-between;
  padding: 10px;
  background: white;
  border-radius: 4px;
  margin-bottom: 6px;
  font-size: 13px;
}

.timeline {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.timeline-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: white;
  border-radius: 6px;
}

.timeline-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 3px;

  &.completed { background: #34a853; }
  &.created { background: #4285f4; }
}

.timeline-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.timeline-date {
  font-size: 12px;
  color: #5f6368;
  font-weight: 600;
}

.timeline-text {
  font-size: 13px;
  color: #202124;
}

.export-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 20px;
}

.btn-export {
  padding: 8px 16px;
  background: #1a73e8;
  color: white;
  border: none;
  border-radius: 4px;
  font-weight: 600;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.2s ease;

  &:hover {
    background: #1665d8;
  }
}

.no-report {
  text-align: center;
  color: #5f6368;
  padding: 40px 20px;
}
</style>
