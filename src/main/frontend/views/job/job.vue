<template>
  <div>
    <div class="filter-container">
      <el-input @keyup.enter.native="handleFilter" style="width:200px;" class="filter-item" placeholder="job name"
                v-model="listQuery.title"></el-input>
      <el-button class="filter-item" type="primary" size="medium" icon="el-icon-search" @click="handleFilter">search
      </el-button>
      <el-button class="filter-item" @click="handleAdd" type="primary" size="medium" icon="el-icon-info">handleAdd
      </el-button>
    </div>
    <el-table
      :data="tableData"
      border
      highlight-current-row
      :cell-style="cellStyle"
      style="width: 100%;overflow: hidden">

      <el-table-column
        prop="jobName"
        label="任务名称"
        sortable
      >
        <template slot-scope="scope">
          <a :href="'/view/jobrec'"
             target="_blank"
             class="">{{scope.row.jobName}}</a>
        </template>
      </el-table-column>


      <el-table-column
        prop="jobClassName"
        label="执行器"
        align="center"
        sortable>
      </el-table-column>

      <el-table-column
        prop="triggerName"
        label="触发器名"
        align="center"
        sortable>
      </el-table-column>
      <el-table-column
        prop="jobStatus"
        label="任务状态"
        align="center"
        sortable>
      </el-table-column>

      <el-table-column
        prop="cronExpression"
        label="Cron表达式"
        align="center"
        sortable>
      </el-table-column>

      <el-table-column
        prop="description"
        label="描述"
        sortable
        align="center"
        show-overflow-tooltip>
      </el-table-column>

      <el-table-column
        prop="jobData"
        :formatter="handlerJobData"
        label="自定义数据"
        sortable
        align="center"
        show-overflow-tooltip>
      </el-table-column>

      <el-table-column label="actions" width="333px" align="center">
        <template slot-scope="scope">
          <el-button
            size="small"
            :type="scope.row.jobStatus=='NORMAL'?'info':'warning'"
            v-text="scope.row.jobStatus == 'NORMAL'?'暂停':'恢复'"
            @click="triggerOption(scope.$index, scope.row, scope.row.jobStatus == 'NORMAL'?'PAUSE':'RESUME')">
          </el-button>

          <el-button
            size="small"
            type="primary"
            @click="triggerOption(scope.$index, scope.row, 'TRIGGER')">触发
          </el-button>

          <el-button
            size="small"
            type="success"
            @click="handleUpdate(scope.$index, scope.row)">编辑
          </el-button>


          <el-button
            size="small"
            type="danger"
            @click="triggerOption(scope.$index, scope.row, 'DELETE')">删除
          </el-button>


        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 30, 40]"
        :page-size="pagesize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalCount">
      </el-pagination>
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
        <el-button type="primary" @click="add(1, form, 'SCHEDULE')">Confirm</el-button>
      </div>
    </el-dialog>

    <el-dialog title="Edit Quartz Job" :visible.syn="updateFormVisible" @close='updateFormVisible = false'>
      <el-form :model="updateform">
        <el-form-item label="Cron" label-width="120px" style="width:66%">
          <el-input v-model="updateform.cronExpression" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="Description" label-width="120px" style="width:66%">
          <el-input v-model="updateform.description" auto-complete="off"></el-input>
        </el-form-item>

        <el-form-item label="Customize" label-width="120px" style="width:66%">
          <el-input v-model="updateform.jobData" auto-complete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="updateFormVisible = false">Cancel</el-button>
        <el-button type="primary" @click="update">Confirm</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  export default {
    name: "Job",
    data() {
      return {
        // current table data
        tableData: [],

        // page size
        pagesize: 10,

        currentPage: 1,

        // start page
        start: 1,

        // total count
        totalCount: 1000,

        // dialog
        dialogFormVisible: false,

        // edit dialog
        updateFormVisible: false,

        //
        form: {
          jobName: '',
          jobGroup: '',
          cronExpression: '0 0/1 * * * ?',
          description: '',
          jobClassName: 'com.beelego.demo.DemoJob2'
        },
        updateform: {
          index: '',
          jobName: '',
          jobGroup: '',
          cronExpression: '',
          description: '',
          jobData: '',
          jobClassName: ''
        },
        listQuery: {
          title: ''
        }
      }
    },
    methods: {

      // fetch data
      loadData: function (pageNum, pageSize) {
        const self = this;
        this.axios.get(this.Global.prefix+'/quartz/job?' + 'pageNum=' + pageNum + '&pageSize=' + pageSize).then(function (res) {
          self.tableData = res.data.content;
          self.totalCount = res.data.totalElements;
        }, function () {
          console.log('failed');
        });
      },
      triggerOption: function (index, row, option) {
        const self = this
        this.axios.put(this.Global.prefix+'/quartz/trigger', this.qs.stringify({
          "jobName": row.jobName,
          "jobGroup": row.jobGroup,
          "option": option
        }), {emulateJSON: true}).then(function (res) {
          console.log(res)
          if (res.data != 200) {
            self.$message({
              message: res.data.error[0].message,
              type: 'error'
            });
            return;
          }
          self.loadData(self.currentPage, self.pagesize);
        }, function () {
          console.log('failed');
        })
      },
      // add a job
      add: function (index, row, option) {
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
          self.loadData(self.currentPage, self.pagesize);
          self.dialogFormVisible = false;
        }, function () {
          console.log('failed');
        });
      },
      // update a job
      handleUpdate: function (index, row) {
        console.log(row)
        this.updateFormVisible = true;
        this.updateform.jobName = row.jobName;
        this.updateform.jobGroup = row.jobGroup;
        this.updateform.cronExpression = row.cronExpression;
        this.updateform.description = row.description;
        this.updateform.index = index;
        this.updateform.jobClassName = row.jobClassName;
        this.updateform.jobData = JSON.stringify(row.jobData);
      },
      // update a job
      update: function () {
        this.add(this.updateform.index, this.updateform, "RESCHEDULE");
        this.updateFormVisible = false;
      },

      // searching
      search: function () {
        this.loadData(this.currentPage, this.pagesize);
      },

      //
      handleAdd: function () {
        this.dialogFormVisible = true;
      },

      //
      handleSizeChange: function (val) {
        this.pagesize = val;
        this.loadData(this.currentPage, this.pagesize);
      },

      //
      handleCurrentChange: function (val) {
        this.currentPage = val;
        this.loadData(this.currentPage, this.pagesize);
      },
      handlerJobData: function (row, column) {
        return JSON.stringify(row.jobData);
      },
      cellStyle: function ({row, column, rowIndex, columnIndex}) {
        console.log("cell style");
        if (columnIndex === 1 || columnIndex === 6) {
          return 'background:pink;font-size:12px'
        } else {
          return ''
        }
      },
      handleFilter: function () {
        this.loadData(1, 20)
      }

    },
    created() {
      this.loadData(1, 20)
    }
  }
</script>

<style scoped>

</style>
