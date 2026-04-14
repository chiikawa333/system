<template>
  <view class="page">
    <!-- 自定义导航栏（非 tabBar 页时显示，若作为 tabBar 页可隐藏返回箭头） -->
    <view class="nav-bar" v-if="!isTabBarPage">
      <!-- <view class="back-icon" @click="goBack">←</view> -->
      <view class="title">维保预约</view>
      <view class="right-placeholder"></view>
    </view>
    <view class="nav-bar" v-else>
      <view class="title" style="text-align:center; width:100%;">维保预约</view>
    </view>

    <scroll-view scroll-y class="scroll-content" :style="{ height: isTabBarPage ? 'calc(100vh - 100px)' : 'auto' }">
      <!-- 维保类型切换 -->
      <view class="form-card">
        <view class="card-title"><text class="title-icon">🔧</text> 服务类型</view>
        <view class="type-switch">
          <view class="type-option" :class="{ active: form.maintenanceType === 1 }" @click="form.maintenanceType = 1">
            常规保养
          </view>
          <view class="type-option" :class="{ active: form.maintenanceType === 2 }" @click="form.maintenanceType = 2">
            车辆维修
          </view>
        </view>
      </view>

      <!-- 基础预约信息 -->
      <view class="form-card">
        <view class="card-title"><text class="title-icon">📍</text> 预约信息</view>

        <!-- 城市 -->
        <view class="form-item">
          <view class="form-label"><text class="required">*</text>城市</view>
          <picker mode="selector" :range="cityList" range-key="name" @change="onCityChange" v-if="useUniPicker">
            <view class="input-field">{{ form.city || '请选择城市' }}</view>
          </picker>
          <view class="input-field" @click="openCityPicker" v-else>{{ form.city || '请选择城市' }}</view>
        </view>

        <!-- 维保服务站 -->
        <view class="form-item">
          <view class="form-label"><text class="required">*</text>维保服务站</view>
          <picker mode="selector" :range="stationList" range-key="name" @change="onStationChange" v-if="useUniPicker">
            <view class="input-field">{{ form.serviceStationName || '请选择服务站' }}</view>
          </picker>
          <view class="input-field" @click="openStationPicker" v-else>{{ form.serviceStationName || '请选择服务站' }}</view>
        </view>

        <!-- 预约日期/时间 -->
        <view class="row-2">
          <view class="form-item">
            <view class="form-label"><text class="required">*</text>预约日期</view>
            <picker mode="date" :value="form.appointmentDate" @change="onDateChange" v-if="useUniPicker">
              <view class="input-field">{{ form.appointmentDateDisplay || '选择日期' }}</view>
            </picker>
            <view class="input-field" @click="openDatePicker" v-else>{{ form.appointmentDateDisplay || '选择日期' }}</view>
          </view>
          <view class="form-item">
            <view class="form-label"><text class="required">*</text>预约时间</view>
            <picker mode="time" :value="form.appointmentTime" @change="onTimeChange" v-if="useUniPicker">
              <view class="input-field">{{ form.appointmentTimeDisplay || '选择时间' }}</view>
            </picker>
            <view class="input-field" @click="openTimePicker" v-else>{{ form.appointmentTimeDisplay || '选择时间' }}</view>
          </view>
        </view>

        <!-- 维保车辆 + 车牌 -->
        <view class="form-item">
          <view class="form-label"><text class="required">*</text>维保车辆</view>
          <picker mode="selector" :range="vehicleList" range-key="label" @change="onVehicleChange" v-if="useUniPicker">
            <view class="input-field">{{ selectedVehicleText || '点击选择车辆' }}</view>
          </picker>
          <view class="input-field" @click="openVehiclePicker" v-else>{{ selectedVehicleText || '点击选择车辆' }}</view>
        </view>
        <view class="form-item">
          <view class="form-label"><text class="required">*</text>车牌号码</view>
          <input class="input-field" v-model="form.licensePlate" placeholder="例如 浙A12345" />
        </view>

        <!-- 联系人/电话 -->
        <view class="row-2">
          <view class="form-item">
            <view class="form-label"><text class="required">*</text>联系人</view>
            <input class="input-field" v-model="form.contactName" placeholder="姓名" />
          </view>
          <view class="form-item">
            <view class="form-label"><text class="required">*</text>联系电话</view>
            <input class="input-field" v-model="form.contactPhone" type="number" placeholder="手机号" />
          </view>
        </view>
      </view>

      <!-- 车辆维修专属：维修方案 -->
      <view class="form-card" v-if="form.maintenanceType === 2">
        <view class="card-title"><text class="title-icon">🛠️</text> 维修方案</view>

        <!-- 维修类目 -->
        <view class="form-item">
          <view class="form-label">维修类目</view>
          <input class="input-field" v-model="repairCategory" placeholder="如：发动机检修、制动系统" />
        </view>

        <!-- 更换零件列表 -->
        <view class="sub-label">🔩 更换零件</view>
        <view class="parts-list">
          <view v-for="(part, idx) in repairParts" :key="idx" class="part-item">
            <view class="part-info">
              <text class="part-name">{{ part.name }}</text>
              <text class="part-price">¥{{ part.price }}</text>
            </view>
            <view class="part-delete" @click="removePart(idx)">✕</view>
          </view>
          <view v-if="repairParts.length === 0" class="empty-parts">暂无零件，请添加</view>
        </view>
        <view class="add-part-btn" @click="showAddPartDialog = true">+ 添加零件</view>

        <!-- 总价 & 周期 -->
        <view class="total-row">
          <text>维修总价</text>
          <text class="total-price">¥{{ totalRepairPrice }}</text>
        </view>
        <view class="form-item">
          <view class="form-label">维修周期</view>
          <input class="input-field" v-model="repairCycle" placeholder="例如 2天" />
        </view>
      </view>
    </scroll-view>

    <!-- 底部提交按钮 -->
    <view class="submit-footer">
      <button class="submit-btn" @click="submitForm" :disabled="submitting" hover-class="none">
        {{ submitting ? '提交中...' : '预约提交' }}
      </button>
    </view>

    <!-- 模拟弹窗（非 uni-app 环境演示用，实际项目中请使用 uni-popup 或自定义组件） -->
    <view class="picker-mask" v-if="showCustomPicker" @click="showCustomPicker = false">
      <view class="picker-panel" @click.stop>
        <view v-for="(item, idx) in pickerData" :key="idx" class="picker-item" @click="onPickerSelect(item)">
          {{ item.label || item.name || item }}
        </view>
        <view class="picker-cancel" @click="showCustomPicker = false">取消</view>
      </view>
    </view>

    <!-- 添加零件弹窗（简易） -->
    <view class="picker-mask" v-if="showAddPartDialog" @click="showAddPartDialog = false">
      <view class="picker-panel" @click.stop>
        <view class="dialog-title">添加零件</view>
        <input class="dialog-input" v-model="newPart.name" placeholder="零件名称" />
        <input class="dialog-input" v-model="newPart.price" type="number" placeholder="单价 (元)" />
        <view class="dialog-actions">
          <button class="dialog-btn cancel" @click="showAddPartDialog = false">取消</button>
          <button class="dialog-btn confirm" @click="addPart">确定</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>

export default {
  name: 'MaintenanceAppointment',
  props: {
    // 是否为 tabBar 页面，若为 true 则隐藏返回箭头并调整布局
    isTabBarPage: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      // 表单数据
      form: {
        maintenanceType: 1,          // 1常规保养 2车辆维修
        city: '',
        serviceStationId: null,
        serviceStationName: '',
        appointmentDate: '',          // ISO 字符串
        appointmentTime: '',
        appointmentDateDisplay: '',
        appointmentTimeDisplay: '',
        vehicleId: null,
        licensePlate: '',
        contactName: '',
        contactPhone: '',
        userId: 1                     // 模拟当前用户ID，实际从 store 获取
      },
      // 维修方案相关
      repairCategory: '',
      repairParts: [],               // [{ name, price }]
      repairCycle: '',

      // 选择器数据
      cityList: [
        { id: 1, name: '北京市' },
        { id: 2, name: '上海市' },
        { id: 3, name: '广州市' },
        { id: 4, name: '深圳市' },
        { id: 5, name: '杭州市' },
        { id: 6, name: '南京市' },
        { id: 7, name: '成都市' },
        { id: 8, name: '武汉市' },
        { id: 9, name: '重庆市' },
        { id: 10, name: '天津市' },
        { id: 11, name: '苏州市' },
        { id: 12, name: '西安市' },
        { id: 13, name: '长沙市' },
        { id: 14, name: '青岛市' },
        { id: 15, name: '郑州市' },
        { id: 16, name: '大连市' },
        { id: 17, name: '宁波市' },
        { id: 18, name: '厦门市' },
        { id: 19, name: '福州市' },
        { id: 20, name: '济南市' },
        { id: 21, name: '沈阳市' },
        { id: 22, name: '合肥市' },
        { id: 23, name: '哈尔滨市' },
        { id: 24, name: '长春市' },
        { id: 25, name: '昆明市' },
        { id: 26, name: '贵阳市' },
        { id: 27, name: '兰州市' },
        { id: 28, name: '南宁市' },
        { id: 29, name: '南昌市' },
        { id: 30, name: '太原市' },
        { id: 31, name: '石家庄市' },
        { id: 32, name: '呼和浩特市' },
        { id: 33, name: '乌鲁木齐市' },
        { id: 34, name: '银川市' }
      ],

      stationList: [
        // 北京
        { id: 101, name: '北京蓝白之星4S店', cityId: 1 },
        { id: 102, name: '京通快速维修中心', cityId: 1 },
        { id: 103, name: '朝阳北路服务站', cityId: 1 },
        // 上海
        { id: 201, name: '上海浦东旗舰店', cityId: 2 },
        { id: 202, name: '闵行七宝维修中心', cityId: 2 },
        // 广州
        { id: 301, name: '广州天河4S店', cityId: 3 },
        { id: 302, name: '白云大道服务站', cityId: 3 },
        // 深圳
        { id: 401, name: '深圳南山科技园店', cityId: 4 },
        { id: 402, name: '龙华民治维修站', cityId: 4 },
        // 杭州
        { id: 501, name: '杭州西湖4S服务中心', cityId: 5 },
        { id: 502, name: '滨江江南大道店', cityId: 5 },
        { id: 503, name: '余杭未来科技城站', cityId: 5 },
        // 南京
        { id: 601, name: '南京江宁4S店', cityId: 6 },
        { id: 602, name: '建邺奥体服务站', cityId: 6 },
        // 成都
        { id: 701, name: '成都高新南区店', cityId: 7 },
        { id: 702, name: '锦江区三圣乡站', cityId: 7 },
        // 武汉
        { id: 801, name: '武汉光谷服务中心', cityId: 8 },
        { id: 802, name: '汉口北维修站', cityId: 8 },
        // 重庆
        { id: 901, name: '重庆江北观音桥店', cityId: 9 },
        { id: 902, name: '渝北汽博中心', cityId: 9 },
        // 天津
        { id: 1001, name: '天津南开4S店', cityId: 10 },
        { id: 1002, name: '滨海新区服务站', cityId: 10 },
        // 苏州
        { id: 1101, name: '苏州工业园区店', cityId: 11 },
        { id: 1102, name: '高新区马运路站', cityId: 11 },
        // 西安
        { id: 1201, name: '西安高新区店', cityId: 12 },
        { id: 1202, name: '未央区服务站', cityId: 12 },
        // 长沙
        { id: 1301, name: '长沙岳麓区4S店', cityId: 13 },
        { id: 1302, name: '星沙维修中心', cityId: 13 },
        // 青岛
        { id: 1401, name: '青岛市南4S店', cityId: 14 },
        { id: 1402, name: '李沧区重庆中路站', cityId: 14 },
        // 郑州
        { id: 1501, name: '郑州郑东新区店', cityId: 15 },
        { id: 1502, name: '金水区花园路站', cityId: 15 },
        // 大连
        { id: 1601, name: '大连沙河口4S店', cityId: 16 },
        // 宁波
        { id: 1701, name: '宁波鄞州店', cityId: 17 },
        // 厦门
        { id: 1801, name: '厦门湖里4S店', cityId: 18 },
        // 福州
        { id: 1901, name: '福州仓山店', cityId: 19 },
        // 济南
        { id: 2001, name: '济南历下4S店', cityId: 20 },
        // 沈阳
        { id: 2101, name: '沈阳铁西店', cityId: 21 },
        // 合肥
        { id: 2201, name: '合肥包河4S店', cityId: 22 },
        // 哈尔滨
        { id: 2301, name: '哈尔滨南岗店', cityId: 23 },
        // 长春
        { id: 2401, name: '长春朝阳4S店', cityId: 24 },
        // 昆明
        { id: 2501, name: '昆明五华店', cityId: 25 },
        // 贵阳
        { id: 2601, name: '贵阳观山湖店', cityId: 26 },
        // 兰州
        { id: 2701, name: '兰州城关4S店', cityId: 27 },
        // 南宁
        { id: 2801, name: '南宁青秀店', cityId: 28 },
        // 南昌
        { id: 2901, name: '南昌红谷滩店', cityId: 29 },
        // 太原
        { id: 3001, name: '太原小店4S店', cityId: 30 },
        // 石家庄
        { id: 3101, name: '石家庄长安店', cityId: 31 },
        // 呼和浩特
        { id: 3201, name: '呼和浩特新城店', cityId: 32 },
        // 乌鲁木齐
        { id: 3301, name: '乌鲁木齐新市店', cityId: 33 },
        // 银川
        { id: 3401, name: '银川金凤4S店', cityId: 34 }
      ],
      vehicleList: [
        { id: 1, label: '我的爱车 - 浙A12345', plate: '浙A12345' },
        { id: 2, label: '家用SUV - 浙B88888', plate: '浙B88888' }
      ],

      // UI 状态
      submitting: false,
      showCustomPicker: false,
      pickerType: '',               // 'city', 'station', 'date', 'time', 'vehicle'
      pickerData: [],

      // 新增零件弹窗
      showAddPartDialog: false,
      newPart: { name: '', price: '' },

      // 是否使用 uni-app 原生 picker（在 uni-app 环境中自动设置为 true）
      useUniPicker: typeof uni !== 'undefined'
    }
  },
  computed: {
    totalRepairPrice() {
      return this.repairParts.reduce((sum, p) => sum + (Number(p.price) || 0), 0).toFixed(2)
    },
    selectedVehicleText() {
      if (!this.form.vehicleId) return ''
      const v = this.vehicleList.find(item => item.id === this.form.vehicleId)
      return v ? v.label : ''
    },
    // 构建维修方案 JSON 字符串（符合接口要求）
    repairPlanJson() {
      const plan = {
        parts: this.repairParts.map(p => ({ name: p.name, price: Number(p.price) })),
        totalPrice: Number(this.totalRepairPrice),
        cycle: this.repairCycle,
        category: this.repairCategory
      }
      return JSON.stringify(plan)
    }
  },
  methods: {
    goBack() {
      // 返回上一页
      if (typeof uni !== 'undefined') {
        uni.navigateBack()
      } else {
        window.history.back()
      }
    },

    // ---------- 模拟选择器 (非 uni-app 环境) ----------
    openCityPicker() {
      this.pickerData = this.cityList.map(c => ({ ...c, label: c.name }))
      this.pickerType = 'city'
      this.showCustomPicker = true
    },
    openStationPicker() {
      if (!this.form.city) {
        this.showToast('请先选择城市')
        return
      }
      this.pickerData = this.stationList.filter(s => s.cityId === this.getCityIdByName(this.form.city))
      this.pickerType = 'station'
      this.showCustomPicker = true
    },
    openDatePicker() {
      // 简易日期选择：使用浏览器 prompt，实际项目请用 picker
      const date = prompt('请输入日期 (YYYY-MM-DD)', '2026-04-15')
      if (date) {
        this.form.appointmentDate = date
        this.form.appointmentDateDisplay = date
      }
    },
    openTimePicker() {
      const time = prompt('请输入时间 (HH:mm)', '10:00')
      if (time) {
        this.form.appointmentTime = time
        this.form.appointmentTimeDisplay = time
      }
    },
    openVehiclePicker() {
      this.pickerData = this.vehicleList.map(v => ({ ...v, label: v.label }))
      this.pickerType = 'vehicle'
      this.showCustomPicker = true
    },

    onPickerSelect(item) {
      switch (this.pickerType) {
        case 'city':
          this.form.city = item.name
          this.form.serviceStationId = null
          this.form.serviceStationName = ''
          break
        case 'station':
          this.form.serviceStationId = item.id
          this.form.serviceStationName = item.name
          break
        case 'vehicle':
          this.form.vehicleId = item.id
          this.form.licensePlate = item.plate
          break
      }
      this.showCustomPicker = false
    },

    getCityIdByName(name) {
      const city = this.cityList.find(c => c.name === name)
      return city ? city.id : null
    },

    // ---------- uni-app picker 事件 (自动适配) ----------
    onCityChange(e) {
      const idx = e.detail.value
      const city = this.cityList[idx]
      this.form.city = city.name
      this.form.serviceStationId = null
      this.form.serviceStationName = ''
    },
    onStationChange(e) {
      const idx = e.detail.value
      const filtered = this.stationList.filter(s => s.cityId === this.getCityIdByName(this.form.city))
      const station = filtered[idx]
      if (station) {
        this.form.serviceStationId = station.id
        this.form.serviceStationName = station.name
      }
    },
    onDateChange(e) {
      this.form.appointmentDate = e.detail.value
      this.form.appointmentDateDisplay = e.detail.value
    },
    onTimeChange(e) {
      this.form.appointmentTime = e.detail.value
      this.form.appointmentTimeDisplay = e.detail.value
    },
    onVehicleChange(e) {
      const idx = e.detail.value
      const vehicle = this.vehicleList[idx]
      this.form.vehicleId = vehicle.id
      this.form.licensePlate = vehicle.plate
    },

    // ---------- 零件管理 ----------
    addPart() {
      if (!this.newPart.name || !this.newPart.price) {
        this.showToast('请填写零件名称和单价')
        return
      }
      this.repairParts.push({
        name: this.newPart.name,
        price: Number(this.newPart.price)
      })
      this.newPart = { name: '', price: '' }
      this.showAddPartDialog = false
    },
    removePart(idx) {
      this.repairParts.splice(idx, 1)
    },

    // ---------- 表单验证 ----------
    validateForm() {
      const f = this.form
      if (!f.city) return '请选择城市'
      if (!f.serviceStationId) return '请选择维保服务站'
      if (!f.appointmentDate) return '请选择预约日期'
      if (!f.appointmentTime) return '请选择预约时间'
      if (!f.vehicleId) return '请选择维保车辆'
      if (!f.licensePlate) return '请输入车牌号码'
      if (!f.contactName) return '请输入联系人姓名'
      if (!f.contactPhone) return '请输入联系电话'

      if (f.maintenanceType === 2) {
        if (this.repairParts.length === 0) return '请至少添加一个更换零件'
        if (!this.repairCycle) return '请填写维修周期'
      }
      return null
    },

    showToast(msg) {
      if (typeof uni !== 'undefined') {
        uni.showToast({ title: msg, icon: 'none' })
      } else {
        alert(msg)
      }
    },

    // ---------- 提交预约 ----------
        async submitForm() {
          const error = this.validateForm()
          if (error) {
            this.showToast(error)
            return
          }

          this.submitting = true

          const payload = {
            maintenanceType: this.form.maintenanceType,
            city: this.form.city,
            serviceStationId: this.form.serviceStationId,
            serviceStationName: this.form.serviceStationName,
            appointmentDate: `${this.form.appointmentDate}T${this.form.appointmentTime}:00`,
            appointmentTime: `${this.form.appointmentDate}T${this.form.appointmentTime}:00`,
            vehicleId: this.form.vehicleId,
            licensePlate: this.form.licensePlate,
            contactName: this.form.contactName,
            contactPhone: this.form.contactPhone,
            userId: this.form.userId
          }

          if (this.form.maintenanceType === 2) {
            payload.repairPlan = this.repairPlanJson
          }

          uni.request({
            url: 'http://121.40.121.6:8080/adminsystem/maint/record/save',
            method: 'POST',
            data: payload,
            header: { 'Content-Type': 'application/json' },
            success: (res) => {
              if (res.data.code === 200) {
                this.showToast('预约提交成功')
                this.resetForm()

                if (this.isTabBarPage) {
                  uni.switchTab({ url: '/pages/index/index' })
                } else {
                  uni.navigateBack()
                }
              } else {
                this.showToast(res.data.message || '提交失败')
              }
            },
            fail: (err) => {
              this.showToast('网络错误，请重试')
              console.error(err)
            },
            complete: () => {
              this.submitting = false
            }
          })
        },


    resetForm() {
      this.form = {
        maintenanceType: 1,
        city: '',
        serviceStationId: null,
        serviceStationName: '',
        appointmentDate: '',
        appointmentTime: '',
        appointmentDateDisplay: '',
        appointmentTimeDisplay: '',
        vehicleId: null,
        licensePlate: '',
        contactName: '',
        contactPhone: '',
        userId: 1
      }
      this.repairCategory = ''
      this.repairParts = []
      this.repairCycle = ''
    }
  }
}
</script>

<style scoped>
.page {
  background: #f0f7ff;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.nav-bar {
  background: linear-gradient(145deg, #1a7bff 0%, #3f94ff 100%);
  padding: 14px 16px;
  display: flex;
  align-items: center;
  color: white;
  box-shadow: 0 4px 10px rgba(26, 123, 255, 0.2);
}
.back-icon {
  font-size: 24px;
  margin-right: 16px;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 20px;
  background: rgba(255,255,255,0.15);
}
.title {
  font-size: 18px;
  font-weight: 600;
  flex: 1;
}
.right-placeholder { width: 28px; }

.scroll-content {
  flex: 1;
  padding: 16px 16px 24px;
}

.form-card {
  background: #ffffff;
  border-radius: 20px;
  padding: 18px 16px;
  margin-bottom: 20px;
  box-shadow: 0 6px 16px rgba(0, 80, 180, 0.05);
  border: 1px solid rgba(64, 148, 255, 0.1);
}
.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #0a3a6d;
  margin-bottom: 16px;
  padding-left: 8px;
  border-left: 5px solid #1a7bff;
  display: flex;
  align-items: center;
}
.title-icon { margin-right: 6px; color: #1a7bff; }

.form-item {
  margin-bottom: 18px;
  display: flex;
  flex-direction: column;
}
.form-label {
  font-size: 14px;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 6px;
}
.required { color: #ff4d4f; margin-right: 4px; }

.input-field {
  background: #f9fcff;
  border: 1.5px solid #e6f0ff;
  border-radius: 14px;
  padding: 12px 16px;
  font-size: 15px;
  color: #1e2f45;
  width: 100%;
}
.input-field:focus { border-color: #1a7bff; background: #fff; }

.row-2 { display: flex; gap: 12px; }
.row-2 .form-item { flex: 1; }

.type-switch {
  display: flex;
  gap: 16px;
  background: #f4faff;
  padding: 6px;
  border-radius: 40px;
  border: 1px solid #d9e9ff;
}
.type-option {
  flex: 1;
  text-align: center;
  padding: 10px 0;
  border-radius: 30px;
  font-weight: 500;
  color: #3e5e7e;
}
.type-option.active {
  background: #1a7bff;
  color: white;
  box-shadow: 0 4px 8px rgba(26, 123, 255, 0.25);
}

.sub-label {
  font-size: 14px;
  font-weight: 500;
  color: #1f4970;
  margin: 16px 0 8px;
}
.parts-list {
  background: #f9fcff;
  border-radius: 16px;
  border: 1px solid #d0e2ff;
}
.part-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid #e3efff;
}
.part-item:last-child { border-bottom: none; }
.part-name { font-weight: 600; color: #0b2f4e; }
.part-price { font-size: 13px; color: #4a6f96; }
.part-delete {
  color: #ff6b6b;
  font-size: 20px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ffecec;
  border-radius: 30px;
}
.empty-parts { padding: 20px; text-align: center; color: #8aa1c0; }

.add-part-btn {
  background: transparent;
  border: 1.5px dashed #1a7bff;
  color: #1a7bff;
  padding: 12px;
  border-radius: 40px;
  font-weight: 600;
  margin: 12px 0 8px;
  text-align: center;
}
.total-row {
  display: flex;
  justify-content: space-between;
  padding: 12px 8px;
  font-weight: 600;
  color: #0a3a6d;
  background: #e6f0ff;
  border-radius: 16px;
  margin: 12px 0;
}
.total-price { font-size: 20px; color: #1a7bff; }

.submit-footer {
  padding: 16px 16px 12px;
  background: white;
  border-top: 1px solid #e0edff;
}
.submit-btn {
  background: linear-gradient(145deg, #1a7bff, #3f94ff);
  border: none;
  border-radius: 60px;
  padding: 16px;
  width: 100%;
  color: white;
  font-size: 18px;
  font-weight: 700;
  box-shadow: 0 10px 20px rgba(26, 123, 255, 0.25);
}
.submit-btn[disabled] { opacity: 0.6; }

/* 模拟弹窗样式 */
.picker-mask {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.3);
  z-index: 1000;
  display: flex;
  align-items: flex-end;
}
.picker-panel {
  background: white;
  width: 100%;
  max-width: 450px;
  margin: 0 auto;
  border-radius: 24px 24px 0 0;
  padding: 16px;
  animation: slideUp 0.2s;
}
@keyframes slideUp { from { transform: translateY(100%); } }
.picker-item {
  padding: 16px;
  text-align: center;
  border-bottom: 1px solid #ecf3ff;
}
.picker-cancel {
  margin-top: 10px;
  color: #1a7bff;
  font-weight: 600;
  padding: 14px;
  text-align: center;
}
.dialog-title { font-size: 18px; font-weight: 600; margin-bottom: 16px; color: #0a3a6d; }
.dialog-input {
  background: #f9fcff;
  border: 1.5px solid #e6f0ff;
  border-radius: 14px;
  padding: 12px;
  margin-bottom: 12px;
  width: 100%;
}
.dialog-actions { display: flex; gap: 12px; margin-top: 20px; }
.dialog-btn {
  flex: 1;
  padding: 12px;
  border-radius: 40px;
  border: none;
  font-weight: 600;
}
.dialog-btn.cancel { background: #f0f7ff; color: #3e5e7e; }
.dialog-btn.confirm { background: #1a7bff; color: white; }
</style>