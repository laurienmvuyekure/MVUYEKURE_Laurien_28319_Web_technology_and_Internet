import axios from 'axios'

// Use environment variable for production, local for development
const baseURL = import.meta.env.VITE_API_URL || '/api'

const api = axios.create({
  baseURL: baseURL,
})

// Attach JWT Bearer token to every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config
})

export const setToken = (token) => localStorage.setItem('token', token)
export const clearToken = () => localStorage.removeItem('token')

// --- Auth ---
export const registerUser = (data) => api.post('/users/register', data)
export const loginUser = (data) => api.post('/users/login', data)

// --- Tasks ---
export const getTasks = () => api.get('/tasks')
export const createTask = (task) => api.post('/tasks', task)
export const updateTask = (id, task) => api.put(`/tasks/${id}`, task)
export const deleteTask = (id) => api.delete(`/tasks/${id}`)
