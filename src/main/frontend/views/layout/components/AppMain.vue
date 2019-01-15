<template>
  <el-main>
    <router-view/>
  </el-main>
</template>

<script>
    export default {
        name: "AppMain"
    }
</script>

<style scoped>
  .el-main {
    color: #333;
    text-align: center;
    height: 90%;
    width: 1344px;
    margin: auto
  }
</style>
