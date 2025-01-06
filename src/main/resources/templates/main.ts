import '../../javascript/assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from '../../javascript/App.vue'
import router from '../../javascript/router'

const pinia = createPinia()
const app = createApp(App)

app.use(router)
app.use(pinia)

app.mount('#app')
