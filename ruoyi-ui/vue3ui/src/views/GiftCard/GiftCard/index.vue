<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="90px" class="search-form">
      <el-form-item label="发件人" prop="sender">
        <el-input
          v-model="queryParams.sender"
          placeholder="请输入发件人"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="主题" prop="subject">
        <el-input
          v-model="queryParams.subject"
          placeholder="请输入主题"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="giftType" class="search-auto-item">
        <el-select
            v-model="queryParams.giftType"
            placeholder="请选择类型"
            clearable
            class="search-auto-select"
        >
          <el-option
              v-for="dict in gift_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
<!--      <el-form-item label="时间" prop="dtStr">
        <el-input
          v-model="queryParams.dtStr"
          placeholder="请输入时间"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>-->
      <el-form-item label="创建时间" style="width: 308px">
        <el-date-picker
            v-model="dateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            format="YYYY-MM-DD"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
        />
      </el-form-item>
      <el-form-item label="礼品卡代码" prop="code">
        <el-input
          v-model="queryParams.code"
          placeholder="请输入礼品卡代码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单号" prop="orderNumber">
        <el-input
          v-model="queryParams.orderNumber"
          placeholder="请输入订单号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="金额" prop="amount">
        <el-input
          v-model="queryParams.amount"
          placeholder="请输入金额"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="编号" prop="extraNumber">
        <el-input
          v-model="queryParams.extraNumber"
          placeholder="请输入编号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="使用类型" prop="usageType" class="search-auto-item">
        <el-select
            v-model="queryParams.usageType"
            placeholder="请选择使用类型"
            clearable
            class="search-auto-select"
        >
          <el-option
              v-for="dict in ka_usage_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status" class="search-auto-item">
        <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
            class="search-auto-select"
        >
          <el-option
              v-for="dict in ka_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['GiftCard:GiftCard:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['GiftCard:GiftCard:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['GiftCard:GiftCard:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['GiftCard:GiftCard:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="GiftCardList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="发件人" align="center" prop="sender" />
      <el-table-column label="主题" align="center" prop="subject" />
      <el-table-column label="类型" align="center" prop="giftType">
        <template #default="scope">
          <dict-tag :options="gift_type" :value="scope.row.giftType"/>
        </template>
      </el-table-column>
      <el-table-column label="时间" align="center" prop="dtStr" />
      <el-table-column label="礼品卡代码" align="center" prop="code" />
      <el-table-column label="订单号" align="center" prop="orderNumber" />
      <el-table-column label="金额" align="center" prop="amount" />
      <el-table-column label="使用类型" align="center" prop="usageType">
        <template #default="scope">
          <dict-tag :options="ka_usage_type" :value="scope.row.usageType"/>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="ka_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['GiftCard:GiftCard:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['GiftCard:GiftCard:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 左边显示总金额 -->
    <span>{{ amountSum }}</span>
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改礼品卡对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="GiftCardRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="发件人" prop="sender">
          <el-input v-model="form.sender" placeholder="请输入发件人" />
        </el-form-item>
        <el-form-item label="主题" prop="subject">
          <el-input v-model="form.subject" placeholder="请输入主题" />
        </el-form-item>
        <el-form-item label="类型" prop="giftType">
          <el-select v-model="form.giftType" placeholder="请选择类型">
            <el-option
              v-for="dict in gift_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="时间" prop="dtStr">
          <el-input v-model="form.dtStr" placeholder="请输入时间" />
        </el-form-item>
        <el-form-item label="礼品卡代码" prop="code">
          <el-input v-model="form.code" placeholder="请输入礼品卡代码" />
        </el-form-item>
        <el-form-item label="订单号" prop="orderNumber">
          <el-input v-model="form.orderNumber" placeholder="请输入订单号" />
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input v-model="form.amount" placeholder="请输入金额" />
        </el-form-item>
        <el-form-item label="编号" prop="extraNumber">
          <el-input v-model="form.extraNumber" placeholder="请输入编号" />
        </el-form-item>
        <el-form-item label="使用类型" prop="usageType" class="search-narrow-item">
          <el-select
              v-model="form.usageType"
              placeholder="请选择使用类型"
              clearable
              class="search-narrow-select"
          >
            <el-option
                v-for="dict in ka_usage_type"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status" class="search-narrow-item">
          <el-select
              v-model="form.status"
              placeholder="请选择状态"
              clearable
              class="search-narrow-select"
          >
            <el-option
                v-for="dict in ka_status"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="GiftCard">
import { listGiftCard, getGiftCard, delGiftCard, addGiftCard, updateGiftCard } from "@/api/GiftCard/GiftCard"
import {parseTime} from "../../../utils/ruoyi.js";

const { proxy } = getCurrentInstance()
const { ka_status, ka_usage_type, gift_type } = proxy.useDict(
    'ka_status',
    'ka_usage_type',
    'gift_type'
)

const GiftCardList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const amountSum = ref(0)
const title = ref("")
const dateRange = ref([])

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    sender: null,
    subject: null,
    giftType: null,
    //dtStr: null,

    code: null,
    orderNumber: null,
    amount: null,
    extraNumber: null,
    usageType: null,
    status: null,
    beginTime: undefined,
    endTime: undefined
  },
  rules: {
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询礼品卡列表 */
function getList() {
  loading.value = true

  const query = {
    ...queryParams.value,
    beginTime: dateRange.value?.[0] || undefined,
    endTime: dateRange.value?.[1] || undefined
  }

  listGiftCard(query).then(response => {
    GiftCardList.value = response.rows
    total.value = response.total
    amountSum.value = response.msg
  }).finally(() => {
    loading.value = false
  })
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    sender: null,
    subject: null,
    giftType: null,
    dtStr: null,
    code: null,
    orderNumber: null,
    amount: null,
    extraNumber: null,
    usageType: null,
    status: null,
    createTime: null
  }
  proxy.resetForm("GiftCardRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = []
  proxy.resetForm("queryRef")
  handleQuery()
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加礼品卡"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getGiftCard(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改礼品卡"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["GiftCardRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateGiftCard(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addGiftCard(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除礼品卡编号为"' + _ids + '"的数据项？').then(function() {
    return delGiftCard(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('GiftCard/GiftCard/export', {
    ...queryParams.value
  }, `GiftCard_${new Date().getTime()}.xlsx`)
}

getList()
</script>

<style scoped>
/* 搜索区域：允许自动换行 */
.search-form {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
}

/* 搜索区域里的每个表单项之间留点间距 */
.search-form .el-form-item {
  margin-right: 12px;
}

/* 三个下拉：类型 / 使用类型 / 状态，自适应宽度 */
.search-auto-item {
  flex: 1 1 180px;
  min-width: 150px;
  max-width: 200px;
}

/* 下拉框占满自己那一格 */
.search-auto-select {
  width: 100%;
}

.search-form .el-form-item__label {
  white-space: nowrap;
}

.search-form :deep(.el-form-item__label) {
  white-space: nowrap;
  flex: 0 0 90px;
}
</style>

