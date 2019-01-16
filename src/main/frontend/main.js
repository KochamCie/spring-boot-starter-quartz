// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import ElementUI from 'element-ui';
import {Message} from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import App from './App'
import router from './router/index'
import './assets/layout.css'
import axios from 'axios'
import qs from "qs"
import global from '@/components/Global'
import './assets/iconfont/iconfont.css'
import i18n from './lang' // Internationalization
import Cookies from 'js-cookie'


Vue.config.productionTip = false
Vue.use(ElementUI);

Vue.prototype.axios = axios;
Vue.prototype.qs = qs;
Vue.prototype.$message = Message;
Vue.prototype.Global = global;

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  i18n,
  Cookies,
  components: {App},
  template: '<App/>'
})
