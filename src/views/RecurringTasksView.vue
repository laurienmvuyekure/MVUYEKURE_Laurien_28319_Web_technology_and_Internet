<template>
  <div class="recurring-view">
    <RecurringTasks @task-generated="handleTaskGenerated" />
  </div>
</template>

<script>
import RecurringTasks from '../components/tasks/RecurringTasks.vue'

export default {
  name: 'RecurringView',
  components: {
    RecurringTasks
  },
  setup() {
    const handleTaskGenerated = (newTask) => {
      // Add task to tasks in localStorage
      const savedTasks = localStorage.getItem('tasks')
      let tasks = []
      if (savedTasks) {
        try {
          tasks = JSON.parse(savedTasks)
        } catch (e) {
          console.error('Failed to parse tasks:', e)
        }
      }
      tasks.push(newTask)
      localStorage.setItem('tasks', JSON.stringify(tasks))
    }

    return {
      handleTaskGenerated
    }
  }
}
</script>

<style scoped>
.recurring-view {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}
</style>
