<script setup>
defineProps({
  settingGroups: {
    type: Array,
    required: true
  }
})

defineEmits(['show-toast'])
</script>

<template>
  <div style="max-width: 640px; display: flex; flex-direction: column; gap: 14px">
    <div v-for="sg in settingGroups" :key="sg.title" class="table-wrap">
      <div style="padding: 12px 16px; border-bottom: 1px solid var(--border); font-size: 13px; font-weight: 600; color: #fff">
        {{ sg.title }}
      </div>
      <div 
        v-for="(s, i) in sg.items" 
        :key="s.label"
        :style="i < sg.items.length - 1 ? 'border-bottom: 1px solid var(--border)' : ''"
      >
        <div style="display: flex; align-items: center; justify-content: space-between; padding: 12px 16px">
          <div>
            <div style="font-size: 13px; color: var(--text)">{{ s.label }}</div>
            <div style="font-size: 11px; color: var(--subtle); margin-top: 2px">{{ s.desc }}</div>
          </div>
          
          <button 
            v-if="s.type === 'toggle'" 
            @click="s.value = !s.value"
            :style="{ 
              width: '40px', 
              height: '22px', 
              borderRadius: '11px', 
              border: 'none', 
              cursor: 'pointer', 
              position: 'relative', 
              transition: 'background .2s', 
              background: s.value ? 'var(--brand)' : 'var(--border)' 
            }"
            style="flex-shrink: 0"
          >
            <span :style="{ 
              position: 'absolute', 
              top: '3px', 
              width: '16px', 
              height: '16px', 
              background: '#fff', 
              borderRadius: '50%', 
              transition: 'left .2s', 
              left: s.value ? '21px' : '3px' 
            }"></span>
          </button>
          
          <select v-else-if="s.type === 'select'" class="form-control" style="width: auto; min-width: 140px">
            <option v-for="o in s.options" :key="o">{{ o }}</option>
          </select>
        </div>
      </div>
    </div>
    
    <div style="display: flex; justify-content: flex-end">
      <button class="btn btn-primary" @click="$emit('show-toast', 'Đã lưu cài đặt!', 'success')">Lưu thay đổi</button>
    </div>
  </div>
</template>
