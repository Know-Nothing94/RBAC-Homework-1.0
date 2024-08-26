<template>
  <div class="user-management">
    <div class="filter-container">
      <div class="input-button-container">
        <el-input v-model="filters.username" placeholder="请输入用户名" class="filter-item username-input" />
        <el-date-picker v-model="filters.createTime" type="daterange" range-separator="至" start-placeholder="开始日期"
          end-placeholder="结束日期" class="filter-item date-input" />
        <div class="search-button">
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">
            搜索
          </el-button>
          <el-button type="default" icon="el-icon-refresh" @click="handleReset">
            重置
          </el-button>
        </div>
      </div>
      <div class="action-container">
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">
          新增
        </el-button>
        <el-button type="danger" icon="el-icon-delete" @click="handleBatchDelete">
          删除
        </el-button>
        <el-button type="primary" icon="el-icon-upload2" @click="handleImportUserDialog">
          导入
        </el-button>
      </div>
    </div>
    <el-card>
      <el-table :data="tableData.list" style="width: 100%;" @selection-change="handleSelectionChange">
        <el-table-column type="index" width="50"></el-table-column>
        <el-table-column type="selection" width="80"></el-table-column>
        <el-table-column prop="userName" label="用户名" width="180"></el-table-column>
        <el-table-column prop="trueName" label="真实姓名" width="180"></el-table-column>
        <el-table-column prop="roleList" label="角色" width="180">
          <template slot-scope="scope">
            {{ translateRoleList(scope.row.roleList) }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="200">
          <template slot-scope="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>

        <el-table-column prop="status" label="是否激活" width="150">
          <template slot-scope="scope">
            <el-switch v-model="scope.row.status" @change="handleStatusChange(scope.row)"></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页组件 -->
      <el-pagination class="pagination" :current-page.sync="tableData.pageNum" :page-sizes="[10, 20, 30, 40, 50]"
        :page-size.sync="tableData.pageSize" layout="total, sizes, prev, pager, next, jumper" :total="tableData.total"
        @size-change="getUserList" @current-change="getUserList" />
    </el-card>

    <!-- 新增/编辑用户对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" @close="handleDialogClose">
      <el-form :model="currentUser" :rules="rules" ref="userForm" label-width="100px">
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="currentUser.userName"></el-input>
        </el-form-item>
        <el-form-item label="真实姓名" prop="trueName">
          <el-input v-model="currentUser.trueName"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="currentUser.password"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="currentUser.email"></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="currentUser.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="currentUser.address"></el-input>
        </el-form-item>
        <el-form-item label="简介" prop="description">
          <el-input v-model="currentUser.description"></el-input>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="currentUser.phone"></el-input>
        </el-form-item>
        <el-form-item label="角色" prop="roleList">
          <el-select v-model="currentUser.roleList" placeholder="请选择角色">
            <el-option label="普通用户" value="user"></el-option>
            <el-option label="管理员" value="admin"></el-option>
            <el-option label="超级管理员" value="super-admin"></el-option>
          </el-select>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </div>
    </el-dialog>

    <!-- 导入用户对话框 -->
    <el-dialog title="导入用户" :visible.sync="importUserDialogVisible" width="80%">
      <div>
        <el-upload class="upload-demo" drag action="/upload" :on-success="handleUploadSuccess"
          :before-upload="beforeUpload">
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        </el-upload>
      </div>

      <el-table :data="importedUsers" style="width: 100%; margin-top: 20px;">
        <el-table-column type="index" label="序号" width="50"></el-table-column>
        <el-table-column prop="username" label="用户名"></el-table-column>
        <el-table-column prop="realname" label="真实姓名"></el-table-column>
        <el-table-column prop="password" label="密码"></el-table-column>
        <el-table-column prop="email" label="邮箱"></el-table-column>
        <el-table-column prop="gender" label="性别"></el-table-column>
        <el-table-column prop="address" label="地址"></el-table-column>
        <el-table-column prop="description" label="简介"></el-table-column>
        <el-table-column prop="phone" label="电话"></el-table-column>
        <el-table-column prop="role" label="角色"></el-table-column>
      </el-table>

      <div slot="footer" class="dialog-footer">
        <el-button @click="importUserDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="importAllUsers">导入</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      filters: {
        username: '',
        createTime: [],
      },
      tableData: {
        list: [],
        total: 0,
        pageNum: 1,
        pageSize: 10
      },
      selectedRows: [],
      dialogVisible: false,
      dialogTitle: "新增用户",
      currentUser: {
        userName: '',
        trueName: '',
        password: '',
        email: '',
        gender: '',
        address: '',
        description: '',
        phone: '',
        roleList: [],
        avatarList: '',
        status: true
      },
      rules: {
        userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        trueName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        roleList: [{ required: true, message: '请选择角色', trigger: 'change' }],
      },
      importUserDialogVisible: false,
      importedUsers: [],
    };
  },
  methods: {
    formatDateTime(dateArray) {
      const [year, month, day, hour, minute, second] = dateArray;
      const formattedDate = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')} ${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}:${String(second).padStart(2, '0')}`;
      return formattedDate;
    },
    translateRoleList(roleList) {
      if (roleList.includes("user")) {
        return "普通用户";
      } else if (roleList.includes("super")) {
        return "超级管理员";
      } else if (roleList.includes("admin")) {
        return "管理员";
      } else {
        return "未知角色"; // 其他情况
      }
    },
    async fetchUsers() {
      const response = await axios.post('http://localhost:8080/api/vue_users/fetch', {
        username: this.filters.username,
        createTime: this.filters.createTime,
        pageNum: this.tableData.pageNum,
        pageSize: this.tableData.pageSize
      });
      this.tableData.list = response.data.list;
      this.tableData.total = response.data.total;
    },
    handleSearch() {
      this.tableData.pageNum = 1;
      this.fetchUsers();
    },
    handleReset() {
      this.filters.username = '';
      this.filters.createTime = [];
      this.fetchUsers();
    },
    handleAdd() {
      this.dialogTitle = "新增用户";
      // 重置 currentUser 对象
      this.currentUser = {
        userName: '',
        trueName: '',
        password: '',
        email: '',
        gender: '',
        address: '',
        description: '',
        phone: '',
        roleList: [],
        avatarList: '',
        status: true
      };
      this.dialogVisible = true;
      this.$nextTick(() => {
        this.$refs.userForm.resetFields(); // 重置表单验证状态
      });
    },

    handleEdit(user) {
      this.dialogTitle = "编辑用户";

      // 确保 roleList 是字符串类型并解析为数组
      let parsedRoleList = [];
      if (typeof user.roleList === 'string') {
        try {
          parsedRoleList = JSON.parse(user.roleList); // 将字符串解析为数组
        } catch (e) {
          console.error("Failed to parse roleList:", e);
        }
      }

      // 将 roleList 转换为标签
      const roleLabels = parsedRoleList.map(role => {
        if (role === 'user') return '普通用户';
        if (role === 'admin') return '管理员';
        if (role === 'super-admin') return '超级管理员';
        return role; // 未知的角色直接返回
      });

      // 赋值给 currentUser
      this.currentUser = {
        ...user,
        roleList: parsedRoleList // 这里使用原始值或解析后的数组
      };

      // 如果你想在 select 中显示标签，那么你可能需要进一步处理
      this.$nextTick(() => {
        // 将 roleList 更新为标签显示
        this.currentUser.roleList = roleLabels;
      });

      this.dialogVisible = true;
    }
    ,

    async handleSave() {
      this.$refs.userForm.validate(async (valid) => {
        if (valid) {
          try {
            // await axios.post('http://localhost:8080/api/vue_users/save', userData);
            // 检查当前 roleList 是否已经是字符串格式，如果是，则不要再次转换
            if (Array.isArray(this.currentUser.roleList)) {
              // 将数组转换为 JSON 字符串
              this.currentUser.roleList = JSON.stringify(this.currentUser.roleList);
            }
            await axios.post('http://localhost:8080/api/vue_users/save', this.currentUser);
            this.dialogVisible = false;
            this.fetchUsers();
          } catch (error) {
            this.$message.error("保存用户信息失败，请稍后重试");
          }
        }
      });
    },
    handleCancel() {
      this.dialogVisible = false;
      this.$refs.userForm.resetFields();
    },
    handleSelectionChange(val) {
      this.selectedRows = val;
    },

    async handleBatchDelete() {
      if (this.selectedRows.length === 0) {
        this.$message.warning('请选择要删除的用户');
        return;
      }

      this.$confirm('此操作将永久删除选中的用户, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(async () => {
        const ids = this.selectedRows.map(row => row.id);
        try {
          await axios.delete('http://localhost:8080/api/vue_users', { data: { ids } });
          this.$message.success('删除成功');
          this.fetchUsers(); // 删除成功后重新获取用户列表
        } catch (error) {
          this.$message.error("删除用户失败，请稍后重试");
        }
      }).catch(() => {
        this.$message.info('已取消删除');
      });
    },

    async handleDelete(user) {
      this.$confirm('此操作将永久删除该用户, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(async () => {
        try {
          await axios.delete(`http://localhost:8080/api/vue_users/${user.id}`);
          this.$message.success('删除成功');
          this.fetchUsers(); // 删除成功后重新获取用户列表
        } catch (error) {
          this.$message.error("删除用户失败，请稍后重试");
        }
      }).catch(() => {
        this.$message.info('已取消删除');
      });
    },
    async handleStatusChange(user) {
      await axios.put(`http://localhost:8080/api/vue_users/${user.id}`, user);
      this.fetchUsers();
    },
    // 清空表单数据和验证状态
    resetForm() {
      this.currentUser = {
        userName: '',
        trueName: '',
        password: '',
        email: '',
        gender: '',
        address: '',
        description: '',
        phone: '',
        roleList: [],
        avatarList: '',
        status: true
      };
      if (this.$refs.userForm) {
        this.$refs.userForm.resetFields(); // 重置表单验证状态
      }
    },
    handleDialogClose() {
      this.resetForm(); // dialog 关闭时重置表单
    },
    handleCancel() {
      this.dialogVisible = false;
      this.resetForm(); // 取消按钮时也重置表单
    },


    getUserList() {
      this.fetchUsers();
    },
    handlePictureCardPreview(file) {
      console.log(file);
    },
    handleRemove(file, fileList) {
      console.log(file, fileList);
    },
    handleImportUserDialog() {
      this.importUserDialogVisible = true;
    },
    downloadTemplate() {
      window.location.href = '/path-to-your-template-file.xlsx';
    },
    handleUploadSuccess(response, file, fileList) {
      this.importedUsers = response.data;
    },
    beforeUpload(file) {
      const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
      if (!isExcel) {
        this.$message.error('只能上传Excel文件!');
        return false;
      }
      return true;
    },
    importAllUsers() {
      this.$message.success('导入全部用户成功');
      this.importUserDialogVisible = false;
    },
  },
  created() {
    this.fetchUsers();
  }
};
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.filter-container {
  margin-bottom: 10px;
  align-items: center;
}

.input-button-container {
  display: flex;
  margin-right: 20px;
}

.username-input {
  width: 150px;
  margin-right: 10px;
}

.date-input {
  width: 500px;
  margin-right: 10px;
}

.button-container {
  display: flex;
  align-items: center;
  margin-top: -10px;
  margin-right: 20px;
  flex-wrap: wrap;
}

.action-container {
  margin-top: 10px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.dialog-footer {
  text-align: right;
}

.pagination {
  margin-top: 20px;
  text-align: left;
}
</style>
