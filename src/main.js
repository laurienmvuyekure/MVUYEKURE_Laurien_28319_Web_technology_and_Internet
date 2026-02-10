import { createApp } from 'vue'
import App from './App.vue'
import router from './router.js'

const app = createApp(App)

app.use(router)

// Global error handler
app.config.errorHandler = (err, instance, info) => {
  // Vue Error handling
  const liveRegion = document.getElementById('a11y-live-region')
  if (liveRegion) {
    liveRegion.textContent = `An error occurred: ${err.message}`
  }
}

app.mount('#app')
