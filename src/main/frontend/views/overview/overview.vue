<template>
  <div>
    <el-row>
      <el-col :span="24"></el-col>
    </el-row>
    <el-row>
      <el-col :span="6">
        <div><p class="cato">Total</p>
          <p class="title" v-text="totalCount">0</p></div>
      </el-col>
      <el-col :span="6">
        <div><p class="cato">Normal</p>
          <p class="title" v-text="normalCount">0</p></div>
      </el-col>
      <el-col :span="6">
        <div><p class="cato">Paused</p>
          <p class="title" v-text="pausedCount">0</p></div>
      </el-col>
      <el-col :span="6">
        <div><p class="cato">error</p>
          <p class="title" v-text="errorCount">0</p></div>
      </el-col>
    </el-row>
  </div>

</template>

<script>
  export default {
    name: "overview",
    data(){
      return {
        applications: []
      }
    },
    methods:{
      init: function () {
        console.log('init applications...')
        const self = this;
        this.axios.get(this.Global.prefix+'/quartz/jobs')
          .then(function (res) {
            console.log(res)
            self.applications = res.data;
          })
      }
    },
    computed: {
      totalCount: function () {
        console.log(this.applications)
        console.log(this.applications.length)
        return this.applications.length;
      },
      normalCount: function () {
        return this.applications.reduce((current, next) => current + (next.jobStatus === 'NORMAL'?1:0), 0);
      },
      pausedCount: function () {
        return this.applications.reduce((current, next) => current + (next.jobStatus === 'PAUSED'?1:0), 0);
      },
      errorCount: function () {
        return this.applications.reduce((current, next) => current + (next.jobStatus === 'ERROR'?1:0), 0);
      }

    },
    created(){
      this.init()
    }

  }
</script>

<style scoped>
  .cato {
    display: block;
    font-size: 11px;
    letter-spacing: 1px;
    text-transform: uppercase;
  }

  .title {
    color: #363636;
    font-size: 2rem;
    font-weight: 600;
    line-height: 1.125;
    margin-top: 1px;
  }
</style>
