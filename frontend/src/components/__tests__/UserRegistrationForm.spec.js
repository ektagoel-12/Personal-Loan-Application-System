import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import CreateAccount from '../../views/UserRegistrationForm.vue'
import { useStore } from 'vuex'
import router from '../../router/index.js'
import { makeRequestWithoutToken } from '../../utils/requests.js'

//Mock router
vi.mock('../../router/index.js', () => ({
  default: {
    push: vi.fn(),
    beforeEach: vi.fn(), // to avoid navigation guard error
  },
}))

// Mock vueX store
vi.mock('vuex', async () => {
  const actual = await vi.importActual('vuex')
  return {
    ...actual,
    useStore: vi.fn(),
  }
})

// Mock API requests
vi.mock('../../utils/requests.js', () => ({
  makeRequestWithoutToken: vi.fn(),
}))

//Tests
describe('UserRegistrationForm.vue', () => {
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

  it('renders all input fields correctly', () => {
    const wrapper = mount(CreateAccount)
    expect(wrapper.find('input#name').exists()).toBe(true)
    expect(wrapper.find('input#email').exists()).toBe(true)
    expect(wrapper.find('input#password').exists()).toBe(true)
    expect(wrapper.find('input#confirmPassword').exists()).toBe(true)
    expect(wrapper.find('input#income').exists()).toBe(true)
    expect(wrapper.find('input#creditScore').exists()).toBe(true)
    expect(wrapper.find('input#aadhar').exists()).toBe(true)
  })

  it('shows validation errors for empty or invalid inputs', async () => {
    const wrapper = mount(CreateAccount)
    await wrapper.find('form').trigger('submit.prevent')
    expect(wrapper.text()).toContain('Name should be at least 2 letters')
  })

  it('shows password mismatch error', async () => {
    const wrapper = mount(CreateAccount)
    await wrapper.find('input#name').setValue('Aom Tester')
    await wrapper.find('input#email').setValue('aom@test.com')
    await wrapper.find('input#password').setValue('Password1!')
    await wrapper.find('input#confirmPassword').setValue('Password2!')
    await wrapper.find('form').trigger('submit.prevent')
    expect(wrapper.text()).toContain('Passwords do not match')
  })

  it('shows invalid email error', async () => {
    const wrapper = mount(CreateAccount)
    await wrapper.find('input#name').setValue('Aom Tester')
    await wrapper.find('input#email').setValue('aom@test')
    await wrapper.find('input#password').setValue('Password1!')
    await wrapper.find('input#confirmPassword').setValue('Password2!')
    await wrapper.find('form').trigger('submit.prevent')
    expect(wrapper.text()).toContain('Not a valid email')
  })

  it('shows invalid password error', async () => {
    const wrapper = mount(CreateAccount)
    await wrapper.find('input#name').setValue('Aom Tester')
    await wrapper.find('input#email').setValue('aom@test.com')
    await wrapper.find('input#password').setValue('aom')
    await wrapper.find('input#confirmPassword').setValue('aom')
    await wrapper.find('form').trigger('submit.prevent')
    expect(wrapper.text()).toContain('Password must have 8+ chars, 1 upper, 1 lower, 1 number, 1 special')
  })

  it('shows invalid aadhar error', async () => {
    const wrapper = mount(CreateAccount)
    await wrapper.find('input#name').setValue('Aom Tester')
    await wrapper.find('input#email').setValue('aom@test.com')
    await wrapper.find('input#password').setValue('ValidPassword@123')
    await wrapper.find('input#confirmPassword').setValue('ValidPassword@123')
    await wrapper.find('input#income').setValue(40000)
    await wrapper.find('input#creditScore').setValue(550)
    await wrapper.find('form').trigger('submit.prevent')
    expect(wrapper.text()).toContain('Please enter a valid aadhar')
  })

  it('submits the form correctly and navigates', async () => {
    const wrapper = mount(CreateAccount)
    await wrapper.find('input#name').setValue('Aom Tester')
    await wrapper.find('input#email').setValue('aom@test.com')
    await wrapper.find('input#password').setValue('Password1!')
    await wrapper.find('input#confirmPassword').setValue('Password1!')
    await wrapper.find('input#income').setValue(50000)
    await wrapper.find('input#creditScore').setValue(750)
    await wrapper.find('input#aadhar').setValue('123456789012')

    makeRequestWithoutToken.mockResolvedValue({
      data: {
        accessToken: 'fakeAccessToken',
        refreshToken: 'fakeRefreshToken',
        name: 'Aom Tester',
        email: 'aom@test.com',
        role: 'USER',
        creditScore: 750,
        income: 50000,
        aadhar: '123456789012',
        id: 1,
      },
    })

    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(localStorage.getItem('token')).toBe('fakeAccessToken')
    expect(localStorage.getItem('refreshToken')).toBe('fakeRefreshToken')
    expect(JSON.parse(localStorage.getItem('currUser'))).toMatchObject({
      name: 'Aom Tester',
      email: 'aom@test.com',
      role: 'USER',
    })
    expect(storeMock.dispatch).toHaveBeenCalledWith(
      'setCurrentUser',
      expect.objectContaining({
        name: 'Aom Tester',
        email: 'aom@test.com',
      })
    )

    expect(router.push).toHaveBeenCalledWith('/user-dashboard')
  })
})
