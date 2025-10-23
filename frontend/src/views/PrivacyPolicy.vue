<template>
  <div class="privacy-policy">
    <div class="container">
      <div class="content">
        <h1>隐私政策</h1>
        <p class="last-updated">最后更新：{{ lastUpdated }}</p>
        
        <div class="policy-content">
          <section>
            <h2>1. 信息收集</h2>
            <p>我们收集以下类型的信息：</p>
            <ul>
              <li><strong>个人信息：</strong>当您注册账户时，我们会收集您的用户名、邮箱地址等基本信息。</li>
              <li><strong>使用数据：</strong>我们会收集您如何使用我们服务的信息，包括访问时间、浏览的页面等。</li>
              <li><strong>设备信息：</strong>我们可能收集您设备的相关信息，如IP地址、浏览器类型等。</li>
            </ul>
          </section>

          <section>
            <h2>2. 信息使用</h2>
            <p>我们使用收集的信息用于：</p>
            <ul>
              <li>提供、维护和改进我们的服务</li>
              <li>处理您的请求和交易</li>
              <li>发送重要通知和更新</li>
              <li>个性化您的用户体验</li>
              <li>分析服务使用情况以改进功能</li>
            </ul>
          </section>

          <section>
            <h2>3. 信息共享</h2>
            <p>我们不会出售、交易或转让您的个人信息给第三方，除非：</p>
            <ul>
              <li>获得您的明确同意</li>
              <li>法律要求或政府部门要求</li>
              <li>保护我们的权利、财产或安全</li>
              <li>与可信的第三方服务提供商合作（他们同意保护您的信息）</li>
            </ul>
          </section>

          <section>
            <h2>4. 数据安全</h2>
            <p>我们采取适当的安全措施来保护您的个人信息：</p>
            <ul>
              <li>使用加密技术保护数据传输</li>
              <li>定期更新安全协议</li>
              <li>限制员工对个人信息的访问</li>
              <li>定期备份数据以防丢失</li>
            </ul>
          </section>

          <section>
            <h2>5. Cookie 使用</h2>
            <p>我们使用Cookie来：</p>
            <ul>
              <li>记住您的登录状态</li>
              <li>保存您的偏好设置</li>
              <li>分析网站流量和使用模式</li>
              <li>提供个性化内容</li>
            </ul>
            <p>您可以通过浏览器设置控制Cookie的使用。</p>
          </section>

          <section>
            <h2>6. 您的权利</h2>
            <p>您有权：</p>
            <ul>
              <li>访问我们持有的关于您的个人信息</li>
              <li>要求更正不准确的信息</li>
              <li>要求删除您的个人信息</li>
              <li>反对处理您的个人信息</li>
              <li>数据可携带权</li>
            </ul>
          </section>

          <section>
            <h2>7. 儿童隐私</h2>
            <p>我们的服务不面向13岁以下的儿童。我们不会故意收集13岁以下儿童的个人信息。如果我们发现收集了此类信息，我们会立即删除。</p>
          </section>

          <section>
            <h2>8. 政策更新</h2>
            <p>我们可能会不时更新此隐私政策。重大变更时，我们会通过网站通知或邮件通知您。建议您定期查看此页面以了解最新信息。</p>
          </section>

          <!-- <section>
            <h2>9. 联系我们</h2>
            <p>如果您对此隐私政策有任何疑问或建议，请通过以下方式联系我们：</p>
            <ul>
              <li>邮箱：privacy@archipelago.com</li>
              <li>地址：中国北京市朝阳区xxx街道xxx号</li>
              <li>电话：+86-xxx-xxxx-xxxx</li>
            </ul>
          </section> -->
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const lastUpdated = ref('2025年8月2日')
</script>

<style scoped>
.privacy-policy {
  min-height: 100vh;
  padding: 6rem 0 2rem;
  background: var(--el-bg-color-page);
}

.dark .privacy-policy {
  background: var(--el-bg-color-page);
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 1rem;
  position: relative;
  z-index: 1;
}

.content {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 3rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid var(--el-border-color-lighter);
  position: relative;
}

.dark .content {
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

h1 {
  color: var(--el-text-color-primary);
  font-size: 3rem;
  margin-bottom: 0.5rem;
  text-align: center;
  font-weight: 700;
}

.last-updated {
  text-align: center;
  color: var(--el-text-color-secondary);
  margin-bottom: 3rem;
  font-style: italic;
  font-size: 1.1rem;
  padding: 0.8rem 1.5rem;
  background: var(--el-fill-color-light);
  border-radius: 25px;
  display: inline-block;
  position: relative;
  left: 50%;
  transform: translateX(-50%);
  border: 1px solid var(--el-border-color-lighter);
}

.policy-content {
  line-height: 1.8;
}

section {
  margin-bottom: 3rem;
  padding: 2rem;
  background: var(--el-fill-color-extra-light);
  border-radius: 12px;
  border: 1px solid var(--el-border-color-lighter);
  transition: all 0.3s ease;
}

.dark section {
  background: var(--el-fill-color-darker);
  border: 1px solid var(--el-border-color);
}

section:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.dark section:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

h2 {
  color: var(--el-text-color-primary);
  font-size: 1.6rem;
  margin-bottom: 1.5rem;
  font-weight: 600;
  border-left: 3px solid var(--el-color-primary);
  padding-left: 1rem;
}

p {
  color: var(--el-text-color-regular);
  margin-bottom: 1.5rem;
  font-size: 1.05rem;
  text-align: justify;
}

ul {
  margin-left: 0;
  margin-bottom: 1.5rem;
  padding-left: 0;
}

li {
  color: var(--el-text-color-regular);
  margin-bottom: 0.8rem;
  padding: 0.8rem 1.2rem;
  background: var(--el-bg-color);
  border-radius: 8px;
  border: 1px solid var(--el-border-color-lighter);
  list-style: none;
  transition: all 0.3s ease;
  font-size: 1rem;
}

.dark li {
  background: var(--el-fill-color-darker);
  border: 1px solid var(--el-border-color);
}

li:hover {
  border-color: var(--el-color-primary);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.dark li:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

strong {
  color: var(--el-text-color-primary);
  font-weight: 600;
}



@media (max-width: 768px) {
  .content {
    padding: 2rem 1.5rem;
    border-radius: 16px;
  }
  
  h1 {
    font-size: 2.5rem;
  }
  
  h2 {
    font-size: 1.4rem;
  }
  
  section {
    padding: 1.5rem;
    margin-bottom: 2rem;
  }
  
  .last-updated {
    font-size: 1rem;
    padding: 0.6rem 1.2rem;
  }
}

@media (max-width: 480px) {
  .privacy-policy {
    padding: 5rem 0 2rem;
  }
  
  h1 {
    font-size: 2rem;
  }
  
  .content {
    padding: 1.5rem 1rem;
  }
  
  section {
    padding: 1rem;
  }
}
</style>