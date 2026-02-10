<template>
  <div class="reminders-view">
    <TaskReminders />
  </div>
</template>

<script>
import TaskReminders from '../components/tasks/TaskReminders.vue'

export default {
  name: 'RemindersView',
  components: {
    TaskReminders
  }
}
</script>

<style scoped>
.reminders-view {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}
</style>
