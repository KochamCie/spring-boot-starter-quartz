import Vue from 'vue'
import Router from 'vue-router'
import HelloWorld from '@/components/HelloWorld'
import Layout from '@/views/layout/Layout'

Vue.use(Router)

export default new Router({
  // mode: 'history',
  base: '/test',
  routes: [
    {
      path: '/',
      name: 'HelloWorld',
      component: Layout,
      redirect: '/overview',
      children: [
        {
          path: 'overview',
          component: () => import('@/views/overview/overview'),
          name: 'overview'
        }
      ]
    },
    {
      path: '/jobs',
      name: 'Jobs',
      component: Layout,
      redirect: '/jobs/list',
      children: [
        {
          path: 'list',
          component: () => import('@/views/job/jobs'),
          name: 'jobsList'
        }
      ]
    }, {
      path: '/job',
      name: 'Job',
      component: Layout,
      redirect: '/job/list',
      children: [
        {
          path: 'list',
          component: () => import('@/views/job/job'),
          name: 'jobList'
        }
      ]
    },
    {
      path: '/records',
      name: 'Records',
      component: Layout,
      redirect: '/records/list',
      children: [
        {
          path: 'list',
          component: () => import('@/views/record/record'),
          name: 'recordList'
        }
      ]
    }
  ]
})
