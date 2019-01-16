<template>
  <div class="app-wrapper">
    <el-container>
      <el-header style="height: 3.25rem;">

        <el-row class="row-wrapper">
          <el-col :span="6">
            <router-link class="navbar-item logo" to="/"><img src="@/static/img/666.gif"><span>{{ $t("quartz.name") }}</span></router-link>
          </el-col>
          <el-col :span="6"></el-col>
          <el-col :span="5"></el-col>
          <el-col :span="7">
            <router-link class="navbar-item menu" to="/overview">{{ $t("nav.overview") }}</router-link>
            <router-link class="navbar-item menu" to="/jobs">{{ $t("nav.jobs") }}</router-link>
            <router-link class="navbar-item menu" to="/job">{{ $t("nav.job") }}</router-link>
            <router-link class="navbar-item menu" to="/records">{{ $t("nav.records") }}</router-link>
            <span class="white" @click.stop="switchLanguage">
              <span :class="{'fs18':fs18 === true,'fs12':fs12 === true}" >中</span>
              <span class="separator">/</span>
              <span :class="{'fs18':fs18 === false,'fs12':fs12 === false}">英</span>
            </span>

          </el-col>
        </el-row>
      </el-header>
      <!--<app-nav></app-nav>-->
      <app-main></app-main>
    </el-container>
  </div>


</template>

<script>
  import AppNav from './components/AppNav'
  import AppMain from './components/AppMain'
  import Cookies from 'js-cookie'

  export default {
    name: "Layout",
    components: {
      AppNav, AppMain
    },
    data: () => ({
      showMenu: false,
      brand: '<img src="./static/img/666.gif"><span>Spring Boot Quartz</span>',
      Jobs: '<span>Jobs</span>',
      Job: '<span>Job</span>',
      Records: '<span>Records</span>',
      Overview: '<span>Overview</span>',
      fs18: undefined === Cookies.get('cn')?false:Cookies.get('cn') === "true",
      fs12: undefined === Cookies.get('cn')?true:Cookies.get('cn') === "false"
    }),
    methods: {
      switchLanguage: function (event) {
        this.fs18=!this.fs18
        this.fs12=!this.fs12
        Cookies.set('cn', this.fs18)
        Cookies.set('language', this.fs18?'cn':'en')
        this.$i18n.locale = this.fs18?'cn':'en'

      }
    }
  }
</script>

<style scoped>
  .app-wrapper {
    position: relative;
    height: 100%;
    width: 100%;
    /*background-color: black;*/
  }

  .el-header {
    padding: 0;
    text-align: center;
    width: 100%;
    background-color: black;

  }

  .el-container {
  }

  .row-wrapper {
    width: 1344px;
    margin: auto;

  }

</style>
