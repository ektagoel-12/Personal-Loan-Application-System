import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import UserLoginForm from '../../views/UserLoginForm.vue'
import { useStore } from 'vuex'
import router from '../../router/index.js'
import { makeRequestWithoutToken } from '../../utils/requests.js'

// ----------------------
// Mock router
// ----------------------
vi.mock('../../router/index.js', () => ({
  default: {
    push: vi.fn(),
    beforeEach: vi.fn(),
  },
}))

// ----------------------
// Mock vuex store
// ----------------------
vi.mock('vuex', async () => {
  const actual = await vi.importActual('vuex')
  return {
    ...actual,
    useStore: vi.fn(),
  }
})

// ----------------------
// Mock API request
// ----------------------
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
  })

  it('renders all input fields and button', () => {
    const wrapper = mount(UserLoginForm)
    expect(wrapper.find('input#email').exists()).toBe(true)
    expect(wrapper.find('input#password').exists()).toBe(true)
    expect(wrapper.find('button[type="submit"]').exists()).toBe(true)
  })

  it('shows validation errors for invalid email', async () => {
    const wrapper = mount(UserLoginForm)

    await wrapper.find('input#email').setValue('invalid-email')
    await wrapper.find('input#password').setValue('validPass')
    await wrapper.find('form').trigger('submit.prevent')

    expect(wrapper.text()).toContain('Invalid email')
  })

  it('shows validation errors for invalid password', async () => {
    const wrapper = mount(UserLoginForm)

    await wrapper.find('input#email').setValue('valid.email@gmail.com')
    await wrapper.find('input#password').setValue('1')
    await wrapper.find('form').trigger('submit.prevent')

    expect(wrapper.text()).toContain('Password should be at least 2 characters')
  })

  it('toggles password visibility', async () => {
    const wrapper = mount(UserLoginForm)
    const input = wrapper.find('input#password')
    const toggleBtn = wrapper.find('button')

    // initially password type
    expect(input.attributes('type')).toBe('password')

    await toggleBtn.trigger('click')
    expect(input.attributes('type')).toBe('text')

    await toggleBtn.trigger('click')
    expect(input.attributes('type')).toBe('password')
  })

  it('submits form correctly and navigates', async () => {
    const wrapper = mount(UserLoginForm)

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

    // ----------------------
    // Check localStorage
    // ----------------------
    expect(localStorage.getItem('token')).toBe('fakeAccessToken')
    expect(localStorage.getItem('refreshToken')).toBe('fakeRefreshToken')
    expect(JSON.parse(localStorage.getItem('currUser'))).toMatchObject({
      name: 'User Tester',
      email: 'user@example.com',
      role: 'USER',
    })

    // ----------------------
    // Check Vuex dispatch
    // ----------------------
    expect(storeMock.dispatch).toHaveBeenCalledWith(
      'setCurrentUser',
      expect.objectContaining({
        name: 'User Tester',
        email: 'user@example.com',
      })
    )

    // ----------------------
    // Check router navigation
    // ----------------------
    expect(router.push).toHaveBeenCalledWith('/user-dashboard')
  })
})
