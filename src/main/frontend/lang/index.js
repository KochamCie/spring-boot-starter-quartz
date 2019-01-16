import Vue from 'vue'
import VueI18n from 'vue-i18n'
import elementEnLocale from 'element-ui/lib/locale/lang/en' // element-ui lang
import elementZhLocale from 'element-ui/lib/locale/lang/zh-CN'// element-ui lang
import enLocale from './en'
import zhLocale from './zh'
import Cookies from 'js-cookie'

Vue.use(VueI18n)

// const messages = {
//   en: {
//     enLocale,
//     elementEnLocale
//   },
//   zh: {
//     zhLocale,
//     elementZhLocale
//   }
// }

const messages = {
  'cn': zhLocale,   // 中文语言包
  'en': enLocale    // 英文语言包
}
console.log(Cookies.get('language'))
const i18n = new VueI18n({
  locale: Cookies.get('language') || 'en', // set locale
  messages // set locale messages
})

export default i18n
