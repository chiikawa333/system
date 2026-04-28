<template>
  <view class="layout" :style="{ paddingTop: statusBarHeight + 'px' }">
    <view class="bg-gradient"></view>

    <view class="main-container">
      <!-- 顶部导航栏 -->
      <view class="navbar">
        <view class="title-section">
          <text class="title">运输助手</text>
          <text class="subtitle">Warehouse Intelligence</text>
        </view>
      </view>

      <!-- 智能问候卡片 -->
      <view class="greeting-card" :class="greetingClass" @click="openAIChat">
        <view class="greeting-icon">{{ greetingIcon }}</view>
        <view class="greeting-content">
          <text class="greeting-title">{{ greetingTitle }}</text>
          <text class="greeting-message">{{ greetingMessage }}</text>
        </view>
        <view class="greeting-badge">AI 仓储大脑</view>
      </view>

      <!-- AI 对话卡片 -->
      <view class="chat-card">
        <view class="chat-header">
          <text class="chat-title">问问运输助手</text>
          <text class="chat-tip">文字指令</text>
        </view>
        <view class="chat-preview" @click="openAIChat">
          <text class="preview-text">{{ lastAIMessage || "你好，我是运输助手，可以帮你查询库存、安排任务、分析效率。" }}</text>
        </view>
        <view class="quick-actions">
          <view class="quick-btn" v-for="(q, index) in quickQuestions" :key="index" @click="sendQuickQuestion(q)">
            <text>{{ q }}</text>
          </view>
        </view>
      </view>

      <!-- 智能作业提醒 -->
      <view class="reminder-card">
        <view class="reminder-header">
          <text class="reminder-title">📋 智能作业提醒</text>
          <text class="reminder-refresh" @click="refreshReminder">🔄 AI 刷新</text>
        </view>
        <view class="reminder-content">
          <text class="reminder-text">{{ aiReminder }}</text>
        </view>
      </view>

      <!-- 快捷功能操作区 -->
      <view class="action-grid">
        <view class="action-item" v-for="(act, idx) in quickActions" :key="idx" @click="handleQuickAction(act.key)">
          <text class="action-icon">{{ act.icon }}</text>
          <text class="action-name">{{ act.name }}</text>
        </view>
      </view>

      <!-- 运营建议卡片 -->
      <view class="suggestion-card">
        <view class="card-header">
          <text class="card-title">📊 运营优化建议</text>
          <text class="card-refresh" @click="refreshSuggestion">🔄 AI 分析</text>
        </view>
        <view class="suggestion-list">
          <view class="suggestion-item" v-for="(tip, idx) in operationTips" :key="idx">
            <text class="suggestion-icon">{{ tip.icon }}</text>
            <text class="suggestion-text">{{ tip.text }}</text>
          </view>
        </view>
      </view>

      <!-- 仓储健康指标 -->
      <view class="health-card">
        <view class="card-header">
          <text class="card-title">🏭 仓储健康指标</text>
          <text class="card-status" :class="healthStatusClass">{{ healthStatusText }}</text>
        </view>
        <view class="health-grid">
          <view class="health-item">
            <text class="health-label">库容占用</text>
            <text class="health-value">{{ capacityUsage }}%</text>
            <text class="health-sub">{{ capacityStatus }}</text>
          </view>
          <view class="health-item">
            <text class="health-label">设备状态</text>
            <text class="health-value">{{ forkliftAvailable }}/{{ forkliftTotal }}</text>
            <text class="health-sub">叉车可用</text>
          </view>
          <view class="health-item">
            <text class="health-label">温湿度</text>
            <text class="health-value">{{ temperature }}℃ {{ humidity }}%</text>
            <text class="health-sub">{{ envStatus }}</text>
          </view>
        </view>
        <view class="health-chart-tip" @click="openHealthDetail">
          <text>📈 查看详细运输报告 →</text>
        </view>
      </view>

      <!-- 仓储功能区导航 -->
      <view class="nav-card">
        <view class="card-header">
          <text class="card-title">📍 物流功能区</text>
          <text class="card-edit" @click="editFavorites">✏️ 编辑</text>
        </view>
        <view class="nav-grid">
          <view class="nav-item" @click="navigateToZone('receiving')">
            <text class="nav-icon">📦</text>
            <text class="nav-name">收货区</text>
            <text class="nav-time">{{ receivingStatus }}</text>
          </view>
          <view class="nav-item" @click="navigateToZone('storage')">
            <text class="nav-icon">🗄️</text>
            <text class="nav-name">存储区</text>
            <text class="nav-time">{{ storageUtilization }}</text>
          </view>
          <view class="nav-item" @click="navigateToZone('picking')">
            <text class="nav-icon">🔍</text>
            <text class="nav-name">拣货区</text>
            <text class="nav-time">{{ pickingLoad }}</text>
          </view>
          <view class="nav-item" @click="navigateToZone('shipping')">
            <text class="nav-icon">🚚</text>
            <text class="nav-name">发货区</text>
            <text class="nav-time">{{ shippingStatus }}</text>
          </view>
        </view>
      </view>

      <view class="footer-deco"></view>
    </view>

    <!-- AI 对话弹窗 -->
    <view v-if="showChatModal" class="modal-mask" @click="closeChatModal">
      <view class="chat-modal" @click.stop>
        <view class="chat-modal-header">
          <image src="/static/deepseek.png" mode="aspectFit" style="width:40rpx;height:40rpx;margin-right:10rpx;"></image>
            <text style="color: aliceblue;font-size: 24rpx;font-weight: 500;">Deepseek_V3.1</text>
          <text class="close-btn" @click="closeChatModal">×</text>
        </view>

        <scroll-view scroll-y class="chat-messages" :scroll-top="scrollTop" scroll-with-animation>
          <view v-for="(msg, idx) in chatMessages" :key="idx" class="message-row" :class="msg.role">
            <view class="message-bubble">
              <!-- 支持流式输出光标效果 -->
              <text>{{ msg.content }}</text>
              <text v-if="msg.isStreaming" class="cursor">|</text>
            </view>
          </view>
          <!-- 初始思考动画 -->
          <view v-if="isLoading" class="message-row assistant">
            <view class="message-bubble thinking-dots">
              <text>正在分析仓库数据</text>
              <view class="dots"><view></view><view></view><view></view></view>
            </view>
          </view>
        </scroll-view>

        <view class="chat-input-area">
          <!-- 语音输入按钮 -->
            <view class="voice-btn" @click="startVoiceInput">
              <text>{{ isListening ? '🎙️' : '🎤' }}</text>
            </view>

            <input
              v-model="userInput"
              class="chat-input"
              placeholder="输入指令，如：查询A01库存"
              @confirm="sendMessage"
            />
          <view class="send-btn" @click="sendMessage">
            <text>{{ isLoading ? '发送中...' : '发送' }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      statusBarHeight: 0,
      timer: null,
      apiUrl: "http://121.40.121.6:8080/adminsystem/api/chat",
      warehouseName: "华东智能仓",

      greetingIcon: "☀️",
      greetingTitle: "早安，运营人员",
      greetingMessage: "今日待处理出库单8个，库容占用率72%，设备运行正常。",
      greetingClass: "morning",
      lastAIMessage: "",
      aiReminder: "A-03 货架补货提醒：SKU 'MZ-8820' 库存低于安全线，建议今日生成补货单。",

      quickQuestions: [
        "查询A区库存状况",
        "今日出库单汇总",
        "生成补货建议",
        "叉车剩余电量",
        "温湿度是否正常",
        "订单热力图"  // 新增快捷问题
      ],

      quickActions: [
        { key: 'inbound', icon: '📥', name: '入库预约' },
        { key: 'outbound', icon: '📤', name: '出库拣货' },
        { key: 'inventory', icon: '📋', name: '库存盘点' },
        { key: 'transfer', icon: '🔄', name: '库内调拨' }
      ],

      operationTips: [
        { icon: "📦", text: "B区货架周转率偏低，建议优化商品布局" },
        { icon: "⚡", text: "3号叉车电量低于20%，请及时充电" },
        { icon: "🌡️", text: "仓库温度23.5℃，湿度58%，符合存储标准" }
      ],

      capacityUsage: 72,
      capacityStatus: "正常",
      forkliftTotal: 6,
      forkliftAvailable: 5,
      temperature: 23.5,
      humidity: 58,
      envStatus: "适宜",

      receivingStatus: "2车等待",
      storageUtilization: "占用72%",
      pickingLoad: "12单进行",
      shippingStatus: "3车待发",

      showChatModal: false,
      chatMessages: [],
      userInput: "",
      isLoading: false,
      scrollTop: 0,

      lastAlertTime: 0,
      alertCooldown: 30000,
      speechEnabled: true,
      isListening: false,      // 是否正在录音
      speechRecognition: null
    };
  },

  computed: {
    healthStatusText() {
      if (this.capacityUsage < 80 && this.forkliftAvailable >= 4 && this.humidity < 70) return "健康";
      return "需关注";
    },
    healthStatusClass() {
      return this.healthStatusText === "健康" ? "status-good" : "status-warn";
    }
  },

  watch: {

  },

  onLoad() {
    const systemInfo = uni.getSystemInfoSync();
    this.statusBarHeight = systemInfo.statusBarHeight || 20;
    this.initGreeting();
    this.startMockDataUpdate();
  },

  onUnload() {
    if (this.timer) clearInterval(this.timer);
  },

  methods: {
    startMockDataUpdate() {
      this.timer = setInterval(() => {
        if (Math.random() > 0.7) {
          this.capacityUsage = Math.min(100, this.capacityUsage + Math.floor(Math.random() * 5));
        } else {
          this.capacityUsage = Math.max(60, this.capacityUsage - Math.floor(Math.random() * 3));
        }
        if (Math.random() > 0.8) {
          this.forkliftAvailable = Math.max(0, this.forkliftAvailable - 1);
        } else if (Math.random() > 0.6) {
          this.forkliftAvailable = Math.min(this.forkliftTotal, this.forkliftAvailable + 1);
        }
      }, 15000);
    },

    initGreeting() {
      const hour = new Date().getHours();
      if (hour < 6) {
        this.greetingIcon = "🌙";
        this.greetingTitle = "深夜好，夜班辛苦了";
        this.greetingMessage = "夜间作业请注意安全，当前库存稳定，无异常预警。";
        this.greetingClass = "night";
      } else if (hour < 12) {
        this.greetingIcon = "☀️";
        this.greetingTitle = "早上好";
        this.greetingMessage = "今日待处理出库单8个，库容占用率72%，设备运行正常。";
        this.greetingClass = "morning";
      } else if (hour < 18) {
        this.greetingIcon = "⛅";
        this.greetingTitle = "下午好";
        this.greetingMessage = "下午时段拣货效率较高，建议优先处理紧急订单。";
        this.greetingClass = "afternoon";
      } else {
        this.greetingIcon = "🌆";
        this.greetingTitle = "晚上好";
        this.greetingMessage = "今日入库任务已完成90%，请确认明日到货计划。";
        this.greetingClass = "evening";
      }
    },

    async triggerAbnormalAlert(type, baseMessage) {
      const now = Date.now();
      if (now - this.lastAlertTime < this.alertCooldown) return;
      this.lastAlertTime = now;

      let alertText = baseMessage;
      try {
        const aiPrompt = `运输出现异常：${baseMessage}。请用简短、专业且带紧迫感的语气生成一句警告语（不超过30字）。`;
        const aiResponse = await this.callAI(aiPrompt);
        if (aiResponse) alertText = aiResponse;
      } catch (e) {
        console.warn('AI生成警告失败，使用默认文本');
      }

      this.speakText(alertText);
      uni.showToast({ title: alertText, icon: 'none', duration: 4000 });
    },

    // ========== 强化版语音播报 ==========
    speakText(text) {
      console.log('🔊 speakText 被调用，文本内容:', text);

      // #ifdef APP-PLUS
      if (window.plus) {
        const speaker = plus.speech.createSpeaker();
        speaker.speak({
          text: text,
          volume: 0.8,
          speed: 1.0,
          pitch: 1.0
        });
      }
      // #endif

      // #ifdef H5
      if ('speechSynthesis' in window) {
        window.speechSynthesis.cancel();
        const utterance = new SpeechSynthesisUtterance(text);
        utterance.lang = 'zh-CN';
        utterance.rate = 1.0;
        utterance.volume = 1;
        utterance.onerror = (e) => console.error('语音错误:', e);
        window.speechSynthesis.speak(utterance);
      }
      // #endif

      // #ifdef MP-WEIXIN
      uni.showToast({ title: text, icon: 'none', duration: 3000 });
      // #endif
    },

    // 意图识别（返回字符串或延迟动作对象）
    handleLocalIntent(text) {
      const lower = text.toLowerCase();

      if (lower.includes('热力图') || lower.includes('热力') ||
          lower.includes('订单分布') || lower.includes('订单密度') ||
          lower.includes('仓储热力')) {
        return {
          type: 'navigate',
          url: '/pages/heatmap/heatmap',
          reply: '🔥 正在为您加载订单热力图，当前A区订单密度较高，请注意资源调度。',
          speakText: '已为您展示订单热力图，A区订单密度较高，请合理调配人员。'
        };
      }
      // 跳转报告
      if (lower.includes('查看报告') || lower.includes('打开报告') ||
          lower.includes('仓储报告') || lower.includes('运营报告') ||
          lower.includes('可视化报告') || lower.includes('分析报告')) {
        return {
          type: 'navigate',
          url: '/pages/report/report',
          reply: '📊 正在为您生成物流运营分析报告...',
          speakText: '正在为您生成物流运营分析报告'
        };
      }

      // ========== 新增：对比订单数据 → 跳转 findcar 页 ==========
      if (lower.includes('对比订单') || lower.includes('订单对比') ||
          lower.includes('订单数据对比') || lower.includes('对比数据') ||
          lower.includes('对比出库单') || lower.includes('关联设备')) {
        return {
          type: 'navigate',
          url: '/pages/findcar/findcar',          // 注意这是一个 tabBar 页面
          reply: '🚚 今日检测到关联设备查询出库订单，正在对比订单数据中。',
          speakText: '今日检测到关联设备查询出库订单，查看详情，对比出库订单中，共5条记录，订单无误。'
        };
      }

      // 叉车查询 → 触发语音播报
      if (lower.includes('叉车') || lower.includes('电量')) {
        const lowBat = this.forkliftTotal - this.forkliftAvailable;
        const replyText = `可用叉车${this.forkliftAvailable}台，${lowBat > 0 ? lowBat + '台需充电或维护' : '全部正常'}。`;
        return {
          type: 'speak',
          text: replyText,
          reply: replyText
        };
      }

      // 以下为立即回复的文本
      if (lower.includes("库存") && lower.includes("a")) {
        return `A区当前占用率${this.capacityUsage}%，SKU种类156，总件数8,420。${this.capacityUsage>80?'建议减少入库':'库位充足'}。`;
      }
      if (lower.includes("出库单") || lower.includes("订单")) {
        return `今日待处理出库单12个，紧急订单3个，已完成6个。`;
      }
      if (lower.includes("补货") || lower.includes("缺货")) {
        return "以下SKU低于安全库存：MZ-8820 (余32件)、PL-4560 (余18件)，建议生成补货计划。";
      }
      if (lower.includes("温湿度")) {
        return `实时环境：温度${this.temperature}℃，湿度${this.humidity}%，符合GSP标准。`;
      }
      if (lower.includes("入库")) {
        this.showToast("📥 已打开入库预约看板");
        return "今日已预约入库12车，已完成8车，请安排收货人员。";
      }
      if (lower.includes("盘点")) {
        return "最近一次盘点在昨日，差异率0.3%，建议下周三进行月度盘点。";
      }
      if (lower.includes("你好") || lower.includes("您好")) {
        return "你好！我是仓储助手，可以帮你管理库存、安排任务、分析效率。";
      }
      if (lower.includes("谢谢") || lower.includes("感谢")) {
        return "不客气，随时为您提升仓储效率！";
      }
      return null;
    },

    showToast(msg) {
      uni.showToast({ title: msg, icon: "none", duration: 2000 });
    },

    handleQuickAction(key) {
      const actions = {
        inbound: () => this.showToast("📥 入库预约看板开发中"),
        outbound: () => this.showToast("📤 出库拣货任务已同步"),
        inventory: () => this.showToast("📋 库存快照生成中"),
        transfer: () => this.showToast("🔄 库内调拨模拟")
      };
      actions[key]?.();
      this.userInput = key === 'inbound' ? '今日入库预约情况' :
                       key === 'outbound' ? '待拣货订单' :
                       key === 'inventory' ? '库存准确率' : '库内移库建议';
      this.openAIChat();
      setTimeout(() => {
        this.isLoading = false;
        // 快捷操作可能触发语音，但这里保持原有简单逻辑
        this.chatMessages.push({ role: "assistant", content: "功能开发中..." });
        this.scrollToBottom();
      }, 1000);
    },

    buildContextPrompt(question) {
      const hour = new Date().getHours();
      const timeGreeting = hour < 6 ? "深夜" : hour < 12 ? "上午" : hour < 18 ? "下午" : "晚上";
      const lowerQuestion = question.toLowerCase();
      const isWarehouseRelated =
        lowerQuestion.includes('库存') || lowerQuestion.includes('货') ||
        lowerQuestion.includes('订单') || lowerQuestion.includes('拣货') ||
        lowerQuestion.includes('叉车') || lowerQuestion.includes('温湿度') ||
        lowerQuestion.includes('库容') || lowerQuestion.includes('补货');

      if (isWarehouseRelated) {
        return `当前是${timeGreeting}。仓储实时数据：
- 库容占用率：${this.capacityUsage}%
- 可用叉车：${this.forkliftAvailable}/${this.forkliftTotal}
- 温湿度：${this.temperature}℃ / ${this.humidity}%
- 待处理出库单：12单，紧急3单
用户问题：${question}
请根据以上数据，以仓储专家身份回答。要求：数据准确、建议实用、语气专业自然。不超过100字。`;
      }
      return `现在是${timeGreeting}。用户问题：${question}。请用专业且简洁的语言回答（不超过80字）。`;
    },

    // ========== 核心发送逻辑（新增流式动画） ==========
    async sendMessage() {
      if (!this.userInput.trim()) return;
      const question = this.userInput.trim();
      this.userInput = "";
      this.chatMessages.push({ role: "user", content: question });
      this.scrollToBottom();

      const localResult = this.handleLocalIntent(question);
      console.log('📋 handleLocalIntent 返回:', localResult);

      // 判断是否为延迟动作对象
      if (localResult && typeof localResult === 'object' && localResult.type) {
        // ---- 新增：流式输出处理 navigate 和 speak 类型 ----
        if (localResult.type === 'navigate') {
          // 1. 显示思考动画
          this.isLoading = true;
          this.scrollToBottom();

          // 2. 1秒后结束思考，开始流式输出
          setTimeout(() => {
            this.isLoading = false;
            // 添加一条空的助手消息，并开始流式填充
            const msgIndex = this.chatMessages.length;
            this.chatMessages.push({
              role: "assistant",
              content: "",
              isStreaming: true   // 控制光标显示
            });

            // 3. 启动流式输出
            this.streamReply(localResult.reply, msgIndex, () => {
              // 输出完成后的回调：语音播报 → 跳转
              if (localResult.speakText) {
                this.speakText(localResult.speakText);
              }
              // 等待一小段时间让语音启动，再跳转
              setTimeout(() => {
                this.lastAIMessage = localResult.reply;
                // ===== 修改：自动判断是否为 tabBar 页面 =====
                if (localResult.url && localResult.url.includes('/findcar')) {
                  uni.switchTab({ url: localResult.url });
                } else {
                  uni.navigateTo({ url: localResult.url });
                }
              }, 300);
            });
          }, 1000);
          return;
        }

        // 对于 speak 类型，也可做简单流式输出（可选）
        if (localResult.type === 'speak') {
          this.isLoading = true;
          this.scrollToBottom();
          setTimeout(() => {
            this.isLoading = false;
            const msgIndex = this.chatMessages.length;
            this.chatMessages.push({
              role: "assistant",
              content: "",
              isStreaming: true
            });
            this.streamReply(localResult.reply, msgIndex, () => {
              this.speakText(localResult.text);
              this.lastAIMessage = localResult.reply;
            });
          }, 800);
          return;
        }
      }

      // 普通文本回复（立即）
      if (localResult && typeof localResult === 'string') {
        console.log('💬 立即回复文本:', localResult);
        this.chatMessages.push({ role: "assistant", content: localResult });
        this.lastAIMessage = localResult;
        this.scrollToBottom();
        return;
      }

      // 云端 AI 请求
      console.log('☁️ 本地未匹配，调用云端 AI');
      this.isLoading = true;
      this.scrollToBottom();
      const contextPrompt = this.buildContextPrompt(question);
      try {
        const response = await this.callAI(contextPrompt);
        this.chatMessages.push({ role: "assistant", content: response });
        this.lastAIMessage = response;
      } catch (error) {
        this.chatMessages.push({ role: "assistant", content: "仓储大脑暂时连接失败，请稍后重试。" });
      } finally {
        this.isLoading = false;
        this.scrollToBottom();
      }
    },

    // 流式输出方法
    streamReply(fullText, msgIndex, callback) {
      let currentIndex = 0;
      const chars = fullText.split('');
      const timer = setInterval(() => {
        if (currentIndex < chars.length) {
          // 逐字添加
          this.chatMessages[msgIndex].content += chars[currentIndex];
          currentIndex++;
          this.$set(this.chatMessages, msgIndex, this.chatMessages[msgIndex]);
          this.scrollToBottom();
        } else {
          // 完成，移除光标，执行回调
          clearInterval(timer);
          this.chatMessages[msgIndex].isStreaming = false;
          this.$set(this.chatMessages, msgIndex, this.chatMessages[msgIndex]);
          if (callback) callback();
        }
      }, 50); // 50ms一个字，可调整速度
    },

    async callAI(question) {
      return new Promise((resolve, reject) => {
        uni.request({
          url: this.apiUrl,
          method: "POST",
          header: { "Content-Type": "application/json" },
          data: { message: question },
          success: (res) => {
            if (res.statusCode === 200 && res.data?.code === 200 && res.data?.data?.content) {
              resolve(res.data.data.content);
            } else {
              resolve("抱歉，暂时无法回答，请尝试其他指令。");
            }
          },
          fail: (err) => reject(err)
        });
      });
    },

    async refreshReminder() {
      uni.showLoading({ title: "AI 分析中..." });
      const prompt = `当前仓库状态：库容占用${this.capacityUsage}%，可用叉车${this.forkliftAvailable}/${this.forkliftTotal}，温湿度正常。请生成一条今日最重要、最紧急的仓储作业提醒（不超过30字）。`;
      try {
        const res = await this.callAI(prompt);
        this.aiReminder = res || "⚠️ MZ-8820 库存仅32件，今日必须补货！";
      } catch {
        this.aiReminder = "⚠️ 请检查3号叉车电量及B区拣货进度。";
      }
      uni.hideLoading();
    },

    async refreshSuggestion() {
      uni.showLoading({ title: "AI 分析运营数据" });
      setTimeout(() => {
        const suggestion1 = this.capacityUsage > 75
          ? "库容紧张，建议暂缓非必要入库预约。"
          : "库容充足，可承接临时加急入库单。";
        this.operationTips = [
          { icon: "📊", text: `A区拣货路径优化空间约${Math.floor(Math.random()*10+8)}%，建议调整动线。` },
          { icon: "🔋", text: this.forkliftAvailable < 5 ? "3号叉车需充电，否则影响14:00作业。" : "所有叉车电量充足。" },
          { icon: "📦", text: suggestion1 }
        ];
        uni.hideLoading();
        uni.showToast({ title: "AI 建议已更新", icon: "none" });
      }, 600);
    },

    openAIChat() {
      this.showChatModal = true;
      if (this.chatMessages.length === 0) {
        this.chatMessages.push({
          role: "assistant",
          content: "你好！我是仓储AI助手。你可以问我：\n• 查询库存\n• 今日订单状态\n• 生成补货建议\n• 设备与温湿度监控"
        });
      }
    },

    sendQuickQuestion(q) {
      this.userInput = q;
      this.openAIChat();
      setTimeout(() => this.sendMessage(), 300);
    },

    closeChatModal() { this.showChatModal = false; },

    scrollToBottom() {
      this.$nextTick(() => { this.scrollTop = 999999; });
    },

    navigateToZone(zone) {
      const names = { receiving: '收货区', storage: '存储区', picking: '拣货区', shipping: '发货区' };
      uni.showToast({ title: `导航至${names[zone]}`, icon: 'none' });
    },

    openHealthDetail() {
      uni.navigateTo({ url: '/pages/report/report' });
    },

    editFavorites() {
      uni.showToast({ title: '编辑常用功能区', icon: 'none' });
    },

    startVoiceInput() {
      // #ifdef H5
      if (!('webkitSpeechRecognition' in window) && !('SpeechRecognition' in window)) {
        uni.showToast({ title: '当前浏览器不支持语音识别', icon: 'none' });
        return;
      }
      if (this.isListening) return;
      const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
      this.speechRecognition = new SpeechRecognition();
      this.speechRecognition.lang = 'zh-CN';
      this.speechRecognition.interimResults = false;
      this.speechRecognition.continuous = false;
      this.isListening = true;
      uni.showToast({ title: '正在聆听...', icon: 'none', duration: 1000 });
      this.speechRecognition.start();
      this.speechRecognition.onresult = (event) => {
        const result = event.results[event.results.length - 1][0].transcript.trim();
        this.userInput = result;
        this.isListening = false;
        this.speechRecognition = null;
        if (this.userInput) {
          this.sendMessage();
        }
      };
      this.speechRecognition.onerror = (event) => {
        console.error('语音识别出错:', event.error);
        this.isListening = false;
        this.speechRecognition = null;
        uni.showToast({ title: '识别失败，请重试', icon: 'none' });
      };
      this.speechRecognition.onend = () => {
        this.isListening = false;
        this.speechRecognition = null;
      };
      // #endif

      // #ifdef MP-WEIXIN
      uni.showToast({ title: '小程序语音识别请使用插件', icon: 'none' });
      // #endif

      // #ifdef APP-PLUS
      uni.showToast({ title: 'App语音识别请使用plus.speech', icon: 'none' });
      // #endif
    }
  }
};
</script>





<style lang="scss" scoped>
.layout {
  position: relative;
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Arial, sans-serif;
  padding-top: env(safe-area-inset-top);
  background: #0f172a; /* 深色工业风 */
}

.bg-gradient {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(145deg, #0b1120 0%, #13203a 50%, #1a2a44 100%);
  z-index: 0;
}

.main-container {
  position: relative;
  z-index: 1;
  padding: 30rpx 40rpx;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 导航栏 */
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid rgba(255, 255, 255, 0.1);
}

.title-section .title {
  font-size: 64rpx;
  font-weight: 700;
  color: #fff;
  letter-spacing: 2rpx;
}

.title-section .subtitle {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.5);
  margin-top: 8rpx;
}

.warehouse-badge {
  background: rgba(59, 130, 246, 0.2);
  padding: 12rpx 24rpx;
  border-radius: 40rpx;
  border: 1rpx solid rgba(59, 130, 246, 0.4);
  color: #60a5fa;
  font-size: 26rpx;
  font-weight: 500;
}

/* 问候卡片 */
.greeting-card {
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.25), rgba(79, 70, 229, 0.3));
  border-radius: 32rpx;
  padding: 32rpx;
  margin-bottom: 30rpx;
  display: flex;
  align-items: center;
  gap: 24rpx;
  border: 1rpx solid rgba(96, 165, 250, 0.2);

  .greeting-icon {
    font-size: 64rpx;
  }

  .greeting-content {
    flex: 1;
  }

  .greeting-title {
    font-size: 36rpx;
    font-weight: 700;
    color: #fff;
    display: block;
    margin-bottom: 8rpx;
  }

  .greeting-message {
    font-size: 26rpx;
    color: rgba(255, 255, 255, 0.85);
  }

  .greeting-badge {
    background: rgba(255, 255, 255, 0.15);
    padding: 8rpx 20rpx;
    border-radius: 60rpx;
    font-size: 22rpx;
    color: #fff;
    backdrop-filter: blur(5px);
  }
}

/* 对话卡片 */
.chat-card {
  background: rgba(15, 25, 45, 0.7);
  border-radius: 32rpx;
  padding: 32rpx;
  margin-bottom: 30rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(4px);

  .chat-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20rpx;
  }

  .chat-title {
    font-size: 32rpx;
    font-weight: 700;
    color: #fff;
  }

  .chat-tip {
    font-size: 22rpx;
    color: #94a3b8;
  }

  .chat-preview {
    background: rgba(255, 255, 255, 0.05);
    border-radius: 24rpx;
    padding: 24rpx;
    margin-bottom: 24rpx;
    border-left: 6rpx solid #3b82f6;

    .preview-text {
      font-size: 26rpx;
      color: #e2e8f0;
    }
  }

  .quick-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 16rpx;
  }

  .quick-btn {
    background: rgba(59, 130, 246, 0.2);
    padding: 14rpx 28rpx;
    border-radius: 60rpx;
    font-size: 24rpx;
    color: #bfdbfe;
    border: 1rpx solid rgba(59, 130, 246, 0.4);
  }
}

/* 快捷操作网格 */
.action-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20rpx;
  margin-bottom: 30rpx;

  .action-item {
    background: rgba(20, 35, 60, 0.7);
    border-radius: 24rpx;
    padding: 24rpx 0;
    text-align: center;
    border: 1rpx solid rgba(255, 255, 255, 0.08);
    backdrop-filter: blur(4px);

    .action-icon {
      font-size: 48rpx;
      display: block;
      margin-bottom: 8rpx;
    }

    .action-name {
      font-size: 24rpx;
      color: #cbd5e1;
    }
  }
}

/* 提醒卡片 */
.reminder-card {
  background: rgba(15, 25, 45, 0.7);
  border-radius: 32rpx;
  padding: 32rpx;
  margin-bottom: 30rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.1);

  .reminder-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20rpx;
  }

  .reminder-title {
    font-size: 32rpx;
    font-weight: 700;
    color: #fff;
  }

  .reminder-refresh {
    font-size: 22rpx;
    color: #60a5fa;
  }

  .reminder-content {
    padding: 20rpx;
    background: rgba(0, 0, 0, 0.2);
    border-radius: 16rpx;
    border-left: 6rpx solid #f59e0b;
  }

  .reminder-text {
    font-size: 26rpx;
    color: #f1f5f9;
    line-height: 1.5;
  }
}

/* 建议卡片 */
.suggestion-card {
  background: rgba(15, 25, 45, 0.7);
  border-radius: 32rpx;
  padding: 32rpx;
  margin-bottom: 30rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.1);
}

.suggestion-list .suggestion-item {
  display: flex;
  align-items: center;
  padding: 18rpx 0;
  border-bottom: 1rpx solid rgba(255, 255, 255, 0.05);

  &:last-child { border-bottom: none; }

  .suggestion-icon {
    font-size: 36rpx;
    margin-right: 20rpx;
  }

  .suggestion-text {
    font-size: 26rpx;
    color: #e2e8f0;
  }
}

/* 健康卡片 */
.health-card {
  background: rgba(15, 25, 45, 0.7);
  border-radius: 32rpx;
  padding: 32rpx;
  margin-bottom: 30rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.1);
}

.health-grid {
  display: flex;
  justify-content: space-around;
  margin-bottom: 24rpx;

  .health-item {
    text-align: center;

    .health-label {
      font-size: 22rpx;
      color: #94a3b8;
      display: block;
      margin-bottom: 8rpx;
    }

    .health-value {
      font-size: 36rpx;
      font-weight: 700;
      color: #60a5fa;
      display: block;
      margin-bottom: 4rpx;
    }

    .health-sub {
      font-size: 20rpx;
      color: #cbd5e1;
    }
  }
}

.health-chart-tip {
  text-align: right;
  font-size: 24rpx;
  color: #60a5fa;
  padding-top: 16rpx;
  border-top: 1rpx solid rgba(255, 255, 255, 0.08);
}

/* 导航卡片 */
.nav-card {
  background: rgba(15, 25, 45, 0.7);
  border-radius: 32rpx;
  padding: 32rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.1);
}

.nav-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16rpx;

  .nav-item {
    background: rgba(255, 255, 255, 0.03);
    border-radius: 24rpx;
    padding: 24rpx 0;
    text-align: center;
    border: 1rpx solid rgba(255, 255, 255, 0.05);

    .nav-icon {
      font-size: 44rpx;
      display: block;
      margin-bottom: 8rpx;
    }

    .nav-name {
      font-size: 24rpx;
      color: #fff;
      display: block;
      margin-bottom: 4rpx;
    }

    .nav-time {
      font-size: 20rpx;
      color: #94a3b8;
    }
  }
}

/* 卡片头部通用 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;

  .card-title {
    font-size: 32rpx;
    font-weight: 700;
    color: #fff;
  }

  .card-refresh, .card-edit, .card-status {
    font-size: 22rpx;
    color: #60a5fa;
  }

  .status-good { color: #10B981; }
  .status-warn { color: #F59E0B; }
}

/* 弹窗样式 */
.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  z-index: 1000;
  display: flex;
  align-items: flex-end;
}

.chat-modal {
  width: 100%;
  height: 70vh;
  background: rgba(15, 25, 45, 0.98);
  border-radius: 32rpx 32rpx 0 0;
  display: flex;
  flex-direction: column;
  border: 1rpx solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.chat-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 32rpx;
  border-bottom: 1rpx solid rgba(255, 255, 255, 0.1);

  .chat-modal-title {
    font-size: 36rpx;
    font-weight: 700;
    color: #fff;
  }

  .close-btn {
    font-size: 40rpx;
    color: #94a3b8;
    padding: 8rpx;
  }
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 24rpx;
}

.message-row {
  margin-bottom: 20rpx;
  display: flex;
}

.message-row.user { justify-content: flex-end; }

.message-bubble {
  max-width: 80%;
  padding: 16rpx 24rpx;
  border-radius: 24rpx;
  font-size: 26rpx;
  line-height: 1.5;
}

.user .message-bubble {
  background: #2563eb;
  color: #fff;
}

.assistant .message-bubble {
  background: #1e293b;
  color: #e2e8f0;
}

.chat-input-area {
  display: flex;
  padding: 20rpx 24rpx;
  gap: 16rpx;
  border-top: 1rpx solid #334155;
  background: #0f172a;

  .chat-input {
    flex: 1;
    background: #1e293b;
    border-radius: 60rpx;
    padding: 16rpx 24rpx;
    color: #fff;
    font-size: 26rpx;
    border: 1rpx solid #334155;
  }

  .send-btn {
    background: #2563eb;
    border-radius: 60rpx;
    padding: 16rpx 32rpx;
    color: #fff;
    font-size: 26rpx;
  }
}
.cursor {
  display: inline-block;
  vertical-align: middle;
  color: #60a5fa;
  font-weight: bold;
  animation: blink 1s infinite;
}

@keyframes blink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0; }
}

/* 思考动画的三个点 */
.thinking-dots .dots {
  display: inline-flex;
  margin-left: 8rpx;
  align-items: center;
}
.thinking-dots .dots view {
  width: 8rpx;
  height: 8rpx;
  border-radius: 50%;
  background: #94a3b8;
  margin: 0 4rpx;
  animation: dot-bounce 1.2s infinite ease-in-out both;
}
.thinking-dots .dots view:nth-child(1) { animation-delay: 0s; }
.thinking-dots .dots view:nth-child(2) { animation-delay: 0.2s; }
.thinking-dots .dots view:nth-child(3) { animation-delay: 0.4s; }
@keyframes dot-bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}.chat-input-area .voice-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  background: rgba(255,255,255,0.1);
  border-radius: 50%;
  margin-right: 10rpx;
}

.footer-deco { height: 32rpx; margin-top: 32rpx; }
</style>