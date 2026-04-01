import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Profile from '../views/Profile.vue'
import MoldeDetails from '../views/MoldeDetails.vue'
import { useAuthStore } from '../stores/auth'


const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path:'/login',
      name:'Login',
      component: Login
    },
    {
      path: '/',
      name: 'Home',
      component: Home,
      meta: { requiresAuth: true }
    },
    {
      path:'/profile',
      name:'Profile',
      component: Profile,
        meta: { requiresAuth: true }
    },
    {
      path: '/molde/:wkz',
      name: 'MoldeDetails',
      component: MoldeDetails,
      meta: { requiresAuth: true }
    }
  ],
})

router.beforeEach(async (to) => {
  const auth = useAuthStore()

  if (!auth.initialized) {
    await auth.fetchMe()
  }

  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return '/login'
  }

  if (to.path === '/login' && auth.isAuthenticated) {
    return '/'
  }
})


export default router