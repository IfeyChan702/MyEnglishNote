<template>
  <div class="app-container">
    <el-form
        :model="queryParams"
        ref="queryForm"
        :inline="true"
        v-show="showSearch"
        label-width="90px"
        class="search-form"
    >
      <el-form-item label="设备号码" prop="deviceNumber">
        <el-input
            v-model="queryParams.deviceNumber"
            placeholder="请输入设备号码"
            clearable
            @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备金额" prop="currentAmount">
        <el-input
            v-model="queryParams.currentAmount"
            placeholder="请输入当前设备金额"
            clearable
            @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="充值上线" prop="rechargeLimit">
        <el-input
            v-model="queryParams.rechargeLimit"
            placeholder="请输入充值金额上限"
            clearable
            @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="充值开关" prop="rechargeEnabled" style="width: 170px">
        <el-select v-model="queryParams.rechargeEnabled" placeholder="全部" clearable style="width: 100%">
          <el-option label="开启" :value="1"/>
          <el-option label="关闭" :value="0"/>
        </el-select>
      </el-form-item>

      <el-form-item label="购物开关" prop="shoppingEnabled" style="width: 170px">
        <el-select v-model="queryParams.shoppingEnabled" placeholder="全部" clearable style="width: 100%">
          <el-option label="开启" :value="1"/>
          <el-option label="关闭" :value="0"/>
        </el-select>
      </el-form-item>

      <el-form-item label="设备开关" prop="deviceEnabled" style="width: 170px">
        <el-select v-model="queryParams.deviceEnabled" placeholder="全部" clearable style="width: 100%">
          <el-option label="启用" :value="1"/>
          <el-option label="停用" :value="0"/>
        </el-select>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain :icon="Plus"
            size="default"
            @click="handleAdd"
            v-hasPermi="['device:info:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="success"
            plain :icon="Edit"
            size="default"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['device:info:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain :icon="Delete"
            size="default"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['device:info:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="warning"
            plain :icon="Download"
            size="default"
            @click="handleExport"
            v-hasPermi="['device:info:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="infoList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="主键ID" align="center" prop="id"/>
      <el-table-column label="设备号码" align="center" prop="deviceNumber"/>
      <el-table-column label="设备金额" align="center" prop="currentAmount"/>
      <el-table-column label="充值上线" align="center" prop="rechargeLimit"/>
      <el-table-column label="充值开关" align="center">
        <template #default="{ row }">
          <el-switch
              v-model="row.rechargeEnabled"
              :active-value="1"
              :inactive-value="0"
              active-text="开"
              inactive-text="关"
              @change="val => handleSwitchChange(row, 'rechargeEnabled', val)"
          />
        </template>
      </el-table-column>
      <el-table-column label="购物开关" align="center">
        <template #default="{ row }">
          <el-switch
              v-model="row.shoppingEnabled"
              :active-value="1"
              :inactive-value="0"
              active-text="开"
              inactive-text="关"
              @change="val => handleSwitchChange(row, 'shoppingEnabled', val)"
          />
        </template>
      </el-table-column>

      <el-table-column label="设备开关" align="center">
        <template #default="{ row }">
          <el-switch
              v-model="row.deviceEnabled"
              :active-value="1"
              :inactive-value="0"
              active-text="开"
              inactive-text="关"
              @change="val => handleSwitchChange(row, 'deviceEnabled', val)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="{ row }">
          <el-button
              size="mini"
              type="text"
              :icon="Edit"
              @click="handleUpdate(row)"
              v-hasPermi="['device:info:edit']"
          >修改
          </el-button>
          <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(row)"
              v-hasPermi="['device:info:remove']"
          >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="page-footer">
      <pagination
          v-show="total>0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList"
      />
    </div>

    <!-- 添加或修改设备信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="设备号码" prop="deviceNumber">
          <el-input v-model="form.deviceNumber" placeholder="请输入设备号码"/>
        </el-form-item>
        <el-form-item label="设备金额" prop="currentAmount">
          <el-input v-model="form.currentAmount" placeholder="请输入当前设备金额"/>
        </el-form-item>
        <el-form-item label="充值上线" prop="rechargeLimit">
          <el-input v-model="form.rechargeLimit" placeholder="请输入充值金额上限"/>
        </el-form-item>
        <el-form-item label="充值开关" prop="rechargeEnabled">
          <el-switch v-model="form.rechargeEnabled" :active-value="1" :inactive-value="0"/>
        </el-form-item>
        <el-form-item label="购物开关" prop="shoppingEnabled">
          <el-switch v-model="form.shoppingEnabled" :active-value="1" :inactive-value="0"/>
        </el-form-item>
        <el-form-item label="设备开关" prop="deviceEnabled">
          <el-switch v-model="form.deviceEnabled" :active-value="1" :inactive-value="0"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {listInfo, getInfo, delInfo, addInfo, updateInfo} from "@/api/GiftCard/info"
import {Search, Refresh, Plus, Edit, Delete, Download} from '@element-plus/icons-vue'

export default {
  name: "Info",
  data() {
    return {
      Search, Refresh, Plus, Edit, Delete, Download,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 设备信息表格数据
      infoList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deviceNumber: null,
        currentAmount: null,
        rechargeLimit: null,
        rechargeEnabled: null,
        shoppingEnabled: null,
        deviceEnabled: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        deviceNumber: [
          {required: true, message: "设备号码不能为空", trigger: "blur"}
        ],
        currentAmount: [
          {required: true, message: "当前设备金额不能为空", trigger: "blur"}
        ],
        rechargeLimit: [
          {required: true, message: "充值金额上限不能为空", trigger: "blur"}
        ],
        rechargeEnabled: [
          {required: true, message: "充值开关不能为空", trigger: "blur"}
        ],
        shoppingEnabled: [
          {required: true, message: "购物开关不能为空", trigger: "blur"}
        ],
        deviceEnabled: [
          {required: true, message: "设备开关不能为空", trigger: "blur"}
        ],
        createTime: [
          {required: true, message: "创建时间不能为空", trigger: "blur"}
        ],
        updateTime: [
          {required: true, message: "更新时间不能为空", trigger: "blur"}
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /**
     * 谢欢开关
     * @param row
     * @param field
     * @param val
     */
    handleSwitchChange(row, field, val) {
      const oldVal = val === 1 ? 0 : 1

      const payload = {...row, [field]: val}

      updateInfo(payload).then(() => {
        this.$modal.msgSuccess("已更新")
      }).catch(() => {
        // 回滚 UI
        row[field] = oldVal
        this.$modal.msgError("更新失败，已回滚")
      })
    },
    /** 查询设备信息列表 */
    getList() {
      this.loading = true
      listInfo(this.queryParams).then(response => {
        this.infoList = (response.rows || []).map(r => ({
          ...r,
          rechargeEnabled: r.rechargeEnabled == null ? 0 : Number(r.rechargeEnabled),
          shoppingEnabled: r.shoppingEnabled == null ? 0 : Number(r.shoppingEnabled),
          deviceEnabled: r.deviceEnabled == null ? 0 : Number(r.deviceEnabled),
        }))
        this.total = response.total || 0
      }).catch(err => {
        this.$modal.msgError("查询失败：" + (err.msg || err.message || "未知错误"))
        this.infoList = []
        this.total = 0
      }).finally(() => {
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        deviceNumber: null,
        currentAmount: null,
        rechargeLimit: null,
        rechargeEnabled: null,
        shoppingEnabled: null,
        deviceEnabled: null,
        createTime: null,
        updateTime: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加设备信息"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getInfo(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改设备信息"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateInfo(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addInfo(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除设备信息编号为"' + ids + '"的数据项？').then(function () {
        return delInfo(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {
      })
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/info/export', {
        ...this.queryParams
      }, `info_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
<style scoped>
/* 搜索区整体间距 */
.search-form :deep(.el-form-item) {
  margin-right: 16px;
  margin-bottom: 12px;
}

/* 统一搜索区输入框/下拉框高度 */
.search-form :deep(.el-input__wrapper),
.search-form :deep(.el-select__wrapper) {
  height: 38px;
  padding: 0 10px;
  font-size: 14px;
}

/* input 内部 */
.search-form :deep(.el-input__inner) {
  height: 38px;
  line-height: 38px;
  font-size: 14px;
}

/* select 选中区域垂直居中 */
.search-form :deep(.el-select__selection) {
  height: 38px;
  display: flex;
  align-items: center;
}


/* 整个页面容器撑满高度 */
.app-container {
  min-height: calc(100vh - 84px); /* 84px 可按你顶部布局微调 */
  display: flex;
  flex-direction: column;
}

/* 表格区域占满剩余空间，把分页顶到最下面 */
.app-container :deep(.el-table) {
  flex: 1;
}

/* 分页固定在底部（不悬浮，只是贴底） */
.page-footer {
  margin-top: auto;
  padding-top: 12px;
}
</style>
