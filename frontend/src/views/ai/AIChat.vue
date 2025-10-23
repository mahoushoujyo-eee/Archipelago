<template>
  <div class="ai-chat-container">
    <!-- 手机端菜单按钮 -->
    <button class="mobile-menu-btn" @click="toggleSidebar" v-show="!isSidebarOpen">
      <el-icon><Menu /></el-icon>
    </button>
    
    <!-- 遮罩层 -->
    <div class="sidebar-overlay" :class="{ active: isSidebarOpen }" @click="closeSidebar"></div>
    
    <!-- 左侧边栏 -->
    <aside class="sidebar" :class="{ open: isSidebarOpen }">
      <div class="new-chat-button">
        <button class="new-chat-btn" @click="startNewConversation" :disabled="isStartingNewChat">
          <span v-if="!isStartingNewChat">+ 新建对话</span>
          <span v-else>创建中...</span>
        </button>
      </div>
      <div class="chat-list">
        <div 
          v-for="conversation in conversationHistory" 
          :key="conversation.conversationId || conversation.id"
          class="chat-item" 
          :class="{ active: currentConversationId === (conversation.conversationId || conversation.id) }"
        >
          <div class="chat-item-content" @click="selectConversation(conversation)">
            <el-icon><ChatDotRound /></el-icon>
            <span>{{ conversation.title || conversation.firstMessage || '对话' }}</span>
          </div>
          <el-icon 
            class="delete-icon" 
            @click.stop="deleteConversation(conversation)"
            title="删除对话"
          >
            <Delete />
          </el-icon>
        </div>
      </div>
      <div class="settings-section">
        <div class="model-select">
          <label for="model-dropdown">选择模型</label>
          <select id="model-dropdown" v-model="selectedModel">
            <option v-for="item in models" :key="item.id || item.modelName" :value="item.modelName">{{ item.modelName }}</option>
          </select>
        </div>
        <div class="knowledge-base-select">
          <div class="kb-header">
            <label for="kb-dropdown">选择知识库</label>
            <button class="manage-kb-button" @click="goToKnowledgeBaseManager" :disabled="isFunctionDisabled">管理知识库</button>
          </div>
          <select id="kb-dropdown" v-model="selectedKnowledge">
            <option value="">不使用知识库</option>
            <option v-for="knowledge in knowledgeList" :key="knowledge.id" :value="knowledge.id">
              {{ knowledge.title }}
            </option>
          </select>
        </div>
      </div>
    </aside>

    <!-- 主对话区域 -->
    <main class="chat-main">

      <div class="chat-messages" ref="messagesContainer" @scroll="checkIfUserAtBottom">
        <!-- 对话消息将在这里显示 -->
        <div v-if="messages.length === 0" class="empty-state">
          <p v-if="userStore.isLoggedIn">👋 您好！我是您的AI助手，有什么可以帮助您的吗？</p>
          <div v-else class="login-prompt">
            <p>🔐 请先登录后使用AI聊天功能</p>
            <p class="login-hint">登录后您可以：</p>
            <ul class="feature-list">
              <li>与AI助手进行对话</li>
              <li>管理个人知识库</li>
              <li>保存对话历史记录</li>
            </ul>
          </div>
        </div>
        
        <div v-for="(message, index) in messages" :key="index" class="message-item" :class="message.type">
          <div class="message-content">
            <div class="message-text" 
                 v-if="message.type === 'user'">{{ message.content }}</div>
            <div class="message-text markdown-content" 
                 v-else 
                 v-html="renderMarkdown(message.content)"></div>
            <!-- <div class="message-time">{{ formatTime(message.timestamp) }}</div> -->
          </div>
        </div>
        
        <!-- AI响应加载动画 -->
        <div v-if="isAiResponding" class="message-item assistant">
          <div class="message-content">
            <div class="ai-loading">
              <div class="loading-dots">
                <span></span>
                <span></span>
                <span></span>
              </div>
              <span class="loading-text">小助手思考中...</span>
            </div>
          </div>
        </div>
      </div>
      <div class="chat-input-area">
        <div class="input-row">
          <input 
            type="text" 
            placeholder="输入您的问题..." 
            class="chat-input" 
            v-model="userInput"
            @keyup.enter="sendMessage"
          />
          <button class="send-button" @click="sendMessage" :disabled="isFunctionDisabled">
            <i class="icon-send"></i>
          </button>
        </div>
        <div class="function-buttons">
          <button class="function-btn search-btn" @click="toggleWebSearch" :class="{ active: isWebSearchEnabled }">
            <Search class="function-icon" />
            <span>联网搜索</span>
          </button>
          <button class="function-btn tool-btn" @click="toggleToolCall" :class="{ active: isToolCallEnabled }">
            <Tools class="function-icon" />
            <span>工具调用</span>
          </button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, computed } from 'vue';
import { useRouter } from 'vue-router';
import { aiApi } from '../../api/ai.js';
import { useUserStore } from '../../stores/user.js';
import MarkdownIt from 'markdown-it';
import Prism from 'prismjs';
import 'prismjs/themes/prism-solarizedlight.css';
import markdownItPrism from 'markdown-it-prism';
import { ChatDotRound, Delete, Search, Tools, CopyDocument, Check, Close, Menu } from '@element-plus/icons-vue';
import { ElMessageBox, ElMessage } from 'element-plus';

// 导入常用语言支持
import 'prismjs/components/prism-javascript';
import 'prismjs/components/prism-typescript';
import 'prismjs/components/prism-python';
import 'prismjs/components/prism-java';
import 'prismjs/components/prism-css';
import 'prismjs/components/prism-json';
import 'prismjs/components/prism-bash';
import 'prismjs/components/prism-sql';
import 'prismjs/components/prism-go';
import 'prismjs/components/prism-rust';

const router = useRouter();
const userStore = useUserStore();
const models = ref([]);
const selectedModel = ref('');
const userInput = ref('');
const currentConversationId = ref('');
const messages = ref([]);
const loading = ref(false);
const messagesContainer = ref(null);
const conversationHistory = ref([]);
const isStartingNewChat = ref(false);
const isAiResponding = ref(false);
const isWebSearchEnabled = ref(false);
const isToolCallEnabled = ref(false);
const isUserAtBottom = ref(true); // 用户是否在底部
const knowledgeList = ref([]); // 知识库列表
const selectedKnowledge = ref(''); // 选中的知识库
// 管理正在进行的流式响应
const activeStreams = ref(new Map()); // conversationId -> { reader, decoder, accumulatedContent, aiMessageIndex, abortController }
// 手机端侧边栏状态
const isSidebarOpen = ref(false); // 侧边栏是否打开

// 初始化markdown渲染器
const md = new MarkdownIt({
  html: false,
  linkify: true,
  typographer: true
}).use(markdownItPrism, {plugins:[]});

// 复制代码到剪贴板
const copyToClipboard = async (text) => {
  try {
    await navigator.clipboard.writeText(text);
    ElMessage.success('代码已复制到剪贴板');
  } catch (err) {
    console.error('复制失败:', err);
    ElMessage.error('复制失败');
  }
};

// 处理代码块，添加语言标签和复制按钮
const enhanceCodeBlocks = (html) => {
  const parser = new DOMParser();
  const doc = parser.parseFromString(html, 'text/html');
  const codeBlocks = doc.querySelectorAll('pre code');
  
  codeBlocks.forEach((codeElement, index) => {
    const preElement = codeElement.parentElement;
    if (!preElement) return;
    
    // 获取语言类型
    const className = codeElement.className || '';
    const languageMatch = className.match(/language-(\w+)/);
    const language = languageMatch ? languageMatch[1] : 'text';
    const displayLanguage = language.charAt(0).toUpperCase() + language.slice(1).toLowerCase();
    
    // 创建代码块容器
    const codeContainer = doc.createElement('div');
    codeContainer.className = 'code-block-container';
    
    // 创建头部工具栏
    const toolbar = doc.createElement('div');
    toolbar.className = 'code-toolbar';
    
    // 语言标签
    const languageLabel = doc.createElement('span');
    languageLabel.className = 'language-label';
    languageLabel.textContent = displayLanguage;
    
    // 复制按钮
    const copyButton = doc.createElement('button');
    copyButton.className = 'copy-button';
    copyButton.innerHTML = '<svg class="copy-icon" viewBox="0 0 1024 1024" width="14" height="14"><path d="M832 64H296c-4.4 0-8 3.6-8 8v56c0 4.4 3.6 8 8 8h496v688c0 4.4 3.6 8 8 8h56c4.4 0 8-3.6 8-8V96c0-17.7-14.3-32-32-32zM704 192H192c-17.7 0-32 14.3-32 32v530.7c0 8.5 3.4 16.6 9.4 22.6l173.3 173.3c2.2 2.2 4.7 4 7.4 5.5v1.9h4.4c2 0.3 4.1 0.5 6.1 0.5H704c17.7 0 32-14.3 32-32V224c0-17.7-14.3-32-32-32zM350 856.2L263.9 770H350v86.2zM672 888H414V746c0-22.1-17.9-40-40-40H232V256h440v632z"></path></svg> 复制';
    copyButton.setAttribute('data-code', codeElement.textContent || '');
    copyButton.setAttribute('onclick', `copyCode(this)`);
    
    toolbar.appendChild(languageLabel);
    toolbar.appendChild(copyButton);
    
    // 重新构建结构 
    preElement.parentNode.insertBefore(codeContainer, preElement);
    codeContainer.appendChild(toolbar);
    codeContainer.appendChild(preElement);
  });
  
  return doc.body.innerHTML;
};



// 渲染markdown内容
const renderMarkdown = (content) => {
  if (!content) return '';
  const html = md.render(content);
  return enhanceCodeBlocks(html);
};

const goToKnowledgeBaseManager = () => {
  if (!userStore.isLoggedIn) return;
  router.push('/knowledge-base-manager'); // 假设知识库管理页面的路由是 /knowledge-base-manager
};

// 开始新对话
const startNewConversation = async () => {
  // 防止重复点击
  if (isStartingNewChat.value) return;
  
  try {
    isStartingNewChat.value = true;
    
    // 重置当前对话的UI状态（不影响后台流式响应）
    currentConversationId.value = '';
    messages.value = [];
    userInput.value = '';
    loading.value = false;
    isAiResponding.value = false;
    
    // 滚动到顶部
    await scrollToBottom();
    
    // 刷新历史会话列表（可选，如果需要从服务器获取最新状态）
    // await getConversationHistory();
    
  } catch (error) {
    console.error('创建新对话失败:', error);
  } finally {
    isStartingNewChat.value = false;
  }
};

// 选择历史对话
const selectConversation = async (conversation) => {
  try {
    const conversationId = conversation.conversationId || conversation.id;
    currentConversationId.value = conversationId;
    
    // 获取该对话的消息历史
    const result = await aiApi.getOneConversation(conversationId);
    if (result.data && result.data.code === 200 && result.data.data) {
      // 将API返回的消息格式转换为组件使用的格式
      messages.value = result.data.data.map(msg => ({
        id: msg.id,
        content: msg.content,
        type: msg.type === 'USER' ? 'user' : 'assistant',
        timestamp: msg.timestamp
      }));
    } else {
      messages.value = [];
    }
    
    // 检查是否有正在进行的流式响应需要恢复
    const activeStream = activeStreams.value.get(conversationId);
    if (activeStream && activeStream.isActive) {
      // 恢复流式响应状态
      isAiResponding.value = true;
      loading.value = true;
      
      // 检查是否需要添加或更新AI消息
      if (activeStream.aiMessageIndex === -1 || activeStream.aiMessageIndex >= messages.value.length) {
        // 添加新的AI消息
        messages.value.push({
          type: 'assistant',
          content: activeStream.accumulatedContent,
          timestamp: new Date()
        });
        activeStream.aiMessageIndex = messages.value.length - 1;
      } else if (activeStream.aiMessageIndex >= 0 && activeStream.aiMessageIndex < messages.value.length) {
        // 更新现有消息内容
        messages.value[activeStream.aiMessageIndex].content = activeStream.accumulatedContent;
      }
      
      console.log(`恢复对话 ${conversationId} 的流式响应，消息索引: ${activeStream.aiMessageIndex}`);
    } else {
      // 确保没有活跃流式响应时清理UI状态
      isAiResponding.value = false;
      loading.value = false;
    }
    
    // 滚动到底部显示最新消息
    await scrollToBottom();
  } catch (error) {
    console.error('获取对话消息历史失败:', error);
    messages.value = [];
  }
};

// 切换联网搜索功能
const toggleWebSearch = () => {
  isWebSearchEnabled.value = !isWebSearchEnabled.value;
  if (isWebSearchEnabled.value) {
    console.log('联网搜索功能已启用');
  } else {
    console.log('联网搜索功能已禁用');
  }
};

// 切换工具调用功能
const toggleToolCall = () => {
  isToolCallEnabled.value = !isToolCallEnabled.value;
  if (isToolCallEnabled.value) {
    console.log('工具调用功能已启用');
  } else {
    console.log('工具调用功能已禁用');
  }
};

// 切换侧边栏显示状态
const toggleSidebar = () => {
  isSidebarOpen.value = !isSidebarOpen.value;
};

// 关闭侧边栏
const closeSidebar = () => {
  isSidebarOpen.value = false;
};

// 删除历史对话
const deleteConversation = async (conversation) => {
  try {
    const conversationId = conversation.conversationId || conversation.id;
    const conversationTitle = conversation.title || conversation.firstMessage || '对话';
    
    // 使用 Element Plus 确认弹窗
    await ElMessageBox.confirm(
      `确定要删除对话 "${conversationTitle}" 吗？此操作不可撤销。`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    );
    
    // 调用删除 API
    const result = await aiApi.deleteConversation(conversationId);
    
    if (result.data && result.data.code === 200) {
      // 删除成功提示
      ElMessage.success('对话删除成功');
      
      // 如果删除的是当前选中的对话，清空消息列表和当前对话ID
      if (currentConversationId.value === conversationId) {
        currentConversationId.value = '';
        messages.value = [];
      }
      
      // 重新获取历史会话列表
      await getConversationHistory();
    } else {
      ElMessage.error('删除失败，请稍后重试');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除对话失败:', error);
      ElMessage.error('删除失败，请稍后重试');
    }
  }
};

// 检查用户是否在底部
const checkIfUserAtBottom = () => {
  if (!messagesContainer.value) return;
  const { scrollTop, scrollHeight, clientHeight } = messagesContainer.value;
  const threshold = 100; // 100px的阈值
  isUserAtBottom.value = scrollTop + clientHeight >= scrollHeight - threshold;
};

// 智能滚动到底部（只有在用户在底部时才滚动）
const smartScrollToBottom = async () => {
  await nextTick();
  if (messagesContainer.value && isUserAtBottom.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
  }
};

// 强制滚动到底部
const scrollToBottom = async () => {
  await nextTick();
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
    isUserAtBottom.value = true;
  }
};

// 获取历史会话
const getConversationHistory = async () => {
  try {
    const result = await aiApi.getConversationHistory(userStore.user?.id || 0);
    if (result.data && result.data.code === 200 && result.data.data && result.data.data.length > 0) {
      // 保存历史会话数据
      conversationHistory.value = result.data.data;
      // 返回最新的会话记录（列表第一条）
      return result.data.data[0];
    }
    return null;
  } catch (error) {
    console.error('获取历史会话失败:', error);
    return null;
  }
};

// 获取知识库列表
const getKnowledgeList = async () => {
  try {
    const params = {
      userId: userStore.user?.id || 0
    };
    const result = await aiApi.getKnowledgeList(params);
    if (result.data && result.data.code === 200 && result.data.data) {
      knowledgeList.value = result.data.data;
    }
  } catch (error) {
    console.error('获取知识库列表失败:', error);
  }
};

// 发送消息
const sendMessage = async () => {
  if (!userInput.value.trim() || loading.value || !userStore.isLoggedIn) return;
  
  const question = userInput.value.trim();
  userInput.value = ''; // 清空输入框
  
  // 添加用户消息到聊天记录
  messages.value.push({
    type: 'user',
    content: question,
    timestamp: new Date()
  });
  
  smartScrollToBottom();
  
  try {
    loading.value = true;
    isAiResponding.value = true; // 开始显示加载动画
    
    // 根据联网搜索状态调整模型名称
    let modelName = selectedModel.value || 'deepseek-v3-0324';
    if (isWebSearchEnabled.value) {
      modelName += '?search';
    }
    
    // 根据选中的知识库ID获取对应的collectionName
    let knowledgeCollectionName = null;
    if (selectedKnowledge.value) {
      const selectedKB = knowledgeList.value.find(kb => kb.id === selectedKnowledge.value);
      if (selectedKB) {
        knowledgeCollectionName = selectedKB.collectionName;
      }
    }
    
    const params = {
      question: question,
      model: modelName,
      conversationId: currentConversationId.value || '',
      userId: userStore.user?.id || 8,
      knowledge: knowledgeCollectionName,
      toolCalling: isToolCallEnabled.value
    };
    
    // 创建AbortController用于取消请求
    const abortController = new AbortController();
    
    // 使用fetch处理流式响应
    const response = await fetch('/api/llm/ai/chat', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': localStorage.getItem('token')
      },
      body: JSON.stringify(params),
      signal: abortController.signal
    });
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    let accumulatedContent = '';
    let aiMessageIndex = -1;
    let targetConversationId = currentConversationId.value;
    
    // 将流式响应信息存储到activeStreams
    const streamData = {
      reader,
      decoder,
      accumulatedContent: '',
      aiMessageIndex: -1,
      abortController,
      isActive: true
    };
    activeStreams.value.set(targetConversationId || '', streamData);
    
    // 如果是第一次接收到数据，准备AI消息
     const prepareAiMessage = () => {
       if (aiMessageIndex === -1) {
         isAiResponding.value = false;
         aiMessageIndex = messages.value.length;
         messages.value.push({
           type: 'assistant',
           content: '',
           timestamp: new Date()
         });
         
         // 更新streamData中的aiMessageIndex
         streamData.aiMessageIndex = aiMessageIndex;
         
         // 如果当前会话ID为空，获取最新会话ID
         if (!targetConversationId) {
           getConversationHistory().then(latestConversation => {
             if (latestConversation) {
               targetConversationId = latestConversation.conversationId || latestConversation.id || '';
               currentConversationId.value = targetConversationId;
               // 更新activeStreams中的conversationId
               if (activeStreams.value.has('')) {
                 const currentStreamData = activeStreams.value.get('');
                 activeStreams.value.delete('');
                 activeStreams.value.set(targetConversationId, currentStreamData);
               }
             }
           }).catch(error => {
             console.error('获取最新会话ID失败:', error);
           });
         }
       }
     };
    
    // 后台处理流式响应
    const processStream = async () => {
      try {
        while (true) {
          const { done, value } = await reader.read();
          
          if (done) break;
          
          const chunk = decoder.decode(value, { stream: true });
          accumulatedContent += chunk;
          streamData.accumulatedContent = accumulatedContent;
          
          // 如果是第一次接收到数据
           if (aiMessageIndex === -1) {
             prepareAiMessage();
           }
          
          // 只有当前对话匹配且流式响应属于当前对话时才更新UI
          if (currentConversationId.value === targetConversationId && activeStreams.value.has(targetConversationId)) {
            if (aiMessageIndex >= 0 && aiMessageIndex < messages.value.length) {
              messages.value[aiMessageIndex].content = accumulatedContent;
            }
            smartScrollToBottom();
          }
        }
        
        // 流式响应完成后检查是否收到了内容
        if (!accumulatedContent.trim()) {
          // 如果没有收到任何内容，显示默认错误消息
          if (aiMessageIndex === -1) {
            prepareAiMessage();
          }
          if (aiMessageIndex >= 0 && currentConversationId.value === targetConversationId) {
            messages.value[aiMessageIndex].content = '抱歉，我无法回答这个问题。';
          }
        }
        
        // 流式响应完成，清理状态
        const currentStreamData = activeStreams.value.get(targetConversationId || '');
        if (currentStreamData) {
          currentStreamData.isActive = false;
        }
        activeStreams.value.delete(targetConversationId || '');
        
        // 如果是当前对话，清理UI状态
        if (currentConversationId.value === targetConversationId) {
          isAiResponding.value = false;
          loading.value = false;
        }
        
      } catch (error) {
        if (error.name !== 'AbortError') {
          console.error('流式响应处理失败:', error);
          
          // 如果是网络错误且还没有创建AI消息，创建错误消息
          if (aiMessageIndex === -1 && currentConversationId.value === targetConversationId) {
            prepareAiMessage();
          }
          if (aiMessageIndex >= 0 && currentConversationId.value === targetConversationId) {
            messages.value[aiMessageIndex].content = '网络连接出现问题，请稍后重试。';
          }
        }
        
        const currentStreamData = activeStreams.value.get(targetConversationId || '');
        if (currentStreamData) {
          currentStreamData.isActive = false;
        }
        activeStreams.value.delete(targetConversationId || '');
        
        // 如果是当前对话，清理UI状态
        if (currentConversationId.value === targetConversationId) {
          isAiResponding.value = false;
          loading.value = false;
        }
      }
    };
    
    // 启动后台流式处理
    processStream();
    
    // 这段代码已移除，因为它会在用户切换对话时错误触发
    // 默认错误消息现在由processStream函数在真正的错误情况下处理
    
  } catch (error) {
    console.error('发送消息失败:', error);
    // 隐藏加载动画
    isAiResponding.value = false;
    
    // 如果是请求被取消，不显示错误消息
    if (error.name === 'AbortError') {
      console.log('请求已被取消');
    } else {
      // 添加错误消息
      messages.value.push({
        type: 'error',
        content: '发送消息失败，请稍后重试。',
        timestamp: new Date()
      });
    }
    
    // 清理失败的流式响应
    const targetConversationId = currentConversationId.value;
    activeStreams.value.delete(targetConversationId || '');
  } finally {
    loading.value = false;
    isAiResponding.value = false; // 确保加载动画被隐藏
    smartScrollToBottom();
  }
};

// 禁用发送按钮和管理知识库按钮的计算属性
const isFunctionDisabled = computed(() => !userStore.isLoggedIn);

onMounted(async () => {
  // 设置全局复制函数
  window.copyCode = function(button) {
    const code = button.getAttribute('data-code');
    if (code) {
      navigator.clipboard.writeText(code).then(() => {
        button.innerHTML = '<svg class="copy-icon" viewBox="0 0 1024 1024" width="14" height="14"><path d="M912 190h-69.9c-9.8 0-19.1 4.5-25.1 12.2l-87 114.8c-9.4 12.4-24.6 19.9-40.8 19.9H296c-35.3 0-64 28.7-64 64v448c0 35.3 28.7 64 64 64h616c35.3 0 64-28.7 64-64V254c0-35.3-28.7-64-64-64z" fill="#52c41a"></path></svg> 已复制';
        setTimeout(() => {
          button.innerHTML = '<svg class="copy-icon" viewBox="0 0 1024 1024" width="14" height="14"><path d="M832 64H296c-4.4 0-8 3.6-8 8v56c0 4.4 3.6 8 8 8h496v688c0 4.4 3.6 8 8 8h56c4.4 0 8-3.6 8-8V96c0-17.7-14.3-32-32-32zM704 192H192c-17.7 0-32 14.3-32 32v530.7c0 8.5 3.4 16.6 9.4 22.6l173.3 173.3c2.2 2.2 4.7 4 7.4 5.5v1.9h4.4c2 0.3 4.1 0.5 6.1 0.5H704c17.7 0 32-14.3 32-32V224c0-17.7-14.3-32-32-32zM350 856.2L263.9 770H350v86.2zM672 888H414V746c0-22.1-17.9-40-40-40H232V256h440v632z"></path></svg> 复制';
        }, 2000);
      }).catch(err => {
        console.error('复制失败:', err);
        button.innerHTML = '<svg class="copy-icon" viewBox="0 0 1024 1024" width="14" height="14"><path d="M685.4 354.8c0-4.4-3.6-8-8-8l-66 .3L512 465.6l-99.3-118.4-66.1-.3c-4.4 0-8 3.5-8 8 0 1.9.7 3.7 1.9 5.2l130.1 155L340.5 670a8.32 8.32 0 0 0 6.4 13.6l66.1-.3L512 564.4l99.3 118.9 66.1.3c4.4 0 8-3.5 8-8 0-1.9-.7-3.7-1.9-5.2L553.5 515l130.1-155c1.2-1.4 1.8-3.3 1.8-5.2z" fill="#ff4d4f"></path></svg> 失败';
        setTimeout(() => {
          button.innerHTML = '<svg class="copy-icon" viewBox="0 0 1024 1024" width="14" height="14"><path d="M832 64H296c-4.4 0-8 3.6-8 8v56c0 4.4 3.6 8 8 8h496v688c0 4.4 3.6 8 8 8h56c4.4 0 8-3.6 8-8V96c0-17.7-14.3-32-32-32zM704 192H192c-17.7 0-32 14.3-32 32v530.7c0 8.5 3.4 16.6 9.4 22.6l173.3 173.3c2.2 2.2 4.7 4 7.4 5.5v1.9h4.4c2 0.3 4.1 0.5 6.1 0.5H704c17.7 0 32-14.3 32-32V224c0-17.7-14.3-32-32-32zM350 856.2L263.9 770H350v86.2zM672 888H414V746c0-22.1-17.9-40-40-40H232V256h440v632z"></path></svg> 复制';
        }, 2000);
      });
    }
  };
  
  // 获取模型列表
  let result = await aiApi.getModels();
  result = result.data

  console.log(result)

  if(result.code !== 200)
  {
    return;
  }
  
  // 根据用户权限过滤模型列表
  let filteredModels = result.data;
  if (!userStore.isAdmin) {
    // 非管理员用户只能看到level≤1的模型
    filteredModels = result.data.filter(model => model.level <= 1);
  }
  
  models.value = filteredModels;
  
  if (models.value.length > 0) {
    selectedModel.value = models.value[0].modelName;
  }
  
  // 获取历史会话
  await getConversationHistory();
  
  // 获取知识库列表
  await getKnowledgeList();
});

// 组件卸载时清理全局函数和取消正在进行的请求
onUnmounted(() => {
  // 取消所有正在进行的流式响应
  activeStreams.value.forEach((streamData, conversationId) => {
    if (streamData.abortController) {
      streamData.abortController.abort();
    }
  });
  activeStreams.value.clear();
  
  // 清理全局函数
  if (window.copyCode) {
    delete window.copyCode;
  }
});
</script>

<style scoped>
.ai-chat-container {
  display: flex;
  height: calc(100vh - 60px); /* 假设头部高度为60px，根据实际情况调整 */
  background-color: #f0f2f5;
  font-family: 'Arial', sans-serif;
}

.sidebar {
  width: 280px;
  min-width: 280px;
  max-width: 280px;
  flex-shrink: 0;
  background-color: #ffffff;
  border-right: 1px solid #e0e0e0;
  display: flex;
  flex-direction: column;
  padding: 15px;
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.05);
}

.new-chat-button {
  margin-bottom: 20px;
}

.new-chat-btn {
  width: 100%;
  padding: 12px 15px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: background-color 0.3s ease;
}

.new-chat-btn:hover:not(:disabled) {
  background-color: #0056b3;
}

.new-chat-btn:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
  opacity: 0.7;
}

.new-chat-btn:disabled:hover {
  background-color: #6c757d;
}

.chat-list {
  flex-grow: 1;
  overflow-y: auto;
  margin-bottom: 20px;
}

.chat-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 15px;
  margin-bottom: 8px;
  border-radius: 8px;
  transition: background-color 0.2s ease;
  position: relative;
}

.chat-item:hover {
  background-color: #e9ecef;
}

.chat-item.active {
  background-color: #e0eaff;
  color: #007bff;
  font-weight: bold;
}

.chat-item-content {
  display: flex;
  align-items: center;
  flex-grow: 1;
  cursor: pointer;
  min-width: 0;
}

.chat-item-content .el-icon {
  margin-right: 10px;
  flex-shrink: 0;
}

.chat-item-content span {
  flex-grow: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.delete-icon {
  color: #999;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s ease;
  opacity: 0;
  flex-shrink: 0;
  font-size: 16px;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chat-item:hover .delete-icon {
  opacity: 1;
}

.delete-icon:hover {
  color: #ff4757;
  background-color: rgba(255, 71, 87, 0.1);
}

.chat-date {
  font-size: 12px;
  color: #6c757d;
}

.settings-section {
  padding-top: 15px;
  border-top: 1px solid #e0e0e0;
}

.model-select,
.knowledge-base-select {
  margin-bottom: 15px;
}

.model-select label,
.knowledge-base-select label {
  display: block;
  font-size: 14px;
  color: #343a40;
  margin-bottom: 5px;
}

.model-select select,
.knowledge-base-select select {
  width: 100%;
  padding: 8px 10px;
  border: 1px solid #ced4da;
  border-radius: 5px;
  background-color: #f8f9fa;
  font-size: 14px;
  cursor: pointer;
}

.chat-main {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
  position: relative;
  height: 100%;
  min-height: 0;
}



.chat-messages {
  flex-grow: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  max-height: calc(100vh - 200px); /* 设置最大高度，确保滚动条生效 */
  min-height: 0; /* 确保flex子元素可以收缩 */
}

/* 自定义滚动条样式 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.empty-state {
  text-align: center;
  color: #adb5bd;
  font-size: 16px;
  padding: 50px;
}

.login-prompt {
  background-color: #f8f9fa;
  border: 2px dashed #dee2e6;
  border-radius: 12px;
  padding: 30px;
  margin: 20px;
  text-align: center;
  color: #6c757d;
}

.login-prompt p {
  margin: 0 0 15px 0;
  font-size: 18px;
  font-weight: 500;
}

.login-hint {
  font-size: 14px !important;
  color: #868e96 !important;
  margin-bottom: 10px !important;
}

.feature-list {
  list-style: none;
  padding: 0;
  margin: 0;
  text-align: left;
  display: inline-block;
}

.feature-list li {
  padding: 5px 0;
  font-size: 14px;
  color: #495057;
  position: relative;
  padding-left: 20px;
}

.feature-list li::before {
  content: '✓';
  position: absolute;
  left: 0;
  color: #28a745;
  font-weight: bold;
}

.message-item {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
}

.message-item.user {
  align-items: flex-end;
}

.message-item.assistant {
  align-items: center;
  width: 100%;
  justify-content: center;
}

.message-item.error {
  align-items: flex-start;
}

.message-content {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 18px;
  position: relative;
}

.message-item.assistant .message-content {
  width: 90%;
  max-width: 1000px;
  background-color: transparent;
  border: none;
  border-radius: 0;
  padding: 20px 0;
  text-align: left;
  font-size: 20px;
  line-height: 1.6;
  margin: 0 auto;
}

.message-item.user .message-content {
  background-color: #007bff;
  color: white;
  border-bottom-right-radius: 4px;
}



.message-item.error .message-content {
  background-color: #fee;
  color: #d63384;
  border: 1px solid #f5c6cb;
  border-bottom-left-radius: 4px;
}

.message-text {
  font-size: 17.5px;
  line-height: 1.5;
  word-wrap: break-word;
}

/* Markdown内容样式 */
.markdown-content {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', sans-serif;
  word-wrap: break-word;
  overflow-wrap: break-word;
  max-width: 100%;
  padding-left: 8px;
  line-height: 1.7;
}

.markdown-content h1,
.markdown-content h2,
.markdown-content h3,
.markdown-content h4,
.markdown-content h5,
.markdown-content h6 {
  margin: 16px 0 8px 0;
  font-weight: 600;
  line-height: 1.25;
}

.markdown-content h1 { font-size: 1.5em; }
.markdown-content h2 { font-size: 1.3em; }
.markdown-content h3 { font-size: 1.1em; }

.markdown-content p {
  margin: 8px 0;
}

.markdown-content ul,
.markdown-content ol {
  margin: 8px 0;
  padding-left: 20px;
}

.markdown-content li {
  margin: 4px 0;
}

.markdown-content blockquote {
  border-left: 4px solid #dfe2e5;
  padding-left: 12px;
  margin: 12px 0;
  color: #6a737d;
}

.markdown-content table {
  border-collapse: collapse;
  margin: 12px 0;
  width: 100%;
}

.markdown-content th,
.markdown-content td {
  border: 1px solid #dfe2e5;
  padding: 6px 12px;
  text-align: left;
}

.markdown-content th {
  background-color: #f6f8fa;
  font-weight: 600;
}

.markdown-content a {
  color: #0366d6;
  text-decoration: none;
}

.markdown-content a:hover {
  text-decoration: underline;
}

.markdown-content strong {
  font-weight: 600;
}

.markdown-content em {
  font-style: italic;
}

.message-time {
  font-size: 11px;
  opacity: 0.7;
  margin-top: 4px;
  text-align: right;
}

.message-item.assistant .message-time {
  text-align: left;
}

.typing-indicator {
  display: inline-flex;
  align-items: center;
  margin-right: 8px;
}

.typing-indicator span {
  height: 4px;
  width: 4px;
  background-color: #999;
  border-radius: 50%;
  display: inline-block;
  margin-right: 2px;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) {
  animation-delay: -0.32s;
}

.typing-indicator span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

/* AI加载动画样式 */
.ai-loading {
  display: flex;
  align-items: center;
  gap: 8px;
}

.loading-dots {
  display: flex;
  gap: 4px;
}

.loading-dots span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #007bff;
  animation: loading-bounce 1.4s infinite ease-in-out;
}

.loading-dots span:nth-child(1) {
  animation-delay: -0.32s;
}

.loading-dots span:nth-child(2) {
  animation-delay: -0.16s;
}

.loading-dots span:nth-child(3) {
  animation-delay: 0s;
}

.loading-text {
  font-size: 14px;
  color: #666;
  font-style: italic;
}

@keyframes loading-bounce {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1.2);
    opacity: 1;
  }
}

.chat-input-area {
  padding: 8px 20px;
  border-top: 1px solid #e0e0e0;
  background-color: #f8f9fa;
  position: sticky;
  bottom: 0;
  margin-top: auto;
}

.input-row {
  display: flex;
  gap: 10px;
  margin-bottom: 6px;
}

.function-buttons {
  display: flex;
  gap: 8px;
  justify-content: flex-start;
  margin-top: 4px;
}

.function-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border: 1px solid #d0d7de;
  border-radius: 6px;
  background-color: #f6f8fa;
  color: #656d76;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  user-select: none;
}

.function-btn:hover {
  background-color: #f3f4f6;
  border-color: #d0d7de;
}

.function-btn.active {
  background-color: #dbeafe;
  border-color: #3b82f6;
  color: #1d4ed8;
}

.function-icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

/* 夜间模式样式 */
.dark-mode .function-btn {
  background-color: #21262d;
  border-color: #30363d;
  color: #e6edf3;
}

.dark-mode .function-btn:hover {
  background-color: #30363d;
  border-color: #484f58;
}

.dark-mode .function-btn.active {
  background-color: #1f2937;
  border-color: #3b82f6;
  color: #60a5fa;
}

.dark-mode .chat-input-area {
  background-color: #0d1117;
  border-top-color: #21262d;
}

.chat-input {
  flex-grow: 1;
  padding: 10px 15px;
  border: 1px solid #ced4da;
  border-radius: 25px;
  font-size: 16px;
  outline: none;
  transition: border-color 0.3s ease;
}

.chat-input:focus {
  border-color: #007bff;
}

.send-button {
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.send-button:hover:not(:disabled) {
  background-color: #0056b3;
}

.send-button:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
  opacity: 0.6;
}

.send-button .icon-send {
  /* 假设图标样式 */
  font-size: 18px;
}

/* 简单的图标占位符 */
.icon-chat::before {
  content: '💬';
}
.icon-moon::before {
  content: '🌙';
}
.icon-info::before {
  content: 'ℹ️';
}
.icon-send::before {
  content: '➤';
}
</style>

<style>
/* 列表样式 - 全局样式，确保动态生成的markdown内容能应用 */
.markdown-content ul,
.markdown-content ol {
  margin: 8px 0;
  padding-left: 0;
  margin-left: 20px;
}

.markdown-content li {
  margin: 4px 0;
  padding-left: 8px;
}

/* 代码块增强样式 - 全局样式，确保动态生成的内容能应用 */
.markdown-content .code-block-container {
  position: relative;
  margin: 16px 0;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e1e5e9;
  background: #f8f9fa;
}

.markdown-content .code-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: #f1f3f4;
  border-bottom: 1px solid #e1e5e9;
  font-size: 12px;
}

.markdown-content .language-label {
  font-weight: 600;
  color: #5f6368;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.markdown-content .copy-button {
  background: #fff;
  border: 1px solid #dadce0;
  border-radius: 4px;
  padding: 4px 8px;
  font-size: 11px;
  color: #5f6368;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 4px;
}

.markdown-content .copy-button .copy-icon {
  flex-shrink: 0;
  fill: currentColor;
}

.markdown-content .copy-button:hover {
  background: #f8f9fa;
  border-color: #5f6368;
  color: #202124;
}

.markdown-content .code-block-container pre {
  margin: 0;
  border-radius: 0;
  border: none;
  background: #fff;
}

.markdown-content .code-block-container pre code {
  display: block;
  padding: 16px;
  background: transparent;
  border-radius: 0;
}

/* 夜间模式下的代码块样式 */
.dark-mode .markdown-content .code-block-container {
  border-color: #30363d;
  background: #0d1117;
}

.dark-mode .markdown-content .code-toolbar {
  background: #161b22;
  border-bottom-color: #30363d;
}

.dark-mode .markdown-content .language-label {
  color: #8b949e;
}

.dark-mode .markdown-content .copy-button {
  background: #21262d;
  border-color: #30363d;
  color: #8b949e;
}

.dark-mode .markdown-content .copy-button:hover {
  background: #30363d;
  border-color: #8b949e;
  color: #f0f6fc;
}

.dark-mode .markdown-content .code-block-container pre {
  background: #0d1117;
}
</style>

<style scoped>
.kb-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.manage-kb-button {
  padding: 4px 8px;
  border: 1px solid #ccc;
  border-radius: 6px;
  background-color: #E0EAFF;
  cursor: pointer;
  font-size: 12px;
  color: #007bff;
  transition: background-color 0.2s ease;
}

.manage-kb-button:hover:not(:disabled) {
  background-color: #d0e0ff;
}

.manage-kb-button:disabled {
  background-color: #f8f9fa;
  color: #6c757d;
  cursor: not-allowed;
  opacity: 0.6;
  border-color: #dee2e6;
}

/* 黑夜模式样式 */
.dark-mode {
  .ai-chat-container {
    background-color: var(--el-bg-color-page);
    color: var(--el-text-color-primary);
  }
  
  .sidebar {
    background-color: var(--el-bg-color);
    border-right-color: var(--el-border-color);
    box-shadow: 2px 0 5px rgba(0, 0, 0, 0.2);
  }
  
  .new-chat-btn {
    background-color: var(--el-color-primary);
    color: white;
  }
  
  .new-chat-btn:hover {
    background-color: var(--el-color-primary-dark);
  }
  
  .chat-item {
    color: var(--el-text-color-regular);
  }
  
  .chat-item:hover {
    background-color: var(--el-fill-color-light);
  }
  
  .chat-item.active {
    background-color: var(--el-fill-color-darker);
    color: var(--el-color-primary);
  }
  
  .chat-date {
    color: var(--el-text-color-secondary);
  }
  
  .settings-section {
    border-top-color: var(--el-border-color);
  }
  
  .model-select label,
  .knowledge-base-select label {
    color: var(--el-text-color-primary);
  }
  
  .model-select select,
  .knowledge-base-select select {
    background-color: var(--el-fill-color);
    border-color: var(--el-border-color);
    color: var(--el-text-color-primary);
  }
  
  .model-select select:focus,
  .knowledge-base-select select:focus {
    border-color: var(--el-color-primary);
  }
  
  .manage-kb-button {
    background-color: var(--el-fill-color-darker);
    border-color: var(--el-border-color);
    color: var(--el-color-primary);
  }
  
  .manage-kb-button:hover:not(:disabled) {
    background-color: var(--el-fill-color-dark);
  }
  
  .manage-kb-button:disabled {
    background-color: var(--el-fill-color);
    color: var(--el-text-color-disabled);
    cursor: not-allowed;
    opacity: 0.6;
    border-color: var(--el-border-color-lighter);
  }
  
  .send-button:disabled {
    background-color: var(--el-text-color-disabled);
    cursor: not-allowed;
    opacity: 0.6;
  }
  
  .login-prompt {
    background-color: var(--el-fill-color);
    border-color: var(--el-border-color);
    color: var(--el-text-color-regular);
  }
  
  .login-hint {
    color: var(--el-text-color-secondary) !important;
  }
  
  .feature-list li {
    color: var(--el-text-color-primary);
  }
  
  .feature-list li::before {
    color: var(--el-color-success);
  }
  
  .chat-main {
    background-color: var(--el-bg-color);
  }
  

  
  .empty-state {
    color: var(--el-text-color-placeholder);
  }
  
  .chat-input-area {
    background-color: var(--el-fill-color-light);
    border-top-color: var(--el-border-color);
  }
  
  .chat-input {
    background-color: var(--el-bg-color);
    border-color: var(--el-border-color);
    color: var(--el-text-color-primary);
  }
  
  .chat-input::placeholder {
    color: var(--el-text-color-placeholder);
  }
  
  .chat-input:focus {
    border-color: var(--el-color-primary);
  }
  
  .send-button {
    background-color: var(--el-color-primary);
  }
  
  .send-button:hover {
    background-color: var(--el-color-primary-dark);
  }
  
  .message-item.user .message-content {
    background-color: var(--el-color-primary);
    color: white;
  }
  
  .message-item.assistant .message-content {
    background-color: transparent;
    color: var(--el-text-color-primary);
  }
  
  .message-item.error .message-content {
    background-color: var(--el-color-danger-light-9);
    color: var(--el-color-danger);
    border-color: var(--el-color-danger-light-7);
  }
  
  .message-text {
    color: inherit;
  }
  
  .typing-indicator span {
    background-color: var(--el-text-color-secondary);
  }
  
  /* AI加载动画夜间模式样式 */
  .loading-dots span {
    background-color: var(--el-color-primary);
  }
  
  .loading-text {
    color: var(--el-text-color-secondary);
  }
  
  /* 夜间模式滚动条样式 */
  .chat-messages::-webkit-scrollbar-track {
    background: var(--el-fill-color);
  }
  
  .chat-messages::-webkit-scrollbar-thumb {
    background: var(--el-border-color-darker);
  }
  
  .chat-messages::-webkit-scrollbar-thumb:hover {
    background: var(--el-text-color-disabled);
  }
}
  .markdown-content code {
    background: #f7fafc;
    padding: 2px 6px;
    border-radius: 4px;
    font-size: 13px;
    color: #e53e3e;
  }

/* 手机端菜单按钮 */
.mobile-menu-btn {
  display: none;
  position: fixed;
  top: 50%;
  left: 20px;
  transform: translateY(-50%);
  z-index: 1001;
  width: 44px;
  height: 44px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 123, 255, 0.3);
  transition: all 0.3s ease;
}

.mobile-menu-btn:hover {
  background-color: #0056b3;
  transform: scale(1.05);
}

.mobile-menu-btn .el-icon {
  font-size: 20px;
}

/* 遮罩层 */
.sidebar-overlay {
  display: none;
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 999;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.sidebar-overlay.active {
  opacity: 1;
}

/* 手机端适配 */
@media (max-width: 768px) {
  .ai-chat-container {
    position: relative;
  }
  
  .mobile-menu-btn {
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .sidebar-overlay {
    display: block;
    pointer-events: none;
  }
  
  .sidebar-overlay.active {
    pointer-events: auto;
  }
  
  .sidebar {
    position: fixed;
    top: 0;
    left: -280px;
    height: 100vh;
    z-index: 1000;
    transition: left 0.3s ease;
    box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  }
  
  .sidebar.open {
    left: 0;
  }
  
  .chat-main {
    width: 100%;
    margin-left: 0;
  }
  
  .chat-input-area {
    padding: 15px;
  }
  
  .input-row {
    gap: 10px;
  }
  
  .chat-input {
    font-size: 16px; /* 防止iOS缩放 */
  }
  
  .function-buttons {
    gap: 10px;
    margin-top: 10px;
  }
  
  .function-btn {
    padding: 8px 12px;
    font-size: 14px;
  }
  
  .function-icon {
    width: 16px;
    height: 16px;
  }
}
  
  /* 夜间模式下的代码样式 */
  .dark-mode .markdown-content code {
    background: #2d3748;
    color: #68d391;
  }


</style>