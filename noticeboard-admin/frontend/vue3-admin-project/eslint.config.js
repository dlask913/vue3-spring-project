import js from '@eslint/js' // 기본 JavaScript용 ESLint 설정
import pluginVue from 'eslint-plugin-vue' // Vue.js 전용 ESLint 플러그인
import skipFormatting from '@vue/eslint-config-prettier/skip-formatting' // Prettier와 충돌 방지 설정

export default [
  {
    name: 'app/files-to-lint', // ESLint 를 적용할 파일 지정
    files: ['**/*.{js,mjs,jsx,vue}'],
    rules: {
      // 커스텀 규칙 설정
      'no-console': 'warn', // console.log 시 경고
      'vue/no-undef-components': 'error', // 정의되지 않은 Vue 컴포넌트 사용 금지
    },
  },

  {
    name: 'app/files-to-ignore', // ESLint 를 무시할 파일 지정
    ignores: ['**/dist/**', '**/dist-ssr/**', '**/coverage/**'],
  },

  js.configs.recommended,
  ...pluginVue.configs['flat/essential'],
  skipFormatting,
]
