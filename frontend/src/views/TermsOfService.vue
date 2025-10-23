<template>
  <div class="terms-of-service">
    <div class="header-image">
      <img src="/bilibili_header.avif" alt="Archipelago Header" />
      <div class="header-overlay">
        <h1>服务条款</h1>
      </div>
    </div>
    <div class="container">
      <p class="last-updated">最后更新：{{ lastUpdated }}</p>

      <div class="content">
        <div class="terms-content">
          <section>
            <h2>1. 服务条款的接受</h2>
            <p>欢迎使用Archipelago现代化博客平台！通过访问和使用我们的网站，您同意遵守并受以下服务条款的约束。如果您不同意这些条款，请不要使用我们的服务。</p>
          </section>

          <section>
            <h2>2. 服务描述</h2>
            <p>Archipelago是一个基于Vue 3 + Spring Boot的现代化博客平台，专注于编程学习和技术交流，提供以下核心服务：</p>
            <ul>
              <li><strong>博客功能：</strong>Markdown编辑器、文章发布编辑、标签分类管理、全文搜索筛选、热门文章推荐</li>
              <li><strong>用户系统：</strong>用户注册登录、邮箱验证、密码重置、OAuth第三方登录、角色权限管理</li>
              <li><strong>交互功能：</strong>文章点赞收藏、评论系统、用户关注、消息通知</li>
              <li><strong>AI智能功能：</strong>多模型AI对话、知识库管理、RAG智能问答、对话历史管理</li>
              <li><strong>资源管理：</strong>文件上传管理、图片处理裁剪、资源分类搜索、链接分享</li>
              <li><strong>管理功能：</strong>用户管理、文章管理、评论管理、系统统计</li>
            </ul>
          </section>

          <section>
            <h2>3. 用户账户与认证</h2>
            <p>我们提供多种用户认证方式，使用服务时您需要：</p>
            <ul>
              <li><strong>账户注册：</strong>提供准确、完整的注册信息，支持邮箱验证</li>
              <li><strong>登录方式：</strong>支持传统登录和OAuth第三方登录（如GitHub、Google等）</li>
              <li><strong>密码安全：</strong>保护您的账户密码安全，支持密码重置功能</li>
              <li><strong>JWT认证：</strong>我们使用JWT Token进行身份验证，确保安全性</li>
              <li><strong>权限管理：</strong>系统分为普通用户和管理员权限，各有不同功能访问权限</li>
              <li><strong>账户责任：</strong>对您账户下的所有活动负责，及时通知任何未经授权的使用</li>
            </ul>
          </section>

          <section>
            <h2>4. 用户行为规范</h2>
            <p>作为编程学习和技术交流平台，在使用我们的服务时，您同意不会：</p>
            <ul>
              <li><strong>内容规范：</strong>发布违法、有害、威胁、辱骂、骚扰、诽谤、粗俗、淫秽或其他令人反感的内容</li>
              <li><strong>知识产权：</strong>侵犯他人的知识产权或其他权利，包括代码版权、文章原创性等</li>
              <li><strong>垃圾信息：</strong>发布垃圾信息、广告或未经授权的商业内容</li>
              <li><strong>技术安全：</strong>传播病毒、恶意软件或其他有害代码，利用技术漏洞攻击系统</li>
              <li><strong>服务稳定：</strong>干扰或破坏服务的正常运行，包括但不限于DDoS攻击、爬虫滥用等</li>
              <li><strong>身份诚信：</strong>冒充他人或虚假陈述您与他人的关系，包括技术能力的虚假宣传</li>
            </ul>
          </section>

          <section>
            <h2>5. 内容所有权和许可</h2>
            <p>关于技术内容和知识产权的权利责任：</p>
            <ul>
              <li><strong>您的内容：</strong>您保留对发布的博客文章、代码、技术分享等内容的所有权，但授予我们使用、展示、分发的许可</li>
              <li><strong>开源精神：</strong>鼓励分享开源代码和技术知识，但请遵守相应的开源协议</li>
              <li><strong>平台内容：</strong>网站设计、Vue 3组件、Element Plus界面、技术架构等属于我们的知识产权</li>
              <li><strong>AI生成内容：</strong>通过我们AI功能生成的内容，使用权归您所有，但请注意AI内容的准确性</li>
              <li><strong>第三方内容：</strong>我们不对用户发布的技术内容负责，但保留删除违规内容的权利</li>
            </ul>
          </section>

          <section>
            <h2>6. AI功能使用规范</h2>
            <p>我们提供多种AI智能功能，使用时请遵守以下规范：</p>
            <ul>
              <li><strong>AI对话：</strong>多模型AI对话功能仅供学习交流，不保证回答的绝对准确性</li>
              <li><strong>知识库：</strong>RAG知识库功能允许您创建和管理个人知识库，请确保上传内容的合法性</li>
              <li><strong>智能问答：</strong>基于知识库的智能问答结果仅供参考，重要决策请多方验证</li>
              <li><strong>使用限制：</strong>请合理使用AI功能，避免恶意刷取或滥用服务资源</li>
              <li><strong>数据安全：</strong>AI处理的数据会按照隐私政策进行保护，不会用于训练第三方模型</li>
            </ul>
          </section>

          <section>
            <h2>7. 隐私保护与数据安全</h2>
            <p>我们重视您的隐私和数据安全，采用多重保护措施：</p>
            <ul>
              <li><strong>隐私政策：</strong>请查看我们的<router-link to="/privacy-policy" class="link">隐私政策</router-link>了解数据收集和使用详情</li>
              <li><strong>安全特性：</strong>采用JWT Token认证、XSS防护、CSRF防护、输入验证等安全措施</li>
              <li><strong>数据加密：</strong>敏感数据传输采用HTTPS加密，存储采用安全加密算法</li>
              <li><strong>权限控制：</strong>严格的路由权限控制，确保用户只能访问授权内容</li>
            </ul>
          </section>

          <section>
            <h2>8. 技术服务与性能</h2>
            <p>我们致力于提供稳定的技术服务，但请理解：</p>
            <ul>
              <li><strong>技术架构：</strong>基于Vue 3 + Spring Boot的微服务架构，包含用户认证、博客、AI、资源管理等多个服务</li>
              <li><strong>性能优化：</strong>采用路由懒加载、组件按需导入、图片懒加载、代码分割等优化策略</li>
              <li><strong>浏览器支持：</strong>支持Chrome >= 87、Firefox >= 78、Safari >= 14、Edge >= 88</li>
              <li><strong>响应式设计：</strong>移动端优先设计，适配平板和桌面端</li>
              <li><strong>服务可用性：</strong>我们努力保证服务稳定，但不保证100%无故障运行</li>
            </ul>
          </section>

          <section>
            <h2>9. 服务的修改和终止</h2>
            <p>我们保留以下权利：</p>
            <ul>
              <li><strong>功能更新：</strong>随时修改、增加或终止服务功能（如新增AI模型、优化编辑器等）</li>
              <li><strong>账户管理：</strong>暂停或终止违反条款的用户账户</li>
              <li><strong>内容管理：</strong>删除违反条款的技术内容、博客文章或评论</li>
              <li><strong>条款更新：</strong>更新这些服务条款以适应技术发展</li>
              <li><strong>维护升级：</strong>定期进行系统维护和技术升级</li>
            </ul>
            <p>我们会通过网站公告或邮件提前通知重大变更，但紧急安全修复可能无法提前通知。</p>
          </section>

          <section>
            <h2>10. 免责声明</h2>
            <p>作为技术平台，我们的服务按"现状"提供，我们不保证：</p>
            <ul>
              <li><strong>服务连续性：</strong>服务将不间断或无错误，可能因维护、升级或不可抗力中断</li>
              <li><strong>功能完整性：</strong>所有功能将满足您的特定技术需求</li>
              <li><strong>内容准确性：</strong>用户发布的技术内容、AI生成的回答等信息的准确性或可靠性</li>
              <li><strong>兼容性：</strong>与所有第三方工具、库或服务的完全兼容</li>
              <li><strong>数据完整性：</strong>虽然我们采用备份策略，但不保证数据永不丢失</li>
            </ul>
          </section>

          <section>
            <h2>11. 责任限制</h2>
            <p>在法律允许的最大范围内，我们不对以下情况承担责任：</p>
            <ul>
              <li><strong>技术损害：</strong>任何间接、偶然、特殊或后果性损害，包括代码丢失、项目延误等</li>
              <li><strong>业务损失：</strong>利润损失、数据丢失或业务中断，包括因技术故障导致的学习或工作影响</li>
              <li><strong>第三方责任：</strong>第三方用户的行为、发布的技术内容或AI模型的回答</li>
              <li><strong>服务中断：</strong>因维护、升级、网络故障等原因导致的服务中断或终止</li>
              <li><strong>安全事件：</strong>虽然我们采用多重安全措施，但不对所有可能的安全风险承担责任</li>
            </ul>
          </section>

          <section>
            <h2>12. 用户赔偿责任</h2>
            <p>您同意就以下情况对我们进行赔偿和免责：</p>
            <ul>
              <li><strong>违规使用：</strong>因违反服务条款而产生的任何索赔、损失、责任和费用</li>
              <li><strong>内容侵权：</strong>因您发布的技术内容侵犯他人知识产权而产生的法律责任</li>
              <li><strong>技术滥用：</strong>因恶意使用技术功能（如攻击、爬虫滥用等）造成的损失</li>
              <li><strong>虚假信息：</strong>因发布虚假技术信息或误导性内容造成的损害</li>
            </ul>
          </section>

          <section>
            <h2>13. 适用法律与争议解决</h2>
            <p>法律适用和争议处理：</p>
            <ul>
              <li><strong>适用法律：</strong>这些条款受中华人民共和国法律管辖</li>
              <li><strong>争议解决：</strong>任何争议将优先通过友好协商解决</li>
              <li><strong>管辖法院：</strong>协商不成的争议将在我们所在地的人民法院解决</li>
              <li><strong>开源协议：</strong>涉及开源代码的争议还需遵守相应的开源协议条款</li>
            </ul>
          </section>

          <section>
            <h2>14. 条款变更与通知</h2>
            <p>关于服务条款的更新和通知机制：</p>
            <ul>
              <li><strong>更新频率：</strong>我们可能会根据技术发展和法律要求不时更新这些服务条款</li>
              <li><strong>通知方式：</strong>重大变更会通过网站公告、邮件通知或应用内消息通知您</li>
              <li><strong>生效时间：</strong>新条款将在发布后30天生效，紧急安全更新可能立即生效</li>
              <li><strong>接受方式：</strong>继续使用服务即表示您接受修订后的条款</li>
            </ul>
          </section>

          <!-- <section>
            <h2>15. 联系信息与技术支持</h2>
            <p>如果您对这些服务条款或技术问题有任何疑问，请联系我们：</p>
            <ul>
              <li><strong>法律事务：</strong>legal@archipelago.com</li>
              <li><strong>技术支持：</strong>support@archipelago.com</li>
              <li><strong>项目地址：</strong><a href="https://github.com/your-username/archipelago" target="_blank" class="link">GitHub Repository</a></li>
              <li><strong>问题反馈：</strong><a href="https://github.com/your-username/archipelago/issues" target="_blank" class="link">Issues</a></li>
              <li><strong>社区交流：</strong>通过平台内的博客和评论功能进行技术交流</li>
            </ul>
          </section> -->

          <section>
            <h2>15. 其他条款与致谢</h2>
            <p>补充条款和开源致谢：</p>
            <ul>
              <li><strong>条款效力：</strong>如果这些条款的任何部分被认定为无效或不可执行，其余部分仍然有效</li>
              <li><strong>执行豁免：</strong>我们未能执行任何条款不构成对该条款的放弃</li>
              <li><strong>开源致谢：</strong>感谢Vue.js、Element Plus、Vditor、Vite、Pinia等开源项目的支持</li>
              <li><strong>技术栈：</strong>本平台采用现代化技术栈，遵循开源精神，鼓励技术分享和学习</li>
              <li><strong>社区建设：</strong>我们致力于建设一个友好、专业的编程学习和技术交流社区</li>
            </ul>
            <p class="final-note">⭐ 如果这个项目对您有帮助，请给我们一个星标！让我们一起构建更好的技术社区。</p>
          </section>
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
.terms-of-service {
  min-height: 100vh;
  background: #ffffff;
  position: relative;
}

.dark-mode .terms-of-service {
  background: var(--el-bg-color);
}

/* Header Image Styles */
.header-image {
  position: relative;
  width: 100vw;
  margin-left: calc(-50vw + 50%);
  margin-right: calc(-50vw + 50%);
}

.header-image img {
  width: 100%;
  height: auto;
  display: block;
}

.header-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
}

.header-overlay h1 {
  font-size: 4rem;
  font-weight: 700;
  margin-bottom: 1rem;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  font-family: 'Playfair Display', 'Georgia', 'Times New Roman', serif;
}

.last-updated {
  font-size: 1.2rem;
  font-style: italic;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
  background: rgba(255, 255, 255, 0.1);
  padding: 0.5rem 1rem;
  border-radius: 20px;
  backdrop-filter: blur(10px);
  text-align: center;
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 2rem 1rem;
  position: relative;
}

.content {
  background: #ffffff;
  border-radius: 12px;
  padding: 3rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e5e7eb;
  position: relative;
}

.dark-mode .content {
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.terms-content {
  line-height: 1.8;
}

.terms-content {
  line-height: 1.8;
}

section {
  margin-bottom: 2.5rem;
  padding: 2rem;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;
  position: relative;
}

.dark-mode section {
  background: var(--el-fill-color-extra-light);
  border-color: var(--el-border-color);
}

section:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.dark-mode section:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

h2 {
  color: var(--el-text-color-primary);
  font-size: 1.6rem;
  margin-bottom: 1.5rem;
  font-weight: 600;
  position: relative;
  padding-left: 1rem;
  border-left: 4px solid var(--el-color-primary);
}

p {
  color: var(--el-text-color-regular);
  margin-bottom: 1.5rem;
  font-size: 1rem;
  line-height: 1.7;
}

ul {
  margin-left: 0;
  margin-bottom: 1.5rem;
  padding-left: 1rem;
}

li {
  color: var(--el-text-color-regular);
  margin-bottom: 0.8rem;
  padding: 0.5rem 0;
  list-style: disc;
  font-size: 1rem;
  line-height: 1.6;
}

li:hover {
  color: var(--el-color-primary);
}

strong {
  color: var(--el-color-primary);
  font-weight: 600;
}

.link {
  color: var(--el-color-primary);
  text-decoration: none;
  font-weight: 500;
}

.link:hover {
  color: var(--el-color-primary-dark-2);
  text-decoration: underline;
}

/* 添加进入动画 */
section {
  opacity: 0;
  transform: translateY(30px) rotateX(10deg);
  animation: slideInUp 0.8s cubic-bezier(0.4, 0, 0.2, 1) forwards;
}

section:nth-child(1) { animation-delay: 0.1s; }
section:nth-child(2) { animation-delay: 0.15s; }
section:nth-child(3) { animation-delay: 0.2s; }
section:nth-child(4) { animation-delay: 0.25s; }
section:nth-child(5) { animation-delay: 0.3s; }
section:nth-child(6) { animation-delay: 0.35s; }
section:nth-child(7) { animation-delay: 0.4s; }
section:nth-child(8) { animation-delay: 0.45s; }
section:nth-child(9) { animation-delay: 0.5s; }
section:nth-child(10) { animation-delay: 0.55s; }
section:nth-child(11) { animation-delay: 0.6s; }
section:nth-child(12) { animation-delay: 0.65s; }
section:nth-child(13) { animation-delay: 0.7s; }
section:nth-child(14) { animation-delay: 0.75s; }

@keyframes slideInUp {
  to {
    opacity: 1;
    transform: translateY(0) rotateX(0);
  }
}

/* 添加浮动动画 */
.content {
  animation: float 6s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

@media (max-width: 768px) {
  .content {
    padding: 1.5rem;
  }
  
  h1 {
    font-size: 2rem;
  }
  
  h2 {
    font-size: 1.4rem;
  }
  
  section {
    padding: 1.5rem;
    margin-bottom: 2rem;
  }
}

@media (max-width: 480px) {
  .content {
    padding: 1rem;
  }
  
  h1 {
    font-size: 1.8rem;
  }
  
  h2 {
    font-size: 1.2rem;
  }
  
  section {
    padding: 1rem;
    margin-bottom: 1.5rem;
  }
  
  p, li {
    font-size: 0.9rem;
  }
}

/* 黑夜模式样式 */
.dark-mode .header-image::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  pointer-events: none;
  z-index: 1;
}

.dark .header-overlay {
  z-index: 2;
}

.dark .header-image img {
  filter: brightness(0.7);
}
</style>