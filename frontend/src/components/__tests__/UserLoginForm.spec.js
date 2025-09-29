import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import UserLoginForm from '../../views/UserLoginForm.vue'
import { useStore } from 'vuex'
import router from '../../router/index.js'
import { makeRequestWithoutToken } from '../../utils/requests.js'
import Toast, { POSITION } from 'vue-toastification'
import 'vue-toastification/dist/index.css'

vi.mock('../../router/index.js', () => ({
  default: {
    push: vi.fn(),
    beforeEach: vi.fn(),
  },
}))

vi.mock('vuex', async () => {
  const actual = await vi.importActual('vuex')
  return {
    ...actual,
    useStore: vi.fn(),
  }
})

vi.mock('../../utils/requests.js', () => ({
  makeRequestWithoutToken: vi.fn(),
}))

describe('UserLoginForm.vue', () => {
  let storeMock

  beforeEach(() => {
    storeMock = {
      dispatch: vi.fn(),
      getters: {
        currentUser: null,
      },
    }
    useStore.mockReturnValue(storeMock)
    vi.clearAllMocks()
    localStorage.clear()

    document.body.innerHTML = ''
  })

  const mountWithToast = () =>
    mount(UserLoginForm, {
      global: {
        plugins: [[Toast, { position: POSITION.TOP_RIGHT }]],
      },
    })

  it('renders all input fields and button', () => {
    const wrapper = mountWithToast()
    expect(wrapper.find('input#email').exists()).toBe(true)
    expect(wrapper.find('input#password').exists()).toBe(true)
    expect(wrapper.find('button[type="submit"]').exists()).toBe(true)
  })

  it('shows validation error toast for invalid email', async () => {
    const wrapper = mountWithToast()

    await wrapper.find('input#email').setValue('invalid-email')
    await wrapper.find('input#password').setValue('validPass')
    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    // Toasts are rendered to document.body
    expect(document.body.textContent).toContain('Invalid email')
  })

  it('shows validation error toast for invalid password', async () => {
    const wrapper = mountWithToast()

    await wrapper.find('input#email').setValue('valid.email@gmail.com')
    await wrapper.find('input#password').setValue('1')
    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(document.body.textContent).toContain('Password should be at least 2 characters')
  })

  it('toggles password visibility', async () => {
    const wrapper = mountWithToast()
    const input = wrapper.find('input#password')
    const toggleBtn = wrapper.find('button')

    // Initially password type
    expect(input.attributes('type')).toBe('password')

    await toggleBtn.trigger('click')
    expect(input.attributes('type')).toBe('text')

    await toggleBtn.trigger('click')
    expect(input.attributes('type')).toBe('password')
  })

  it('submits form correctly and navigates', async () => {
    const wrapper = mountWithToast()

    await wrapper.find('input#email').setValue('user@example.com')
    await wrapper.find('input#password').setValue('password123')

    // Mock API response
    makeRequestWithoutToken.mockResolvedValue({
      data: {
        accessToken: 'fakeAccessToken',
        refreshToken: 'fakeRefreshToken',
        id: 1,
        name: 'User Tester',
        email: 'user@example.com',
        role: 'USER',
        creditScore: 750,
        income: 50000,
        aadhar: '123456789012',
      },
    })

    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(localStorage.getItem('token')).toBe('fakeAccessToken')
    expect(localStorage.getItem('refreshToken')).toBe('fakeRefreshToken')
    expect(JSON.parse(localStorage.getItem('currUser'))).toMatchObject({
      name: 'User Tester',
      email: 'user@example.com',
      role: 'USER',
    })

    expect(storeMock.dispatch).toHaveBeenCalledWith(
      'setCurrentUser',
      expect.objectContaining({
        name: 'User Tester',
        email: 'user@example.com',
      })
    )
    expect(router.push).toHaveBeenCalledWith('/user-dashboard')
  })
})
