<template>
  <div class="accessibility-widget">
    <button 
      @click="toggleWidget"
      class="widget-toggle"
      :aria-expanded="isOpen"
      aria-label="Accessibility settings"
    >
      <span aria-hidden="true">♿</span>
    </button>
    
    <div v-if="isOpen" class="widget-panel" role="dialog" aria-label="Accessibility options">
      <div class="widget-header">
        <h3>Accessibility</h3>
        <button @click="toggleWidget" class="close-button" aria-label="Close">
          &times;
        </button>
      </div>
      
      <div class="widget-body">
        <div class="control-group">
          <label>Text Size</label>
          <div class="size-controls">
            <button 
              v-for="size in sizes" 
              :key="size.value"
              @click="setFontSize(size.value)"
              :class="['size-button', { active: fontSize === size.value }]"
              :aria-label="`Set text size to ${size.label}`"
            >
              {{ size.label }}
            </button>
          </div>
        </div>
        
        <div class="control-group">
          <label>Theme</label>
          <div class="theme-controls">
            <button 
              v-for="theme in themes" 
              :key="theme.value"
              @click="setTheme(theme.value)"
              :class="['theme-button', { active: currentTheme === theme.value }]"
              :aria-label="`Set ${theme.label} theme`"
              :style="{ backgroundColor: theme.color }"
            >
              <span class="sr-only">{{ theme.label }}</span>
            </button>
          </div>
        </div>
        
        <div class="control-group">
          <div class="toggle-control">
            <input 
              type="checkbox" 
              id="high-contrast"
              v-model="highContrast"
              @change="toggleContrast"
            />
            <label for="high-contrast">High Contrast</label>
          </div>
          
          <div class="toggle-control">
            <input 
              type="checkbox" 
              id="reduced-motion"
              v-model="reducedMotion"
              @change="toggleMotion"
            />
            <label for="reduced-motion">Reduce Motion</label>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'

export default {
  name: 'HelpersAccessibilityControls',
  setup() {
    const isOpen = ref(false)
    const fontSize = ref('medium')
    const currentTheme = ref('light')
    const highContrast = ref(false)
    const reducedMotion = ref(false)
    
    const sizes = [
      { value: 'small', label: 'S' },
      { value: 'medium', label: 'M' },
      { value: 'large', label: 'L' },
      { value: 'x-large', label: 'XL' }
    ]
    
    const themes = [
      { value: 'light', label: 'Light', color: '#ffffff' },
      { value: 'dark', label: 'Dark', color: '#1a1a1a' },
      { value: 'blue', label: 'Blue', color: '#e6f2ff' },
      { value: 'sepia', label: 'Sepia', color: '#f4ecd8' }
    ]
    
    const toggleWidget = () => {
      isOpen.value = !isOpen.value
    }
    
    const setFontSize = (size) => {
      fontSize.value = size
      const sizes = {
        small: '14px',
        medium: '16px',
        large: '18px',
        'x-large': '20px'
      }
      document.documentElement.style.fontSize = sizes[size]
      localStorage.setItem('fontSize', size)
    }
    
    const setTheme = (theme) => {
      currentTheme.value = theme
      document.documentElement.setAttribute('data-theme', theme)
      localStorage.setItem('theme', theme)
    }
    
    const toggleContrast = () => {
      const html = document.documentElement
      if (highContrast.value) {
        html.classList.add('high-contrast')
      } else {
        html.classList.remove('high-contrast')
      }
      localStorage.setItem('highContrast', highContrast.value)
    }
    
    const toggleMotion = () => {
      const html = document.documentElement
      if (reducedMotion.value) {
        html.classList.add('reduced-motion')
      } else {
        html.classList.remove('reduced-motion')
      }
      localStorage.setItem('reducedMotion', reducedMotion.value)
    }
    
    // Load saved settings
    onMounted(() => {
      const savedFontSize = localStorage.getItem('fontSize')
      if (savedFontSize) {
        setFontSize(savedFontSize)
      }
      
      const savedTheme = localStorage.getItem('theme')
      if (savedTheme) {
        setTheme(savedTheme)
      }
      
      highContrast.value = localStorage.getItem('highContrast') === 'true'
      if (highContrast.value) {
        toggleContrast()
      }
      
      reducedMotion.value = localStorage.getItem('reducedMotion') === 'true'
      if (reducedMotion.value) {
        toggleMotion()
      }
    })
    
    return {
      isOpen,
      fontSize,
      currentTheme,
      highContrast,
      reducedMotion,
      sizes,
      themes,
      toggleWidget,
      setFontSize,
      setTheme,
      toggleContrast,
      toggleMotion
    }
  }
}
</script>

<style scoped>
.accessibility-widget {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 1000;
}

.widget-toggle {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: #0066cc;
  color: white;
  border: none;
  font-size: 24px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  transition: transform 0.2s;
}

.widget-toggle:hover,
.widget-toggle:focus {
  transform: scale(1.1);
  outline: 3px solid #ff9900;
}

.widget-panel {
  position: absolute;
  bottom: 60px;
  right: 0;
  width: 300px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
  border: 1px solid #dee2e6;
}

.widget-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #dee2e6;
}

.widget-header h3 {
  margin: 0;
  color: #333;
}

.close-button {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  padding: 0;
  width: 30px;
  height: 30px;
  border-radius: 4px;
}

.close-button:hover,
.close-button:focus {
  background: #f8f9fa;
  outline: none;
}

.widget-body {
  padding: 15px;
}

.control-group {
  margin-bottom: 20px;
}

.control-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.size-controls {
  display: flex;
  gap: 8px;
}

.size-button {
  flex: 1;
  padding: 8px;
  border: 2px solid #dee2e6;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.size-button.active {
  background: #0066cc;
  color: white;
  border-color: #0066cc;
}

.theme-controls {
  display: flex;
  gap: 10px;
}

.theme-button {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  border: 2px solid #dee2e6;
  cursor: pointer;
  position: relative;
}

.theme-button.active {
  border-color: #0066cc;
}

.theme-button.active::after {
  content: '✓';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #000;
  font-weight: bold;
}

.theme-button[style*="1a1a1a"].active::after {
  color: white;
}

.toggle-control {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.toggle-control input {
  margin-right: 10px;
}

.toggle-control label {
  margin: 0;
  cursor: pointer;
}

@media (max-width: 768px) {
  .widget-panel {
    width: calc(100vw - 40px);
    right: -10px;
  }
}
</style>