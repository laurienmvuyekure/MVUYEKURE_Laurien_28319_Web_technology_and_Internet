<template>
  <div>
    <h2 class="text-purple-700 font-bold text-xl mb-2">TaskBuddy</h2>

    <!-- Input Section -->
    <input
      type="text"
      placeholder="New Task"
      v-model="newTask"
      class="border p-1 mr-2"
    />
    <button
  @click="addTask"
  style="background-color: green; color: white; padding: 5px 10px;"
>
  Add Task
</button>

    <hr class="my-3" />

    <!-- Task List -->
    <div
      v-for="(task, index) in tasks"
      :key="index"
      style="display: flex; justify-content: space-between; margin-bottom: 10px;"
    >
      <!-- Task Name -->
      <span :style="{ textDecoration: task.completed ? 'line-through' : 'none' }">
        {{ task.name }}
      </span>

      <!-- Operations -->
      <div>
        <span
          @click="completeTask(index)"
          style="color: green; margin-right: 15px; cursor: pointer;"
        >
          Complete
        </span>

        <span
          @click="deleteTask(index)"
          style="color: red; cursor: pointer;"
        >
          Delete
        </span>
      </div>
    </div>

    <br />

    <!-- Counter -->
    <p style="margin-left: -180px;">Count: {{ count }} </p>
    <button
      @click="count+s+"
  style="background-color: green; margin-left: -180px; color: white; padding: 5px 10px;"
    >
      Increase +
    </button>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const newTask = ref('')
const count = ref(0)

const tasks = ref([
  
])

const addTask = () => {
  if (newTask.value.trim() !== '') {
    tasks.value.push({
      name: newTask.value,
      completed: false
    })
    newTask.value = ''
  }
}

const completeTask = (index) => {
  tasks.value[index].completed = !tasks.value[index].completed
}

const deleteTask = (index) => {
  tasks.value.splice(index, 1)
}
</script>
