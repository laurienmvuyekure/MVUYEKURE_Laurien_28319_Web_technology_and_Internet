<template>
  <div class="documents-view">
    <DocumentManager />
  </div>
</template>

<script>
import DocumentManager from '../components/documents/DocumentManager.vue'

export default {
  name: 'DocumentsView',
  components: {
    DocumentManager
  }
}
</script>

<style scoped>
.documents-view {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}
</style>
