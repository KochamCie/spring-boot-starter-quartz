<template>
  <div v-on-clickaway="deselect">
    <span class="iconfont icon-tianjia" @click.stop="dialogFormVisible = true"></span>
    <div class="job" v-for="job in jobs">
      <p class="heading" v-text="job.jobStatus"/>
      <div :id="job.jobName" class="card" :class="{'is-active': selected === job.jobName}"
           @click.stop="select(job.jobName, job)">
        <el-row>
          <el-col :span="2">
            <div>
              <span class="iconfont icon-zongshu icon-job-count icon-job-count-position"></span>
              <p class="margin0 icon-job-count-position">99次</p>
            </div>
          </el-col>
          <el-col :span="20" align="left">
            <div>
              <p v-text="job.jobName + '&' + job.jobGroup" class="margin10"></p>
              <p v-text="job.jobClassName" class="margin10"></p>
            </div>
          </el-col>
          <el-col :span="2">
            <div class="application-list__item__header__actions">
              <span class="iconfont icon-dianjichufa- icon-job-action"
                    @click.stop="jobTrigger(job.jobName, job.jobGroup)"></span>

              <span class="iconfont  icon-job-action"
                    :class="icons(job.jobStatus)"
                    @click.stop="jobPauseOrResume(job.jobName, job.jobGroup, job.jobStatus)"></span>
              <span class="iconfont icon-trash icon-job-action"
                    @click.stop="jobDelete(job.jobName, job.jobGroup)"></span>

            </div>
          </el-col>
        </el-row>
      </div>
      <div class="card-content" v-if="selected === job.jobName">
        <el-form ref="updateform" label-width="80px">
          <el-row>
            <el-col :span="6">
              <el-form-item label="cron:"  >
                <el-input :class="{'job-input': edits['jobCron'] === true}"
                          v-model="updateform.cronExpression"
                          @focus="show('jobCron')"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="15">
              <el-form-item label="remark:">
                <el-input :class="{'job-input': edits['jobDescription'] === true}"
                          v-model="updateform.description"
                          @focus="show('jobDescription')"></el-input>
              </el-form-item>

            </el-col>
            <el-col :span="3">
              <span class="el-icon-edit-outline icon-job-edit" @click.stop="jobUpdate()"></span>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="21">

              <el-form-item label="jobData:">

                <el-input :class="{'job-input': edits['jobData'] === true}"
                          v-model="updateform.jobData"
                          @focus="show('jobData')"></el-input>

              </el-form-item>
            </el-col>

          </el-row>

        </el-form>

      </div>
    </div>



    <el-dialog title="Add Quartz Job" :visible.syn="dialogFormVisible" @close='dialogFormVisible = false'>
      <el-form :model="form">
        <el-form-item label="Job Name" label-width="120px" style="width:66%">
          <el-input v-model="form.jobName" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="Job Group" label-width="120px" style="width:66%">
          <el-input v-model="form.jobGroup" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="Target" label-width="120px" style="width:66%">
          <el-input v-model="form.jobClassName" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="Cron" label-width="120px" style="width:66%">
          <el-input v-model="form.cronExpression" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="Description" label-width="120px" style="width:66%">
          <el-input v-model="form.description" auto-complete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">Cancel</el-button>
        <el-button type="primary" @click="jobAdd(form, 'SCHEDULE')">Confirm</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import {mixin as clickaway} from 'vue-clickaway';

  export default {
    name: "Job",
    mixins: [clickaway],
    data() {
      return {
        jobs: [],
        selected: '',
        iconscss: {
          'NORMAL': 'icon-zanting',
          'PAUSED': 'icon-kaishishijian',
          'ERROR': 'icon-gantanhao'
        },
        options: {
          'NORMAL': 'PAUSE',
          'PAUSED': 'RESUME',
          'ERROR': 'RESUME'
        },
        edits: {
          'jobCron': true,
          'jobDescription': true,
          'jobData': true
        },
        form: {
          jobName: '',
          jobGroup: '',
          cronExpression: '0 0/1 * * * ?',
          description: '',
          jobClassName: 'com.beelego.demo.DemoJob'
        },
        updateform: {
          jobName: '',
          jobGroup: '',
          cronExpression: '',
          description: '',
          jobData: '',
          jobClassName: ''
        },
        job: {},
        dialogFormVisible: false
      }
    },
    methods: {
      // fetch data
      loadData: function () {
        const self = this;
        this.axios.get(this.Global.prefix + '/quartz/jobs').then(function (res) {
          self.jobs = res.data
        }, function (error) {
          self.handlerError(error)
        });
      },

      show: function(obj){
        this.edits[obj] = false
      },

      select: function (name, row) {
        this.selected = name
        this.updateform.jobName = row.jobName;
        this.updateform.jobGroup = row.jobGroup;
        this.updateform.cronExpression = row.cronExpression;
        this.updateform.description = row.description;
        this.updateform.jobClassName = row.jobClassName;
        this.updateform.jobData = JSON.stringify(row.jobData);
        // this.updateform.jobData = row.jobData;
        console.log(this.updateform)
      },
      icons: function (name) {
        return this.iconscss[name]
      },

      deselect: function (event) {
        this.selected = '';
        this.edits = {
          'jobCron': true,
          'jobDescription': true,
          'jobData': true
        }
      },

      jobDelete: function (jobName, jobGroup) {
        console.log('delete', jobName, jobGroup);
        this.triggerOption(jobName, jobGroup, 'DELETE')
      },
      jobPauseOrResume: function (jobName, jobGroup, jobStatus) {
        console.log('pause', jobName, jobGroup)
        this.triggerOption(jobName, jobGroup, this.options[jobStatus])
      },
      jobTrigger: function(jobName, jobGroup){
        console.log('trigger', jobName, jobGroup);
        this.triggerOption(jobName, jobGroup, 'TRIGGER')
      },
      triggerOption: function (jobName, jobGroup, option) {
        const self = this
        this.axios.put(this.Global.prefix + '/quartz/trigger', this.qs.stringify({
          "jobName": jobName,
          "jobGroup": jobGroup,
          "option": option
        }), {emulateJSON: true}).then(function (res) {
          self.handlerRes(res)
          self.loadData(self.currentPage, self.pagesize);
        }, function (error) {
          self.handlerError(error)
        })
      },
      // add a job
      jobAdd: function (row, option) {
        const self = this
        this.axios.post(this.Global.prefix+'/quartz/job', this.qs.stringify({
          "jobName": row.jobName,
          "jobGroup": row.jobGroup,
          "jobClassName": row.jobClassName,
          "cronExpression": row.cronExpression,
          "description": row.description,
          "jobData": row.jobData,
          "option": option
        }), {emulateJSON: true}).then(function (res) {
          self.handlerRes(res)
          self.loadData(self.currentPage, self.pagesize);
          self.dialogFormVisible = false;
        }, function (error) {
          self.handlerError(error)
        });
      },
      // update a job
      jobUpdate: function () {
        console.log(this.updateform)
        this.jobAdd(this.updateform, "RESCHEDULE");
      },
      handlerError: function(error) {
        const self = this
        if (error.response) {
          if(500 === error.response.status){
            self.$message({
              message: error.response.data.message,
              type: 'error'
            });
          }
          // 请求已发出，但服务器响应的状态码不在 2xx 范围内
          console.log(error.response.data);
          console.log(error.response.status);
          console.log(error.response.headers);
        } else {
          // Something happened in setting up the request that triggered an Error
          console.log('Error', error.message);
        }
        console.log(error.config);
        console.log('failed');
      },
      handlerRes: function (res) {
        const self = this
        console.log(res)
        if (res.data != 200) {
          self.$message({
            message: res.data.error[0].message,
            type: 'error'
          });
          return;
        } else {
          self.$message({
            message: '操作成功',
            type: 'success'
          });
        }
      }
    },

    created() {
      this.loadData()
    }
  }
</script>

<style>
  .job {
  }

  .heading {
    display: block;
    font-size: 11px;
    letter-spacing: 1px;
    margin-bottom: 5px;
    text-transform: uppercase;
    text-align: left;
  }

  .card {
    background-color: #fff;
    box-shadow: 0 2px 3px hsla(0, 0%, 4%, .1), 0 0 0 1px hsla(0, 0%, 4%, .1);
    color: #4a4a4a;
    max-width: 100%;
    position: relative;
    height: 75px;
    cursor: pointer;

  }

  .card-content {
    box-shadow: 0 2px 3px hsla(0, 0%, 4%, .1), 0 0 0 1px hsla(0, 0%, 4%, .1);
    margin: -.75rem -.75rem .75rem -.75rem;
    padding: .75rem;
  }

  .icon-job-mini {
    height: 32px;
  }

  .icon-job-count-position {
    position: relative;
    top: 8px;
    right: 15%;
  }

  .icon-job-action {
    position: relative;
    top: 20px;
  }

  .margin0 {
    margin: 0px;
  }

  .margin10 {
    margin: 11px;
  }

  .is-active {
    margin: 0rem -.75rem .75rem -.75rem;
    max-width: unset;
    background-color: #42d3a5;
    color: #ffffff
  }



  .icon-job-edit {
    font-size: 32px;
    position: relative;
    left: 10px;
    cursor: pointer;
  }

  .job-input input {
    border: 0px solid;
  }


</style>
