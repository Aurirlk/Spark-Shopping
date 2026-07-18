<template>
  <div class="product-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商品管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加商品
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.name" placeholder="请输入商品名称" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.categoryId" placeholder="请选择分类" clearable>
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 商品表格 -->
      <el-table :data="productList" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="商品图片" width="100">
          <template #default="{ row }">
            <el-image
              :src="row.mainImage"
              :preview-src-list="[row.mainImage]"
              fit="cover"
              class="product-image"
            />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" min-width="200" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            <span class="price">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="sales" label="销量" width="80" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              link
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑商品' : '添加商品'"
      width="600px"
    >
      <el-form :model="productForm" label-width="100px">
        <el-form-item label="商品名称" required>
          <el-input v-model="productForm.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="分类" required>
          <el-select v-model="productForm.categoryId" placeholder="请选择分类">
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" required>
          <el-input-number v-model="productForm.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="原价">
          <el-input-number v-model="productForm.originalPrice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存" required>
          <el-input-number v-model="productForm.stock" :min="0" />
        </el-form-item>
        <el-form-item label="商品图片">
          <div class="upload-wrap">
            <el-upload
              action="/api/upload/image"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
              :before-upload="beforeUpload"
            >
              <img v-if="productForm.mainImage" :src="productForm.mainImage" class="upload-preview" />
              <el-icon v-else :size="40"><Plus /></el-icon>
            </el-upload>
            <el-button v-if="productForm.mainImage" type="danger" size="small" @click="productForm.mainImage = ''">删除图片</el-button>
          </div>
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="productForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAdminProductList,
  saveProduct,
  updateProduct,
  toggleProductStatus,
  deleteProduct
} from '@/api/admin'
import { getCategoryList } from '@/api/category'

const loading = ref(false)
const productList = ref([])
const categories = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = ref({
  name: '',
  categoryId: null,
  status: null
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const productForm = ref({
  name: '',
  categoryId: null,
  price: 0,
  originalPrice: 0,
  stock: 0,
  description: ''
})

onMounted(() => {
  loadProducts()
  loadCategories()
})

const loadProducts = async () => {
  loading.value = true
  try {
    const res = await getAdminProductList({
      name: searchForm.value.name || undefined,
      categoryId: searchForm.value.categoryId || undefined,
      status: searchForm.value.status ?? undefined,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    productList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {
    console.error('加载商品失败', e)
    ElMessage.error('加载商品失败')
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const res = await getCategoryList()
    categories.value = res.data || []
  } catch (e) {
    console.error('加载分类失败', e)
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadProducts()
}

const handleReset = () => {
  searchForm.value = { name: '', categoryId: null, status: null }
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  productForm.value = { name: '', categoryId: null, price: 0, originalPrice: 0, stock: 0, description: '' }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  productForm.value = { ...row }
  dialogVisible.value = true
}

const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const text = newStatus === 1 ? '上架' : '下架'
  await ElMessageBox.confirm(`确定要${text}该商品吗？`, '提示')
  try {
    const res = await toggleProductStatus(row.id)
    ElMessage.success(res.message || `${text}成功`)
    loadProducts()
  } catch (e) {
    console.error('操作失败', e)
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除该商品吗？', '提示')
  try {
    const res = await deleteProduct(row.id)
    ElMessage.success(res.message || '删除成功')
    loadProducts()
  } catch (e) {
    console.error('删除失败', e)
  }
}

// 图片上传成功
const handleUploadSuccess = (res) => {
  if (res.code === 200) {
    productForm.value.mainImage = res.data.url
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

// 上传前校验
const beforeUpload = (file) => {
  const isImg = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImg) { ElMessage.error('只能上传图片文件'); return false }
  if (!isLt2M) { ElMessage.error('图片大小不能超过2MB'); return false }
  return true
}

const handleSubmit = async () => {
  try {
    let res
    if (isEdit.value) {
      res = await updateProduct(productForm.value)
    } else {
      res = await saveProduct(productForm.value)
    }
    ElMessage.success(res.message || (isEdit.value ? '编辑成功' : '添加成功'))
    dialogVisible.value = false
    loadProducts()
  } catch (e) {
    console.error('保存失败', e)
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadProducts()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadProducts()
}
</script>

<style scoped>
.product-manage {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.product-image {
  width: 60px;
  height: 60px;
  border-radius: 4px;
}

.price {
  color: #f56c6c;
  font-weight: bold;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}

.upload-wrap {
  display: flex;
  align-items: center;
  gap: 12px;
}

.upload-wrap .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: border-color 0.2s;
  overflow: hidden;
}

.upload-wrap .el-upload:hover {
  border-color: #409eff;
}

.upload-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>
