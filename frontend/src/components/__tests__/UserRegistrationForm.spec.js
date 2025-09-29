import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import UserRegistrationForm from '../../views/UserRegistrationForm.vue'
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

    // Clear toasts
    document.body.innerHTML = ''
  })

  const mountWithToast = () =>
    mount(UserRegistrationForm, {
      global: {
        plugins: [[Toast, { position: POSITION.TOP_RIGHT }]],
      },
    })

  it('renders all input fields correctly', () => {
    const wrapper = mountWithToast()
    expect(wrapper.find('input#name').exists()).toBe(true)
    expect(wrapper.find('input#email').exists()).toBe(true)
    expect(wrapper.find('input#password').exists()).toBe(true)
    expect(wrapper.find('input#confirmPassword').exists()).toBe(true)
    expect(wrapper.find('input#income').exists()).toBe(true)
    expect(wrapper.find('input#creditScore').exists()).toBe(true)
    expect(wrapper.find('input#aadhar').exists()).toBe(true)
  })

  it('shows validation error for invalid name', async () => {
    const wrapper = mountWithToast()
    await wrapper.find('input#name').setValue('A')
    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()
    expect(document.body.textContent).toContain('Name should be at least 2 characters long')
  })

  it('shows invalid email error', async () => {
    const wrapper = mountWithToast()
    await wrapper.find('input#name').setValue('Aom Tester')
    await wrapper.find('input#email').setValue('invalid-email')
    await wrapper.find('input#password').setValue('ValidPass1!')
    await wrapper.find('input#confirmPassword').setValue('ValidPass1!')
    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()
    expect(document.body.textContent).toContain('Please enter a valid email address')
  })

  it('shows password mismatch error', async () => {
    const wrapper = mountWithToast()
    await wrapper.find('input#name').setValue('Aom Tester')
    await wrapper.find('input#email').setValue('aom@test.com')
    await wrapper.find('input#password').setValue('Password1!')
    await wrapper.find('input#confirmPassword').setValue('Password2!')
    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()
    expect(document.body.textContent).toContain('Passwords do not match')
  })

  it('shows invalid password error', async () => {
    const wrapper = mountWithToast()
    await wrapper.find('input#name').setValue('Aom Tester')
    await wrapper.find('input#email').setValue('aom@test.com')
    await wrapper.find('input#password').setValue('short')
    await wrapper.find('input#confirmPassword').setValue('short')
    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()
    expect(document.body.textContent).toContain(
      'Password must have 8+ characters, 1 uppercase, 1 lowercase, 1 number, and 1 special character'
    )
  })

  it('shows invalid aadhar error', async () => {
    const wrapper = mountWithToast()
    await wrapper.find('input#name').setValue('Aom Tester')
    await wrapper.find('input#email').setValue('aom@test.com')
    await wrapper.find('input#password').setValue('ValidPass1!')
    await wrapper.find('input#confirmPassword').setValue('ValidPass1!')
    await wrapper.find('input#income').setValue(40000)
    await wrapper.find('input#creditScore').setValue(600)
    await wrapper.find('input#aadhar').setValue('1234')
    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()
    expect(document.body.textContent).toContain('Please enter a valid 12-digit Aadhar number')
  })

  it('submits the form correctly and navigates', async () => {
    const wrapper = mountWithToast()
    await wrapper.find('input#name').setValue('Aom Tester')
    await wrapper.find('input#email').setValue('aom@test.com')
    await wrapper.find('input#password').setValue('ValidPass1!')
    await wrapper.find('input#confirmPassword').setValue('ValidPass1!')
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
