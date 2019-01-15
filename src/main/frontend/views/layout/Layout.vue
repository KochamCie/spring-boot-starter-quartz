<template>
  <div class="app-wrapper">
    <el-container>
      <el-header style="height: 3.25rem;">

        <el-row class="row-wrapper">
          <el-col :span="6">
            <router-link class="navbar-item logo" to="/" v-html="brand"/>
          </el-col>
          <el-col :span="6"></el-col>
          <el-col :span="6"></el-col>
          <el-col :span="6">
            <router-link class="navbar-item menu" to="/overview" v-html="Overview"/>
            <router-link class="navbar-item menu" to="/jobs" v-html="Jobs"/>
            <router-link class="navbar-item menu" to="/job" v-html="Job"/>
            <router-link class="navbar-item menu" to="/records" v-html="Records"/>
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
      userName: null,
      csrfToken: null,
      csrfParameterName: null
    })
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
