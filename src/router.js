import { createRouter, createWebHistory } from 'vue-router'
import Login from './views/Login.vue'
import NewTask from './components/tasks/NewTask.vue'
import Dashboard from './views/Dashboard.vue'
import TimeManager from './views/TimeManager.vue'
import AdvancedDashboard from './views/AdvancedDashboard.vue'
import TimeBlocker from './components/time/TimeBlocker.vue'

// Placeholder components for pages not yet created
const Profile = { template: '<div class="profile"><h2>Profile</h2><p>User profile page</p></div>' }

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Login
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: Login
  },
  {
    path: '/',
    name: 'Home',
    component: Dashboard
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard
  },
  {
    path: '/demo',
    name: 'Demo',
    component: Dashboard
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile
  },
  {
    path: '/new-task',
    name: 'NewTask',
    component: NewTask
  },
  {
    path: '/time-manager',
    name: 'TimeManager',
    component: TimeManager
  },
  {
    path: '/time-blocker',
    name: 'TimeBlocker',
    component: TimeBlocker
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
