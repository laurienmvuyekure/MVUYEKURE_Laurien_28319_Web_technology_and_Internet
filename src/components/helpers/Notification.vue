<template>
  <div 
    class="notification" 
    :class="type"
    role="alert"
    :aria-live="type === 'error' ? 'assertive' : 'polite'"
  >
    <div class="notification-content">
      <span class="notification-icon" aria-hidden="true">
        {{ getIcon(type) }}
      </span>
      <div class="notification-message">
        <strong>{{ getTitle(type) }}</strong>
        <p>{{ notification.message }}</p>
        <span class="notification-time">
          {{ formatTime(notification.timestamp) }}
        </span>
      </div>
    </div>
    <button 
      @click="$emit('dismiss', notification.id)"
      class="dismiss-button"
      aria-label="Dismiss notification"
    >
      ×
    </button>
  </div>
</template>

<script>
export default {
  name: 'HelpersNotification',
  props: {
    notification: {
      type: Object,
      required: true
    }
  },
  emits: ['dismiss'],
  computed: {
    type() {
      return this.notification.type || 'info'
    }
  },
  methods: {
    getIcon(type) {
      const icons = {
        success: '✅',
        error: '❌',
        warning: '⚠️',
        info: 'ℹ️'
      }
      return icons[type] || 'ℹ️'
    },
    getTitle(type) {
      const titles = {
        success: 'Success',
        error: 'Error',
        warning: 'Warning',
        info: 'Info'
      }
      return titles[type] || 'Notification'
    },
    formatTime(timestamp) {
      const now = new Date()
      const diff = now - new Date(timestamp)
      
      if (diff < 60000) return 'just now'
      if (diff < 3600000) return `${Math.floor(diff / 60000)}m ago`
      if (diff < 86400000) return `${Math.floor(diff / 3600000)}h ago`
      return `${Math.floor(diff / 86400000)}d ago`
    }
  }
}
</script>

<style scoped>
.notification {
  position: relative;
  min-width: 320px;
  max-width: 400px;
  padding: 16px;
  border-radius: 8px;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  animation: slideIn 0.3s ease;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

.notification-content {
  display: flex;
  gap: 12px;
  flex: 1;
}

.notification-icon {
  font-size: 20px;
  flex-shrink: 0;
  margin-top: 2px;
}

.notification-message {
  flex: 1;
  font-size: 14px;
  line-height: 1.4;
}

.notification-time {
  display: block;
  font-size: 12px;
  color: rgba(255,255,255,0.8);
  margin-top: 4px;
}

.dismiss-button {
  background: none;
  border: none;
  color: white;
  font-size: 20px;
  cursor: pointer;
  padding: 0;
  width: 24px;
  height: 24px;
  flex-shrink: 0;
  opacity: 0.8;
  transition: opacity 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.dismiss-button:hover,
.dismiss-button:focus {
  opacity: 1;
  outline: none;
}

.notification.success {
  background: #4caf50;
  color: white;
}

.notification.error {
  background: #f44336;
  color: white;
}

.notification.warning {
  background: #ff9800;
  color: white;
}

.notification.info {
  background: #2196f3;
  color: white;
}

@keyframes slideIn {
  from {
    transform: translateX(120%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@keyframes slideOut {
  from {
    transform: translateX(0);
    opacity: 1;
  }
  to {
    transform: translateX(120%);
    opacity: 0;
  }
}

@media (prefers-reduced-motion: reduce) {
  .notification {
    animation: none;
  }
}

@media (max-width: 768px) {
  .notification {
    min-width: auto;
    max-width: 90vw;
  }
}
</style>