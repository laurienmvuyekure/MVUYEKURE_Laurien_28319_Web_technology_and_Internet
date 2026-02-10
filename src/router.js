import { createRouter, createWebHistory } from 'vue-router'
import Login from './views/Login.vue'
import Register from './views/Register.vue'
import ForgotPassword from './views/ForgotPassword.vue'
import NewTask from './components/tasks/NewTask.vue'
import Dashboard from './views/Dashboard.vue'
import TimeManager from './views/TimeManager.vue'
import AdvancedDashboard from './views/AdvancedDashboard.vue'
import TimeBlocker from './components/time/TimeBlocker.vue'
import Documents from './views/Documents.vue'
import Settings from './views/Settings.vue'
import Reminders from './views/Reminders.vue'
import RecurringTasksView from './views/RecurringTasksView.vue'
import DependenciesView from './views/DependenciesView.vue'
import UserProfile from './views/UserProfile.vue'
import authService from './services/authService'

// Placeholder components (remove when implemented)
// const Profile = { template: '<div class="profile"><h2>Profile</h2><p>User profile page</p></div>' }

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: ForgotPassword
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
    component: UserProfile
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
  },
  {
    path: '/documents',
    name: 'Documents',
    component: Documents
  },
  {
    path: '/settings',
    name: 'Settings',
    component: Settings
  },
  {
    path: '/reminders',
    name: 'Reminders',
    component: Reminders
  },
  {
    path: '/recurring-tasks',
    name: 'RecurringTasks',
    component: RecurringTasksView
  },
  {
    path: '/task-dependencies',
    name: 'TaskDependencies',
    component: DependenciesView
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// Route guards for authentication
router.beforeEach((to, from, next) => {
  const isAuthenticated = authService.isAuthenticated()
  const authPages = ['/login', '/register', '/forgot-password']
  const isAuthPage = authPages.includes(to.path)

  // If user is authenticated and trying to access auth pages, redirect to dashboard
  if (isAuthenticated && isAuthPage) {
    next('/dashboard')
  }
  // If user is not authenticated and trying to access protected pages, redirect to login
  else if (!isAuthenticated && !isAuthPage && to.path !== '/' && to.path !== '/demo') {
    next('/login')
  }
  // Allow the navigation
  else {
    next()
  }
})

export default router
