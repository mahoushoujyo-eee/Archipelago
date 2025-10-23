<template>
  <footer class="app-footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-logo">
          <h2>Archipelago</h2>
          <p>一个专注于学习和技术交流的网站，记录学习过程和实践经验，促进知识分享与讨论。</p>
        </div>
        
        <div class="footer-links">
          <div class="links-group">
            <h3>导航</h3>
            <ul>
              <li><router-link to="/">首页</router-link></li>
              <li><router-link to="/blog">博客</router-link></li>
            </ul>
          </div>
          
          <div class="links-group">
            <h3>账户</h3>
            <ul>
              <li><router-link to="/login">登录</router-link></li>
              <li><router-link to="/register">注册</router-link></li>
            </ul>
          </div>
          
          <div class="links-group">
            <h3>法律</h3>
            <ul>
              <li><router-link to="/privacy-policy">隐私政策</router-link></li>
              <li><router-link to="/terms-of-service">服务条款</router-link></li>
            </ul>
          </div>
        </div>
      </div>
      
      <div class="footer-bottom">
        <p>&copy; {{ currentYear }} Archipelago. 保留所有权利。</p>
        <div class="social-icons">
          <a href="#" aria-label="GitHub">
            <el-icon><Platform /></el-icon>
          </a>
          <a href="#" aria-label="Twitter">
            <el-icon><ChatDotRound /></el-icon>
          </a>
          <a href="#" aria-label="LinkedIn">
            <el-icon><Connection /></el-icon>
          </a>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { computed } from 'vue'

const currentYear = computed(() => new Date().getFullYear())
</script>

<style scoped>
.app-footer {
  background-color: #f5f5f5;
  padding: 3rem 0 1.5rem;
  margin-top: auto;
}

.dark-mode .app-footer {
  background-color: var(--el-bg-color);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
}

.footer-content {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  margin-bottom: 2rem;
  gap: 2rem;
}

.footer-logo h2 {
  margin: 0 0 0.5rem;
  color: #0288d1;
  font-size: 1.5rem;
  font-family: 'Playfair Display', 'Georgia', 'Times New Roman', serif;
  font-weight: 700;
  letter-spacing: 0.3px;
}

.footer-logo p {
  margin: 0;
  color: #666;
}

.dark-mode .footer-logo p {
  color: var(--el-text-color-secondary);
}

.footer-links {
  display: flex;
  flex-wrap: wrap;
  gap: 3rem;
}

.links-group h3 {
  font-size: 1rem;
  margin: 0 0 1rem;
  color: #333;
}

.dark-mode .links-group h3 {
  color: var(--el-text-color-primary);
}

.links-group ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.links-group li {
  margin-bottom: 0.5rem;
}

.links-group a {
  text-decoration: none;
  color: #666;
  transition: color 0.3s;
}

.dark-mode .links-group a {
  color: var(--el-text-color-secondary);
}

.links-group a:hover {
  color: #0288d1;
}

.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 1.5rem;
  border-top: 1px solid #ddd;
}

.dark-mode .footer-bottom {
  border-top-color: var(--el-border-color);
}

.footer-bottom p {
  margin: 0;
  color: #666;
  font-size: 0.875rem;
}

.dark-mode .footer-bottom p {
  color: var(--el-text-color-secondary);
}

.social-icons {
  display: flex;
  gap: 1rem;
}

.social-icons a {
  color: #666;
  font-size: 1.25rem;
  transition: color 0.3s;
}

.dark-mode .social-icons a {
  color: var(--el-text-color-secondary);
}

.social-icons a:hover {
  color: #0288d1;
}

@media (max-width: 768px) {
  .footer-content {
    flex-direction: column;
    gap: 2rem;
  }
  
  .footer-bottom {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }
}
</style>