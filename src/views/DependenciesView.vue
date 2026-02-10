<template>
  <div class="dependencies-view">
    <DependencyManager />
  </div>
</template>

<script>
import DependencyManager from '../components/tasks/DependencyManager.vue'

export default {
  name: 'DependenciesView',
  components: {
    DependencyManager
  }
}
</script>

<style scoped>
.dependencies-view {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 24px;
}
</style>
