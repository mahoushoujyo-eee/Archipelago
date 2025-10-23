```vue
<template>
  <div class="users-management">
    <h2>Manage Users</h2>
    
    <div class="table-actions">
      <el-input
        v-model="searchQuery"
        placeholder="Search users..."
        prefix-icon="Search"
        clearable
        @input="handleSearch"
      />
      
      <el-select v-model="filterRole" placeholder="Filter by role" @change="handleRoleFilter">
        <el-option label="All" value="" />
        <el-option label="User" value="user" />
        <el-option label="Admin" value="admin" />
      </el-select>
    </div>
    
    <el-table
      v-loading="loading"
      :data="users"
      style="width: 100%"
    >
      <el-table-column label="User" min-width="250">
        <template #default="{ row }">
          <div class="user-info">
            <img :src="row.avatar" :alt="row.name" class="user-avatar" />
            <div class="user-details">
              <span class="user-name">{{ row.name }}</span>
              <span class="user-email">{{ row.email }}</span>
            </div>
          </div>
        </template>
      </el-table-column>
      
      <el-table-column prop="role" label="Role" width="120">
        <template #default="{ row }">
          <el-tag :type="row.role === 'admin' ? 'danger' : 'info'">
            {{ row.role }}
          </el-tag>
        </template>
      </el-table-column>
      
      <el-table-column prop="status" label="Status" width="120">
        <template #default="{ row }">
          <el-tag :type="row.status === 'active' ? 'success' : 'danger'">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      
      <el-table-column prop="createdAt" label="Joined" width="150">
        <template #default="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
      </el-table-column>
      
      <el-table-column label="Actions" width="200" fixed="right">
        <template #default="{ row }">
          <el-button
            :type="row.status === 'active' ? 'danger' : 'success'"
            link
            @click="handleToggleBan(row)"
          >
            <el-icon><Warning v-if="row.status === 'active'" /><Check v-else /></el-icon>
            {{ row.status === 'active' ? 'Ban' : 'Unban' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { useUserStore } from '../../stores/user'
import { getUserAvatarUrl } from '../../utils/avatar'

const userStore = useUserStore()

// State
const loading = ref(false)
const searchQuery = ref('')
const filterRole = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const users = ref([])
const total = ref(0)

// Methods
const formatDate = (date) => {
  return dayjs(date).format('MMM D, YYYY')
}

const fetchUsers = async () => {
  loading.value = true
  try {
    const response = await userStore.getAllUsers({
      page: currentPage.value,
      limit: pageSize.value,
      search: searchQuery.value,
      role: filterRole.value
    })
    
    // 处理用户头像
    const usersWithAvatars = await Promise.all(response.users.map(async user => {
      return {
        ...user,
        avatar: await getUserAvatarUrl(user.id, user.avatar)
      }
    }))
    
    users.value = usersWithAvatars
    total.value = response.total
  } catch (error) {
    ElMessage.error('Failed to fetch users')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchUsers()
}

const handleRoleFilter = () => {
  currentPage.value = 1
  fetchUsers()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchUsers()
}

const handleToggleBan = async (user) => {
  try {
    const action = user.status === 'active' ? 'ban' : 'unban'
    await ElMessageBox.confirm(
      `Are you sure you want to ${action} this user?`,
      'Warning',
      {
        confirmButtonText: action === 'ban' ? 'Ban' : 'Unban',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }
    )
    
    if (action === 'ban') {
      await userStore.banUser(user.id)
    } else {
      await userStore.unbanUser(user.id)
    }
    
    ElMessage.success(`User ${action}ned successfully`)
    fetchUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`Failed to ${action} user`)
    }
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.users-management {
  margin-top: 2rem;
}

h2 {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  color: #1e293b;
}

:deep(.dark-mode) h2 {
  color: #f1f5f9;
}

.table-actions {
  margin-bottom: 1rem;
  display: flex;
  gap: 1rem;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.user-details {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-weight: 500;
  color: #1e293b;
}

:deep(.dark-mode) .user-name {
  color: #f1f5f9;
}

.user-email {
  font-size: 0.875rem;
  color: #64748b;
}

:deep(.dark-mode) .user-email {
  color: #94a3b8;
}

.pagination {
  margin-top: 1.5rem;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .table-actions {
    flex-direction: column;
  }
}
</style>
```