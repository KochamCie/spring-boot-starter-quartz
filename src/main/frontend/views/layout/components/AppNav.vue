<template>
  <el-header style="height: 3.25rem;">
    <el-row>
      <el-col :span="6"><router-link class="navbar-item logo" to="/" v-html="brand"/></el-col>
      <el-col :span="6"></el-col>
      <el-col :span="6"></el-col>
      <el-col :span="6">
        <router-link class="navbar-item menu" to="/jobs" v-html="Jobs"/>
        <router-link class="navbar-item menu" to="/records" v-html="Records"/>
      </el-col>
    </el-row>
  </el-header>

</template>

<script>
  export default {
    name: "AppNav",
    data: () => ({
      showMenu: false,
      brand: '<img src="./static/img/icon-spring-boot-admin.svg"><span>Spring Boot Admin</span>',
      Jobs: '<span>Jobs</span>',
      Records: '<span>Records</span>',
      userName: null,
      csrfToken: null,
      csrfParameterName: null
    })

  }
</script>

<style scoped>

  .el-header {
    padding: 0;
    text-align: center;
    width: 1344px;
    margin: auto;
  }


</style>
